package com.example.change.setting;

import com.microsoft.projectoxford.face.FaceServiceClient;
import com.microsoft.projectoxford.face.FaceServiceRestClient;

public class AppSetting {

    // ms 계정 연동
    private final static String apiEndpoint = "https://westus.api.cognitive.microsoft.com/face/v1.0";
    private final static String subscriptionKey = "dbc846e99bd6424f83d26c11f6633492";
    public static FaceServiceClient faceServiceClient = new FaceServiceRestClient(apiEndpoint, subscriptionKey);

    public static String personGroupId; // test 그룹
    public static String personUUID=""; // test 그룹
    public static boolean registeredPersonFlag = false;
    public static boolean trainRequestFlag = false; // 처음 등록한 사람
    // 마지막 사진 찍고 모델 train 요청하는 플래그
    // 사용하고 false 설정해야 한다

}
