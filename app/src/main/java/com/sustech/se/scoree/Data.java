package com.sustech.se.scoree;

import android.app.Application;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by liaoweiduo on 08/04/2017.
 */

public class Data extends Application {
    private Queue<byte[]> dataQueue;

    private Queue<byte[]> getDataQueue() {
        return dataQueue;
    }
    public void offer(byte[] data){
        getDataQueue().offer(data);
    }
    public byte[] poll(){
        return getDataQueue().poll();
    }
    @Override
    public void onCreate() {
        super.onCreate();
        dataQueue=new LinkedList<>();
    }
}
