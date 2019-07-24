package com.sad.assistant.datastore.sharedPreferences;

/**
 * Created by Administrator on 2018/5/17 0017.
 */

public interface ISharedPreferencesPutBeanConverter<Bean,Store> {

    public Store putFrom(Bean bean);

}
