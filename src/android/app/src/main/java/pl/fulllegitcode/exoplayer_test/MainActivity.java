package pl.fulllegitcode.exoplayer_test;

import android.graphics.SurfaceTexture;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.TextureView;
import android.widget.LinearLayout;

import java.util.Locale;

import pl.fulllegitcode.exoplayer.Player;
import pl.fulllegitcode.exoplayer.Test;

public class MainActivity extends AppCompatActivity implements TextureView.SurfaceTextureListener {

  final String TAG = "FlcExoPlayerTest";

  private Player _player;
  private TextureView _view;
  private Test _test;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    _test();
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    if (_player != null) {
//      _player.dispose();
    }
  }

  private void _test() {
    _player = new Player(this);
    _view = new TextureView(this);
    _view.setSurfaceTextureListener(this);
    addContentView(
      _view,
      new LinearLayout.LayoutParams(
        LinearLayout.LayoutParams.MATCH_PARENT,
        LinearLayout.LayoutParams.MATCH_PARENT
      )
    );
    /*addContentView(
      _player.view(),
      new LinearLayout.LayoutParams(
        LinearLayout.LayoutParams.MATCH_PARENT,
        LinearLayout.LayoutParams.MATCH_PARENT
      )
    );*/
  }

  @Override
  public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
    _log("================== onSurfaceTextureAvailable");
//    _player.prepare("http://rdstest.pl/redbull_1.mp4", surface);
  }

  @Override
  public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

  }

  @Override
  public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
    return false;
  }

  @Override
  public void onSurfaceTextureUpdated(SurfaceTexture surface) {

  }

  private void _log(String message) {
    Log.d(TAG, message);
  }

  private void _logError(Exception e) {
    Log.e(TAG, e.toString());
  }

}
