package io.reactionframework.android.camera;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.SensorManager;
import android.util.Log;
import android.view.OrientationEventListener;
import android.view.Surface;

public class CameraInstanceManager {
    private static final String LOG_TAG = CameraInstanceManager.class.getSimpleName();

    private Camera[] mCameraList = new Camera[2];
    private Activity mActivity;

    private int mCurrentCameraId;
    private CameraOrientationListener mOrientationListener;

    public CameraInstanceManager(Activity activity) {
        mActivity = activity;
        mCurrentCameraId = -1;
        mOrientationListener = new CameraOrientationListener(activity);
    }

    public OrientationEventListener getCameraOrientationListener() {
        return mOrientationListener;
    }

    public Camera getCamera(int cameraType) {
        int cameraId = getCameraId(cameraType);

        if (mCameraList[cameraId] == null) {
            releaseCurrentCamera();
            mCameraList[cameraId] = Camera.open(cameraId);
        }

        return mCameraList[(mCurrentCameraId = cameraId)];
    }

    public int getCameraId(Camera camera) {
        if (mCameraList[0] == camera) {
            return 0;
        }

        if (mCameraList[1] == camera) {
            return 1;
        }

        Log.e(LOG_TAG, "Could not find id of camera, returning current camera id.");

        return mCurrentCameraId;
    }

    public void updateCameraOrientation(Camera camera) {
        int displayOrientation = getDisplayOrientation();
        camera.setDisplayOrientation(displayOrientation);

        Camera.Parameters cameraParameters = camera.getParameters();
        cameraParameters.setRotation(displayOrientation);
        camera.setParameters(cameraParameters);
    }

    public int getCameraOrientation() {
        mOrientationListener.saveOrientation();
        return mOrientationListener.mCameraOrientation;
    }

    private int getDisplayOrientation() {
        CameraInfo cameraInfo = new CameraInfo();
        Camera.getCameraInfo(mCurrentCameraId, cameraInfo);

        int rotation = mActivity.getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        if (cameraInfo.facing == CameraInfo.CAMERA_FACING_FRONT) {
            degrees = (cameraInfo.orientation + degrees) % 360;
            return (360 - degrees) % 360;
        }

        return (cameraInfo.orientation - degrees + 360) % 360;
    }

    private int getCameraId(int cameraType) {
        if (cameraType == CameraModule.Type.FRONT) {
            return getFrontCameraID();
        }

        return getBackCameraID();
    }

    private int getBackCameraID() {
        return CameraInfo.CAMERA_FACING_BACK;
    }

    private int getFrontCameraID() {
        return mActivity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT) ?
                CameraInfo.CAMERA_FACING_FRONT :
                getBackCameraID();
    }

    public void releaseCurrentCamera() {
        if (mCurrentCameraId < 0) {
            return;
        }

        Camera camera = mCameraList[mCurrentCameraId];
        camera.release();
        mCameraList[mCurrentCameraId] = null;
        mCurrentCameraId = -1;
    }

    private class CameraOrientationListener extends OrientationEventListener {
        private int mCurrentOrientation;
        private int mCameraOrientation;

        public CameraOrientationListener(Context context) {
            super(context, SensorManager.SENSOR_DELAY_NORMAL);
        }

        @Override
        public void onOrientationChanged(int orientation) {
            if (orientation != ORIENTATION_UNKNOWN) {
                mCurrentOrientation = normalize(orientation);
            }
        }

        private int normalize(int rotation) {
            if (rotation > 315 || rotation <= 45) {
                return 0;
            }

            if (rotation > 45 && rotation <= 135) {
                return 90;
            }

            if (rotation > 135 && rotation <= 225) {
                return 180;
            }

            if (rotation > 225 && rotation <= 315) {
                return 270;
            }

            throw new RuntimeException("The physics as we know them are no more. Watch out for anomalies.");
        }

        private void saveOrientation() {
            mCameraOrientation = mCurrentOrientation;
        }
    }
}
