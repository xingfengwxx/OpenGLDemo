#version 300 es
//外部纹理
#extension GL_OES_EGL_image_external_essl3 : require
precision mediump float;
uniform samplerExternalOES uTextureSampler;
in vec2 vTextureCoord;
out vec4 vFragColor;

const float horizontal = 2.0; //(1) 封装 横竖屏
const float vertical = 2.0;

void main() {
    float horizontalCount = max(horizontal, 1.0); // (2)参数控制，分屏必须是1个以上的屏幕
    float verticalCount = max(vertical, 1.0);

    float ratio = verticalCount / horizontalCount; // (3)

    vec2 originSize = vec2(1.0, 1.0);
    vec2 newSize = originSize;

    if (ratio > 1.0) {
        newSize.y = 1.0 / ratio;
    } else {
        newSize.x = ratio;
    }
    vec2 offset = (originSize - newSize) / 2.0; // (4)计算新的图像在原始图像中的偏移量。因为我们的图像要居中裁剪，所以要计算出裁剪后的偏移。比如 (2.0 / 3.0, 1.0) 的图像，对应的 offset 是 (1.0 / 6.0, 0.0)
    vec2 position = offset + mod(vTextureCoord * min(horizontalCount, verticalCount), newSize); // (5)将原始的纹理坐标，乘上 horizontalCount 和 verticalCount 的较小者，然后对新的尺寸进行求模运算。
    vFragColor = texture(uTextureSampler, position); // (6)通过新的计算出来的纹理坐标，从纹理中读出相应的颜色值输出。

    //   vec4 vCameraColor = texture(uTextureSampler, vTextureCoord);
    //黑白滤镜
    //        float fGrayColor = (0.299*vCameraColor.r + 0.587*vCameraColor.g + 0.114*vCameraColor.b);
    //        vFragColor = vec4(fGrayColor, fGrayColor, fGrayColor, 1.0);
    //   vFragColor = vCameraColor;
}

//
////分色偏移
//#version 300 es
////外部纹理
//#extension GL_OES_EGL_image_external_essl3 : require
//precision mediump float;
//uniform samplerExternalOES uTextureSampler;
//in vec2 vTextureCoord;
//out vec4 vFragColor;
////颜色的偏移距离
//uniform float uTextureCoordOffset;
////const float uTextureCoordOffset_f = 2.0;
//
//void main()
//{
//    vec4 blue = texture(uTextureSampler,vTextureCoord);
//    vec4 green = texture(uTextureSampler,vec2(vTextureCoord.x + uTextureCoordOffset,vTextureCoord.y + uTextureCoordOffset));
//    vec4 red = texture(uTextureSampler,vec2(vTextureCoord.x - uTextureCoordOffset,vTextureCoord.y - uTextureCoordOffset));
//    vFragColor = vec4(red.x,green.y,blue.z,blue.w);
//}