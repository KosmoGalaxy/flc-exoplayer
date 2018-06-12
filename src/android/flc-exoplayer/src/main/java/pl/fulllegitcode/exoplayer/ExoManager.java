package pl.fulllegitcode.exoplayer;

import android.content.Context;
import android.net.Uri;
import android.view.Surface;

import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;

import java.util.Locale;

public class ExoManager {

  private SimpleExoPlayer _player;

  public ExoManager(Context context, Surface surface) {
    BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
    TrackSelection.Factory factory = new AdaptiveTrackSelection.Factory(bandwidthMeter);

    _player = ExoPlayerFactory.newSimpleInstance(context, new DefaultTrackSelector(factory));
    if (surface != null) {
      _player.setVideoSurface(surface);
    }
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
      context,
      Util.getUserAgent(context)
    );
    MediaSource mediaSource = new ExtractorMediaSource.Factory(dataSourceFactory)
      .createMediaSource(Uri.parse("http://rdstest.pl/redbull_1.mp4"));
    _player.prepare(mediaSource);
  }
}
