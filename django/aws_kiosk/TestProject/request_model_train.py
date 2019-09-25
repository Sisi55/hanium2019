from firebase_admin import firestore
from imblearn.over_sampling import SMOTE
from xgboost import XGBClassifier
import pickle
import pandas as pd
from django.utils import timezone



def check_time_and_decide_to_train_model():

    # 저장한 이전 시간을 가져오고
    pre_time_dict = firestore.client().collection(u'time').document(u'pre_request_time').get().to_dict()
    pre_time = pre_time_dict['time'] # 이전 갱신 시간 str
    pre_update_minute = int(pre_time.split(':')[1]) % 10 # 나머지

    # 현재 시간을 저장한다
    now = str(timezone.localtime())
    date = str(now).split(' ')[1] # 0은 yymmdd, 1은 hhmmss
    date = date.split('.')[0] #18:40:16 형태
    now_minute = int(date.split(':')[1]) % 10

    # pre_update_minute 과 now_minute 이
    # 같지 않으면 모델 재학습 요청하기
    if pre_update_minute != now_minute:
        # 재학습 요청
        request_model_train() # 모델 재학습한다
        # 학습한 시간 저장
        save_retraining_time(date)


def save_retraining_time(date):
    transaction = firestore.client().transaction()
    time_ref = firestore.client().collection(u'time').document(u'pre_request_time')

    # 기본키 역할을 하는 변수의 트랜잭션 처리
    @firestore.transactional # 단순 1 증가하는 함수
    def update_in_transaction(transaction, time_ref, date):
        snapshot = time_ref.get(transaction=transaction)
        transaction.update(time_ref, {
            u'time': date
        })

    update_in_transaction(transaction, time_ref, date)

# DataFrame 전처리
# 날 수 세는 함수
def date_to_VIP(date):
    year,month,day = date.split('-')
    year = int(year)
    month = int(month)
    day = int(day)
    preYear = year-1
    num_of_days = preYear*365 + (preYear/4 - preYear/100 + preYear/400)
    
    monthArray = [31,28,31,30,31,30,31,31,30,31,30,31]
    for m in range(month):
        num_of_days += monthArray[m]
    
    cond = (month>=3 and (year%4==0 and year%100!=0 or year%400==0))
    if cond == True:
        num_of_days += 1
    
    num_of_days += day
    
    #return int(num_of_days)
    #print(num_of_days,num_of_days % 7 - 3)
    
    return int(num_of_days)


# 날 수를 평일/휴일로 변환하는 함수
def num_of_days_to_work_or_free(num_of_days):
    
    dayOfWeek = num_of_days % 7 - 3
    if int(dayOfWeek) ==0 or int(dayOfWeek)==6:
        return 0 # 주말
    else:
        return 1 # 평일


# 아침/점심/저녁 나누는 함수
def time_to_3(time):
    result = -1
    time = int(time.split(':')[0])
    if time < 11:
        result = 0 #아침은 0
    elif time < 16: #오후는 1
        result = 1
    else:
        result = 2 #저녁은 2
    
    return result


def get_emotion_vec():

    # 8가지 감정 키워드
    emotion_keyword = ["anger","contempt","disgust","fear","happiness","neutral","sadness","surprise"]

    emotion_df = pd.DataFrame({ 'emotion_keyword':emotion_keyword })
    emotion_df['emotion_encoding'] = pd.factorize(emotion_df['emotion_keyword'])[0]  # pd.factorize(df['Col'])[0]

    #print(emotion_df.values)
    emotion_keyword_to_vec = {} #딕셔너리로 저장하면 편하겠지?
    for keyword,encoding in emotion_df.values:
        #print(keyword,encoding)
        emotion_keyword_to_vec[keyword] = encoding

    del emotion_df  #불필요한 메모리 제거

    return emotion_keyword_to_vec


