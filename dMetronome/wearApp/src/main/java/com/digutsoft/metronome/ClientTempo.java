package com.digutsoft.metronome;

import android.app.Activity;
import android.app.Fragment;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.wearable.view.CircledImageView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class ClientTempo extends Fragament {

    View rootView;
    DMCMetronome metronome;
    TextView tvTempo;
    SeekBar sbTempo;
    NotificationCompat.Builder notificationBuilder;
    NotificationManagerCompat notificationManager;
    int mTempo;
    int offset;
    Context mContext;
    PowerManager.WakeLock wakeLock;
    // Client variable that will be set somewhere
    boolean isClient = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.settempo, container, false);

        tvTempo = (TextView) rootView.findViewById(R.id.tvTempo);
        
        Intent viewIntent = new Intent(getActivity(), DMAMain.class);
        PendingIntent viewPendingIntent = PendingIntent.getActivity(getActivity(), 0, viewIntent, 0);

        notificationBuilder = new NotificationCompat.Builder(getActivity())
                .setOngoing(true)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle(getString(R.string.app_name))
                .setContentIntent(viewPendingIntent);


        notificationManager = NotificationManagerCompat.from(getActivity());

        Vibrator vibrator = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
        metronome = new DMCMetronome(getActivity(), vibrator, rootView.findViewById(R.id.bilBackground));

        PowerManager powerManager = (PowerManager) mContext.getSystemService(Context.POWER_SERVICE);
        //wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, getString(R.string.app_name));

        // This is the function that should recieve the message about the tempo and offset
        // I'm not exactly sure how the message is recieved, but this https://developer.android.com/training/wearables/data-layer/messages.html
        // says we implement a MessageListener Interface.
        // One hacky way I thought of doing it is making the StartActivity path passed by the message
        // actually hold the info of the tempo and offset value we need.
        // You probably have a better idea but you are sleeping now :)
        getMessage();

        // start the metronome at the tempo and offset specified (offset will be implemented)
        metronome.startTick();
        // set the text to the tempo mark
        tvTempo.setText(Integer.toString(mTempo));

        return rootView;
    }

    public void getMessage() {
        setTempo(150);
        offset = 3;
    }

    public void onDestroy() {
        super.onDestroy();
        metronome.stopTick();
        if (wakeLock.isHeld()) wakeLock.release();
        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        notificationManager.cancel(1);
    }

    private void setTempo(int tempo) {
        //we changed tempo max to 240
        if (tempo < 0 || tempo > 240) return;
        tvTempo.setText(Integer.toString(tempo));
        mTempo = tempo;
    }
}