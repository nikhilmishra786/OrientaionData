// IOrientationAidlInterface.aidl
package com.androidlikepro.orientationsdk;

import com.androidlikepro.orientationsdk.ICallback;

// Declare any non-default types here with import statements

interface IOrientationAidlInterface {

        float[] orientaion();
        void registerCallback(ICallback callback);
        void unregisterCallback(ICallback callback);
}