package pl.fulllegitcode.exoplayer_unity;

import android.content.Context;

import com.unity3d.player.UnityPlayer;

import pl.fulllegitcode.exoplayer.Player;

public class PlayerUnity extends Player {

  private static PlayerUnity _player;

  public static void create() {
    _player = new PlayerUnity();
  }

  public static int getPlayerTextureId() {
    return _player != null ? _player.getTextureId() : 0;
  }


  @Override
  protected Context _getContext() { return UnityPlayer.currentActivity; }

}
