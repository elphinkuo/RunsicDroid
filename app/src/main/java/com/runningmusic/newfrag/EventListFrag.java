package com.runningmusic.newfrag;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.androidquery.AQuery;
import com.runningmusic.activity.WebViewActivity;
import com.runningmusic.adapter.EventAdapter;
import com.runningmusic.adapter.EventItemClickListener;
import com.runningmusic.event.EventRequestEvent;
import com.runningmusic.music.Event;
import com.runningmusic.network.http.RunsicRestClientUsage;
import com.runningmusic.runninspire.R;
import com.twotoasters.jazzylistview.JazzyHelper;
import com.twotoasters.jazzylistview.recyclerview.JazzyRecyclerViewScrollListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class EventListFrag extends Fragment implements EventItemClickListener{

    private static final String KEY_TRANSITION_EFFECT = "transition_effect";
    private int mCurrentTransitionEffect = JazzyHelper.TILT;
    private JazzyRecyclerViewScrollListener jazzyScrollListener;

    private AQuery aQuery;
    private Activity context;
    private RecyclerView eventRecyclerView;
    private EventAdapter eventAdapter;
    private ArrayList<Event> eventArrayList;
    public EventListFrag() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_event_list, container, false);

        context = this.getActivity();
        aQuery = new AQuery(view);
        eventRecyclerView = (RecyclerView) view.findViewById(R.id.event_list);
        eventRecyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        jazzyScrollListener = new JazzyRecyclerViewScrollListener();
        eventRecyclerView.setOnScrollListener(jazzyScrollListener);
        RunsicRestClientUsage.getInstance().getEventList();

        //when the result comeBack
//        eventAdapter = new EventAdapter(context, R.layout.event_list_item_layout, )

        return view;
    }



    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    /**
     * 活动列表页收到网络请求
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventRequestEvent(EventRequestEvent event) {
        eventArrayList = event.eventList;
        eventAdapter = new EventAdapter(context, R.layout.event_list_item_layout, event.eventList);
        eventAdapter.setOnItemClickListener(this);
        eventRecyclerView.setAdapter(eventAdapter);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }


    @Override
    public void onItemClick(View view, int position) {
        Event event = eventArrayList.get(position);
        Intent intent = new Intent();
        intent.setClass(context, WebViewActivity.class);
        intent.putExtra("title", event.title);
        intent.putExtra("url", event.linkURL);
        startActivity(intent);
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
}
