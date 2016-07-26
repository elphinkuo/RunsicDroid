package com.runningmusic.utils;

/**
 * Created by guofuming on 18/1/16.
 */
public class Constants {

    public static Boolean TIME_LINE_MODEL = true;

    public static String SERVER_IP;
    public static String SERVER_STORE;
    public static String PL_SERVER_IP;
    public static String SOCIAL_IP;
    public static String DOGOOD_SERVER;
    public static String DODOOD_RECEIVE_URL;
    public static String BACKUP_QUERY_URL;
    public static String BACKUP_UPLOAD_URL;
    public static String BACKUP_DOWNLOAD_URL;
    public static String MALL_KEY;
    public static String SECURITY_IP;
    public static String QQ_APPID;
    public static String QQ_APPSECRET;
    public static String LEDONGLI_VERSION_NUMBER = "LEDONGLI_VERSION_NUMBER";

    /*
     * this is for Running Music
     */
//    public static String BASE_URL = "http://facelending.com:83";
//    public static String BASE_URL = "http://api.runningmusic.cn";
    /**
     * qiuxiang test
     */
    public static String access_token;
    public static String BASE_URL = "http://10.15.27.212:5000";
    public static String PLAYLIST_URL = BASE_URL + "/api/playlist/";
    public static String MUSIC_IN_PLAYLIST_URL = BASE_URL + "/api/playlist";
    public static final String LOGIN_URL = BASE_URL + "/api/app_auth/";
    public static final String CHANNEL_URL = BASE_URL + "/api/channel/";
    public static final String LOGOUT_URL = BASE_URL + "/api/user/logout/";
    public static final String USER_PROFILE_URL = BASE_URL + "/api/user/profile/";
    public static final String USER_MUSIC_URL = BASE_URL + "/api/user/music/";
    public static final String USER_HISTORY_URL = BASE_URL + "/api/user/history/";
    public static final String MUSIC_ON_TEMPO = BASE_URL + "/api/playontempo/";
    public static final String PLAY_LIST_ON_TEMPO = BASE_URL + "/api/playlistontempo/";
    public static final String PLAY_ON_TEMPO = BASE_URL + "/api/playontempo/";

    public static final String PLAYLIST_GROUP = BASE_URL + "/playlist_group/";

    public static final String CREATE_USER = BASE_URL + "/user/auth";
    public static final String GET_USER_INFO = BASE_URL + "/user/info";
    public static final String USER_FAV_MUSIC = BASE_URL + "/user/favorites_music";

    public static final String USER_LIKE_MUSIC = BASE_URL + "/music";

    public static final String USER_SPORT_SAVE = BASE_URL + "/user/sport";

    public static final String START_PAGE_AD = BASE_URL + "/app/start_page";
    public static final String EVENT_LIST_URL = BASE_URL + "/app/activities";

    public static final String PGC_MUSIC_LIST = BASE_URL + "/api/playlist/5625e85d0307b34b1d881857/";

    public static final String TENCENT_APP_URL_OLD = "http://qzs.qq.com/open/yyb/good_new_app/details.html?id=614570823670180785125&appid=42248887&from=timeline&isappinstalled=0&g_f=&srctype=&ticket=";







