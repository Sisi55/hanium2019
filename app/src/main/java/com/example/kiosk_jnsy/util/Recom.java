package com.example.kiosk_jnsy.util;

import android.os.AsyncTask;
import android.util.Log;

import com.example.kiosk_jnsy.setting.AppSetting;
import com.microsoft.projectoxford.face.contract.Person;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;

public class Recom {



    public static class RecomXGBTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {

//            String uri = "http://api.openweathermap.org/data/2.5/weather?lat=37.652490&lon=127.013178&mode=json&APPID=41d82c8172c1c237afb77833d08a8a59";
            BufferedReader bufferedReader = null;
            StringBuilder sb = new StringBuilder();
            String json_str;
            try{

                //  /<humidity>/<temp>/<speed>
                SimpleDateFormat format1 = new SimpleDateFormat ( "yyyy-MM-dd HH:mm:ss");
                String today = format1.format(System.currentTimeMillis());

                String recom_url = AppSetting.aws_dns+"/xgb_recom/" +
                        today + "/"+
                        AppSetting.recom_weather_humidity+"/"+
                        AppSetting.recom_weather_temp+"/"+
                        AppSetting.recom_weather_speed+"/"+
                        AppSetting.emotion1.keySet().iterator().next() +"/"+
                        AppSetting.emotion2.keySet().iterator().next();

                URL url = new URL(recom_url);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                while ((json_str = bufferedReader.readLine()) != null) {
                    sb.append(json_str + "\n");
                }
                return sb.toString().trim();
            }catch(Exception e){
                Log.e("오류",e.getMessage());
                return "";
            }

//            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

/*
            Document doc = Jsoup.parse(s);
            s = doc.text();
*/

            AppSetting.response_xgb_personalize = s;
            Log.e("   recom xgb", s);
        }
    }

    public static class RecomWeatherEmotionTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {

//            String uri = "http://api.openweathermap.org/data/2.5/weather?lat=37.652490&lon=127.013178&mode=json&APPID=41d82c8172c1c237afb77833d08a8a59";
            BufferedReader bufferedReader = null;
            StringBuilder sb = new StringBuilder();
            String json_str;
            try {
                URL url = new URL(AppSetting.recom_weather_emotion_URL);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                while ((json_str = bufferedReader.readLine()) != null) {
                    sb.append(json_str + "\n");
                }
                return sb.toString().trim();
            } catch (Exception e) {
                Log.e("오류", e.getMessage());
                return "{}";
            }

//            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

//            Log.e(" jsoup before", s);

/*
            Document doc = Jsoup.parse(s);
            s = doc.text();
*/

            AppSetting.response_weather_emotion_matrix = s;
            Log.e("   recom wea", s);

        }

    }

    public static class RecomItemCFTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {

//            String uri = "http://api.openweathermap.org/data/2.5/weather?lat=37.652490&lon=127.013178&mode=json&APPID=41d82c8172c1c237afb77833d08a8a59";
            BufferedReader bufferedReader = null;
            StringBuilder sb = new StringBuilder();
            String json_str;
            try {
                String itemCF_URL = AppSetting.aws_dns + "/item_cf/" + AppSetting.personUUID;
                URL url = new URL(itemCF_URL);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                while ((json_str = bufferedReader.readLine()) != null) {
                    sb.append(json_str + "\n");
                }
                return sb.toString().trim();
            } catch (Exception e) {
                Log.e("오류", e.getMessage());
                return "{}";
            }

//            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

//            Log.e("  jsoup cf b", s);

/*
            Document doc = Jsoup.parse(s);
            s = doc.text();
*/

            AppSetting.response_CF_overall = s;
            Log.e("   recom cf", s);

        }

    }
/*
    class GetDataJSON extends AsyncTask<Void, Void, String> {
        // public GetDataJSON()
        @Override
        protected String doInBackground(Void... params) {
            //
            // 날씨 받아올 url
            String uri = "http://api.openweathermap.org/data/2.5/weather?lat=37.652490&lon=127.013178&mode=json&APPID=41d82c8172c1c237afb77833d08a8a59";
            BufferedReader bufferedReader = null;
            StringBuilder sb = new StringBuilder();
            String json;
            try{
                URL url = new URL(uri);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                while ((json = bufferedReader.readLine()) != null) {
                    sb.append(json + "\n");
                }
                return sb.toString().trim();
            }catch(Exception e){
                Log.d("오류",e.getMessage());
                return "e.getMessage()";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            // myJSON = result; // Activity 멤버 변수
            try{
                //JSONObject jsonObj = ggggnew JSONObject(result);
                //peoples = jsonObj.getJSONArray(TAG_RESULTS); // peoples는 Activity 멤버 JsonArray
                // tag results는 json dict의 key겠지 ? value-리스트를 가져온다는 소리
                // 일단 매개 result 를 출력하는 정도로만 시도해보자

                //Toast.makeText(getActivity().getApplicationContext(), "날씨 갱신함", Toast.LENGTH_SHORT).show();
                //Toast.makeText(getActivity().getApplicationContext(), result, Toast.LENGTH_SHORT).show();
                //description="초기";


                JSONArray jarray = new JSONObject(result).getJSONArray("weather");
                JSONObject jObject = jarray.getJSONObject(0);
                String description = jObject.optString("description");

                JSONObject main=new JSONObject(result).getJSONObject("main");
                AppSetting.recom_weather_humidity = main.optString("humidity");
                AppSetting.recom_weather_temp = main.optString("temp");

                JSONObject wind=new JSONObject(result).getJSONObject("wind");
                AppSetting.recom_weather_speed= wind.optString("speed");

                // 추천 받아온다
                new RecomItemCFTask().execute();


                //Toast.makeText(getActivity().getApplicationContext(), temp_d+"입니다.", Toast.LENGTH_SHORT).show();

                Log.d("날씨 갱신결과","설명 >>"+description+"\n온도 >>"+AppSetting.recom_weather_temp+"\n습도 >>"+AppSetting.recom_weather_humidity+"\n풍속 >>"+AppSetting.recom_weather_speed);
                //tv.setText("설명 >>"+description+"\n온도 >>"+temp+"\n습도 >>"+humidity+"\n풍속 >>"+speed);

                // 추천내역 받아온다

            }catch(Exception e){

            }
        }
    }
*/

}
