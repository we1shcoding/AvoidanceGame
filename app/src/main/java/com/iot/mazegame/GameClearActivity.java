package com.iot.mazegame;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class GameClearActivity extends AppCompatActivity {

    private ImageView clearImageView;
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_clear);

        clearImageView = findViewById(R.id.clearImageView);
        clearImageView.setVisibility(View.VISIBLE);
        clearImageView.setImageResource(R.drawable.clear); // PNG 이미지 설정

        // MediaPlayer를 초기화하고 clear.wav 파일을 재생
        mediaPlayer = MediaPlayer.create(this, R.raw.clear);

        // MediaPlayer가 제대로 생성되었는지 확인
        if (mediaPlayer != null) {
            mediaPlayer.start(); // 소리 재생 시작

            // 소리가 끝난 후 MediaPlayer를 정리
            mediaPlayer.setOnCompletionListener(mp -> {
                mp.release(); // MediaPlayer 해제
                mediaPlayer = null;
            });
        } else {
            // MediaPlayer가 null인 경우 로깅 (디버깅 용도)
            System.out.println("MediaPlayer was not created.");
        }
    }

    public void onRetryClick(View view) {
        // 게임을 다시 시작하기 위해 MainActivity로 돌아가기
        if (mediaPlayer != null) {
            mediaPlayer.release(); // MediaPlayer가 여전히 재생 중일 경우 해제
            mediaPlayer = null;
        }

        Intent intent = new Intent(GameClearActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}
