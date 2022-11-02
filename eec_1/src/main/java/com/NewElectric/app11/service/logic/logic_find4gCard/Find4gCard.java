package com.NewElectric.app11.service.logic.logic_find4gCard;

import android.content.Context;
import android.telephony.TelephonyManager;

import java.util.Observer;
import java.util.concurrent.TimeUnit;

import com.NewElectric.app11.hardwarecomm.androidHard.DataFormat;
import com.NewElectric.app11.model.dao.sharedPreferences.CabInfoSp;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class Find4gCard extends java.util.Observable {

    public interface Find4gCardListener{
        void dataReturn();
    }

    //单例
    private static Find4gCard instance = new Find4gCard();
    private Find4gCard() {}
    public static Find4gCard getInstance() {
        return instance;
    }

    private Disposable disposable;
    private int count = 0;
    private String imsi = "";


    private void sendData(Find4gCardReturnDataFormat find4gCardReturnDataFormat){
        setChanged();
        notifyObservers(new DataFormat<>("find4G",find4gCardReturnDataFormat));
    }

    public void addMyObserver(Observer observer){
        addObserver(observer);
        if(imsi!=null && !imsi.equals("")){
            sendData(new Find4gCardReturnDataFormat("success",imsi));
        }
    }

    public void find4gInit(Context context , Find4gCardListener find4gCardListener){
        //每两秒更新数据
        disposable =  Observable.interval(0, 500, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        TelephonyManager mTelephonyMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                        imsi = mTelephonyMgr.getSubscriberId();
                        if (imsi == null || imsi.equals("") || imsi.equals("null")) {
                        } else {
                            sendData(new Find4gCardReturnDataFormat("success",imsi));
                            new CabInfoSp(context).setCabinetNumber_4600XXXX(imsi);
                            find4gCardListener.dataReturn();
                            onDestroy();
                        }
                        count = count + 1;
                        if (count > 60) {
                            sendData(new Find4gCardReturnDataFormat("error",""));
                            onDestroy();
                        }
                    }
                });
    }

    public void onDestroy(){
        disposable.dispose();
    }
}
