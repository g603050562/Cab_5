package com.NewElectric.app11.units;

import android.content.Context;
import android.os.Environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/3/10 0010.
 * 内存创建文件夹
 */
public class FilesDirectoryUnits {

    private static FilesDirectoryUnits instance = new FilesDirectoryUnits();
    private FilesDirectoryUnits() {}
    public static FilesDirectoryUnits getInstance() {
        return instance;
    }

    private Context context;
    //内置卡路径
    public static String SD_CARD = Environment.getExternalStorageDirectory() + "/";
    public static String INTERNAL_DIR = SD_CARD + "HelloElectricity/";
    public static String INTERNAL_LOG_DIR = INTERNAL_DIR + "MyLog/";
    //外置卡路径
    public static String EXTERNAL_DIR = "";
    public static String EXTERNAL_MOVIES_DIR = "";

    public void init(Context context) {
        this.context = context;
        //内置内存卡初始化
        createInternalSdFile();
        //外置内存卡初始化
        createExternalSdFIle();
    }

    private void createInternalSdFile(){
        if (Units.ExistSDCard()) {
            String filePath = INTERNAL_DIR;
            File file = new File(filePath);
            if (!file.exists()) {
                file.mkdirs();
                System.out.println("在SD上创建HelloElectricity文件夹!");
            } else {
                System.out.println("SD上HelloElectricity文件夹以创建!");
            }

            String musicFilePath = INTERNAL_LOG_DIR;
            File musicfile = new File(musicFilePath);
            if (!musicfile.exists()) {
                musicfile.mkdirs();
                System.out.println("在HelloElectricity文件夹里创建HTML文件夹!");
            } else {
                System.out.println("文件夹HTML在HelloElectricity里已创建!");
            }
        } else {
            String filePath = "/data/data/HelloElectricity/";
            File file = new File(filePath);
            if (file.exists()) {
                System.out.println("项目目录文件夹以创建!");
            } else {
                file.mkdirs();
                System.out.println("在项目目录上创建文件夹!");
            }
        }
    }

    private void createExternalSdFIle(){
        //初始化外部内存卡路径
        EXTERNAL_DIR = getExternalFileDir(context);
        //初始化外部内存卡视频路径
        File dir = new File(EXTERNAL_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        EXTERNAL_MOVIES_DIR = EXTERNAL_DIR + "/MyCameraApp";
        File dir2 = new File(EXTERNAL_MOVIES_DIR);
        if (!dir2.exists()) {
            dir2.mkdirs();
        }
    }

    /**
     * 获取外置SD卡存储文件的绝对路径
     * Android 4.4以后
     *
     * @param context
     */
    private String getExternalFileDir(Context context) {
        StringBuilder sb = new StringBuilder();
        File file = context.getExternalCacheDir();     //此句代码一定要，在内部存储空间创建对应的data目录，但不存储文件
        if (file.exists()) {
            sb.append(getTFSDCardPath().toString()).append("/Android/data/").append(context.getPackageName()).append("/cache").append(File.separator).toString();
        } else {
            sb.append(getTFSDCardPath().toString()).append("/Android/data/").append(context.getPackageName()).append("/cache").append(File.separator).toString();
        }
        return sb.toString();
    }

    private String getTFSDCardPath() {
        List<String> list = getExtSDCardPathList();
        if (list.size() > 1) {
            return list.get(1);
        } else {
            return list.get(0);
        }
    }

    /**
     * 获取外置SD卡路径
     */
    private List<String> getExtSDCardPathList() {
        List<String> paths = new ArrayList<String>();
        String extFileStatus = Environment.getExternalStorageState();
        File extFile = Environment.getExternalStorageDirectory();
        //首先判断一下外置SD卡的状态，处于挂载状态才能获取的到
        if (extFileStatus.equals(Environment.MEDIA_MOUNTED) && extFile.exists() && extFile.isDirectory() && extFile.canWrite()) {
            //外置SD卡的路径
            paths.add(extFile.getAbsolutePath());
        }
        try {
            Runtime runtime = Runtime.getRuntime();
            Process process = runtime.exec("mount");
            InputStream is = process.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line = null;
            int mountPathIndex = 1;
            while ((line = br.readLine()) != null) {
                // format of sdcard file system: vfat/fuse
                if ((!line.contains("fat") && !line.contains("fuse") && !line
                        .contains("storage"))
                        || line.contains("secure")
                        || line.contains("asec")
                        || line.contains("firmware")
                        || line.contains("shell")
                        || line.contains("obb")
                        || line.contains("legacy") || line.contains("data")) {
                    continue;
                }
                String[] parts = line.split(" ");
                int length = parts.length;
                if (mountPathIndex >= length) {
                    continue;
                }
                String mountPath = parts[mountPathIndex];
                if (!mountPath.contains("/") || mountPath.contains("data")
                        || mountPath.contains("Data")) {
                    continue;
                }
                File mountRoot = new File(mountPath);
                if (!mountRoot.exists() || !mountRoot.isDirectory()
                        || !mountRoot.canWrite()) {
                    continue;
                }
                boolean equalsToPrimarySD = mountPath.equals(extFile
                        .getAbsolutePath());
                if (equalsToPrimarySD) {
                    continue;
                }
                //扩展存储卡即TF卡或者SD卡路径
                paths.add(mountPath);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return paths;
    }
}
