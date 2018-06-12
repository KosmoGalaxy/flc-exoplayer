package pl.fulllegitcode.exoplayer;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class Triangle {

  private final String vertexShaderCode =
    "attribute vec4 vPosition;" +
    "void main() {" +
    "  gl_Position = vPosition;" +
    "}";

  private final String fragmentShaderCode =
    "precision mediump float;" +
    "uniform vec4 vColor;" +
    "void main() {" +
    "  gl_FragColor = vColor;" +
    "}";

  private final int vertexCount = triangleCoords.length / COORDS_PER_VERTEX;
  private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex

  private FloatBuffer vertexBuffer;
  private int mProgram;
  private int mPositionHandle;
  private int mColorHandle;

  // number of coordinates per vertex in this array
  static final int COORDS_PER_VERTEX = 3;
  static float triangleCoords[] = {   // in counterclockwise order:
    -1.0f,  1.0f, 0.0f, // top left
    -1.0f, -1.0f, 0.0f, // bottom left
     1.0f, -1.0f, 0.0f  // bottom right
  };

  // Set color with red, green, blue and alpha (opacity) values
  float color[] = { 1.0f, 0.0f, 0.0f, 1.0f };

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

    int vertexShader = TextureRenderer.loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
    int fragmentShader = TextureRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);

    // create empty OpenGL ES Program
    mProgram = GLES20.glCreateProgram();

    // add the vertex shader to program
    GLES20.glAttachShader(mProgram, vertexShader);

    // add the fragment shader to program
    GLES20.glAttachShader(mProgram, fragmentShader);

    // creates OpenGL ES program executables
    GLES20.glLinkProgram(mProgram);

    final int[] linkStatus = new int[1];
    GLES20.glGetProgramiv(mProgram, GLES20.GL_LINK_STATUS, linkStatus, 0);
    if (linkStatus[0] == 0) {
      Log.error("program linking failed");
    }
  }

  public void draw() {
    // Add program to OpenGL ES environment
    GLES20.glUseProgram(mProgram);
    TextureRenderer.checkGlError("glUseProgram");

    // get handle to vertex shader's vPosition member
    mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
//    Log.info("position handle: " + mPositionHandle);
    TextureRenderer.checkGlError("glGetAttribLocation");

    // Enable a handle to the triangle vertices
    GLES20.glEnableVertexAttribArray(mPositionHandle);
    TextureRenderer.checkGlError("glEnableVertexAttribArray");

    // Prepare the triangle coordinate data
    GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX,
      GLES20.GL_FLOAT, false,
      vertexStride, vertexBuffer);
    TextureRenderer.checkGlError("glVertexAttribPointer");

    // get handle to fragment shader's vColor member
    mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");
//    Log.info("color handle: " + mColorHandle);
    TextureRenderer.checkGlError("glGetUniformLocation");

    // Set color for drawing the triangle
    GLES20.glUniform4fv(mColorHandle, 1, color, 0);
    TextureRenderer.checkGlError("glUniform4fv");

    // Draw the triangle
    GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount);
    TextureRenderer.checkGlError("glDrawArrays");

    // Disable vertex array
    GLES20.glDisableVertexAttribArray(mPositionHandle);
    TextureRenderer.checkGlError("glDisableVertexAttribArray");
  }

}
