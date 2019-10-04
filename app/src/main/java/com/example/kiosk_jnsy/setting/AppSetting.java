package com.example.kiosk_jnsy.setting;

import com.microsoft.projectoxford.face.FaceServiceClient;
import com.microsoft.projectoxford.face.FaceServiceRestClient;

import java.util.Map;
import java.util.UUID;

public class AppSetting {

    // aws 서버
    public static String protocol = "http://";
    public static String aws_dns = protocol + "ec2-15-164-215-70.ap-northeast-2.compute.amazonaws.com";
    // 요청 url
//    public static String recom_xgb_URL = aws_dns + "/xgb_recom/2019-09-09%2021:05:58/80.0/300.0/1.00/happiness/neutral";
    public static String recom_weather_humidity;
    public static String recom_weather_temp;
    public static String recom_weather_speed;
//    public static String recom_itemCF_URL = aws_dns + "/item_cf/911283bd-2e4f-439b-89c4-fc8667782b51";
    // user_cf  items_sim
    public static String recom_weather_emotion_URL = aws_dns + "/weather_emotion_recom/";
    // 응답 결과
    public static String response_xgb_personalize=null;  // 키: item
    public static String response_CF_overall=null;   // items_sim
//    public static String response_CF_personalize=null;   // user_cf
    public static String response_weather_emotion_matrix=null;   // 각 아이템 이름


    // ms 계정 연동
    private final static String apiEndpoint = "https://sisi.cognitiveservices.azure.com/face/v1.0";
    private final static String subscriptionKey = "cee5fa54119d469dadcc4e2e158e9f2c";
    public static FaceServiceClient faceServiceClient = new FaceServiceRestClient(apiEndpoint, subscriptionKey);

    public static String personGroupId="mini-cafe"; // test 그룹
    public static String personUUID=null; // test 그룹
    public static String personName=null;
    public static boolean registeredPersonFlag = false;
    public static boolean trainRequestFlag = false; // 처음 등록한 사람
    // 마지막 사진 찍고 모델 train 요청하는 플래그
    // 사용하고 false 설정해야 한다

//    public static String emotion=null;
    public static boolean camefromMain = false;
    public static boolean camefromCamera = false;
    public static boolean isSetPersonUUID=false;
    public static Map<String,Double> emotion=null;
    public static Map<String,Double> emotion1=null;
    public static Map<String,Double> emotion2=null;
    public static boolean progressEndFlag=true;

    public static Map<String,Integer> itemPreferences=null;
    public static String documentID=null;
    public static boolean isSetPersonalRecom=false;

    public static final int PREFERENCE_CLICK = 1;
    public static final int PREFERENCE_SHOPLIST = 3;
    public static final int PREFERENCE_ORDER = 5;


}
