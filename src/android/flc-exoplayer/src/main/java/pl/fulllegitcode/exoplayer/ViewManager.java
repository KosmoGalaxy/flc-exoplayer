package pl.fulllegitcode.exoplayer;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.SurfaceView;

public class ViewManager {

  public int glTexture() { return renderer().glTexture(); }

  private final GLSurfaceView _view;
  public GLSurfaceView view() { return _view; }

  private final ViewRenderer _renderer;
  private ViewRenderer renderer() { return _renderer; }

  ViewManager(Context context) {
    _view = new GLSurfaceView(context);
    _renderer = new ViewRenderer();
    _setupView();
  }

  private void _setupView() {
    view().setEGLContextClientVersion(2);
    view().setRenderer(renderer());
  }

}
