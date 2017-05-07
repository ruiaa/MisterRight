package com.rui;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by ruiaa on 2016/11/20.
 */

public class GenerateValueFiles {

    /*  my base
     *
     *  base :   px  720*1280  dpi 320  x2
     *
     *  android  dp: 360*640
     *
     */

    /*
     *
     *  以屏幕宽度dp值划分
     *
     *  当前设计图为360dp
     *
     *  values/dimens.xml 为基准值
     *  使用的屏幕宽度为360dp
     *
     *
     *  屏幕兼容测试
     *
     *  320dp  480*800
     *
     *
     */
    private static int baseWidthDp=360;
    private static final String Screen_Width_Dp = "300;320;340;360;400;440;480;520;600;700;820;";

    private static String Res_Dir = "E:\\androidstudio\\MisterRight\\Android\\MisterRight\\app\\src\\main\\res";
    private static String Base_dimen_Path="E:\\androidstudio\\MisterRight\\Android\\MisterRight\\app\\src\\main\\res\\values\\dimens.xml";

    private final static String Dp_Template="<dimen name=\"dp_{0}\">{1}dp</dimen>\n";
    private final static String Sp_Template="<dimen name=\"sp_{0}\">{1}sp</dimen>\n";

    private final static String Value_Template = "values-sw{0}dp";


    public static void  generateBaseDpSp(){
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(Dp_Template.replace("{0}",String.valueOf(0.5)).replace("{1}",String.valueOf(0.5)));
        for(int i=1;i<=1000;i++){
            stringBuilder.append(Dp_Template.replace("{0}",String.valueOf(i)).replace("{1}",String.valueOf(i)));
        }

        stringBuilder.append(Sp_Template.replace("{0}",String.valueOf(0.5)).replace("{1}",String.valueOf(0.5)));
        for(int i=1;i<=40;i++){
            stringBuilder.append(Sp_Template.replace("{0}",String.valueOf(i)).replace("{1}",String.valueOf(i)));
        }

        File baseFile = new File(Res_Dir + File.separator + "base_dimens.xml");
        try {
            PrintWriter pw = new PrintWriter(new FileOutputStream(baseFile));
            pw.print(stringBuilder.toString());
            pw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }


    public static void generateEvery(int target,int base) {

        float scale=((float) target)/((float)base);

        File baseSizeFile = new File(Base_dimen_Path);
        BufferedReader reader = null;
        PrintWriter out=null;
        StringBuilder stringBuilder=new StringBuilder();


        try {
            System.out.println("生成不同分辨率：--to--"+target+"  --from--"+base);
            reader = new BufferedReader(new FileReader(baseSizeFile));
            String tempString;
            int line = 1;

            while ((tempString = reader.readLine()) != null) {
                if (tempString.contains("</dimen>")) {
                    //tempString = tempString.replaceAll(" ", "");
                    String start = tempString.substring(0, tempString.indexOf(">") + 1);
                    String end = tempString.substring(tempString.lastIndexOf("<") - 2);
                    float num = Float.valueOf(tempString.substring(tempString.indexOf(">") + 1, tempString.indexOf("</dimen>") - 2));
                    int resultNum=(int)(num*scale);
                    if (resultNum!=0||num==0){
                        stringBuilder.append(start).append(resultNum).append(end).append("\n");
                    }else{
                        stringBuilder.append(start).append(0.5).append(end).append("\n");
                    }
                } else {
                    stringBuilder.append(tempString).append("\n");
                }
                line++;
            }
            reader.close();

            File valueDir = new File(Res_Dir + File.separator+ Value_Template.replace("{0}", String.valueOf(target)));
            valueDir.mkdir();
            String valueDirName=valueDir.getAbsolutePath()+File.separator+"dimens.xml";

            out = new PrintWriter(new BufferedWriter(new FileWriter(valueDirName)));
            out.println(stringBuilder.toString());
            out.close();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            if (out != null) {
                out.close();
            }
        }
    }


    public static void main(String[] args) {
        System.out.print("start_generate\n");

        //generateBaseDpSp();
        String[] targets = Screen_Width_Dp.split(";");
        for (String target : targets) {
            generateEvery(Integer.valueOf(target),baseWidthDp);
        }
    }
}
