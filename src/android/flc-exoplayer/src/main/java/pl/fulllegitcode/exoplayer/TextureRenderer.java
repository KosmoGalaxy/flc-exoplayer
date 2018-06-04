package pl.fulllegitcode.exoplayer;

import android.opengl.GLES30;
import android.opengl.GLUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.Locale;

class TextureRenderer {

  private static final String vertexShaderCode =
    "attribute vec4 vPosition;" +
//    "attribute vec4 vTexCoordinate;" +
//    "uniform mat4 textureTransform;" +
//    "varying vec2 v_TexCoordinate;" +
    "void main() {" +
//    "  v_TexCoordinate = (textureTransform * vTexCoordinate).xy;" +
//    "  v_TexCoordinate = vTexCoordinate.xy;" +
    "  gl_Position = vPosition;" +
    "}";

  private static final String fragmentShaderCode =
    "#extension GL_OES_EGL_image_external : require\n" +
    "precision mediump float;" +
    "uniform sampler2D texture;" +
    "varying vec2 v_TexCoordinate;" +
    "void main () {" +
//    "  vec4 color = texture2D(texture, v_TexCoordinate);" +
    "  vec4 color = vec4(0.5, 0.5, 0.5, 0.5);" +
    "  gl_FragColor = color;" +
    "}";

  /*private float[] g_quad_vertex_buffer_data = {
    -1.0f, -1.0f, 0.0f,
     1.0f, -1.0f, 0.0f,
    -1.0f,  1.0f, 0.0f,
    -1.0f,  1.0f, 0.0f,
     1.0f, -1.0f, 0.0f,
     1.0f,  1.0f, 0.0f,
  };*/


  private static float squareSize = 1.0f;
  private static float squareCoords[] = { -squareSize,  squareSize, 0.0f,   // top left
    -squareSize, -squareSize, 0.0f,   // bottom left
    squareSize, -squareSize, 0.0f,   // bottom right
    squareSize,  squareSize, 0.0f }; // top right
  private static short drawOrder[] = { 0, 1, 2, 0, 2, 3};
  private FloatBuffer vertexBuffer;
  private ShortBuffer drawListBuffer;


  private int _inputTextureId;
  private int[] _frameBufferIdHolder;
  /*private int[] _vertexArrayIdHolder;
  private int[] _vertexBufferIdHolder;*/

  private int[] _outputTextureIdHolder;
  public int outputTextureId() { return _outputTextureIdHolder[0]; }

  private int _vertexShaderHandle;
  private int _fragmentShaderHandle;
  private int _shaderProgram;

  TextureRenderer(int inputTextureId) {
    _inputTextureId = inputTextureId;
    _createOutputTexture();
    _createFrameBuffer();
    _createProgram();
    _createVertexBuffer();
  }

  public void render() {
    GLES30.glUseProgram(_shaderProgram);
    _checkGlError("use program");

    GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, _frameBufferIdHolder[0]);
    _checkGlError("bind frame buffer");

    GLES30.glViewport(0, 0, 1024, 1024);
    _checkGlError("set viewport");

    int positionHandle = GLES30.glGetAttribLocation(_shaderProgram, "vPosition");
    _checkGlError("get position handle");

    GLES30.glEnableVertexAttribArray(positionHandle);
    _checkGlError("enable vertex attribute array");

    /*GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, _vertexBufferIdHolder[0]);
    _checkGlError("bind vertex buffer");

    GLES30.glVertexAttribIPointer(_vertexBufferIdHolder[0], 3, GLES30.GL_FLOAT, 0, 0);
    _checkGlError("set vertex attribute pointer");*/

    GLES30.glVertexAttribPointer(positionHandle, 3, GLES30.GL_FLOAT, false, 4 * 3, vertexBuffer);
    _checkGlError("set vertex buffer");

    GLES30.glDrawElements(GLES30.GL_TRIANGLES, drawOrder.length, GLES30.GL_UNSIGNED_SHORT, drawListBuffer);
    _checkGlError("draw elements");

