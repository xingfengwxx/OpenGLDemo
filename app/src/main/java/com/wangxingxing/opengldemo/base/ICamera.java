package com.wangxingxing.opengldemo.base;

import android.graphics.SurfaceTexture;

import java.io.IOException;

public interface ICamera {

    boolean openCamera(int cameraId);

    void enablePreview(boolean enable);

    void setPreviewTexture(SurfaceTexture surfaceTexture) throws IOException;

    void closeCamera();
}
