using System;
using System.Runtime.InteropServices;

namespace FullLegitCode.ExoPlayer
{
    internal class NativeBridge
    {
        private const string PLUGIN_NAME = "FlcExoPlayer-lib";

        public static void Init()
        {
            InitLog();
        }

        [DllImport(PLUGIN_NAME, CallingConvention = CallingConvention.Cdecl)]
        private static extern IntPtr GetRenderEventFunc();
        public static IntPtr Call_GetRenderEventFunc()
        {
#if UNITY_ANDROID && !UNITY_EDITOR
            return GetRenderEventFunc();
#endif
            return IntPtr.Zero;
        }

        [DllImport(PLUGIN_NAME, CallingConvention = CallingConvention.Cdecl)]
        private static extern void Test();
        public static void Call_Test()
        {
#if UNITY_ANDROID && !UNITY_EDITOR
            Test();
#endif
        }

        [DllImport(PLUGIN_NAME, CallingConvention = CallingConvention.Cdecl)]
        private static extern int GetExtTex();
        public static int Call_GetExtTex()
        {
#if UNITY_ANDROID && !UNITY_EDITOR
            return GetExtTex();
#endif
            return 0;
        }

        #region log

        [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
        public delegate void LogDelegate(string message);

        private static void LogCallback(string message)
        {
            Log.Info("[C++] " + message);
        }

        [DllImport(PLUGIN_NAME)]
        private static extern void SetLogFunc(IntPtr func);
        public static void Call_SetLogFunc(IntPtr func)
        {
#if UNITY_ANDROID && !UNITY_EDITOR
            SetLogFunc(func);
#endif
        }

        private static void InitLog()
        {
            LogDelegate d = new LogDelegate(LogCallback);
            IntPtr p = Marshal.GetFunctionPointerForDelegate(d);
            Call_SetLogFunc(p);
        }

        #endregion
    }
}
