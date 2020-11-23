package com.heaton.baselib.base.recycleview;

import android.content.Context;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.heaton.baselib.utils.ViewUtils;

import java.util.LinkedHashSet;
import java.util.List;

/**
 * @author: liulei
 * @date: 2016-10-31 14:15
 * @GitHub: https://github.com/liulei-0911/BleDemo
 */
public abstract class RecyclerAdapter<T> extends RecyclerView.Adapter<RecyclerViewHolder> {
    protected Context mContext;
    protected int mLayoutId;
    protected List<T> mData;
    protected LayoutInflater mInflater;
    public int select = -1;//当前选中项

    private OnItemClickListener<T> mOnItemClickListener;
    private OnItemLongClickListener<T> mOnItemLongClickListener;
    private OnItemChildClickListener<T> mOnItemChildClickListener;
    private LinkedHashSet<Integer> childClickViewIds;

    public void setOnItemClickListener(OnItemClickListener<T> onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener<T> onItemLongClickListener) {
        this.mOnItemLongClickListener = onItemLongClickListener;
    }

    public void setOnItemChildClickListener(OnItemChildClickListener<T> onItemChildClickListener) {
        this.mOnItemChildClickListener = onItemChildClickListener;
    }

    public void bindChildClickViewIds(@IdRes int... viewIds){
        childClickViewIds = new LinkedHashSet<>();
        for (int id: viewIds) {
            childClickViewIds.add(id);
        }
    }

    public RecyclerAdapter(Context context, List<T> data, @LayoutRes int layoutId) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mLayoutId = layoutId;
        mData = data;
    }

    public RecyclerAdapter(Context context, List<T> data) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mLayoutId = layoutId();
        mData = data;
    }

    public int layoutId(){
        return 0;
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        RecyclerViewHolder viewHolder = RecyclerViewHolder.get(mContext, null, parent, mLayoutId, -1);
        setListener(parent, viewHolder, viewType);
        return viewHolder;
    }

    protected int getPosition(RecyclerViewHolder viewHolder) {
        return viewHolder.getAdapterPosition();
    }

    protected void setListener(final ViewGroup parent, final RecyclerViewHolder viewHolder, int viewType) {
        viewHolder.getConvertView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ViewUtils.filter(500))return;
                if (mOnItemClickListener != null) {
                    int position = getPosition(viewHolder);
                    mOnItemClickListener.onItemClick(parent, v, mData.get(position), position);
                }
            }
        });
        viewHolder.getConvertView().setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mOnItemLongClickListener != null) {
                    int position = getPosition(viewHolder);
                    return mOnItemLongClickListener.onItemLongClick(parent, v, mData.get(position), position);
                }
                return false;
            }
        });
        if (childClickViewIds != null && !childClickViewIds.isEmpty()){
            for (int id: childClickViewIds){
                View view = viewHolder.getView(id);
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (ViewUtils.filter(500))return;
                        if (mOnItemChildClickListener != null){
                            int position = getPosition(viewHolder);
                            mOnItemChildClickListener.onItemChildClick(parent, v, mData.get(position), position);
                        }
                    }
                });
            }
        }
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {
        holder.updatePosition(position);
        convert(holder, mData.get(position));
    }

    public abstract void convert(RecyclerViewHolder holder, T t);

    @Override
    public int getItemCount() {
        return mData.size() != 0 ? mData.size() : 0;
    }


    public interface OnItemClickListener<T> {
        void onItemClick(ViewGroup parent, View view, T t, int position);
    }

    public interface OnItemLongClickListener<T> {
        boolean onItemLongClick(ViewGroup parent, View view, T t, int position);
    }

    public interface OnItemChildClickListener<T> {
        void onItemChildClick(ViewGroup parent, View view, T t, int position);
    }

}