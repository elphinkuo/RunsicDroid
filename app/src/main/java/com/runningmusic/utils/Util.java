package com.runningmusic.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Environment;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.ViewConfiguration;
import android.view.WindowManager;

import com.runningmusic.music.Music;
import com.runningmusic.videocache.file.FileNameGenerator;
import com.runningmusic.videocache.file.Md5FileNameGenerator;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;

//import com.runningmusic.MainFragmentActivity;
//import com.runningmusic.oauth.ThirdLoginOAuth;

/**
 * Created by guofuming on 18/1/16.
 */
public class Util {
    public static String TAG = Util.class.getName();
    public static boolean DEBUG = true;
    public static boolean OFFLINE = false;
    public static boolean wifiConnectivity = true;
    public static boolean mobileConnectivity = true;
    private static int screenWidth;
    private static FileNameGenerator fileNameGenerator;


    //    @SuppressLint("SimpleDateFormat")
//    public static String dateFormat(Date date, String format) {
//        if (format == null) {
//            format = "yyyy-MM-dd HH:mm:ss";
//        }
//
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTime(date);
//
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
//        String string = simpleDateFormat.format(calendar.getTime());
//
//        return string;
//    }
//
    @SuppressLint("SimpleDateFormat")
    public static String dateFormat( java.util.Date date, String format ) {
        if (format == null) {
            format = "yyyy-MM-dd HH:mm:ss";
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        String string = simpleDateFormat.format(calendar.getTime());

        return string;
    }

    // public static Date SecondToDate(double second) {
    // return new Date((long) (second * 1000));
    // }
    //
    // public static double DateToSecond(Date date) {
    // long milliseconds = date.getTime();
    // return ((double) milliseconds) / 1000;
    // }

    /**
     * 获取本地数据TimeSlot第一个的时间（即安装时间）
     *
     * @return
     */
//    public static long getSetupTime() {
//        TimeSlot timeSlot = TimeSlotsManagerWrapper.firstTimeSlot(Util.context());
//        if (timeSlot == null) {
//            Log.e(TAG, "timeSlot is NULL!");
//            return Date.now().startOfCurrentDay().getTime();
//        }
//        return timeSlot.getStartTime().startOfCurrentDay().getTime();
//    }
    public static boolean saveUserPreferences( String[] args ) {
        SharedPreferences sPreferences = context().getSharedPreferences("XIAOBAI_SP", 0);

        Editor editor = sPreferences.edit();
        // editor.putString("", "");
        int i = 0;
        while (i < args.length) {
            editor.putString(args[i++], args[i++]);
        }
        editor.commit();

        return false;
    }

    public static SharedPreferences getUserPreferences() {
        SharedPreferences sPreferences = context().getSharedPreferences("XIAOBAI_SP", 0);

        return sPreferences;
    }

    /**
     * 获取用户的性别
     *
     * @return true man; false female
     */
    public static boolean userGender() {
        SharedPreferences sPreferences = Util.getUserPreferences();
        String sexString = sPreferences.getString(Constants.USER_INFO_GENDER, "m");
        return sexString.equalsIgnoreCase("m");
    }

    public static float userWeight() {
        SharedPreferences sPreferences = Util.getUserPreferences();
        float weight = sPreferences.getFloat(Constants.USER_INFO_WEIGHT, 65);
        return weight;
    }

    public static float userHeight() {
        SharedPreferences sPreferences = Util.getUserPreferences();
        float height = sPreferences.getFloat(Constants.USER_INFO_HEIGHT, (float) 1.70);
        return height;
    }

    public static float userAge() {
        SharedPreferences sPreferences = Util.getUserPreferences();
        float birthday = sPreferences.getFloat(Constants.USER_INFO_BIRTHDAY, 1988);
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        return year - birthday;
    }

    public static int userId() {
        SharedPreferences sPreferences = Util.getUserPreferences();
        return sPreferences.getInt(Constants.USER_INFO_USERID, 0);
    }

    public static String deviceId() {
        SharedPreferences sPreferences = Util.getUserPreferences();
        return sPreferences.getString(Constants.DEVICE_ID, "");
    }

    private static Context context_;

    public static void setContext( Context context ) {
        context_ = context;
    }

    public static Context context() {
        return context_;
    }

    public static String getDurationStringBySeconds( double seconds ) {
        int duration = (int) seconds;
        String durationString = "";
        if (duration >= 0) {
            int hour = duration / 3600;
            int minute = (duration % 3600) / 60;
            if (hour > 0) {
                durationString += "" + hour + "时";
            }
            durationString += minute + "分";
        }

        return durationString;
    }

    /*
     * set& get unread Comment
     */
    public static void setUnreadComment( int unreadCount ) {
        SharedPreferences sp = Util.getUserPreferences();
        Editor editor = sp.edit();
        editor.putInt("UNREADCOMMENT", unreadCount);
        editor.commit();
        // Intent intent = new Intent();
        // Util.context().sendBroadcast(intent);
    }

    public static int getUnreadComment() {
        SharedPreferences sp = Util.getUserPreferences();
        return sp.getInt("UNREADCOMMENT", 0);
    }

    public static void setUnreadNotification( int unreadCount ) {
        SharedPreferences sp = Util.getUserPreferences();
        Editor editor = sp.edit();
        editor.putInt("UNREAD", unreadCount);
        editor.commit();
    }

    public static int getUnreadNotification() {
        SharedPreferences sp = Util.getUserPreferences();
        return sp.getInt("UNREAD", 0);
    }

    public static boolean hasUnreadNotification() {
        return getUnreadNotification() > 0 ? true : false;
    }

    public static void setLedongliRunning( boolean running ) {
        SharedPreferences sp = Util.getUserPreferences();
        Editor editor = sp.edit();
        editor.putBoolean("IS_RUNNING", running);
        editor.commit();
    }

    public static boolean isLedongliRunning() {
        SharedPreferences sp = Util.getUserPreferences();
        return sp.getBoolean("IS_RUNNING", true);
    }

    public static boolean isMIUI() {
        SharedPreferences sPreferences = context().getSharedPreferences("XIAOBAI_SP", 0);
        int style = sPreferences.getInt("HEARTBEAT_STYLE", 0);
        return style == 1;
    }

//    public static int getDayCount() {
//        long setupTimeLong = Util.getSetupTime();
//        long nowTimeLong = System.currentTimeMillis();
//
//        long timeDiff = nowTimeLong - setupTimeLong;
//
//        int dayCount = (int) Math.ceil(((double) (timeDiff)) / (24 * 60 * 60 * 1000));
//
//        // 防止dayCount为0
//        if (timeDiff <= 0 || dayCount == 0) {
//            dayCount = 1;
//        }
//        return dayCount;
//    }

//    public static void saveUserInfo() {
//        SharedPreferences sPreferences = Util.getUserPreferences();
//        int steps = sPreferences.getInt(Constants.USER_GOAL_STEPS, 10000);
//        int calories = sPreferences.getInt(Constants.USER_GOAL_CALORIES, 300);
//        String sexString = sPreferences.getString(Constants.USER_INFO_GENDER, "f");
//        float weight = sPreferences.getFloat(Constants.USER_INFO_WEIGHT, 70);
//        float height = sPreferences.getFloat(Constants.USER_INFO_HEIGHT, 1.72f);
//        float birthday = sPreferences.getFloat(Constants.USER_INFO_BIRTHDAY, 1980);
//        boolean gender = true;
//        if (sexString.equals("f")) {
//            gender = false;
//        }
//
//        ServiceLauncher.saveProfile(gender, (int) birthday, (int) (height * 100), weight);
//        ServiceLauncher.saveGoal(calories, steps);
//    }

//    public static int getGoalSteps() {
//        return getGoalSteps(null);
//    }

//    public static int getGoalSteps(DailyStats dailyStats) {
//        int goalSteps = getUserPreferences().getInt(Constants.USER_GOAL_STEPS, 200);
//        if (dailyStats != null) {
//            goalSteps = dailyStats.getGoalSteps();
//        }
//        return goalSteps;
//    }

//    public static int getGoalCalories() {
//        return getGoalCalories(null);
//    }

//    public static int getGoalCalories(DailyStats dailyStats) {
//        int goalCalories = getUserPreferences().getInt(Constants.USER_GOAL_CALORIES, 200);
//        if (dailyStats != null) {
//            goalCalories = dailyStats.getGoalCalories();
//        }
//        return goalCalories;
//    }

    public static void showMsg( String msgString ) {

    }

    public static double getSpeed( double stepsPerS, double height ) {
        return 0.25 * stepsPerS * height * stepsPerS;
    }

    public static double getStepDistance( double steps, double height, double timeInterval ) {
        if (steps / timeInterval <= 1.68) {
            return steps * height * 0.42;
        }
        double strideLength = 0.25 * steps / timeInterval * height;
        return strideLength * steps;
    }

    public static double getCalorie( double steps, double timeInterval, double weight, double height, double age, boolean male ) {
        if (steps == 0 || timeInterval == 0) {
            return 0;
        }

        double ageFactor = 1;
        if (age <= 16) {
            ageFactor = 0.9;
        } else if (age > 16 && age <= 30) {
            ageFactor = 1;
        } else if (age > 30 && age <= 50) {
            ageFactor = 0.9;
        } else if (age > 50) {
            ageFactor = 0.8;
        }

        double genderFactor = 1;
        if (!male) {
            genderFactor = 0.9;
        }

        double stepPerS = steps / timeInterval;

        // double mileDistancePerH = speed * 3600 / 1609.344;
        //
        // if(mileDistancePerH < 2)
        // {
        // mileDistancePerH = 2;
        // }

        double result = 0;
        if (stepPerS < 1.68) {
            result = (0.0004792953 * height - 0.000265846) * weight * steps;
        } else {
            result = 0.0002852948 * (stepPerS * stepPerS * height - 1.56547328256) * weight * timeInterval;
        }

        // double result = 1.83655 * (mileDistancePerH - 0.875466) * weight *
        // timeInterval / 3600;

        // double speed = getSpeed(stepPerS, height);

        if (result < 0) {
            return 0;
        }

        result *= ageFactor;
        result *= genderFactor;

        return result;
    }

    public static int getCalorie( double steps) {
        return (int)steps/20;
    }

    public static long swapEndian( long i ) {
        long b0, b1, b2, b3, b4, b5, b6, b7;

        b0 = (i & 0xffL) >> 0;
        b1 = (i & 0xff00L) >> 8;
        b2 = (i & 0xff0000L) >> 16;
        b3 = (i & 0xff000000L) >> 24;
        b4 = (i & 0xff00000000L) >> 32;
        b5 = (i & 0xff0000000000L) >> 40;
        b6 = (i & 0xff000000000000L) >> 48;
        b7 = (i & 0xff00000000000000L) >> 56;

        return ((b0 << 56) | (b1 << 48) | (b2 << 40) | (b3 << 32) | (b4 << 24) | (b5 << 16) | (b6 << 8) | (b7 << 0));
    }

    public static String formatDouble2( double number ) {
        return new java.text.DecimalFormat("#0.0").format(number);
    }

    public static String getKM( double number ) {
        return formatDouble2(number / 1000);
    }

    public static String getCal( double number ) {
        if (number > 100) {
            return String.valueOf((int) number);
        } else {
            return formatDouble2(number);
        }
    }

    public static String getKMhFromMs( double number ) {
        return formatDouble2(number * 3.6);
    }

    public static String getDurationStringBySeconds( double seconds, String hourUnit, String minUnit ) {
        int duration = (int) seconds;
        String durationString = "";
        if (duration >= 0) {
            int hour = duration / 3600;
            int minute = (duration % 3600) / 60;
            if (hour > 0) {
                durationString += "" + hour + hourUnit;
            }
            durationString += minute + minUnit;
        }

        return durationString;
    }

    public static boolean isEmpty( String str ) {
        return str == null || str.isEmpty() || str.equals("null") || str.equals("");
    }

    /**
     * 根据登录状态和性别获取默认的头像id
     *
     * @return drawable id
     */
//    public static int getDefaultAvatarImage() {
//        int avatarImage = R.drawable.avatar_00;
//        if (isLogin()) {
//            if (userGender()) {
//                avatarImage = R.drawable.avatar_10;
//            } else {
//                avatarImage = R.drawable.avatar_11;
//            }
//        } else {
//            if (userGender()) {
//                avatarImage = R.drawable.avatar_00;
//            } else {
//                avatarImage = R.drawable.avatar_01;
//            }
//        }
//        return avatarImage;
//    }
    public static boolean isLogin() {
        SharedPreferences sPreferences = Util.getUserPreferences();
        String nickname = sPreferences.getString(Constants.USER_INFO_NICKNAME, "");

        return !Util.isEmpty(nickname);
    }


    /**
     * 第三方登录 登出logout
     */
//    public static void logout() {
//        SharedPreferences sPreferences = Util.getUserPreferences();
//
//        Editor editor = sPreferences.edit();
//        editor.putInt(Constants.USER_INFO_USERID, 0);
//        editor.putString(Constants.USER_INFO_NICKNAME, null);
//        editor.putString(Constants.USER_INFO_AVATARURL, null);
//
//        // 重置绑定帐号
//        editor.putBoolean(Constants.USER_INFO_SINA, false);
//        editor.putBoolean(Constants.USER_INFO_QQ, false);
//        editor.putBoolean(Constants.USER_INFO_EMAIL, false);
//        editor.putBoolean(Constants.USER_INFO_MOBILE, false);
//
//        editor.commit();
//
//        ThirdLoginOAuth.getInstance().logout(context_);
//    }

    private static String fileDir_;

    public static String fileDirectory() {
        if (fileDir_ == null) {
            String fileDir = Util.context().getFilesDir().getAbsolutePath() + File.separator + "ledongli";
            File file = new File(fileDir);
            if (!file.exists()) {
                file.mkdir();
            }
            fileDir_ = (fileDir);
        }

        return fileDir_;
    }

    // Bitmap → byte[]
    public static byte[] Bitmap2Bytes( Bitmap bm ) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 90, baos);

