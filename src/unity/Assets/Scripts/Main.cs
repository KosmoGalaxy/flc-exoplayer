using FullLegitCode.ExoPlayer;
using System;
using UnityEngine;

public class Main : MonoBehaviour
{
    public Texture2D sourceTexture;

    bool _isStarted;
    bool _isPrepared;
    int _textureId;

    void Start()
    {
        _isStarted = true;
    }

    void OnRenderObject()
    {
        if (!_isStarted)
        {
            return;
        }
        FlcExoPlayer player = GetComponent<FlcExoPlayer>();
        if (!_isPrepared)
        {
            _textureId = player.Test_Init(sourceTexture.GetNativeTexturePtr().ToInt32());
            Texture2D texture = Texture2D.CreateExternalTexture(1024, 1024, TextureFormat.ARGB32, false, true, new IntPtr(_textureId));
            GetComponent<MeshRenderer>().material.mainTexture = texture;
            _isPrepared = true;
        }
        else
        {
            player.Test_Render();
        }
    }
}
