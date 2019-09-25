import json

# import firebase_admin
# from firebase_admin import credentials
from firebase_admin import firestore
import pandas as pd
from sklearn.tree import DecisionTreeClassifier
from sklearn.model_selection import train_test_split
from sklearn.metrics import accuracy_score
from sklearn.preprocessing import LabelEncoder

from .request_model_train import get_emotion_vec

#초기화
#cred = credentials.Certificate('kiosk-firestore-jnsy-bcfe4-firebase-adminsdk-269q4-4a795774bb.json')
#firebase_admin.initialize_app(cred)
#encoder = LabelEncoder() #레이블 벡터 -> 변환 객체

# 8가지 감정 키워드
emotion_keyword_to_vec = get_emotion_vec()
# emotion_keyword = ["anger","contempt","disgust","fear","happiness","neutral","sadness","surprise"]

# emotion_df = pd.DataFrame({ 'emotion_keyword':emotion_keyword })
# emotion_vec = pd.get_dummies(emotion_df).values
# #print(emotion_vec)

# emotion_keyword_to_vec = {}
# for index,keyword in enumerate(emotion_keyword):
#     emotion_keyword_to_vec[keyword] = ''.join(list(map(str,emotion_vec[index].tolist())))


def get_dataFrame():
    docs = firestore.client().collection(u'order').stream()

    doc_list = []
    for doc in docs:
    #print(doc.get('emotion'))
    #print(type(doc))
        doc_list_row = []
    
    # 메뉴 - 레이블
        doc_list_row.append(doc.get('orderToString'))

    # 날씨
        weather_key_list = []
        for key in doc.get('weather').keys():
        #weather_key_list.append(doc.get('weather').get(key))
            doc_list_row.append(doc.get('weather').get(key))
    
    # 감정
        emotion_key_list = ''
        for key in doc.get('emotion').keys():
        #print(key)
            emotion_key_list += emotion_keyword_to_vec.get(key)
        doc_list_row.append(emotion_key_list)
    
        doc_list.append(doc_list_row)

    doc_df = pd.DataFrame(doc_list, columns=['item_name','w_humidity','w_temp','w_speed','emotion'])
    #레이블 인코딩
    encoder = LabelEncoder()
    items = doc_df['item_name'].values.tolist()
    encoder.fit(items)
    labels = encoder.transform(items)

    print('인코딩 클래스:', encoder.classes_)
    #print('디코딩 원본 값:', encoder.inverse_transform([0,1]))

    return encoder,doc_df



def order_decisionTree_recom(humidity,temp,speed,emo1,emo2):

    encoder,df = get_dataFrame()

    emotion = emotion_keyword_to_vec[emo1]+emotion_keyword_to_vec[emo2]

    #학습
    dt_clf = DecisionTreeClassifier()
    X_train,X_test,y_train,y_test = train_test_split(df[['w_humidity','w_temp','w_speed','emotion']],
                                                df[['item_name']], test_size=0.2)
    dt_clf.fit(X_train,y_train)

    pred = dt_clf.predict(X_test)
    accuracy = accuracy_score(y_test, pred)
    print('결정 트리 예측 정확도: {0:.4f}'.format(accuracy))

    # 람다
#    label_df = pd.DataFrame({ 'w_humidity':[event['humidity']],
#                        'w_temp':[event['temp']],
#                        'w_speed':[event['speed']],
#                        'emotion':[event['emotion']] })
    label_df = pd.DataFrame({ 'w_humidity':[humidity], # humidity,temp,speed,emotion
                            'w_temp':[temp],
                            'w_speed':[speed],
                            'emotion':[emotion] })

    result_predict = dt_clf.predict(label_df)

    return result_predict
