package com.misterright.model.entity;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.misterright.R;
import com.misterright.http.MisterApi;
import com.misterright.util.ResUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by ruiaa on 2016/10/29.
 */

public class MapInfo {

    private static final List<String> Grades_Default = Arrays.asList("大一", "大二", "大三", "大四", "研一", "研二", "研三");
    public static final int INFO_TYPE_INTRODUCTION=1;
    public static final int INFO_TYPE_FRIEND_STANDARD=2;
    public static final int INFO_TYPE_BASE_INFO=3;
    public static final int INFO_TYPE_HOBBY=4;

    @SerializedName("head_url")
    public String headUrl;
    @SerializedName("album")
    public List<String> album; //图片*8
    @SerializedName("introduction")
    public String introduction;
    @SerializedName("url_list")
    public List<String> urlList;

    @SerializedName("base_info")
    public BaseInfo baseInfo=new BaseInfo();
    @SerializedName("friend_standard")
    public FriendStandard friendStandard;
    @SerializedName("hobby")
    public Hobby hobby;
    @SerializedName("nick_name")
    public String nickName;   // base64

    public String getIntroduction() {
        if (introduction == null || introduction.isEmpty())
            return ResUtil.getString(R.string.no_introduction);
        return introduction;
    }

    public MapInfo getClone() {
        Gson gson = new Gson();
        return gson.fromJson(gson.toJson(this), MapInfo.class);
    }

    /*
     * 8 张专辑图
     */
    public void addAlbum(String key) {
        if (album == null) {
            album = new ArrayList<>();
        }
        album.add(key);
    }

    public int getAlbumSize() {
        if (album == null) {
            return 0;
        } else {
            return album.size();
        }

    }

    public ArrayList<String> getCompleteAlbum() {
        ArrayList<String> list = new ArrayList<>();
        if (album == null) return list;
        for (String s : album) {
            list.add(MisterApi.QINIU_BASE_URL + s);
        }
        return list;
    }

    /*
     * 一列兴趣爱好
     */
    public ArrayList<String> getOneListHobby(){
        ArrayList<String> list=new ArrayList<>();
        if (hobby==null) return list;
        boolean isEmpty=false;
        for(int i=0;list.size()<8;i++){
            isEmpty=true;
            if (!hobby.noSport()&&hobby.sport.size()>i){
                list.add(hobby.sport.get(i));
                isEmpty=false;
            }
            if (!hobby.noDiet()&&hobby.diet.size()>i){
                list.add(hobby.diet.get(i));
                isEmpty=false;
            }
            if (!hobby.noDrink()&&hobby.drink.size()>i){
                list.add(hobby.drink.get(i));
                isEmpty=false;
            }
            if (!hobby.noBook()&&hobby.book.size()>i){
                list.add(hobby.book.get(i));
                isEmpty=false;
            }
            if (!hobby.noVideo()&&hobby.video.size()>i){
                list.add(hobby.video.get(i));
                isEmpty=false;
            }
            if (!hobby.noLeisure()&&hobby.leisure.size()>i){
                list.add(hobby.leisure.get(i));
                isEmpty=false;
            }
            if (isEmpty){
                break;
            }
        }
        return list;
    }

    public Hobby newHobby(){
        return new Hobby();
    }

    public int getInfoTypeIcon(int type){
        switch (type){
            case INFO_TYPE_INTRODUCTION :{
                return R.mipmap.icon_introduce_oneself;
            }
            case INFO_TYPE_FRIEND_STANDARD:{
                return R.mipmap.icon_friend_standard;
            }
            case INFO_TYPE_BASE_INFO:{
                return R.mipmap.icon_info_base;
            }
            case INFO_TYPE_HOBBY:{
                return R.mipmap.icon_hobby;
            }
            default:{
                return R.mipmap.icon_introduce_oneself;
            }
        }
    }

    public static List<String> getGradesDefault() {
        return Grades_Default;
    }

