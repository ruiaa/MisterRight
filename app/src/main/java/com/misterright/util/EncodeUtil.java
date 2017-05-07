package com.misterright.util;

import android.util.Base64;

import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;

/**
 * Created by ruiaa on 2016/10/29.
 */

public class EncodeUtil {
    /**
     * 用MD5算法进行加密
     *
     * @param str 需要加密的字符串
     * @return MD5加密后的结果
     */
    public static String encodeMD5String(String str) {
        return encode(str, "MD5");
    }

    /**
     * 用SHA算法进行加密
     *
     * @param str 需要加密的字符串
     * @return SHA加密后的结果
     */
    public static String encodeSHAString(String str) {
        return encode(str, "SHA");
    }
    /**
     * 用base64算法进行加密
     * @param str 需要加密的字符串
     * @return base64加密后的结果
     */
/*    public static String encodeBase64String(String str) {
        BASE64Encoder encoder = new BASE64Encoder();
        return encoder.encode(str.getBytes());
    }*/

    /**
     * 用base64算法进行解密
     *
     * @param str 需要解密的字符串
     * @return base64解密后的结果
     * @throws IOException
     */
/*    public static String decodeBase64String(String str) throws IOException {
        BASE64Decoder encoder = new BASE64Decoder();
        return new String(encoder.decodeBuffer(str));
    }*/
    private static String encode(String str, String method) {
        MessageDigest md = null;
        String dstr = null;
        try {
            md = MessageDigest.getInstance(method);
            md.update(str.getBytes());
            dstr = new BigInteger(1, md.digest()).toString(16);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return dstr;
    }


    public static String encodeMD5(String str) throws NoSuchAlgorithmException {
        if (str == null) return null;
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(str.getBytes());
        String m=new BigInteger(1, md.digest()).toString(16);
        //LogUtil.i("encodeMD5--"+str+"\n"+m);
        return m;
    }

    public static String encodeMD5NoE(String str) {
        if (str == null) return null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(str.getBytes());
            String m=new BigInteger(1, md.digest()).toString(16);
            //LogUtil.i("encodeMD5--"+str+"\n"+m);
            return m;
        }catch (NoSuchAlgorithmException e){
            e.printStackTrace();
        }
        return null;
    }

    public static Map<String, String> encodeMD5AddTime(Map<String, String> map) {
        String time = String.valueOf(System.currentTimeMillis()/1000);
        map.put("_", time);
        ArrayList<String> arrayList=new ArrayList<>();
        int i = 0;
        for (Map.Entry<String, String> entry : map.entrySet()) {
            i = i + 1;
            arrayList.add(entry.getKey() + "=" + entry.getValue());
        }
        String[] strs=new String[arrayList.size()];
        arrayList.toArray(strs);
        map.put("sign",encodeMD5(strs,map.get("_")));
        return map;
    }

/*    public static Map<String, String> standWithPhoTakTimSig() {
        Map<String, String> map=new HashMap<>();
        String time = String.valueOf(System.currentTimeMillis()/1000);
        map.put("_", time);
        map.put("phone_num", MisterData.getInstance().phone);
        map.put("token", MisterData.getInstance().globalInfo.getToken());

        ArrayList<String> arrayList=new ArrayList<>();
        int i = 0;
        for (Map.Entry<String, String> entry : map.entrySet()) {
            i = i + 1;
            arrayList.add(entry.getKey() + "=" + entry.getValue());
        }
        String[] strs=new String[arrayList.size()];
        arrayList.toArray(strs);
        map.put("sign",encodeMD5(strs,map.get("_")));

        return map;
    }*/

    public static String encodeBase64( String s){
        if (s==null) return null;
        return Base64.encodeToString(s.getBytes(),Base64.NO_WRAP);
    }

    public static String decodeBase64( String s){
        if (s==null) return null;
        return new String(Base64.decode(s,Base64.NO_WRAP));
    }

    public static String encodeMD5(Map<String, String> map) {
        ArrayList<String> arrayList=new ArrayList<>();
        int i = 0;
        for (Map.Entry<String, String> entry : map.entrySet()) {
            i = i + 1;
            arrayList.add(entry.getKey() + "=" + entry.getValue());
        }
        String[] strs=new String[arrayList.size()];
        arrayList.toArray(strs);
        return encodeMD5(strs,map.get("_"));
    }

    public static String encodeMD5(String[] strs,String time) {
        if (strs == null) return null;
        sort(strs);
        String result = "";
        try {
            int size = strs.length;
            for (int i = 0; i < size; i++) {
                result = result + encodeMD5(strs[i]);
            }
            return encodeMD5(result+time);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static void sort(String[] strs) {
        Arrays.sort(strs, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                int result = o1.compareTo(o2);
                if (result > 0) return 1;
                if (result < 0) return -1;
                return 0;
            }
        });
    }
}
