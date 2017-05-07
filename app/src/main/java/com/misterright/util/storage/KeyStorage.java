package com.misterright.util.storage;

import android.content.Context;

import com.orhanobut.hawk.Hawk;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by ruiaa on 2016/11/3.
 */

public class KeyStorage {

    public static void init(Context appContext){
        Hawk.init(appContext).build();
    }

    public static boolean contains(String key){
        return Hawk.contains(key);
    }

    public static long count(){
        return Hawk.count();
    }

    public static <T> void put(String key,T value){
        Hawk.put(key,value);
    }

    public static <T> T get(String key){
        return Hawk.get(key);
    }

    public static <T> T get(String key,T defaultValue){
        return Hawk.get(key,defaultValue);
    }

    public static void delete(String key){
        Hawk.delete(key);
    }

    public static void deleteAll(){
        Hawk.deleteAll();
    }

    public static <T>void addToList(String key,T value){
        List<T> list;
        if (KeyStorage.contains(key)){
            list=KeyStorage.get(key);
        }else {
            list=new ArrayList<T>();
        }
        list.add(value);
        KeyStorage.put(key,list);
    }

}
