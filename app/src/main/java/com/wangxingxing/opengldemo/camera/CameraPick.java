package com.wangxingxing.opengldemo.camera;

import android.app.Activity;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.util.Log;
import android.view.TextureView;

import com.wangxingxing.opengldemo.base.ICamera;
import com.wangxingxing.opengldemo.util.TextureUtils;

import java.io.IOException;

public class CameraPick implements TextureView.SurfaceTextureListener {

    private static final String TAG = "CameraPick";

    private TextureView mTextureView;
    private int mCameraId;
    private ICamera mCamera;
    private TextureEGLHelper mTextureEglHelper;

    public void bindTextureView(TextureView textureView) {
        this.mTextureView = textureView;
        mTextureEglHelper = new TextureEGLHelper();
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        //加载OES纹理ID
        final int textureId = TextureUtils.loadOESTexture();
        //初始化操作
        mTextureEglHelper.initEgl(mTextureView, textureId);
        //自定义的SurfaceTexture
        SurfaceTexture surfaceTexture = mTextureEglHelper.loadOESTexture();
        //前置摄像头
        mCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
        mCamera = new MyCamera((Activity) mTextureView.getContext());
        if (mCamera.openCamera(mCameraId)) {
            try {
                mCamera.setPreviewTexture(surfaceTexture);
            } catch (IOException e) {
                e.printStackTrace();
            }
            mCamera.enablePreview(true);
        } else {
            Log.e(TAG, "openCamera failed");
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        mTextureEglHelper.onSurfaceChanged(width, height);
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        if (mCamera != null) {
            mCamera.enablePreview(false);
            mCamera.closeCamera();
            mCamera = null;
        }
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }

    public void onDestroy() {
        if (mTextureEglHelper != null) {
            mTextureEglHelper.onDestroy();
        }
    }
}
