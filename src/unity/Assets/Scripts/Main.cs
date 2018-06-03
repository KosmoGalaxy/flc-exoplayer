using FullLegitCode.ExoPlayer;
using System;
using UnityEngine;

public class Main : MonoBehaviour
{
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
            //player.Prepare("http://rdstest.pl/redbull_1.mp4");
            _textureId = player.Test3();
            Debug.Log("----- " + _textureId);
            Texture2D texture = Texture2D.CreateExternalTexture(1024, 1024, TextureFormat.ARGB32, false, true, new IntPtr(_textureId));
            GetComponent<MeshRenderer>().material.mainTexture = texture;
            _isPrepared = true;
        }
        player.Test3_Update();
    }
}
