package com.heaton.baselib.widget;

import android.content.Context;
import android.support.v7.widget.AppCompatEditText;
import android.util.AttributeSet;
import android.view.KeyEvent;

/**
 * author: jerry
 * date: 20-8-8
 * email: superliu0911@gmail.com
 * des: 带有软键盘监听的EditText
 */
public class KeyBoardEditText extends AppCompatEditText {
    private static final String TAG = "CustomEditTextView";

    public KeyBoardEditText(Context context) {
        super(context);
    }

    public KeyBoardEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public KeyBoardEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == 1) {
            super.onKeyPreIme(keyCode, event);
            onKeyBoardHideListener.onKeyHide(keyCode, event);
            return false;
        }
        return super.onKeyPreIme(keyCode, event);
    }

    /**
     *键盘监听接口
     */
    OnKeyBoardHideListener onKeyBoardHideListener;
    public void setOnKeyBoardHideListener(OnKeyBoardHideListener onKeyBoardHideListener) {
        this.onKeyBoardHideListener = onKeyBoardHideListener;
    }

    public interface OnKeyBoardHideListener{
        void onKeyHide(int keyCode, KeyEvent event);
    }
}
