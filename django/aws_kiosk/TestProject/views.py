from django.shortcuts import render
from django.http import HttpResponse
from django.utils import timezone

#from .models import Name
from .order_decisiontree_recom import order_decisionTree_recom
from .item_collaborative_filtering import item_collaborativeFiltering
from .request_model_train import check_time_and_decide_to_train_model

from .request_model_train import get_emotion_vec,date_to_VIP,num_of_days_to_work_or_free,time_to_3
import pickle
import pandas as pd
import json
from firebase_admin import firestore
from sklearn.metrics.pairwise import cosine_similarity

# xgb 알고리즘 사용하는 뷰 함수에 
# check_time_and_decide_to_train_model()

# 날씨, 감정, 휴일/평일, 아침/점심/저녁
def xgb_recom(request, today,humidity,temp,speed,emo1,emo2): # 특징에 해당하는 데이터 전부 가져와야 하네..?
    # 날씨, 감정만 받아오고
    # /home_recom/80.0/300.0/1.00/happiness/neutral 처럼

    # 감정 인코딩하고
    # 8가지 감정 키워드
    emotion_keyword_to_vec = get_emotion_vec()
    encode_emo1 = str(emotion_keyword_to_vec.get(emo1))
    encode_emo2 = str(emotion_keyword_to_vec.get(emo2))
    encode_emotion = encode_emo1+encode_emo1

    # 날 수 계산해서 휴일/평일, 아침/점심/저녁 추출하면 되겠다
    # today도 받아와야 한다
    today = str(today).split(' ')
    date = today[0]
    time = today[1]

    num_of_days = date_to_VIP(date)
    work_or_free = num_of_days_to_work_or_free(num_of_days) # 날수 > 평일/휴일
    time_3 = time_to_3(time) # 시간 > 아침/점심/저녁

    input_features = [float(humidity), float(temp), float(speed), int(encode_emotion), work_or_free, time_3]
    feature_names = ['w_humidity','w_temp','w_speed','emotion','work_or_free','time_to_3']
    input_df = pd.DataFrame([input_features], columns=feature_names)

    # 그리고 모델 pkl 읽어와서
    with open('xgb_baseline.pkl', 'rb') as f:
        model = pickle.load(f)
    with open('item_name.pkl', 'rb') as f:
        item_name_restore = pickle.load(f)    
    # reload_data = {'model':'', 'item_name_restore':''}
    # with open('xgb_baseline.pkl', 'rb') as f:
    #     for x in reload_data.keys(): #2번 ?
    #         data = pickle.load(f)
    #         reload_data[x] = data 

    # 예측값 반환하면 되지
    pred = model.predict(input_df)
    result_item_name = item_name_restore[pred].tolist()[0]
    print(type(result_item_name))

    result_json = json.dumps(dict({'item':str(result_item_name)}), ensure_ascii=False)

    check_time_and_decide_to_train_model()

    return HttpResponse(result_json) # 객체



def weather_emotion_recom(request):

    sim_df = weather_emo_similiar(get_emotion_vec())
    
    # 습도
    w_humidity_df = sim_df[['item_name','guest','w_humidity']]
    # 날씨 - 온도
    w_temp_df = sim_df[['item_name','guest','w_temp']]
    # 날씨 - 바람
    w_speed_df = sim_df[['item_name','guest','w_speed']]
    
    humidity_matrix = w_humidity_df.pivot_table('w_humidity', index='item_name', columns='guest')
    humidity_matrix = humidity_matrix.fillna(0)
    temp_matrix = w_temp_df.pivot_table('w_temp', index='item_name', columns='guest')
    temp_matrix = temp_matrix.fillna(0)
    speed_matrix = w_speed_df.pivot_table('w_speed', index='item_name', columns='guest')
    speed_matrix = speed_matrix.fillna(0)

    # 날씨 3요소 기반 유사도 행렬
    humidity_sim = cosine_similarity(humidity_matrix, humidity_matrix)
    temp_sim = cosine_similarity(temp_matrix, temp_matrix)
    speed_sim = cosine_similarity(speed_matrix, speed_matrix)

    weather_sim = humidity_sim + temp_sim + speed_sim # ndarray
    weather_sim_df = pd.DataFrame(data=weather_sim, index=speed_matrix.index , columns=speed_matrix.index ) / 3

    # 감정
    emotion_df = sim_df[['item_name','guest','emotion']]

    emotion_matrix = emotion_df.pivot_table('emotion', index='item_name', columns='guest')
    emotion_matrix = emotion_matrix.fillna(0)

    # 감정 기반 유사도 행렬
    emotion_sim = cosine_similarity(emotion_matrix, emotion_matrix)
    emotion_sim_df = pd.DataFrame(data=emotion_sim, index=emotion_matrix.index , columns=emotion_matrix.index )

    # 날씨:감정 = 2:1
    weather_emotion_sim_df = weather_sim_df *2 + emotion_sim_df

    item_name_list = weather_emotion_sim_df.columns.tolist()
    return_dict = {}
    for name in item_name_list:
        input_index = name
        temp_df = weather_emotion_sim_df[ (weather_emotion_sim_df[input_index].index!=input_index)]

        return_dict[name] = temp_df[input_index].sort_values().index.tolist()[0] # 값 중 최댓값 하나

    result_json = json.dumps(return_dict, ensure_ascii=False)

    return HttpResponse(result_json)


