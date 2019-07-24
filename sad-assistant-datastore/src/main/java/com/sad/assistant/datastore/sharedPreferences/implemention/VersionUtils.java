package com.sad.assistant.datastore.sharedPreferences.implemention;

import android.content.Context;
import android.text.TextUtils;

import com.sad.assistant.datastore.sharedPreferences.ISharedPreferencesGetBeanConverter;
import com.sad.assistant.datastore.sharedPreferences.ISharedPreferencesPutBeanConverter;
import com.sad.assistant.datastore.sharedPreferences.SharedPerferencesClient;
import com.sad.basic.utils.app.AppInfoUtil;

import org.json.JSONObject;

/**
 * Created by Administrator on 2018/9/28 0028.
 */

public class VersionUtils {
    public final static String SHAREDPERFERENCES_VERSION ="APP_VERSION";
    public final static String SHAREDPERFERENCES_VERSION_LAST ="APP_VERSION_LAST";
    public static boolean isDiffVersion(Context context){
        return isDiffVersion(context,"");
    }
    public static boolean isDiffVersion(Context context,String tag){
        AppVersion storeVersion=getLastVersion(context,tag);
        if (storeVersion==null){
            return true;
        }
        int currVersionCode= AppInfoUtil.getVersionCode(context);
        int storeVersionCode=storeVersion.getCode();
        return storeVersionCode!=currVersionCode;
    }
    public static AppVersion getLastVersion(Context context){
        return getLastVersion(context,"");
    }
    public static AppVersion getLastVersion(Context context,String tag){
       return SharedPerferencesClient.with(context)
                .initValueWhenReadError(true)
                .mode(Context.MODE_PRIVATE | Context.MODE_MULTI_PROCESS)
                .name(SHAREDPERFERENCES_VERSION)
                .build()
                .get(SHAREDPERFERENCES_VERSION_LAST+tag,"",new GetConverter())
                ;
    }
    public static void storeLastVersion(Context context){
        storeLastVersion(context,"");
    }
    public static void storeLastVersion(Context context,AppVersion appVersion,String tag){
        SharedPerferencesClient.with(context)
                .initValueWhenReadError(true)
                .mode(Context.MODE_PRIVATE | Context.MODE_MULTI_PROCESS)
                .name(SHAREDPERFERENCES_VERSION)
                .build()
                .put(SHAREDPERFERENCES_VERSION_LAST+tag,appVersion,new PutConverter())
                .end();
    }
    public static void storeLastVersion(Context context,String tag){

        storeLastVersion(context,new AppVersion(AppInfoUtil.getVersionCode(context),AppInfoUtil.getVersionName(context)),tag);
    }

    private static class PutConverter implements ISharedPreferencesPutBeanConverter<AppVersion,String>{
        @Override
        public String putFrom(AppVersion appVersion) {
            try{
                if (appVersion!=null){
                    JSONObject jsonObject=new JSONObject();
                    jsonObject.put("code",appVersion.getCode());
                    jsonObject.put("name",appVersion.getName());
                    jsonObject.put("description",appVersion.getDescription());
                    return jsonObject.toString();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }
    }
    private static class GetConverter implements ISharedPreferencesGetBeanConverter<AppVersion,String> {

        @Override
        public AppVersion getFrom(String s) {
            try{
                if (TextUtils.isEmpty(s)){
                    return null;
                }
                AppVersion appVersion=new AppVersion();
                JSONObject jsonObject=new JSONObject(s);
                appVersion.setCode(jsonObject.optInt("code"));
                appVersion.setName(jsonObject.optString("name"));
                appVersion.setDescription(jsonObject.optString("description"));
                return appVersion;
            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }
    }
}
