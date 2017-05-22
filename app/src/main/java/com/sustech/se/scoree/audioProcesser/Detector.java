package com.sustech.se.scoree.audioProcesser;

import android.util.Log;
import com.sustech.se.scoree.fftpack.RealDoubleFFT;


/**
 * Created by David GAO on 2017/4/20.
 */

public class Detector implements DetectorInterface{
    private RealDoubleFFT fftTrans;
    private int blockSize;
    private double[] freq_vct;
    private int[] det = new int[3];//This is an int array to record recent average value for comparision.
    private int loop_cun = 0;
    private int counter = 0;//This counter is used to count the number of short[] got from listener.

    public Detector(int blockSize){
        this.blockSize = blockSize;
        fftTrans = new RealDoubleFFT(blockSize);
        freq_vct = new double[blockSize];
    }
    @Override
    public double[] detect(short[] audioData){
        int ret = audioData.length;
        if (ret > blockSize) return null;
        int ave = average(audioData);   //average data
        det[loop_cun % 3] = ave;
        loop_cun++;
        if(ave > 3 * det[(loop_cun + 1)%3]){// 至少比上上个信号强5倍
            if(counter >3){ //与上一个按键至少间隔3个采样周期
                for (int i = 0; i < blockSize && i < ret; i++) {
                    freq_vct[i] = (double) audioData[i] / Short.MAX_VALUE;
                }
                fftTrans.ft(freq_vct);
                return freq_vct;
            }
            counter = 0;
        }else counter++;
        return null;
    }

    private int average(short[] d) {
        long tmp = 0;
        for (int i = 0; i < d.length; i++) {
            if (d[i] > 0) {
                tmp += d[i];
            } else {
                tmp -= d[i];
            }
        }
        tmp /= d.length;
        return (int) tmp;
    }
}