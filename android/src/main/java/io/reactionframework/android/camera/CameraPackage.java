package io.reactionframework.android.camera;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import android.app.Activity;

import com.facebook.react.ReactPackage;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.JavaScriptModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.uimanager.ViewManager;

public class CameraPackage implements ReactPackage {
    private CameraInstanceManager mCameraInstanceManager;

    public CameraPackage(Activity mainActivity) {
        mCameraInstanceManager = new CameraInstanceManager(mainActivity);
    }

    @Override
    public List<NativeModule> createNativeModules(ReactApplicationContext reactContext) {
        List<NativeModule> modules = new ArrayList<NativeModule>();
        modules.add(new CameraModule(reactContext, mCameraInstanceManager));
        return modules;
    }

    @Override
    public List<Class<? extends JavaScriptModule>> createJSModules() {
        return Collections.emptyList();
    }

    @Override
    public List<ViewManager> createViewManagers(ReactApplicationContext reactContext) {
        List<ViewManager> viewManagers = new ArrayList<ViewManager>();
        viewManagers.add(new CameraViewManager(mCameraInstanceManager));
        return viewManagers;
    }
}
