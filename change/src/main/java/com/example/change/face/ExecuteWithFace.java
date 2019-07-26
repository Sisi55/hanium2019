package com.example.change.face;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.util.Log;

//import com.example.camera2basic.Camera2BasicFragment;
//import com.example.camera2basic.setting.AppSetting;
import com.example.change.Camera2BasicFragment;
import com.example.change.setting.AppSetting;
import com.microsoft.projectoxford.face.FaceServiceClient;
import com.microsoft.projectoxford.face.contract.Emotion;
import com.microsoft.projectoxford.face.contract.Face;
import com.microsoft.projectoxford.face.contract.IdentifyResult;
import com.microsoft.projectoxford.face.contract.Person;
import com.microsoft.projectoxford.face.contract.TrainingStatus;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

//import com.example.camera.MainActivity;
//import com.example.camera.setting.AppSetting;


public class ExecuteWithFace {

    static Handler handler = new Handler(Looper.getMainLooper());
    // class
    public static class DetectAndAddFaceTask extends AsyncTask<Void,Void, Face[]> {
        Fragment fragment;
        InputStream inputStream;
        boolean detectFlag = false;
        byte[] bytes;
        // 생성자
        public DetectAndAddFaceTask(byte[] bytes, final Fragment fragment) {
            this.bytes=bytes;  this.fragment=fragment;
            inputStream = new ByteArrayInputStream(bytes);
/* 지우지마요
            int atemp=-1;
            try{
                atemp = inputStream.available();

            }catch(Exception e){
                Log.e("  availe error","");
            }
            final int a = atemp;
            handler.post(new Runnable() {
                @Override
                public void run() {
                    ((Camera2BasicFragment)fragment).addTextToEditText("detect image size"+a);
                }
            });
*/
        }
        public DetectAndAddFaceTask(InputStream in, final Fragment fragment){
            inputStream = in;
//            inputStreamCopy = new ByteArrayInputStream(in);
//            mainActivity = context;
            this.fragment = fragment;
            int atemp=-1;
            try{
                atemp = inputStream.available();

            }catch(Exception e){
                Log.e("  availe error","");
            }
            final int a = atemp;
/*
            handler.post(new Runnable() {
                @Override
                public void run() {
                    ((Camera2BasicFragment)fragment).addTextToEditText("detect image size"+a);
                }
            });
*/
        }
        @Override
        protected Face[] doInBackground(Void... voids) {
            try{

                // detect 가 여러 얼굴 탐지하는 똑똑한 함수였구나
                return AppSetting.faceServiceClient.detect(inputStream,true,false,
                        null);
                /*new FaceServiceClient.FaceAttributeType[] {
                                FaceServiceClient.FaceAttributeType.Emotion
                        }*/

            }
            catch (final Exception ex) // 여기서 오류가 났다  detect 자체가 실행되지 않았다 이유가 뭐지
            {
/* 지우지마요
                handler.postDelayed(new Runnable() { // handler 에 looper 할당안하면 여기서 오류
                    @Override
                    public void run() {
                        ((Camera2BasicFragment)fragment).addTextToEditText("detect try catch failed"+ ex.getMessage());
                    }
                },0);
*/
                return null;
            }
            //end try catch
        }

