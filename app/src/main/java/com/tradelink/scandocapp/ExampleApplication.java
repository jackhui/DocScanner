package com.tradelink.scandocapp;

import android.app.Application;

import net.doo.snap.ScanbotSDKInitializer;

/**
 * {@link ScanbotSDKInitializer} should be called
 * in {@code Application.onCreate()} method for RoboGuice modules initialization
 */
public class ExampleApplication extends Application {

    private final String licenseKey =
            "OxyXABzQdn9MUzx78QoCL4uyfAXZlW" +
                    "NK2WXZGN+UAyjcKoha7itm+rwwaw2r" +
                    "RfcgNfYZWQ8c9GGQoiWUOOJY9eg7vL" +
                    "/Sx8VzvSDu82Ot8bPOFB5/647UDFdP" +
                    "pfwxrexEHlc18Ik/pR9M/pTHDY8WBQ" +
                    "qaZrVFQ0ZyluXLNYesUz6tSrtubS9P" +
                    "FGiozOF8Seu8/r1yEmqEb06AErD4yF" +
                    "VckXZ9I++Yvuku1S0LaIghu2sqYWVz" +
                    "95y5hAvJcJrA33oc1GSq6aYXn/CncT" +
                    "xeR3w8mhC78OZRz/FUV3W7r4v8gviL" +
                    "oiBDMosIgp62EVJrHUFrz+ukZ6+YZk" +
                    "ZDXCCWVd/YZA==\nU2NhbmJvdFNESw" +
                    "pjb20udHJhZGVsaW5rLnNjYW5kb2Nh" +
                    "cHAKMTUyNzQ2NTU5OQo1OTAKMw==\n";

    @Override
    public void onCreate() {
        new ScanbotSDKInitializer()
                .license(this, licenseKey)
                .initialize(this);
        super.onCreate();
    }
}
