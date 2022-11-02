package com.NewElectric.app11.model.dao.sqlLite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


/**
 * 换电信息 数据库操作
 */


public class ExchangeInfoDB extends SQLiteOpenHelper {

    // 数据库文件名
    public static final String DB_NAME = "hello_client.db";
    // 数据库表名
    public static final String TABLE_NAME = "exchange";
    // 数据库版本号
    public static final int DB_VERSION = 1;

    //单例
    private static ExchangeInfoDB instance = null;
    public synchronized static ExchangeInfoDB getInstance(Context context) {
        if (instance == null) {
            instance = new ExchangeInfoDB(context);
        }
        return instance;
    }
    public ExchangeInfoDB(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    public static final String NUMBER = "number";
    public static final String UID = "uid";
    public static final String EXTIME = "extime";
    public static final String IN_BATTERY = "in_battery";
    public static final String IN_DOOR = "in_door";
    public static final String IN_ELECTRIC = "in_electric";
    public static final String OUT_BATTERY = "out_battery";
    public static final String OUT_DOOR = "out_door";
    public static final String OUT_ELECTRIC = "out_electric";

    // 当数据库文件创建时，执行初始化操作，并且只执行一次
    @Override
    public void onCreate(SQLiteDatabase db) {
        // 建表
        String sql = "create table " +
                TABLE_NAME +
                "(_id integer primary key autoincrement, " +
                NUMBER + " varchar, " +
                UID + " varchar, " +
                EXTIME + " varchar, " +
                IN_BATTERY + " varchar, " +
                IN_DOOR + " varchar, " +
                IN_ELECTRIC + " varchar, " +
                OUT_BATTERY + " varchar, " +
                OUT_DOOR + " varchar, " +
                OUT_ELECTRIC + " varchar"
                + ")";

        db.execSQL(sql);
    }

    // 当数据库版本更新执行该方法
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    private Cursor getCursor(){
        Cursor cursor = this.getWritableDatabase().query(ExchangeInfoDB.TABLE_NAME,
                new String[]{ExchangeInfoDB.NUMBER, ExchangeInfoDB.UID, ExchangeInfoDB.EXTIME, ExchangeInfoDB.IN_BATTERY, ExchangeInfoDB.IN_DOOR, ExchangeInfoDB.IN_ELECTRIC, ExchangeInfoDB.OUT_BATTERY, ExchangeInfoDB.OUT_DOOR, ExchangeInfoDB.OUT_ELECTRIC},
                null,
                null,
                null,
                null,
                null);
        return cursor;
    }

    public void insertData(OutLineExchangeSaveInfo outLineExchangeSaveInfo){
        //插入数据库
        ContentValues values = new ContentValues();
        values.put(ExchangeInfoDB.NUMBER, outLineExchangeSaveInfo.getNumber());
        values.put(ExchangeInfoDB.UID, outLineExchangeSaveInfo.getUid());
        long timeStamp = System.currentTimeMillis();
        values.put(ExchangeInfoDB.EXTIME, timeStamp + "");
        values.put(ExchangeInfoDB.IN_BATTERY, outLineExchangeSaveInfo.getInBattery());
        values.put(ExchangeInfoDB.IN_DOOR, outLineExchangeSaveInfo.getInDoor());
        values.put(ExchangeInfoDB.IN_ELECTRIC, outLineExchangeSaveInfo.getInElectric());
        values.put(ExchangeInfoDB.OUT_BATTERY, outLineExchangeSaveInfo.getOutBattery());
        values.put(ExchangeInfoDB.OUT_DOOR, outLineExchangeSaveInfo.getOutDoor());
        values.put(ExchangeInfoDB.OUT_ELECTRIC, outLineExchangeSaveInfo.getOutElectric());
        this.getWritableDatabase().insert(ExchangeInfoDB.TABLE_NAME, null, values);
    }


    //获取数据库最新的一条数据
    public OutLineExchangeSaveInfo getLastInfo(){

        OutLineExchangeSaveInfo outLineExchangeSaveInfo = null;

        Cursor cursor = getCursor();

        int numberIndex = cursor.getColumnIndex(ExchangeInfoDB.NUMBER);
        int uidIndex = cursor.getColumnIndex(ExchangeInfoDB.UID);
        int extimeIndex = cursor.getColumnIndex(ExchangeInfoDB.EXTIME);
        int inBatteryIndex = cursor.getColumnIndex(ExchangeInfoDB.IN_BATTERY);
        int inDoorIndex = cursor.getColumnIndex(ExchangeInfoDB.IN_DOOR);
        int inElectricIndex = cursor.getColumnIndex(ExchangeInfoDB.IN_ELECTRIC);
        int outBatteryIndex = cursor.getColumnIndex(ExchangeInfoDB.OUT_BATTERY);
        int outDoorIndex = cursor.getColumnIndex(ExchangeInfoDB.OUT_DOOR);
        int outElectricIndex = cursor.getColumnIndex(ExchangeInfoDB.OUT_ELECTRIC);

        if (cursor.moveToFirst()) {
            outLineExchangeSaveInfo = new OutLineExchangeSaveInfo(
                    cursor.getString(numberIndex),
                    cursor.getString(uidIndex),
                    cursor.getString(extimeIndex),
                    cursor.getString(inBatteryIndex),
                    cursor.getString(inDoorIndex),
                    cursor.getString(inElectricIndex),
                    cursor.getString(outBatteryIndex),
                    cursor.getString(outDoorIndex),
                    cursor.getString(outElectricIndex));
        }

        return outLineExchangeSaveInfo;
    }

    //删除本地指定数据库
    public int deleteData(String extime){
        int count = this.getWritableDatabase().delete(ExchangeInfoDB.TABLE_NAME, ExchangeInfoDB.EXTIME + " = ?", new String[]{extime});
        return count;
    }

    //获取当前数据库还有多少条数据
    public int getCount(){
        Cursor cursor = getCursor();
        return cursor.getCount();
    }
}