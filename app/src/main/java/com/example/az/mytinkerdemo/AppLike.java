package com.example.az.mytinkerdemo;

import android.app.Application;
import android.content.Intent;

import com.tencent.tinker.anno.DefaultLifeCycle;
import com.tencent.tinker.loader.app.DefaultApplicationLike;
import com.tencent.tinker.loader.shareutil.ShareConstants;

/**
 * Created by wp on 2018/6/4.
 */

@DefaultLifeCycle(application = ".APP", flags = ShareConstants.TINKER_ENABLE_ALL, loadVerifyFlag = false)
public class AppLike extends DefaultApplicationLike {
	public AppLike(Application application, int tinkerFlags, boolean tinkerLoadVerifyFlag,
	               long applicationStartElapsedTime, long applicationStartMillisTime, Intent tinkerResultIntent) {
		super(application, tinkerFlags, tinkerLoadVerifyFlag, applicationStartElapsedTime, applicationStartMillisTime, tinkerResultIntent);
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		TinkerManager.init(this);
	}
}
