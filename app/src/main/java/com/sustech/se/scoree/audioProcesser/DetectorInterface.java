package com.sustech.se.scoree.audioProcesser;

/**
 * Created by liaoweiduo on 21/05/2017.
 */

public interface DetectorInterface {
    double[] detect(short[] audioData);
}
