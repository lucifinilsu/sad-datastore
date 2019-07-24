package com.sad.assistant.datastore.sharedPreferences;

/**
 * Created by Administrator on 2018/5/17 0017.
 */

public interface ISharedPreferencesGetBeanConverter<Bean,Store> {

    public Bean getFrom(Store store);

}
