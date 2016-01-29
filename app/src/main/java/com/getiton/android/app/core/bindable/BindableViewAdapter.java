package com.getiton.android.app.core.bindable;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;


import com.getiton.android.app.core.collections.IQueryable;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by Christophe on 17/05/2014.
 */

public class BindableViewAdapter<M> extends BaseAdapter {

    private Context mContext;

    private LayoutInflater mLayoutInflater;

    private int mLayoutResourceId;

    private IQueryable<M> mData;

    public BindableViewAdapter(@NotNull Context context, int listitemResourceId) {
        this.mContext = context;
        this.mLayoutResourceId = listitemResourceId;
        init();
    }

    public BindableViewAdapter(@NotNull Context context, int listitemResourceId, IQueryable<M> data) {
        this.mContext = context;
        this.mLayoutResourceId = listitemResourceId;
        this.mData = data;
        init();
    }

    private void init() {
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    public void setCollection(@NotNull IQueryable<M> collection) {
        this.mData = collection;
        notifyDataSetChanged();
    }

    @Override
    public M getItem(int i) {
        return mData.getItem(i);
    }

    @Override
    public long getItemId(int i) {
        return mData.getItemId(i);
    }

    @Override
    public int getCount() {
        if (mData != null) {
            return mData.getCount();
        }
        return 0;
    }

    @Nullable
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = mLayoutInflater.inflate(mLayoutResourceId, viewGroup, false);
        }
        if (view instanceof IBindableListItemView) {
            ((IBindableListItemView) view).bind(getItem(i), i);
        } else if (view instanceof IBindableView) {
            ((IBindableView) view).bind(getItem(i));
        }
        return view;
    }
}