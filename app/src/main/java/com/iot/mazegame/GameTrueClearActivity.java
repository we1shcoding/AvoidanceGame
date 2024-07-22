package com.iot.mazegame;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class GameTrueClearActivity extends AppCompatActivity {

    private ImageView clearImageView;
    private TextView gameClearText;
    private TextView questionText;
    private TextView runText;
    private Button retryButton;
    private MediaPlayer mediaPlayer;
    private MediaPlayer laughingMediaPlayer;

    private static final String RUN_TEXT = "도망쳐도망쳐도망쳐도망쳐도망쳐도망쳐" +
            "도망쳐도망쳐도망쳐도망쳐도망쳐도망쳐도망쳐도망쳐도망쳐도망쳐도망쳐도망쳐" +
            "도망쳐도망쳐도망쳐도망쳐도망쳐도망쳐도망쳐도망쳐도망쳐도망쳐도망쳐도망쳐" +
            "도망쳐도망쳐도망쳐도망쳐도망쳐도망쳐도망쳐도망쳐도망쳐도망쳐도망쳐도망쳐" +
            "도망쳐도망쳐도망쳐도망쳐도망쳐도망쳐도망쳐도망쳐도망쳐도망쳐도망쳐도망쳐" +
            "도망쳐도망쳐도망쳐도망쳐도망쳐도망쳐도망쳐도망쳐도망쳐도망쳐도망쳐도망쳐";
    private static final int TYPE_SPEED = 10; // 타이핑 속도 (밀리초)
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_true_clear);

        clearImageView = findViewById(R.id.clearImageView);
        gameClearText = findViewById(R.id.gameClearText);
        questionText = findViewById(R.id.questionText);
        runText = findViewById(R.id.runText);
        retryButton = findViewById(R.id.retryButton);

        // MediaPlayer를 초기화하고 clear.wav 파일을 재생
        mediaPlayer = MediaPlayer.create(this, R.raw.clear);

        if (mediaPlayer != null) {
            mediaPlayer.start();
            mediaPlayer.setOnCompletionListener(mp -> {
                mp.release();
                mediaPlayer = null;
            });
        } else {
            System.out.println("MediaPlayer was not created.");
        }
    }

    public void onRetryClick(View view) {
        retryButton.setVisibility(View.GONE);  // 버튼 숨김
        clearImageView.setVisibility(View.GONE);  // 이미지 숨김
        gameClearText.setVisibility(View.GONE);  // "Game Clear" 텍스트 숨김

        // 2초 후 질문 텍스트 표시
        new Handler().postDelayed(() -> {
            questionText.setVisibility(View.VISIBLE);

            // 2초 후 질문 텍스트를 숨기고 "도망쳐" 텍스트 애니메이션 시작
            new Handler().postDelayed(() -> {
                questionText.setVisibility(View.GONE);
                runText.setVisibility(View.VISIBLE);
                startTypingAnimation();
            }, 2000);
        }, 2000);
    }

    private void startTypingAnimation() {
        runText.setText("");
        playLaughingSound();
        typeText(runText, RUN_TEXT, 0);
    }

    private void playLaughingSound() {
        if (laughingMediaPlayer != null) {
            laughingMediaPlayer.release();
        }
        laughingMediaPlayer = MediaPlayer.create(this, R.raw.laughing);
        if (laughingMediaPlayer != null) {
            laughingMediaPlayer.start();
            laughingMediaPlayer.setOnCompletionListener(mp -> {
                mp.release();
                laughingMediaPlayer = null;
            });
        } else {
            System.out.println("Laughing MediaPlayer was not created.");
        }
    }

    private void typeText(final TextView textView, final String text, final int index) {
        if (index < text.length()) {
            textView.append(String.valueOf(text.charAt(index)));
            handler.postDelayed(() -> typeText(textView, text, index + 1), TYPE_SPEED);
        } else {
            // "도망쳐" 텍스트 애니메이션이 끝난 후 화면 전환
            new Handler().postDelayed(() -> {
                Intent intent = new Intent(GameTrueClearActivity.this, GameTrueClearActivity2.class);
                startActivity(intent);
                finish();
            }, 3000); // 애니메이션이 끝난 후 1초 대기
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        if (laughingMediaPlayer != null) {
            laughingMediaPlayer.release();
            laughingMediaPlayer = null;
        }
    }
}
