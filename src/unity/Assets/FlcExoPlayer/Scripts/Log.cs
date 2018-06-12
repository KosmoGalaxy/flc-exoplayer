using UnityEngine;

namespace FullLegitCode.ExoPlayer
{
    internal class Log
    {
        const string TAG = "[FlcExoPlayer]";

        public static void Error(string message)
        {
            Debug.LogError(TAG + " " + message);
        }

        public static void Info(string message)
        {
            Debug.Log(TAG + " " + message);
        }
    }
}
