package com.sad.assistant.datastore.sharedPreferences.implemention;

/**
 * Created by Administrator on 2018/9/28 0028.
 */

public class AppVersion  {
    private int code=0;
    private String name="";
    private String description="";
    public AppVersion(){}
    public AppVersion(int code,String name){
        this.code=code;
        this.name=name;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
