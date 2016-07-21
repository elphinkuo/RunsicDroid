package com.runningmusic.runninspire;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.graphics.Palette;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.google.android.gms.appindexing.Action;
import com.makeramen.roundedimageview.RoundedImageView;
import com.runningmusic.network.service.ImageSingleton;
import com.runningmusic.service.RunsicService;
import com.runningmusic.share.BaseShareActivity;
import com.runningmusic.utils.Util;
import com.runningmusic.view.Blur;
//import com.runningmusiclib.cppwrapper.ActivityManagerWrapper;

public class ShareActivity extends BaseShareActivity implements View.OnClickListener {
    private static String TAG = ShareActivity.class.getName();

    private Typeface highNumberTypeface;
    private RoundedImageView roundImageView;
    private RoundedImageView roundImageView2;
    private RoundedImageView roundImageView3;
    private ImageLoader imageLoader;
    private RelativeLayout wholeRelativeLayout;
    private RelativeLayout shareBottom;
    private RelativeLayout shareTitleContainer;


    private ImageButton shareWechat;
    private ImageButton shareQQ;
    private ImageButton shareWeibo;
    private ImageView bgGradient;

    private Animation mSlideinFromBottom;
    private Animation mSlideinFromBottomLeft;
    private Animation mSlideinFromBottomRight;

    private Palette palette;


    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);

        Bundle bundle = getIntent().getExtras();

        imageLoader = ImageSingleton.getInstance(this).getImageLoader();
        wholeRelativeLayout = (RelativeLayout) findViewById(R.id.share_content);
        shareBottom = (RelativeLayout) findViewById(R.id.layout_share_bottom);
//        bgGradient = (ImageView) findViewById(R.id.share_bg_gradient);
        shareTitleContainer = (RelativeLayout) findViewById(R.id.share_title_container);
        shareWechat = (ImageButton) findViewById(R.id.share_wechatmoments);
        shareWeibo = (ImageButton) findViewById(R.id.share_qzone);
        shareQQ = (ImageButton) findViewById(R.id.share_qq);

        mSlideinFromBottom = AnimationUtils.loadAnimation(thisActivity_, R.anim.slidein_frombottom);
        mSlideinFromBottomLeft = AnimationUtils.loadAnimation(thisActivity_, R.anim.slidein_frombottom_left);
        mSlideinFromBottomRight = AnimationUtils.loadAnimation(thisActivity_, R.anim.slidein_frombottom_right);

        highNumberTypeface = Typeface.createFromAsset(this.getAssets(), "fonts/tradegothicltstdbdcn20.ttf");

        query_.id(R.id.share_distance_value).typeface(highNumberTypeface).text(Util.getTimeForShow(bundle.getInt("duration", 0)));
        query_.id(R.id.share_time_value).typeface(highNumberTypeface).text(String.format("%.02f", (float) bundle.getInt("distance", 0) * 0.6 / 1000));
        query_.id(R.id.share_song).text("本次运动您听了"+bundle.getInt("song", 0) + "首歌");
//        query_.id(R.id.share_date).text(Util.dateFormat(ActivityManagerWrapper.getLastRgmActivity().getStartTime(), "yyyy-MM-dd"));


        query_.id(R.id.share_button).clickable(true).clicked(this);
        query_.id(R.id.share_return).clickable(true).clicked(this);
        query_.id(R.id.share_content).clickable(true).clicked(this);

        query_.id(R.id.share_wechatmoments).clickable(true).clicked(this);
        query_.id(R.id.share_qzone).clickable(true).clicked(this);
        query_.id(R.id.share_qq).clickable(true).clicked(this);