        int options = 90;
        while (baos.toByteArray().length / 1024 > 60 && options > 30) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
            options -= 5;// 每次都减少5
            baos.reset();// 重置baos即清空baos
            bm.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
        }

        return baos.toByteArray();
    }

    // byte[] → Bitmap
    public static Bitmap Bytes2Bimap( byte[] b ) {
        if (b.length != 0) {
            return BitmapFactory.decodeByteArray(b, 0, b.length);
        } else {
            return null;
        }
    }

    // load bitmap by filename
    public static Bitmap loadFromFile( String filename ) {
        try {
            File f = new File(filename);
            if (!f.exists()) {
                return null;
            }
            Bitmap tmp = BitmapFactory.decodeFile(filename);
            return tmp;
        } catch (Exception e) {
            return null;
        }
    }

    // load bitmap by file
    public static Bitmap loadFromFile( File file ) {
        try {
            if (!file.exists()) {
                return null;
            }
            Bitmap tmp = BitmapFactory.decodeFile(file.getAbsolutePath());
            return tmp;
        } catch (Exception e) {
            return null;
        }
    }

    public static byte[] convertFileToByteArray( File f ) {
        byte[] byteArray = null;
        try {
            InputStream inputStream = new FileInputStream(f);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] b = new byte[1024 * 8];
            int bytesRead = 0;

            while ((bytesRead = inputStream.read(b)) != -1) {
                bos.write(b, 0, bytesRead);
            }

            byteArray = bos.toByteArray();
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return byteArray;
    }

    public static boolean hasSdcard() {
        String status = Environment.getExternalStorageState();
        return status.equals(Environment.MEDIA_MOUNTED);
    }

    private static String version = null;

    public static String getVersion() {
        if (version == null) {
            try {
                version = Util.context().getPackageManager().getPackageInfo(Util.context().getPackageName(), 0).versionName;

            } catch (Exception e) {
                // TODO: handle exception
            }
        }

        return version == null ? "" : version;
    }


    /**
     * author Guo Fuming
     * 检查设备是否含有底部虚拟按键
     *
     * @param activity
     * @return
     */
    public static boolean checkDeviceHasNavigationBar( Context activity ) {

        boolean hasMenuKey = ViewConfiguration.get(activity).hasPermanentMenuKey();
        boolean hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);

        return !hasMenuKey && !hasBackKey;

    }

    /**
     * author Guo Fuming
     * 返回设备底部虚拟按键的高度
     *
     * @param activity
     * @return
     */
    public static int getNavigationBarHeight( Activity activity ) {

        Resources resources = activity.getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");

        int height = resources.getDimensionPixelOffset(resourceId);
        return height;
    }


    /**
     * author Guo Fuming
     * 返回设备顶部通知栏的高度
     *
     * @param activity
     * @return
     */
    public static int getStatusBarHeight( Activity activity ) {

        Resources resources = activity.getResources();
        int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");

        int height = resources.getDimensionPixelOffset(resourceId);
        return height;
    }

    /**
     * author Guo Fuming
     *
     * @param resources
     * @param dp
     * @return
     */
    public static float dp2px( Resources resources, float dp ) {
        final float scale = resources.getDisplayMetrics().density;
        return dp * scale + 0.5f;
    }

    /**
     * author Guo Fuming
     *
     * @param resources
     * @param sp
     * @return
     */
    public static float sp2px( Resources resources, float sp ) {
        final float scale = resources.getDisplayMetrics().scaledDensity;
        return sp * scale;
    }


    /**
     *
     */
    public static int getFixedBpm( int bpm ) {
        if (bpm >= 190) {
            return 190;
        } else if (bpm <= 60) {
            return 60;
        } else {
            return bpm;
        }
    }


    public static String getTimeForShow( int duration ) {
        String hour = String.format("%02d", duration / 3600);
        String minute = String.format("%02d", (duration / 60) % 60);
        return (hour + ":" + minute);
    }


    public static int getScreenWidth() {
        WindowManager wm = (WindowManager) context_.getSystemService(Context.WINDOW_SERVICE);

        screenWidth = wm.getDefaultDisplay().getWidth();
        return screenWidth;
    }

