package com.example.ceo;


import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ceo.model.CafeItem;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

public class RegisterMenuActivity extends AppCompatActivity {

    private TextView menu_name, menu_price;
    private EditText input_name, input_price;
    private ImageView imageview;
    private Button upload_btn, regi_btn;
    private final int GET_GALLERY_IMAGE = 200;
    private StorageTask uploadTask; // 지연 : 중복 방지
    StorageReference mStorageRef; // 지연 : 파이어베이스
    public Uri selectedImageUri; // 지연 : 이미지 uri
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_menu);

        menu_name=(TextView)findViewById(R.id.menu_tv);
        menu_price=(TextView)findViewById(R.id.price_tv);

        input_name=(EditText) findViewById(R.id.menu_edt);
        input_price=(EditText) findViewById(R.id.price_edt);

        imageview=(ImageView)findViewById(R.id.image_iv);

        upload_btn=(Button)findViewById(R.id.image_btn);
        regi_btn=(Button)findViewById(R.id.register_btn); // 등록버튼

        // 지연 : storage
        mStorageRef= FirebaseStorage.getInstance().getReference("Image");

        // 이미지뷰 클릭하면 인텐트를 이용하여 갤러리로 넘어간다.
        imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent. setDataAndType(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent, GET_GALLERY_IMAGE);
            }
        });

        // 지연 : 등록버튼을 클릭하면 파이어베이스에 가격과 이름, 이미지가 저장된다.
        regi_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(uploadTask!=null&&uploadTask.isInProgress()){
                    // 파일이 올라가는 중임을 암시
                    Toast.makeText(RegisterMenuActivity.this,"upload in progress",Toast.LENGTH_LONG).show();
                }else{
                    fileUploader();
                }

                startActivity(new Intent(RegisterMenuActivity.this, MainActivity.class));

            }
        });


    }// onCreate

    // 갤러리에서 사진 선택해서 이미지뷰에 띄우는 코드
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == GET_GALLERY_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            imageview.setImageURI(selectedImageUri);

        }
    }

    // 지연 : 이미지를 파이어베이스에 올림
    private void fileUploader(){
        StorageReference Ref=mStorageRef.child(System.currentTimeMillis()+","+getExtension(selectedImageUri));
        uploadTask=Ref.putFile(selectedImageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        // 성공하면
                        Toast.makeText(RegisterMenuActivity.this,"Image Uproad Successfultty",Toast.LENGTH_LONG).show();

                        Log.d("백지연",taskSnapshot.getMetadata().getReference().getDownloadUrl()+"");
                        taskSnapshot.getMetadata().getReference().getDownloadUrl()
                                .addOnCompleteListener(RegisterMenuActivity.this,
                                        new OnCompleteListener<Uri>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Uri> task) {
                                                if (task.isSuccessful()) {
                                                    String name=input_name.getText().toString();
                                                    int price=Integer.parseInt(input_price.getText().toString());
                                                    CafeItem ci = new CafeItem(name, price, task.getResult().toString(),"상세설명");
                                                    //mFirebaseDatabaseReference.child(MESSAGES_CHILD).child(key).setValue(friendlyMessage);
                                                    //FirebaseDatabase.getInstance().getReference().child("menu").push().setValue(ci);
                                                    FirebaseFirestore.getInstance().collection("cre_menu").add(ci);
                                                }
                                            }
                                        });
                    }

                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        // ...
                    }
                });
    }
    // 지연 : 추가
    private String getExtension(Uri uri){
        ContentResolver cr=getContentResolver();
        MimeTypeMap mimeTypeMap=MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(cr.getType(uri));
    }
}