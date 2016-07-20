package com.runningmusic.fragment;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.runningmusic.activity.MusicListDetailActivity;
import com.runningmusic.adapter.GridAdapter;
import com.runningmusic.adapter.PGCItemClickListener;
import com.runningmusic.music.Music;
import com.runningmusic.music.PGCMusicList;
import com.runningmusic.runninspire.R;
import com.runningmusic.service.RunsicService;
import com.runningmusic.utils.Util;
import com.twotoasters.jazzylistview.JazzyGridView;
import com.twotoasters.jazzylistview.JazzyHelper;
import com.twotoasters.jazzylistview.recyclerview.JazzyRecyclerViewScrollListener;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

/**
 * PGC MUSIC LIST PAGE
 * 编辑推荐音乐界面
 * A simple {@link Fragment} subclass.
 */
public class MusicList extends Fragment implements PGCItemClickListener, Observer, OnBackPressedListener {
    private static final String KEY_TRANSITION_EFFECT = "transition_effect";
    private String TAG = MusicList.class.getName();
    private onMusicListCloseListener mCallBack;

    private int mCurrentTransitionEffect = JazzyHelper.HELIX;
    private JazzyRecyclerViewScrollListener jazzyScrollListener;

    private ArrayList<Music> pgcList;
    private JazzyGridView gridView;
    private RecyclerView recyclerView;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private Context context;
    private GridAdapter gridAdapter;
    public MusicList() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (Util.DEBUG)
            Log.e(TAG, "onCreateView");
        View view = inflater.inflate(R.layout.fragment_music_list, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.pgc_list);
        recyclerView.setLayoutManager(new GridLayoutManager(this.getActivity(), 2));
        gridAdapter = new GridAdapter(this.getActivity(), R.layout.grid_item, RunsicService.getInstance().musicPGCList);
        gridAdapter.setOnItemClickListener(this);
        recyclerView.setAdapter(gridAdapter);

        jazzyScrollListener = new JazzyRecyclerViewScrollListener();
        recyclerView.setOnScrollListener(jazzyScrollListener);

        RunsicService.getInstance().addPGCListObserver(this);
        return view;

    }



    @Override
    public void onResume() {
        super.onResume();
        if (Util.DEBUG)
            Log.e(TAG, "" + pgcList);
        context = this.getActivity();
        if (RunsicService.getInstance().musicPGCList != null && RunsicService.getInstance().musicPGCList.size()!=0) {
            pgcList = RunsicService.getInstance().musicPGCList;
            recyclerView.setLayoutManager(new GridLayoutManager(this.getActivity(), 2));
            recyclerView.setAdapter(new GridAdapter(context, R.layout.grid_item, pgcList));
            return;
        }
        RunsicService.getInstance().getPGCList();


    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_TRANSITION_EFFECT, mCurrentTransitionEffect);
    }


    private void setupJazziness(int effect) {
        mCurrentTransitionEffect = effect;
        jazzyScrollListener.setTransitionEffect(mCurrentTransitionEffect);
    }




    @Override
    public void update(Observable observable, Object data) {
        if (Util.DEBUG)
            Log.e("Music List", "onReceive PGC List Change");

        if (observable instanceof PGCMusicList) {
            PGCMusicList musicList = (PGCMusicList) observable;
            pgcList = musicList.getPGCMusic();
            recyclerView.setLayoutManager(new GridLayoutManager(this.getActivity(), 2));
            if (Util.DEBUG)
                Log.e(TAG, "this is 0000" + this + "     this act is 0000 " + this.getActivity());
            recyclerView.setAdapter(new GridAdapter(context, R.layout.grid_item, pgcList));
        }
    }


    @Override
    public void onItemClick(View view, int position) {
        if (Util.DEBUG)
            Log.e(TAG, "OnItemClick position is " + position);
//        Intent intent = new Intent();
//        intent.setClass(this.getActivity(), MusicListDetailActivity.class);
//        this.getActivity().startActivity(intent);
        mCallBack.onMusicListClose(position);

    }

    @Override
    public void onBackPressed() {
        if (Util.DEBUG)
            Log.e(TAG, "back");
        mCallBack.onMusicListClose(-1);
    }

    public interface onMusicListCloseListener {
        void onMusicListClose(int position);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

//        try {
//            mCallBack = (onMusicListCloseListener) activity;
//        } catch (ClassCastException e) {
//            throw new ClassCastException(activity.toString() + " must implement onMusicListCloseListener");
//        }
    }


}