        @Override
        protected void onPostExecute(Face[] faces) {
            super.onPostExecute(faces);
            if(faces != null){
                detectFlag = true; // 사람있다
            }
/*
            String result_emotion = getEmotion(faces[0].faceAttributes.emotion);

            ((Camera2BasicFragment)fragment).addTextToEditText("감정: "+result_emotion);
*/
            // add face
            new AddFaceTask(/*전역:create 후 저장*/UUID.fromString(AppSetting.personUUID),
                    bytes, /*faces[0],*/ fragment, detectFlag).execute();
//            ((Camera2BasicFragment)fragment).addTextToEditText("new add face task");
        }
        private String getEmotion(Emotion emotion){
            Map<String, Double> emotionMap = new HashMap<>();
            emotionMap.put("anger",emotion.anger);
            emotionMap.put("contempt",emotion.contempt);
            emotionMap.put("disgust",emotion.disgust);
            emotionMap.put("fear",emotion.fear);

            emotionMap.put("happiness",emotion.happiness);
            emotionMap.put("neutral",emotion.neutral);
            emotionMap.put("sadness",emotion.sadness);
            emotionMap.put("surprise",emotion.surprise);

            // map 내림차 정렬
            Iterator iterator = sortByValue(emotionMap/* map */).iterator(); // 내림차 정렬
            // 값으로 내림차 정렬된 키가 들어있는 Iterator
            String maxValueKey = (String) iterator.next();

            return maxValueKey + ": "+ emotionMap.get(maxValueKey);
            //감정: 점수 형태로 가장 최대 하나만 반환하자
        }
        // 예상 점수 내림차 정렬한다
        //from: getEmotion
        private List sortByValue(final Map map) {

            List<String> list = new ArrayList();
            list.addAll(map.keySet());

            Collections.sort(list,new Comparator() { // key를 내림차 value에 대해 정렬

                public int compare(Object o1, Object o2) { // (오름차: 1>2일 때 return 양수

                    Object v1 = map.get(o1);
                    Object v2 = map.get(o2);

                    return ((Comparable) v2).compareTo(v1); //compareTo: 인자가 더 크면 return 음수
                    //compare: 양수인 경우  두 객체의 자리가 바뀐다
                    // 뭐.. 잘 안나오면 1,2 바꿔봐..
                }

            });

            return list; // key 리스트 리턴한다
        }


    }// end class

    // detect 하고 identify 하는 클래스 !
    public static class DetectAndIdentifyTask extends AsyncTask<InputStream,String, Face[]> {

        UUID[] facesDetected;
        InputStream inputStream;
        Face[] faceArray;
        // detect() 하고 post() 에서 루프 돌며 face 저장한다
        Context mainActivity;
        Fragment fragment;
        boolean detectFlag = false;
        byte[] bytes;

        // 생성자
        public DetectAndIdentifyTask(byte[] bytes, Fragment fragment) {
            this.bytes=bytes;  this.fragment=fragment;
            this.inputStream = new ByteArrayInputStream(bytes);
        }
        public DetectAndIdentifyTask(InputStream in, Context context){
            inputStream = in;
            mainActivity = context;
        }

        public DetectAndIdentifyTask(InputStream in, Fragment fragment){
            inputStream = in;
//            mainActivity = context;
            this.fragment = fragment;
        }

        @Override
        protected Face[] doInBackground(InputStream... params) {

            try{
                // detect 가 여러 얼굴 탐지하는 똑똑한 함수였구나
                return AppSetting.faceServiceClient.detect(inputStream,true,false,
                        new FaceServiceClient.FaceAttributeType[] {
                                FaceServiceClient.FaceAttributeType.Emotion
                        });
                // 사진 속 얼굴이 들어있겠다. family 얼굴 4개 !

            }
            catch (final Exception ex) // 여기서 오류가 났다  detect 자체가 실행되지 않았다 이유가 뭐지
            {
/* 지우지마요
                handler.postDelayed(new Runnable() { // handler 에 looper 할당안하면 여기서 오류
                    @Override
                    public void run() {
                        ((Camera2BasicFragment)fragment).addTextToEditText(" detect-i try catch failed"+ ex.getMessage());
                    }
                },0);
*/
                return null;
            }
            //end try catch

        }//end doIn