    public static List<String> getGradesDefault(int startIndex) {
        if (startIndex >= Grades_Default.size()) {
            return new ArrayList<>();
        } else {
            return Grades_Default.subList(startIndex, Grades_Default.size());
        }
    }


    /*
     *
     *
     *
     */
    public static class BaseInfo {
        private static final List<String> ONLY_ONE_LEVEL=new ArrayList<>(Arrays.asList("北京市","天津市","重庆市","上海市","香港","澳门","钓鱼岛","其他"));

        @SerializedName("age")
        public String[] age;
        @SerializedName("height")
        public String[] height;
        @SerializedName("nickname")
        public String[] nickname;

        @SerializedName("school")
        public String[] school;
        @SerializedName("college")
        public String[] college;
        @SerializedName("grade")
        public String[] grade;


        @SerializedName("hometown")
        public String[] hometown;
        @SerializedName("location")
        public String[] location;

        public String getNicknameStr() {
            if (nickname == null || nickname.length == 0) return "";
            return nickname[0];
        }

        public String getAgeStr() {
            if (age == null || age.length == 0) return "";
            if (age.length == 1) {
                return age[0];
            } else {
                return age[0] + "~" + age[1];
            }
        }

        public String getHeightStr() {
            if (height == null || height.length == 0) return "";
            if (height.length == 1) {
                return height[0];
            } else {
                return height[0] + "~" + height[1];
            }
        }
        public String getOneHeightStr() {
            if (height == null || height.length == 0) return "";
            return height[0];
        }

        public String getSchoolStr() {
            if (school == null || school.length == 0) return "";
            return school[0];
        }

        public String getCollegeStr() {
            if (college == null || college.length == 0) return "";
            return college[0];
        }

        public String getGradeStr() {
            if (grade == null || grade.length == 0) return "";
            return grade[0];
        }

        public String getLocationStr() {
            if (location == null || location.length == 0) return "";
            if (location.length == 1) {
                return location[0];
            } else {
                return location[0] + "" + location[1];
            }
        }

        public String getHometownStr() {
            if (hometown == null || hometown.length == 0) return "";
            if (hometown.length == 1) {
                return hometown[0];
            } else {
                return hometown[0] + "" + hometown[1];
            }
        }

        public static boolean isOnlyOneLevel(String province){
            return ONLY_ONE_LEVEL.contains(province);
        }

    }

    public static class FriendStandard {
        @SerializedName("age")
        public String[] age;
        @SerializedName("grade")
        public String[] grade;
        @SerializedName("height")
        public String[] height;

        public String getAgeStr() {
            if (age == null || age.length == 0) return "";
            if (age.length == 1) {
                return age[0];
            } else {
                return age[0] + "~" + age[1];
            }
        }

        public String getHeightStr() {
            if (height == null || height.length == 0) return "";
            if (height.length == 1) {
                return height[0];
            } else {
                return height[0] + "~" + height[1];
            }
        }

        public String getGradeStr() {
            if (grade == null || grade.length == 0) return "";
            if (grade.length == 1) {
                return grade[0];
            } else {
                return grade[0] + "~" + grade[1];
            }
        }
    }

    public static class Hobby {

        public static final int SPORT = 1;
        public static final int DIET = 2;
        public static final int DRINK = 3;
        public static final int BOOK = 4;
        public static final int VIDEO = 5;
        public static final int LEISURE = 6;
        @SerializedName("sport")
        public List<String> sport;
        @SerializedName("diet")
        public List<String> diet;
        @SerializedName("drink")
        public List<String> drink;
        @SerializedName("book")
        public List<String> book;
        @SerializedName("video")
        public List<String> video;
        @SerializedName("leisure")
        public List<String> leisure;

        public boolean noBook() {
            return book == null || (book).isEmpty();
        }

        public boolean noDiet() {
            return diet == null || (diet).isEmpty();
        }

        public boolean noDrink() {
            return drink == null || (drink).isEmpty();
        }

        public boolean noLeisure() {
            return leisure == null || (leisure).isEmpty();
        }

