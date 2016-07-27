package com.runningmusic.fragment;


import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.PolylineOptions;
import com.androidquery.AQuery;
import com.androidquery.util.AQUtility;
import com.google.protobuf.InvalidProtocolBufferException;
import com.runningmusic.jni.SportTracker;
import com.runningmusic.runninspire.Messages;
import com.runningmusic.runninspire.R;
import com.runningmusic.runninspire.RunsicActivity;
import com.runningmusic.utils.Log;
import com.runningmusic.utils.Util;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class MoveMapFragment extends Fragment implements AMap.OnMapLoadedListener, AMap.OnCameraChangeListener, AMap.CancelableCallback, OnBackPressedListener {
    private static String TAG = MoveMapFragment.class.getName();

    private AMap aMap;
    private MapView mapView;
    private UiSettings uiSettings;
    private MarkerOptions startMarkerOptions;
    private MarkerOptions endMarkerOptions;
    private AQuery aQuery;
    private List<Messages.Location> locations;
    private Typeface highNumberTypeface;
    private float mCameraZoom = 17;
    private PolylineOptions polylineOptions;
    private boolean isFirstRefresh = true;
    private Handler loopHandler;
    private Runnable runnable;
    private FragmentActivity contextActivity;

    private boolean isDottedLine;

    private double distanceValue;
    private String paceValue;
    private double speed;


    public MoveMapFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState ) {
        // Inflate the layout for this fragment
        contextActivity = this.getActivity();
        View view = inflater.inflate(R.layout.fragment_move_map, container, false);
        mapView = (MapView) view.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        aQuery = new AQuery(view);
        aQuery.id(R.id.distance_data).typeface(RunsicActivity.highNumberTypeface);
        aQuery.id(R.id.pace_data).typeface(RunsicActivity.highNumberTypeface);
        aQuery.id(R.id.time_data).typeface(RunsicActivity.highNumberTypeface);
        loopHandler = new Handler();

        if (aMap == null) {
            aMap = mapView.getMap();
            aMap.clear();
            uiSettings = aMap.getUiSettings();
            setMap();
        }
        return view;
    }

    @Override
    public void onMapLoaded() {

        Log.e(TAG, "onMapLoaded()");

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(new LatLng(39.992645, 116.337548));
        builder.build();
        mCameraZoom = 17;

        aMap.setOnCameraChangeListener(this);

        refreshMap();
    }

    public void refreshMap() {

        distanceValue = SportTracker.getDistance();
        speed = SportTracker.getSpeed();
        paceValue = Util.getPaceValue(speed);


        try {
            locations = Messages.Sport.Extra.parseFrom(SportTracker.getExtra()).getLocationList();
//            locations = Messages.Sport.parseFrom(SportTracker.getData()).getExtra().getLocationList();
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }


        aQuery.id(R.id.distance_data).text(String.format("%.2f", distanceValue/1000));
        aQuery.id(R.id.pace_data).text(paceValue);


        int i = 0;
        Log.e(TAG, "" + i++);
        Log.e(TAG, "locations size" + locations.size());
//        boolean ret = checkGPS();
//        if (!ret && openGPSDialog != null) {
//            if (!openGPSDialog.isShowing()) {
//                try {
//                    showDialog(ID_OPEN_GPS_DIALOG);
//                } catch (Exception e) {
//
//                }
//            }
//
//        } else if (ret && (openGPSDialog != null)) {
//            dismissDialog(ID_OPEN_GPS_DIALOG);
//        }
        if (locations.size() != 0) {
            Messages.Location startLocation = locations.get(0);
            Messages.Location leLocation = locations.get(locations.size() - 1);

            LatLng startLatLng = new LatLng(startLocation.getLatitude(), startLocation.getLongitude());
            LatLng locationLatLng = new LatLng(leLocation.getLatitude(), leLocation.getLongitude());
//            CameraPosition cameraPosition = new CameraPosition(locationLatLng, )
            if (isFirstRefresh) {
                aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(locationLatLng, 17), 2000, this);
//                aMap.animateCamera(CameraUpdateFactory.newCameraPosition());
                isFirstRefresh = false;
            } else {
                Log.e(TAG, "camera zoom is " + mCameraZoom);
                aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(locationLatLng, mCameraZoom), 2000, this);
                isFirstRefresh = false;
            }
            aMap.clear();
            polylineOptions.add(locationLatLng);
            aMap.addPolyline(polylineOptions);
            startMarkerOptions.position(startLatLng);
            endMarkerOptions.position(locationLatLng);
            aMap.addMarker(startMarkerOptions);
            aMap.addMarker(endMarkerOptions);
        }

    }

    @Override
    public void onCameraChange( CameraPosition cameraPosition ) {

    }

    @Override
    public void onCameraChangeFinish( CameraPosition cameraPosition ) {
        if (cameraPosition != null) {
            mCameraZoom = cameraPosition.zoom;
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
        isFirstRefresh = true;
        runnable = new Runnable() {

            @Override
            public void run() {
                refreshMap();
                loopHandler.postDelayed(this, 2000);
                Log.e("", "loop timer test");
            }
        };
        loopHandler.postDelayed(runnable, 2000);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    /*
     * 设置地图底图风格
     */
    private void setMap() {

        startMarkerOptions = new MarkerOptions();
        startMarkerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.manual_icon_start));
        startMarkerOptions.anchor((float) 0.5, (float) 0.5);
        endMarkerOptions = new MarkerOptions();
//        endMarkerIcons.add(BitmapDescriptorFactory.fromResource(R.mipmap.anchor_green_1));
//        endMarkerIcons.add(BitmapDescriptorFactory.fromResource(R.mipmap.anchor_green_2));
//        endMarkerIcons.add(BitmapDescriptorFactory.fromResource(R.mipmap.anchor_green_3));

        endMarkerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.anchor_green_2));
        endMarkerOptions.anchor((float) 0.5, (float) 0.5);
        endMarkerOptions.period(10);
        endMarkerOptions.setFlat(false);
        aMap.setMyLocationEnabled(true);
        aMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);
        uiSettings.setZoomControlsEnabled(false);
        uiSettings.setMyLocationButtonEnabled(false);
        uiSettings.setRotateGesturesEnabled(false);
        uiSettings.setTiltGesturesEnabled(false);

        polylineOptions = new PolylineOptions();

