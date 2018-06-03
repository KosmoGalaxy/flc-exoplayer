package pl.fulllegitcode.exoplayer;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.os.Handler;
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

  public int test() {
    int[] textureIdHolder = new int[1];
    GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
    GLES20.glGenTextures(1, textureIdHolder, 0);
    _checkGlError("gen");
    GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureIdHolder[0]);
    _checkGlError("bind");
    GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, 1024, 1024, 0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null);
    _checkGlError("tex");
    return textureIdHolder[0];
  }

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
    while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
      android.util.Log.e("SurfaceTest", op + ": glError " + GLUtils.getEGLErrorString(error));
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
