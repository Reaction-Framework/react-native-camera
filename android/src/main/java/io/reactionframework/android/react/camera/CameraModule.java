package io.reactionframework.android.react.camera;

import android.hardware.Camera;
import android.net.Uri;
import android.util.Log;
import com.facebook.react.bridge.*;
import io.reactionframework.android.image.ImageUtils;

import java.util.HashMap;
import java.util.Map;

public class CameraModule extends ReactContextBaseJavaModule {
    private static final String LOG_TAG = CameraModule.class.getSimpleName();
    private static final String REACT_MODULE = "CameraModule";

    public static class CaptureTarget {
        public static final int MEMORY = 0;
        public static final int DISK = 1;
        public static final int CAMERA_ROLL = 2;

        private static Map<String, Object> getValuesMap() {
            Map<String, Object> valuesMap = new HashMap<>();
            valuesMap.put("memory", MEMORY);
            valuesMap.put("disk", DISK);
            valuesMap.put("cameraRoll", CAMERA_ROLL);
            return valuesMap;
        }
    }

    public static class Type {
        public static final int BACK = 1;
        public static final int FRONT = 2;

        private static Map<String, Object> getValuesMap() {
            Map<String, Object> valuesMap = new HashMap<>();
            valuesMap.put("back", BACK);
            valuesMap.put("front", FRONT);
            return valuesMap;
        }
    }

    public static class Aspect {
        public static final int FILL = 0;
        public static final int FIT = 1;
        public static final int STRETCH = 2;

        private static Map<String, Object> getValuesMap() {
            Map<String, Object> valuesMap = new HashMap<>();
            valuesMap.put("fill", FILL);
            valuesMap.put("fit", FIT);
            valuesMap.put("stretch", STRETCH);
            return valuesMap;
        }
    }

    private ReactApplicationContext mReactContext;
    private CameraInstanceManager mCameraInstanceManager;

    public CameraModule(ReactApplicationContext reactContext, CameraInstanceManager cameraInstanceManager) {
        super(reactContext);
        mReactContext = reactContext;
        mCameraInstanceManager = cameraInstanceManager;
    }

    @Override
    public String getName() {
        return REACT_MODULE;
    }

    @Override
    public Map<String, Object> getConstants() {
        final Map<String, Object> constants = new HashMap<>();

        constants.put("Aspect", Aspect.getValuesMap());
        constants.put("CaptureTarget", CaptureTarget.getValuesMap());
        constants.put("Type", Type.getValuesMap());

        return constants;
    }

    @ReactMethod
    public void capture(ReadableMap options, Promise promise) {
        Camera camera = mCameraInstanceManager.getCamera(options.getInt(CameraViewManager.PROP_TYPE));
        mCameraInstanceManager.updateCameraOrientation(camera);
        camera.takePicture(null, null, new PictureTakenCallback(options, promise, mReactContext));
    }

    private class PictureTakenCallback implements Camera.PictureCallback {
        private ReadableMap mOptions;
        private Promise mPromise;
        private ReactApplicationContext mReactContext;

        PictureTakenCallback(ReadableMap options, Promise promise, ReactApplicationContext reactContext) {
            this.mOptions = options;
            this.mPromise = promise;
            this.mReactContext = reactContext;
        }

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            camera.startPreview();

            int target = mOptions.getInt(CameraViewManager.PROP_TARGET);
            if (target == CaptureTarget.MEMORY) {
                String imageString = ImageUtils.dataToBase64String(data);
                mPromise.resolve(imageString);
                return;
            }

            Uri uri;

            if (target == CaptureTarget.CAMERA_ROLL) {
                uri = ImageUtils.storeInCameraRoll(mReactContext, data);
            } else {
                uri = ImageUtils.storeInAppFiles(mReactContext, data);
            }

            if (uri == null) {
                Log.e(LOG_TAG, "Photo is not saved.");
                mPromise.reject("Photo could not be saved!");
                return;
            }

            mPromise.resolve(uri.getPath());
        }
    }
}
