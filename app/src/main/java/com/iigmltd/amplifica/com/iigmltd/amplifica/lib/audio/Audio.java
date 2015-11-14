package com.iigmltd.amplifica.com.iigmltd.amplifica.lib.audio;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.media.audiofx.AcousticEchoCanceler;
import android.util.Log;

/**
 * Created by ilpinzeo on 14/11/2015.
 */
public class Audio {
    private boolean isRecording;

    private static String TAG = "Audio";

    private static int[] mSampleRates = new int[] { 8000, 11025, 22050, 44100 };

    public AudioManager audioManager;
    public AudioRecord audioRecord;
    public AudioTrack audioTrack;

    public Audio(Context context) {
        isRecording = false;
        int min = AudioRecord.getMinBufferSize(8000, AudioFormat.CHANNEL_IN_STEREO, AudioFormat.ENCODING_PCM_16BIT);
        audioRecord = new AudioRecord(MediaRecorder.AudioSource.VOICE_COMMUNICATION, 8000, AudioFormat.CHANNEL_IN_STEREO, AudioFormat.ENCODING_PCM_16BIT, min);
        if (AcousticEchoCanceler.isAvailable()) {
            AcousticEchoCanceler echoCancler = AcousticEchoCanceler.create(audioRecord.getAudioSessionId());
            echoCancler.setEnabled(true);
        }
        int maxJitter = AudioTrack.getMinBufferSize(8000, AudioFormat.CHANNEL_OUT_STEREO, AudioFormat.ENCODING_PCM_16BIT);
        audioTrack = new AudioTrack(AudioManager.MODE_IN_COMMUNICATION, 8000, AudioFormat.CHANNEL_OUT_STEREO, AudioFormat.ENCODING_PCM_16BIT, maxJitter, AudioTrack.MODE_STREAM);

        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        audioManager.setSpeakerphoneOn(true);
    }

    public void recordAndPlay() {
        short[] lin = new short[1024];
        int num     = 0;
        audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
        while(true) {
            if(isRecording) {
                num = audioRecord.read(lin, 0, 1024);
                audioTrack.write(lin, 0, num);
            }
        }
    }

    public void startRecordAndPlay() {
        audioRecord.startRecording();
        audioTrack.play();
        isRecording = true;
    }

    public void stopRecordAndPlay() {
        audioRecord.stop();
        audioTrack.pause();
        isRecording = false;
    }

    public void startThread() {
        (new Thread() {
            @Override
            public void run() {
                recordAndPlay();
            }
        }).start();
    }


    private AudioRecord findAudioRecord() {
        for (int rate : mSampleRates) {
            for (short audioFormat : new short[] { AudioFormat.ENCODING_PCM_8BIT, AudioFormat.ENCODING_PCM_16BIT }) {
                for (short channelConfig : new short[] { AudioFormat.CHANNEL_IN_MONO, AudioFormat.CHANNEL_IN_STEREO }) {
                    try {
                        Log.d(TAG, "Attempting rate " + rate + "Hz, bits: " + audioFormat + ", channel: "
                                + channelConfig);
                        int bufferSize = AudioRecord.getMinBufferSize(rate, channelConfig, audioFormat);

                        if (bufferSize != AudioRecord.ERROR_BAD_VALUE) {
                            // check if we can instantiate and have a success
                            AudioRecord recorder = new AudioRecord(MediaRecorder.AudioSource.DEFAULT, rate, channelConfig, audioFormat, bufferSize);

                            if (recorder.getState() == AudioRecord.STATE_INITIALIZED)
                                return recorder;
                        }
                    } catch (Exception e) {
                        Log.e(TAG, rate + "Exception, keep trying.",e);
                    }
                }
            }
        }
        return null;
    }
}



  /*  @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setVolumeControlStream(AudioManager.MODE_IN_COMMUNICATION);

        initRecordAndTrack();

        am = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        am.setSpeakerphoneOn(true);


        Button startButton = (Button) findViewById(R.id.start_button);
        startButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isRecording) {
                    startRecordAndPlay();
                }
            }
        });
        Button stopButton = (Button) findViewById(R.id.stop_button);
        stopButton.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (isRecording)
                {
                    stopRecordAndPlay();
                }
            }
        });
    }*/

