package pl.fulllegitcode.exoplayer;

import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLUtils;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;

public class Test implements Runnable {

  private static final int EGL_OPENGL_ES2_BIT = 4;
  private static final int EGL_CONTEXT_CLIENT_VERSION = 0x3098;
  private EGL10 egl;
  private EGLDisplay eglDisplay;
  private EGLContext eglContext;
  private EGLSurface eglSurface;

  public Test() {
    Thread thrd = new Thread(this);
    thrd.start();
  }

  @Override
  public void run() {
    initGL();
    Log.error("---------------------------------------");
    int[] textures = new int[1];
    GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
    GLES20.glGenTextures(1, textures, 0);
    GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textures[0]);
    Log.error("" + textures[0]);
    Log.error("---------------------------------------");
  }

  private void initGL()
  {
    egl = (EGL10) EGLContext.getEGL();
    eglDisplay = egl.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);

    int[] version = new int[2];
    egl.eglInitialize(eglDisplay, version);

    EGLConfig eglConfig = chooseEglConfig();
    eglContext = createContext(egl, eglDisplay, eglConfig);

    /*eglSurface = egl.eglCreateWindowSurface(eglDisplay, eglConfig, texture, null);

    if (eglSurface == null || eglSurface == EGL10.EGL_NO_SURFACE)
    {
      throw new RuntimeException("GL Error: " + GLUtils.getEGLErrorString(egl.eglGetError()));
    }

    if (!egl.eglMakeCurrent(eglDisplay, eglSurface, eglSurface, eglContext))
    {
      throw new RuntimeException("GL Make current error: " + GLUtils.getEGLErrorString(egl.eglGetError()));
    }*/
  }

  private EGLContext createContext(EGL10 egl, EGLDisplay eglDisplay, EGLConfig eglConfig)
  {
    int[] attribList = { EGL_CONTEXT_CLIENT_VERSION, 2, EGL10.EGL_NONE };
    return egl.eglCreateContext(eglDisplay, eglConfig, EGL10.EGL_NO_CONTEXT, attribList);
  }

  private EGLConfig chooseEglConfig()
  {
    int[] configsCount = new int[1];
    EGLConfig[] configs = new EGLConfig[1];
    int[] configSpec = getConfig();

    if (!egl.eglChooseConfig(eglDisplay, configSpec, configs, 1, configsCount))
    {
      throw new IllegalArgumentException("Failed to choose config: " + GLUtils.getEGLErrorString(egl.eglGetError()));
    }
    else if (configsCount[0] > 0)
    {
      return configs[0];
    }

    return null;
  }

  private int[] getConfig()
  {
    return new int[] {
      EGL10.EGL_RENDERABLE_TYPE, EGL_OPENGL_ES2_BIT,
      EGL10.EGL_RED_SIZE, 8,
      EGL10.EGL_GREEN_SIZE, 8,
      EGL10.EGL_BLUE_SIZE, 8,
      EGL10.EGL_ALPHA_SIZE, 8,
      EGL10.EGL_DEPTH_SIZE, 0,
      EGL10.EGL_STENCIL_SIZE, 0,
      EGL10.EGL_NONE
    };
  }

}
