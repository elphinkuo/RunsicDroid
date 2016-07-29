package com.runningmusic.runninspire;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.PolylineOptions;
import com.androidquery.AQuery;
import com.androidquery.util.AQUtility;
import com.google.protobuf.InvalidProtocolBufferException;
import com.runningmusic.jni.SportTracker;
import com.runningmusic.utils.ManualRGMColorPick;
import com.runningmusic.utils.Util;
import com.runningmusic.view.LineChart;
import com.umeng.socialize.net.utils.Base64;

import java.util.ArrayList;
import java.util.List;

public class RunResultActivity extends AppCompatActivity implements AMap.OnMapLoadedListener, AMap.OnMapScreenShotListener, View.OnClickListener {
    private MapView mapView;
    private boolean showMap = true;
    private static String TAG = RunResultActivity.class.getName();

    private List<Messages.Location> locations_;
    private AQuery aQuery;
    private AMap aMap;
    private Activity context;

    private LineChart lineChart;

    private Messages.Sport sport;
    private MarkerOptions startMarkerOptions;
    private MarkerOptions endMarkerOptions;
    private UiSettings uiSettings;
    private ArrayList<Messages.Location> adjustLocations;
    private LatLng startLatLng;
    private LatLng endLatLng;
    private List<Messages.Step> speedArray;
    private DisplayMetrics metrics_;
    private RelativeLayout colorPanelLayout;
    private Typeface condTypeface_;

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_run_result);
        aQuery = new AQuery(this);
        context = this;
        Intent intent = this.getIntent();


        //初始化sport
        byte[] data = Base64.decodeBase64("CI2Wo+TiKhDJAhjfBCBvLS1C4kM1dUOoP0KSEUISCOym3RIQ////////////ARgAQgkIhL3dEhABGABCCQi44N0SEAEYAEIJCNPw3RIQBBhJQgkIgYHeEhAHGE5CCQjYkd4SEAsYWEIJCN+h3hIQDxhlQgkI17PeEhATGG5CCQjcxt4SEBgYcUIJCPfW3hIQHBh0QgkIkOreEhAhGHZCCQjT+t4SECMYXEIJCL6L3xIQJRg/QgkInJvfEhAnGDlCCQjErN8SECkYOEIJCPK83xIQLBhAQgkI48/fEhAwGE5CCQj34N8SEDMYV0IJCNXw3xIQNxhgQgkIrIHgEhA8GGtCCQjTkuASEEAYfEIJCJWm4BIQRRhzQgkIlLfgEhBJGHFCCQiHx+ASEE0YdEIJCMnX4BIQURhzQgkI9+rgEhBWGHRCCQj7gOESEFkYVkIJCJSU4RIQXRhZQgkImqThEhBhGFxCCQiateESEGUYc0IJCLPI4RIQahh0QgkIqeDhEhBuGF5CCQjD8OESEHIYZUIJCMGE4hIQdxhlQgkI8JTiEhB7GHRCCQj2pOISEH4YZ0IKCOe34hIQgwEYbkIKCKrI4hIQhwEYa0IKCIjY4hIQiwEYdkIKCI/o4hIQjwEYdUIKCJX44hIQkwEYdkIKCJOM4xIQmAEYdUIKCKWg4xIQnAEYaUIKCKyw4xIQoAEYbUIKCJbE4xIQpAEYX0IKCLDU4xIQqQEYbUIKCLfk4xIQrQEYfkIKCKn04xIQsQEYf0IKCNiE5BIQtQEYc0IKCJuV5BIQuAEYZUIKCKCo5BIQvQEYbEIKCM645BIQwgEYdUILCK3I5BIQxgEYhAFCCgif2OQSEMoBGHtCCgi56OQSEM4BGHZCCgjA+OQSENIBGHVCCgjGiOUSENYBGHVCCgjNmOUSENkBGGlCCgiQqeUSEN0BGGpCCgiWueUSEOEBGGxCCgidyeUSEOYBGHVCCgj02eUSEOoBGH9CCgi26uUSEO0BGG1CCgj5+uUSEPEBGGhCCgj5i+YSEPUBGGdCCgiSn+YSEPoBGHZCCgjVr+YSEP4BGH1CCwiCw+YSEIQCGIMBQgsIidPmEhCJAhiIAUILCLfj5hIQjQIYhwFCCgi+8+YSEJECGHpCCwjEg+cSEJYCGIUBQgoIh5TnEhCaAhh/QgoIjaTnEhCeAhh/QgoIlLTnEhChAhhoQgoIr8TnEhClAhhrQgoIztnnEhCpAhhoQgoI1OnnEhCtAhhpQgoIl/rnEhCwAhhgQgoI9YnoEhC0AhhlQgoI55noEhC4AhhsQgoIxqnoEhC8Ahh3QgoIpLnoEhDAAhh3QgoIvsnoEhDEAhh2QgoIvtroEhDIAhhzQgoIr+3oEhDNAhh1QgoI6v7oEhDRAhhyQgoImJLpEhDWAhh0QgoI9qHpEhDaAhh0QgsIxrPpEhDhAhiHAUILCL7F6RIQ5wIYoQFCCwjY1ekSEOsCGIwBQgoImunpEhDwAhh6QgoIjPnpEhD0Ahh2QgsIk4nqEhD5AhiAAUIKCJ+b6hIQ/QIYfUIKCJCu6hIQggMYdEIKCL3B6hIQhwMYeEIKCOTV6hIQjAMYdUIKCOrl6hIQkAMYdUIKCJj56hIQlQMYdkIKCPaI6xIQmQMYd0IKCM2Z6xIQnQMYdUIKCNKs6xIQogMYd0IKCKm96xIQpgMYdUIKCOzN6xIQqQMYaEIKCPHg6xIQrgMYbEIKCPfw6xIQsgMYbUIKCLqB7BIQtgMYdUIKCOmR7BIQuwMYe0ILCIKl7BIQwAMYgAFCCwjFtewSEMQDGIABQgoI8sjsEhDJAxh1QgoI+djsEhDNAxh2QgsI6+jsEhDTAxiGAUILCJr57BIQ2QMYmwFCCwigie0SEN8DGKoBQgsIs53tEhDkAxiJAUIKCPWt7RIQ6AMYd0IKCJC+7RIQ7AMYbkIKCIHR7RIQ8QMYdkIKCL3i7RIQ9QMYdEIKCNb17RIQ+gMYd0IKCK2G7hIQ/gMYc0IKCLmY7hIQhAQYf0ILCOio7hIQiAQYhwFCCwjuuO4SEIwEGIEBQgsI38vuEhCSBBiCAUIKCIDe7hIQlgQYfkIKCMPu7hIQmgQYdEIKCMiB7xIQoAQYfkILCM6R7xIQpAQYgwFCCwjVoe8SEKgEGIIBQgoI07XvEhCtBBh1QgoIgcbvEhCxBBh0QgoIh9nvEhC2BBh3QgoI+ejvEhC6BBh3QgoIqPnvEhC+BBh1QgoIronwEhDCBBh1QgoIv6DwEhDGBBhgQgoIsbDwEhDKBBhmQgoIuMDwEhDOBBhmQgoI39HwEhDSBBh1QgoItuLwEhDXBBh7QgsIhvTwEhDcBBiBAUIKCKWJ8RIQ3wQYXEolCPi9pOTiKhFxPQrXO/1DQBmtaCTgDxRdQCUAAAAALQAAAAAwAEojCMKApuTiKhGcNuM0RP1DQBmKOnMPCRRdQCUAAAAALbdj6T9KIwijvKfk4ioRGCR9WkX9Q0AZe5+qQgMUXUAlAAAAAC1K+6E/SiMIovin5OIqEcuGNZVF/UNAGbEUyVcCFF1AJQAAAAAtIS0hP0ojCJiHqeTiKhFNZVHYRf1DQBl/vcKC+xNdQCUAAAAALTCv+D9KIwjuz6rk4ioREf+wpUf9Q0AZDat4I/MTXUAlAAAAAC3LGts/SiMIhtOr5OIqEc6njlVK/UNAGX9skh/xE11AJQAAAAAtashTP0ojCMm0ruTiKhEexM4UOv1DQBkYCAJk6BNdQCUAAAAALREYyj9KIwi63bDk4ioRzqeOVUr9Q0AZf2ySH/ETXUAlAAAAAC2BpvA/SiMIvdqx5OIqEZJ1OLpK/UNAGXE8nwH1E11AJQAAAAAtvN+hP0ojCIaItOTiKhHuXBjpRf1DQBmn6Egu/xNdQCUAAAAALRGktz9KJQjh0rbk4ioRN6W8VkL9Q0AZigYpeAoUXUAlAAAAAC1cQ7U/MAI=");
        try {
            sport = Messages.Sport.parseFrom(data);
            locations_ = sport.getExtra().getLocationList();

            Log.e(TAG, "Location Size is " + sport.getExtra().getLocationCount());
            Log.e(TAG, "Location LIST is " + sport.getExtra().getLocationList());

        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
            this.finish();
        }
        condTypeface_ = Typeface.createFromAsset(this.getAssets(), "fonts/akzidenzgrotesklightcond.ttf");

        //显示地图还是图表
        showMap = intent.getBooleanExtra("showmap", false);

        if (showMap) {
            mapView = (MapView) findViewById(R.id.result_map);
            mapView.onCreate(savedInstanceState);
            if (aMap == null) {
                aMap = mapView.getMap();
            }
            uiSettings = aMap.getUiSettings();
            setMap();
        } else {

            setLineChat();

        }

        aQuery.id(R.id.result_back).clickable(true).clicked(this);
        aQuery.id(R.id.result_delete).clickable(true).clicked(this);

        aQuery.id(R.id.distance_data).text(String.format("%.2f", sport.getDistance()/1000)).typeface(condTypeface_);
        aQuery.id(R.id.time_data).text(Util.getClockShowTime(sport.getDuration())).typeface(condTypeface_);
        aQuery.id(R.id.result_steps).text(""+sport.getStep()+"步").typeface(condTypeface_);
        aQuery.id(R.id.result_bpm).text(""+ sport.getBpm()+"bpm").typeface(condTypeface_);
        aQuery.id(R.id.result_pace).text(Util.getPaceValue(sport.getSpeed())).typeface(condTypeface_);
        aQuery.id(R.id.result_calory).text(""+Util.getCalorie(sport.getStep())+"kCal").typeface(condTypeface_);


    }

    @Override
    public void onMapLoaded() {
        int size = locations_.size();
        Log.e(TAG, "size is " + size);
        PolylineOptions optline = new PolylineOptions();
        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(Color.argb(255, 243, 51, 25));
        colors.add(Color.argb(255, 250, 197, 44));
        colors.add(Color.argb(255, 130, 219, 12));

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        if (size == 0) {
            return;
        } else if (size == 1) {
            Messages.Location point = locations_.get(0);
            aMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(point.getLatitude(), point.getLongitude())), 200, null);
        } else {
            for (int i = 0; i < size - 1; i++) {
                Log.e(TAG, "RunResultActivity " + i);
                LatLng tmp = new LatLng(locations_.get(i).getLatitude(), locations_.get(i).getLongitude());
                optline.add(tmp);
                builder.include(tmp);
                Log.e(TAG, "polyLine is " + optline.getPoints());
                Log.e(TAG, "polyLine Z is " + optline.getZIndex());
            }

        }
        optline.colorValues(colors);

        optline.useGradient(true);

        optline.width(16);
        aMap.addPolyline(optline);
        LatLngBounds bounds = builder.build();
        aMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 0), 200, null);


    }

    /**
     * 初始化地图
     */
    private void setMap() {
        startMarkerOptions = new MarkerOptions();
        startMarkerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.manual_icon_start));
        startMarkerOptions.anchor((float) 0.5, (float) 0.5);
        endMarkerOptions = new MarkerOptions();
        endMarkerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.manual_icon_end));
        endMarkerOptions.anchor((float) 0.5, (float) 0.5);

        uiSettings.setZoomControlsEnabled(false);
        uiSettings.setMyLocationButtonEnabled(false);
        uiSettings.setRotateGesturesEnabled(false);
        uiSettings.setTiltGesturesEnabled(false);
        aMap.setMapType(AMap.MAP_TYPE_NIGHT);
        Log.e(TAG, "set map type 2");
        aMap.setOnMapLoadedListener(this);

        if (locations_.size() != 0) {
            Messages.Location startLocation = locations_.get(0);
            Messages.Location leLocation = locations_.get(locations_.size() - 1);

            adjustLocations = new ArrayList<Messages.Location>();

            startLatLng = new LatLng(startLocation.getLatitude(), startLocation.getLongitude());
            endLatLng = new LatLng(leLocation.getLatitude(), leLocation.getLongitude());
            speedArray = new ArrayList<>();
            speedArray = sport.getExtra().getStepList();
            new ManualRGMColorPick();

            startMarkerOptions.position(startLatLng);
            endMarkerOptions.position(endLatLng);
            aMap.addMarker(startMarkerOptions);
            aMap.addMarker(endMarkerOptions);
            aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(endLatLng, 18));
        }

    }

    /**
     * 初始化图表
     */
    private void setLineChat() {

        metrics_ = new DisplayMetrics();
        colorPanelLayout = (RelativeLayout) this.findViewById(R.id.manual_rgm_colorpanel);
        getWindowManager().getDefaultDisplay().getMetrics(metrics_);
        lineChart = new LineChart(this, sport, metrics_, 300);


        RelativeLayout lineChartLayout = new RelativeLayout(this);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(metrics_.widthPixels, AQUtility.dip2pixel(this, 300));

        params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        params.addRule(RelativeLayout.CENTER_HORIZONTAL);

        lineChartLayout.setLayoutParams(params);
        // lineChart_.setBackgroundResource(R.drawable.sport_icon_start);

        lineChart.setBackgroundColor(Color.TRANSPARENT);
        lineChartLayout.addView(lineChart);

        lineChartLayout.setBackgroundResource(R.mipmap.result_bg);

        colorPanelLayout.addView(lineChartLayout);
    }

    @Override
    public void onMapScreenShot( Bitmap bitmap ) {

    }

    @Override
    public void onMapScreenShot( Bitmap bitmap, int i ) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (lineChart!=null) {
            lineChart.destroyDrawingCache();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (lineChart!=null) {
            lineChart.destroyDrawingCache();
        }
    }


    @Override
    public void onClick( View v ) {
        switch(v.getId()) {
            case R.id.result_back:
                context.finish();
                break;
            case R.id.result_delete:
                SportTracker.reset();
                this.finish();
                break;
            case R.id.result_share:
                break;

            default:
                break;
        }
    }

    private void setCustomTextView(int id, String tempContentString, String unit, int sizeSpan) {
        SpannableString contentString = null;

        contentString = new SpannableString(tempContentString + unit);

        contentString.setSpan(new AbsoluteSizeSpan(sizeSpan, true), 0, tempContentString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        contentString.setSpan(new AbsoluteSizeSpan(20, true), tempContentString.length(), contentString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        aQuery.id(id).text(contentString).typeface(condTypeface_);

    }
}
