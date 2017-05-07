package com.misterright.util.storage;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.support.annotation.Nullable;

import com.misterright.util.LogUtil;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by ruiaa on 2016/11/8.
 */

public class ImageUtils {

    private static Bitmap getBitmap(File file) {
        if (file == null) return null;
        InputStream is = null;
        try {
            is = new BufferedInputStream(new FileInputStream(file));
            return BitmapFactory.decodeStream(is);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } finally {
            try{
                if (is!=null){
                    is.close();
                }
            }catch (IOException e){
                LogUtil.e("getBitmap--",e);
            }
        }
    }

    private static boolean isEmptyBitmap(Bitmap src) {
        return src == null || src.getWidth() == 0 || src.getHeight() == 0;
    }

    private static Bitmap compressByQuality(Bitmap src, long maxByteSize, boolean recycle) {
        if (isEmptyBitmap(src) || maxByteSize <= 0) return null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int quality = 100;
        src.compress(CompressFormat.JPEG, quality, baos);
        while (baos.toByteArray().length > maxByteSize && quality >= 0) {
            baos.reset();
            src.compress(CompressFormat.JPEG, quality -= 5, baos);
        }
        if (quality < 0) return null;
        byte[] bytes = baos.toByteArray();
        if (recycle && !src.isRecycled()) src.recycle();
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }


    @Nullable
    private static byte[] compressToQiniu(String path,long maxByteSize){
        if (path==null) return null;

        BitmapFactory.Options options = new BitmapFactory.Options();
        Bitmap src;

        options.inJustDecodeBounds = true; //只获取图片边界信息
        src=BitmapFactory.decodeFile(path,options); //null

        options.inSampleSize=Math.max(options.outWidth/1080,options.outHeight/1920);
        options.inJustDecodeBounds=false;
        src=BitmapFactory.decodeFile(path,options);

        if (src==null) return null;
        if (isEmptyBitmap(src) || maxByteSize <= 0) return null;

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int quality = 100;
        src.compress(CompressFormat.JPEG, quality, baos);
        while (baos.toByteArray().length > maxByteSize && quality >= 0) {
            baos.reset();
            src.compress(CompressFormat.JPEG, quality, baos);
/*            LogUtil.i("compressToQiniu--"+quality);
            LogUtil.i("compressToQiniu--"+((double)baos.toByteArray().length)/1024);*/
            quality=quality-5;
        }
        byte[] bytes = baos.toByteArray();
        src.recycle();
        return bytes;
    }

    @Nullable
    public static byte[] compressToQiniu(String path){
        return compressToQiniu(path,100*1024);
    }
}
