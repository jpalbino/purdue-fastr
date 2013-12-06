/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class r_ext_SystemLibs */

#ifndef _Included_r_ext_SystemLibs
#define _Included_r_ext_SystemLibs
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     r_ext_SystemLibs
 * Method:    pow
 * Signature: (DD)D
 */
JNIEXPORT jdouble JNICALL Java_r_ext_SystemLibs_pow__DD
  (JNIEnv *, jclass, jdouble, jdouble);

/*
 * Class:     r_ext_SystemLibs
 * Method:    pow
 * Signature: ([D[D[DI)V
 */
JNIEXPORT void JNICALL Java_r_ext_SystemLibs_pow___3D_3D_3DI
  (JNIEnv *, jclass, jdoubleArray, jdoubleArray, jdoubleArray, jint);

/*
 * Class:     r_ext_SystemLibs
 * Method:    pow
 * Signature: ([DD[DI)V
 */
JNIEXPORT void JNICALL Java_r_ext_SystemLibs_pow___3DD_3DI
  (JNIEnv *, jclass, jdoubleArray, jdouble, jdoubleArray, jint);

/*
 * Class:     r_ext_SystemLibs
 * Method:    pow
 * Signature: (D[D[DI)V
 */
JNIEXPORT void JNICALL Java_r_ext_SystemLibs_pow__D_3D_3DI
  (JNIEnv *, jclass, jdouble, jdoubleArray, jdoubleArray, jint);

/*
 * Class:     r_ext_SystemLibs
 * Method:    fmod
 * Signature: ([D[D[DI)Z
 */
JNIEXPORT jboolean JNICALL Java_r_ext_SystemLibs_fmod
  (JNIEnv *, jclass, jdoubleArray, jdoubleArray, jdoubleArray, jint);

/*
 * Class:     r_ext_SystemLibs
 * Method:    exp
 * Signature: (D)D
 */
JNIEXPORT jdouble JNICALL Java_r_ext_SystemLibs_exp__D
  (JNIEnv *, jclass, jdouble);

/*
 * Class:     r_ext_SystemLibs
 * Method:    exp
 * Signature: ([D[DI)V
 */
JNIEXPORT void JNICALL Java_r_ext_SystemLibs_exp___3D_3DI
  (JNIEnv *, jclass, jdoubleArray, jdoubleArray, jint);

#ifdef __cplusplus
}
#endif
#endif
