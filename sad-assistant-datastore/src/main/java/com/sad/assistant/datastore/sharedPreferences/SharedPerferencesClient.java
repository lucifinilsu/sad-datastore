package com.sad.assistant.datastore.sharedPreferences;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
/**
 * Created by LucifinilSu on 2018/5/17 0017.
 */
public class SharedPerferencesClient implements Observer {

    private Context context;
    private SharedPreferences sharedPreferences;
    private String name="sad-app";
    private int mode= Context.MODE_PRIVATE | Context.MODE_MULTI_PROCESS;
    protected boolean isInitValueWhenReadError=true;
    private Builder builder;
    private SharedPerferencesClient(Builder builder){
        this.builder=builder;
        //init();
    }

    public static Builder with(Context context){

         return new Builder(context);
    }

    public Builder modifyBuilderParamas(){
        return this.builder;
    };

    private void init(){
        sharedPreferences=context.getSharedPreferences(name,mode);
    }

    @Override
    public void update(Observable o, Object arg) {
        init();
    }
    /**********************************dao**************************************/

    public <V> SharedPerferencesClient put(
            String key,
            V value
    ){

        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (value instanceof String){
            editor.putString(key, String.valueOf(value));
        }
        else if(value instanceof Integer){
            editor.putInt(key, (Integer) value);
        }
        else if(value instanceof Boolean){
            editor.putBoolean(key, (Boolean) value);
        }
        else if(value instanceof Long){
            editor.putLong(key, (Long) value);
        }
        else if(value instanceof Float){
            editor.putFloat(key, (Float) value);
        }
        else if(value instanceof Set){
            editor.putStringSet(key, (Set<String>) value);
        }
        editor.commit();
        return this;
    }

    public <B,S> SharedPerferencesClient put(String key, B bean, @NonNull ISharedPreferencesPutBeanConverter<B,S> converter){
        S s=converter.putFrom(bean);
        put(key,s);
        return this;
    }
    @Retention(RetentionPolicy.RUNTIME)
    @Inherited
    @Target(ElementType.METHOD)
    private   @interface ParamsValueTypeLimit{
        Class<?>[] classes() default {};
    }

    //利用注解限定一下参数类型
    @ParamsValueTypeLimit(classes = {String.class,Integer.class,Float.class,Long.class,Boolean.class,Set.class})
    public <V> V get(String key, @NonNull V defaultValue){
        try {

            Method method=getClass().getDeclaredMethod("get",String.class,Object.class);
            ParamsValueTypeLimit paramsValueTypeLimit =method.getAnnotation(ParamsValueTypeLimit.class);
            if (paramsValueTypeLimit!=null){
                Class<?>[] c=paramsValueTypeLimit.classes();

                List<Class<?>> lc= Arrays.asList(c);
                Class<?> cv =
                        //method.getParameterTypes()[1];
                        defaultValue.getClass();
                //Log.e("15","--------------------->限定范围："+lc+",参数类型："+cv+"，位置："+lc.indexOf(cv));
                if (lc.indexOf(cv)!=-1){
                    V v=(V) getValue(key,defaultValue);
                    //Log.e("hahah","--------------------->获取："+v);
                    return v;
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
            if (isInitValueWhenReadError){
                put(key,defaultValue);
            }
        }
        return defaultValue;
    }
    public <B,V> B get(String key, V defaultValue, ISharedPreferencesGetBeanConverter<B,V> converter){
        try {
            V v=get(key,defaultValue);
            if (converter!=null){
                B bean=converter.getFrom(v);
                return bean;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    private <V> Object getValue(String key, V defaultValue) throws Exception{
        if (defaultValue instanceof String){
            return sharedPreferences.getString(key, defaultValue+"");
        }
        else if (defaultValue instanceof Integer){
            return sharedPreferences.getInt(key,(Integer) defaultValue);
        }
        else if (defaultValue instanceof Long){
            return  sharedPreferences.getLong(key, (Long) defaultValue);
        }
        else if(defaultValue instanceof Float){
            return sharedPreferences.getFloat(key, (Float) defaultValue);
        }
        else if (defaultValue instanceof Boolean){
            return sharedPreferences.getBoolean(key, (Boolean) defaultValue);
        }
        else if (defaultValue instanceof Set){
            return sharedPreferences.getStringSet(key, (Set<String>) defaultValue);
        }
        return defaultValue;
    }



    public SharedPerferencesClient remove(String key){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(key);
        editor.commit();
        return this;
    }

    public SharedPerferencesClient clear(){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.commit();
        return this;
    }

    public void end(){
        //sss(String.class);
        if (builder!=null){
            builder.deleteObserver(this);
        }
    }


    //自定义一个注解来限制调用者的参数类型设定
    /*@Retention(RetentionPolicy.SOURCE)
    *//*@Inherited
    @Target(ElementType.PARAMETER)*//*
    @TypeDef({Integer.class})
    public  @interface ParamsValueTypeLimit {

    }*/


    /*@Retention(SOURCE)
    @Target({ANNOTATION_TYPE})
    public @interface TypeDef {
        Class[] value() default {};
        boolean flag() default false;
    }*/



    public static class Builder extends Observable {
        private String name="sad-app";
        private Context context;
        private int mode= Context.MODE_PRIVATE | Context.MODE_MULTI_PROCESS;
        private boolean isBuilded=false;
        private boolean isInitValueWhenReadError=true;
        private Builder(Context context){
            this.context=context;
            isBuilded=false;
        }
        public Builder initValueWhenReadError(boolean isInitValueWhenReadError){
            this.isInitValueWhenReadError=isInitValueWhenReadError;
            if (isBuilded){
                setChanged();
                notifyObservers();
            }
            return this;
        }
        public Builder name(String name){
            this.name=name;
            if (isBuilded){
                setChanged();
                notifyObservers();
            }
            return this;
        }
        public Builder mode(int mode){
            this.mode=mode;
            if (isBuilded){
                setChanged();
                notifyObservers();
            }
            return this;
        }
        public SharedPerferencesClient build(){
            SharedPerferencesClient sharedPerferencesClient=new SharedPerferencesClient(this);
            sharedPerferencesClient.context=this.context;
            sharedPerferencesClient.name=this.name;
            sharedPerferencesClient.mode=this.mode;
            sharedPerferencesClient.isInitValueWhenReadError=this.isInitValueWhenReadError;
            addObserver(sharedPerferencesClient);
            isBuilded=true;
            sharedPerferencesClient.init();
            return sharedPerferencesClient;
        }
    }


}
