package pl.fulllegitcode.exoplayer_test;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLSurfaceView;

public class MyView extends GLSurfaceView {

  private MyRenderer _renderer;

  public MyView(Context context) {
    super(context);
    setEGLContextClientVersion(2);
    Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.test);
    _renderer = new MyRenderer(bitmap);
    setRenderer(_renderer);
  }

}
