package com.runningmusic.adapter;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.runningmusic.music.IPlayList;
import com.runningmusic.runninspire.R;
import com.runningmusic.utils.Log;
import com.runningmusic.utils.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by GuDong on 3/18/16 18:45.
 * Contact with gudong.name@gmail.com.
 */
public class RunningMusicAdapter extends RecyclerView.Adapter<RunningMusicAdapter.BaseViewHolder> {
    //热门推荐
    public static final int TYPE_HR = 0;
    //本地音乐
    public static final int TYPE_LM = 1;
    //跑步歌单
    public static final int TYPE_PL = 1;

    private List<List<IPlayList>> dataSource = new ArrayList<>(2);

    private Map<Integer, BaseViewHolder> holderMap = new HashMap<>();

    private static PGCItemClickListener itemClickListener;

    public RunningMusicAdapter() {
        //手动设置初始容量有问题 通过 add 可避免
        dataSource.add(new ArrayList<IPlayList>());
//        dataSource.add(new ArrayList<Music>());
        dataSource.add(new ArrayList<IPlayList>());
    }

    public RunningMusicAdapter(List<List<IPlayList>> dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public int getItemViewType(int position) {
        Log.i("----","getItemViewType is "+position);
        return position ;
    }

    @Override
    public RunningMusicAdapter.BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycle_contain, null);
        BaseViewHolder holder = null;

        switch (viewType) {
            case TYPE_HR:
                holder = new HRViewHolder(view);
                break;
//            case TYPE_LM:
//                holder = new LMViewHolder(view);
//                break;
            case TYPE_PL:
                holder = new PLViewHolder(view);
                break;
        }

        holderMap.put(viewType, holder);
        return holder;
    }


    @Override
    public void onBindViewHolder(RunningMusicAdapter.BaseViewHolder holder, int position) {
        List<IPlayList> data = dataSource.get(position);
        if (data != null && holder!=null)  {
            holder.updateDataList(data);
        }
    }

    @Override
    public int getItemCount() {
        Log.i("----", "count is " + dataSource.size());
        return dataSource.size();
    }

    public void updateByType(int type, List<IPlayList> list) {
        dataSource.remove(type);
        dataSource.add(type,list);
        notifyDataSetChanged();
        BaseViewHolder holder = holderMap.get(type);
        if (holder != null) {
            holderMap.get(type).updateDataList(list);
        } else {
            Log.e("----", "type is " + type + " holder is null");
        }
    }

    public void update(List<List<IPlayList>> dataSource) {
        this.dataSource = dataSource;
        notifyDataSetChanged();
    }

    class HRViewHolder extends BaseViewHolder {

        @Override
        RecyclerView.LayoutManager initLayoutManage() {
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
            linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            return linearLayoutManager;
        }

        @Override
        String initTitle() {
            return "热门推荐";
        }

        public HRViewHolder(View itemView) {
            super(itemView);
        }
    }

    class LMViewHolder extends BaseViewHolder {

        @Override
        RecyclerView.LayoutManager initLayoutManage() {
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
            linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            return linearLayoutManager;
        }

        @Override
        String initTitle() {
            return "本地音乐";
        }

        public LMViewHolder(View itemView) {
            super(itemView);
        }
    }

    class PLViewHolder extends BaseViewHolder {

        public PLViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        RecyclerView.LayoutManager initLayoutManage() {
            return new GridLayoutManager(mContext, 2);
        }

        @Override
        String initTitle() {
            return "跑步歌单";
        }
    }

    abstract class BaseViewHolder extends RecyclerView.ViewHolder {
        protected RecyclerView mRecyclerView;
        protected Context mContext;
        protected MusicListAdapter mAdapter;
        protected TextView mTvTitle;
        protected View mViewLine;

        private int viewType = -1;

        public void updateDataList(List<IPlayList> musicList) {
            mAdapter.update(musicList);
        }

        abstract RecyclerView.LayoutManager initLayoutManage();

        abstract String initTitle();

        public BaseViewHolder(View itemView) {
            super(itemView);
            mContext = itemView.getContext();

            mTvTitle = (TextView) itemView.findViewById(R.id.tv_type_title);
            mViewLine =  itemView.findViewById(R.id.view_line);
            mTvTitle.setText(initTitle());
            mRecyclerView = (RecyclerView) itemView.findViewById(R.id.list_item);
            mRecyclerView.setLayoutManager(initLayoutManage());

            mAdapter = new MusicListAdapter(mContext, R.layout.grid_item);
            mRecyclerView.setAdapter(mAdapter);
            if (itemClickListener != null) {
                mAdapter.setOnItemClickListener(itemClickListener);
            }

            mRecyclerView.getLayoutParams().width = Util.getScreenWidth();
        }

    }

    public void setOnItemClickListener(PGCItemClickListener listener) {
        itemClickListener = listener;
    }
}

