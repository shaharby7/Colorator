package com.colorator.ColoratorImageProc.Detector;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.core.Core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class RangesDetector extends DetectorAbstractClass {
    public RangesDetector(Map detectorArgs) {
        super(detectorArgs);
    }

    private Mat getMaskOfRange(Mat inputImage, HashMap<String, Integer> rangeDescription) {

        Mat mask = new Mat();
        Core.inRange(inputImage,
                new Scalar(rangeDescription.get("minH"), rangeDescription.get("minS"), rangeDescription.get("minV")),
                new Scalar(rangeDescription.get("maxH"), rangeDescription.get("maxS"), rangeDescription.get("maxV")),
                mask);
        return mask;
    }

    @Override
    public Mat detect(Mat inputImage) {
        Mat output = new Mat(inputImage.height(), inputImage.width(), 0);
        ArrayList allRanges = (ArrayList) mDetectorArgs.get("Ranges");
        assert allRanges != null;
        for (Object range : allRanges) {
            HashMap typedRange = (HashMap) range;
            Core.bitwise_or(output, getMaskOfRange(inputImage, typedRange), output);
        }
        return output;
    }
}
