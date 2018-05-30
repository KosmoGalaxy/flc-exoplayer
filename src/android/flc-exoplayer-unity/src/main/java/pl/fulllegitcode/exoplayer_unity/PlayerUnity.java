package pl.fulllegitcode.exoplayer_unity;

import com.unity3d.player.UnityPlayer;

import pl.fulllegitcode.exoplayer.Player;

public class PlayerUnity extends Player {

  public PlayerUnity() {
    super(UnityPlayer.currentActivity);
  }

  public void prepareOnUiThread(final String uri, final int textureId) {
    final PlayerUnity thiz = this;
    UnityPlayer.currentActivity.runOnUiThread(new Runnable() {
      @Override
      public void run() {
//        thiz.prepare(uri, textureId);
      }
    });
  }

}
