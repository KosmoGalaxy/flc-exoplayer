using FullLegitCode.ExoPlayer;
using System;
using UnityEngine;

public class Main : MonoBehaviour
{
    /*private void Update()
    {
        GetComponent<FlcExoPlayer>().Test(_texture);
    }*/

    /*private void Update()
    {
        if (!_texture)
        {
            int textureId = GetComponent<FlcExoPlayer>().Test();
            if (textureId != 0)
            {
                _texture = Texture2D.CreateExternalTexture(1024, 1024, TextureFormat.ARGB32, false, true, new IntPtr(textureId));
                GetComponent<MeshRenderer>().material.mainTexture = _texture;
            }
        }
    }*/

    /*void OnRenderObject()
    {
        if (!_isStarted)
        {
            return;
        }
        FlcExoPlayer player = GetComponent<FlcExoPlayer>();
        if (!_isPrepared && Time.time > 1f)
        {
            _textureId = player.Test_Init(sourceTexture.GetNativeTexturePtr().ToInt32());
            Texture2D texture = Texture2D.CreateExternalTexture(1024, 1024, TextureFormat.ARGB32, false, true, new IntPtr(_textureId));
            GetComponent<MeshRenderer>().material.mainTexture = texture;
            _isPrepared = true;
        }
        if (_isPrepared)
        {
            player.Test_Render();
        }
    }*/
}