# 선호도
def itemCF(request, user_uuid):#사용자1, 아이템1
    
    result = item_collaborativeFiltering(user_uuid) #str:dict
    # dict으로 두 가지 종류 추천을 반환할거야
    # 아이템:안먹은 유사아이템
    # 사용자 > 안먹은 아이템  각각 하나씩
    #result = "{'user_cf':"+item_cf_result+",'items_sim':"+items_sim_dict+"}"

    return HttpResponse(result) ## 마지막 str

###

        #     transaction = firestore.client().transaction()
        #     info_ref = firestore.client().collection(u'info').document(u'posting_info')

        #     # 기본키 역할을 하는 변수의 트랜잭션 처리
        #     @firestore.transactional # 단순 1 증가하는 함수
        #     def update_in_transaction(transaction, info_ref):
        #         snapshot = info_ref.get(transaction=transaction)
        #         transaction.update(info_ref, {
        #             u'number': snapshot.get(u'number') + 1
        #         })
        # #doc = doc_ref.get()
        
        # # 데이터 저장
        #         post = Post(title=title, contents=contents, time=now, n=post_number)
        #         firestore.client().collection(u'posts').add(post.to_dict())


        #     update_in_transaction(transaction, info_ref)

##

# 결정트리 알고리즘
def lambda_handler(request,humidity,temp,speed,emo1,emo2):#사용자1
    
    result_predict = order_decisionTree_recom(humidity,temp,speed,emo1,emo2)

    return HttpResponse(result_predict)


# def hello(request, name):
#     return render(request, "hello.html", {"name": name})

    # return HttpResponse("<b>Hello " + name +"</b>")

# def saveToDb(request, name):
#     Name.objects.create(name = name)

#     count = Name.objects.count()
#     return HttpResponse("<h2>You have inserted a name successfully</h2> <br> count = " + str(count))


def index(request):
    return HttpResponse("<h2>kiosk django</h2>")


# def testsisi(request):
#     return HttpResponse('<h1>장고 배포 성공</h1>')

def weather_emo_similiar(emotion_keyword_to_vec): # views.py
    
    # order_df 만들기    
    docs = firestore.client().collection(u'order').stream()

    doc_list = []
    for doc in docs:
        doc_list_row = []

        # 메뉴 - 레이블
        #doc_list_row.append(doc.get('orderToString'))
        # 데이터가 거의 없는 문제로 임시로 name으로 모델 학습한다
        doc_list_row.append(doc.get('items')[0].get('name'))

        # 날씨
    #    weather_key_list = []
        for key in doc.get('weather').keys():
            doc_list_row.append(doc.get('weather').get(key))

        # 감정
        emotion_key_list = ''
        for key in doc.get('emotion').keys():
            emotion_key_list += str(emotion_keyword_to_vec[key])  # 감정 데이터 전처리 str to str 
        doc_list_row.append(emotion_key_list)
        
        # 사용자 uuid
        doc_list_row.append(doc.get('guest'))

        # 날짜
        #print(type(doc.get('today')))
        #print(doc.get('today').split(' '))
#        list_date = doc.get('today').split(' ') # 2019-09-09 21:05:58
#        doc_list_row.append(list_date[0]) # date 2019-09-09
#        doc_list_row.append(list_date[1]) # time 21:05:58

        doc_list.append(doc_list_row)

    doc_df = pd.DataFrame(doc_list, columns=['item_name','w_humidity','w_temp','w_speed','emotion','guest'])#,'date','time'])
    doc_df['emotion'] = doc_df['emotion'].astype(int) # 감정 데이터 전처리 str to int

    # encoding 사용자
    doc_df['guest'], guest_restore = pd.factorize(doc_df['guest']) 
    
    return doc_df
    