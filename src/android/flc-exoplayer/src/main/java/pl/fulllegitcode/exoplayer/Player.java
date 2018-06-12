package pl.fulllegitcode.exoplayer;

import android.content.Context;
import android.view.Surface;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLContext;

import gl.VideoTextureRenderer;

public class Player {

  /*private static SimpleExoPlayer _player;
  public static void test() {
    int[] h = new int[1];
    GLES20.glGenTextures(1, h, 0);
    GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
    GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, h[0]);
    android.util.Log.d("FlcExoPlayer", "--------------------------------- test: " + h[0]);

    SurfaceTexture st = new SurfaceTexture(h[0]);
    st.setDefaultBufferSize(1080, 1920);
    Surface surface = new Surface(st);

    BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
    TrackSelection.Factory factory = new AdaptiveTrackSelection.Factory(bandwidthMeter);

    _player = ExoPlayerFactory.newSimpleInstance(_context, new DefaultTrackSelector(factory));
    _player.setVideoSurface(surface);
    _player.setPlayWhenReady(true);
    _player.addListener(new com.google.android.exoplayer2.Player.DefaultEventListener() {
      @Override
      public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        Log.info(String.format(
          Locale.ENGLISH,
          "player state changed. (play when ready)=%b (playback state)=%s",
          playWhenReady,
          Util.getPlaybackStateName(playbackState)
        ));
        Format format = _player.getVideoFormat();
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
    DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(
      _context,
      Util.getUserAgent(_context)
    );
    MediaSource mediaSource = new ExtractorMediaSource.Factory(dataSourceFactory)
      .createMediaSource(Uri.parse("http://rdstest.pl/redbull_1.mp4"));
    _player.prepare(mediaSource);
  }*/


  private EGLContext _eglContext;
  private VideoTextureRenderer _renderer;
  private ExoManager _exoManager;
  private SurfaceManager _surfaceManager;

  public Player() {
    Log.info("new player");
    _setupEglContext();

    /*_renderer = new VideoTextureRenderer(_getContext(), null, 1024, 1024, _eglContext);
    _renderer.videoTextureReadyCallback = new VideoTextureRenderer.VideoTextureReadyCallback() {
      @Override
      public void onReady() {
        Log.info("video texture ready: " + _renderer.getVideoTextureId());
        _exoManager = new ExoManager(_getContext(), new Surface(_renderer.getVideoTexture()));
      }
    };*/

    _surfaceManager = new SurfaceManager(null);
    _surfaceManager.readyCallback = new SurfaceManager.ReadyCallback() {
      @Override
      public void onReady() {
        _exoManager = new ExoManager(_getContext(), _surfaceManager.surface());
      }
    };
  }

  public int getTextureId() {
    return _surfaceManager != null ? _surfaceManager.textureId() : 0;
  }

  protected Context _getContext() { return null; }

  private void _setupEglContext() {
    _eglContext = ((EGL10) EGLContext.getEGL()).eglGetCurrentContext();
    if (_eglContext == null || _eglContext == EGL10.EGL_NO_CONTEXT) {
      Log.error("egl context not found");
    } else {
      Log.info("egl context found");
    }
  }

}
