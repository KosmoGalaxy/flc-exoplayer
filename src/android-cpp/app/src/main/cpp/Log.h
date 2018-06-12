#ifndef ANDROID_CPP_LOG_H
#define ANDROID_CPP_LOG_H


#include <string>

typedef void (*LogFuncPtr)(const char*);

class Log {
public:
    static void Init(LogFuncPtr fp);
    static void Info(std::string message);

private:
    static LogFuncPtr _logFunc;
};


#endif //ANDROID_CPP_LOG_H
