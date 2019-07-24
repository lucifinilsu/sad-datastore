package com.sad.assistant.datastore.db;

import android.database.sqlite.SQLiteDatabase;

import com.sad.core.async.SADExecutor;
import com.sad.core.async.SADHandlerAssistant;
import com.sad.core.async.SADTaskRunnable;
import com.sad.core.async.SADTaskSchedulerClient;

import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;

public class DBWorkerImpl implements IDBWorker<DBWorkerImpl>,IDBWorker.Api{

    protected static IDBWorker newInstance(SQLiteDatabase db){
        return new DBWorkerImpl(db);
    }

    private SADTaskSchedulerClient sadTaskSchedulerClient;
    //内置一个计数器，当计数器为0时，表示所有线程任务结束，可以end
    private AtomicInteger counter=new AtomicInteger(0);
    private Executor executor;
    private DBTranscationEndCallback endCallback;
    private SQLiteDatabase db;
    private DBWorkerImpl(SQLiteDatabase db){
        this.db=db;
        this.sadTaskSchedulerClient=SADTaskSchedulerClient.newInstance();
    }




    @Override
    public DBWorkerImpl executor(Executor executor) {
        this.executor=executor;
        return this;
    }

    @Override
    public DBWorkerImpl endCallback(DBTranscationEndCallback endCallback) {
        this.endCallback=endCallback;
        return this;
    }

    @Override
    public <R> DBWorkerImpl performSync(IDBAction<R> action) {
        if (db!=null && action!=null){
            boolean isSuccess=false;
            counter.getAndIncrement();
            if (!db.isOpen()){
                //uiInfoFailedRunnable=new UIInfoFailedRunnable(action,db,new Exception("the database '"+dbName+"' is closed !!!"));
                SADHandlerAssistant.runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        action.onExecuteFailed(db,new Exception("the database '"+db.getPath()+"' is closed !!!"));
                    }
                });
            }
            try {
                db.beginTransaction();
                R r=action.onExecute(db);
                db.setTransactionSuccessful();
                //uiInfoSuccessRunnable=new UIInfoSuccessRunnable<R>(action,r,db);
                SADHandlerAssistant.runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        action.onExecuteSuccess(r,db);
                    }
                });
            }catch (Exception e){
                e.printStackTrace();
                //uiInfoFailedRunnable=new UIInfoFailedRunnable(action,db,e);
                SADHandlerAssistant.runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        action.onExecuteFailed(db,e);
                    }
                });
            }
            finally {
                if(db != null && db.inTransaction()){
                    db.endTransaction();
                }
            }
            counter.getAndDecrement();
        }

        return this;
    }

    @Override
    public <R> DBWorkerImpl performAsync(IDBAction<R> action) {

        if (executor==null){
            executor= new SADExecutor();
        }
        sadTaskSchedulerClient.executor(executor);
        sadTaskSchedulerClient.execute(new SADTaskRunnable<R>(action.getId()) {
            @Override
            public R doInBackground() throws Exception {
                counter.getAndIncrement();
                if (db!=null && action!=null){
                    if (!db.isOpen()){
                        throw new Exception("the database '"+db.getPath()+"' is closed !!!");
                    }
                    try {
                        db.beginTransaction();
                        R r=action.onExecute(db);
                        db.setTransactionSuccessful();
                        return r;
                    }catch (Exception e){
                        e.printStackTrace();
                        throw new Exception(e);
                    }
                    catch (Error error){
                        error.printStackTrace();
                        throw new Exception(error);
                    }
                    finally {
                        if(db != null && db.inTransaction()){
                            db.endTransaction();
                        }
                    }
                }
                else {
                    throw new Exception("the database '"+db.getPath()+"' or action is null !!!");
                }

            }

            @Override
            public void onSuccess(R result) {

                action.onExecuteSuccess(result,db);
                counter.getAndDecrement();
                closeDB(action.getId());
            }

            @Override
            public void onFail(Throwable throwable) {
                action.onExecuteFailed(db,new Exception(throwable));
                counter.getAndDecrement();
                closeDB(action.getId());
            }

            @Override
            public void onCancel() {
                counter.getAndDecrement();
                closeDB(action.getId());
            }
        });
        return this;
    }
    @Override
    public void closeDB() {
        closeDB("");
    }

    private void closeDB(String id){
        if (counter.get()==0){
            if (db!=null && db.isOpen()){
                db.close();
            }
            if (endCallback!=null){
                //Log.e("sad","------------------------------>回调endcallback");
                endCallback.OnDBTaskEnd(id);
            }
        }
    }

    @Override
    public Api api() {
        return this;
    }

    @Override
    public Executor getExecutor() {
        return this.executor;
    }

    @Override
    public DBTranscationEndCallback getEndCallback() {
        return this.endCallback;
    }

    @Override
    public SQLiteDatabase getDatabase() {
        return this.db;
    }

}
