package io.reactionframework.android.camera;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.net.Uri;
import android.util.Log;
import com.facebook.react.bridge.*;

import java.util.HashMap;
import java.util.Map;

public class CameraModule extends ReactContextBaseJavaModule {
    private static final String LOG_TAG = CameraModule.class.getSimpleName();
    // TODO WHY SAME NAME AS CLASS?
    private static final String REACT_MODULE = "CameraModule";

    public static class CaptureTarget {
        public static final int MEMORY = 0;
        public static final int DISC = 1;
        public static final int CAMERA_ROLL = 2;

        private static Map<String, Object> getValuesMap() {
            Map<String, Object> valuesMap = new HashMap<>();
            valuesMap.put("memory", MEMORY);
            valuesMap.put("disc", DISC);
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

    // TODO WHY SAME NAME AS CLASS?
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
    public void capture(ReadableMap options, final Callback callback) {
        Camera camera = mCameraInstanceManager.getCamera(options.getInt(CameraViewManager.PROP_TYPE));
        camera.takePicture(null, null, new PictureTakenCallback(options, callback, mReactContext));
    }

    private class PictureTakenCallback implements Camera.PictureCallback {
        ReadableMap options;
        Callback callback;
        ReactApplicationContext reactContext;

        PictureTakenCallback(ReadableMap options, Callback callback, ReactApplicationContext reactContext) {
            this.options = options;
            this.callback = callback;
            this.reactContext = reactContext;
        }

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            camera.startPreview();

            int photoRotation = getPhotoOrientation(camera);
            BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
            bitmapOptions.inSampleSize = options.getInt("sampleSize");
            Bitmap bitmap = ImageUtils.rotateBitmap(BitmapFactory.decodeByteArray(data, 0, data.length, bitmapOptions), photoRotation);

            int target = options.getInt(CameraViewManager.PROP_TARGET);
            if (target == CaptureTarget.MEMORY) {
                String imageString = ImageUtils.bitmapToString(bitmap);
                callback.invoke(null, imageString);
                return;
            }

            // Store photo to disc and into media store
            Uri uri = ImageUtils.storePhoto(mReactContext, bitmap);

            if (uri == null) {
                Log.e(LOG_TAG, "Photo is not saved!");
                return;
            }

            callback.invoke(null, uri.toString());
        }

        private int getPhotoOrientation(Camera camera) {
            int cameraOrientation = mCameraInstanceManager.getCameraOrientation();
            int cameraId = mCameraInstanceManager.getCameraId(camera);
            CameraInfo info = new CameraInfo();
            Camera.getCameraInfo(cameraId, info);

            int photoOrientation = (info.orientation + cameraOrientation) % 360;
            if (info.facing == CameraInfo.CAMERA_FACING_FRONT) {
                photoOrientation = (info.orientation - cameraOrientation + 360) % 360;
            }

            return photoOrientation;
        }
    }
}