        @Override
        protected void onPostExecute(Face[] faces) {

            if(faces == null){
                Log.e("   detect failed","");
/* 지우지마요
                ((Camera2BasicFragment)fragment).addTextToEditText("   detect failed");
*/
                return;
            }

            this.faceArray = faces; // Face[]
            detectFlag = true; // true

            // identify 단계로 넘어가자 !
            facesDetected = new UUID[faces.length];

            //일단 첫 번째 사람 감정만 출력해본다
            String result_emotion = getEmotion(faces[0].faceAttributes.emotion);

            ((Camera2BasicFragment)fragment).addTextToEditText("감정: "+result_emotion);


            for(int i=0;i<facesDetected.length;i++){
                facesDetected[i] = faces[i].faceId;
                Log.e("   detect>UUID:  ", ""+facesDetected[i]); // 체크
//                ((Camera2BasicFragment)fragment).addTextToEditText("\n"+"   detect>ok "+faces[i].faceRectangle.width+","+faces[i].faceRectangle.height);

            }

            // 정의하고 바로 아무 매개없이 호출한다
            class IdentificationTask extends AsyncTask<Void,String, IdentifyResult[]> {

                InputStream in;
                public IdentificationTask(){
                    in = new ByteArrayInputStream(bytes);
/* 지우지마요
                    int atemp=-1;
                    try{
                        atemp = in.available();

                    }catch(Exception e){
                        Log.e("  availe error","");
                    }
                    final int a = atemp;
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            ((Camera2BasicFragment)fragment).addTextToEditText("identify image size"+a);
                        }
                    });
*/
                }

                @Override
                protected IdentifyResult[] doInBackground(Void... aVoid) {

                    try{

                        // getLargePersonGroupTrainingStatus
                        TrainingStatus trainingStatus  = AppSetting.faceServiceClient.getLargePersonGroupTrainingStatus(AppSetting.personGroupId);

                        if(trainingStatus.status == TrainingStatus.Status.Failed)
                        {

                            // 상당히 오랜시간 train 한다 ?
                            handler.postDelayed(new Runnable() { // handler 에 looper 할당안하면 여기서 오류
                                @Override
                                public void run() {
                                    ((Camera2BasicFragment)fragment).addTextToEditText("train status failed");
                                }
                            },0);

                            Log.e("train status failed","");

                            return null;

                        }else if(trainingStatus.status == TrainingStatus.Status.Running){

                            handler.postDelayed(new Runnable() { // handler 에 looper 할당안하면 여기서 오류
                                @Override
                                public void run() {
                                    ((Camera2BasicFragment)fragment).addTextToEditText("train status running ok ?");
                                }
                            },0);

                            Log.e("train status running","");
//                            return null;
                        }else if(trainingStatus.status == TrainingStatus.Status.Succeeded){

                            handler.postDelayed(new Runnable() { // handler 에 looper 할당안하면 여기서 오류
                                @Override
                                public void run() {
                                    ((Camera2BasicFragment)fragment).addTextToEditText("train status succeeded");
                                }
                            },0);

                            Log.e("train status succeeded","");
                        }
                        //end if else
                        if(detectFlag == true){

                            return AppSetting.faceServiceClient.identityInLargePersonGroup(
                                    AppSetting.personGroupId, // person group id
                                    facesDetected // face ids 상위 클래스 멤버
                                    ,1); // return IdentifyResult[]
                        }else{
                            return null; // 얼굴 detect 되지 않으면 return null
                        }
                        //end if else

                    } catch (final Exception e)
                    {

                        handler.postDelayed(new Runnable() { // handler 에 looper 할당안하면 여기서 오류
                            @Override
                            public void run() {
                                ((Camera2BasicFragment)fragment).addTextToEditText("  d-identify try catch:\n"+ e.getMessage()+"\n");
                            }
                        },0);

                        return null;
                    }


//                    return null;
                }//end doIn

                @Override
                protected void onPostExecute(IdentifyResult[] identifyResults) {

                    if(identifyResults == null){
                        return;
                    }

                    // identifyResults.length
//                    ((Camera2BasicFragment)fragment).addTextToEditText("  d-identify length:"+ identifyResults.length);

                    for(int i=0;i<1;i++){ // 한번만 루프
                        // identify uuid 출력
//                        ((Camera2BasicFragment)fragment).addTextToEditText("   identify>UUID:  "+ ""+identifyResults[i].faceId);
                        if(identifyResults[i].candidates.size() != 0) {
//                            ((Camera2BasicFragment)fragment).addTextToEditText("   identify: 사람있다");

                            // 여기서 get person
                            // "uuid: "+identifyResults[i].candidates.get(0).personId
                            new GetPersonTask(fragment/*, identifyResults[i].candidates.get(0).personId*/).execute(identifyResults[i].candidates.get(0).personId);


                            ((Camera2BasicFragment)fragment).addTextToEditText("정확도:"+identifyResults[i].candidates.get(0).confidence);
//                            identifyResults[i].candidates.get(0).personId
                            AppSetting.personUUID = identifyResults[i].candidates.get(0).personId.toString();
                            AppSetting.trainRequestFlag=true; // true하면 사용한 곳에서 자동으로 false 초기화한다
                            // 여기 add face
                            new AddFaceTask(/*전역:create 후 저장*/UUID.fromString(AppSetting.personUUID),
                                    bytes, /*faces[0],*/ fragment, detectFlag).execute();
// get person 추가하면 name도 얻을 수 있다
// 바로 아래에 있다

                        }else{
                            ((Camera2BasicFragment)fragment).addTextToEditText("등록안됨");
                        }
                        //end if else
                    }
                    //end for
                }
            }
            //end task identify

            new IdentificationTask().execute();

        }
        //end post method

