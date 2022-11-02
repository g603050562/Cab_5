package com.NewElectric.app11.service.logic.logic_exchange;

import java.math.BigInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 62进制UID解析
 */
public class UidDictionart {

    private static String dictionary = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";


    /**
     * 获取手机后四位号码
     * @param notConString 传入的UID（支持大小写 32进制 62进制自动识别）
     * @return
     */
    public static String getI10EndPhoneNumber(String notConString) {

        String phoneReturn = "";
        String fristChar = notConString.substring(0, 1);
        //32进制
        if (isNumeric(fristChar) == true) {
            String fUid10 = Long.parseLong(notConString, 32) + "";
            phoneReturn = fUid10.substring(fUid10.length() - 5, fUid10.length() - 1);
        }
        //62进制
        else {
            String fUid10 = get_S62_To_I10(notConString) + "";
            if (fUid10.equals("-1")) {
                phoneReturn = "-1";
            } else {
                phoneReturn = fUid10.substring(fUid10.length() - 7, fUid10.length() - 3);
            }
        }
        return phoneReturn;
    }

    /**
     * 获取手机整体手机号
     * @param notConString 传入的UID（支持大小写 32进制 62进制自动识别）
     * @return
     */
    public static String getI10PhoneNumber(String notConString) {

        String phoneReturn = "";
        String fristChar = notConString.substring(0, 1);
        //32进制
        if (isNumeric(fristChar) == true) {
            String fUid10 = Long.parseLong(notConString, 32) + "";
            phoneReturn = fUid10.substring(0, fUid10.length() - 1);
        }
        //62进制
        else {
            String fUid10 = get_S62_To_I10(notConString) + "";
            if (fUid10.equals("-1")) {
                phoneReturn = "-1";
            } else {
                phoneReturn = fUid10.substring(0, fUid10.length() - 3);
            }
        }
        return phoneReturn;
    }


    /**
     * 内部类 62转10进制转换
     * @param S62
     * @return
     */
    public static long get_S62_To_I10(String S62) {

        int status = 0;
        long total = 0;
        for (int i = 0; i < S62.length(); i++) {
            long param = (long) Math.pow(62, i);
            String paramString = S62.substring(S62.length() - 1 - i, S62.length() - i);
            long I10 = dictionary.indexOf(paramString);
            if (I10 == -1) {
                status = 1;
            } else {
                total = total + (param * I10);
            }
        }
        if (status == 0) {
            return total;
        } else {
            return -1;
        }
    }

    /**
     * 内部类 10转62进制转换
     * @return
     */
    public static String get_T10_To_S62(String num_str) {
        BigInteger num = new BigInteger(num_str);
        BigInteger scale = new BigInteger("62");
        BigInteger scaleSubtract_1 = new BigInteger("61");

        StringBuilder sb = new StringBuilder();
        int remainder;
        while (num.compareTo(scaleSubtract_1) == 1) {
            //对 scale 进行求余，然后将余数追加至 sb 中，由于是从末位开始追加的，因此最后需要反转字符串
            BigInteger[] resBigIntegers = num.divideAndRemainder(scale);
            remainder = resBigIntegers[1].intValue();
            sb.append(dictionary.substring(remainder , remainder + 1));
            //除以进制数，获取下一个末尾数
            num = resBigIntegers[0];
        }
        final int fNum = num.intValue();
        sb.append(dictionary.substring(fNum , fNum + 1));
        String value = sb.reverse().toString();
        return value;
    }

    //正则
    public static boolean isNumeric(String unknownNum) {
        Pattern pattern = Pattern.compile("[0-9]+");
        Matcher isNum = pattern.matcher(unknownNum);
        if (!isNum.matches()) {
            return false;
        }
        return true;
    }
}