    GLES30.glDisableVertexAttribArray(positionHandle);
  }

  private void _createOutputTexture() {
    _outputTextureIdHolder = new int[1];
    GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
    GLES30.glGenTextures(1, _outputTextureIdHolder, 0);
    _checkGlError("generate output texture");

    GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, _outputTextureIdHolder[0]);
    _checkGlError("bind output texture");

    GLES30.glTexImage2D(GLES30.GL_TEXTURE_2D, 0, GLES30.GL_RGBA, 1024, 1024, 0, GLES30.GL_RGBA, GLES30.GL_UNSIGNED_BYTE, null);
    _checkGlError("output texture image");

    GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_NEAREST);
    GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_NEAREST);

    Log.info(String.format(Locale.ENGLISH, "TextureRenderer output texture created. (id)=%d", _outputTextureIdHolder[0]));
  }

  private void _createFrameBuffer() {
    _frameBufferIdHolder = new int[1];
    GLES30.glGenFramebuffers(1, _frameBufferIdHolder, 0);
    _checkGlError("generate frame buffer");

    GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, _frameBufferIdHolder[0]);
    _checkGlError("bind frame buffer");

    GLES30.glFramebufferTexture2D(GLES30.GL_FRAMEBUFFER, GLES30.GL_COLOR_ATTACHMENT0, GLES30.GL_TEXTURE_2D, _outputTextureIdHolder[0], 0);
    _checkGlError("set frame buffer texture");

    IntBuffer drawBuffers = IntBuffer.wrap(new int[] { GLES30.GL_COLOR_ATTACHMENT0 });
    GLES30.glDrawBuffers(1, drawBuffers);
    _checkGlError("set draw buffers");
  }

  private void _createProgram() {
    _vertexShaderHandle = GLES30.glCreateShader(GLES30.GL_VERTEX_SHADER);
    GLES30.glShaderSource(_vertexShaderHandle, vertexShaderCode);
    GLES30.glCompileShader(_vertexShaderHandle);
    _checkGlError("compile vertex shader");

    _fragmentShaderHandle = GLES30.glCreateShader(GLES30.GL_FRAGMENT_SHADER);
    GLES30.glShaderSource(_fragmentShaderHandle, fragmentShaderCode);
    GLES30.glCompileShader(_fragmentShaderHandle);
    _checkGlError("compile fragment shader");

    _shaderProgram = GLES30.glCreateProgram();
    GLES30.glAttachShader(_shaderProgram, _vertexShaderHandle);
    GLES30.glAttachShader(_shaderProgram, _fragmentShaderHandle);
    GLES30.glLinkProgram(_shaderProgram);
    _checkGlError("compile program");

    int[] status = new int[1];
    GLES30.glGetProgramiv(_shaderProgram, GLES30.GL_LINK_STATUS, status, 0);
    if (status[0] != GLES30.GL_TRUE) {
      String error = GLES30.glGetProgramInfoLog(_shaderProgram);
      Log.error("error while linking program:\n" + error);
    }

    Log.info("TextureRenderer program created");
  }

  /*private void _createVertexBuffer()
  {
    _vertexArrayIdHolder = new int[1];
    GLES30.glGenVertexArrays(1, _vertexArrayIdHolder, 0);
    GLES30.glBindVertexArray(_vertexArrayIdHolder[0]);

    _vertexBufferIdHolder = new int[1];
    GLES30.glGenBuffers(1, _vertexBufferIdHolder, 0);
    GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, _vertexBufferIdHolder[0]);

    FloatBuffer buffer = FloatBuffer.wrap(g_quad_vertex_buffer_data);
    buffer.position(0);
    GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER, g_quad_vertex_buffer_data.length, buffer, GLES30.GL_STATIC_DRAW);
  }*/

  private void _createVertexBuffer()
  {
    // Draw list buffer
    ByteBuffer dlb = ByteBuffer.allocateDirect(drawOrder. length * 2);
    dlb.order(ByteOrder.nativeOrder());
    drawListBuffer = dlb.asShortBuffer();
    drawListBuffer.put(drawOrder);
    drawListBuffer.position(0);

    // Initialize the texture holder
    ByteBuffer bb = ByteBuffer.allocateDirect(squareCoords.length * 4);
    bb.order(ByteOrder.nativeOrder());

    vertexBuffer = bb.asFloatBuffer();
    vertexBuffer.put(squareCoords);
    vertexBuffer.position(0);
  }

  private void _checkGlError(String op)
  {
    int error;
    while ((error = GLES30.glGetError()) != GLES30.GL_NO_ERROR) {
      Log.error(op + ": glError " + GLUtils.getEGLErrorString(error));
    }
  }

}
