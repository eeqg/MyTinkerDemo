package com.example.az.mytinkerdemo;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.example.az.mytinkerdemo.tinker.Log.MyLogImp;
import com.example.az.mytinkerdemo.tinker.TinkerManagerBase;
import com.tencent.tinker.lib.library.TinkerLoadLibrary;
import com.tencent.tinker.lib.tinker.Tinker;
import com.tencent.tinker.lib.tinker.TinkerInstaller;
import com.tencent.tinker.loader.app.ApplicationLike;
import com.tencent.tinker.loader.shareutil.ShareConstants;
import com.tencent.tinker.loader.shareutil.ShareTinkerInternals;

/**
 * Tinker管理类
 */
public class TinkerManager {
	
	private static final String TAG = "TinkerManager";
	
	private static TinkerManager mInstance;
	
	/** 补丁名称 */
	private static String mPatchName = "patch_signed_7zip.apk";
	/** 补丁ID */
	private static String mPatchID;
	
	/** 是否强制完全退出APP */
	private boolean isForceExit;
	
	public static TinkerManager getInstance() {
		if (mInstance == null) {
			synchronized (TinkerManager.class) {
				mInstance = new TinkerManager();
			}
		}
		return mInstance;
	}
	
	
	public static void init(ApplicationLike applicationLike) {
		TinkerManagerBase.setTinkerApplicationLike(applicationLike);
		
		TinkerManagerBase.initFastCrashProtect();
		TinkerManagerBase.setUpgradeRetryEnable(true);
		
		TinkerInstaller.setLogIml(new MyLogImp());
		
		//installTinker after load multiDex
		//or you can put com.tencent.tinker.** to main dex
		TinkerManagerBase.installTinker(applicationLike);
		// Tinker tinker = Tinker.with(context);
	}
	
	
	/**
	 * 处理热修复信息
	 *
	 * @param status 0: 失败, 1: 成功, 2: 下载补丁包
	 */
	public void handleHotFix(Context context, int status) {
		// if (status == 0) {
		//     LogUtil.debugWithToast(context, "补丁安装失败");
		//     return;
		// }
		// if (status == 1){
		//     LogUtil.debugWithToast(context,"热修复成功，退出APP后生效");
		//     isForceExit = true; // 强制完全退出
		// }else{
		//     LogUtil.d("补丁下载次数统计 +1");
		// }
		// HotfixBusinessAPI.sendHotfixResult(status);
	}
	
	/** 请求热修复补丁信息 */
	public static void loadPatchData(final Context context) {
		//         LogUtil.d("test_wp", "loadPathData()-----TINKER_ID: "+BuildInfo.TINKER_ID);
		//         HotfixBusinessAPI.getHotFix(new OkHttpManager.HttpCallback() {
		//             @Override
		//             public void onResponse(QueryResult result) {
		//                 if (result.isSuccess()){
		//                     final HotFix hotFix = (HotFix) result.getObject();
		//                     // 已经修复的跳过
		//                     LogUtil.d("test_wp", "loadPathData()-----getPatchId: "+hotFix.getPatchId());
		//                     if (hotFix.getPatchId().equals(BuildInfo.TINKER_ID)) {
		//                         LogUtil.debugWithToast(context,TAG,"跳过操作,已安装最新补丁");
		//                         return;
		//                     }
		//                     mPatchID = hotFix.getPatchId();
		//                     String url = hotFix.getPatchUrl();
		//                     if (TextUtils.isEmpty(url)){
		//                         Log.d(TAG,"url = "+url);
		//                         return;
		//                     }
		//
		//                     try {
		//                         mPatchName = TextUtils.substring(url,url.lastIndexOf("/"),url.indexOf("?"));
		//                     } catch (Exception e) {
		//                         e.printStackTrace();
		//                     }
		//                     if (TextUtils.isEmpty(mPatchName)){
		//                         Log.d(TAG,"hotfix patchName为空");
		//                         return;
		//                     }
		//
		//                     DownloadManager.getInstance().downLoadFile(url,
		//                             BaseStorageManager.getAppRootDir(),
		//                             mPatchName,
		//                             new DownloadManager.DownloadCallback() {
		//                                 @Override
		//                                 public void onProgress(long progress, long current, long total) {
		//                                 }
		//
		//                                 @Override
		//                                 public void onSuccess() {
		//                                     LogUtil.debugWithToast(context,TAG,"hotfix 补丁下载成功");
		//                                     LogUtil.d("test_wp", "loadPathData()-----isMustUpdate: "+hotFix.isMustUpdate());
		//                                     if (hotFix.isMustUpdate()) {
		//                                         LogUtil.debugWithToast(context,TAG,"hotfix 补丁强制更新安装");
		//                                         loadPatch(context);
		// //                                    }else{
		// //                                        BaseEventBusManager.post(HotfixEventBusManager.EVENT_SHOW_HOT_FIX_SETUP_DIALOG);
		//                                     }
		//
		//                                     getInstance().handleHotFix(context,2); // 用于统计补丁成功率
		//                                 }
		//
		//                                 @Override
		//                                 public void onFailure() {
		//                                     LogUtil.errorWithToast(context,TAG,"hotfix 补丁下载失败");
		//
		//                                 }
		//                             });
		//
		//
		//                 }
		//             }
		//
		//             @Override
		//             public void onFailure(String error) {
		//
		//             }
		//         });
	}
	
