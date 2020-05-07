package com.colorator.ColoratorOptions;


import com.colorator.utils.FragmentWithFragments;

import org.json.JSONObject;


abstract class DetectorArgsAbstractClass extends FragmentWithFragments {
    abstract JSONObject getDetectorsArgs();
}