//    public static int setScreenWidth() {
//        return screenWidth;
//    }

    public static String generateFileName( String url ) {
        if (fileNameGenerator == null) {
            fileNameGenerator = new Md5FileNameGenerator();

        }
        return fileNameGenerator.generate(url);
    }

    public static String getPaceValue( double speed ) {
        if (speed == 0) {
            return "" + "0" + "\'" + "00" + "\"";
        } else {
            String paceResult;
            int min = (int) (1000 / speed / 60);
            int seconds = (int) (1000 / speed % 60);
            paceResult = "" + min + "\'" + String.format("%02d", seconds) + "\"";
            return paceResult;
        }

    }

    public static String getClockShowTime( int duration ) {
        if (duration == 0) {
            return "" + "0" + ":" + "00" + ":" + "00";
        } else {
            String timeResult;
            int hour = duration / 3600;
            int minute = (duration % 3600) / 60;
            int seconnd = duration % 3600 % 60;
            timeResult = "" + hour + ":" + String.format("%02d", minute) + ":" + String.format("%02d", seconnd);
            return timeResult;
        }
    }

    public final class TempoComparator implements Comparator<Music> {

        @Override
        public int compare( Music lhs, Music rhs ) {
            return compareLong(lhs.tempo, rhs.tempo);
        }

        private int compareLong( int first, int second ) {
            return (first < second) ? -1 : ((first == second) ? 0 : 1);
        }
    }


}
