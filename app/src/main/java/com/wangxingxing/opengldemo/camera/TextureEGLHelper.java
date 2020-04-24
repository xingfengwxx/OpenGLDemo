package com.wangxingxing.opengldemo.camera;

import android.graphics.SurfaceTexture;
import android.opengl.EGL14;
import android.opengl.EGLConfig;
import android.opengl.EGLContext;
import android.opengl.EGLDisplay;
import android.opengl.EGLSurface;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.view.TextureView;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;

import com.wangxingxing.opengldemo.CameraTextureRender;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class TextureEGLHelper extends HandlerThread implements SurfaceTexture.OnFrameAvailableListener {


    @IntDef({EGLMessage.MSG_INIT, EGLMessage.MSG_RENDER, EGLMessage.MSG_DESTROY})
    @Retention(RetentionPolicy.SOURCE)
    public @interface EGLMessage {
        int MSG_INIT = 100;
        int MSG_RENDER = 200;
        int MSG_DESTROY = 300;
    }

    private HandlerThread mHandlerThread;
    private Handler mHandler;
    private TextureView mTextureView;
    private int mOESTextureId;

    /**
     * 显示设备
     */
    private EGLDisplay mEGLDisplay = EGL14.EGL_NO_DISPLAY;

    /**
     * EGL上下文
     */
    private EGLContext mEGLContext = EGL14.EGL_NO_CONTEXT;

    /**
     * 描述帧缓冲区配置参数
     */
    private EGLConfig[] configs = new EGLConfig[1];

    /**
     * EGL绘图表面
     */
    private EGLSurface mEglSurface;

    /**
     * 自定义的SurfaceTexture
     * 用来接受Camera数据作二次处理
     */
    private SurfaceTexture mOESSurfaceTexture;

    private CameraTextureRender mTextureRender;

    public TextureEGLHelper() {
        super("TextureEGLHelper");
    }

    private final class TextureHandler extends Handler {
        public TextureHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case EGLMessage.MSG_INIT:
                    //笔者使用的是OpenGL ES 3.0
                    initEGLContext(3);
                    break;
                case EGLMessage.MSG_RENDER:
                    //开始渲染
                    drawFrame();
                    return;
                case EGLMessage.MSG_DESTROY:
                    //销毁
                    return;
            }
        }
    }

    /**
     * 初始化EGL环境
     *
     * @param clientVersion
     */
    private void initEGLContext(int clientVersion) {
        //获取默认显示设备
        mEGLDisplay = EGL14.eglGetDisplay(EGL14.EGL_DEFAULT_DISPLAY);
        if (mEGLDisplay == EGL14.EGL_NO_DISPLAY) {
            throw  new RuntimeException("eglGetDisplay error: " + EGL14.eglGetError());
        }
        //存放EGL版本号
        int[] version = new int[2];
        version[0] = 3;
        if (!EGL14.eglInitialize(mEGLDisplay, version, 0, version, 1)) {
            throw new RuntimeException("eglInitialize error: " + EGL14.eglGetError());
        }
        //配置列表
        int[] attributes = {
                EGL14.EGL_BUFFER_SIZE, 32,
                EGL14.EGL_RED_SIZE, 8,
                EGL14.EGL_GREEN_SIZE, 8,
                EGL14.EGL_BLUE_SIZE, 8,
                EGL14.EGL_RENDERABLE_TYPE, 4,
                EGL14.EGL_SURFACE_TYPE, EGL14.EGL_WINDOW_BIT,
                EGL14.EGL_NONE
        };
        int[] numConfigs = new int[1];
        //EGL选择配置
        if (!EGL14.eglChooseConfig(mEGLDisplay, attributes, 0, configs, 0, configs.length, numConfigs, 0)) {
            throw new RuntimeException("eglChooseConfig error: " + EGL14.eglGetError());
        }
        //获取TextureView内置的SurfaceTexture作为EGL的绘图表面，也就是跟系统屏幕打交道
        SurfaceTexture surfaceTexture = mTextureView.getSurfaceTexture();
        if (surfaceTexture == null) {
            throw new RuntimeException("surfaceTexture is null");
        }
        //创建EGL显示窗口
        final int[] surfaceAttributes = {EGL14.EGL_NONE};
        mEglSurface = EGL14.eglCreateWindowSurface(mEGLDisplay, configs[0], surfaceTexture, surfaceAttributes, 0);
        //创建上下文环境
        int[] contexAttributes = {
                EGL14.EGL_CONTEXT_CLIENT_VERSION, clientVersion,
                EGL14.EGL_NONE
        };
        mEGLContext = EGL14.eglCreateContext(mEGLDisplay, configs[0], EGL14.EGL_NO_CONTEXT, contexAttributes, 0);


        if (mEGLDisplay == EGL14.EGL_NO_DISPLAY || mEGLContext == EGL14.EGL_NO_CONTEXT) {
            throw new RuntimeException("eglCreateContext fail error: " + EGL14.eglGetError());
        }
        if (!EGL14.eglMakeCurrent(mEGLDisplay, mEglSurface, mEglSurface, mEGLContext)) {
            throw new RuntimeException("eglMakeCurrent error: " + EGL14.eglGetError());
        }
        //加载渲染器
        mTextureRender = new CameraTextureRender(mOESTextureId);
        //mTextureRenderer = new MeiYanRenderer(mOESTextureId);
        mTextureRender.onSurfaceCreated();
    }

    public void initEgl(TextureView textureView, int textureId) {
        mTextureView = textureView;
        mOESTextureId = textureId;
        //启动线程
        mHandlerThread = new HandlerThread("Renderer Thread");
        mHandlerThread.start();
        mHandler = new TextureHandler(mHandlerThread.getLooper());
        //线程中初始化
        mHandler.sendEmptyMessage(EGLMessage.MSG_INIT);
    }

    public void onSurfaceChanged(int width, int height) {
        //设置视口
        mTextureRender.onSurfaceChanged(width, height);
    }

    private void drawFrame() {
        if (mTextureRender != null) {
            //指定mEGLContext为当前系统的EGL上下文
            EGL14.eglMakeCurrent(mEGLDisplay, mEglSurface, mEglSurface, mEGLContext);
            //调用渲染器绘制
            mTextureRender.onDrawFrame(mOESSurfaceTexture);
            //交换缓冲区,android使用双缓冲机制,所以我们绘制的都是在后台缓冲区,通过交换将后台缓冲区变为前台显示区,下一帧的绘制仍然在后台缓冲区
            EGL14.eglSwapBuffers(mEGLDisplay, mEglSurface);
        }
    }

    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        if (mHandler != null) {
            //通知子线程渲染
            mHandler.sendEmptyMessage(EGLMessage.MSG_RENDER);
        }
    }

    public SurfaceTexture loadOESTexture() {
        //加载自定义的SurfaceTexture传递给相机
        mOESSurfaceTexture = new SurfaceTexture(mOESTextureId);
        mOESSurfaceTexture.setOnFrameAvailableListener(this);
        return mOESSurfaceTexture;
    }

    /**
     * 销毁
     * 释放
     */
    public void onDestroy() {
        if (mHandlerThread != null) {
            mHandlerThread.quitSafely();
        }
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
    }
}
