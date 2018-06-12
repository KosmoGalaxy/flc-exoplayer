#ifndef ANDROID_CPP_PLANE_H
#define ANDROID_CPP_PLANE_H

#include <GLES2/gl2.h>
#include <string>

class Plane {
public:
    bool isError;
    std::string errorMessage;

    Plane();
    void Draw();

private:
    GLuint _vShaderId;
    GLuint _fShaderId;
    GLuint _programId;

    bool _CheckGlError(std::string action);
};


#endif //ANDROID_CPP_PLANE_H
