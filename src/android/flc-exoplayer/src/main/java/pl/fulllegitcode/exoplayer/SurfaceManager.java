package pl.fulllegitcode.exoplayer;

import android.graphics.SurfaceTexture;
import android.opengl.EGL14;
import android.opengl.EGLConfig;
import android.opengl.EGLContext;
import android.opengl.EGLDisplay;
import android.opengl.EGLExt;
import android.opengl.EGLSurface;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.util.Log;
import android.view.Surface;

class SurfaceManager implements Runnable, SurfaceTexture.OnFrameAvailableListener {

  public interface ReadyCallback {
    void onReady();
  }


  public final static String TAG = "TextureManager";

  private static final int EGL_RECORDABLE_ANDROID = 0x3142;

  // Contexto donde dibujar
  private EGLContext mEGLContext = null;
  // Contexto compartido entre hilos. Para poder pasar el FBO de un hilo a otro
  private EGLContext mEGLSharedContext = null;
  private EGLSurface mEGLSurface = null;
  private EGLDisplay mEGLDisplay = null;

  // La surface donde se va a dibujar
  private Surface mSurface;

  public ReadyCallback readyCallback;
  private boolean _isFrameAvailable;
  private boolean _isRunning = true;

  private int _textureId;
  public int textureId() { return _textureId; }

  private SurfaceTexture _texture;
  public SurfaceTexture texture() { return _texture; }

  private Surface _surface;
  public Surface surface() { return _surface; }

  /**
   * Creates an EGL context and an EGL surface.
   */
  public SurfaceManager(Surface surface) {
    EGLContext shared = EGL14.eglGetCurrentContext();
    mEGLSharedContext = shared;
//    if (surface == null) {
//      throw new NullPointerException();
//    }
    mSurface = surface;
    android.util.Log.d("Surface2UnityDebug", "vamos al setup");
    Thread thread = new Thread(this);
    thread.start();
  }

  @Override
  public void run() {
    eglSetup();
    makeCurrent();

    int[] h = new int[1];
    GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
    GLES20.glGenTextures(1, h, 0);
    GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, h[0]);
    _textureId = h[0];
    Log.d("Surface2UnityDebug", "------------ gen tex: " + _textureId);

    _texture = new SurfaceTexture(_textureId);
    _texture.setOnFrameAvailableListener(this);
    _surface = new Surface(_texture);

    if (readyCallback != null) {
      readyCallback.onReady();
    }

    while (_isRunning) {
      synchronized (this) {
        if (_isFrameAvailable) {
          _texture.updateTexImage();
          _isFrameAvailable = false;
        }
      }

      try {
        Thread.sleep(10);
      } catch (Exception e) {

      }
    }
  }

  @Override
  public void onFrameAvailable(SurfaceTexture surfaceTexture) {
    synchronized (this) {
      _isFrameAvailable = true;
    }
  }

  // Hace que la surface actual sea esta
  public void makeCurrent() {
    if (!EGL14.eglMakeCurrent(mEGLDisplay, mEGLSurface, mEGLSurface, mEGLContext))
      throw new RuntimeException("eglMakeCurrent failed");
  }

  // Cambia el buffer donde se está pintando por el de la surface. es decir, guarda lo que se haya pintado.
  public void swapBuffers() {
    EGL14.eglSwapBuffers(mEGLDisplay, mEGLSurface);
  }

  /**
   * Sends the presentation time stamp to EGL.  Time is expressed in nanoseconds.
   */
  public void setPresentationTime(long nsecs) {
    EGLExt.eglPresentationTimeANDROID(mEGLDisplay, mEGLSurface, nsecs);
    checkEglError("eglPresentationTimeANDROID");
  }

  /**
   * Prepares EGL.  We want a GLES 2.0 context and a surface that supports recording.
   */
  private void eglSetup() {
    mEGLDisplay = EGL14.eglGetDisplay(EGL14.EGL_DEFAULT_DISPLAY);
    if (mEGLDisplay == EGL14.EGL_NO_DISPLAY) {
      android.util.Log.d("Surface2UnityDebug", "unable to get EGL14 display");
      throw new RuntimeException("unable to get EGL14 display");
    }
    int[] version = new int[2];
    if (!EGL14.eglInitialize(mEGLDisplay, version, 0, version, 1)) {
      android.util.Log.d("Surface2UnityDebug", "unable to initialize EGL14");
      throw new RuntimeException("unable to initialize EGL14");
    }

    // Configure EGL for recording and OpenGL ES 2.0.
    int[] attribList;
    attribList = new int[]{
      EGL14.EGL_RED_SIZE, 8,
      EGL14.EGL_GREEN_SIZE, 8,
      EGL14.EGL_BLUE_SIZE, 8,
      EGL14.EGL_RENDERABLE_TYPE, EGL14.EGL_OPENGL_ES2_BIT,
      EGL_RECORDABLE_ANDROID, 1,
      EGL14.EGL_NONE
    };
    EGLConfig[] configs = new EGLConfig[1];
    int[] numConfigs = new int[1];

    EGL14.eglChooseConfig(mEGLDisplay, attribList, 0, configs, 0, configs.length,
      numConfigs, 0);
    checkEglError("eglCreateContext RGB888+recordable ES2");

    // Configure context for OpenGL ES 2.0.
    int[] attrib_list = {
      EGL14.EGL_CONTEXT_CLIENT_VERSION, 3,
      EGL14.EGL_NONE
    };

    mEGLContext = EGL14.eglCreateContext(mEGLDisplay, configs[0], mEGLSharedContext, attrib_list, 0);
    checkEglError("eglCreateContext");

    // Create a window surface, and attach it to the Surface we received.
    int[] surfaceAttribs = {
      EGL14.EGL_NONE
    };
    mEGLSurface = EGL14.eglGetCurrentSurface(EGL14.EGL_DRAW);
    checkEglError("eglCreateWindowSurface");


  }

  /**
   * Discards all resources held by this class, notably the EGL context.  Also releases the
   * Surface that was passed to our constructor.
   */
  public void release() {
    if (mEGLDisplay != EGL14.EGL_NO_DISPLAY) {
      EGL14.eglMakeCurrent(mEGLDisplay, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_SURFACE,
        EGL14.EGL_NO_CONTEXT);
      EGL14.eglDestroySurface(mEGLDisplay, mEGLSurface);
      EGL14.eglDestroyContext(mEGLDisplay, mEGLContext);
      EGL14.eglReleaseThread();
      EGL14.eglTerminate(mEGLDisplay);
    }
    mEGLDisplay = EGL14.EGL_NO_DISPLAY;
    mEGLContext = EGL14.EGL_NO_CONTEXT;
    mEGLSurface = EGL14.EGL_NO_SURFACE;
    mSurface.release();
  }

  /**
   * Checks for EGL errors. Throws an exception if one is found.
   */
  private void checkEglError(String msg) {
    int error;
    if ((error = EGL14.eglGetError()) != EGL14.EGL_SUCCESS) {
      Log.d("Surface2UnityDebug", msg + ": EGL error: 0x" + Integer.toHexString(error));
      throw new RuntimeException(msg + ": EGL error: 0x" + Integer.toHexString(error));
    }
  }

}
