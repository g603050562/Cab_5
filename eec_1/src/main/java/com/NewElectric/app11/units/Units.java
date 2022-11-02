package com.NewElectric.app11.units;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.hardware.Camera;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Hashtable;
import java.util.List;

import static android.content.ContentValues.TAG;

public class Units {

    //转换
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
    //转换
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }
    //查询数组在数组的哪个区间
    public static double[] numSearch(double arr[], double key) {
        double[] a = null;
        int start = 0;
        int end = arr.length - 1;
        while (start <= end) {
            int middle = (start + end) / 2;

            if (key < arr[middle]) {
                start = middle + 1;
            } else if (key > arr[middle]) {
                end = middle - 1;
            } else {
                a = new double[]{middle, start, end};
                return a;
            }
        }
        a = new double[]{start, start, end};
        return a;
    }

    //10进制转2进制 并且补齐
    public static String int2Binary(int a) {
        String tempStr = "";
        String str2 = Integer.toBinaryString(a);
        //判断一下：如果转化为二进制为0或者1或者不满8位，要在数后补0
        int bit = 8 - str2.length();
        if (str2.length() < 8) {
            for (int j = 0; j < bit; j++) {
                str2 = "0" + str2;
            }
        }
        tempStr += str2;
        return tempStr;
    }

    //16进制转2进制 并且补齐
    public static String hexString2binaryString(String hexString) {
        if (hexString == null || hexString.length() % 2 != 0)
            return null;
        String bString = "", tmp;
        for (int i = 0; i < hexString.length(); i++) {
            tmp = "0000" + Integer.toBinaryString(Integer.parseInt(hexString.substring(i, i + 1), 16));
            bString += tmp.substring(tmp.length() - 4);
        }
        return bString;
    }

    public static String hexString2binaryStringAnt(String hexString) {
        if (hexString == null || hexString.length() % 2 != 0)
            return null;
        String bString = "";
        String tmp_1 = "";
        String tmp_2 = "";
        for (int i = 0; i < hexString.length() / 2; i++) {

            String item_2 = "";
            tmp_2 = "0000" + Integer.toBinaryString(Integer.parseInt(hexString.substring(i * 2 + 1, i * 2 + 2), 16));
            item_2 += tmp_2.substring(tmp_2.length() - 4);
            bString = bString + new StringBuilder(item_2).reverse().toString();

            String item_1 = "";
            tmp_1 = "0000" + Integer.toBinaryString(Integer.parseInt(hexString.substring(i * 2, i * 2 + 1), 16));
            item_1 += tmp_1.substring(tmp_1.length() - 4);
            bString = bString + new StringBuilder(item_1).reverse().toString();
        }

        //00040000
        return bString;
    }

    public static char backchar(int backnum) {
        char strChar = (char) backnum;
        return strChar;
    }


    //CRC验证
    public static String getCRC(byte[] bytes) {
        int CRC = 0x0000ffff;
        int POLYNOMIAL = 0x0000a001;
        int i, j;
        for (i = 0; i < bytes.length; i++) {
            CRC ^= ((int) bytes[i] & 0x000000ff);
            for (j = 0; j < 8; j++) {
                if ((CRC & 0x00000001) != 0) {
                    CRC >>= 1;
                    CRC ^= POLYNOMIAL;
                } else {
                    CRC >>= 1;
                }
            }
        }
        return Integer.toHexString(CRC);
    }

    //文件二进制化
    public static byte[] getBytes(File file) {
        byte[] buffer = null;
        try {
            FileInputStream fis = new FileInputStream(file);
            ByteArrayOutputStream bos = new ByteArrayOutputStream(1000);
            byte[] b = new byte[1000];
            int n;
            while ((n = fis.read(b)) != -1) {
                bos.write(b, 0, n);
            }
            fis.close();
            bos.close();
            buffer = bos.toByteArray();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buffer;
    }

    //二进制数组 转 16进制
    public static String ByteArrToHex(byte[] inBytArr) {
        StringBuilder strBuilder = new StringBuilder();
        for (byte valueOf : inBytArr) {
            strBuilder.append(Byte2Hex(Byte.valueOf(valueOf)));
        }
        return strBuilder.toString();
    }

    public static String Byte2Hex(Byte inByte) {
        return String.format("%02x", new Object[]{inByte}).toUpperCase();
    }

    //是否存在内存卡
    public static boolean ExistSDCard() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            System.out.println("存在SD卡!");
            return true;
        } else
            System.out.println("不存在SD卡!");
        return false;
    }

    //查询是否存在扩展卡
    public static int getIsExistExCard(){
        int return_int = 0 ; // 0 - 不存在//  1 - 存在
        File file = new File("/mnt/external_sd/Android");
        if(file.exists()){
            return_int = 1;
        }
        return return_int;
    }

    //判断是有存在前置摄像头
    public static boolean hasCamera() {

        boolean result;
        Camera camera = null;
        try {
            camera = Camera.open();
            if (camera == null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
                boolean connected = false;
                for (int camIdx = 0; camIdx < Camera.getNumberOfCameras(); ++camIdx) {
                    Log.d(TAG, "Trying to open camera with new open(" + Integer.valueOf(camIdx) + ")");
                    try {
                        camera = Camera.open(camIdx);
                        connected = true;
                    } catch (RuntimeException e) {
                        Log.e(TAG, "Camera #" + camIdx + "failed to open: " + e.getLocalizedMessage());
                    }
                    if (connected) {
                        break;
                    }
                }
            }
            List<Camera.Size> supportedPreviewSizes = camera.getParameters().getSupportedPreviewSizes();
            result = supportedPreviewSizes != null;
            /* Finally we are ready to start the preview */
            Log.d(TAG, "startPreview");
            camera.startPreview();
        } catch (Exception e) {
            Log.e(TAG, "Camera is not available (in use or does not exist): " + e.getLocalizedMessage());
            result = false;
        } finally {
            if (camera != null) {
                camera.release();
            }
        }
        return result;
    }

    //判断电池是那种型号的
    public static String bar_60_or_48(String bid) {
        String TopBidStr = bid.substring(0, 1);
        String EndBidStr = bid.substring(10, 12);
        String volt = "";
        if (TopBidStr.equals("M") && EndBidStr.equals("MM")) {
            volt = "60";
        } else if(TopBidStr.equals("N") && EndBidStr.equals("NN")) {
            volt = "48";
        } else if(TopBidStr.equals("M") && EndBidStr.equals("ZZ")) {
            volt = "60";
        }else{
            volt = "48";
        }
        return volt;
    }

    //二维码生成
    public static Bitmap generateBitmap(String str, int width, int height) {

        try {
            Hashtable<EncodeHintType, Object> hints = new Hashtable<EncodeHintType, Object>();
            hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
//            hints.put(EncodeHintType.MARGIN, 1);
            BitMatrix matrix = new QRCodeWriter().encode(str, BarcodeFormat.QR_CODE, width, height);
            matrix = deleteWhite(matrix);//删除白边
            width = matrix.getWidth();
            height = matrix.getHeight();
            int[] pixels = new int[width * height];
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    if (matrix.get(x, y)) {
                        pixels[y * width + x] = Color.BLACK;
                    } else {
                        pixels[y * width + x] = Color.WHITE;
                    }
                }
            }
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
            return bitmap;
        } catch (Exception e) {
            return null;
        }
    }

    //二维码生成
    public static Bitmap generateBitmap(String str, int width, int height , int color_1 , int color_2) {

        try {
            Hashtable<EncodeHintType, Object> hints = new Hashtable<EncodeHintType, Object>();
            hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
//            hints.put(EncodeHintType.MARGIN, 1);
            BitMatrix matrix = new QRCodeWriter().encode(str, BarcodeFormat.QR_CODE, width, height);
            matrix = deleteWhite(matrix);//删除白边
            width = matrix.getWidth();
            height = matrix.getHeight();
            int[] pixels = new int[width * height];
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    if (matrix.get(x, y)) {
                        pixels[y * width + x] = color_1;
                    } else {
                        pixels[y * width + x] = color_2;
                    }
                }
            }
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
            return bitmap;
        } catch (Exception e) {
            return null;
        }
    }


    private static BitMatrix deleteWhite(BitMatrix matrix) {
        int[] rec = matrix.getEnclosingRectangle();
        int resWidth = rec[2] + 1;
        int resHeight = rec[3] + 1;

        BitMatrix resMatrix = new BitMatrix(resWidth, resHeight);
        resMatrix.clear();
        for (int i = 0; i < resWidth; i++) {
            for (int j = 0; j < resHeight; j++) {
                if (matrix.get(i + rec[0], j + rec[1]))
                    resMatrix.set(i, j);
            }
        }
        return resMatrix;
    }
}
