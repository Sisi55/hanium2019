# import firebase_admin
# from firebase_admin import credentials
from firebase_admin import firestore
import pandas as pd
import numpy as np
from sklearn.preprocessing import LabelEncoder
from sklearn.metrics.pairwise import cosine_similarity
import json


#cred = credentials.Certificate('kiosk-firestore-jnsy-bcfe4-firebase-adminsdk-269q4-4a795774bb.json')
#firebase_admin.initialize_app(cred)


def get_user_rating_list(tag='user'):
    docs = firestore.client().collection(tag).stream()
    doc_list = []
    
    for doc in docs:
        doc_list_row = []
        doc_list_row.append(doc.get('personUUID'))
        for key in doc.get('itemPreference').keys(): # {아이템이름str:점수int}
            doc_list_row.append([key,doc.get('itemPreference').get(key)])
            
        doc_list.append(doc_list_row)
    
    return doc_list


def data_preprocessing(user_items_list):
    temp_list = [] # user,item,rating 저장할 리스트
    
    for row in user_items_list: #[0]은 항상 person uuid
        if len(row) != 1: #길이가 하나인 행은 uuid만 있으므로 제외
            size = len(row)-1  # size는 user의 점수 매긴 item 개수
            for i in range(1,size+1): # 한 행 안의 아이템 루프 
                temp_list.append([row[0], row[i][0] , row[i][1] ])
                
    return temp_list


def userid_preprocessing(rating_item_df): #userid 전처리
    
    userid_encoder = LabelEncoder()
    users = rating_item_df['userid'].values.tolist()
    userid_encoder.fit(users)
    users_vec = userid_encoder.transform(users)
    
#print('인코딩 클래스:', userid_encoder.classes_)
#print('디코딩 원본 값:', encoder.inverse_transform([0,1])) #리스트나 스칼라로 전달하면 변환해서 반환함
    rating_item_df['userid'] = pd.Series(users_vec)
    
    return userid_encoder,rating_item_df # 사용자가 아이템에 점수 매긴 DF ## return


def preference_df(ratings_matrix,item_sim_df):
    
    ratings_pred = ratings_matrix.values.dot(item_sim_df.values)/np.array([np.abs(item_sim_df.values).sum(axis=1)])
    df = pd.DataFrame(data=ratings_pred, index=ratings_matrix.index, columns=ratings_matrix.columns)
    
    return df

def user_already_ordered(user_id,ratings_matrix):
    user_rating = ratings_matrix.loc[user_id,:] # 그래서 슬라이싱을 했구나
    return user_rating[user_rating>0].index.tolist() ## 이 두줄 필요


def item_cf(user_id,ratings_matrix,ratings_pred_matrix,top_n=1): #
    
    already_ordered = user_already_ordered(user_id,ratings_matrix)
    
    order_list = ratings_matrix.columns.tolist() # 전체 메뉴판에서 가져올 필요가 없다 계산못하니까
    unordered_list = [item for item in order_list if item not in already_ordered]
    recomm_item = ratings_pred_matrix.loc[user_id, unordered_list].sort_values(ascending=False)[:top_n]
    
    return recomm_item #추천 아이템 1개


def not_order_item_dict(already_ordered,item_sim_des_dict):
    return_dict = {}
    for index,value_list in item_sim_des_dict.items():
        
        temp_list = [item for item in value_list if item not in already_ordered]
        if len(temp_list) != 0:
            return_dict[index] = temp_list[0]
        else: # 길이가 0이면
            return_dict[index] = []
        
    
    return return_dict


def items_sim_dict(user_id,ratings_matrix,item_sim_df):
    already_ordered = user_already_ordered(user_id,ratings_matrix) #list
    
    item_sim_des_dict = {}
    for row,item in enumerate(item_sim_df):
        #print(row, item) #item은 행:아이템 이름
        row_ndarray = item_sim_df.iloc[row].values
    
        des_sort_index = np.argsort(row_ndarray)[::-1][1:]
        
        item_sim_des_list = item_sim_df.iloc[row].index[des_sort_index].tolist()
        
    
        item_sim_des_dict[item] = item_sim_des_list #하나만
        
    return not_order_item_dict(already_ordered,item_sim_des_dict)


def processing_to_str(result,item_sim_des_dict):
    
    # temp_str = '{'
    # for index,value in item_sim_des_dict.items():
    #     temp_str = temp_str +'"'+ index +'"'+ ":" + '"'+value + '",'
    # temp_str = temp_str + '}'
    #item_sim_dict = json.dumps(item_sim_des_dict, ensure_ascii=False, indent=4)
    keys = ('user_cf','items_sim')
    values = (result, item_sim_des_dict)
    print(result, item_sim_des_dict)
    #result_zip = zip(keys, values)
    result_dict = dict(zip(keys, values))
    #result_dict = {}
    #map(lambda k,v: result_dict.update({k:v}), keys, values)
    
    return json.dumps(result_dict, ensure_ascii=False)#, indent=4)#'{"user_cf":"'+ result +'","items_sim":'+ temp_str +'}'
    


def item_collaborativeFiltering(user_uuid): ## 매개 request 포함 path() 설정할 뷰 함수
    
    user_items_list = data_preprocessing(get_user_rating_list()) # order DB에서 데이터 가공해서 가져온다
    
    rating_item_df = pd.DataFrame(user_items_list, columns=['userid','itemid','rating']) #ndarray로 변환
    userid_encoder,rating_item_df = userid_preprocessing(rating_item_df)
    
    user_id = userid_encoder.transform([user_uuid]).tolist()[0]
    
    
    ratings_matrix = rating_item_df.pivot_table(values='rating', index='userid', columns='itemid').fillna(0)
    ratings_matrix_T = ratings_matrix.transpose()
    
    item_sim = cosine_similarity(ratings_matrix_T, ratings_matrix_T)
    item_sim_df = pd.DataFrame(data=item_sim, index=ratings_matrix.columns, columns=ratings_matrix.columns)
    
    ratings_pred_matrix = preference_df(ratings_matrix, item_sim_df)
    result = item_cf(user_id,ratings_matrix,ratings_pred_matrix).index.tolist()[0] #
    
    item_sim_des_dict = items_sim_dict(user_id,ratings_matrix,item_sim_df)
    
        #result = "{'user_cf':"+item_cf_result+",'items_sim':"+items_sim_dict+"}"
    str_result = processing_to_str(result,item_sim_des_dict)

    return str_result ## 마지막 str
