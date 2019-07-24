package com.sad.assistant.datastore.db;

import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Administrator on 2018/5/14 0014.
 */

public interface IDBAction<R> {

    String getId();

    R onExecute(SQLiteDatabase db);

    void onExecuteSuccess(R r, SQLiteDatabase db);

    void onExecuteFailed(SQLiteDatabase db, Exception e);

}
