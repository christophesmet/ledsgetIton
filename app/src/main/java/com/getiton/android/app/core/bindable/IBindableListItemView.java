package com.getiton.android.app.core.bindable;


import org.jetbrains.annotations.NotNull;

/**
 * Created by Christophe on 17/05/2014.
 */
public interface IBindableListItemView<M> {

    public void bind(@NotNull M model, int position);
}
