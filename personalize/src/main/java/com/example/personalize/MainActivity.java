package com.example.personalize;

//import android.graphics.Color;
//import android.graphics.Color;
import nl.dionsegijn.konfetti.models.Shape;
import nl.dionsegijn.konfetti.models.Size;
//import android.graphics.Color;
//import android.graphics.drawable.shapes.Shape;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import nl.dionsegijn.konfetti.KonfettiView;

public class MainActivity extends AppCompatActivity {

    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        textView = (TextView) findViewById(R.id.textView);

/*
        String url = "https://n87vqvuisa.execute-api.ap-northeast-2.amazonaws.com/Prod/greeting";
        String name = "kiosk";
        String parameter = "?name="+name;
        new AWSconnectTask().execute(url+parameter);
*/

        final KonfettiView konfettiView = findViewById(R.id.konfettiView);
        konfettiView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                konfettiView.build()
                        .addColors(Color.YELLOW, Color.GREEN, Color.MAGENTA)
                        .setDirection(0.0, 359.0)
                        .setSpeed(1f, 5f)
                        .setFadeOutEnabled(true)
                        .setTimeToLive(2000L)
                        .addShapes(Shape.RECT, Shape.CIRCLE)
                        .addSizes(new Size(12, 5f))
                        .setPosition(-50f, konfettiView.getWidth() + 50f, -50f, -50f)
                        .streamFor(300, 5000L);
            }
        });
    }

    class AWSconnectTask extends AsyncTask<String, Void, String> {


        @Override
        protected String doInBackground(String... strings) {

            String uri = strings[0];
            BufferedReader bufferedReader = null;

            try {
                URL url = new URL(uri);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();

                StringBuilder sb = new StringBuilder();
                bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));

                String json;
                while ((json = bufferedReader.readLine()) != null) {
                    sb.append(json + "\n");
                }

                return sb.toString().trim();

            } catch (Exception e) {
                return null;
            }

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            String name=null;
            try{

                JSONObject jsonObject = new JSONObject(s);
                name = jsonObject.getString("greeting");

            }catch(Exception e){
                Log.e("   error json", e.getMessage());
            }
//            int age = jsonObject.getInt("age");
//            boolean flag = jsonObject.getBoolean("flag");
//            double weight = jsonObject.getDouble("weight");

            textView.setText(name);
        }

    }//end class

}
