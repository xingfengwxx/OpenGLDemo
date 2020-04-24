package com.wangxingxing.opengldemo.camera;

import android.app.Activity;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.view.Surface;

import com.wangxingxing.opengldemo.base.ICamera;

import java.io.IOException;

public class MyCamera implements ICamera {

    private Activity mActivity;

    private int mCameraId;

    private Camera mCamera;

    public MyCamera(Activity mActivity) {
        this.mActivity = mActivity;
    }

    /**
     * 打开相机
     *
     * @param cameraId
     * @return
     */
    @Override
    public boolean openCamera(int cameraId) {
        try {
            mCameraId = cameraId;
            mCamera = Camera.open(mCameraId);
            setCameraDisplayOrientation(mActivity, mCameraId, mCamera);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 释放开启预览
     *
     * @param enable
     */
    @Override
    public void enablePreview(boolean enable) {
        if (mCamera != null) {
            if (enable) {
                mCamera.startPreview();
            } else {
                mCamera.stopPreview();
            }
        }
    }

    @Override
    public void setPreviewTexture(SurfaceTexture surfaceTexture) throws IOException {
        if (mCamera != null) {
            mCamera.setPreviewTexture(surfaceTexture);
        }
    }

    @Override
    public void closeCamera() {
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
        mActivity = null;
    }

    /**
     * 设置相机的旋转角度
     * 前置相机旋转270度
     * 后置相机旋转90度
     *
     * @param activity
     * @param cameraId
     * @param camera
     */
    private void setCameraDisplayOrientation(Activity activity, int cameraId, Camera camera) {
        Camera.CameraInfo info =
                new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, info);
        int rotation = activity.getWindowManager().getDefaultDisplay()
                .getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
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
        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(result);
    }
}
