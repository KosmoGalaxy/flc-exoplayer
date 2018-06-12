package pl.fulllegitcode.exoplayer_test;

import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import pl.fulllegitcode.exoplayer.Log;
import pl.fulllegitcode.exoplayer.TextureRenderer;

public class MyRenderer implements GLSurfaceView.Renderer {

  public static int loadShader(int type, String shaderCode){
    // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
    // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
    int shader = GLES20.glCreateShader(type);

    // add the source code to the shader and compile it
    GLES20.glShaderSource(shader, shaderCode);
    GLES20.glCompileShader(shader);

    final int[] compileStatus = new int[1];
    GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compileStatus, 0);
    if (compileStatus[0] == 0) {
      String sType = type == GLES20.GL_VERTEX_SHADER ? "vertex shader" : "fragment shader";
      Log.error("shader compilation failed: " + sType);
      return 0;
    }

    return shader;
  }


  private int _width;
  private int _height;

  private Triangle _triangle;
  private TextureRenderer _textureRenderer;

  private Bitmap _bitmap;
  private Texture _texture;

  private int texId;

  public MyRenderer(Bitmap bitmap) {
    _bitmap = bitmap;
  }

  @Override
  public void onSurfaceCreated(GL10 gl, EGLConfig config) {
    _triangle = new Triangle();
//    _textureRenderer = new TextureRenderer(MainActivity.instance, 0);
    _texture = new Texture(_bitmap);
  }

  @Override
  public void onSurfaceChanged(GL10 gl, int width, int height) {
    _width = width;
    _height = height;
  }

  @Override
  public void onDrawFrame(GL10 gl) {
//    _textureRenderer.render(1.0f);
    GLES20.glViewport(0, 0, _width, _height);
    GLES20.glClearColor(0.2f, 0.2f, 0.2f, 1.0f);
    GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
    _triangle.draw(_texture.id());
  }

}
