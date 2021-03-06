package com.example.accer.aphasia_app;
/*
* one bug found
*
* */
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.PowerManager;
import android.os.Vibrator;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class training extends AppCompatActivity implements View.OnClickListener{

    static int picAttemptArray[];
    ImageView img;
    ImageButton btntick;
    RelativeLayout r1,r3;
    ConstraintLayout r2;
    PowerManager pm;
    PowerManager.WakeLock wl;
    int position=-1;
    int interval=20,maxGivenTime=25,ctr;
    final int maxGivenTimeConstant=25;
    int threashold=0;
    static  MediaPlayer player;
    String pics[]=null;
    ArrayList<String>failedPicsArralist=null;
    ADB db;
    Handler handler=null;
    Runnable handlerRunnable=null;
    boolean doubleBackToExitPressedOnce=false;
    Meta meta;
    Calendar todayDate,lastDate;

    Button btnc1,btnc2,btnc3,btnc4;
    int cue1=0,cue2=0,cue3=0,cue4=0;
    ImageButton btnIns,btnHome;
    CountDownTimer timer=null,maxGivenCountDownTimer=null;
    String timeTaken;
    int type=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_training);
        r1 = (RelativeLayout) findViewById(R.id.relative_image);
        r2 = (ConstraintLayout) findViewById(R.id.relative_cue);
        r3 = (RelativeLayout) findViewById(R.id.relative_check);
        img = (ImageView) findViewById(R.id.img_training);
        btntick = (ImageButton) findViewById(R.id.btn_tick);
        btnc1 = (Button) findViewById(R.id.btn_cue1);
        btnc2 = (Button) findViewById(R.id.btn_cue2);
        btnc3 = (Button) findViewById(R.id.btn_cue3);
        btnc4 = (Button) findViewById(R.id.btn_cue4);
        btnHome=(ImageButton)findViewById(R.id.btn_home);
        btnIns=(ImageButton)findViewById(R.id.btn_instructions);
        meta=new Meta(this);
        todayDate=Calendar.getInstance();
        failedPicsArralist=new ArrayList<String>();
        db = new ADB(this);


        handler = new Handler();



        btntick.setOnClickListener(this);
        btnc1.setOnClickListener(this);
        btnc2.setOnClickListener(this);
        btnc3.setOnClickListener(this);
        btnc4.setOnClickListener(this);
        btnIns.setOnClickListener(this);
        btnHome.setOnClickListener(this);

        if(meta.read()!=null){
            meta=meta.read();
            position=-1;
            if(meta.getLastDate().get(Calendar.DATE)==todayDate.get(Calendar.DATE)
                    &&meta.getLastDate().get(Calendar.MONTH)==todayDate.get(Calendar.MONTH)
                    &&meta.getLastDate().get(Calendar.YEAR)==todayDate.get(Calendar.YEAR)) {
                position = meta.getTrainingPosition();
                if(meta.isFailedLooping()  /*meta.isDailyPicsOver()&&meta.getFailedPics().length>0*/) {
                    pics=meta.getFailedPics();

                } else {
                    pics = db.getDataPics("valid", "1",(meta.getDay()*meta.getNoOfQuestions())+","+meta.getNoOfQuestions());
                }
                if(!meta.isDailyPicsOver()&&meta.getFailedPics()!=null){
                    failedPicsArralist=new ArrayList<String>(Arrays.asList(meta.getFailedPics()));
                }
            }else
                pics = db.getDataPics("valid", "1",(meta.getDay()*meta.getNoOfQuestions())+","+meta.getNoOfQuestions());
            threashold=pics.length;

        }
       maxGivenTime=maxGivenTimeConstant-meta.getMaxGivenTimeElapsed();
        if(meta.isTodayTrainingOver()){
            finish();
        }
        if(meta.isFailedLooping())
            type=1;
        lastDate=meta.getLastDate();
        setImg();

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;


        ViewGroup.LayoutParams lp = r1.getLayoutParams();
        lp.width = (int) (width * .5);
        lp.height = (int) (height * .8);
        r1.setLayoutParams(lp);


        lp = r2.getLayoutParams();
        lp.width = (int) (width * .4);
        lp.height = (int) (height * .5);
        r2.setLayoutParams(lp);

        lp = r3.getLayoutParams();
        lp.width = (int) (width * .35);
        lp.height = (int) (height * .25);
        r3.setLayoutParams(lp);


        lp = btnc1.getLayoutParams();
        lp.width = (int) (width * .35 * .45);
        lp.height = (int) (height * .25 * .65);
        btnc1.setLayoutParams(lp);
        btnc1.setTextSize(TypedValue.COMPLEX_UNIT_PX, (float) (width * .35 * .085));

        lp = btnc2.getLayoutParams();
        lp.width = (int) (width * .35 * .45);
        lp.height = (int) (height * .25 * .65);
        btnc2.setLayoutParams(lp);
        btnc2.setTextSize(TypedValue.COMPLEX_UNIT_PX, (float) (width * .35 * .085));

        lp = btnc3.getLayoutParams();
        lp.width = (int) (width * .35 * .45);
        lp.height = (int) (height * .25 * .65);
        btnc3.setLayoutParams(lp);
        btnc3.setTextSize(TypedValue.COMPLEX_UNIT_PX, (float) (width * .35 * .085));

        lp = btnc4.getLayoutParams();
        lp.width = (int) (width * .35 * .45);
        lp.height = (int) (height * .25 * .65);
        btnc4.setLayoutParams(lp);
        btnc4.setTextSize(TypedValue.COMPLEX_UNIT_PX, (float) (width * .35 * .085));


        lp=btnHome.getLayoutParams();
        lp.width = (int) (width * .16 * .45);
        lp.height = (int) (height * .16 * .65);
        btnHome.setLayoutParams(lp);

        lp=btnIns.getLayoutParams();
        lp.width = (int) (width * .12 * .45);
        lp.height = (int) (height * .12 * .65);
        btnIns.setLayoutParams(lp);



        maxGivenCountDownTimer=new CountDownTimer(maxGivenTime*1000*60,1000) {
            @Override
            public void onTick(long l) {
                meta.setMaxGivenTimeElapsed((int) (maxGivenTimeConstant-(TimeUnit.MILLISECONDS.toMinutes(l))));
                if((l/1000)==60){
                    Snackbar.make(findViewById(R.id.trainingConstraint),"ಕೇವಲ 1 ನಿಮಿಷ ಉಳಿದಿದೆ",Snackbar.LENGTH_LONG).show();
                    Vibrator v = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
                    v.vibrate(1000);
                }
            }
            @Override
            public void onFinish() {
                if(wl!=null&&wl.isHeld())
                    wl.release();
                if(handler!=null) {
                    handler.removeCallbacks(handlerRunnable);
                    handler.removeCallbacksAndMessages(null);
                }
                meta.setTodayTrainingOver(true);
                meta.setLastDate(todayDate);
                meta.setDailyPicsOver(false);
                meta.setFailedPics(null);
                meta.setFailedLooping(false);
                meta.write();
                if(handler!=null) {
                    handler.removeCallbacks(handlerRunnable);
                    handler.removeCallbacksAndMessages(null);
                }
                Intent i=new Intent(getApplicationContext(),Instructions.class);
                i.putExtra("activity","training");
                startActivity(i);
            }
        }.start();

        pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "WakeLock");
        wl.acquire(interval * 1000);


    }

    public void startTimer(){
        btnc1.setEnabled(false);
        btnc2.setEnabled(false);
        btnc3.setEnabled(false);
        btnc4.setEnabled(false);
        ctr=1;
        handlerRunnable=new Runnable() {
            @Override
            public void run() {
                if(player!=null&&player.isPlaying()) {
                    handler.removeCallbacks(handlerRunnable);
                    handler.postDelayed(this, (interval*1000)+player.getDuration());

                    if(ctr==4)
                        btnc4.setEnabled(true);
                    return;
                }

                stopPlayer();
                player = MediaPlayer.create(getApplicationContext(), getResources().getIdentifier(img.getTag() + "_" + ctr, "raw", getPackageName()));
                player.start();
                switch (ctr){
                    case 1:
                        btnc1.setEnabled(true);
                        btnc2.setEnabled(false);
                        btnc3.setEnabled(false);
                        btnc4.setEnabled(false);
                        break;
                    case 2:
                        btnc1.setEnabled(false);
                        btnc2.setEnabled(true);
                        btnc3.setEnabled(false);
                        btnc4.setEnabled(false);
                        break;
                    case 3:
                        btnc1.setEnabled(false);
                        btnc2.setEnabled(false);
                        btnc3.setEnabled(true);
                        btnc4.setEnabled(false);
                        break;
                    case 4:
                        btnc1.setEnabled(false);
                        btnc2.setEnabled(false);
                        btnc3.setEnabled(false);
                        btnc4.setEnabled(true);
                        break;
                }
                player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        if(ctr<=3) {
                            btnc1.setEnabled(false);
                            btnc2.setEnabled(false);
                            btnc3.setEnabled(false);
                            btnc4.setEnabled(false);
                        }
                        switch (ctr-1){
                            case 1:
                                cue1=1;
                                cue2=0;
                                cue3=0;
                                cue4=0;
                                break;

                            case 2:
                                cue1=0;
                                cue2=1;
                                cue3=0;
                                cue4=0;
                                break;

                            case 3:
                                cue1=0;
                                cue2=0;
                                cue3=1;
                                cue4=0;
                                break;

                            case 4:
                                cue1=0;
                                cue2=0;
                                cue3=0;
                                cue4=1;
                                break;
                        }
                        db.addTransaction(type,picAttemptArray[1]+1,picAttemptArray[0],cue1,cue2,cue3,cue4,timeTaken);
                    }
                });
                ctr++;
                if (ctr <4) {
                    handler.postDelayed(this, (interval * 1000) + player.getDuration());
                }
                else if(ctr==4) {
                    handler.postDelayed(this, (1000 * ((interval/2)+interval))+player.getDuration());//after 3rd cue gets over
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            btnc1.setEnabled(true);
                            btnc2.setEnabled(true);
                            btnc3.setEnabled(true);
                        }
                    },player.getDuration()-player.getCurrentPosition());

                }
                else if(position<=threashold-1) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if(player!=null&&player.isPlaying()){
                                new Handler().postDelayed(this,6000);

                                  /*stores failed attempts for next iteration(in secondary storage)*/
                                if(failedPicsArralist!=null&&!failedPicsArralist.contains(pics[position])) {
                                    failedPicsArralist.add(pics[position]);
                                    meta.setFailedPics(failedPicsArralist.toArray(new String[failedPicsArralist.size()]));
                                    meta.write();
                                }

                                btntick.setEnabled(false);
                                return;
                            }
                            btntick.setEnabled(true);
                            setImg();
                        }
                    },2000);
                }
            }
        };
        handler.postDelayed(handlerRunnable,interval*1000);
    }
    @Override
    protected void onDestroy() {
        if(wl.isHeld())
            wl.release();
        if(handler!=null)
            handler.removeCallbacks(handlerRunnable);
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        int id=view.getId();
        switch (id){
            case R.id.btn_tick :
                if(failedPicsArralist!=null&&failedPicsArralist.contains(pics[position])) {
                    failedPicsArralist.remove(pics[position]);
                    ArrayList<String> temp=new ArrayList<String>(Arrays.asList(pics));
                    if(temp.contains(pics[position])){
                        temp.remove(pics[position]);
                        pics=new String[temp.size()];
                        pics=temp.toArray(pics);
                    }
                    meta.setFailedPics(failedPicsArralist.toArray(new String[failedPicsArralist.size()]));
                    meta.write();
                }
                db.addTransaction(type,picAttemptArray[1]+1,picAttemptArray[0],0,0,0,0,timeTaken);
                stopPlayer();
                player=MediaPlayer.create(this,R.raw.tick_sound);
                player.start();
                setImg();
                if(meta.isFailedLooping()&&threashold<=position) {
                    cleanUp();
                }
                break;
            case R.id.btn_cue1 :
                db.addTransaction(type,picAttemptArray[1]+1,picAttemptArray[0],1,0,0,0,timeTaken);
                stopPlayer();
                player = MediaPlayer.create(getApplicationContext(),getResources().getIdentifier(img.getTag()+"_1","raw",getPackageName()));
                player.start();
                break;

            case R.id.btn_cue2 :
                db.addTransaction(type,picAttemptArray[1]+1,picAttemptArray[0],0,1,0,0,timeTaken);
                stopPlayer();
                player = MediaPlayer.create(getApplicationContext(),getResources().getIdentifier(img.getTag()+"_2","raw",getPackageName()));
                player.start();

                break;
            case R.id.btn_cue3 :
                db.addTransaction(type,picAttemptArray[1]+1,picAttemptArray[0],0,0,1,0,timeTaken);
                player = MediaPlayer.create(getApplicationContext(),getResources().getIdentifier(img.getTag()+"_3","raw",getPackageName()));
                player.start();
                break;
            case R.id.btn_cue4 :
                db.addTransaction(type,picAttemptArray[1]+1,picAttemptArray[0],0,0,0,1,timeTaken);
                stopPlayer();
                player = MediaPlayer.create(getApplicationContext(),getResources().getIdentifier(img.getTag()+"_4","raw",getPackageName()));
                player.start();
                break;

            case R.id.btn_home:
                finish();
                startActivity(new Intent(this,Home.class));
                break;
            case R.id.btn_instructions:
                finish();
                Intent in=new Intent(getApplicationContext(),Instructions.class);
                in.putExtra("activity","training");
                startActivity(in);
                break;
        }
    }



    void setImg(){

        if(timer!=null)
            timer.cancel();
        timer=new CountDownTimer(maxGivenTime*1000*60,1000) {
            @Override
            public void onTick(long l) {
                long mins=(maxGivenTime*60-l/1000)/60;
                long secs=(maxGivenTime*60-l/1000)%60;
                timeTaken="";;

                timeTaken+=mins+":";
                if(secs<=9)
                    timeTaken+="0"+secs;
                else
                    timeTaken+=""+secs;


            }
            @Override
            public void onFinish() {

            }
        };

        timer.start();



        position++;
        if(position<threashold&&position<pics.length&&position>-1) {
            picAttemptArray=db.getLastAttemptFromTransaction(pics[position],type);
            r1.startAnimation(AnimationUtils.loadAnimation (getApplicationContext(),R.anim.fromright));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                img.setImageDrawable(getDrawable(getResources().getIdentifier(pics[position], "drawable", getPackageName())));
            } else {
                img.setImageDrawable(getResources().getDrawable(getResources().getIdentifier(pics[position], "drawable", getPackageName())));
            }
            img.setTag(pics[position]);
            if(handler!=null) {
                handler.removeCallbacks(handlerRunnable);
                handler.removeCallbacksAndMessages(null);
            }
            startTimer();
        }else {
            if(handler!=null) {
                handler.removeCallbacks(handlerRunnable);
                handler.removeCallbacksAndMessages(null);
            }
            if(meta.read()!=null){
                meta=meta.read();
                position=-1;
                pics=new String[failedPicsArralist.size()];
                if(failedPicsArralist.size()<=0) {
                    cleanUp();
                    return;
                }
                pics=failedPicsArralist.toArray(pics);
                failedPicsArralist=new ArrayList<>();
                meta.setFailedLooping(true);
                type=1;
                meta.write();
                threashold =pics.length;
                setImg();
            }
        }
    }
    void stopPlayer(){
        if(player!=null){
            player.stop();
            player.release();
            player=null;
        }
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            stopPlayer();
            meta.backup();
            if(handler!=null) {
                handler.removeCallbacks(handlerRunnable);
                handler.removeCallbacksAndMessages(null);
            }
            finish();
            Intent intent = new Intent(this,Home.class);
            startActivity(intent);
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);

    }


    @Override
    protected void onStop() {
        super.onStop();
        meta.backup();
        if(wl.isHeld())
            wl.release();
        if(handler!=null) {
            handler.removeCallbacks(handlerRunnable);
            handler.removeCallbacksAndMessages(null);
        }

        if(maxGivenCountDownTimer!=null)
            maxGivenCountDownTimer.cancel();
        if(timer!=null)
            timer.cancel();
        todayDate=Calendar.getInstance();/*
        int last=lastDate.get(Calendar.DATE);
        int tod=todayDate.get(Calendar.DATE);*/

        stopPlayer();
        meta.setTrainingPosition(position-1);
        meta.setLastDate(todayDate);
        meta.write();
        startService(new Intent(this,SendData.class));
    }

    void cleanUp(){
        if(wl!=null&&wl.isHeld())
            wl.release();
        if(handler!=null) {
            handler.removeCallbacks(handlerRunnable);
            handler.removeCallbacksAndMessages(null);
        }
        meta.setTodayTrainingOver(true);
        meta.setLastDate(todayDate);
        meta.setDailyPicsOver(false);
        meta.setFailedPics(null);
        meta.setFailedLooping(false);
        meta.write();
        finish();
        Intent i=new Intent(getApplicationContext(),Instructions.class);
        i.putExtra("activity","training");
        startActivity(i);
    }
}
