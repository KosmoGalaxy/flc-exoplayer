package pl.fulllegitcode.exoplayer_test;

import android.opengl.GLES20;
import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import pl.fulllegitcode.exoplayer.Log;
import pl.fulllegitcode.exoplayer.TextureRenderer;

public class Triangle {

  private final String vertexShaderCode =
    "attribute vec4 vPosition;" +
    "attribute vec2 aTexCoord;" +
    "varying vec2 vTexCoord;" +
    "void main() {" +
    "  gl_Position = vPosition;" +
    "  vTexCoord = aTexCoord;" +
    "}";

  private final String fragmentShaderCode =
    "precision mediump float;" +
//    "uniform vec4 vColor;" +
    "uniform sampler2D uTex;" +
    "varying vec2 vTexCoord;" +
    "void main() {" +
    "  gl_FragColor = texture2D(uTex, vTexCoord);" +
    "}";

  private final int vertexCount = triangleCoords.length / COORDS_PER_VERTEX;
  private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex

  private FloatBuffer vertexBuffer;
  private FloatBuffer texCoordBuffer;
  private int mProgram;
  private int mPositionHandle;
  private int mColorHandle;

  // number of coordinates per vertex in this array
  static final int COORDS_PER_VERTEX = 3;
  static float triangleCoords[] = {   // in counterclockwise order:
     0.0f,  0.622008459f, 0.0f, // top
    -0.5f, -0.311004243f, 0.0f, // bottom left
     0.5f, -0.311004243f, 0.0f  // bottom right
  };

  static float[] textureCoords = {
    0.0f, 0.0f,
    0.0f, 1.0f,
    1.0f, 1.0f
  };

  // Set color with red, green, blue and alpha (opacity) values
  float color[] = { 0.0f, 1.0f, 0.0f, 1.0f };

  private boolean _isInited;

  public Triangle() {
    // initialize vertex byte buffer for shape coordinates
    ByteBuffer bb = ByteBuffer.allocateDirect(
      // (number of coordinate values * 4 bytes per float)
      triangleCoords.length * 4);
    // use the device hardware's native byte order
    bb.order(ByteOrder.nativeOrder());

    // create a floating point buffer from the ByteBuffer
    vertexBuffer = bb.asFloatBuffer();
    // add the coordinates to the FloatBuffer
    vertexBuffer.put(triangleCoords);
    // set the buffer to read the first coordinate
    vertexBuffer.position(0);

    texCoordBuffer = ByteBuffer.allocateDirect(textureCoords.length * 4)
      .order(ByteOrder.nativeOrder())
      .asFloatBuffer();
    texCoordBuffer.put(textureCoords);
    texCoordBuffer.position(0);

    int vertexShader = MyRenderer.loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
    if (vertexShader == 0) {
      return;
    }

    int fragmentShader = MyRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);
    if (fragmentShader == 0) {
      return;
    }

    // create empty OpenGL ES Program
    mProgram = GLES20.glCreateProgram();

    // add the vertex shader to program
    GLES20.glAttachShader(mProgram, vertexShader);

    // add the fragment shader to program
    GLES20.glAttachShader(mProgram, fragmentShader);

    // creates OpenGL ES program executables
    GLES20.glLinkProgram(mProgram);

    Log.info("Triangle init complete");
    _isInited = true;
  }

  public void draw(int textureId) {
    if (!_isInited) {
      return;
    }

    // Add program to OpenGL ES environment
    GLES20.glUseProgram(mProgram);

    // get handle to vertex shader's vPosition member
    mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
//    Log.info("position handle: " + mPositionHandle);
    TextureRenderer.checkGlError("glGetAttribLocation");

    // Enable a handle to the triangle vertices
    GLES20.glEnableVertexAttribArray(mPositionHandle);

    // Prepare the triangle coordinate data
    GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX,
      GLES20.GL_FLOAT, false,
      vertexStride, vertexBuffer);

    // get handle to fragment shader's vColor member
//    mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");

    // Set color for drawing the triangle
//    GLES20.glUniform4fv(mColorHandle, 1, color, 0);

    int texCoordHandle = GLES20.glGetAttribLocation(mProgram, "aTexCoord");
    GLES20.glEnableVertexAttribArray(texCoordHandle);
    GLES20.glVertexAttribPointer(texCoordHandle, 2, GLES20.GL_FLOAT, false, 2 * 4, texCoordBuffer);

    int texHandle = GLES20.glGetUniformLocation(mProgram, "uTex");
    GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
    GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
    GLES20.glUniform1i(texHandle, 0);

    // Draw the triangle
    GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount);

    // Disable vertex array
    GLES20.glDisableVertexAttribArray(mPositionHandle);

    GLES20.glDisableVertexAttribArray(texCoordHandle);
  }

}
