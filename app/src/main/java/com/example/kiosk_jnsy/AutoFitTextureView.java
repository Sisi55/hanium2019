package com.example.kiosk_jnsy;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.TextureView;

public class AutoFitTextureView extends TextureView {

    private int mRatioWidth = 0;
    private int mRatioHeight = 0;

    // 생성자
    public AutoFitTextureView(Context context) {
        this(context, null);
    }

    public AutoFitTextureView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AutoFitTextureView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) { // 자동으로 측정된 크기 전달해주나
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        // 측정된 폭과 높이를 출력해 보자
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        Log.e("   측정된 크기 :  ",width+"  "+height);

        int ratio = 1;
        // set measured dimension 하는 단계. 여기만 바꾸면 해상도도 알아서 설정해준다
        if (0 == mRatioWidth || 0 == mRatioHeight) { // 멤버변수 초기화된 상태
            setMeasuredDimension(width/ratio, height/ratio);
            // 매개 기준으로 비율 맞춘다

        } else {
            if (width < height * mRatioWidth / mRatioHeight) { // 이 수식은 무슨 의미일까
                setMeasuredDimension(width/ratio, width * mRatioHeight / mRatioWidth/ratio);
                // 가로에 대해 비율 맞춘다

            } else {
                setMeasuredDimension(height * mRatioWidth / mRatioHeight/ratio, height/ratio);
                // 세로에 대해 비율을 맞춘다
            }
        }
        //end if else
    }
    //end method

    // 매개로 멤버를 바꾸는 함수
    public void setAspectRatio(int width, int height) {

        if (width < 0 || height < 0) { // 매개 유효성 검사
            throw new IllegalArgumentException("Size cannot be negative.");
        }//end if

        mRatioWidth = width;
        mRatioHeight = height;

        requestLayout(); // ?
    }
    //end method

}//end class
