package pl.fulllegitcode.exoplayer_test;

import android.content.Context;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import gl.VideoTextureRenderer;
import pl.fulllegitcode.exoplayer.ExoManager;

public class MainActivity extends AppCompatActivity {

  static final String TAG = "FlcExoPlayerTest";

  public static MainActivity instance;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    instance = this;
//    _test1();
    _test2();
  }

  /*private SurfaceView _view;
  private ExoManager _exoManager;

  private void _test1() {
    final Context context = this;
    _view = new SurfaceView(this);
    _view.getHolder().addCallback(new SurfaceHolder.Callback() {
      @Override
      public void surfaceCreated(SurfaceHolder holder) {
        _exoManager = new ExoManager(context, holder.getSurface());
      }

      @Override
      public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

      }

      @Override
      public void surfaceDestroyed(SurfaceHolder holder) {

      }
    });
    addContentView(
      _view,
      new LinearLayout.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.MATCH_PARENT
      )
    );
  }*/

  private VideoTextureRenderer _renderer;
  private ExoManager _exoManager;

  private void _test2() {
    final Context context = this;
    _renderer = new VideoTextureRenderer(this, null, 1024, 1024, null);
    _renderer.videoTextureReadyCallback = new VideoTextureRenderer.VideoTextureReadyCallback() {
      @Override
      public void onReady() {
        _log("video texture ready: " + _renderer.getVideoTextureId());
        _exoManager = new ExoManager(context, new Surface(_renderer.getVideoTexture()));
      }
    };
  }

  private void _log(String message) {
    Log.d(TAG, "---------------------- " + message);
  }

  private void _logError(Exception e) {
    Log.e(TAG, e.toString());
  }

}
