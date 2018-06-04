package pl.fulllegitcode.exoplayer;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.view.Surface;

import com.google.android.exoplayer2.SimpleExoPlayer;

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

  private final Context _context;
  private Context context() { return _context; }

  private final SimpleExoPlayer _player;
  private SimpleExoPlayer player() { return _player; }

  private TextureRenderer _renderer;
  private TextureRenderer renderer() { return _renderer; }

  private SurfaceTexture _texture;
  private SurfaceTexture texture() { return _texture; }

  SurfaceManager(Context context, SimpleExoPlayer player) {
    _context = context;
    _player = player;
  }

  public void setup(int textureId) {
    Log.info(String.format(Locale.ENGLISH, "SurfaceManager setup. (texture id)=%d", textureId));
    _createRenderer(textureId);
//    _surface = new Surface(renderer().videoTexture());
    player().setVideoSurface(surface());
  }

  public void update() {
    if (renderer() != null) {
//      renderer().update();
    }
  }

  private void _createRenderer(int textureId) {
    Log.info(String.format(Locale.ENGLISH, "SurfaceManager create renderer. (texture id)=%d", textureId));
    _renderer = new TextureRenderer(textureId);
  }

}
