package pl.fulllegitcode.exoplayer;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLES30;
import android.opengl.GLUtils;
import android.view.Surface;

import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;

import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.util.Locale;

public class Player {

  enum State {
    Idle,
    Preparing,
    Ready
  }


  private State _state = State.Idle;
  public State state() { return _state; }

  private boolean _isDisposed = false;
  public boolean isDisposed() { return _isDisposed; }

  private final Context _context;
  private Context context() { return _context; }

  private final SimpleExoPlayer _player;
  private SimpleExoPlayer player() { return _player; }

  private final SurfaceManager _surfaceManager;
  private SurfaceManager surfaceManager() { return _surfaceManager; }

  private final PrepareManager _prepareManager;
  private PrepareManager prepareManager() { return _prepareManager; }

  public Player(Context context) {
    _context = context;
    _player = _createPlayer();
    _surfaceManager = new SurfaceManager(context(), player());
    _prepareManager = new PrepareManager(context(), player());
    _setupPlayer();
    _bindPlayer();
  }

  private TextureRenderer _test_renderer;
  public int test_init(int textureId) {
    _test_renderer = new TextureRenderer(textureId);
    return _test_renderer.outputTextureId();
  }
  public void test_render() {
    _test_renderer.render();
  }

/*//  private int[] _test_sourceTextureIdHolder;
  private int _test_sourceTextureId;
  private int[] _test_destinationBufferIdHolder;
  private int[] _test_destinationTextureIdHolder;
  private int[] _test_vertexArrayIdHolder;
  private int[] _test_vertexBufferIdHolder;
  private float[] g_quad_vertex_buffer_data = {
    -1.0f, -1.0f, 0.0f,
    1.0f, -1.0f, 0.0f,
    -1.0f,  1.0f, 0.0f,
    -1.0f,  1.0f, 0.0f,
    1.0f, -1.0f, 0.0f,
    1.0f,  1.0f, 0.0f,
  };
  public int test_init(int sourceTextureId) {
    // source texture
//    _test_sourceTextureIdHolder = new int[1];
//    GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
//    GLES30.glGenTextures(1, _test_sourceTextureIdHolder, 0);
//    _checkGlError("source texture generate");
//    Log.info("----- source texture id: " + _test_sourceTextureIdHolder[0]);
//    GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, _test_sourceTextureIdHolder[0]);
//    _checkGlError("source texture bind");
//    GLES30.glTexImage2D(GLES30.GL_TEXTURE_2D, 0, GLES30.GL_RGBA, 1024, 1024, 0, GLES30.GL_RGBA, GLES30.GL_UNSIGNED_BYTE, null);
//    _checkGlError("source texture image");
    _test_sourceTextureId = sourceTextureId;
    // destination buffer
    _test_destinationBufferIdHolder = new int[1];
    GLES30.glGenFramebuffers(1, _test_destinationBufferIdHolder, 0);
    _checkGlError("destination buffer generate");
    Log.info("----- destination buffer id: " + _test_destinationBufferIdHolder[0]);
    GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, _test_destinationBufferIdHolder[0]);
    _checkGlError("destination buffer bind");
    // destination texture
    _test_destinationTextureIdHolder = new int[1];
    GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
    GLES30.glGenTextures(1, _test_destinationTextureIdHolder, 0);
    _checkGlError("destination texture generate");
    Log.info("----- destination texture id: " + _test_destinationTextureIdHolder[0]);
    GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, _test_destinationTextureIdHolder[0]);
    _checkGlError("destination texture bind");
    GLES30.glTexImage2D(GLES30.GL_TEXTURE_2D, 0, GLES30.GL_RGBA, 1024, 1024, 0, GLES30.GL_RGBA, GLES30.GL_UNSIGNED_BYTE, null);
    _checkGlError("destination texture image");
    GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_NEAREST);
    GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_NEAREST);
    // vertex array
    _test_vertexArrayIdHolder = new int[1];
    GLES30.glGenVertexArrays(1, _test_vertexArrayIdHolder, 0);
    _checkGlError("vertex array generate");
    GLES30.glBindVertexArray(_test_vertexArrayIdHolder[0]);
    _checkGlError("vertex array bind");
    // vertex buffer
    GLES30.glGenBuffers(1, _test_vertexBufferIdHolder, 0);
    _checkGlError("vertex buffer generate");
    GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, _test_vertexBufferIdHolder[0]);
    _checkGlError("vertex buffer bind");
    FloatBuffer buffer = FloatBuffer.wrap(g_quad_vertex_buffer_data);
    buffer.position(0);
    GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER, g_quad_vertex_buffer_data.length, buffer, GLES30.GL_STATIC_DRAW);
    // program
    loadShaders();
    return _test_destinationTextureIdHolder[0];
  }
  private static final String vertexShaderCode =
    "layout(location = 0) in vec3 vertexPosition_modelspace;" +
    "out vec2 UV;" +
    "void main() {" +
    "  gl_Position =  vec4(vertexPosition_modelspace,1);" +
    "  UV = (vertexPosition_modelspace.xy+vec2(1,1))/2.0;" +
    "}";

  private static final String fragmentShaderCode =
    "in vec2 UV;" +
    "out vec3 color;" +
    "uniform sampler2D renderedTexture;" +
    "void main() {" +
    "  color = texture( renderedTexture, UV + 0.005*vec2( sin(1024.0*UV.x),cos(1024.0*UV.y)) ).xyz;" +
    "}";
  private int vertexShaderHandle;
  private int fragmentShaderHandle;
  private int shaderProgram;
  private void loadShaders()
  {
    vertexShaderHandle = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER);
    GLES20.glShaderSource(vertexShaderHandle, vertexShaderCode);
    GLES20.glCompileShader(vertexShaderHandle);
    _checkGlError("Vertex shader compile");

    fragmentShaderHandle = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER);
    GLES20.glShaderSource(fragmentShaderHandle, fragmentShaderCode);
    GLES20.glCompileShader(fragmentShaderHandle);
    _checkGlError("Pixel shader compile");

    shaderProgram = GLES20.glCreateProgram();
    GLES20.glAttachShader(shaderProgram, vertexShaderHandle);
    GLES20.glAttachShader(shaderProgram, fragmentShaderHandle);
    GLES20.glLinkProgram(shaderProgram);
    _checkGlError("Shader program compile");

    int[] status = new int[1];
    GLES20.glGetProgramiv(shaderProgram, GLES20.GL_LINK_STATUS, status, 0);
    if (status[0] != GLES20.GL_TRUE) {
      String error = GLES20.glGetProgramInfoLog(shaderProgram);
      android.util.Log.e("SurfaceTest", "Error while linking program:\n" + error);
    }
  }
  public void test_draw() {
    // source texture
    //GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, _test_sourceTextureIdHolder[0]);
    //_checkGlError("source texture bind");
    //GLES30.glClearColor((float) Math.random(), (float) Math.random(), (float) Math.random(), 1f);
    //GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT);
    //_checkGlError("source texture clear");
    // destination
    GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, _test_destinationBufferIdHolder[0]);
    _checkGlError("destination buffer bind");
    GLES30.glFramebufferTexture2D(GLES30.GL_FRAMEBUFFER, GLES30.GL_COLOR_ATTACHMENT0, GLES30.GL_TEXTURE_2D, _test_destinationTextureIdHolder[0], 0);
    _checkGlError("destination buffer texture");
    GLES30.glDrawBuffers(1, new int[] { GLES30.GL_COLOR_ATTACHMENT0 }, 0);
    _checkGlError("draw buffers");
    GLES30.glViewport(0, 0, 1024, 1024);
    _checkGlError("viewport");
  }*/