//        LocationProvider
//
//        mAMapLocationManager = LocationManagerProxy.getInstance(this);
//        mAMapLocationManager.setGpsEnable(true);
//        /*
//         * mAMapLocManager.setGpsEnable(false); 1.0.2版本新增方法，设置true表示混合定位中包含gps定位，false表示纯网络定位，默认是true Location API定位采用GPS和网络混合定位方式
//         * ，第一个参数是定位provider，第二个参数时间最短是2000毫秒，第三个参数距离间隔单位是米，第四个参数是定位监听者
//         */
//        mAMapLocationManager.requestLocationUpdates(LocationProviderProxy.AMapNetwork, 2000, 10, this);

        aMap.setOnMapLoadedListener(this);
        aMap.setMapType(AMap.MAP_TYPE_NIGHT);

        isDottedLine = false;
        polylineOptions = new PolylineOptions();
        polylineOptions.width(AQUtility.dip2pixel(contextActivity, 8));
        polylineOptions.visible(true);
        polylineOptions.setDottedLine(isDottedLine);
        polylineOptions.color(Color.argb(255, 27, 222, 97));

    }


    @Override
    public void onFinish() {

    }

    @Override
    public void onCancel() {

    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onBackPressed() {

    }

    @Override
    public void onSaveInstanceState( Bundle outState ) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onLocationChangedEvent(LocationChangedEvent event) {
//        Location location
////        eventArrayList = event.eventList;
////        eventAdapter = new EventAdapter(context, R.layout.event_list_item_layout, event.eventList);
////        eventAdapter.setOnItemClickListener(this);
////        eventRecyclerView.setAdapter(eventAdapter);
//    }

}
