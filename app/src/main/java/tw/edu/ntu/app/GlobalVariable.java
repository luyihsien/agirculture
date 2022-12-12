package tw.edu.ntu.app;


import android.app.Application;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

public class GlobalVariable extends Application {
    private static String TAG = "GlobalVariable";
    private String token;
    private String android_id;
    private String weather_token;
    private String web_api_url;

    public void setToken(String token) {
        this.token = token;
    }

    public void setAndroid_id(String android_id) {
        this.android_id = android_id;
    }

    public void setWeather_token(String weather) {
        this.weather_token = weather;
    }

    public String getToken() {
        return this.token;
    }

    public String getWeather_token() {
        return this.weather_token;
    }

    public String getAndroid_id() {
        return this.android_id;
    }

    private String getMetaData(String name) {
        try {
            ApplicationInfo ai = this.getPackageManager().getApplicationInfo(this.getPackageName(), PackageManager.GET_META_DATA);
            Bundle bundle = ai.metaData;
            return bundle.getString(name);
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "Unable to load meta-data: " + e.getMessage());
        }
        return null;
    }

    public String getWebApiURL() {
        if (web_api_url != null)
            return web_api_url;
        return web_api_url = getMetaData("web_api_url");
    }
}