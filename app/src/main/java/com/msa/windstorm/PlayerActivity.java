package com.msa.windstorm;

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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.msa.windstorm.MusicFiles;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Random;

import static com.msa.windstorm.MainActivity.musicFiles;
import static com.msa.windstorm.MainActivity.repeat;
import static com.msa.windstorm.MainActivity.shuffle;

public class PlayerActivity extends AppCompatActivity implements MediaPlayer.OnCompletionListener {

    TextView songName, artistName, durationPlayed, durationTotal;
    ImageView coverArt, buttonNext, buttonPrevious, buttonBack, buttonShuffle, buttonRepeat;
    FloatingActionButton playPauseButton;
    SeekBar seekBar;
    int position = 1 ;
    static ArrayList<MusicFiles> songs_list = new ArrayList<>();
    static Uri uri;
    static MediaPlayer mediaPlayer;
    public String msaLoco = null;
    private Handler handler = new Handler();
    private Thread playThread, prevThread, nextThread;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        msaLoco= getIntent().getStringExtra("nsame");
        initialization();

        if (msaLoco ==null){
            getIntentMethod();
        }else if (!msaLoco.equals(null)){
            getLocoMethod();
        }
        songName.setText(songs_list.get(position).getTitle());
        artistName.setText(songs_list.get(position).getArtist());
        mediaPlayer.setOnCompletionListener(this);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (mediaPlayer != null && fromUser){
                    mediaPlayer.seekTo(progress*1000);
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        PlayerActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null) {
                    int mCurrentPosition = mediaPlayer.getCurrentPosition()/1000;
                    seekBar.setProgress(mCurrentPosition);
                    durationPlayed.setText(formattedTime(mCurrentPosition));
                }
                handler.postDelayed(this, 1000);
            }
        });
        buttonShuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (shuffle) {
                    shuffle = false;
                    buttonShuffle.setImageResource(R.drawable.shuffle_off);
                }else{
                    shuffle = true;
                    buttonShuffle.setImageResource(R.drawable.shuffle_onn);
                }
            }
        });

        buttonRepeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (repeat) {
                    repeat = false;
                    buttonRepeat.setImageResource(R.drawable.repeat_off);
                }else{
                    repeat = true;
                    buttonRepeat.setImageResource(R.drawable.repeat_onn);
                }
            }
        });
    }

    private void getLocoMethod() {
        for (int i = 0 ; i <songs_list.size()-1 ; i++){
            if (msaLoco.equals(songs_list.get(i).getTitle())){
                position = i;
            }
        }
        songs_list = musicFiles;

        if (songs_list != null)
        {
            playPauseButton.setImageResource(R.drawable.pause);
            uri = Uri.parse(songs_list.get(position).getPath());
        }
        if (mediaPlayer != null)
        {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
            mediaPlayer.start();
        } else
        {
            mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
            mediaPlayer.start();
        }
        seekBar.setMax(mediaPlayer.getDuration()/1000);
        metaData(uri);
    }

    @Override
    protected void onResume() {
        playThreadBtn();
        nextThreadBtn();
        prevThreadBtn();
        super.onResume();
    }

    private void prevThreadBtn() {
        prevThread = new Thread()
        {
            @Override
            public void run() {
                super.run();
                buttonPrevious.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        prevButtonClicked();
                    }
                });
            }
        };
        prevThread.start();
    }

    private void prevButtonClicked() {
        if (mediaPlayer.isPlaying()){
            if (position == 0){
                position = songs_list.size() -1;
            }else {
                mediaPlayer.stop();
                mediaPlayer.release();
                position = (position - 1);
                uri = Uri.parse(songs_list.get(position).getPath());
                mediaPlayer = MediaPlayer.create(getApplicationContext(),uri);
                metaData(uri);
                songName.setText(songs_list.get(position).getTitle());
                artistName.setText(songs_list.get(position).getArtist());
                mediaPlayer.start();
                seekBar.setMax(mediaPlayer.getDuration()/1000);
                PlayerActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mediaPlayer != null) {
                            int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
                            seekBar.setProgress(mCurrentPosition);
                        }
                        handler.postDelayed(this, 1000);
                    }
                });
                mediaPlayer.setOnCompletionListener(this);
            }
        }else {
            if (position == 0){
                position = songs_list.size() -1;
            }else {
                position = (position - 1) % songs_list.size();
                uri = Uri.parse(songs_list.get(position).getPath());
                mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
                metaData(uri);
                songName.setText(songs_list.get(position).getTitle());
                artistName.setText(songs_list.get(position).getArtist());
                mediaPlayer.start();
                playPauseButton.setImageResource(R.drawable.pause);
                seekBar.setMax(mediaPlayer.getDuration() / 1000);
                PlayerActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mediaPlayer != null) {
                            int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
                            seekBar.setProgress(mCurrentPosition);
                        }
                        handler.postDelayed(this, 1000);
                    }
                });
                mediaPlayer.setOnCompletionListener(this);
            }
        }
    }

    private void nextThreadBtn() {
        nextThread = new Thread()
        {
            @Override
            public void run() {
                super.run();
                buttonNext.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        nextButtonClicked();
                    }
                });
            }
        };
        nextThread.start();
    }

    private void nextButtonClicked() {
        if (mediaPlayer.isPlaying()){
            mediaPlayer.stop();
            mediaPlayer.release();
            if (shuffle && !repeat){
                position = rendomize(songs_list.size() - 1);
            }else if(!shuffle && !repeat){
                position = (position + 1) % songs_list.size();
            }
            uri = Uri.parse(songs_list.get(position).getPath());
            mediaPlayer = MediaPlayer.create(getApplicationContext(),uri);
            metaData(uri);
            songName.setText(songs_list.get(position).getTitle());
            artistName.setText(songs_list.get(position).getArtist());
            seekBar.setMax(mediaPlayer.getDuration()/1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mediaPlayer != null) {
                        int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
                        seekBar.setProgress(mCurrentPosition);
                    }
                    handler.postDelayed(this, 1000);
                }
            });
            mediaPlayer.setOnCompletionListener(this);
            mediaPlayer.start();
        }else {
            if (shuffle && !repeat){
                position = rendomize(songs_list.size() - 1);
            }else if(!shuffle && !repeat){
                position = (position + 1) % songs_list.size();
            }
            uri = Uri.parse(songs_list.get(position).getPath());
            mediaPlayer = MediaPlayer.create(getApplicationContext(),uri);
            metaData(uri);
            songName.setText(songs_list.get(position).getTitle());
            artistName.setText(songs_list.get(position).getArtist());
            mediaPlayer.start();
            playPauseButton.setImageResource(R.drawable.pause);
            seekBar.setMax(mediaPlayer.getDuration()/1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mediaPlayer != null) {
                        int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
                        seekBar.setProgress(mCurrentPosition);
                    }
                    handler.postDelayed(this, 1000);
                }
            });
            mediaPlayer.setOnCompletionListener(this);
        }
    }

    private int rendomize(int i) {
        Random random = new Random();
        return  random.nextInt(i+1);
    }

    private void playThreadBtn() {
        playThread = new Thread()
        {
            @Override
            public void run() {
                super.run();
                playPauseButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        playPauseButtonClicked();
                    }
                });
            }
        };
        playThread.start();
    }

    private void playPauseButtonClicked() {
        if (mediaPlayer.isPlaying()){
            playPauseButton.setImageResource(R.drawable.play);
            mediaPlayer.pause();
            seekBar.setMax(mediaPlayer.getDuration()/1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mediaPlayer != null) {
                        int mCurrentPosition = mediaPlayer.getCurrentPosition()/1000;
                        seekBar.setProgress(mCurrentPosition);
                    }
                    handler.postDelayed(this, 1000);
                }
            });
        }else{
            playPauseButton.setImageResource(R.drawable.pause);
            mediaPlayer.start();
            seekBar.setMax(mediaPlayer.getDuration() / 1000);

            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mediaPlayer != null) {
                        int mCurrentPosition = mediaPlayer.getCurrentPosition()/1000;
                        seekBar.setProgress(mCurrentPosition);
                    }
                    handler.postDelayed(this, 1000);
                }
            });
        }
    }

    private String formattedTime(int mCurrentPosition) {
        String totalOut = "";
        String totalNew = "";
        String seconds = String.valueOf(mCurrentPosition % 60);
        String minutes = String.valueOf(mCurrentPosition / 60);
        totalOut = minutes+":"+seconds;
        totalNew = minutes+":"+"0"+seconds;
        if (seconds.length() == 1)
        {
            return totalNew;
        }else {
            return totalOut;
        }
    }

    private void getIntentMethod() {
        position = getIntent().getIntExtra("position", -1);
        //msaLoco = getIntent().getStringExtra("nsame");
        songs_list = musicFiles;

        if (songs_list != null)
        {
            playPauseButton.setImageResource(R.drawable.pause);
            uri = Uri.parse(songs_list.get(position).getPath());
        }
        if (mediaPlayer != null)
        {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
            mediaPlayer.start();
        } else
        {
            mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
            mediaPlayer.start();
        }
        seekBar.setMax(mediaPlayer.getDuration()/1000);
        metaData(uri);
    }

    private void initialization() {
        songName = findViewById(R.id.song_name);
        artistName = findViewById(R.id.artist);
        durationPlayed = findViewById(R.id.duration);
        durationTotal = findViewById(R.id.duration_total);
        coverArt = findViewById(R.id.cover_art);
        buttonNext = findViewById(R.id.next);
        buttonPrevious = findViewById(R.id.prev);
        buttonBack = findViewById(R.id.back_btn);
        buttonShuffle = findViewById(R.id.shuffle);
        buttonRepeat = findViewById(R.id.repeat);
        playPauseButton = findViewById(R.id.play_pause);
        seekBar = findViewById(R.id.seek_bar);
    }

    private void metaData(Uri uri){
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri.toString());
        String Dmsa = songs_list.get(position).getDuration();
        int msa = Integer.parseInt(Dmsa)/1000;
        durationTotal.setText(formattedTime(msa));
        byte[] art = retriever.getEmbeddedPicture();
        if (art != null){
            Glide.with(this).asBitmap()
                    .load(art)
                    .into(coverArt);
        }else{
            Glide.with(this).asBitmap()
                    .load(R.drawable.mscw)
                    .into(coverArt);
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        if (mediaPlayer != null){
            mediaPlayer = MediaPlayer.create(getApplicationContext(),uri);
            nextButtonClicked();
            mediaPlayer.start();
            mediaPlayer.setOnCompletionListener(this);
        }
    }
}