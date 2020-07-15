package com.colorator.ColoratorImageProc;

import org.opencv.core.Mat;

class MaskDenoiser {
    private ColoratorMatManager mColoratorMatManager;
    private boolean hasNotRanYet = true;

    MaskDenoiser(ColoratorMatManager coloratorMatManager) {
        mColoratorMatManager = coloratorMatManager;
    }

    void denoise(Mat mask, Mat mFrameInProcess) {
//        allocationsOfFirstDenosieWrapper();
    }

    private void allocationsOfFirstDenosieWrapper() {
        if (hasNotRanYet) {
            allocationsOfFirstDetection();
        }
        hasNotRanYet = false;
    }

    private void allocationsOfFirstDetection() {
    }
}
