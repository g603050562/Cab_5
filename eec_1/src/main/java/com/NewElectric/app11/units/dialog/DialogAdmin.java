package com.NewElectric.app11.units.dialog;

import android.app.Activity;
import android.app.Dialog;

import java.util.concurrent.TimeUnit;

import com.NewElectric.app11.R;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

public class DialogAdmin {
	private Dialog progressDialog;
	private Activity activity;


	public DialogAdmin(Activity activity) {
		this.activity = activity;
		init();
	}
	
	private void init(){
		if(activity == null){
			return;
		}

		progressDialog = new Dialog(activity, R.style.progress_dialog);
		progressDialog.setContentView(R.layout.alertdialog_admin);
		progressDialog.setCancelable(true);
		progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
	}

	public void show(){
		if(progressDialog == null){
			init();
		}
		progressDialog.show();
	}
	
	public void showByTime(int time){
		Observable.timer(time, TimeUnit.SECONDS).subscribeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Long>() {
			@Override
			public void onSubscribe(@NonNull Disposable d) {
				progressDialog.show();
			}

			@Override
			public void onNext(@NonNull Long aLong) {

			}

			@Override
			public void onError(@NonNull Throwable e) {

			}

			@Override
			public void onComplete() {
				progressDialog.dismiss();
			}
		});
	}

	
	public void dismiss(){
		if(progressDialog!=null){
			progressDialog.dismiss();
		}
	}
	
	public void destory(){
		progressDialog = null;
	}
}