def request_model_train():

    # emotion_keyword_to_vec를 만든다
    emotion_keyword_to_vec = get_emotion_vec()


#
# order에 있는 데이터 가져오자
# doc_df를 만들었다

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

        # 날짜
        #print(type(doc.get('today')))
        #print(doc.get('today').split(' '))
        list_date = doc.get('today').split(' ') # 2019-09-09 21:05:58
        doc_list_row.append(list_date[0]) # date 2019-09-09
        doc_list_row.append(list_date[1]) # time 21:05:58

        doc_list.append(doc_list_row)

    doc_df = pd.DataFrame(doc_list, columns=['item_name','w_humidity','w_temp','w_speed','emotion','date','time'])
    doc_df['emotion'] = doc_df['emotion'].astype(int) # 감정 데이터 전처리 str to int
    #doc_df

#
    # 메뉴 이름
    doc_df['item_name'], item_name_restore = pd.factorize(doc_df['item_name']) # encoding 메뉴이름
    #doc_df

    # date > 요일 > 주중/주말
    doc_df['num_of_days'] = doc_df['date'].apply(lambda x: date_to_VIP(x))
    doc_df['work_or_free'] = doc_df['num_of_days'].apply(lambda x: num_of_days_to_work_or_free(x))


    # time > 아침(오픈-임시:9:00~11)/오후(~16)/저녁(~마감-임시:20:00) -- 시간 추후 수정
    doc_df['time_to_3'] = doc_df['time'].apply(lambda x: time_to_3(x))
    #doc_df

#
    # 그리고 필요없는 date, time은 drop한다
    doc_df.drop(['date','time'],axis=1,inplace=True)

    # 날 수로 오름차 정렬한다
    doc_df.sort_values(by=['num_of_days'], inplace=True)

    # 날 수 제거한다
    doc_df.drop('num_of_days',axis=1, inplace=True)

    # 특징, 레이블 index 리스트 형태로 저장
    features = doc_df.columns.tolist()
    # python list에서 remove(값)으로 요소 삭제할 수 있다
    features.remove('item_name') # 리스트에 반영된다
    label = ['item_name']
    # features

    test_data_num = int(doc_df.shape[0] * 0.2) # 검증 데이터 개수

    # 학습 데이터, 검증 데이터 분리
    eval_df = doc_df[-test_data_num:]
    train_df = doc_df[:-test_data_num]
    print(train_df['item_name'].value_counts())

    
    # 학습 데이터 오버 샘플링
    print('오버샘플링 전', train_df[features].shape) # 오버샘플링 전
    smote = SMOTE()

    smote_x, smote_y = smote.fit_sample(train_df[features], train_df[label])

    print('오버샘플링 후', smote_x.shape) # 오버샘플링 후
    print(pd.Series(smote_y).value_counts()) # 가장 많은 개수에 맞춰서 증폭된다


    xgb_wrapper = XGBClassifier(n_estimator=400, learning_rate=0.1, max_depth=3)
    evals = [(eval_df[features], eval_df[label])]
    xgb_wrapper.fit(train_df[features], train_df[label], early_stopping_rounds=100, eval_metric='mlogloss', eval_set=evals, verbose=True)
    # pred = xgb_wrapper.predict(_df[features])
    # print(item_name_restore[pred]) # encoding to 원래 아이템 이름


    # 학습한 모델을 저장한다
    pickle.dump(xgb_wrapper, open('xgb_baseline.pkl', 'wb')) # 모델
    pickle.dump(item_name_restore, open('item_name.pkl', 'wb')) # 아이템 이름
    # keys = ('model','item_name_restore')
    # values = (xgb_wrapper, item_name_restore)
    # save_dict = dict(zip(keys, values))
    # json.dumps(save_dict, ensure_ascii=False)

    # with open('xgb_baseline.pkl', 'wb') as f:
    #     for e in save_dict:
    #         pickle.dump(e, f)

    # pickle.dump(, )

