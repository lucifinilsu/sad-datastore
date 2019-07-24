package com.sad.assistant.datastore.db;

import android.database.sqlite.SQLiteDatabase;

import java.util.concurrent.Executor;

public interface IDBWorker<I extends IDBWorker<I>> {

    I executor(Executor executor);

    I endCallback(DBTranscationEndCallback endCallback);


    <RE> I  performSync(IDBAction<RE> action);

    <RE> I  performAsync(IDBAction<RE> action);

    void closeDB();

    Api api();

    interface Api{

        Executor getExecutor();

        DBTranscationEndCallback getEndCallback();

        SQLiteDatabase getDatabase();

    }



}
