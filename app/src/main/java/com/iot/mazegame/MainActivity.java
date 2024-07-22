package com.iot.mazegame;

import android.animation.AnimatorInflater;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private MediaPlayer mediaPlayer; // 배경 음악을 위한 MediaPlayer
    private MediaPlayer buttonSoundPlayer; // 버튼 클릭 효과음을 위한 MediaPlayer

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button startGameButton = findViewById(R.id.startGameButton);

        // 버튼 클릭 애니메이터 설정
        startGameButton.setStateListAnimator(AnimatorInflater.loadStateListAnimator(this, R.animator.button_state_animator));

        try {
            // 배경 음악 MediaPlayer 초기화 및 재생
            mediaPlayer = MediaPlayer.create(this, R.raw.mainsound);
            mediaPlayer.setLooping(true); // 음악 반복 재생
            setMediaPlayerVolume(mediaPlayer, 1.0f, 1.0f); // 좌측과 우측 스피커의 볼륨을 최대값으로 설정
            mediaPlayer.start();
        } catch (Exception e) {
            e.printStackTrace(); // 예외를 로그로 출력
        }

        try {
            // 버튼 클릭 효과음 MediaPlayer 초기화
            buttonSoundPlayer = MediaPlayer.create(this, R.raw.buttonsound);
        } catch (Exception e) {
            e.printStackTrace(); // 예외를 로그로 출력
        }

        startGameButton.setOnClickListener(v -> {
            // 버튼 클릭 시 효과음 재생
            if (buttonSoundPlayer != null) {
                buttonSoundPlayer.start();
            }
            // 배경 음악 정지
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
            }

            // 스토리 화면으로 이동
            Intent intent = new Intent(MainActivity.this, StoryActivity.class);
            startActivity(intent);
        });

        // 시스템 미디어 볼륨 최대화
        setSystemMediaVolumeMax();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            try {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop(); // 배경 음악 정지
                }
            } catch (IllegalStateException e) {
                e.printStackTrace(); // 예외를 로그로 출력
            } finally {
                mediaPlayer.release(); // 배경 음악 리소스 해제
                mediaPlayer = null;
            }
        }
        if (buttonSoundPlayer != null) {
            buttonSoundPlayer.release(); // 버튼 클릭 효과음 리소스 해제
            buttonSoundPlayer = null;
        }
    }

    private void setMediaPlayerVolume(MediaPlayer player, float leftVolume, float rightVolume) {
        if (player != null) {
            player.setVolume(leftVolume, rightVolume);
        }
    }

    private void setSystemMediaVolumeMax() {
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        if (audioManager != null) {
            // 현재 미디어 볼륨을 최대값으로 설정
            int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, maxVolume, 0);
        }
    }
}

