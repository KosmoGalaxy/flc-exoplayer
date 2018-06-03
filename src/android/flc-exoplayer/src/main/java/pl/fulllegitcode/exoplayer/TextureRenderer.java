package pl.fulllegitcode.exoplayer;

import android.graphics.SurfaceTexture;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.Log;

class TextureRenderer {

  private final int _outTextureId;
  private int[] _videoTextureIdHolder = new int[1];

  private SurfaceTexture _videoTexture;
  public SurfaceTexture videoTexture() { return _videoTexture; }

  TextureRenderer(int outTextureId) {
    _outTextureId = outTextureId;
    _setupVideoTexture();
  }

  public void update() {
    if (_outTextureId != 0) {
      GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, _outTextureId);
      GLES20.glClearColor(
        (float) Math.random(),
        (float) Math.random(),
        (float) Math.random(),
        1
      );
      GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
    }
  }

  private void _setupVideoTexture() {
    GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
    GLES20.glGenTextures(1, _videoTextureIdHolder, 0);
    _checkGlError("Texture generate");
    GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, _videoTextureIdHolder[0]);
    _checkGlError("Texture bind");
    _videoTexture = new SurfaceTexture(_videoTextureIdHolder[0]);
  }

  private void _checkGlError(String op)
  {
    int error;
    while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
      Log.e("SurfaceTest", op + ": glError " + GLUtils.getEGLErrorString(error));
    }
  }

}