    static {
        switch (2) {
            case 1: // xq_test 上线测试版（正式版数据库,尽量由工程师去帮助修改）
//                SERVER_STORE = "http://xq.ledongli.cn/xq_test/mall.ashx";
//                MALL_KEY = "F8684F8A391C6B8B950C4D7F5D6E3826";
//                SERVER_IP = "http://core.api.ledongli.cn/xq_test/io.ashx";
//                PL_SERVER_IP = "http://core.api.ledongli.cn/xq_test/io.ashx";
//                SECURITY_IP = "https://secure-api.ledongli.cn/xq_test/io.ashx";
//                SOCIAL_IP = "http://social.api.ledongli.cn:8090/xq/index.php";
//                DOGOOD_SERVER = "http://test.imore.net:2013/walks/api/authUser";
//                DODOOD_RECEIVE_URL = "http://core.api.ledongli.cn/xq_test/donate.ashx";
//                BACKUP_QUERY_URL = "http://core.api.ledongli.cn/xq_test/io.ashx";
//                BACKUP_UPLOAD_URL = "http://core.api.ledongli.cn/xq_test/io.ashx";
//                BACKUP_DOWNLOAD_URL = "http://core.api.ledongli.cn/xq_test/io.ashx";
                QQ_APPID = "100481185";
                QQ_APPSECRET = "f586d6abab308f3ae79bf001ccdf92cc";
                break;
            case 2: // 正式版
//                SERVER_STORE = "http://xq.ledongli.cn/xq/mall.ashx";
//                MALL_KEY = "F8684F8A391C6B8B950C4D7F5D6E3826";
//                SERVER_IP = "http://core.api.ledongli.cn/xq/io.ashx";
//                PL_SERVER_IP = "http://pl.api.ledongli.cn/xq/io.ashx";
//                SECURITY_IP = "https://secure-api.ledongli.cn/xq/io.ashx";
//                SOCIAL_IP = "http://social.api.ledongli.cn:8090/xq/index.php";
//                DOGOOD_SERVER = "http://www.ixingshan.org/api/authUser";
//                DODOOD_RECEIVE_URL = "http://core.api.ledongli.cn/xq/donate.ashx";
//                BACKUP_QUERY_URL = "http://dailystorage.api.ledongli.cn/xq/io.ashx";
//                BACKUP_UPLOAD_URL = "http://dailystorage-up.api.ledongli.cn/xq/io.ashx";
//                BACKUP_DOWNLOAD_URL = "http://dailystorage-down.api.ledongli.cn/xq/io.ashx";
                QQ_APPID = "100481185";
                QQ_APPSECRET = "f586d6abab308f3ae79bf001ccdf92cc";
                break;
            case 0: // 测试版
            default:
//                SERVER_STORE = "http://xq.ledongli.cn/staging/mall.ashx";
//                MALL_KEY = "123qwe456";
//                SERVER_IP = "http://core.api.ledongli.cn/staging/io.ashx";
//                PL_SERVER_IP = "http://core.api.ledongli.cn/staging/io.ashx";
//                SECURITY_IP = "https://secure-api.ledongli.cn/staging/io.ashx";
//                SOCIAL_IP = "http://social.api.ledongli.cn:8090/staging/index.php";
//                DOGOOD_SERVER = "http://test.imore.net:2013/walks/api/authUser";
//                DODOOD_RECEIVE_URL = "http://core.api.ledongli.cn/staging/donate.ashx";
//                BACKUP_QUERY_URL = "http://core.api.ledongli.cn/staging/io.ashx";
//                BACKUP_UPLOAD_URL = "http://core.api.ledongli.cn/staging/io.ashx";
//                BACKUP_DOWNLOAD_URL = "http://core.api.ledongli.cn/staging/io.ashx";
                QQ_APPID = "101107495";
                QQ_APPSECRET = "269d1b4f64685ddba81839565b70124e";
                break;

        }
    }

    public static final String PM2D5_URL = "http://xq.ledongli.cn:8082/getpm.aspx";

    // 统一的编码格式
    public static String CHARSET = "UTF-8";

    public static String VIEW_PAGER_REFRESH_TIME = "VIEW_PAGER_REFRESH_TIME";
    public static String VIEW_PAGER_CURRENT_ITEM = "VIEW_PAGER_CURRENT_ITEM";
    public static String LOCATION_SETTING_TIME = "LOCATION_SETTING_TIME";
    public static String XIAOMI_SETTING_TIME = "XIAOMI_SETTING_TIME";
    public static String BACKUP_TIP = "BACKUP_TIP";

    // 锻炼时间提醒
    public static String IS_DAILY_NOTIFY = "IS_DAILY_NOTIFY";
    public static String DAILY_NOTIFY_HOUR = "DAILY_NOTIFY_HOUR";
    public static String DAILY_NOTIFY_MINUTE = "DAILY_NOTIFY_MINUTE";
    public static String FROM_ACTIVITY = "FROM_ACTIVITY";

    // 设置保存用户信息的key
    public static String IS_FIRSTTIME_START = "IS_FIRSTTIME_START";
    public static String USER_INFO_GUIDE = "USER_INFO_GUIDE";
    public static String SHOW_WEBVIEW = "SHOW_WEBVIEW";
    public static String STORE_RECOMMEND = "STORE_RECOMMEND";
    public static String DEVICE_ID = "DEVICE_ID";
    public static String CURRENT_SWITCH_TYPE = "CURRENT_SWITCH_TYPE";// 当前选择的类型
    public static String USER_INFO_USERID = "USER_INFO_USERID";
    public static String USER_INFO_NICKNAME = "USER_INFO_NICKNAME";
    public static String USER_INFO_AVATARURL = "USER_INFO_AVATARURL";
    public static String USER_INFO_LOCATIONSTR = "USER_INFO_LOCATIONSTR";
    // 用户绑定账户
    public static String USER_INFO_QQ = "USER_INFO_QQ";
    public static String USER_INFO_SINA = "USER_INFO_SINA";
    public static String USER_INFO_EMAIL = "USER_INFO_EMAIL";
    public static String USER_INFO_MOBILE = "USER_INFO_MOBILE";

