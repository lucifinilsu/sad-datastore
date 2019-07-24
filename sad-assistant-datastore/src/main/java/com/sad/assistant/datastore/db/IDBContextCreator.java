package com.sad.assistant.datastore.db;

import android.content.ContextWrapper;

import com.sad.basic.utils.app.AppDirFactoryClient;

import java.io.File;

public interface IDBContextCreator<I extends IDBContextCreator<I>> {

    Api api();

    I cd(AppDirFactoryClient dbDirFactory, AppDirFactoryClient.OnDirGoListener onDirCreatedListener);

    I cd(ContextWrapper contextWrapper);

    I cd(File dirFile, AppDirFactoryClient.OnDirGoListener onDirCreatedListener);

    I cd(String dirPath, AppDirFactoryClient.OnDirGoListener onDirCreatedListener);

    interface Api{

        ContextWrapper getContextWrapper();
    }

}
