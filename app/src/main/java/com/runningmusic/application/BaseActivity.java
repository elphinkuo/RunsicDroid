package com.runningmusic.application;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Debug;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.runningmusic.runninspire.R;
import com.runningmusic.utils.Constants;
import com.runningmusic.utils.Util;
import com.umeng.analytics.MobclickAgent;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


@SuppressLint("InflateParams")
public class BaseActivity extends LifecycleActivity {

    protected AQuery query_;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        // setTheme(SampleList.THEME); //Used for theme switching in samples
//        actionBarSetting(getActionBar());
        setWindowContentOverlayCompat();

        // 初始化过程中云同步按钮保持invisible
        setProgressBarIndeterminateVisibility(false);

        initActivity();
    }

    private void initActivity() {
        query_ = new AQuery(this);

    }

    /**
     * Set the window content overlay on device's that don't respect the theme
     * attribute.
     */
    private void setWindowContentOverlayCompat() {
        if (Build.VERSION.SDK_INT == 18) {// Build.VERSION_CODES.JELLY_BEAN_MR2
            // == 18 os 4.3
            // Get the content view
            View contentView = findViewById(android.R.id.content);

            // Make sure it's a valid instance of a FrameLayout
            if (contentView instanceof FrameLayout) {
                TypedValue tv = new TypedValue();

                // Get the windowContentOverlay value of the current theme
                if (getTheme().resolveAttribute(android.R.attr.windowContentOverlay, tv, true)) {

                    // If it's a valid resource, set it as the foreground
                    // drawable
                    // for the content view
                    if (tv.resourceId != 0) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            contentView.setForeground(getResources().getDrawable(tv.resourceId));
                        }
                        // ((FrameLayout)
                        // contentView).setForegroundGravity(tv.resourceId);

                    }
                }
            }
        }
    }

    /*
     * 消息提示
     */
    protected Toast toast = null;

    public void showMsg(String msg) {
        showMsg(msg, Toast.LENGTH_SHORT);
    }

    public void showMsg(String msg, int duration) {
        if (toast == null) {
            toast = Toast.makeText(this, msg, duration);
        } else {
            // toast.cancel();
            toast.setText(msg);
            toast.setDuration(duration);
        }
        toast.show();
    }

    String TAG = "FM----";

    public void showMemory() {
        ActivityManager activityManager = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(memoryInfo);

        Log.i(TAG, " memoryInfo.availMem " + memoryInfo.availMem + "\n");
        Log.i(TAG, " memoryInfo.lowMemory " + memoryInfo.lowMemory + "\n");
        Log.i(TAG, " memoryInfo.threshold " + memoryInfo.threshold + "\n");

        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = activityManager.getRunningAppProcesses();

        Map<Integer, String> pidMap = new TreeMap<Integer, String>();
        for (ActivityManager.RunningAppProcessInfo runningAppProcessInfo : runningAppProcesses) {
            pidMap.put(runningAppProcessInfo.pid, runningAppProcessInfo.processName);
        }

        Collection<Integer> keys = pidMap.keySet();

        for (int key : keys) {
            int pids[] = new int[1];
            pids[0] = key;
            android.os.Debug.MemoryInfo[] memoryInfoArray = activityManager.getProcessMemoryInfo(pids);
            for (android.os.Debug.MemoryInfo pidMemoryInfo : memoryInfoArray) {

                Log.i(TAG, String.format("** MEMINFO in pid %d [%s] **\n", pids[0], pidMap.get(pids[0])));
                Log.i(TAG, " pidMemoryInfo.getTotalPrivateDirty(): " + pidMemoryInfo.getTotalPrivateDirty() + "\n");
                Log.i(TAG, " pidMemoryInfo.getTotalPss(): " + pidMemoryInfo.getTotalPss() + "\n");
                Log.i(TAG, " pidMemoryInfo.getTotalSharedDirty(): " + pidMemoryInfo.getTotalSharedDirty() + "\n");
            }
        }
    }

    // 获得系统进程信息
    public void getRunningAppProcessInfo() {
        ActivityManager mActivityManager = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);

        // ProcessInfo Model类 用来保存所有进程信息
        // processInfoList = new ArrayList<ProcessInfo>();

        // 通过调用ActivityManager的getRunningAppProcesses()方法获得系统里所有正在运行的进程
        List<ActivityManager.RunningAppProcessInfo> appProcessList = mActivityManager.getRunningAppProcesses();

        for (ActivityManager.RunningAppProcessInfo appProcessInfo : appProcessList) {
            if (!appProcessInfo.processName.equals("cn.ledongli.ldl")) {
                continue;
            }
            // 进程ID号
            int pid = appProcessInfo.pid;
            // 用户ID 类似于Linux的权限不同，ID也就不同 比如 root等
            int uid = appProcessInfo.uid;
            // 进程名，默认是包名或者由属性android：process=""指定
            String processName = appProcessInfo.processName;
            // 获得该进程占用的内存
            int[] myMempid = new int[] { pid };
            // 此MemoryInfo位于android.os.Debug.MemoryInfo包中，用来统计进程的内存信息
            Debug.MemoryInfo[] memoryInfo = mActivityManager.getProcessMemoryInfo(myMempid);
            // 获取进程占内存用信息 kb单位
            int memSize = memoryInfo[0].dalvikPrivateDirty;
            int totalSize = memoryInfo[0].getTotalPrivateDirty();
            String msg = "processName: " + processName + "  pid: " + pid + " uid:" + uid + " memorySize is -->" + memSize + "kb" + "totalPrivateDirty -->"
                    + totalSize;
            Log.e(TAG, msg);
            showMessageDialog(msg);
            // 构造一个ProcessInfo对象
            // ProcessInfo processInfo = new ProcessInfo();
            // processInfo.setPid(pid);
            // processInfo.setUid(uid);
            // processInfo.setMemSize(memSize);
            // processInfo.setPocessName(processName);
            // processInfoList.add(processInfo);

            // 获得每个进程里运行的应用程序(包),即每个应用程序的包名
            String[] packageList = appProcessInfo.pkgList;
            Log.e(TAG, "process id is " + pid + "has " + packageList.length);
            for (String pkg : packageList) {
                Log.e(TAG, "packageName " + pkg + " in process id is -->" + pid);
            }
            break;
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void onStart() {
        super.onStart();
        // if (Log.LOG) {
        // getRunningAppProcessInfo();
        // }
        // if(EnvironmentDetector.getInstance().isAccelerometerFrozen() ==
        // EnvironmentDetector.STATUS_TRUE)
        // {
        // showImageDialog();
        // }
//        String tempStr = MobclickAgent.getConfigParams(this, "SHOW_WEBVIEW");
//
//
//        if (Util.isEmpty(tempStr))
//            tempStr = "0";
//        int showWebViewOnline = 0;
//        try {
//            showWebViewOnline = Integer.parseInt(tempStr);
//        } catch (Exception e) {
//
//        }
        SharedPreferences sPreferences = Util.getUserPreferences();
        int showWebViewOffline = sPreferences.getInt(Constants.SHOW_WEBVIEW, 0);
//        if (showWebViewOnline == 1 && showWebViewOffline == 0) {
//            Intent intent = new Intent();
////            intent.setClass(this, ShowWebViewActivity.class);
//            startActivity(intent);
//        }
        SharedPreferences.Editor editor = sPreferences.edit();
//        editor.putInt(Constants.SHOW_WEBVIEW, showWebViewOnline);
        editor.commit();
    }

    public void tapCancel(View view) {
        finish();
        // overridePendingTransition(0, R.anim.fade_out);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i(TAG, "onRestart cancelNotification");
//        LedongliGexinSdkMsgReceiver.cancelNotification();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void showMessageDialog(String msg) {
        AlertDialog.Builder MessageDialog = new AlertDialog.Builder(this);
        MessageDialog.setTitle("温馨提示");
        MessageDialog.setMessage(msg);
        MessageDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                arg0.dismiss();

            }
        });
        MessageDialog.create().show();
    }

