package io.reactionframework.android.camera;

import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.views.view.ReactViewGroup;

@SuppressLint("ViewConstructor")
public class CameraView extends RelativeLayout {
    private static final String LOG_TAG = CameraView.class.getSimpleName();

    private CameraInstanceManager mCameraInstanceManager;
    private CameraSurface mCameraSurface;
    private ReactViewGroup mViewGroupHolder;

    public CameraView(ThemedReactContext context, CameraInstanceManager cameraInstanceManager) {
        super(context);

        mCameraInstanceManager = cameraInstanceManager;

        mCameraSurface = new CameraSurface(context);
        addView(mCameraSurface, new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT));

        mViewGroupHolder = new ReactViewGroup(context);
        addView(mViewGroupHolder, new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT));
    }

    private boolean isStaticChild(View child) {
        return child == mViewGroupHolder ||
                child == mCameraSurface;
    }

    @Override
    public void addView(@NonNull View child) {
        if (isStaticChild(child)) {
            super.addView(child);
            return;
        }

        mViewGroupHolder.addView(child);
    }

    @Override
    public void addView(@NonNull View child, int index) {
        if (isStaticChild(child)) {
            super.addView(child, index);
            return;
        }

        mViewGroupHolder.addView(child, index);
    }

    @Override
    public void addView(@NonNull View child, int width, int height) {
        if (isStaticChild(child)) {
            super.addView(child, width, height);
            return;
        }

        mViewGroupHolder.addView(child, width, height);
    }

    @Override
    public void addView(@NonNull View child, ViewGroup.LayoutParams params) {
        if (isStaticChild(child)) {
            super.addView(child, params);
            return;
        }

        mViewGroupHolder.addView(child, params);
    }

    @Override
    public void addView(@NonNull View child, int index, ViewGroup.LayoutParams params) {
        if (isStaticChild(child)) {
            super.addView(child, index, params);
            return;
        }

        mViewGroupHolder.addView(child, index, params);
    }

    @Override
    public void removeView(@NonNull View child) {
        if (isStaticChild(child)) {
            super.removeView(child);
            return;
        }

        mViewGroupHolder.removeView(child);
    }

    public void updateAspect(int aspect) {
        mCameraSurface.updateAspect(aspect);
    }

    public void updateCamera(int cameraType) {
        Camera camera = mCameraInstanceManager.getCamera(cameraType);

        if (camera != mCameraSurface.mCamera) {
            try {
                mCameraSurface.mCameraType = cameraType;
                mCameraSurface.mCamera = camera;
                mCameraSurface.surfaceCreated(mCameraSurface.getHolder());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class CameraSurface extends SurfaceView implements SurfaceHolder.Callback {
        private int mAspect;
        private int mCameraType;
        private Camera mCamera;
        private Camera.Size mCameraSize;

        public CameraSurface(ThemedReactContext context) {
            super(context);

            mAspect = -1;
            mCameraType = -1;
            mCamera = null;
            mCameraSize = null;

            getHolder().addCallback(this);
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            Log.v(LOG_TAG, "Surface is created.");

            if (mCamera != null) {
                try {
                    mCameraSize = mCameraInstanceManager.updateCameraPictureSize(mCamera);
                    mCamera.setPreviewDisplay(getHolder());
                    mCameraInstanceManager.updateCameraOrientation(mCamera);
                    mCameraInstanceManager.getCameraOrientationListener().enable();
                    mCamera.startPreview();
                    onCameraSurfaceUpdate();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return;
            }

            if (mCameraType != -1) {
                mCamera = mCameraInstanceManager.getCamera(mCameraType);

                if (mCamera == null) {
                    String error = String.format("Something gone terribly wrong. Can't find camera for type: %d", mCameraType);
                    Log.e(LOG_TAG, error);
                    throw new RuntimeException(error);
                }

                surfaceCreated(getHolder());
            }
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
            Log.v(LOG_TAG, "Surface is changed.");
            onCameraSurfaceUpdate();
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            Log.v(LOG_TAG, "Surface is destroyed.");

            if (mCamera != null) {
                mCamera.stopPreview();
                mCameraInstanceManager.releaseCurrentCamera();
                mCameraInstanceManager.getCameraOrientationListener().disable();
                mCamera = null;
            }
        }

        public void updateAspect(int aspect) {
            if (aspect != mAspect) {
                mAspect = aspect;
                onCameraSurfaceUpdate();
            }
        }

        private void onCameraSurfaceUpdate() {
            if (mCamera != null) {
                mCameraInstanceManager.updateCameraOrientation(mCamera);
                updatePreviewLayout();
            }
        }

        private void updatePreviewLayout() {
            int width = getMeasuredWidth();
            int height = getMeasuredHeight();

            if (width <= 0 || height <= 0) {
                Log.v(LOG_TAG, "Skipping preview layout update.");
                return;
            }

            if (mAspect != CameraModule.Aspect.FILL && mAspect != CameraModule.Aspect.FIT) {
                mCameraSurface.layout(0, 0, width, height);
                return;
            }

            int screenOrientation = getContext().getResources().getConfiguration().orientation;

            float cameraWidth = screenOrientation == Configuration.ORIENTATION_LANDSCAPE ? mCameraSize.width : mCameraSize.height;
            float cameraHeight = screenOrientation == Configuration.ORIENTATION_LANDSCAPE ? mCameraSize.height : mCameraSize.width;

            float scale = 0;

            switch (mAspect) {
                case CameraModule.Aspect.FILL:
                    scale = Math.max((float) width / cameraWidth, (float) height / cameraHeight);
                    break;
                case CameraModule.Aspect.FIT:
                    scale = Math.min((float) width / cameraWidth, (float) height / cameraHeight);
                    break;
            }

            cameraWidth = cameraWidth * scale;
            cameraHeight = cameraHeight * scale;

            float cameraX = (float) width / 2f - cameraWidth / 2f;
            float cameraY = (float) height / 2f - cameraHeight / 2f;

            mCameraSurface.layout((int) cameraX, (int) cameraY, (int) (cameraX + cameraWidth), (int) (cameraY + cameraHeight));
        }
    }
}
