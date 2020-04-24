package com.wangxingxing.opengldemo.base;

import android.graphics.SurfaceTexture;

public interface ITextureRender {

    void onSurfaceCreated();

    void onSurfaceChanged(int width, int height);

    void onDrawFrame(SurfaceTexture surfaceTexture);
}