//    @SuppressLint("InflateParams")
//    public void showImageDialog() {
//
//        AlertDialog.Builder MessageDialog = new AlertDialog.Builder(this);
//        final View viewDia = LayoutInflater.from(this).inflate(R.layout.custom_dialog, null);
//        TextView textView = (TextView) viewDia.findViewById(R.id.textView_device_desc);
//        textView.setText("设备：" + EnvironmentDetector.getInstance().getDeviceBrand() + " " + EnvironmentDetector.getInstance().getDeviceModel() + " android "
//                + EnvironmentDetector.getInstance().getDeviceOSVersion());
//        ((TextView) viewDia.findViewById(R.id.textView_reason)).setText("原因：系统不允许后台计步");
//        MessageDialog.setView(viewDia);
//
//        MessageDialog.setTitle("手机不兼容乐动力");
//        // MessageDialog.setMessage(msg);
//        MessageDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
//
//            @Override
//            public void onClick(DialogInterface arg0, int arg1) {
//                arg0.dismiss();
//
//            }
//        });
//        MessageDialog.create().show();
//    }

    /**
     * show dialog hide dialog
     */
    private Dialog progressDialog_ = null;
    private ProgressBar progressBar_;

    private boolean synValue = false;

    synchronized protected boolean showDialog() {
        if (synValue) {// 已经出现过
            return true;
        }

        showDialog("");
        synValue = true;

        return false;
    }

    protected void showDialog(String processString) {
        if (progressDialog_ == null) {
            progressDialog_ = getProgressDialog(processString);
        }
        progressDialog_.show();
    }

    synchronized protected void hideDialog() {
        if (progressDialog_ != null) {
            progressDialog_.dismiss();
        }
        synValue = false;
    }

    @SuppressLint("InflateParams")
    private Dialog getProgressDialog(String processString) {
        Dialog progressDialog = null;
        progressDialog = new Dialog(this, R.style.ProgressDialog1);
        View view = LayoutInflater.from(this).inflate(R.layout.progressdialog_indicator, null);
        AQuery aQuery = new AQuery(view);
        aQuery.id(R.id.processTxt).text("" + processString);
        progressBar_ = (ProgressBar) aQuery.id(R.id.progress_bar_sending).getView();
        progressBar_.setVisibility(View.VISIBLE);
        progressDialog.setContentView(view);
        progressDialog.setCancelable(false);
        return progressDialog;
    }

    // ActionBar 设置
//    public void actionBarSetting(ActionBar actionBar) {
//        actionBar.setDisplayHomeAsUpEnabled(true);
//        actionBar.setDisplayShowHomeEnabled(false);
//        actionBar.setDisplayShowTitleEnabled(true);
//    }

}
