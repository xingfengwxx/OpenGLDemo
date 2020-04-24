package com.wangxingxing.opengldemo;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

public class PumpKin {
    private FloatBuffer vertexBuffer;
    private FloatBuffer colorsBuffer;
    private ByteBuffer indicesBuffer;

    private float vertex[]={
            0.0f,0.0f,0.0f,
            1.0f,0.0f,0.0f,
            0.0f,0.0f,0.0f,
            0.0f,1.0f,0.0f,
            0.0f,0.0f,0.0f,
            0.0f,0.0f,1.0f
    };

    private float colors[]={
            1.0f,0.0f,0.0f,1.0f,
            1.0f,0.0f,0.0f,1.0f,
            0.0f,1.0f,0.0f,1.0f,
            0.0f,1.0f,0.0f,1.0f,
            0.0f,0.0f,1.0f,1.0f,
            0.0f,0.0f,1.0f,1.0f
    };
    private float wenli[]={
            1.0f,0.0f,0.0f,1.0f,
            1.0f,0.0f,0.0f,1.0f,
            0.0f,1.0f,0.0f,1.0f,
            0.0f,1.0f,0.0f,1.0f,
            0.0f,0.0f,1.0f,1.0f,
            0.0f,0.0f,1.0f,1.0f
    };
    private byte indices[] = {0, 1, 2};

    public PumpKin() {
        ByteBuffer vbb = ByteBuffer.allocateDirect(vertex.length * 4);
        vbb.order(ByteOrder.nativeOrder());
        vertexBuffer = vbb.asFloatBuffer();
        vertexBuffer.put(vertex);
        vertexBuffer.position(0);

        ByteBuffer cbb = ByteBuffer.allocateDirect(colors.length * 4);
        cbb.order(ByteOrder.nativeOrder());
        colorsBuffer = cbb.asFloatBuffer();
        colorsBuffer.put(colors);
        colorsBuffer.position(0);
    }

    public void draw(GL10 gl) {
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glEnableClientState(GL10.GL_COLOR_ARRAY);

        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);
        gl.glColorPointer(4, GL10.GL_FLOAT, 0, vertexBuffer);
        gl.glColorPointer(4, GL10.GL_FLOAT, 0, colorsBuffer);

        gl.glDrawArrays(GL10.GL_LINES, 0, vertex.length / 3);

        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
    }
}
