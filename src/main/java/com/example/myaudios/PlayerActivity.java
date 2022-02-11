package com.example.myaudios;

import static com.example.myaudios.MainActivity.musicFiles;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.util.ArrayList;


public class PlayerActivity extends AppCompatActivity {

    TextView song_title, song_artist, current_duration, total_duration;
    ImageView album_art, shuffle, repeat, next, prev, back;
    FloatingActionButton play_pause;
    SeekBar seekBar;
    private static MediaPlayer mPlayer;
    static ArrayList<MusicFiles> listSong = new ArrayList<>();
    static ArrayList<Integer> arr;
    Uri uri;
    boolean repeatbtnOn = false;

//    MusicFiles mFile;
    String title, maxDuration, path, songTimer, songDuration, artistName;
    int position = -1;

    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        getSupportActionBar().hide();
        initView();
        mIntentCreate();

        btnFun();
        playpause();
        setSeekBar();

        mPlayer.setLooping(false);
//        onCompletion(mPlayer);



    }//OnCreate

    private void initView() {
        song_title = findViewById(R.id.song_title);
        song_artist = findViewById(R.id.song_artist);
        total_duration = findViewById(R.id.total_duration);
        current_duration = findViewById(R.id.duration_songPlayed);
        play_pause = findViewById(R.id.play);
        next = findViewById(R.id.next);
        back = findViewById(R.id.back_btn);
        prev = findViewById(R.id.prev);
        shuffle = findViewById(R.id.shuffle);
        repeat = findViewById(R.id.repeat);
        album_art = findViewById(R.id.album_art);
        seekBar = findViewById(R.id.seekBar);
    }

    public void mIntentCreate(){
        Intent intent = getIntent();
        listSong = musicFiles;
        position = intent.getIntExtra("Position", -1);
        title = listSong.get(position).getTitle();
        maxDuration = listSong.get(position).getDuration();
        path = intent.getStringExtra("path");
        artistName = listSong.get(position).getArtist();
        uri = Uri.fromFile(new File(path));
        setViews();

        // Playing audio and mediaPlayer Settings
        if (arr == null) {
            arr = new ArrayList<>();
            arr.add(23);
        }
        arr.add(position);

        int m = arr.get(arr.size() - 1);
        int n = arr.get(arr.size() - 2);
        for (int i =0 ; i < arr.size(); i++) {
            Log.e("Vlaue of list", String.valueOf(arr.get(i)));
        }
        if (m !=n ){
            if(mPlayer != null){
                if (mPlayer.isPlaying()) {
                    mPlayer.stop();
                }
                mPlayer.release();
                Log.e("Not NULL", "i was called");
            }
            mPlayer = MediaPlayer.create(getApplicationContext(), uri);
            mPlayer.start();
            OnCompletionSong();
        }
    }

    public void currentTimer(int mCurrentPosition){
        int Sec = (mCurrentPosition)%60;
        int Min = (mCurrentPosition/(60));
        songTimer = String.format("%d:%02d", Min, Sec);
        current_duration.setText(songTimer);
    }

    public void setViews(){
        song_title.setText(listSong.get(position).getTitle());
        song_artist.setText(listSong.get(position).getArtist());
        byte[] image = getAlbumArt(listSong.get(position).getPath());
        if (image != null){
            Glide.with(getApplicationContext()).asBitmap()
                    .load(image)
                    .into(album_art);
        }else {
            Glide.with(getApplicationContext())
                    .load(R.drawable.default_image)
                    .into(album_art);
        }
    }

    private byte[] getAlbumArt(String uri){
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri);
        byte[] art = retriever.getEmbeddedPicture();
        retriever.release();
        return art;
    }

    public void playpause(){

        play_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!(mPlayer.isPlaying())){
                    mPlayer.start();
                    play_pause.setImageResource(R.drawable.ic_pause);
                }else{

                    mPlayer.pause();
                    play_pause.setImageResource(R.drawable.ic_play);
                }
            }
        });


    }

    public void setSeekBar(){

        int mdura = (mPlayer.getDuration());
        int toSec = (mdura/1000)%60;
        int toMin = (mdura/(60*1000));
        songDuration = String.format("%d:%02d", toMin, toSec);
        total_duration.setText(songDuration);

        seekBar.setMax(mPlayer.getDuration()/1000);

         //Make sure you update Seekbar on UI thread
        // seekbar settings
        PlayerActivity.this.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                if(mPlayer != null){
                    int mCurrentPosition = mPlayer.getCurrentPosition() / 1000;
                    seekBar.setProgress(mCurrentPosition);
                    currentTimer(mCurrentPosition);
                }
                handler.postDelayed(this, 300);
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(mPlayer != null && fromUser){
                    mPlayer.seekTo(progress * 1000);
                }
            }
        });
    }

    public void nextSong(){
        if (position < (listSong.size() -1)) {
            if (mPlayer != null) {
                if (mPlayer.isPlaying()) {
                    mPlayer.stop();
                }
                mPlayer.release();
            }
            int p = position + 1;
            position = p;
            path = listSong.get(position).getPath();
            uri = Uri.fromFile(new File(path));
            mPlayer = MediaPlayer.create(getApplicationContext(), uri);
            mPlayer.start();
            setSeekBar();
            setViews();
        }
    }

    public void btnFun(){
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nextSong();
            }
        });
        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (position > 0) {
                    if (mPlayer != null) {
                        if (mPlayer.isPlaying()) {
                            mPlayer.stop();
                        }
                        mPlayer.release();
                    }
                    int p = position - 1;
                    position = p;
                    path = listSong.get(position).getPath();
                    uri = Uri.fromFile(new File(path));
                    mPlayer = MediaPlayer.create(getApplicationContext(), uri);
                    mPlayer.start();
                    setSeekBar();
                    setViews();
                }// if statement
            }
        });

        repeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!repeatbtnOn) {
                    repeat.setImageResource(R.drawable.ic_repeat_on);
                    repeatbtnOn = true;
                    Log.e("repeatBtnOn", "onClick: ON");
                    mPlayer.setLooping(true);
                }else{
                    repeat.setImageResource(R.drawable.ic_repeat_off);
                    repeatbtnOn = false;
                    Log.e("repeatBtnOn", "onClick: OFF");
                    mPlayer.setLooping(false);
                }
            }
        });

    }


    public void OnCompletionSong(){
        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                nextSong();
            }
        });
    }
}