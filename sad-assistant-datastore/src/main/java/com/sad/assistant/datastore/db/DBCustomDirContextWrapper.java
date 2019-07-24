package com.sad.assistant.datastore.db;

import android.content.Context;
import android.content.ContextWrapper;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;

import com.sad.basic.utils.app.AppDirFactoryClient;
import com.sad.basic.utils.file.DirScanningResult;

import java.io.File;

public class DBCustomDirContextWrapper extends ContextWrapper {

    private AppDirFactoryClient appDirFactoryClient;
    private AppDirFactoryClient.OnDirGoListener onDirCreatedListener;
    public DBCustomDirContextWrapper(Context base,AppDirFactoryClient appDirFactoryClient,AppDirFactoryClient.OnDirGoListener onDirCreatedListener) {
        super(base);
        this.appDirFactoryClient=appDirFactoryClient;
        this.onDirCreatedListener=onDirCreatedListener;
    }

    /**
     * 自定义db文件的路径
     * @param name
     * @return
     */
    @Override
    public File getDatabasePath(String name) {
        //Toast.makeText(getBaseContext(),"getDatabasePath方法调用",Toast.LENGTH_LONG).show();
        if (appDirFactoryClient==null){
            return super.getDatabasePath(name);
        }
        DirScanningResult scanningResult=appDirFactoryClient.go(onDirCreatedListener);
        if (scanningResult.isSuccess()){
            File dir=scanningResult.getFile();
            if (dir==null){
                return super.getDatabasePath(name);
            }
            else{
                File file=new File(dir.getAbsolutePath()+ File.separator+name);
                return file;
            }
        }
        return super.getDatabasePath(name);
    }

    /**
     * 重载这个方法，是用来打开SD卡上的数据库的，android 2.3及以下会调用这个方法。
     *
     * @param    name
     * @param    mode
     * @param    factory
     */
    @Override
    public SQLiteDatabase openOrCreateDatabase(String name, int mode,
                                               SQLiteDatabase.CursorFactory factory) {
        if (appDirFactoryClient==null){
            return super.openOrCreateDatabase(name,mode,factory);
        }
        SQLiteDatabase result = SQLiteDatabase.openOrCreateDatabase(getDatabasePath(name), null);
        return result;
    }

    /**
     * Android 4.0及以上会调用此方法获取数据库。
     *
     * @see android.content.ContextWrapper#openOrCreateDatabase(java.lang.String, int,
     *              android.database.sqlite.SQLiteDatabase.CursorFactory,
     *              android.database.DatabaseErrorHandler)
     * @param    name
     * @param    mode
     * @param    factory
     * @param     errorHandler
     */
    @Override
    public SQLiteDatabase openOrCreateDatabase(String name, int mode, SQLiteDatabase.CursorFactory factory,
                                               DatabaseErrorHandler errorHandler) {
        //Log.e("ssssaaaddd","------>"+getDatabasePath(name).getAbsolutePath());
        if (appDirFactoryClient==null){
            return super.openOrCreateDatabase(name,mode,factory,errorHandler);
        }
        SQLiteDatabase result = SQLiteDatabase.openOrCreateDatabase(getDatabasePath(name).getAbsolutePath(), null,errorHandler);
        return result;
    }
}
