package com.misterright.model;

import com.misterright.MisterConfig;
import com.misterright.model.entity.GlobalInfo;
import com.misterright.model.entity.MisterStatus;
import com.misterright.model.entity.UserInfo;
import com.misterright.util.LogUtil;
import com.misterright.util.storage.KeyStorage;

/**
 * Created by ruiaa on 2016/10/29.
 */

public class MisterData {

    private static MisterData instance = new MisterData();

    public String phone;
    public String pwd;
    public GlobalInfo globalInfo=null;

    public MisterStatus status;
    public UserInfo otherUser;

    private MisterData(){
        LogUtil.i("MisterData--");
    }

    public static MisterData getInstance() {
        return instance;
    }

    public static void setMisterData(MisterData misterData){
        instance=misterData;
    }

    public static MisterData setGlobalInfo(GlobalInfo globalInfo) {
        instance.globalInfo = globalInfo;
        return instance;
    }

    public int getKnowDay(){
        if (MisterStatus.KNOW.equals(getInstance().status.pageStatus)) {
            return (int) (System.currentTimeMillis() / 1000 - getInstance().status.pairInfo.startTime) / (24 * 60 * 60);
        }else {
            return 0;
        }
    }

    public static void save(){
        KeyStorage.put(MisterConfig.KEY_MISTER_DATA,MisterData.getInstance());
    }

    public static MisterData getFromKeyStorage(){
        if (KeyStorage.contains(MisterConfig.KEY_MISTER_DATA)){
            return KeyStorage.get(MisterConfig.KEY_MISTER_DATA);
        }else {
            return null;
        }
    }

    public static boolean setFromKeyStorage(){
        if (KeyStorage.contains(MisterConfig.KEY_MISTER_DATA)){
            LogUtil.i("setFromKeyStorage--");
            instance=KeyStorage.get(MisterConfig.KEY_MISTER_DATA);
            return true;
        }else {
            return false;
        }
    }



}
