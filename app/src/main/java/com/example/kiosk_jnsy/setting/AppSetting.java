package com.example.kiosk_jnsy.setting;

import com.microsoft.projectoxford.face.FaceServiceClient;
import com.microsoft.projectoxford.face.FaceServiceRestClient;

public class AppSetting {

    // ms 계정 연동
    private final static String apiEndpoint = "https://skdud.cognitiveservices.azure.com/face/v1.0";
    private final static String subscriptionKey = "daa9aadc76a24e9898a474960b08aa5d";
    public static FaceServiceClient faceServiceClient = new FaceServiceRestClient(apiEndpoint, subscriptionKey);

    public static String personGroupId="cafetest"; // test 그룹
    public static String personUUID=""; // test 그룹
    public static boolean registeredPersonFlag = false;
    public static boolean trainRequestFlag = false; // 처음 등록한 사람
    // 마지막 사진 찍고 모델 train 요청하는 플래그
    // 사용하고 false 설정해야 한다

    public static String emotion=null;
    public static boolean camefromMain = false;
    public static boolean isSetPersonUUID=false;

}
