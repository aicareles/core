package com.heaton.baselibsample.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.heaton.baselib.base.recycleview.RecyclerAdapter;
import com.heaton.baselib.base.recycleview.RecyclerViewHolder;
import com.heaton.baselibsample.R;

import java.util.List;

public class LanguageAdapter extends RecyclerAdapter<String> {

    private int mPosition;

    public LanguageAdapter(Context context, List<String> datas) {
        super(context, datas);
    }

    public void setPosition(int position){
        mPosition = position;
    }

    @Override
    public int layoutId() {
        return R.layout.item_language;
    }

    @Override
    public void convert(RecyclerViewHolder holder, String language) {
        TextView tvLanguage = holder.getView(R.id.tv_language);
        ImageView ivCheck = holder.getView(R.id.iv_check);

        tvLanguage.setText(language);
        if (holder.getAdapterPosition() == mPosition){
            ivCheck.setVisibility(View.VISIBLE);
        }else {
            ivCheck.setVisibility(View.INVISIBLE);
        }
    }
}
