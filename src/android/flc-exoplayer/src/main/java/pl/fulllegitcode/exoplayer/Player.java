package pl.fulllegitcode.exoplayer;

import android.content.Context;
import android.graphics.SurfaceTexture;
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
    _surfaceManager = new SurfaceManager(context());
    _prepareManager = new PrepareManager(context(), player());
    _setupPlayer();
    _bindPlayer();
    _loop();
  }

  //region control

  private SurfaceTexture _asd;
  public void prepare(String uri, SurfaceTexture texture) {
    _asd = texture;
    _reset();
    _state = State.Preparing;
    prepareManager().prepare(uri);
  }

  //endregion

  //region lifecycle

  public void dispose() {
    _isDisposed = true;
  }

  private void _reset() {
    _state = State.Idle;
    surfaceManager().reset();
  }

  private void _loop() {
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
  }

  private void _updatePreparing() {
    Format format = player().getVideoFormat();
    if (format == null) {
      return;
    }
    if (surfaceManager().surface() == null && !surfaceManager().isDuringSetup()) {
      surfaceManager().setup(format.width, format.height, _asd);
    }
    if (prepareManager().surface() == null && surfaceManager().surface() != null) {
      prepareManager().setSurface(surfaceManager().surface());
    }
  }

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