//        riv.setBorderWidth((float) 4);
//        riv.setBorderColor(Color.argb(255, 119, 119, 119));
//        riv.setBorderColor(ColorStateList.createFromXml());



        initImage();
    }

    private void initImage() {
        roundImageView = (RoundedImageView) findViewById(R.id.share_image1);
        roundImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        roundImageView.setCornerRadius(Util.dp2px(this.getResources(), 10));
        roundImageView.mutateBackground(false);
        roundImageView.setBackgroundColor(Color.argb(0,255,255,255));
        roundImageView2 = (RoundedImageView) findViewById(R.id.share_image2);
        roundImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        roundImageView.setCornerRadius(Util.dp2px(this.getResources(), 10));
        roundImageView.mutateBackground(false);
        roundImageView.setBackgroundColor(Color.argb(0,255,255,255));
        roundImageView3 = (RoundedImageView) findViewById(R.id.share_image3);
        roundImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        roundImageView.setCornerRadius(Util.dp2px(this.getResources(), 10));
        roundImageView.mutateBackground(false);
        roundImageView.setBackgroundColor(Color.argb(0, 255, 255, 255));




        if (RunsicService.getInstance().musicCurrentList!=null ) {
            if (Util.DEBUG)
                Log.e(TAG, "set adapter currentList");

            imageLoader.get(RunsicService.getInstance().musicCurrent.getCurrentMusic().coverURL, new ImageLoader.ImageListener() {
                @Override
                public void onResponse(final ImageLoader.ImageContainer imageContainer, boolean isImmediate) {
                    if (Util.DEBUG)
                        Log.e(TAG, "onResponse");
                    if (isImmediate && imageContainer.getBitmap() == null) return;
                    roundImageView.setImageBitmap(imageContainer.getBitmap());

                    Palette.generateAsync(imageContainer.getBitmap(), new Palette.PaletteAsyncListener() {
                        @Override
                        public void onGenerated(Palette palette) {
                            Palette.Swatch vibrant = palette.getDarkMutedSwatch();
                            Palette.Swatch lightVibrantSwatch = palette.getLightVibrantSwatch();
                            Palette.Swatch darkSwatch = palette.getDarkMutedSwatch();

                            if (vibrant!=null) {
                                query_.id(R.id.share_date).textColor(vibrant.getTitleTextColor());
                                query_.id(R.id.share_song).textColor(vibrant.getBodyTextColor());
                                query_.id(R.id.share_distance_value).textColor(vibrant.getTitleTextColor());
                                query_.id(R.id.share_distance_text).textColor(vibrant.getBodyTextColor());
                                query_.id(R.id.share_time_value).textColor(vibrant.getTitleTextColor());
                                query_.id(R.id.share_time_text).textColor(vibrant.getBodyTextColor());
//                                int colors[] = {darkSwatch.getRgb(), Color.argb(0, 255,255,255)};
//                                GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, colors);
//                                query_.id(R.id.share_bg_gradient).bac();
//                                bgGradient.setBackground(gradientDrawable);
                            }
                        }
                    });
                    new AsyncTask<String, Integer, Bitmap>() {

                        @Override
                        protected Bitmap doInBackground(String... params) {
                            return Blur.fastblur(thisActivity_, ThumbnailUtils.extractThumbnail(imageContainer.getBitmap(), 150, 250), 80);
                        }

                        @Override
                        protected void onPostExecute(Bitmap result) {
                            if (result != null) {
                                wholeRelativeLayout.setBackground(new BitmapDrawable(result));
                            }


                        }

                    }.execute();
                }
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    if (Util.DEBUG)
                        Log.e(TAG, "onErrorResponse");
                }
            });

            imageLoader.get(RunsicService.getInstance().musicCurrentList.getCurrentMusicList().get(1).coverURL, new ImageLoader.ImageListener() {
                @Override
                public void onResponse(ImageLoader.ImageContainer imageContainer, boolean b) {
                    roundImageView2.setImageBitmap(imageContainer.getBitmap());
                }

                @Override
                public void onErrorResponse(VolleyError volleyError) {

                }
            });

            imageLoader.get(RunsicService.getInstance().musicCurrentList.getCurrentMusicList().get(2).coverURL, new ImageLoader.ImageListener() {
                @Override
                public void onResponse(ImageLoader.ImageContainer imageContainer, boolean b) {
                    roundImageView3.setImageBitmap(imageContainer.getBitmap());
                }

                @Override
                public void onErrorResponse(VolleyError volleyError) {

                }
            });
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Share Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.runningmusic.runninspire/http/host/path")
        );
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Share Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.runningmusic.runninspire/http/host/path")
        );

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.share_return:
                onBackPressed();
                break;
            case R.id.share_button:
                shareBottom.setVisibility(View.VISIBLE);
                shareWechat.startAnimation(mSlideinFromBottomLeft);
                shareWeibo.startAnimation(mSlideinFromBottom);
                shareQQ.startAnimation(mSlideinFromBottomRight);
                break;

            case R.id.share_qq:
                shareBottom.setVisibility(View.INVISIBLE);
//                shareTitleContainer.setVisibility(View.INVISIBLE);
                query_.id(R.id.share_button).invisible();
                query_.id(R.id.share_return).invisible();
                query_.id(R.id.share_title_container).backgroundColor(Color.argb(0, 0, 0, 0));
                tapShareQQ();

                break;

            case R.id.share_wechatmoments:
                shareBottom.setVisibility(View.INVISIBLE);
                query_.id(R.id.share_button).invisible();
                query_.id(R.id.share_return).invisible();
                query_.id(R.id.share_title_container).backgroundColor(Color.argb(0, 0, 0, 0));
                tapShareWechatmoments();

                break;

//            case R.id.share_wechat:
//                shareBottom.setVisibility(View.INVISIBLE);
//                query_.id(R.id.share_button).invisible();
//                query_.id(R.id.share_return).invisible();
//                query_.id(R.id.share_title_container).backgroundColor(Color.argb(0, 0, 0, 0));
//                tapShareQZone();
//
//                break;
            case R.id.share_qzone:
                shareBottom.setVisibility(View.INVISIBLE);
                query_.id(R.id.share_button).invisible();
                query_.id(R.id.share_return).invisible();
                query_.id(R.id.share_title_container).backgroundColor(Color.argb(0, 0, 0, 0));
                tapShareQZone();

                break;



            case R.id.share_content:
                query_.id(R.id.share_button).visible();
                query_.id(R.id.share_return).visible();
                query_.id(R.id.share_title_container).background(R.mipmap.sharepage_nav_bg);
                break;



        }
    }
}
