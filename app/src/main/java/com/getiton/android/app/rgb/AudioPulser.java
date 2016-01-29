package com.getiton.android.app.rgb;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.support.annotation.NonNull;


import java.util.ArrayList;

import com.christophesmet.getiton.library.logging.ILoggingService;
import com.christophesmet.getiton.library.modules.RGBV1Module;

/**
 * Created by christophesmet on 14/11/15.
 */

public class AudioPulser {
    @NonNull
    private RGBV1Module mRGBV1Module;
    @NonNull
    private ILoggingService mLoggingService;

    private boolean mIsRunning = false;
    private byte[] buffer;


    public AudioPulser(@NonNull ILoggingService loggingService, @NonNull RGBV1Module RGBV1Module) {
        this.mLoggingService = loggingService;
        this.mRGBV1Module = RGBV1Module;
        buffer = new byte[AudioRecord.getMinBufferSize(8000, AudioFormat.CHANNEL_CONFIGURATION_MONO, AudioFormat.ENCODING_PCM_8BIT)];
    }

    public void start() {
        mIsRunning = true;
        createThreadedListener();
    }

    private void createThreadedListener() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final AudioRecord ar = new AudioRecord(MediaRecorder.AudioSource.MIC, 8000, AudioFormat.CHANNEL_CONFIGURATION_MONO, AudioFormat.ENCODING_PCM_8BIT, buffer.length);
                if (ar.getState() != AudioRecord.STATE_INITIALIZED) {
                    mLoggingService.log("Unable to initialise AudioRecord, bailing.");
                    return;
                }
                ar.startRecording();
                while (mIsRunning) {
                    ar.read(buffer, 0, buffer.length);
                }
                mLoggingService.log("Done with the micro recording, releasing it.");
                ar.stop();
                ar.release();
            }
        }).start();
    }

    private int[] findRGBValuesForAudioBuffer(byte[] buffer) {
        return new int[0];
    }

    /**
     * Finds the peaks of the buffer
     *
     * @param buffer
     * @return the position of the peaks in the buffer
     */
    //Todo: lookup a faster approach for this
    //MIT online courseware
    private Integer[] findPeaks(byte[] buffer) {

        ArrayList<Integer> output = new ArrayList<>(3);

        //triple peak buffer
        byte left;
        byte middle;
        byte right;

        for (int i = 0; i < buffer.length; i++) {

            //Fill in the peak buffers
            left = i >= 0 ? buffer[i] : 0;
            middle = buffer[i]; //this shouldn't go wrong
            right = i <= buffer.length - 1 ? 0 : buffer[i];

            if (left < middle && right < middle) {
                //Peak
                output.add(i);
            }
        }
        return output.toArray(new Integer[output.size()]);
    }

    public void stop() {
        mIsRunning = false;
    }
}