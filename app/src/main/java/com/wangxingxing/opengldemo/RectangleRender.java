package com.wangxingxing.opengldemo;

import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

import com.wangxingxing.opengldemo.base.App;
import com.wangxingxing.opengldemo.util.OpenUtils;
import com.wangxingxing.opengldemo.util.ShaderUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class RectangleRender implements GLSurfaceView.Renderer {

    private static final String TAG = "RectangleRender";

    FloatBuffer vertexBuffer;
    /**
     * 位置顶点属性的大小
     * 包含(x,y)
     */
    public static final int VERTEX_POSITION_SIZE = 2;

    /**
     * 颜色顶点属性的大小
     * 包含(r,g,b)
     */
    public static final int VERTEX_COLOR_SIZE = 3;

    /**
     * 浮点型数据占用字节数
     */
    public static final int BYTES_PER_FLOAT = 4;

    public int uMatrixLocation;
    private static final int STRIDE = (VERTEX_POSITION_SIZE + VERTEX_COLOR_SIZE) * BYTES_PER_FLOAT;
    private final float[] mMatrix = new float[16];

    private float[] vertexPoints = new float[]{
            //前两个是坐标,后三个是颜色RGB
            0.0f, 0.0f, 1.0f, 1.0f, 1.0f,
            -0.5f, -0.5f, 1.0f, 1.0f, 1.0f,
            0.5f, -0.5f, 1.0f, 1.0f, 1.0f,
            0.5f, 0.5f, 1.0f, 1.0f, 1.0f,
            -0.5f, 0.5f, 1.0f, 1.0f, 1.0f,
            -0.5f, -0.5f, 1.0f, 1.0f, 1.0f,

            0.0f, 0.25f, 0.5f, 0.5f, 0.5f,
            0.0f, -0.25f, 0.5f, 0.5f, 0.5f,
    };

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        //设置背景颜色
        GLES30.glClearColor(0.5f, 0.5f, 0.5f, 0.5f);
        //编译
        final int vertexShaderId = ShaderUtils.compileVertexShader(OpenUtils.readRawTextFile(App.instance, R.raw.test_vert));
        final int fragmentShaderId = ShaderUtils.compileFragmentShader(OpenUtils.readRawTextFile(App.instance, R.raw.test));
        //鏈接程序片段
        int mProgram = ShaderUtils.linkProgram(vertexShaderId, fragmentShaderId);
        //在OpenGLES环境中使用程序片段，在这里opengl就能够获取数据（jvm--->c）
        GLES30.glUseProgram(mProgram);

        //获取vertex 和fragment配置的数据(这里写的代码取决于你的frag和vert的代码)
        //uMatrixLocation = GLES30.glGetUniformLocation(mProgram,"u_Matrix");
        int aPositionLocation = GLES30.glGetAttribLocation(mProgram, "vPosition");
        int aColorLocation = GLES30.glGetAttribLocation(mProgram, "aColor");

        vertexBuffer.position(0);
        //获取顶点数组 (VERTEX_POSITION_SIZE = 2)
        GLES30.glVertexAttribPointer(aPositionLocation, VERTEX_POSITION_SIZE, GLES30.GL_FLOAT, false, STRIDE, vertexBuffer);
        GLES30.glEnableVertexAttribArray(aPositionLocation);

        vertexBuffer.position(VERTEX_COLOR_SIZE);
        //颜色属性分量的数量 VERTEX_COLOR_SIZE = 3
        GLES30.glVertexAttribPointer(aColorLocation, VERTEX_COLOR_SIZE, GLES30.GL_FLOAT, false, STRIDE, vertexBuffer);
        GLES30.glEnableVertexAttribArray(aColorLocation);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES30.glViewport(0, 0, width, height);
        Log.i(TAG, width + " x " + height);
        final float aspectRation = width > height ?
                (float) width / (float) height :
                (float) height / (float) width;
        if (width > height) {
            //横屏
            Matrix.orthoM(mMatrix, 0, -aspectRation, aspectRation, -1f, 1f, -1f, 1f);
        } else {
            //竖屏
            Matrix.orthoM(mMatrix, 0, -1f, 1f, -aspectRation, aspectRation, -1f, 1f);
        }
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT);
        GLES30.glUniformMatrix4fv(uMatrixLocation, 1, false, mMatrix, 0);
        //绘制矩形
        GLES30.glDrawArrays(GLES30.GL_TRIANGLE_STRIP, 0, 6);

        //绘制两个点
        GLES30.glDrawArrays(GLES30.GL_POINTS, 6, 2);
    }

    //本地内存的开辟
    public RectangleRender() {
        //分配内存空间,每个浮点型占4字节空间
        vertexBuffer = ByteBuffer.allocateDirect(vertexPoints.length * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder())//字节序
                .asFloatBuffer();//避免直接操作字节
        //传入指定的坐标数据
        vertexBuffer.put(vertexPoints);
        vertexBuffer.position(0);
    }
}
