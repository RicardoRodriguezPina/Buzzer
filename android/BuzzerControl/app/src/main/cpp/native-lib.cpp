#include <jni.h>
#include <string>

extern "C"
jstring
Java_com_buzzer_strl_buzzercontrol_MainActivity_stringFromJNI(
        JNIEnv* env,
        jobject /* this */) {
    std::string hello = "Hello from NDK";
    return env->NewStringUTF(hello.c_str());
}
