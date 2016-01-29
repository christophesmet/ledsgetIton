package com.getiton.android.app.core.collections;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * Created by Christophe on 19/05/2014.
 */

public class ArrayQueryable<M> implements IQueryable<M> {

    @NotNull
    private ArrayList<M> mArrayListItems;

    public ArrayQueryable(@NotNull ArrayList<M> arrayListItems) {
        mArrayListItems = arrayListItems;
    }

    @Override
    public M getItem(int position) {
        return mArrayListItems.get(position);
    }

    @Override
    public int getCount() {
        return mArrayListItems.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}
