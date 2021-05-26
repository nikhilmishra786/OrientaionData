package com.androidlikepro.orientationsdk

interface IOrientationCallback {
    fun getOrientationData(orientaionData: FloatArray): FloatArray
}