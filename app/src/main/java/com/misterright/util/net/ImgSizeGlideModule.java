package com.misterright.util.net;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.cache.DiskLruCacheFactory;
import com.bumptech.glide.load.model.GenericLoaderFactory;
import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.load.model.ModelLoaderFactory;
import com.bumptech.glide.load.model.stream.BaseGlideUrlLoader;
import com.bumptech.glide.module.GlideModule;
import com.misterright.util.storage.FileStorage;

import java.io.InputStream;

/**
 * Created by ruiaa on 2016/11/8.
 */

public class ImgSizeGlideModule implements GlideModule {

    /*
     * 自定义缓存
     */
    @Override
    public void applyOptions(Context context, GlideBuilder builder) {
        builder
                //.setMemoryCache(new LruResourceCache(yourSizeInBytes))  //内存缓存
                //.setBitmapPool(new LruBitmapPool(sizeInBytes))  //
                //.setDiskCache(new InternalCacheDiskCacheFactory(context, FileStorage.getImgCacheDir(), 250 * 1024 * 1024))  //磁盘缓存
                .setDiskCache(new DiskLruCacheFactory(FileStorage.getImgCacheDir(), 250 * 1024 * 1024))
                //.setDiskCacheService(ExecutorService service)
                //.setResizeService(ExecutorService service)
                .setDecodeFormat(DecodeFormat.PREFER_RGB_565);  //图片解码格式 ARGB8888 , RGB565
    }

    /*
     * 自定义url +baseUrl +sizeQuery
     */
    @Override
    public void registerComponents(Context context, Glide glide) {
        glide.register(ImgSizeModel.class, InputStream.class, new ImgSizeModelFactory());
    }


    public interface ImgSizeModel {
        public String buildUrl(int width, int height);
    }

    public static class ImgSizeUrlLoader extends BaseGlideUrlLoader<ImgSizeModel> {

        public ImgSizeUrlLoader(Context context) {
            super(context);
        }

        @Override
        protected String getUrl(ImgSizeModel model, int width, int height) {
            return model.buildUrl(width, height);
        }
    }

    public static class ImgSizeModelFactory implements ModelLoaderFactory<ImgSizeModel, InputStream> {
        @Override
        public ModelLoader<ImgSizeModel, InputStream> build(Context context, GenericLoaderFactory factories) {
            return new ImgSizeUrlLoader(context);
        }

        @Override
        public void teardown() {

        }
    }

    public static class MyImgSizeModel implements ImgSizeModel {
        String baseImageUrl;

        public MyImgSizeModel(String baseImageUrl) {
            this.baseImageUrl = baseImageUrl;
        }

        @Override
        public String buildUrl(int width, int height) {
            //LogUtil.i("buildUrl--" + baseImageUrl + "?w=" + width + "&h=" + height);
            return baseImageUrl + "?w=" + width + "&h=" + height;
        }
    }
}
