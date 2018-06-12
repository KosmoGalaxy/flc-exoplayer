using System;
using System.Collections;
using UnityEngine;

namespace FullLegitCode.ExoPlayer
{
    public class FlcExoPlayer : MonoBehaviour
    {
        public Texture2D Texture { get; private set; }

        //AndroidJavaObject _jo;

        #region unity lifecycle

        private void Awake()
        {
            try
            {
                NativeBridge.Init();
                //_jo = new AndroidJavaObject("pl.fulllegitcode.exoplayer_unity.PlayerUnity");
            }
            catch (Exception e)
            {
                Log.Error(string.Format("creating new Player failed. (message)={0} (stack trace)={1}", e.Message, e.StackTrace));
            }
        }

        private IEnumerator Start()
        {
            yield return StartCoroutine("Render");
        }

        private IEnumerator Render()
        {
            while (true)
            {
                yield return new WaitForEndOfFrame();
                IntPtr callback = NativeBridge.Call_GetRenderEventFunc();
                if (callback != IntPtr.Zero)
                {
                    GL.IssuePluginEvent(callback, 1);
                }
            }
        }

        #endregion
    }
}
