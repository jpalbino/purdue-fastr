/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class r_gnur_GNUR */

#ifndef _Included_r_gnur_GNUR
#define _Included_r_gnur_GNUR
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     r_gnur_GNUR
 * Method:    rnorm
 * Signature: (DD)D
 */
JNIEXPORT jdouble JNICALL Java_r_gnur_GNUR_rnorm__DD
  (JNIEnv *, jclass, jdouble, jdouble);

/*
 * Class:     r_gnur_GNUR
 * Method:    rnorm
 * Signature: ([DIDD)Z
 */
JNIEXPORT jboolean JNICALL Java_r_gnur_GNUR_rnorm___3DIDD
  (JNIEnv *, jclass, jdoubleArray, jint, jdouble, jdouble);

/*
 * Class:     r_gnur_GNUR
 * Method:    rnormNonChecking
 * Signature: ([DIDD)Z
 */
JNIEXPORT jboolean JNICALL Java_r_gnur_GNUR_rnormNonChecking
  (JNIEnv *, jclass, jdoubleArray, jint, jdouble, jdouble);

#ifdef __cplusplus
}
#endif
#endif