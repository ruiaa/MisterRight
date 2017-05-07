package com.misterright.util;

import android.content.Context;
import android.support.annotation.ColorRes;
import android.support.annotation.StyleRes;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.TextAppearanceSpan;


/**
 * Created by ruiaa on 2016/10/21.
 */

public class StringStyles {

    private static Context context;

    public static void register(Context appContext) {
        context = appContext.getApplicationContext();
    }

    public static SpannableString format(String text, @StyleRes int style) {
        if (text == null) {
            return null;
        }
        SpannableString spannableString = new SpannableString(text);
        spannableString.setSpan(new TextAppearanceSpan(context, style), 0, text.length(), 0);
        return spannableString;
    }

    public static SpannableStringBuilder newSpanBuilder(){
        return new SpannableStringBuilder();
    }

   /* public static String removeEnter(String s) {
        if (s == null) {
            return "";
        } else {
            return s.replaceAll("", "");
        }
    }

    public static int getEnterCount(String s){
        if (s==null){
            return 0;
        }else {
            return 0;
        }
    }*/

    public static class RichText{
        public SpannableStringBuilder builder;

        public RichText() {
            builder=new SpannableStringBuilder();
        }

        public RichText(CharSequence text) {
            builder=new SpannableStringBuilder(text);
        }

        public static RichText newRichText(){
            return new RichText();
        }

        public static RichText newRichText(CharSequence text){
            return new RichText(text);
        }

        public RichText append(String text){
            if (text==null) return this;
            builder.append(text);
            return this;
        }

        public RichText append(String text,float sizeSp){
            if (text==null) return this;
            SpannableString spannableString = new SpannableString(text);
            spannableString.setSpan(new AbsoluteSizeSpan(ConvertUtil.sp2px(sizeSp)), 0, text.length(), 0);
            builder.append(spannableString);
            return this;
        }

        public RichText appendColor(CharSequence text,int color){
            if (text==null) return this;
            SpannableString spannableString = new SpannableString(text);
            spannableString.setSpan(new ForegroundColorSpan(color), 0, text.length(), 0);
            builder.append(spannableString);
            return this;
        }

        public RichText appendColorId(CharSequence text,@ColorRes int colorId){
            return appendColor(text,ResUtil.getColor(colorId));
        }

        public RichText setSizeInPx(CharSequence text,int sizePx){
            if (text==null) return this;
            String source=builder.toString();
            int startIndex=source.indexOf(text.toString());
            int endIndex=startIndex+text.length();
//            LogUtil.i("setSize--source**"+source+"**target**"+text+"**start**"+startIndex+"**end**"+endIndex);
//            LogUtil.i("setSize--px"+sizePx+"sp"+ConvertUtil.px2sp(sizePx));
            builder.setSpan(new AbsoluteSizeSpan(sizePx),startIndex,endIndex,0);
            return this;
        }

        public RichText setSizeInSp(Object text,float sizeSp){
            if (text==null) return this;
            return setSizeInPx(text.toString(),ConvertUtil.sp2px(sizeSp));
        }

        public CharSequence build(){
            return builder.subSequence(0, builder.length());
        }











        public static CharSequence setFormatSizeSp(CharSequence text,float sizeSp) {
            return setFormatSizePx(text,ConvertUtil.sp2px(sizeSp));
        }

        public static CharSequence setFormatSizePx(CharSequence text,float sizePx) {
            SpannableString spannableString = new SpannableString(text);
            spannableString.setSpan(new AbsoluteSizeSpan((int)sizePx), 0, text.length(), 0);
            return spannableString.subSequence(0,spannableString.length());
        }
    }

}
