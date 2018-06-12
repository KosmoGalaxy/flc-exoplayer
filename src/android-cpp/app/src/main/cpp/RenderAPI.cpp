#include "RenderAPI.h"
#include <assert.h>

//region local

enum VertexInputs
{
    kVertexInputPosition = 0,
    kVertexInputColor = 1
};


// Simple vertex shader source
/*#define VERTEX_SHADER_SRC(ver, attr, varying)						\
	ver																\
	attr " highp vec3 pos;\n"										\
	attr " lowp vec4 color;\n"										\
	"\n"															\
	varying " lowp vec4 ocolor;\n"									\
	"\n"															\
	"uniform highp mat4 worldMatrix;\n"								\
	"uniform highp mat4 projMatrix;\n"								\
    "attribute vec2 aTexCoord;\n"                                   \
	"\n"															\
	"void main()\n"													\
	"{\n"															\
	"	gl_Position = (projMatrix * worldMatrix) * vec4(pos,1);\n"	\
	"	ocolor = color;\n"											\
    "   vTexCoord = aTexCoord;\n"                                   \
	"}\n"															\*/
#define VERTEX_SHADER_SRC(ver, attr, varying)						\
	ver																\
	attr " highp vec3 pos;\n"										\
	"\n"															\
    "in vec2 aTexCoord;\n"                                  \
    "out vec2 vTexCoord;\n"                                 \
	"\n"															\
	"uniform highp mat4 worldMatrix;\n"								\
	"uniform highp mat4 projMatrix;\n"								\
	"\n"															\
	"void main()\n"													\
	"{\n"															\
	"	gl_Position = (projMatrix * worldMatrix) * vec4(pos,1);\n"	\
    "  vTexCoord = aTexCoord;\n"                                    \
	"}\n"															\

static const char* kGlesVProgTextGLES2 = VERTEX_SHADER_SRC("\n", "attribute", "varying");
static const char* kGlesVProgTextGLES3 = VERTEX_SHADER_SRC("#version 300 es\n", "in", "out");
#if SUPPORT_OPENGL_CORE
static const char* kGlesVProgTextGLCore = VERTEX_SHADER_SRC("#version 150\n", "in", "out");
#endif

#undef VERTEX_SHADER_SRC


// Simple fragment shader source
#define FRAGMENT_SHADER_SRC(ver, varying, outDecl, outVar)	\
	ver												\
	outDecl											\
	varying " lowp vec4 ocolor;\n"					\
	"\n"											\
	"void main()\n"									\
	"{\n"											\
	"	" outVar " = ocolor;\n"						\
	"}\n"											\

static const char* kGlesFShaderTextGLES2 = FRAGMENT_SHADER_SRC("\n", "varying", "\n", "gl_FragColor");
//static const char* kGlesFShaderTextGLES3 = FRAGMENT_SHADER_SRC("#version 300 es\n", "in", "out lowp vec4 fragColor;\n", "fragColor");
static const char* kGlesFShaderTextGLES3 =
    "#version 300 es\n"
    "#extension GL_OES_EGL_image_external : require\n"
    "in highp vec2 vTexCoord;\n"
    "out lowp vec4 fragColor;\n"
    "uniform samplerExternalOES uTex;\n"
    "void main() {\n"
    "  fragColor = texture(uTex, vTexCoord).rgba;\n"
    "}\n";
#if SUPPORT_OPENGL_CORE
static const char* kGlesFShaderTextGLCore = FRAGMENT_SHADER_SRC("#version 150\n", "in", "out lowp vec4 fragColor;\n", "fragColor");
#endif

#undef FRAGMENT_SHADER_SRC


static GLfloat textureCoords[] = {
    0.0f, 0.0f,
    0.0f, 1.0f,
    1.0f, 1.0f
};


static GLuint CreateShader(GLenum type, const char* sourceText, GLint* status)
{
    GLuint ret = glCreateShader(type);
    glShaderSource(ret, 1, &sourceText, NULL);
    glCompileShader(ret);
    glGetShaderiv(ret, GL_COMPILE_STATUS, status);
    return ret;
}

//endregion

RenderAPI::RenderAPI() {
    m_APIType = kUnityGfxRendererOpenGLES30;
}

