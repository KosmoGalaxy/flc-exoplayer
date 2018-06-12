#include "Log.h"

LogFuncPtr Log::_logFunc = NULL;

void Log::Init(LogFuncPtr fp) {
    _logFunc = fp;
}

void Log::Info(std::string message) {
    if (_logFunc != NULL) {
        _logFunc(message.c_str());
    }
}
