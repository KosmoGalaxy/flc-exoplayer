package pl.fulllegitcode.exoplayer_test;

import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLUtils;

class Texture {

  private final int _id;
  public int id() { return _id; }

  Texture(Bitmap bitmap) {
    int[] h = new int[1];
    GLES20.glGenTextures(1, h, 0);
    _id = h[0];
    GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, _id);
    GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
    GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
    GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
    GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
    GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
  }

}