        private String getEmotion(Emotion emotion){
            Map<String, Double> emotionMap = new HashMap<>();
            emotionMap.put("anger",emotion.anger);
            emotionMap.put("contempt",emotion.contempt);
            emotionMap.put("disgust",emotion.disgust);
            emotionMap.put("fear",emotion.fear);

            emotionMap.put("happiness",emotion.happiness);
            emotionMap.put("neutral",emotion.neutral);
            emotionMap.put("sadness",emotion.sadness);
            emotionMap.put("surprise",emotion.surprise);

            // map 내림차 정렬
            Iterator iterator = sortByValue(emotionMap/* map */).iterator(); // 내림차 정렬
            // 값으로 내림차 정렬된 키가 들어있는 Iterator
            String maxValueKey = (String) iterator.next();

            return maxValueKey + ": "+ emotionMap.get(maxValueKey);
            //감정: 점수 형태로 가장 최대 하나만 반환하자
        }
        // 예상 점수 내림차 정렬한다
//from: getEmotion
        private List sortByValue(final Map map) {

            List<String> list = new ArrayList();
            list.addAll(map.keySet());

            Collections.sort(list,new Comparator() { // key를 내림차 value에 대해 정렬

                public int compare(Object o1, Object o2) { // (오름차: 1>2일 때 return 양수

                    Object v1 = map.get(o1);
                    Object v2 = map.get(o2);

                    return ((Comparable) v2).compareTo(v1); //compareTo: 인자가 더 크면 return 음수
                    //compare: 양수인 경우  두 객체의 자리가 바뀐다
                    // 뭐.. 잘 안나오면 1,2 바꿔봐..
                }

            });

            return list; // key 리스트 리턴한다
        }


    }
    // end task

    // detect and identify task > identification task > get person task 순으로 호출된다
    private static class GetPersonTask extends AsyncTask<UUID,String, Person> {

        InputStream inputStream; Face face;
        Context mainActivity;
        Fragment fragment;

        public GetPersonTask(){}

        public GetPersonTask(InputStream in, Face face){
            inputStream = in; this.face = face;
        }
        //end 생성자

        public GetPersonTask(Context context, InputStream in, Face face){
            inputStream = in; this.face = face;
            mainActivity = context;
        }
        //end 생성자

        public GetPersonTask(Fragment fragment, InputStream in, Face face){
            inputStream = in; this.face = face;
//            mainActivity = context;
            this.fragment = fragment;
        }

        public GetPersonTask(Fragment fragment){
            this.fragment = fragment;
        }
        //end 생성자

        @Override
        protected Person doInBackground(UUID... params) {
            try{

                // uuid만 보내주면 되겠다
                return AppSetting.faceServiceClient.getPersonInLargePersonGroup(AppSetting.personGroupId, params[0]);

            } catch (Exception e)
            {
                return null;
            }
        }
        //end doIn

        @Override
        protected void onPostExecute(final Person person) {

            if(person == null)
                return;

/*
            ((Camera2BasicFragment)fragment).addTextToEditText(person.name);
*/

// 여기서도 ui 접근가능한데
            handler.post(new Runnable() {
                @Override
                public void run() {

                    ((Camera2BasicFragment)fragment).addTextToEditText("이름: "+person.name);

//                    Log.e("   getPerson>UUID:  ", ""+person.personId); // 체크

                }
            });

/*
            // 일단은 일련의 순서로 작성하지만
            // 재사용 할 수 있도록 좀 더 분리된 구조를 생각해봤으면 좋겠다
            // 그런데 UUID 말고도 필요한게 더 있다 시발
            // UUID, inputStream, face 안의 rect
            new AddFaceTask(person.personId, inputStream, face).execute();
            // UUID를 전달하는 형태로 변경하자

            // InputStream inputStream; Face face;
*/


/*
            String str = binding.text.getText().toString()+"\n";
            if(person.name == null)
                str += "result null";
            else str += person.name;
            binding.text.setText(str); // 그럼 검출된 사람 수 만큼 add 되겠지!
*/

/*
            ImageView img = (ImageView)findViewById(R.id.imageView);
            imageView.setImageBitmap(drawFaceRectangleOnBitmap(mBitmap,facesDetected,person.name));
*/
        }
        //end post


    }
    //end task