void RenderAPI::ProcessDeviceEvent(UnityGfxDeviceEventType type, IUnityInterfaces* interfaces)
{
    if (type == kUnityGfxDeviceEventInitialize)
    {
        _CreateResources();
    }
    else if (type == kUnityGfxDeviceEventShutdown)
    {
        //@TODO: release resources
    }
}

void* RenderAPI::BeginModifyTexture(void* textureHandle, int textureWidth, int textureHeight, int* outRowPitch)
{
    const int rowPitch = textureWidth * 4;
    // Just allocate a system memory buffer here for simplicity
    unsigned char* data = new unsigned char[rowPitch * textureHeight];
    *outRowPitch = rowPitch;
    return data;
}


void RenderAPI::EndModifyTexture(void* textureHandle, int textureWidth, int textureHeight, int rowPitch, void* dataPtr)
{
    GLuint gltex = (GLuint)(size_t)(textureHandle);
    // Update texture data, and free the memory buffer
    glBindTexture(GL_TEXTURE_2D, gltex);
    glTexSubImage2D(GL_TEXTURE_2D, 0, 0, 0, textureWidth, textureHeight, GL_RGBA, GL_UNSIGNED_BYTE, dataPtr);
    delete[](unsigned char*)dataPtr;
}

void RenderAPI::DrawSimpleTriangles(const float *worldMatrix, int triangleCount,
                                    const void *verticesFloat3Byte4, GLuint textureHandle) {
    // Set basic render state
    glDisable(GL_CULL_FACE);
    glDisable(GL_BLEND);
    glDepthFunc(GL_LEQUAL);
    glEnable(GL_DEPTH_TEST);
    glDepthMask(GL_FALSE);

    // Tweak the projection matrix a bit to make it match what identity projection would do in D3D case.
    float projectionMatrix[16] = {
            1,0,0,0,
            0,1,0,0,
            0,0,2,0,
            0,0,-1,1,
    };

    // Setup shader program to use, and the matrices
    glUseProgram(m_Program);
    glUniformMatrix4fv(m_UniformWorldMatrix, 1, GL_FALSE, worldMatrix);
    glUniformMatrix4fv(m_UniformProjMatrix, 1, GL_FALSE, projectionMatrix);

    // Core profile needs VAOs, setup one
#	if SUPPORT_OPENGL_CORE
    if (m_APIType == kUnityGfxRendererOpenGLCore)
	{
		glGenVertexArrays(1, &m_VertexArray);
		glBindVertexArray(m_VertexArray);
	}
#	endif // if SUPPORT_OPENGL_CORE

    // Bind a vertex buffer, and update data in it
    const int kVertexSize = 12 + 4;
    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
    glBindBuffer(GL_ARRAY_BUFFER, m_VertexBuffer);
    glBufferSubData(GL_ARRAY_BUFFER, 0, kVertexSize * triangleCount * 3, verticesFloat3Byte4);

    // Setup vertex layout
    glEnableVertexAttribArray(kVertexInputPosition);
    glVertexAttribPointer(kVertexInputPosition, 3, GL_FLOAT, GL_FALSE, kVertexSize, (char*)NULL + 0);
//    glEnableVertexAttribArray(kVertexInputColor);
//    glVertexAttribPointer(kVertexInputColor, 4, GL_UNSIGNED_BYTE, GL_TRUE, kVertexSize, (char*)NULL + 12);

    // set texture coordinates
    glEnableVertexAttribArray(1);
    glBindBuffer(GL_ARRAY_BUFFER, _uvBuffer);
    glVertexAttribPointer(1, 2, GL_FLOAT, GL_FALSE, 2 * 4, (char*)NULL + 0);
    assert(glGetError() == GL_NO_ERROR && "uv buffer");

    // set texture
    GLuint texUniHandle = (GLuint) glGetUniformLocation(m_Program, "uTex");
    glActiveTexture(GL_TEXTURE0);
//    glBindTexture(GL_TEXTURE_EXTERNAL_OES, reinterpret_cast<GLuint>(textureHandle));
//    glBindTexture(GL_TEXTURE_EXTERNAL_OES, _extTex);
    glBindTexture(GL_TEXTURE_EXTERNAL_OES, textureHandle);
    glUniform1i(texUniHandle, 0);

    // Draw
    glDrawArrays(GL_TRIANGLES, 0, triangleCount * 3);

    // Cleanup VAO
#	if SUPPORT_OPENGL_CORE
    if (m_APIType == kUnityGfxRendererOpenGLCore)
	{
		glDeleteVertexArrays(1, &m_VertexArray);
	}
#	endif
}

