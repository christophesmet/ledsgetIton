package com.getiton.android.app.core.bindable;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.getiton.android.app.core.collections.IQueryable;


/**
 * Created by christophesmet on 14/10/15.
 */

public class BindableRecyclerViewAdapter<M> extends RecyclerView.Adapter {

    @NonNull
    protected Context mContext;
    @NonNull
    protected IQueryable<M> mCollection;
    private LayoutInflater mInflater;
    protected int mDefaultLayoutId;

    @Nullable
    private ItemClickListener mItemClickListener;
    @Nullable
    private AdapterView.OnItemLongClickListener mOnItemLongClickListener;

    public BindableRecyclerViewAdapter(@NonNull Context context, @LayoutRes int defaultLayoutId, @NonNull IQueryable<M> collection) {
        mContext = context;
        mDefaultLayoutId = defaultLayoutId;
        mCollection = collection;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View v = onCreateView(viewType, mInflater, viewGroup);
        return new BindableViewHolder(v);
    }

    protected int getLayoutIdForViewType(int viewType) {
        return mDefaultLayoutId;
    }

    @NonNull
    protected View onCreateView(int viewType, @NonNull LayoutInflater inflater, @NonNull ViewGroup viewGroup) {
        return inflater.inflate(getLayoutIdForViewType(viewType), viewGroup, false);
    }

    private void fireItemClickListener(@NonNull View v, int position) {
        if (mItemClickListener != null) {
            mItemClickListener.onItemClicked(v, position);
        }
    }

    @Nullable
    protected M getItem(int position) {
        return mCollection.getItem(position);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int i) {
        M object = getItem(i);

        if (viewHolder.itemView instanceof IBindableView) {
            ((IBindableView) viewHolder.itemView).bind(object);
        } else if (viewHolder.itemView instanceof IBindableListItemView) {
            ((IBindableListItemView) viewHolder.itemView).bind(object, i);
        }

        if (mItemClickListener != null) {
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    fireItemClickListener(v, i);
                }
            });
        }
        viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return fireOnLongClickItemView(v, i);
            }
        });
    }

    private boolean fireOnLongClickItemView(@NonNull View v, int pos) {
        if (mOnItemLongClickListener != null) {
            return mOnItemLongClickListener.onItemLongClick(null, v, pos, -1);
        }
        return false;
    }

    public void setItemClickListener(@Nullable ItemClickListener itemClickListener) {
        this.mItemClickListener = itemClickListener;
        notifyDataSetChanged();
    }

    public void setItemLongClickListener(@NonNull AdapterView.OnItemLongClickListener longClickListener) {
        this.mOnItemLongClickListener = longClickListener;
    }

    @Override
    public int getItemCount() {
        return mCollection.getCount();
    }

    public void setCollection(@Nullable IQueryable<M> collection, boolean notifyChanged) {
        this.mCollection = collection;
        if (notifyChanged) {
            notifyDataSetChanged();
        }
    }

    @NonNull
    public IQueryable<M> getCollection() {
        return mCollection;
    }

    public static class BindableViewHolder extends RecyclerView.ViewHolder {
        public BindableViewHolder(View itemView) {
            super(itemView);
        }
    }

    public interface ItemClickListener<M> {
        void onItemClicked(@NonNull M view, int position);
    }
}