package com.wangxingxing.opengldemo.widget;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

import com.wangxingxing.opengldemo.MapBufferRenderer;
import com.wangxingxing.opengldemo.RectangleRender;
import com.wangxingxing.opengldemo.VertexBufferRenderer;

public class DouyinView extends GLSurfaceView {
    public DouyinView(Context context) {
        super(context, null);
    }

    public DouyinView(Context context, AttributeSet attrs) {
        super(context, attrs);

        /**
         * 配置GLSurfaceView
         */
        //设置EGL版本
        setEGLContextClientVersion(3);
        setRenderer(new DouyinRenderer(this));
//        setRenderer(new RectangleRender());
//        setRenderer(new VertexBufferRenderer());
//        setRenderer(new MapBufferRenderer());

        //设置按需渲染 当我们调用 requestRender 请求GLThread 回调一次 onDrawFrame
        // 连续渲染 就是自动的回调onDrawFrame
        setRenderMode(RENDERMODE_WHEN_DIRTY);
    }
}