void RenderAPI::_CreateResources() {
    // Create shaders
    GLint shaderStatus;
    if (m_APIType == kUnityGfxRendererOpenGLES20)
    {
        m_VertexShader = CreateShader(GL_VERTEX_SHADER, kGlesVProgTextGLES2, &shaderStatus);
        assert(shaderStatus != GL_FALSE && "GL_VERTEX_SHADER");
        m_FragmentShader = CreateShader(GL_FRAGMENT_SHADER, kGlesFShaderTextGLES2, &shaderStatus);
        assert(shaderStatus != GL_FALSE && "GL_FRAGMENT_SHADER");
    }
    else if (m_APIType == kUnityGfxRendererOpenGLES30)
    {
        m_VertexShader = CreateShader(GL_VERTEX_SHADER, kGlesVProgTextGLES3, &shaderStatus);
        assert(shaderStatus != GL_FALSE && "GL_VERTEX_SHADER");
        m_FragmentShader = CreateShader(GL_FRAGMENT_SHADER, kGlesFShaderTextGLES3, &shaderStatus);
        assert(shaderStatus != GL_FALSE && "GL_FRAGMENT_SHADER");
    }
#	if SUPPORT_OPENGL_CORE
    else if (m_APIType == kUnityGfxRendererOpenGLCore)
	{
		glewExperimental = GL_TRUE;
		glewInit();
		glGetError(); // Clean up error generated by glewInit

		m_VertexShader = CreateShader(GL_VERTEX_SHADER, kGlesVProgTextGLCore);
		m_FragmentShader = CreateShader(GL_FRAGMENT_SHADER, kGlesFShaderTextGLCore);
	}
#	endif // if SUPPORT_OPENGL_CORE


    // Link shaders into a program and find uniform locations
    m_Program = glCreateProgram();
    glBindAttribLocation(m_Program, kVertexInputPosition, "pos");
//    glBindAttribLocation(m_Program, kVertexInputColor, "color");
    glBindAttribLocation(m_Program, 1, "aTexCoord");
    glAttachShader(m_Program, m_VertexShader);
    glAttachShader(m_Program, m_FragmentShader);
#	if SUPPORT_OPENGL_CORE
    if (m_APIType == kUnityGfxRendererOpenGLCore)
		glBindFragDataLocation(m_Program, 0, "fragColor");
#	endif // if SUPPORT_OPENGL_CORE
    glLinkProgram(m_Program);

    GLint status = 0;
    glGetProgramiv(m_Program, GL_LINK_STATUS, &status);
    assert(status == GL_TRUE && "link program");

    m_UniformWorldMatrix = glGetUniformLocation(m_Program, "worldMatrix");
    m_UniformProjMatrix = glGetUniformLocation(m_Program, "projMatrix");

    // Create vertex buffer
    glGenBuffers(1, &m_VertexBuffer);
    glBindBuffer(GL_ARRAY_BUFFER, m_VertexBuffer);
    glBufferData(GL_ARRAY_BUFFER, 1024, NULL, GL_STREAM_DRAW);

    // texture coordinates
    glGenBuffers(1, &_uvBuffer);
    glBindBuffer(GL_ARRAY_BUFFER, _uvBuffer);
    glBufferData(GL_ARRAY_BUFFER, 6 * 4, textureCoords, GL_STATIC_DRAW);

    // external texture
    glActiveTexture(GL_TEXTURE0);
    glGenTextures(1, &_extTex);
    glBindTexture(GL_TEXTURE_EXTERNAL_OES, _extTex);

    assert(glGetError() == GL_NO_ERROR);
}

GLuint RenderAPI::GetExtTex() {
    return _extTex;
}
