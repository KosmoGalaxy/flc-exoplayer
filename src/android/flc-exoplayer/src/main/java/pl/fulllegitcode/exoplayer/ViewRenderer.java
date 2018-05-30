package pl.fulllegitcode.exoplayer;

import android.graphics.SurfaceTexture;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.view.Surface;

import java.util.Locale;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class ViewRenderer implements GLSurfaceView.Renderer {

  private int _glTexture = 0;
  public int glTexture() { return _glTexture; }

  private SurfaceTexture _surfaceTexture = null;
  private Surface _surface = null;

  @Override
  public void onSurfaceCreated(GL10 gl, EGLConfig config) { }

  @Override
  public void onSurfaceChanged(GL10 gl, int width, int height) {
    _destroySurface();
    _createSurface(gl, width, height);
    Log.info(String.format(
      Locale.ENGLISH,
      "surface changed. (texture id)=%d (width)=%d (height)=%d",
      _glTexture,
      width,
      height
    ));
  }

  @Override
  public void onDrawFrame(GL10 gl) {
    synchronized (this) {
      _surfaceTexture.updateTexImage();
    }
  }

  private void _createSurface(GL10 gl, int width, int height) {
    int[] textures = new int[1];
    GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
    GLES20.glGenTextures(1, textures, 0);
    _checkGlError();
    GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textures[0]);
    _checkGlError();
    /*gl.glActiveTexture(GL10.GL_TEXTURE0);
    gl.glGenTextures(1, textures, 0);
    gl.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textures[0]);*/
    GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GL10.GL_TEXTURE_MIN_FILTER,GL10.GL_LINEAR);
    GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
    GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
    GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);
    _glTexture = textures[0];
    _surfaceTexture = new SurfaceTexture(_glTexture);
    _surfaceTexture.setDefaultBufferSize(width, height);
    _surface = new Surface(_surfaceTexture);
  }

  private void _destroySurface() {
    if (_surfaceTexture != null) {
      _surfaceTexture.release();
      _surfaceTexture = null;
    }
    if (_surface != null) {
      _surface.release();
      _surface = null;
    }
  }

  private void _checkGlError() {
    int error;
    while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
      Log.error(String.format(
        Locale.ENGLISH,
        "gl error. (error)=%s",
        GLUtils.getEGLErrorString(error)
      ));
    }
  }

}
