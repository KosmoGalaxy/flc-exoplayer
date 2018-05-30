package pl.fulllegitcode.exoplayer;

import android.content.Context;

import com.google.android.exoplayer2.Player;

public class Util {

  public static String getPlaybackStateName(int state) {
    switch (state) {
      case Player.STATE_IDLE:
        return "idle";
      case Player.STATE_BUFFERING:
        return "buffering";
      case Player.STATE_READY:
        return "ready";
      case Player.STATE_ENDED:
        return "ended";
    }
    return null;
  }

  public static String getUserAgent(Context context) {
    return com.google.android.exoplayer2.util.Util.getUserAgent(
      context,
      "FlcExoPlayer"
    );
  }

}
