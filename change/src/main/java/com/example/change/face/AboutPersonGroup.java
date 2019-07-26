package com.example.change.face;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.util.Log;

//import com.example.camera2basic.setting.AppSetting;
import com.example.change.Camera2BasicFragment;
import com.example.change.setting.AppSetting;
import com.microsoft.projectoxford.face.contract.LargePersonGroup;

//import com.example.camera.setting.AppSetting;

public class AboutPersonGroup {

    static Handler handler = new Handler(Looper.getMainLooper());

    // class
    static public class ListPersonGroupTask extends AsyncTask<Void, Void, LargePersonGroup[]> {

        Fragment fragment;

        // 생성자
        public ListPersonGroupTask(Fragment fragment) {
            this.fragment = fragment;
        }

        @Override
        protected LargePersonGroup[] doInBackground(Void... voids) {
            try {

                return AppSetting.faceServiceClient.listLargePersonGroups();

            } catch (Exception e) {
                Log.e("   error", "listPersonGroup");
                return null;
            }
        }

        @Override
        protected void onPostExecute(LargePersonGroup[] personGroups) {
            super.onPostExecute(personGroups);

            if (personGroups == null) {
                return;
            }

            String str = personGroups.length + "  ";
            // 성공하면
            for (int i = 0; i < personGroups.length; i++) {
                str += personGroups[i].largePersonGroupId + "  ";
            }

            final String strr = str;
/* 지우지마요
            handler.postDelayed(new Runnable() { // handler 에 looper 할당안하면 여기서 오류
                @Override
                public void run() {
                    ((Camera2BasicFragment)fragment).addTextToEditText(strr);
                }
            },0);
*/
        }
    }
    //end task

    // class
    public static class GetPersonGroupTask extends AsyncTask<Void, Void, LargePersonGroup> {

        Fragment fragment;

        // 생성자
        public GetPersonGroupTask(Fragment fragment) {
            this.fragment = fragment;
        }

        @Override
        protected LargePersonGroup doInBackground(Void... voids) {

            try {

                return AppSetting.faceServiceClient.getLargePersonGroup(AppSetting.personGroupId);

            } catch (final Exception e) {
                // try catch 로그
/* 지우지마요
                handler.postDelayed(new Runnable() { // handler 에 looper 할당안하면 여기서 오류
                    @Override
                    public void run() {
                        ((Camera2BasicFragment)fragment).addTextToEditText("get try catch:\n"+e.getMessage()+"\n<end>\n");
                    }
                },0);
*/
            }
            //end try catch
            return null;
        }

        @Override
        protected void onPostExecute(LargePersonGroup group) {
            super.onPostExecute(group);

            if (group == null) { // try cath 오류, 또는 결과 없음
/* 지우지마요
                handler.postDelayed(new Runnable() { // handler 에 looper 할당안하면 여기서 오류
                    @Override
                    public void run() {
                        ((Camera2BasicFragment)fragment).addTextToEditText("get null\n");
                    }
                },0);
*/

//                new AboutPersonGroup.CreatePersonGroupTask(fragment).execute(AppSetting.personGroupId); // 그룹id 전달

                return;
            }
        }
    }
    //end class

    // 그룹 생성
    public static class CreatePersonGroupTask extends AsyncTask<String, String, String> {

        Fragment fragment;

        public CreatePersonGroupTask(Fragment fragment) {
            this.fragment = fragment;
        }
        //end 생성자

        @Override
        protected String doInBackground(String... params) {


            try {

                AppSetting.faceServiceClient.createLargePersonGroup(
                        AppSetting.personGroupId, // 그룹 id
                        "sisisi", // name
                        null); // userData

                return params[0]; // 그룹 id 반환

            } catch (final Exception e) {

/* 지우지마요
                handler.postDelayed(new Runnable() { // handler 에 looper 할당안하면 여기서 오류
                    @Override
                    public void run() {
                        ((Camera2BasicFragment)fragment).addTextToEditText("create try catch "+e.getMessage()+"\n");
                    }
                },0);
*/
                return null;
            }
        }
        //end doIn

        @Override
        protected void onPostExecute(String result) {

            Log.e("   create person group", "");

            if (result != null) {
//                ((Camera2BasicFragment)fragment).addTextToEditText("create ok\n");
            }
        }
    }
    //end class create person group

    public static class TrainPersonGroupTask extends AsyncTask<Void, String, Boolean> {

        Fragment fragment;

        public TrainPersonGroupTask(Fragment fragment) {
            this.fragment = fragment;
        }

        @Override
        protected Boolean doInBackground(Void... aVoid) {

            try {

                AppSetting.faceServiceClient.trainLargePersonGroup(AppSetting.personGroupId);

                return true;

            } catch (Exception e) {
                Log.e("   error", "train group"); // 오류 로그 찍는다
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {

            if (result == false) { // 실패하면
                return;
            }

            // 성공 로그
            Log.e("   train group", "");


            handler.postDelayed(new Runnable() { // handler 에 looper 할당안하면 여기서 오류
                @Override
                public void run() {
                    ((Camera2BasicFragment)fragment).addTextToEditText("train request\n");
                }
            },0);

        }
    }
    //end train group

}// end class

