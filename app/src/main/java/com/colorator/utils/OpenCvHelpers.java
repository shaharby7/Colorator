package com.colorator.utils;

import static java.lang.StrictMath.cos;
import static java.lang.StrictMath.sin;

public class OpenCvHelpers {
    public static double[] rotatePoint(double[] point, double[] center, int angel) {
        double[] rotatedPoint = new double[2];
        rotatedPoint[0] = (int) (((point[0] - center[0]) * cos(angel))
                - ((point[1] - center[1]) * sin(angel))
                + point[1]);
        rotatedPoint[1] = (int) (((point[0] - center[0]) * sin(angel))
                + ((point[1] - center[1]) * cos(angel))
                + point[1]);
        return rotatedPoint;
    }
}
