package io.openim.flutter_openim_unimp;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.dcloud.feature.sdk.DCSDKInitConfig;
import io.dcloud.feature.sdk.DCUniMPSDK;
import io.dcloud.feature.sdk.Interface.IUniMP;
import io.dcloud.feature.sdk.MenuActionSheetItem;
import io.dcloud.feature.unimp.config.UniMPOpenConfiguration;
import io.dcloud.feature.unimp.config.UniMPReleaseConfiguration;
import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;

/**
 * FlutterOpenimUnimpPlugin
 */
public class FlutterOpenimUnimpPlugin implements FlutterPlugin, MethodCallHandler {
    /// The MethodChannel that will the communication between Flutter and native Android
    ///
    /// This local reference serves to register the plugin with the Flutter Engine and unregister it
    /// when the Flutter Engine is detached from the Activity
    private MethodChannel channel;
    private Context context;
    /**
     * unimp小程序实例缓存
     **/
    HashMap<String, IUniMP> mUniMPCaches = new HashMap<>();

    @Override
    public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
        channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "flutter_openim_unimp");
        channel.setMethodCallHandler(this);
        context = flutterPluginBinding.getApplicationContext();
        initUniMP();
    }

    @Override
    public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
        if ("releaseWgtToRunPath".equals(call.method)) {
            String appID = call.argument("appID");
            String wgtPath = call.argument("wgtPath");
            String password = call.argument("password");
            UniMPReleaseConfiguration uniMPReleaseConfiguration = new UniMPReleaseConfiguration();
            uniMPReleaseConfiguration.wgtPath = wgtPath;
            uniMPReleaseConfiguration.password = password;
            DCUniMPSDK.getInstance().releaseWgtToRunPath(appID, uniMPReleaseConfiguration, (code, pArgs) -> {
                Log.e("unimp", "code ---  " + code + "  pArgs --" + pArgs);
                if (code == 1) {
                    //释放wgt完成
                    try {
//                        IUniMP uniMP = DCUniMPSDK.getInstance().openUniMP(context, appID, parseConfiguration(call));
//                        mUniMPCaches.put(uniMP.getAppid(), uniMP);
                        result.success(true);
                    } catch (Exception e) {
                        e.printStackTrace();
                        result.error("-1", e.getMessage(), e.getCause());
                    }
                } else {
                    //释放wgt失败
                    result.success(false);
                }
            });
        } else if ("openUniMP".equals(call.method)) {
            try {
                String appID = call.argument("appID");
                IUniMP uniMP = DCUniMPSDK.getInstance().openUniMP(context, appID, parseConfiguration(call));
                mUniMPCaches.put(uniMP.getAppid(), uniMP);
                result.success(true);
            } catch (Exception e) {
                e.printStackTrace();
                result.error("-1", e.getMessage(), e.getCause());
            }
        } else if ("getAppVersionInfo".equals(call.method)) {
            String appID = call.argument("appID");
            JSONObject info = DCUniMPSDK.getInstance().getAppVersionInfo(appID);
            if (info != null) {
                result.success(info.toString());
            } else {
                result.success(null);
            }
        } else if ("sendUniMPEvent".equals(call.method)) {
            String appID = call.argument("appID");
            String event = call.argument("event");
            String data = call.argument("data");
            IUniMP uniMP = mUniMPCaches.get(appID);
            if (null != uniMP) {
                boolean success = uniMP.sendUniMPEvent(event, data);
                result.success(success);
            } else {
                result.success(false);
            }
        } else if ("isInitialize".equals(call.method)) {
            result.success(DCUniMPSDK.getInstance().isInitialize());
        } else {
            result.notImplemented();
        }
    }

    @Override
    public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
        channel.setMethodCallHandler(null);
    }

    private void initUniMP() {
        //初始化 uni小程序SDK ----start----------
        MenuActionSheetItem item = new MenuActionSheetItem("关于", "gy");
        List<MenuActionSheetItem> sheetItems = new ArrayList<>();
        sheetItems.add(item);
        Log.i("unimp", "onCreate----");
        DCSDKInitConfig config = new DCSDKInitConfig.Builder()
                .setCapsule(true)
                .setMenuDefFontSize("16px")
                .setMenuDefFontColor("#ff00ff")
                .setMenuDefFontWeight("normal")
                .setMenuActionSheetItems(sheetItems)
                .setEnableBackground(true)//开启后台运行
                .setUniMPFromRecents(true)
                .build();

        DCUniMPSDK.getInstance().initialize(context, config, b -> {
            Log.i("unimp", "onInitFinished----" + b);
            channel.invokeMethod("initialize", null);
        });
        //初始化 uni小程序SDK ----end----------


        // 菜单点击
        DCUniMPSDK.getInstance().setDefMenuButtonClickCallBack((appid, id) -> {
            if ("gy".equals(id)) {// 用户点击了关于
            }
            Map<String, String> arguments = new HashMap<>();
            arguments.put("appid", appid);
            arguments.put("id", id);
            channel.invokeMethod("initialize", arguments);
        });


        //用来测试sdkDemo 胶囊×的点击事件，是否生效；lxl增加的
        DCUniMPSDK.getInstance().setCapsuleCloseButtonClickCallBack(appid -> {
            if (mUniMPCaches.containsKey(appid)) {
                IUniMP mIUniMP = mUniMPCaches.get(appid);
                if (mIUniMP != null && mIUniMP.isRuning()) {
                    mIUniMP.closeUniMP();
                    mUniMPCaches.remove(appid);
                }
            }
        });

        DCUniMPSDK.getInstance().setOnUniMPEventCallBack((appid, event, data, callback) -> {
            Log.i("unimp", "onUniMPEventReceive    event=" + event);
            Map<String, Object> arguments = new HashMap<>();
            arguments.put("appid", appid);
            arguments.put("event", event);
            arguments.put("data", data);
            channel.invokeMethod("setOnUniMPEventCallBack", arguments);
            //回传数据给小程序
            callback.invoke("收到消息");
        });
    }

    private static UniMPOpenConfiguration parseConfiguration(MethodCall call) {
        UniMPOpenConfiguration uniMPOpenConfiguration = new UniMPOpenConfiguration();
        try {
            Map<String, Object> extraData = call.argument("extraData");
            if (null != extraData) {
                Set<Map.Entry<String, Object>> entries = extraData.entrySet();
                for (Map.Entry<String, Object> entry : entries) {
                    uniMPOpenConfiguration.extraData.put(entry.getKey(), entry.getValue());
                }
            }

            Map<String, Object> arguments = call.argument("arguments");
            if (null != arguments) {
                Set<Map.Entry<String, Object>> entries = arguments.entrySet();
                for (Map.Entry<String, Object> entry : entries) {
                    uniMPOpenConfiguration.arguments.put(entry.getKey(), entry.getValue());
                }
            }

            uniMPOpenConfiguration.redirectPath = call.argument("redirectPath");

            uniMPOpenConfiguration.path = call.argument("path");

            String splashClass = call.argument("splashClassName");
            if (null != splashClass) {
                try {
                    uniMPOpenConfiguration.splashClass = Class.forName(splashClass);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return uniMPOpenConfiguration;
    }
}
