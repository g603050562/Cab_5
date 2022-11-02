package com.NewElectric.app11.model.dao.fileSave;

import android.content.Context;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.NewElectric.app11.units.FilesDirectoryUnits;
import com.NewElectric.app11.units.Units;


public class LocalLog {

    private static volatile LocalLog localLog;
    private LocalLog(){}
    public static LocalLog getInstance(){
        if(localLog == null){
            synchronized (LocalLog.class){
                if(localLog == null){
                    localLog = new LocalLog();
                }
            }
        }
        return localLog;
    }

    private Context context;
    private String cabID = "";

    public void init(Context context, String cabID) {
        this.context = context;
        this.cabID = cabID;
    }

    public void writeLog(String string) {
        System.out.println(string);
        //没有sd卡 返回
        if (Units.getIsExistExCard() == 0) {
            return;
        }else{
            //写入日志
            try {
                File file = getOutputTxtFile();
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String writeData = df.format(new Date()) + ":   " + cabID + "   " + string + "";
                FileWriter filerWriter = new FileWriter(file, true);//后面这个参数代表是不是要接上文件中原来的数据，不进行覆盖
                BufferedWriter bufWriter = new BufferedWriter(filerWriter);
                bufWriter.write(writeData);
                bufWriter.newLine();
                bufWriter.close();
                filerWriter.close();
            } catch (FileNotFoundException e) {
                System.out.println("Clog - error - " + e.toString());
                System.out.println("Clog - error - 文件不存在");
                e.printStackTrace();
            } catch (IOException e) {
                System.out.println("Clog - error - " + e.toString());
                e.printStackTrace();
            }
            //删除大于一个星期的log文件夹
            try {
                File file = new File(FilesDirectoryUnits.INTERNAL_LOG_DIR);
                if(file.exists()){
                    //遍历文件夹
                    File[] subFile = file.listFiles();
                    if (subFile == null) {
                        System.out.println("movies：   找不到文件夹 - return");
                        return;
                    }
                    String timeStamp_1 = new SimpleDateFormat("yyyyMMdd").format(new Date());

                    for (int iFileLength = 0; iFileLength < subFile.length; iFileLength++) {
                        //获取日子信息 比较相差几天
                        String filename_1 = subFile[iFileLength].getName();
                        Map<String, Long> diff_1 = dateDiff(filename_1, timeStamp_1, "yyyyMMdd");
                        Long diff_1_day = diff_1.get("day");
                        //相差几天开始判断
                        if (diff_1_day >= 14) {
                            deleteDirWihtFile(subFile[iFileLength].getAbsoluteFile());
                        }
                    }
                }
            }catch (Exception e){
                System.out.println("Clog - 删除日志 - " + e.toString());
            }
        }
    }

    private File getOutputTxtFile() {

        String outSideSd = FilesDirectoryUnits.INTERNAL_LOG_DIR;
        File mediaStorageDir = new File(outSideSd);

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                System.out.println("Clog - error - 创建目录MyLog失败");
                return null;
            }
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String path = mediaStorageDir.getPath() + File.separator + timeStamp;
        File file = new File(path);
        if (!file.exists()) {
            //创建文件夹
            file.mkdirs();
            System.out.println("Clog - 创建日志文件夹成功");
        } else {
        }
        String fileName = new SimpleDateFormat("yyyyMMddHH").format(new Date());
        File mediaFile = new File(path + File.separator + "Log_" + fileName + ".txt");

        return mediaFile;
    }


    public Map<String, Long> dateDiff(String startTime, String endTime, String format) {
        // 按照传入的格式生成一个simpledateformate对象
        SimpleDateFormat sd = new SimpleDateFormat(format);
        long nd = 1000 * 24 * 60 * 60;// 一天的毫秒数
        long nh = 1000 * 60 * 60;// 一小时的毫秒数
        long nm = 1000 * 60;// 一分钟的毫秒数
        long ns = 1000;// 一秒钟的毫秒数
        long diff;
        long day = 0;
        long hour = 0;
        long min = 0;
        long sec = 0;
        try {
            // 获得两个时间的毫秒时间差异
            diff = sd.parse(endTime).getTime() - sd.parse(startTime).getTime();
            day = diff / nd;// 计算差多少天
            hour = diff % nd / nh;// 计算差多少小时
            min = diff % nd % nh / nm;// 计算差多少分钟
            sec = diff % nd % nh % nm / ns;// 计算差多少秒
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Map<String, Long> map = new HashMap<>();
        map.put("day", day);
        map.put("hour", hour);
        map.put("min", min);
        map.put("sec", sec);
        return map;
    }
    //删除文件及文件夹
    public static void deleteDirWihtFile(File dir) {
        if (dir == null || !dir.exists() || !dir.isDirectory())
            return;
        for (File file : dir.listFiles()) {
            if (file.isFile())
                file.delete(); // 删除所有文件
            else if (file.isDirectory())
                deleteDirWihtFile(file); // 递规的方式删除文件夹
        }
        dir.delete();// 删除目录本身
    }

}
