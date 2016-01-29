package com.getiton.android.app.core.collections;

import org.jetbrains.annotations.Nullable;

/**
 * Created by Christophe on 19/05/2014.
 */
public interface IQueryable<M> {

    @Nullable
    public M getItem(int position);
    public int getCount();
    public long getItemId(int position);
}
