package com.heaton.baselib.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.heaton.baselib.R;

/**
 * author: jerry
 * date: 20-11-16
 * email: superliu0911@gmail.com
 * des: 公共的头部封装
 */
class TitlebarView extends RelativeLayout {

    private OnClick onClick;

    public TitlebarView(Context context) {
        this(context, null);
    }

    public TitlebarView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TitlebarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context,attrs);
    }

    private void init(Context context,AttributeSet attrs) {
//        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.TitlebarView, 0, 0);
//        ColorStateList centerTextColor = typedArray.getColorStateList(R.styleable.TitlebarView_centerTextColor);
//        ColorStateList rightTextColor = typedArray.getColorStateList(R.styleable.TitlebarView_rightTextColor);
    }

    public void setOnClick(OnClick onClick) {
        this.onClick = onClick;
    }

    public interface OnClick {
        void leftClick();
        void rightClick();
    }

}
