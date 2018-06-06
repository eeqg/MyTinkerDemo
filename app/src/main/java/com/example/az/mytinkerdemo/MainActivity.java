package com.example.az.mytinkerdemo;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Tinker局限性：
 * <li>1、Tinker不支持修改AndroidManifest.xml
 * <li>2、Tinker不支持新增四大组件
 * <li>3、在Android N上，补丁对应用启动时间有轻微的影响;
 * <li>4、不支持部分三星android-21机型，加载补丁时会主动抛异常;
 * <li>5、在1.7.6以及之后的版本，tinker不再支持加固的动态更新；
 * <li>6、对于资源替换，不支持修改remoteView。例如transition动画，notification icon以及桌面图标。
 * <li>7、任何热修复技术都无法做到100%的成功修复。
 */

public class MainActivity extends AppCompatActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		requestPermissions();
		
		TextView tvDes = findViewById(R.id.tvDes);
		View btnLoad = findViewById(R.id.btnLoad);
		btnLoad.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				TinkerManager.loadPatch(MainActivity.this);
			}
		});
	}
	
	/**
	 * 请求权限
	 */
	private void requestPermissions() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			PermissionUtils.build(this)
					.setOnPermissionListener(new PermissionUtils.OnPermissionListener() {
						@Override
						public void onGranted() {
						}
						
						@Override
						public void onDenied() {
						}
						
						@Override
						public void onRationale(String... permissions) {
							requestPermission(permissions);
						}
					})
					.requestPermissions(
							Manifest.permission.WRITE_EXTERNAL_STORAGE,
							Manifest.permission.READ_EXTERNAL_STORAGE
					);
		}
	}
	
	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onMessageEvent(MessageEvent messageEvent) {
		if (messageEvent.eventType != Const.EVENT_TYPE_PATCH_RESULT) {
			return;
		}
		boolean isSuccess = (boolean) messageEvent.data;
		Log.d("test_wp", "-------------->> isSuccess =  " + isSuccess);
		if (isSuccess) {
			showDialog();
		}
	}
	
	private void showDialog() {
		new AlertDialog.Builder(this)
				.setMessage("修复成功, 重启生效.\n 立即重启吗?")
				.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int i) {
						restartApplication();
						TinkerManager.killSelf(MainActivity.this);
					}
				})
				.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int i) {
					
					}
				}).show();
	}
	
	private void restartApplication() {
		final Intent intent = getPackageManager().getLaunchIntentForPackage(getPackageName());
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		EventBus.getDefault().register(this);
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		EventBus.getDefault().unregister(this);
	}
}