    // add face task는 하나만 처리할 수 있다
    public static class AddFaceTask extends AsyncTask<Void, String, Boolean> {

        InputStream inputStream;
        Face face;
        UUID personId;
        Fragment fragment;
        boolean detectFlag;
        byte[] bytes;
        // 얼굴 하나 add 할 생성자
        AddFaceTask(UUID personId, byte[] bytes, /*Face face, */final Fragment fragment, boolean detectFlag) {
            this.personId = personId;  /*this.inputStream = inputStream;*/  /*this.face = face;*/  this.fragment=fragment;  this.detectFlag=detectFlag;
            this.bytes = bytes;
            this.inputStream = new ByteArrayInputStream(bytes);
/* 지우지마요
            int atemp=-1;
            try{
                atemp = inputStream.available();

            }catch(Exception e){
                Log.e("  availe error","");
            }
            final int a = atemp;
            handler.post(new Runnable() {
                @Override
                public void run() {
                    ((Camera2BasicFragment)fragment).addTextToEditText("detect image size"+a);
                }
            });
*/
        }
        AddFaceTask(UUID personId, InputStream inputStream, Face face, final Fragment fragment, boolean detectFlag) {
            this.personId = personId;  this.inputStream = inputStream;  this.face = face;  this.fragment=fragment;  this.detectFlag=detectFlag;
            int atemp=-1;
            try{
                atemp = inputStream.available();

            }catch(Exception e){
                Log.e("  availe error","");
            }
            final int a = atemp;
            handler.post(new Runnable() {
                @Override
                public void run() {
                    ((Camera2BasicFragment)fragment).addTextToEditText("add face image size"+a);
                }
            });
        }
        AddFaceTask(UUID personId, InputStream inputStream, /*Face face, */Fragment fragment) {
            this.personId = personId;  this.inputStream = inputStream; /* this.face = face;*/  this.fragment=fragment;
        }
        AddFaceTask(InputStream inputStream) {

            this.inputStream = inputStream;
        }
        AddFaceTask(InputStream inputStream, Face[] faces) {
            this.inputStream = inputStream;
        }

        @Override
        protected Boolean doInBackground(Void... params) {


            try{

                if(detectFlag == true){
                    AppSetting.faceServiceClient.addPersonFaceInLargePersonGroup(
                            AppSetting.personGroupId,
                            personId,  // Person 에서 UUID
                            inputStream, // 이미지 Input stream
                            null, // user data ?
                            null/*face.faceRectangle*/); // Face rect 얼굴 범위
                    return true;

                }else{
                    return false;
                }
                //end if else

            } catch (final Exception e) {
                Log.e("   error", "add face"); // 오류 로그 찍는다

                handler.postDelayed(new Runnable() { // handler 에 looper 할당안하면 여기서 오류
                    @Override
                    public void run() {
                        ((Camera2BasicFragment)fragment).addTextToEditText("add face try failed "+e.getMessage());
                        Log.e("add face try failed ",""+e.getMessage());
                    }
                },0);

                return false;

            }
        }

        @Override
        protected void onPostExecute(Boolean result) {

            if (result == false) { // 실패하면
                ((Camera2BasicFragment)fragment).addTextToEditText("add face 실패");
                return;
            }

            // 성공 로그
            Log.e("   add face", "");

            if(AppSetting.trainRequestFlag == true){
                new AboutPersonGroup.TrainPersonGroupTask(fragment).execute();
                AppSetting.trainRequestFlag = false; // 사용하고 초기화
            }
            ((Camera2BasicFragment)fragment).addTextToEditText("add face 성공");
        }
    }
    //end add face task

}//end class
