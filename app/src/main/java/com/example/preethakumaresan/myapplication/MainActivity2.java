package com.example.preethakumaresan.myapplication;


import android.content.Context;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.widget.Button;

/**
 * Created by PREETHA KUMARESAN on 20-06-2016.
 */
public class MainActivity2 extends AsyncTask<Integer, Void, Integer> implements MediaPlayer.OnCompletionListener {

    MediaPlayer mp;
    Context context;
    Button b1,b2;


    public MainActivity2(Context ctx,Button play,Button stop) {
        this.context = ctx;
        this.b1=play;
        this.b2=stop;
    }



    @Override
    public Integer doInBackground(Integer... params) {
        int paatu = params[0];


        switch (paatu) {
            case 0:
                mp = MediaPlayer.create(context, R.raw.escapee);
                break;
            case 1:
                mp = MediaPlayer.create(context, R.raw.pokerface);
                break;
            case 2:
                mp = MediaPlayer.create(context, R.raw.tangled);
                break;


        }
        mp.start();
        mp.setOnCompletionListener(this);
        double startTime = mp.getCurrentPosition();
        double finalTime = mp.getDuration();
        while (startTime < finalTime && mp.isPlaying()&& !isCancelled()) {
            startTime = mp.getCurrentPosition();
            if (isCancelled()) {
                startTime = mp.getDuration();
            }
        }
        return null;
    }




    @Override
    protected void onPostExecute(Integer pos) {
        super.onPostExecute(pos);

    }

    @Override
    public void onCompletion(MediaPlayer mp) {

    }


    @Override
    protected void onCancelled() {
        super.onCancelled();
        b1.setEnabled(true);
        b2.setEnabled(false);


    }
}

