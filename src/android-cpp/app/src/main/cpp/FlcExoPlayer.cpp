#include "Unity/IUnityGraphics.h"
#include "RenderAPI.h"
#include "Log.h"
#include <stddef.h>
#include <assert.h>
#include <math.h>
#include <jni.h>

//region log

extern "C" void UNITY_INTERFACE_EXPORT SetLogFunc(LogFuncPtr fp) {
    Log::Init(fp);
}

//endregion

//region unity interface

static void UNITY_INTERFACE_API OnGraphicsDeviceEvent(UnityGfxDeviceEventType eventType);

static IUnityInterfaces* s_UnityInterfaces = NULL;
static IUnityGraphics* s_Graphics = NULL;

extern "C" void	UNITY_INTERFACE_EXPORT UNITY_INTERFACE_API UnityPluginLoad(IUnityInterfaces* unityInterfaces)
{
    s_UnityInterfaces = unityInterfaces;
    s_Graphics = s_UnityInterfaces->Get<IUnityGraphics>();
    s_Graphics->RegisterDeviceEventCallback(OnGraphicsDeviceEvent);

    // Run OnGraphicsDeviceEvent(initialize) manually on plugin load
    OnGraphicsDeviceEvent(kUnityGfxDeviceEventInitialize);
}

extern "C" void UNITY_INTERFACE_EXPORT UNITY_INTERFACE_API UnityPluginUnload()
{
    s_Graphics->UnregisterDeviceEventCallback(OnGraphicsDeviceEvent);
}

//endregion

//region render api

static RenderAPI* s_CurrentAPI = NULL;
static UnityGfxRenderer s_DeviceType = kUnityGfxRendererNull;

static void UNITY_INTERFACE_API OnGraphicsDeviceEvent(UnityGfxDeviceEventType eventType)
{
    // Create graphics API implementation upon initialization
    if (eventType == kUnityGfxDeviceEventInitialize)
    {
        assert(s_CurrentAPI == NULL);
        s_DeviceType = s_Graphics->GetRenderer();
        s_CurrentAPI = new RenderAPI();
    }

    // Let the implementation process the device related events
    if (s_CurrentAPI)
    {
        s_CurrentAPI->ProcessDeviceEvent(eventType, s_UnityInterfaces);
    }

    // Cleanup graphics API implementation upon shutdown
    if (eventType == kUnityGfxDeviceEventShutdown)
    {
        delete s_CurrentAPI;
        s_CurrentAPI = NULL;
        s_DeviceType = kUnityGfxRendererNull;
    }
}

//endregion

//region logic

static void* _sourceTextureHandle = NULL;
static void* _destinationTextureHandle = NULL;
static float _time = 0.0f;

extern "C" int UNITY_INTERFACE_EXPORT UNITY_INTERFACE_API GetExtTex() {
    return s_CurrentAPI->GetExtTex();
}

static void ModifyTexturePixels()
{
    void* textureHandle = _destinationTextureHandle;
    int width = 1024;
    int height = 1024;
    if (!textureHandle)
        return;

    int textureRowPitch;
    void* textureDataPtr = s_CurrentAPI->BeginModifyTexture(textureHandle, width, height, &textureRowPitch);
    if (!textureDataPtr)
        return;

    const float t = _time * 4.0f;

    unsigned char* dst = (unsigned char*)textureDataPtr;
    for (int y = 0; y < height; ++y)
    {
        unsigned char* ptr = dst;
        for (int x = 0; x < width; ++x)
        {
            // Simple "plasma effect": several combined sine waves
            int vv = int(
                    (127.0f + (127.0f * sinf(x / 7.0f + t))) +
                    (127.0f + (127.0f * sinf(y / 5.0f - t))) +
                    (127.0f + (127.0f * sinf((x + y) / 6.0f - t))) +
                    (127.0f + (127.0f * sinf(sqrtf(float(x*x + y*y)) / 4.0f - t)))
            ) / 4;

            // Write the texture pixel
            ptr[0] = vv;
            ptr[1] = vv;
            ptr[2] = vv;
            ptr[3] = vv;

            // To next pixel (our pixels are 4 bpp)
            ptr += 4;
        }

        // To next image row
        dst += textureRowPitch;
    }

    s_CurrentAPI->EndModifyTexture(textureHandle, width, height, textureRowPitch, textureDataPtr);
}

static void DrawColoredTriangle(GLuint textureHandle)
{
    // Draw a colored triangle. Note that colors will come out differently
    // in D3D and OpenGL, for example, since they expect color bytes
    // in different ordering.
    struct MyVertex
    {
        float x, y, z;
        unsigned int color;
    };
    MyVertex verts[3] =
            {
                    { -0.5f, -0.25f,  0, 0xFFff0000 },
                    { 0.5f, -0.25f,  0, 0xFF00ff00 },
                    { 0,     0.5f ,  0, 0xFF0000ff },
            };

    // Transformation matrix: rotate around Z axis based on time.
    float phi = 1.0f; // time set externally from Unity script
    float cosPhi = cosf(phi);
    float sinPhi = sinf(phi);
    float depth = 0.7f;
    float finalDepth = depth;
    float worldMatrix[16] = {
            cosPhi,-sinPhi,0,0,
            sinPhi,cosPhi,0,0,
            0,0,1,0,
            0,0,finalDepth,1,
    };

    s_CurrentAPI->DrawSimpleTriangles(worldMatrix, 1, verts, textureHandle);
}

//endregion

//region test_1

JNIEnv* jni_env = NULL;
JavaVM* JVM;

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM* vm, void* reserved) {
    JVM = vm;
    jint res = vm->GetEnv((void**)&jni_env, JNI_VERSION_1_6);
    assert(res == JNI_OK && "get env");
    return JNI_VERSION_1_6;
}

static bool isTestMade = false;
extern "C" void UNITY_INTERFACE_EXPORT UNITY_INTERFACE_API Test() {
    if (isTestMade) {
        return;
    }
    isTestMade = true;

    jclass c = jni_env->FindClass("pl/fulllegitcode/exoplayer_unity/PlayerUnity");
    assert(c != NULL && "find class");

    jmethodID m = jni_env->GetStaticMethodID(c, "create", "()V");
    assert(m != NULL && "get method");

    jni_env->CallStaticVoidMethod(c, m);
}

static void Test2() {
    jclass c = jni_env->FindClass("pl/fulllegitcode/exoplayer_unity/PlayerUnity");
    jmethodID m = jni_env->GetStaticMethodID(c, "getPlayerTextureId", "()I");
    int id = jni_env->CallStaticIntMethod(c, m);
    if (id != 0) {
        DrawColoredTriangle((GLuint) id);
    }
}

//region

//region render event

static void UNITY_INTERFACE_API OnRenderEvent(int eventID) {
    // Unknown / unsupported graphics device type? Do nothing
    if (s_CurrentAPI == NULL)
        return;

    Test();
    Test2();
//    ModifyTexturePixels();
//    DrawColoredTriangle();
//    ModifyVertexBuffer();
}

extern "C" UnityRenderingEvent UNITY_INTERFACE_EXPORT UNITY_INTERFACE_API GetRenderEventFunc() {
    return OnRenderEvent;
}

//endregion
