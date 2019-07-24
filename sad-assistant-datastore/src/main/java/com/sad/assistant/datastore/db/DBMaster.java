package com.sad.assistant.datastore.db;

import android.database.sqlite.SQLiteDatabase;

public class DBMaster {

    public static IDBWorker with(SQLiteDatabase db){
        return DBWorkerImpl.newInstance(db);
    }

}
