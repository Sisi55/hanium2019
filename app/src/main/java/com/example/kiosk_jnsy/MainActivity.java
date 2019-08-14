package com.example.kiosk_jnsy;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Camera;
import android.service.media.CameraPrewarmService;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import com.example.kiosk_jnsy.databinding.ActivityMainBinding;
import com.example.kiosk_jnsy.face.AboutPerson;
import com.example.kiosk_jnsy.face.AboutPersonGroup;
import com.example.kiosk_jnsy.setting.AppSetting;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    boolean iamRegistered = false;
    View alertView;
    String name;
    EditText editName;

    private void showRegisterDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this/*getContext()*/)
                .setTitle("얼굴 등록하셨나요?")
                //.setView(alertView)
                //.setMessage("10번 사진 찍습니다!")
                .setPositiveButton("네", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        AppSetting.registeredPersonFlag = true;
                        showGetPermissionDialog();
                    }
                }).setNegativeButton("아니요", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AppSetting.registeredPersonFlag = false;
                        showGetPermissionDialog();
                    }
                }
        );


        // 되려나 된다 모달리스
        builder.show().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
    }

    public void intentToCameraActivity(){
        AppSetting.camefromMain = true;
        startActivity(new Intent(this,CameraActivity.class));
    }

    private void showGetPermissionDialog(){
        // 얼굴 등록했다고 말했으면 iamRegistered =true,
        // 얼굴 인식 한다고 하면 한번만 찍어서 결과 보여주면 된다

        // 얼굴 등록 안했으면 iamRegistered = false,
        // 얼굴 인식한다고 하면 이름 등록하고 10번 찍어서 메뉴판 보여주기


        // 대화상자 출력한다
        new AlertDialog.Builder(this/*getContext()*/)
                .setTitle("얼굴 인식하시겠습니까?")
                .setPositiveButton("네", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if(AppSetting.registeredPersonFlag == true){// 한번만 찍어서 결과 보여주면 된다

                            // registeredPersonFlag == true
                            intentToCameraActivity();

                        }else{// 이름 등록하고 10번 찍어서 메뉴판 보여주기  registeredPersonFlag == false
                            // 대화상자 출력한다
                            new AlertDialog.Builder(MainActivity.this)
                                    .setTitle("이름 등록")
                                    .setView(alertView)
                                    //.setMessage("10번 사진 찍습니다!")
                                    .setPositiveButton("등록", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            // 가져온다
                                            name = editName.getText().toString();
                                            // 이름 받고 이름 토대로 사람 생성
                                            new AboutPerson.CreatePersonTask(name, MainActivity.this).execute("");

//                                            intentToCameraActivity();
/*
                                            new AlertDialog.Builder(MainActivity.this)
                                                    .setTitle("10번 사진 찍습니다!\n카메라에 얼굴이 나오게 해주세요!")
                                                    .setPositiveButton("네", new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int which) {

                                                            // 10번 스레드
                                                            // registeredPersonFlag == false
                                                            intentToCameraActivity();
                                                        }
                                                    }).show();
*/

                                        }
                                    }).show();

                        }
                    }
                }).setNegativeButton("아니요", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                    }
                }
        ).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        // 그룹이 없으면 자동으로 생성해줄거야
//        new AboutPersonGroup.GetPersonGroupTask(MainActivity.this);
//        new AboutPersonGroup.CreatePersonGroupTask(this).execute(AppSetting.personGroupId); // 그룹id 전달

//        Log.e("  Main group id", AppSetting.personGroupId);

        LayoutInflater inflater = (LayoutInflater) getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        alertView = inflater.inflate(R.layout.alertview_name, null); // 이게 되려나 ?

        editName = (EditText) alertView.findViewById(R.id.edit_name);

        if(AppSetting.camefromCamera != true){
            // 시나리오: Main > Camera > Main
            // 자칫하면 무한루프가 발생할 수 있으므로 체크한다

            // 대화상자 출력한다
            showRegisterDialog(); // 얼굴 등록했는지 -> 얼굴 인식할건지
            AppSetting.camefromCamera = false; // 사용하고 초기화
        }



/*
        // 타 추천 클릭하면 화면 이동
        binding.btnOtherReco.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RegisterMenuActivity.class);
                startActivity(intent);
            }
        });
        */

        // 메뉴판 클릭하면 화면 이동
        binding.btnMenuList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MenuListActivity.class);
                startActivity(intent);
            }
        });

        // 주문기록 클릭하면 화면 이동
        binding.btnOrderedList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, OrderedListActivity.class);
                startActivity(intent);
            }
        });
    }//end onCreate
}//end Activity