    // 是否更新用户信息
    public static String UPDATE_INFO_KEY = "UPDATE_INFO_KEY";
    // 用户的身体特征
    public static String USER_INFO_GENDER = "USER_INFO_GENDER";
    public static String USER_INFO_WEIGHT = "USER_INFO_WEIGHT";
    public static String USER_INFO_HEIGHT = "USER_INFO_HEIGHT";
    public static String USER_INFO_BIRTHDAY = "USER_INFO_BIRTHDAY";
    public static String USER_INIT_WEIGHT = "USER_INIT_WEIGHT";
    public static String USER_INIT_TIME = "USER_INIT_TIME";
    // 用户的目标设置
    public static String USER_GOAL_STEPS = "USER_GOAL_STEPS";
    public static String USER_GOAL_CALORIES = "USER_GOAL_CALORIES";
    // 上传device的状态
    public static String USER_DEVICE_INFO_STATUS = "USER_DEVICE_INFO_STATUS";
    // 乐动力保存的版本号
    public static String INSTALL_SOURCE = "INSTALL_SOURCE";
    public static String INSTALL_TIME = "INSTALL_TIME";
    // 个推的client id
    public static String MOBILE_PUSH_TOKEN = "MOBILE_PUSH_TOKEN";
    // 第一个rgm卡片产生时间 推送有关
    public static String FIRST_RGM_TIME = "FIRST_RGM_TIME";
    public static String RUNNING_ACTIVITY_TIME = "RUNNING_ACTIVITY_TIME";
    // 流量统计
    public static String WIFI_FOREGROUND_BYTES = "WIFI_FOREGROUND_BYTES";
    public static String WIFI_BACKGROUND_BYTES = "WIFI_BACKGROUND_BYTES";
    public static String MOBILE_FOREGROUND_BYTES = "MOBILE_FOREGROUND_BYTES";
    public static String MOBILE_BACKGROUND_BTYES = "MOBILE_BACKGROUND_BYTES";

    public static final double DISTANT_FUTURE = 64092211200.0;
    // weight = 70;
    // height = (float) 1.75;
    // birthday = 1988;
    // 高德
    public static final int ERROR = 1001;
    public static final int REOCODER_RESULT = 3000;

    /* 头像请求码 */
    public static final int IMAGE_REQUEST_CODE = 3000; // 本地图片
    public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 3001; // 照相机
    public static final int RESULT_REQUEST_CODE = 3002;
    public static final int PHOTO_CROP_REQUEST_CODE = 3003;

    // NEW YEAR TARGET
    public static String NEW_YEAR_TARGET = "NEW_YEAR_TARGET";
    public static String NEW_YEAR_POP_UP = "NEW_YEAR_POP_UP";

    // PROFILE
    public static String PROFILE_SINGATURE = "PROFILE_SINGATURE";
    public static String PROFILE_HOBBY1 = "PROFILE_HOBBY1";
    public static String PROFILE_HOBBY2 = "PROFILE_HOBBY2";
    public static String PROFILE_HOBBY3 = "PROFILE_HOBBY3";
    public static String PROFILE_HOBBY4 = "PROFILE_HOBBY4";
    public static String PROFILE_SELECTED_SPORTS_COUNTS = "PROFILE_SELECTED_SPORTS_COUNTS";

    // backup
//    public static String BACKUPSTATE = "com.runningmusic.broadcast.backupstate";
//    public static String RECOVERYSTATE = "com.runningmusic.broadcast.recoverystate";
    public static String STEPSTATE = "com.runningmusic.broadcast.stepstate";
    public static String MESSAGE_STEPSTATE = "stepstate";

    public static String SONG_CHANGED_ON_TEMPLE = "com.ruuningmusic.songchangedontemple";


    // widget是否被添加到桌面
    public static String WIDGET_ENABLE = "WIDGET_ENABLE";

    // 第三方应用访问
    public static String OUTSIDE_APP = "outside_app";

    public static String IFMANUALSPORT = "ifmanualsport";

    // error code
    /**
     * 用户验证失败
     */
    public static int USER_VERIFIED_ERROR = 1;
    /**
     * 网络不给力
     */
    public static int NETWORK_ERROR = -1;

}

