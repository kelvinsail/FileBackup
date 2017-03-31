package com.yifan.utils.base.widget;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.yifan.utils.widget.LoadingFooter;

/**
 * BaseRecyclerAdapter 支持Header & Footer的RecyclerView.Adapter
 *
 * Created by yifan on 2016/8/25.
 */
public abstract class BaseRecyclerAdapter<VH extends BaseRecyclerHolder>
        extends RecyclerView.Adapter<VH> implements View.OnClickListener, View.OnLongClickListener,CompoundButton.OnCheckedChangeListener {

    public static final String TAG = "BaseRecyclerAdapter";

    /**
     * 默认视图类型
     */
    public static final int ITEM_TYPE_NORMAL = 0;
    /**
     * 头部视图类型
     */
    public static final int ITEM_TYPE_HEADER = -1;
    /**
     * 尾部视图类型
     */
    public static final int ITEM_TYPE_FOOTER = -2;

    /**
     * 绑定的RecyclerView
     */
    public RecyclerView mRecyclerView;

    /**
     * 底部加载状态
     */
    public enum LoadingState {

        /**
         * 隐藏
         */
        none,
        /**
         * 正在加载
         */
        loading,
        /**
         * 加载失败
         */
        loadError,
        /**
         * 加载结束，没有更多数据
         */
        loadEnd,
        /**
         * 自定义文本样式
         */
        custom;

    }

    /**
     * 头部View
     */
    private View mHeaderView;

    /**
     * 尾部View
     */
    private View mFooterView;

    /**
     * 列表点击事件监听
     */
    private OnItemClickListener mListener;

    /**
     * 列表项长按事件监听
     */
    private OnItemLongClickListener mOnItemLongClickListener;

    /**
     * 列表项选中事件监听
     */
    private OnItemCheckedListener mOnItemCheckedListener;

    /**
     * 设置列表项点击事件监听
     *
     * @param listener
     */
    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    /**
     * 设置列表项长按事件监听
     *
     * @param onItemLongClickListener
     */
    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        this.mOnItemLongClickListener = onItemLongClickListener;
    }

    public OnItemLongClickListener getOnItemLongClickListener() {
        return mOnItemLongClickListener;
    }

    /**
     * 设置条目选中事件监听
     *
     * @param onItemCheckedListener
     */
    public void setOnItemCheckedListener(OnItemCheckedListener onItemCheckedListener) {
        this.mOnItemCheckedListener = onItemCheckedListener;
    }

    /**
     * 获取列表项点击事件监听
     *
     * @return
     */
    public OnItemClickListener getOnItemClickListener() {
        return mListener;
    }

    /**
     * 获取列表项选中事件监听器
     *
     * @return
     */
    public OnItemCheckedListener getOnItemCheckedListener() {
        return mOnItemCheckedListener;
    }

    /**
     * 添加头部控件或布局
     */
    public void setHeaderView(View headerView) {
        mHeaderView = headerView;
        notifyItemInserted(0);
    }

    /**
     * 添加尾部控件或布局
     */
    public void setFooterView(View footerView) {
        mFooterView = footerView;
        notifyItemInserted(0);
    }

    /**
     * 移除头部控件或布局
     */
    public void removeHeaderView() {
        if (null != mHeaderView) {
            mHeaderView = null;
            notifyItemRemoved(0);
        }
    }

    /**
     * 移除尾部控件或布局
     */
    public void removeFooterView() {
        if (null != mFooterView) {
            mFooterView = null;
            notifyItemRemoved(0);
        }
    }

    /**
     * 添加加载更多底部
     *
     * @param context
     */
    public void setLoadMoreFooter(Context context, View.OnClickListener listener) {
        setFooterView(newLoadingFooter(context));
        mFooterView.setOnClickListener(listener);
    }

    /**
     * 移除加载更多底部
     */
    public void removeLoadMoreFooter() {
        if (null != mFooterView && mFooterView instanceof LoadingFooter) {
            removeFooterView();
        }
    }

    /**
     * 获取一个新的底部加载更多布局（未添加到Adapter中）
     *
     * @param context
     * @return
     */
    public LoadingFooter newLoadingFooter(Context context) {
        LoadingFooter view = new LoadingFooter(context);
        return view;
    }

    /**
     * 设置底部加载更多布局样式
     *
     * @param state
     */
    public void setLoadingFooterState(LoadingState state, String text) {
        if (null != mFooterView && mFooterView instanceof LoadingFooter) {
            ((LoadingFooter) mFooterView).setState(state, text);
        }
    }

    /**
     * 获取头部控件或布局
     *
     * @return
     */
    public View getHeaderView() {
        return mHeaderView;
    }

    /**
     * 获取尾部控件或布局
     *
     * @return
     */
    public View getFooterView() {
        return mFooterView;
    }

    /**
     * 判断是否头部或底部自定义控件
     *
     * @param position
     * @return
     */
    public boolean isHeaderOrFooter(int position) {
        if (null != mHeaderView && position == 0) {
            return true;
        } else if (null != mFooterView && position == getItemCount() - 1) {
            return true;
        }
        return false;
    }

    /**
     * 判断是否头部自定义控件
     *
     * @param position
     * @return
     */
    public boolean isHeader(int position) {
        if (null != mHeaderView && position == 0) {
            return true;
        }
        return false;
    }

    /**
     * 判断是否底部自定义控件
     *
     * @param position
     * @return
     */
    public boolean isFooter(int position) {
        if (null != mFooterView && position == getItemCount() - 1) {
            return true;
        }
        return false;
    }

    /**
     * 判断是否添加了头部自定义控件
     *
     * @return
     */
    public boolean hasHeader() {
        if (null != mHeaderView) {
            return true;
        }
        return false;
    }

    /**
     * 判断是否添加了底部自定义控件
     *
     * @return
     */
    public boolean hasFooter() {
        if (null != mFooterView) {
            return true;
        }
        return false;
    }

    @Override
    public int getItemViewType(int position) {
        if (null != mHeaderView && position == 0) return ITEM_TYPE_HEADER;
        if (null != mFooterView && position == getItemCount() - 1) return ITEM_TYPE_FOOTER;
        return getRealItemType(position);
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        if (null != mHeaderView && viewType == ITEM_TYPE_HEADER) return getFakeHolder(mHeaderView);
        if (null != mFooterView && viewType == ITEM_TYPE_FOOTER) return getFakeHolder(mFooterView);
        return onCreate(parent, viewType);
    }

    @Override
    public void onBindViewHolder(VH viewHolder, int position) {
        int type = getItemViewType(position);
        if (type == ITEM_TYPE_HEADER) return;
        if (type == ITEM_TYPE_FOOTER) return;
        int pos = getRealPosition(viewHolder);
        onBind((VH) viewHolder, pos);
        if (null != viewHolder) {
            viewHolder.setPosition(position);
            viewHolder.itemView.setOnClickListener(this);
            viewHolder.itemView.setOnLongClickListener(this);
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        setRecyclerView(recyclerView);
        RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
        if (manager instanceof GridLayoutManager) {
            final GridLayoutManager gridManager = ((GridLayoutManager) manager);
            gridManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    int type = getItemViewType(position);
                    return type == ITEM_TYPE_HEADER || type == ITEM_TYPE_FOOTER
                            ? gridManager.getSpanCount() : 1;
                }
            });
        }
    }

    @Override
    public void onViewAttachedToWindow(VH holder) {
        super.onViewAttachedToWindow(holder);
        ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
        if (null != lp && lp instanceof StaggeredGridLayoutManager.LayoutParams
                && holder.getLayoutPosition() == 0) {
            StaggeredGridLayoutManager.LayoutParams p = (StaggeredGridLayoutManager.LayoutParams) lp;
            p.setFullSpan(true);
        }
    }

    /**
     * 获取除去header&footer以外的真实序号
     *
     * @param holder
     * @return
     */
    public int getRealPosition(RecyclerView.ViewHolder holder) {
        int position = holder.getLayoutPosition();
        if (null != mFooterView && position == getItemCount() - 1) {
            return position - 2;
        }
        return mHeaderView == null ? position : position - 1;
    }

    @Override
    public int getItemCount() {
        int i = getRealItemCount();
        if (null != mHeaderView) i++;
        if (null != mFooterView) i++;
        return i;
    }

    @Override
    public void onClick(View view) {
        if (null != view && null != mListener) {
            int position = BaseRecyclerHolder.getPositionFromView(view);
            if (position >= 0) {
                mListener.onItemClick(view, getItemViewType(position), position);
            }
        }
    }

    @Override
    public boolean onLongClick(View view) {
        if (null != view && null != mOnItemLongClickListener) {
            int position = BaseRecyclerHolder.getPositionFromView(view);
            if (position >= 0) {
                return mOnItemLongClickListener.onItemLongClick(view, getItemViewType(position), position);
            }
        }
        return false;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (null != getOnItemCheckedListener()) {
            int position = BaseRecyclerHolder.getPositionFromView(buttonView);
            if (position >= 0) {
                getOnItemCheckedListener().onItemChecked(buttonView, isChecked, position);
            }
        }
    }

    /**
     * 创建ViewHolder
     *
     * @param parent
     * @param viewType
     * @return
     */
    public abstract VH onCreate(ViewGroup parent, int viewType);

    /**
     * 绑定并展示数据
     *
     * @param viewHolder
     * @param realPosition
     */
    public abstract void onBind(VH viewHolder, int realPosition);

    /**
     * 获取数据的数量
     *
     * @return
     */
    public abstract int getRealItemCount();

    /**
     * 获取数据的数量
     *
     * @return
     */
    public int getRealItemType(int position) {
        return ITEM_TYPE_NORMAL;
    }

    /**
     * 创建header或footer的替代ViewHolder
     *
     * @param view
     * @return
     */
    public abstract VH getFakeHolder(View view);

    /**
     * 列表点击事件监听
     */
    public interface OnItemClickListener {
        void onItemClick(View view, int itemType, int position);
    }

    /**
     * 列表点击事件监听接口
     */
    public interface OnItemLongClickListener {
        boolean onItemLongClick(View v, int itemType, int position);
    }

    /**
     * 条目选中事件监听
     */
    public interface OnItemCheckedListener {

        /**
         * 条目选中事件回调
         *
         * @param view      点击的控件
         * @param isChecked 是否选中
         * @param position  条目序号
         */
        void onItemChecked(View view, boolean isChecked, int position);

    }

    /**
     * 设置所绑定的RecyclerView
     *
     * @param recyclerView
     */
    public void setRecyclerView(RecyclerView recyclerView) {
        this.mRecyclerView = recyclerView;
    }

    /**
     * 获取绑定的RecyclerView
     *
     * @return
     */
    public RecyclerView getRecyclerView() {
        return mRecyclerView;
    }
}