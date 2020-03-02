package com.heaton.baselib.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.PorterDuff;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.util.Log;

import com.heaton.baselib.R;

/**
 * 点击变暗效果的图片
 */
public class ClickableImageView extends AppCompatImageView {

    public final float[] BG_PRESSED =  new float[] { 1, 0, 0, 0, -50, 0, 1,
            0, 0, -50, 0, 0, 1, 0, -50, 0, 0, 0, 1, 0 };
    public final float[]  BG_NO_PRESSED = new float[] { 1, 0, 0, 0, 0, 0,
            1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0 };

    private float pressedAlpha;
    private ColorStateList mColor;

    public ClickableImageView(Context context) {
        super(context);
    }

    public ClickableImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setClickable(true);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ClickableImageView);
        pressedAlpha = typedArray.getFloat(R.styleable.ClickableImageView_pressed_alpha, 0.5f);

        mColor = typedArray.getColorStateList(R.styleable.ClickableImageView_pressed_color);

        typedArray.recycle();
    }

    public ClickableImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setPressed(boolean pressed) {
        updateView(pressed);
        super.setPressed(pressed);
    }

    private void updateView(boolean pressed){
        if( pressed ){//点击
            //通过设置滤镜来改变图片亮度
            this.setDrawingCacheEnabled(true);
            if (mColor != null){
                final int[] drawableState = getDrawableState();
                int color = mColor.getColorForState(drawableState, 0);
                this.getDrawable().setColorFilter(color, PorterDuff.Mode.MULTIPLY);
            }else {
                ColorMatrix colorMatrix = new ColorMatrix();
                colorMatrix.setSaturation(pressedAlpha);
                this.setColorFilter(new ColorMatrixColorFilter(colorMatrix)) ;
                this.getDrawable().setColorFilter(new ColorMatrixColorFilter(colorMatrix));
            }
        }else{//未点击
            this.setColorFilter(new ColorMatrixColorFilter(BG_NO_PRESSED)) ;
            this.getDrawable().setColorFilter(new ColorMatrixColorFilter(BG_NO_PRESSED));
        }
    }

}
