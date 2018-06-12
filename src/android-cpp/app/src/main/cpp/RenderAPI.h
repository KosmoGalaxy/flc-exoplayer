#ifndef ANDROID_CPP_RENDERAPI_H
#define ANDROID_CPP_RENDERAPI_H


#include "Unity/IUnityGraphics.h"
#include <GLES2/gl2.h>
#include <GLES2/gl2ext.h>

class RenderAPI {
public:
    RenderAPI();
    void ProcessDeviceEvent(UnityGfxDeviceEventType type, IUnityInterfaces* interfaces);
    void* BeginModifyTexture(void* textureHandle, int textureWidth, int textureHeight, int* outRowPitch);
    void EndModifyTexture(void* textureHandle, int textureWidth, int textureHeight, int rowPitch, void* dataPtr);
    void DrawSimpleTriangles(const float worldMatrix[16], int triangleCount, const void* verticesFloat3Byte4, GLuint textureHandle);

    GLuint GetExtTex();

private:
    UnityGfxRenderer m_APIType;
    GLuint m_VertexShader;
    GLuint m_FragmentShader;
    GLuint m_Program;
    GLuint m_VertexArray;
    GLuint m_VertexBuffer;
    int m_UniformWorldMatrix;
    int m_UniformProjMatrix;

    GLuint _uvBuffer;
    GLuint _extTex;

    void _CreateResources();
};


#endif //ANDROID_CPP_RENDERAPI_H
