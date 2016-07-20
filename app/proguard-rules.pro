# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/guofuming/Library/Android/sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

-ignorewarnings

-dontwarn java.awt.**

-dontwarn android.support.v4.**
-keep class android.support.v4.** { *; }
-keep class android.support.v7.** { *; }
-keep public class * extends android.support.v4.**
-keep public class * extends android.app.Fragment
-keep public class * extends android.support.v4.app.Fragment
-keep public class * extends android.support.v4.app.FragmentActivity

-keepattributes **
-keep class !android.support.v7.internal.view.menu.**,** {*;}
-dontpreverify
-dontoptimize
-dontshrink
-dontwarn **
-dontnote **
-keep class android.support.design.widget.** { *; }
-keep interface android.support.design.widget.** { *; }

-keep class android.support.design.widget.** { *; }
-keep interface android.support.design.widget.** { *; }
-keep public class * extends android.support.design.widget

#-dontwarn android.support.design.**

-keep class com.amap.api.maps.** {*;}
-keep class com.amap.api.search.** {*;}
#-keepclasseswithmembers class class com.amap.api.maps.**{*;}

#-keepclasseswithmembers class com.amap.api.search.**{*;}

-keep class com.google.protobuf.** {*;}
-keep class org.fusesource.hawtjni.runtime.** {*;}
-keep class org.fusesource.leveldbjni.** {*;}
-keep class org.fusesource.leveldbjni.internal.** {*;}
-keep class org.iq80.leveldb.* {*;}

-keep class com.runningmusic.fragment.MusicList { *; }

-keep public interface com.tencent.**

-keep public interface com.umeng.socialize.**
-keep public interface com.umeng.socialize.sensor.**

-keep class com.umeng.scrshot.**
-keep public class com.tencent.** {*;}
-keep class com.umeng.socialize.sensor.**
-keep class com.umeng.socialize.handler.**
-keep class com.umeng.socialize.handler.*
-keep class com.tencent.mm.sdk.modelmsg.WXMediaMessage {*;}
-keep class com.tencent.mm.sdk.modelmsg.** implements com.tencent.mm.sdk.modelmsg.WXMediaMessage$IMediaObject {*;}
-keep public class com.umeng.socialize.* {*;}
-keep public class javax.**
-keep public class android.webkit.**

-keep class com.tencent.** {*;}
-dontwarn com.tencent.**

-keep class com.tencent.open.TDialog$*
-keep class com.tencent.open.TDialog$* {*;}
-keep class com.tencent.open.PKDialog
-keep class com.tencent.open.PKDialog {*;}
-keep class com.tencent.open.PKDialog$*
-keep class com.tencent.open.PKDialog$* {*;}


 -keep class com.amap.api.**  {*;}
 -keep class com.autonavi.**  {*;}
 -keep class com.a.a.**  {*;}

-keepattributes Signature
-keep class com.alibaba.fastjson.JSON { *; }
-keep class com.alibaba.fastjson.JSONObject {*;}
-keep class com.alibaba.fastjson.**

-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int i(...);
    public static int w(...);
    public static int d(...);
    public static int e(...);
}

-keepclassmembers class * {
	public <methods>;
}

-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

-keepclassmembers class * {
   public <init>(org.json.JSONObject);
}
-keep public class * extends com.umeng.**
-keep class com.umeng.** { *; }




-keepclasseswithmembers class * {
    native <methods>;
}

-dontskipnonpubliclibraryclassmembers
-dontshrink
-dontoptimize

-keepattributes SourceFile,LineNumberTable

#-keep public class * extends android.support.design.widget.CoordinatorLayout{
#    *;
#}
#-keep public class * extends android.support.design.widget.CollapsingToolbarLayout{
#    *;
#}
#-keep public class * extends android.support.design.widget.AppBarLayout {
#    *;
#}

-keep public class com.runningmusic.runninspire.R$*{
    public static final int *;
}

