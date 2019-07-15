package com.example.kiosk_jnsy;
// 지연 : 메뉴등록을 할때는 주석된 것을 풀어야 함. 또한 xml에서도 수정이 필요함.

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.kiosk_jnsy.model.CafeItem; // package model : CafeItem
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import static java.lang.String.valueOf;

// 이미지 업로드 https://www.youtube.com/watch?v=6u0gzjth4IE
public class MenuListActivity extends AppCompatActivity {


    List<CafeItem> citems; // 메뉴판 객체들을 담은 arraylist
    CafeItemAdapter adapter;
    public Uri imguri;
    Button ch,up,down; // 메뉴 사진 등록을 위한 변수.
    ImageView img;
    StorageReference mStorageRef;
    StorageReference ref;// 다운로드 시도
    private StorageTask uploadTask; //중복 방지
    Button button;
    int cnt=0;

    // 이미지 다운로드 하기
    public void download(){

    }
    // 앨범에서 파일 고르기
    private void fileChooser(){
        Intent intent=new Intent();
        intent.setType("image/'");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,1);
    }


    private String getExtension(Uri uri){
        ContentResolver cr=getContentResolver();
        MimeTypeMap mimeTypeMap=MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(cr.getType(uri));
    }
    // 이미지를 파이어베이스에 올림
    private void fileUploader(){
        StorageReference Ref=mStorageRef.child(System.currentTimeMillis()+","+getExtension(imguri));
        uploadTask=Ref.putFile(imguri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        // 성공하면
                        Toast.makeText(MenuListActivity.this,"Image Uproad Successfultty",Toast.LENGTH_LONG).show();

                        Log.d("백지연",taskSnapshot.getMetadata().getReference().getDownloadUrl()+"");
                        taskSnapshot.getMetadata().getReference().getDownloadUrl()
                                .addOnCompleteListener(MenuListActivity.this,
                                        new OnCompleteListener<Uri>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Uri> task) {
                                                if (task.isSuccessful()) {
                                                    CafeItem ci = new CafeItem("변경필요", 5000, task.getResult().toString(),"상세설명");
                                                    //mFirebaseDatabaseReference.child(MESSAGES_CHILD).child(key).setValue(friendlyMessage);
                                                    FirebaseDatabase.getInstance().getReference().push().setValue(ci);
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
    @Override
    protected void onActivityResult(int requestCode,int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if(requestCode==1&&resultCode==RESULT_OK&&data!=null&&data.getData()!=null){
            imguri=data.getData();
            img.setImageURI(imguri);
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_list);
        citems=new ArrayList<CafeItem>();

        // storage
        mStorageRef= FirebaseStorage.getInstance().getReference("Image");

     /*
        ch=(Button)findViewById(R.id.choosebtn);
        up=(Button)findViewById(R.id.uploadbtn);
        down=(Button)findViewById(R.id.download);
        img=(ImageView)findViewById(R.id.imageview);
        ch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              fileChooser();
            }
        });
        up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(uploadTask!=null&&uploadTask.isInProgress()){
                    // 파일이 올라가는 중임을 암시
                    Toast.makeText(MenuListActivity.this,"upload in progress",Toast.LENGTH_LONG).show();
                }else{
                    fileUploader();
                }
            }
        });
        down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               download();
            }
        });


        // 데이터베이스 쓰기
        button = (Button) findViewById(R.id.btn1);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("MainActivity", "Button - onClickListener");
                //FirebaseDatabase.getInstance().getReference().push().setValue(new CafeItem("바닐라 라떼",3000,"url3"));
            }
        });// 버튼 이벤트 리스너

*/

        // 리사이클러뷰


        RecyclerView recyclerView=findViewById(R.id.recyclerView);
        //어댑터 만들기
        adapter=new CafeItemAdapter(new CafeItemAdapter.OnCafeItemClickListener() {
            @Override
            public void onCafeItemClicked(CafeItem model) {
                //Toast.makeText(MenuListActivity.this, model.getName()+" 상세정보 보기", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MenuListActivity.this, DetailMenuItemActivity.class);
                intent.putExtra("detail",model);
                startActivity(intent);

            }
        });
        recyclerView.setAdapter(adapter);
        Log.d("현재",citems.size()+"");


        // 데이터베이스 읽기 #2. Single ValueEventListener
        FirebaseDatabase.getInstance().getReference().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Log.d("MainActivity", "Single ValueEventListener : " + snapshot.getValue());
                    Log.d("데이터",valueOf(snapshot)+"형");
                    String name = (String) snapshot.child("name").getValue();
                    Long pricen = (Long) snapshot.child("price").getValue();
                    String body=(String)snapshot.child("body").getValue();
                    int price=pricen.intValue();
                    String imageUrl=(String)snapshot.child("imageUrl").getValue();

                    // 객체 형태로 받아와야 함. 오류...
                    //CafeItem ciObject = dataSnapshot.getValue(CafeItem.class);
                    citems.add(new CafeItem(name,price,imageUrl,body));
                    Log.d("현재 들어감",citems.size()+"");

                    /*
                    if(ciObject!=null) {

                        Log.d("데이터1",ciObject.getName());
                        Log.d("데이터2", message+"이다");
                       // Log.d("데이터3", ciObject.getPrice() + "이다");
                    }
                    */

                }
                // for문 다 수행 후 어댑터 설정
                adapter.setItems(citems);


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        // 파이어베이스 입출력 : 출처: https://stack07142.tistory.com/282 [Hello World]


    }// onCreate()
    // 리사이클러뷰 어댑터 클래스
    private static class CafeItemAdapter extends RecyclerView.Adapter<CafeItemAdapter.CafeItemViewHolder> {
        interface OnCafeItemClickListener {
            void onCafeItemClicked(CafeItem model);
        }

        private OnCafeItemClickListener mListener;

        private List<CafeItem> mItems = new ArrayList<>();

        public CafeItemAdapter() {}

        public CafeItemAdapter(OnCafeItemClickListener listener) {
            mListener = listener;
        }

        public void setItems(List<CafeItem> items) {
            this.mItems = items;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public CafeItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_cafeitem, parent, false);
            final CafeItemViewHolder viewHolder = new CafeItemViewHolder(view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        final CafeItem item = mItems.get(viewHolder.getAdapterPosition());
                        mListener.onCafeItemClicked(item);
                    }
                }
            });
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull CafeItemViewHolder holder, int position) {
            CafeItem item = mItems.get(position);
            // TODO : 데이터를 뷰홀더에 표시하시오
            holder.name.setText(item.getName());
            holder.price.setText(item.getPrice()+"");
            // holder.cafe_imageview.setImageResource();
            Glide.with(holder.cafe_imageview.getContext())
                    .load(item.getImageUrl())
                    .into(holder.cafe_imageview);
            //String imageUrl = friendlyMessage.getImageUrl();
        }

        @Override
        public int getItemCount() {
            return mItems.size();
        }

        public static class CafeItemViewHolder extends RecyclerView.ViewHolder {
            // TODO : 뷰홀더 완성하시오
            TextView name;
            TextView price;
            ImageView cafe_imageview;

            public CafeItemViewHolder(@NonNull View itemView) {
                super(itemView);
                // TODO : 뷰홀더 완성하시오
                name=itemView.findViewById(R.id.name_text);
                price=itemView.findViewById(R.id.age_text);
                cafe_imageview=itemView.findViewById(R.id.cafe_imageview);
            }
        }
    }//CafeItemAdapter 클래스

}
