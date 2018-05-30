package pl.fulllegitcode.exoplayer;

public class Log {

  private static final String TAG = "FlcExoPlayer";

  public static void error(String message) {
    android.util.Log.e(TAG, message);
  }

  public static void info(String message) {
    android.util.Log.i(TAG, message);
  }

}
