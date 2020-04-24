package com.wangxingxing.opengldemo;

import android.graphics.SurfaceTexture;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLES30;
import android.opengl.Matrix;

import com.wangxingxing.opengldemo.base.ITextureRender;
import com.wangxingxing.opengldemo.util.ResReadUtils;
import com.wangxingxing.opengldemo.util.ShaderUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class CameraTextureRender implements ITextureRender {

    private FloatBuffer mVertexBuffer;

    /**
     * OES纹理ID
     */
    private int mOESTextureId = -1;

    /**
     * 程序
     */
    private int mShaderProgram = -1;

    private int aPositionLocation = -1;
    private int aTextureCoordLocation = -1;
    private int uTextureMatrixLocation = -1;
    private int uTextureSamplerLocation = -1;
    public int mTextureCoordOffsetLocation = -1;

    public static final String POSITION_ATTRIBUTE = "aPosition";
    public static final String TEXTURE_COORD_ATTRIBUTE = "aTextureCoord";
    public static final String TEXTURE_MATRIX_UNIFORM = "uTextureMatrix";
    public static final String TEXTURE_SAMPLER_UNIFORM = "uTextureSampler";

    public static final int POSITION_SIZE = 2;
    public static final int TEXTURE_SIZE = 2;
    public static final int STRIDE = (POSITION_SIZE + TEXTURE_SIZE) * 4;

    /**
     * 前两个为顶点坐标
     * 后两个为纹理坐标
     */
    private static final float[] VERTEX_DATA = {
            1.0f, 1.0f, 1.0f, 1.0f,
            -1.0f, 1.0f, 0.0f, 1.0f,
            -1.0f, -1f, 0.0f, 0.0f,
            1.0f, 1.0f, 1.0f, 1.0f,
            -1.0f, -1.0f, 0f, 0.0f,
            1.0f, -1.0f, 1.0f, 0.0f
    };

    /**
     * 变换矩阵
     */
    private float[] transformMatrix = new float[16];

    public CameraTextureRender(int OESTextureId) {
        mOESTextureId = OESTextureId;
        mVertexBuffer = loadVertexBuffer(VERTEX_DATA);
    }

    public FloatBuffer loadVertexBuffer(float[] vertexData) {
        FloatBuffer buffer = ByteBuffer.allocateDirect(vertexData.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        buffer.put(vertexData, 0, vertexData.length).position(0);
        return buffer;
    }

    @Override
    public void onSurfaceCreated() {
        final int vertexShader = ShaderUtils.compileVertexShader(ResReadUtils.readResource(R.raw.vertex_texture_shader));
        final int fragmentShader = ShaderUtils.compileFragmentShader(ResReadUtils.readResource(R.raw.fragment_texture_shader));
        mShaderProgram = ShaderUtils.linkProgram(vertexShader, fragmentShader);
        //开始使用程序
        GLES30.glUseProgram(mShaderProgram);

        aPositionLocation = GLES30.glGetAttribLocation(mShaderProgram, CameraTextureRender.POSITION_ATTRIBUTE);
        aTextureCoordLocation = GLES30.glGetAttribLocation(mShaderProgram, CameraTextureRender.TEXTURE_COORD_ATTRIBUTE);
        uTextureMatrixLocation = GLES30.glGetUniformLocation(mShaderProgram, CameraTextureRender.TEXTURE_MATRIX_UNIFORM);
        mTextureCoordOffsetLocation = GLES30.glGetUniformLocation(mShaderProgram, "uTextureCoordOffset");
    }

    @Override
    public void onSurfaceChanged(int width, int height) {
        GLES30.glViewport(0, 0, width, height);
    }

    private float mProgress = 0.0f;
    private int mFrames = 0;
    public static final int mMaxFrames = 8;
    public static final int mSkipFrames = 4;

    @Override
    public void onDrawFrame(SurfaceTexture surfaceTexture) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        mProgress = (float) mFrames / mMaxFrames;
        if (mProgress > 1f) {
            mProgress = 0f;
        }
        mFrames++;
        if (mFrames > mMaxFrames + mSkipFrames) {
            mFrames = 0;
        }
        float scale = 1.0f + 0.2f * mProgress; //比例
        Matrix.setIdentityM(transformMatrix, 0);
        //设置放大的百分比
        Matrix.scaleM(transformMatrix, 0, scale, scale, 1.0f);
        surfaceTexture.updateTexImage();
        surfaceTexture.getTransformMatrix(transformMatrix);

        GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
        GLES30.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, mOESTextureId);
        GLES30.glUniform1i(uTextureMatrixLocation, 0);
        GLES30.glUniformMatrix4fv(uTextureMatrixLocation, 1, false, transformMatrix, 0);

        float textureCoordOffset = 0.01f * mProgress;
        GLES30.glUniform1f(mTextureCoordOffsetLocation, textureCoordOffset);

        mVertexBuffer.position(0);
        GLES30.glEnableVertexAttribArray(aPositionLocation);
        GLES30.glVertexAttribPointer(aPositionLocation, 2, GLES30.GL_FLOAT, false, STRIDE, mVertexBuffer);

        mVertexBuffer.position(2);
        GLES30.glEnableVertexAttribArray(aTextureCoordLocation);
        GLES30.glVertexAttribPointer(aTextureCoordLocation, 2, GLES30.GL_FLOAT, false, STRIDE, mVertexBuffer);

        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, 6);
    }
}
