package com.runningmusic.fragment;



import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.amap.api.maps.AMap;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.MarkerOptions;
import com.runningmusic.event.LocationChangedEvent;
import com.runningmusic.runninspire.R;
import com.runningmusic.utils.Log;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * A simple {@link Fragment} subclass.
 */
public class MoveMapFragment extends Fragment implements AMap.OnMapLoadedListener, AMap.OnCameraChangeListener {
    private static String TAG = MoveMapFragment.class.getName();

    private AMap aMap;
    private MapView mapView;
    private UiSettings uiSettings;
    private MarkerOptions startMarkerOptions;
    private MarkerOptions endMarkerOptions;






    public MoveMapFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_move_map, container, false);
        mapView = (MapView) view.findViewById(R.id.map);


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

    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {

    }

    @Override
    public void onCameraChangeFinish(CameraPosition cameraPosition) {

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLocationChangedEvent(LocationChangedEvent locationChangedEvent) {
        Log.e(TAG, "GET MESSAGE DISTANCE IS " + locationChangedEvent.distance + " GET MESSAGE SPEED IS " + locationChangedEvent.speed);
//        favMusicList = favMusicListEvent.musicFavList;
//        gridAdapter = new GridAdapter(context, R.layout.horizontal_item_recycler, favMusicList);
//        favRecyclerView.setAdapter(gridAdapter);
    }

    /*
     * 设置地图底图风格
     */
    private void setMap() {

        startMarkerOptions = new MarkerOptions();
        startMarkerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.manual_icon_start));
        startMarkerOptions.anchor((float) 0.5, (float) 0.5);
        endMarkerOptions = new MarkerOptions();
        endMarkerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.manual_icon_end));
        endMarkerOptions.anchor((float) 0.5, (float) 0.5);
        aMap.setMyLocationEnabled(true);
        aMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);
        aMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);
        uiSettings.setZoomControlsEnabled(false);
        uiSettings.setMyLocationButtonEnabled(false);
        uiSettings.setRotateGesturesEnabled(false);
        uiSettings.setTiltGesturesEnabled(false);

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
        aMap.setMapType(AMap.MAP_TYPE_NORMAL);

//        isDottedLine = false;
//        polylineOptions = new PolylineOptions();
//        polylineOptions.width(AQUtility.dip2pixel(this, 8));
//        polylineOptions.visible(true);
//        polylineOptions.setDottedLine(isDottedLine);
//        polylineOptions.color(Color.argb(200, 255, 126, 0));

    }




}
