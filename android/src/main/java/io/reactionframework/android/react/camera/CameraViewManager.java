package io.reactionframework.android.react.camera;

import android.util.Log;
import com.facebook.react.uimanager.ReactProp;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.ViewGroupManager;

public class CameraViewManager extends ViewGroupManager<CameraView> {
    private static final String LOG_TAG = CameraViewManager.class.getSimpleName();

    public static final String REACT_CLASS = "RCTIONCameraView";
    public static final String PROP_ASPECT = "aspect";
    public static final String PROP_TYPE = "type";
    public static final String PROP_TARGET = "target";

    private CameraInstanceManager mCameraInstanceManager;

    public CameraViewManager(CameraInstanceManager cameraInstanceManager) {
        this.mCameraInstanceManager = cameraInstanceManager;
    }

    @Override
    public String getName() {
        return REACT_CLASS;
    }

    @ReactProp(name = PROP_ASPECT, defaultInt = CameraModule.Aspect.FILL)
    public void setAspect(CameraView cameraView, int aspect) {
        Log.v(LOG_TAG, String.format("Property '%s' changed.", PROP_ASPECT));
        cameraView.updateAspect(aspect);
    }

    @ReactProp(name = PROP_TYPE, defaultInt = CameraModule.Type.BACK)
    public void setType(CameraView cameraView, int type) {
        Log.v(LOG_TAG, String.format("Property '%s' changed.", PROP_TYPE));
        cameraView.updateCamera(type);
    }

    @Override
    public CameraView createViewInstance(ThemedReactContext context) {
        return new CameraView(context, mCameraInstanceManager);
    }
}