  public void test2(int textureId) {
    GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
    _checkGlError("bind");
    GLES20.glClearColor((float) Math.random(), 1f, 1f, 1f);
    GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
    _checkGlError("clear");
  }

  private int[] _test3_textureIdHolder = new int[1];
  private SurfaceTexture _test3_texture;
  private Surface _test3_surface;
  public int test3() {
    GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
    GLES20.glGenTextures(1, _test3_textureIdHolder, 0);
    _checkGlError("Texture generate");
    GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, _test3_textureIdHolder[0]);
    _checkGlError("Texture bind");
    _test3_texture = new SurfaceTexture(_test3_textureIdHolder[0]);
    _test3_surface = new Surface(_test3_texture);
    player().setVideoSurface(_test3_surface);
    prepareManager().prepare("http://rdstest.pl/redbull_1.mp4");
    return _test3_textureIdHolder[0];
  }
  public void test3_update() {
    if (_test3_texture != null) {
      _test3_texture.updateTexImage();
    }
  }

  private void _checkGlError(String op)
  {
    int error;
    while ((error = GLES30.glGetError()) != GLES30.GL_NO_ERROR) {
      android.util.Log.e("Test", op + ": glError " + GLUtils.getEGLErrorString(error));
    }
  }

  //region control

  public void prepare(String uri, int textureId) {
    Log.info(String.format(Locale.ENGLISH, "Player prepare. (uri)=%s (texture id)=%d", uri, textureId));
    surfaceManager().setup(textureId);
    prepareManager().prepare(uri);
  }

  //endregion

  //region lifecycle

  public void update() {
    surfaceManager().update();
  }

  /*private void _loop() {
    final Handler handler = new Handler();
    handler.postDelayed(
      new Runnable() {
        @Override
        public void run() {
          if (!isDisposed()) {
            surfaceManager().update();
            switch (state()) {
              case Preparing:
                _updatePreparing();
                break;
            }
            handler.postDelayed(this, 100);
          }
        }
      },
      100
    );
  }*/

  //endregion

  //region factory

  private SimpleExoPlayer _createPlayer() {
    TrackSelector trackSelector = _createTrackSelector();
    return ExoPlayerFactory.newSimpleInstance(context(), trackSelector);
  }

  private TrackSelector _createTrackSelector() {
    BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
    TrackSelection.Factory factory = new AdaptiveTrackSelection.Factory(bandwidthMeter);
    return new DefaultTrackSelector(factory);
  }

  //endregion

  //region setup

  private void _setupPlayer() {
    player().setPlayWhenReady(true);
  }

  //endregion

  //region bind

  private void _bindPlayer() {
    player().addListener(new com.google.android.exoplayer2.Player.DefaultEventListener() {
      @Override
      public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        Log.info(String.format(
          Locale.ENGLISH,
          "player state changed. (play when ready)=%b (playback state)=%s",
          playWhenReady,
          Util.getPlaybackStateName(playbackState)
        ));
        Format format = player().getVideoFormat();
        if (format != null) {
          Log.info(
            String.format(Locale.ENGLISH,
            "video size. (width)=%d (height)=%d",
            format.width,
            format.height
          ));
        }
      }
    });
  }

  //endregion

}
