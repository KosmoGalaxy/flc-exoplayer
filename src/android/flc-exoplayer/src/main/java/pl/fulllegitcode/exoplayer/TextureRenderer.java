package pl.fulllegitcode.exoplayer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;

public class TextureRenderer {

  public static int loadShader(int type, String shaderCode){
    int shader = GLES20.glCreateShader(type);
    GLES20.glShaderSource(shader, shaderCode);
    GLES20.glCompileShader(shader);

    final int[] compileStatus = new int[1];
    GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compileStatus, 0);
    if (compileStatus[0] == 0) {
      String sType = type == GLES20.GL_VERTEX_SHADER ? "vertex shader" : "fragment shader";
      Log.error("shader compilation failed: " + sType);
    }

    return shader;
  }

  public static void checkGlError(String op) {
    int error;
    while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
      Log.error(op + ": glError " + GLUtils.getEGLErrorString(error));
    }
  }


  private int _inputTextureId;
  private Triangle _triangle;

  private Context _context;
  private Bitmap _bitmap;
  private boolean _isInited;

  public TextureRenderer(Context context, int inputTextureId) {
    _context = context;
    _inputTextureId = inputTextureId;
    Log.info("texture id: " + inputTextureId);
//    _setup();

    _bitmap = BitmapFactory.decodeResource(_context.getResources(), R.drawable.test);
    Log.info("bitmap: " + _bitmap);

    int[] temp = new int[1];
    GLES20.glGenTextures(1, temp, 0);
    fboTex = temp[0];
    GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, fboTex);
    GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
    GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
    GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
    GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
//    GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, fboWidth, fboHeight, 0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null);
    GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, _bitmap, 0);
    checkGlError("copy");
  }

  public void render(float time) {
//    if (!_isInited) {
//      return;
//    }

//    GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, fboId);
//    GLES20.glViewport(0, 0, fboWidth, fboHeight);

//    float g = ((float) Math.sin(time * Math.PI) + 1f) / 4f;
//    float b = ((float) Math.cos(time * Math.PI) + 1f) / 4f;
//    GLES20.glClearColor(1.0f, g, b, 1.0f);
//    GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

//    _triangle.draw();
    checkGlError("draw");
//    GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
//    GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, fboTex);
    checkGlError("bind");
//    GLES20.glCopyTexSubImage2D(GLES20.GL_TEXTURE_2D, 0, 0, 0, 0, 0, fboWidth, fboHeight);
//    GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, _bitmap, 0);
    checkGlError("copy");

//    GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
//    GLES20.glViewport(0, 0, 1080, 1920);
    checkGlError("render");
  }

  private int fboId;
  public int fboTex;
  private int texRenderBufferId;
  private int depthRenderBufferId;
  private int stencilRenderBufferId;
  private int fboWidth = 1024;
  private int fboHeight = 1024;
  private void _setup() {
    int[] temp = new int[1];
//generate fbo id
    GLES20.glGenFramebuffers(1, temp, 0);
    fboId = temp[0];
//generate texture
    GLES20.glGenTextures(1, temp, 0);
    fboTex = temp[0];
    GLES20.glGenRenderbuffers(1, temp, 0);
    texRenderBufferId = temp[0];
//generate render buffer
    GLES20.glGenRenderbuffers(1, temp, 0);
    depthRenderBufferId = temp[0];
    // stencil
//    GLES20.glGenRenderbuffers(1, temp, 0);
//    stencilRenderBufferId = temp[0];
//Bind Frame buffer
    GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, fboId);
//Bind texture
    GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, fboTex);
//Define texture parameters
    GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, fboWidth, fboHeight, 0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null);
    GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
    GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
    GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
    GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
//Bind render buffer and define buffer dimension
    GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, depthRenderBufferId);
    GLES20.glRenderbufferStorage(GLES20.GL_RENDERBUFFER, GLES20.GL_DEPTH_COMPONENT16, fboWidth, fboHeight);
    GLES20.glFramebufferRenderbuffer(GLES20.GL_FRAMEBUFFER, GLES20.GL_DEPTH_ATTACHMENT, GLES20.GL_RENDERBUFFER, depthRenderBufferId);
//Attach texture FBO color attachment
//    GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_TEXTURE_2D, fboTex, 0);
    GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, texRenderBufferId);
    GLES20.glRenderbufferStorage(GLES20.GL_RENDERBUFFER, GLES20.GL_RGBA4, fboWidth, fboHeight);
    GLES20.glFramebufferRenderbuffer(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_RENDERBUFFER, texRenderBufferId);
    // stencil
//    GLES20.glEnable(GLES20.GL_STENCIL_TEST);
//    GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, stencilRenderBufferId);
//    GLES20.glRenderbufferStorage(GLES20.GL_RENDERBUFFER, GLES20.GL_STENCIL_INDEX8, fboWidth, fboHeight);
//    GLES20.glFramebufferRenderbuffer(GLES20.GL_FRAMEBUFFER, GLES20.GL_STENCIL_ATTACHMENT, GLES20.GL_RENDERBUFFER, stencilRenderBufferId);
//Attach render buffer to depth attachment

    int status = GLES20.glCheckFramebufferStatus(GLES20.GL_FRAMEBUFFER);
    if (status != GLES20.GL_FRAMEBUFFER_COMPLETE) {
      Log.error("TextureRenderer - frame buffer incomplete");
      return;
    }

    _triangle = new Triangle();

//we are done, reset
    GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
    GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, 0);
    GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);

    Log.info("TextureRenderer - init complete");
    checkGlError("setup");

    _isInited = true;
  }

}
