#include "Plane.h"
#include <GLES2/gl2.h>

const char* vShaderStr =
    "attribute vec4 vPosition;"
    "void main() {"
    "  gl_Position = vPosition;"
    "}";

const char* fShaderStr =
    "precision mediump float;"
    "uniform vec4 vColor;"
    "void main() {"
    "  gl_FragColor = vColor;"
    "}";

GLfloat vertices[] = {
    -1.0f,  1.0f, 0.0f,
    -1.0f, -1.0f, 0.0f,
     1.0f, -1.0f, 0.0f
};

GLfloat color[] = { 1.0f, 1.0f, 1.0f, 1.0f };

GLuint LoadShader(GLenum type, const char* shaderSrc) {
    GLuint shader;
    GLint compiled;

    // Create the shader object
    shader = glCreateShader(type);
    if(shader == 0)
        return 0;
    // Load the shader source
    glShaderSource(shader, 1, &shaderSrc, NULL);

    // Compile the shader
    glCompileShader(shader);
    // Check the compile status
    glGetShaderiv(shader, GL_COMPILE_STATUS, &compiled);
    if(!compiled)
    {
        glDeleteShader(shader);
        return 0;
    }
    return shader;
}

Plane::Plane() {
    _vShaderId = LoadShader(GL_VERTEX_SHADER, vShaderStr);
    if (_vShaderId == 0) {
        isError = true;
        errorMessage = "vertex shader error";
        return;
    }

    _fShaderId = LoadShader(GL_FRAGMENT_SHADER, fShaderStr);
    if (_fShaderId == 0) {
        isError = true;
        errorMessage = "fragment shader error";
        return;
    }

    _programId = glCreateProgram();
    glAttachShader(_programId, _vShaderId);
    glAttachShader(_programId, _fShaderId);
    glLinkProgram(_programId);
    if (_CheckGlError("program setup")) return;

    GLint isLinked;
    glGetProgramiv(_programId, GL_LINK_STATUS, &isLinked);
    if (!isLinked) {
        isError = true;
        errorMessage = "program linking error";
    }
}

void Plane::Draw() {
    glUseProgram(_programId);
    if (_CheckGlError("use program")) return;

    GLuint positionHandle = (GLuint) glGetAttribLocation(_programId, "vPosition");
    if (_CheckGlError("get attribute location")) return;

    glEnableVertexAttribArray(positionHandle);
    if (_CheckGlError("enable vertex attribute array")) return;

    glVertexAttribPointer(positionHandle, 3, GL_FLOAT, GL_FALSE, 3 * 4, vertices);
    if (_CheckGlError("set vertex attribute pointer")) return;

    GLint colorHandle = glGetUniformLocation(_programId, "vColor");
    if (_CheckGlError("get uniform location")) return;

    glUniform4fv(colorHandle, 1, color);
    if (_CheckGlError("set uniform")) return;

    glDrawArrays(GL_TRIANGLES, 0, 3);
    if (_CheckGlError("draw arrays")) return;

    glDisableVertexAttribArray(positionHandle);
    if (_CheckGlError("disable vertex attribute array")) return;
}

bool Plane::_CheckGlError(std::string action) {
    GLenum error = glGetError();
    if (error != GL_NO_ERROR) {
        isError = true;
        errorMessage = action + " error";
        return true;
    }
    return false;
}