	/** 加载补丁 */
	public static void loadPatch(Context context) {
		Log.d(TAG, "加载hotfix补丁");
		TinkerInstaller.onReceiveUpgradePatch(context,
				Environment.getExternalStorageDirectory().getAbsolutePath() + "/patch_signed_7zip.apk");
	}
	
	/** 清除补丁 */
	public static void cleanPatch(Context context) {
		Log.d(TAG, "清除hotfix补丁");
		Tinker.with(context).cleanPatch();
	}
	
	/** 加载库补丁 */
	public static void loadLibrary(Context context) {
		Log.d(TAG, "清除Library补丁");
		// #method 1, hack classloader library path
		TinkerLoadLibrary.installNavitveLibraryABI(context, "armeabi");
		System.loadLibrary("stlport_shared");
		
		// #method 2, for lib/armeabi, just use TinkerInstaller.loadLibrary
		//                TinkerLoadLibrary.loadArmLibrary(getApplicationContext(), "stlport_shared");
		
		// #method 3, load tinker patch library directly
		//                TinkerInstaller.loadLibraryFromTinker(getApplicationContext(), "assets/x86", "stlport_shared");
		//
	}
	
	/** 强制停止退出使补丁生效 */
	public static void killSelf(Context context) {
		Log.d(TAG, "hotfix 强制停止退出使补丁生效");
		ShareTinkerInternals.killAllOtherProcess(context);
		android.os.Process.killProcess(android.os.Process.myPid());
	}
	
	/** 补丁信息 */
	public static String getInfo(Context context) {
		final StringBuilder sb = new StringBuilder();
		Tinker tinker = Tinker.with(context);
		if (tinker.isTinkerLoaded()) {
			sb.append(String.format("[patch is loaded] \n"));
			sb.append(String.format("[buildConfig TINKER_ID] %s \n", BuildConfig.TINKER_ID));
			sb.append(String.format("[buildConfig BASE_TINKER_ID] %s \n", BaseBuildInfo.BASE_TINKER_ID));
			
			sb.append(String.format("[buildConfig MESSSAGE] %s \n", BaseBuildInfo.TEST_MESSAGE));
			sb.append(String.format("[TINKER_ID] %s \n", tinker.getTinkerLoadResultIfPresent().getPackageConfigByName(ShareConstants.TINKER_ID)));
			sb.append(String.format("[packageConfig patchMessage] %s \n", tinker.getTinkerLoadResultIfPresent().getPackageConfigByName("patchMessage")));
			sb.append(String.format("[TINKER_ID Rom Space] %d k \n", tinker.getTinkerRomSpace()));
			
		} else {
			sb.append(String.format("[patch is not loaded] \n"));
			sb.append(String.format("[buildConfig TINKER_ID] %s \n", BuildConfig.TINKER_ID));
			sb.append(String.format("[buildConfig BASE_TINKER_ID] %s \n", BaseBuildInfo.BASE_TINKER_ID));
			
			sb.append(String.format("[buildConfig MESSSAGE] %s \n", BaseBuildInfo.TEST_MESSAGE));
			sb.append(String.format("[TINKER_ID] %s \n", ShareTinkerInternals.getManifestTinkerID(context)));
		}
		sb.append(String.format("[BaseBuildInfo Message] %s \n", BaseBuildInfo.TEST_MESSAGE));
		return sb.toString();
		
	}
	
	/** 是否强制完全退出APP */
	public boolean isForceExit() {
		return isForceExit;
	}
	
	/** 获取最新的补丁ID */
	public static String getPatchID() {
		return mPatchID;
	}
}
