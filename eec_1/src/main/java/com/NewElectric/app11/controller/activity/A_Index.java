package com.NewElectric.app11.controller.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.NewElectric.app11.R;
import com.NewElectric.app11.config.SystemConfig;
import com.NewElectric.app11.controller.activity.A_Main.A_Main_Hello;
import com.NewElectric.app11.controller.activity.A_Main.A_Main_MiXiang;
import com.NewElectric.app11.controller.custom.BlackExchangeAnimation;
import com.NewElectric.app11.model.dao.sharedPreferences.CabInfoSp;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;

/**
 * Created by guo on 2017/12/2.
 * 引导页
 */

public class A_Index extends Activity {

    private Activity activity;

    @BindView(R.id.version)
    TextView version;
    @BindView(R.id.user)
    TextView user;
    @BindView(R.id.title)
    TextView title;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_index);
        ButterKnife.bind(this);
        activity = this;

        if(SystemConfig.getServer(new CabInfoSp(activity).getServer()) == SystemConfig.serverEnum.mixiang){
            title.setText("欢迎使用长城赛阳换电");
            version.setText("版本 : " + new CabInfoSp(activity).getVersion());
            user.setText("");
        }else{
            title.setText("欢迎使用哈喽换电");
            version.setText("版本 : " + new CabInfoSp(activity).getVersion());
            user.setText("北京兴达智联科技有限公司");
        }

        Observable.timer(2, TimeUnit.SECONDS)
                //把结果处理到哪个线程中
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        if(SystemConfig.getServer(new CabInfoSp(activity).getServer()) == SystemConfig.serverEnum.mixiang){
                            activity.startActivity(new Intent(activity, A_Main_MiXiang.class));
                        }else{
                            activity.startActivity(new Intent(activity, A_Main_Hello.class));
                        }
                        activity.finish();
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.out.println("Activity：A_Index onDestroy");
    }
}
