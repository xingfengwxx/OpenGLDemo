package com.wangxingxing.opengldemo;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Context;
import android.os.Bundle;
import android.view.TextureView;

import com.blankj.utilcode.util.PermissionUtils;
import com.wangxingxing.opengldemo.camera.CameraPick;

public class CameraTextureActivity extends AppCompatActivity {

    private String[] permissions = {
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private TextureView mTextureView;
    private CameraPick mCameraPick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_texture);

        checkPermission();
    }

    private void checkPermission() {
        if (!PermissionUtils.isGranted(permissions)) {
            PermissionUtils.permission(permissions).request();
        } else {

        }
    }

    private void setupView() {
        mTextureView = new TextureView(this);
        setContentView(mTextureView);

        mCameraPick = new CameraPick();
        mCameraPick.bindTextureView(mTextureView);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCameraPick != null) {
            mCameraPick.onDestroy();
            mCameraPick = null;
        }
    }
}
