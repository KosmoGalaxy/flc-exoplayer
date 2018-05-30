package pl.fulllegitcode.exoplayer;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.view.Surface;

import java.util.Locale;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;

import gl.VideoTextureRenderer;

class SurfaceManager {

  private boolean _isDuringSetup = false;
  public boolean isDuringSetup() { return _isDuringSetup; }

  private Surface _surface;
  public Surface surface() { return _surface; }

  public int textureId() { return textureIdHolder()[0]; }

  private final Context _context;
  private Context context() { return _context; }

  private VideoTextureRenderer _renderer;
  private VideoTextureRenderer renderer() { return _renderer; }

  private SurfaceTexture _texture;
  private SurfaceTexture texture() { return _texture; }

  private final int[] _textureIdHolder = new int[1];
  private int[] textureIdHolder() { return _textureIdHolder; }

  SurfaceManager(Context context) {
    _context = context;
  }

  public void reset() {
    if (surface() != null) {
      surface().release();
      _surface = null;
    }
    if (texture() != null) {
      texture().release();
      _texture = null;
    }
    if (renderer() != null) {
      renderer().onPause();
      _renderer = null;
    }
    _isDuringSetup = false;
  }

  public void setup(int width, int height, SurfaceTexture texture) {
    Log.info(String.format(Locale.ENGLISH, "surface manager setup. (width)=%d (height)=%d", width, height));
    reset();
    _isDuringSetup = true;
//    _createTexture();
    _texture = texture;
    _createRenderer(width, height);
  }

  public void update() {
    if (surface() == null && renderer() != null) {
      SurfaceTexture texture = renderer().getVideoTexture();
      if (texture != null) {
        _surface = new Surface(texture);
        Log.info("surface created");
      }
    }
  }

  private void _createTexture() {
    GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
    GLES20.glGenTextures(1, textureIdHolder(), 0);
    GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textureIdHolder()[0]);
    _texture = new SurfaceTexture(textureId());
    Log.info(String.format(Locale.ENGLISH, "texture created. (id)=%d", textureId()));
  }

  private void _createRenderer(int width, int height) {
    _renderer = new VideoTextureRenderer(context(), texture(), width, height);
  }

}
