package pl.fulllegitcode.exoplayer;

import android.content.Context;
import android.net.Uri;
import android.view.Surface;

import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;

import java.util.Locale;

class PrepareManager {

  private Surface _surface;
  public Surface surface() { return _surface; }


  private final Context _context;
  private Context context() { return _context; }

  private final SimpleExoPlayer _player;
  private SimpleExoPlayer player() { return _player; }

  PrepareManager(Context context, SimpleExoPlayer player) {
    _context = context;
    _player = player;
  }

  public void prepare(String uri) {
    Log.info(String.format(Locale.ENGLISH, "prepare. (uri)=%s", uri));
    _reset();
    DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(
      context(),
      Util.getUserAgent(context())
    );
    MediaSource mediaSource = new ExtractorMediaSource.Factory(dataSourceFactory)
      .createMediaSource(Uri.parse(uri));
    player().prepare(mediaSource);
  }

  public void setSurface(Surface surface) {
    Log.info("prepare manager surface set");
    _surface = surface;
    player().setVideoSurface(surface());
  }

  private void _reset() {
    _surface = null;
  }

}
