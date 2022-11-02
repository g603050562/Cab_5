package com.NewElectric.app11.service.logic.logic_movies;

import android.app.Activity;

import com.wonderkiln.camerakit.CameraKit;
import com.wonderkiln.camerakit.CameraKitError;
import com.wonderkiln.camerakit.CameraKitEvent;
import com.wonderkiln.camerakit.CameraKitEventListener;
import com.wonderkiln.camerakit.CameraKitImage;
import com.wonderkiln.camerakit.CameraKitVideo;
import com.wonderkiln.camerakit.CameraView;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.NewElectric.app11.units.FilesDirectoryUnits;

public class MoviesController {

    private Activity activity;
    private CameraView cameraView;
    private int moviesThreadCode = 0;
    private File tempFile = null;
    private Thread moviesThread;

    public MoviesController(Activity mActivity, CameraView cameraView) {
        this.activity = mActivity;
        this.cameraView = cameraView;
        cameraView.setVideoBitRate(10);
        cameraView.setVideoQuality(CameraKit.Constants.VIDEO_QUALITY_480P);
        cameraView.addCameraKitListener(new CameraKitEventListener() {
            @Override
            public void onEvent(CameraKitEvent cameraKitEvent) {

            }

            @Override
            public void onError(CameraKitError cameraKitError) {

            }

            @Override
            public void onImage(CameraKitImage cameraKitImage) {

            }

            @Override
            public void onVideo(CameraKitVideo cameraKitVideo) {
                System.out.println("movies：   录制完成   文件大小 - " + tempFile.length());
                System.out.println("movies：   录制完成   文件地址 - " + tempFile.getAbsolutePath());
            }
        });
    }

    private void captureVideo() {
        tempFile = getOutputMediaFile(1);
        cameraView.captureVideo(tempFile);
    }

    private void stopVideo() {
        try {
            cameraView.stopVideo();
        } catch (Exception e) {

        }
    }

    public void onResume() {

        cameraView.start();
        moviesThreadCode = 0;

        if (moviesThread == null) {
            moviesThread = new Thread() {
                @Override
                public void run() {
                    super.run();
                    while (moviesThreadCode == 0 || moviesThreadCode == 2) {
                        try {
                            sleep(1000);

                            Calendar calendar = Calendar.getInstance();
                            int second = calendar.get(Calendar.SECOND);
                            if (second == 0) {

                                /**
                                 * 录制视频
                                 */
                                if(moviesThreadCode == 0){
                                    System.out.println("movies：   开始录制视频");
                                    stopVideo();
                                    sleep(500);
                                    captureVideo();
                                }else{
                                    System.out.println("movies：   录像挂起");
                                }

                                /**
                                 * 删除文件
                                 */
                                String moviesFilePath = FilesDirectoryUnits.getInstance().EXTERNAL_MOVIES_DIR;
                                File file = new File(moviesFilePath);
                                File[] subFile = file.listFiles();

                                if (subFile == null) {
                                    System.out.println("movies：   找不到文件夹 - return");
                                    return;
                                }

                                String timeStamp_1 = new SimpleDateFormat("yyyyMMdd").format(new Date());
                                for (int iFileLength = 0; iFileLength < subFile.length; iFileLength++) {
                                    //获取日子信息 比较相差几天
                                    String filename_1 = subFile[iFileLength].getName();
                                    Map<String, Long> diff = dateDiff(filename_1, timeStamp_1, "yyyyMMdd");
                                    Long diff_day = diff.get("day");
                                    Long diff_hour = diff.get("hour");

                                    //相差几天开始判断
                                    if (diff_day >= 1 || diff_hour >= 6) {
                                        String moviesFilePath_1 = FilesDirectoryUnits.getInstance().EXTERNAL_MOVIES_DIR + File.separator + filename_1;
                                        File file_1 = new File(moviesFilePath_1);
                                        File[] subFile_1 = file_1.listFiles();
                                        if (subFile_1 == null) {
                                            return;
                                        }
                                        //如果日子里面还有 每个小时的时间 就每一分钟删除一个小时的视频
                                        if (subFile_1.length > 0) {
                                            String filename_2 = subFile_1[0].getName();
                                            String moviesFilePath_2 = FilesDirectoryUnits.getInstance().EXTERNAL_MOVIES_DIR + File.separator + filename_1 + File.separator + filename_2;
                                            deleteDir(moviesFilePath_2);
                                            System.out.println("movies：" + "   正在删除" + timeStamp_1 + filename_2 + "文件夹");
                                            break;
                                        } else { //如果日子里面没有时间了 删掉这个文件夹
                                            deleteDir(moviesFilePath_1);
                                        }
                                    }
                                }
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                            System.out.println("movies：   ERROR - " + e.toString());
                            break;
                        }
                    }
                }
            };
            moviesThread.start();
        }
    }

    public void onPause() {
        cameraView.stop();
        moviesThreadCode = 2;
    }


    public void onDestroy() {
        cameraView.stop();
        moviesThreadCode = 1;
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

    public static void deleteDir(String path) {
        File dir = new File(path);
        deleteDirWihtFile(dir);
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



    /**
     * Create a File for saving an image or video
     */
    private static File getOutputMediaFile(int type) {

        File mediaStorageDir = new File(FilesDirectoryUnits.EXTERNAL_MOVIES_DIR);
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                System.out.println("movies：   创建目录MyCameraApp失败");
                return null;
            }
        }
        String timeStamp_1 = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String timeStamp_2 = new SimpleDateFormat("HH").format(new Date());
        String path_1 = mediaStorageDir.getPath() + File.separator + timeStamp_1;
        String path_2 = path_1 + File.separator + timeStamp_2;
        File file_1 = new File(path_1);
        if (!file_1.exists()) {
            //创建文件夹
            file_1.mkdirs();
            System.out.println("movies：   创建目录一成功");
        } else {
        }
        File file_2 = new File(path_2);
        if (!file_2.exists()) {
            //创建文件夹
            file_2.mkdirs();
            System.out.println("movies：   创建目录一成功");
        } else {
        }
        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String timeString = Calendar.getInstance().getTimeInMillis() + "";
        File mediaFile = new File(file_2 + File.separator + "VID_" + timeString + "_" + timeStamp + ".mp4");
        return mediaFile;
    }
}
