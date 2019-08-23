package com.heaton.baselib.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.heaton.baselib.R;

import java.util.ArrayList;

/**
 * description $desc$
 * created by jerry on 2019/7/17.
 */
public class BannerView extends LinearLayout {
    private ArrayList<ImageView> tips = new ArrayList<>();//标记点集合
    private Drawable drawableFocus;
    private Drawable drawableUnfocus;

    public BannerView(Context context) {
        this(context, null,0);
    }

    public BannerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public BannerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.BannerView);
        drawableFocus = array.getDrawable(R.styleable.BannerView_focus_drawable);
        drawableUnfocus = array.getDrawable(R.styleable.BannerView_unfoucs_drawable);
        array.recycle();
    }

    public void addBannerDot(){
        ImageView imageView = new ImageView(getContext());
        imageView.setLayoutParams(new ViewGroup.LayoutParams(10, 10));
        tips.add(imageView);
        imageView.setImageDrawable(drawableFocus);
        LayoutParams layoutParams = new LayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        layoutParams.leftMargin = 15;
        layoutParams.rightMargin = 15;
        layoutParams.bottomMargin = 20;
        addView(imageView, layoutParams);
    }

    public void removeBannerDot(int position){
        if (tips.size() > position){
            tips.remove(position);
        }
        if (getChildCount() > position){
            removeViewAt(position);
        }
    }

    public void setFocusDot(int position){
        for (int i = 0; i < tips.size(); i++) {
            if (i == position) {
                tips.get(i).setImageDrawable(drawableFocus);
            } else {
                tips.get(i).setImageDrawable(drawableUnfocus);
            }
        }
    }

    public int getDotSize(){
        return tips.size();
    }
}