        public boolean noSport() {
            return sport == null || (sport).isEmpty();
        }

        public boolean noVideo() {
            return video == null || (video).isEmpty();
        }

        public boolean noHobbyItem(int type){
            return getHobbyItem(type)==null||getHobbyItem(type).isEmpty();
        }

        public List<String> getHobbyItem(int type){
            switch (type){
                case SPORT:{
                    return sport;
                }
                case DIET : {
                    return diet;
                }
                case DRINK : {
                    return drink;
                }
                case BOOK : {
                    return book;
                }
                case VIDEO : {
                    return video;
                }
                case LEISURE : {
                    return leisure;
                }
                default:{
                    return sport;
                }
            }
        }

        public int getIconId(int type){
            switch (type){
                case SPORT:{
                    return R.mipmap.icon_sport;
                }
                case DIET : {
                    return R.mipmap.icon_diet;
                }
                case DRINK : {
                    return R.mipmap.icon_drink;
                }
                case BOOK : {
                    return R.mipmap.icon_book;
                }
                case VIDEO : {
                    return R.mipmap.icon_viedo;
                }
                case LEISURE : {
                    return R.mipmap.icon_leisure;
                }
                default:{
                    return R.mipmap.icon_sport;
                }
            }
        }



        private static List<String> merge(List<String> target, List<String> source) {
            if (source == null || source.isEmpty() || target == null) return target;
            for (String s : source) {
                if (!target.contains(s)) {
                    target.add(s);
                }
            }
            return target;
        }

        public List<String> getDefaultSport() {
            List<String> list = new ArrayList<>(Arrays.asList("游泳", "跑步", "羽毛球", "单车", "乒乓球", "爬山", "台球"));
            return merge(list, sport);
        }

        public List<String> getDefaultDiet() {
            List<String> list = new ArrayList<>(Arrays.asList("滴辣不沾", "无辣不欢", "无肉不欢", "素食主义者", "麻辣烫", "中餐", "西餐", "粤菜", "东北菜"));
            return merge(list, diet);
        }

        public List<String> getDefaultDrink() {
            List<String> list = new ArrayList<>(Arrays.asList("咖啡", "茶"));
            return merge(list, drink);
        }

        public List<String> getDefaultBook() {
            List<String> list = new ArrayList<>(Arrays.asList("很少看书", "金庸", "古龙", "鲁迅", "韩寒", "郭敬明", "张爱玲", "曹雪芹", "心理学", "文学", "哲学", "自然科学", "社会科学"));
            return merge(list, book);
        }

        public List<String> getDefaultVideo() {
            List<String> list = new ArrayList<>(Arrays.asList("很少看视频", "韩剧", "美剧", "动漫", "综艺", "娱乐", "搞笑", "连续剧", "电影", "科技", "权力的游戏", "生活大爆炸", "火影忍者", "海贼王"));
            return merge(list, video);
        }

        public List<String> getDefaultLeisure() {
            List<String> list = new ArrayList<>(Arrays.asList("摄影", "阅读", "电影", "酒吧", "KTV", "旅游", "音乐", "上网", "网络游戏"));
            return merge(list, leisure);
        }
/*
     运动：
    "游泳", "跑步", "羽毛球", "单车", "乒乓球", "爬山", "台球"

    美食：
    "滴辣不沾", "无辣不欢", "无肉不欢", "素食主义者", "麻辣烫", "中餐", "西餐", "粤菜", "东北菜"

    书籍：
    "很少看书", "金庸", "古龙", "鲁迅", "韩寒", "郭敬明", "张爱玲", "曹雪芹", "心理学", "文学", "哲学", "自然科学", "社会科学"

    视频：
    "很少看视频", "韩剧", "美剧", "动漫", "综艺", "娱乐", "搞笑", "连续剧", "电影", "科技", "权力的游戏", "生活大爆炸", "火影忍者", "海贼王"

    休闲：
    "摄影", "阅读", "电影", "酒吧", "KTV", "旅游", "音乐", "上网", "网络游戏"
*/
    }
}
