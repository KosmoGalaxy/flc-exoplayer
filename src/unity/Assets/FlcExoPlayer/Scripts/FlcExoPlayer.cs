using System;
using UnityEngine;

namespace FullLegitCode.ExoPlayer
{
    public class FlcExoPlayer : MonoBehaviour
    {
        const string TAG = "[FlcExoPlayer]";

        static void _Log(string message)
        {
            Debug.Log(TAG + " " + message);
        }

        static void _LogError(string message)
        {
            Debug.LogError(TAG + " " + message);
        }


        public Texture2D Texture { get; private set; }

        AndroidJavaObject _jo;

        void Awake()
        {
            try
            {
                _jo = new AndroidJavaObject("pl.fulllegitcode.exoplayer_unity.PlayerUnity");
                _Log("new Player created");
            }
            catch (Exception e)
            {
                _LogError(string.Format("creating new Player failed. (message)={0} (stack trace)={1}", e.Message, e.StackTrace));
            }
        }

        void OnRenderObject()
        {
            if (_jo != null)
            {
                //_jo.Call("update");
            }
        }

        public int Test()
        {
            return _jo.Call<int>("test");
        }

        public void Test2(int textureId)
        {
            _jo.Call("test2", new object[] { textureId });
        }

        public int Test3()
        {
            return _jo.Call<int>("test3");
        }

        public void Test3_Update()
        {
            _jo.Call("test3_update");
        }

        public void Prepare(String uri)
        {
            try
            {
                _Log(string.Format("prepare. (uri)={0}", uri));
                _CreateTexture();
                object[] args = { uri, Texture.GetNativeTexturePtr().ToInt32() };
                _jo.Call("prepare", args);
            }
            catch (Exception e)
            {
                _LogError(string.Format("prepare failed. (message)={0} (stack trace)={1}", e.Message, e.StackTrace));
            }
        }

        void _CreateTexture()
        {
            Texture2D texture = new Texture2D(1024, 1024, TextureFormat.ARGB32, false);
            Color32[] pixels = texture.GetPixels32();
            for (int i = 0; i < pixels.Length; i++)
            {
                //pixels[i] = new Color32(0xff, 0, 0, 0xff);
            }
            texture.SetPixels32(pixels);
            texture.Apply();
            Texture = texture;
        }
    }
}
