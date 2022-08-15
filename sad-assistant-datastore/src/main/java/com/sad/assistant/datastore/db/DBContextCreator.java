package com.sad.assistant.datastore.db;

import static com.sad.basic.utils.app.AppDirFactoryClient.IStorageLocationStrategy.StorageLocation.SDCARD;

import android.content.Context;
import android.content.ContextWrapper;

import androidx.annotation.NonNull;

import com.sad.basic.utils.app.AppDirFactoryClient;

import java.io.File;

public class DBContextCreator implements IDBContextCreator<DBContextCreator>, IDBContextCreator.Api{
    private ContextWrapper contextWrapper;
    private Context context;
    private DBContextCreator(@NonNull Context base) {
        this.context=base;
        this.contextWrapper=new ContextWrapper(base);
    }
    public static IDBContextCreator newInstance(Context context){
        return new DBContextCreator(context);
    }

    @Override
    public Api api() {
        return this;
    }

    @Override
    public ContextWrapper getContextWrapper() {
        return contextWrapper;
    }

    @Override
    public DBContextCreator cd(AppDirFactoryClient factoryClient, AppDirFactoryClient.OnDirGoListener onDirCreatedListener) {
        this.contextWrapper=new DBCustomDirContextWrapper(context,factoryClient,onDirCreatedListener);
        return this;
    }

    @Override
    public DBContextCreator cd(File dirFile, AppDirFactoryClient.OnDirGoListener onDirCreatedListener) {

        return cd(dirFile.getAbsolutePath(),onDirCreatedListener);
    }

    @Override
    public DBContextCreator cd(String dirPath, AppDirFactoryClient.OnDirGoListener onDirCreatedListener) {
        AppDirFactoryClient factoryClient=AppDirFactoryClient
                .with(context)
                .setStorageLocationStrategy(new AppDirFactoryClient.IStorageLocationStrategy(){
                    @Override
                    public StorageLocation location(long storageReserverSize) {
                        StorageLocation storageLocation=new StorageLocation();
                        storageLocation.setRwMode(StorageLocation._W);
                        storageLocation.setLocation(SDCARD);
                        storageLocation.setAutoSwitch(false);
                        return storageLocation;
                    }
                })
                .setStorageReserverSize(5*1024*1024)
                .path(dirPath);
        return cd(factoryClient,onDirCreatedListener);
    }

    @Override
    public DBContextCreator cd(ContextWrapper contextWrapper) {
        this.context=contextWrapper.getApplicationContext();
        this.contextWrapper=contextWrapper;
        return this;
    }



}
