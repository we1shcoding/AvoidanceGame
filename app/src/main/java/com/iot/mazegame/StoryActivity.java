package com.iot.mazegame;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class StoryActivity extends AppCompatActivity {

    private TextView storyTextView;
    private Button enterButton;
    private Button returnButton;
    private Handler handler;
    private int currentIndex = 0;
    private boolean isTextFullyDisplayed = false;
    private String[] storyTexts = {
            "어두운 밤, 당신은 오래된\n저택 앞에 서 있습니다.",
            "저택에 대한 괴담이 사람들\n사이에서 떠돌고 있습니다.",
            "보라색 괴물이 나타난다는 저택,\n그리고 그곳에 들어간 사람은 아무도\n돌아오지 않았다고 전해집니다...",
            "호기심이 많은 당신은\n낡은 저택의 문을 열고\n들어가기로 결심합니다.",
            "저택의 문을 열었을 때, 무거운 문이\n삐걱거리며 열리고, 안쪽에서 희미한\n빛이 새어 나옵니다.",
            "들어가는 것과 돌아가는 것 중\n무엇을 선택할까요?"
    };
    private static final int TYPE_SPEED = 100; // 텍스트 타이핑 속도 (밀리초)
    private MediaPlayer buttonSound; // MediaPlayer 객체
    private MediaPlayer returnSound; // MediaPlayer 객체 추가
    private MediaPlayer textSound; // MediaPlayer 객체 추가
    private MediaPlayer backgroundMusic; // 배경음악 MediaPlayer 객체 추가

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story);

        storyTextView = findViewById(R.id.storyTextView);
        enterButton = findViewById(R.id.enterButton);
        returnButton = findViewById(R.id.returnButton);
        handler = new Handler();

        // MediaPlayer 초기화
        buttonSound = MediaPlayer.create(this, R.raw.buttonsound);
        returnSound = MediaPlayer.create(this, R.raw.run); // 새 MediaPlayer 객체 초기화
        textSound = MediaPlayer.create(this, R.raw.text); // text.mp3 초기화
        backgroundMusic = MediaPlayer.create(this, R.raw.storysound2); // 배경음악 초기화

        // 소리 볼륨을 최대값으로 설정
        setMediaPlayerVolume(buttonSound, 1.0f, 1.0f);
        setMediaPlayerVolume(returnSound, 1.0f, 1.0f);
        setMediaPlayerVolume(textSound, 1.0f, 1.0f);
        setMediaPlayerVolume(backgroundMusic, 1.0f, 1.0f);

        // 배경음악 무한 재생
        backgroundMusic.setLooping(true);
        backgroundMusic.start();

        // 버튼 초기 상태를 숨김으로 설정
        enterButton.setVisibility(View.GONE);
        returnButton.setVisibility(View.GONE);

        // 버튼 클릭 리스너 설정
        enterButton.setOnClickListener(v -> {
            // "들어간다." 버튼 클릭 시 소리 재생 및 SubActivity로 이동
            buttonSound.start(); // 소리 재생
            startActivity(new Intent(StoryActivity.this, SubActivity.class));
            finish(); // 현재 액티비티 종료
        });

        returnButton.setOnClickListener(v -> {
            // "돌아간다." 버튼 클릭 시 소리 재생 및 GameClearActivity로 이동
            returnSound.start(); // 소리 재생
            startActivity(new Intent(StoryActivity.this, GameClearActivity.class));
            finish(); // 현재 액티비티 종료
        });

        displayNextText();
    }

    private void displayNextText() {
        if (currentIndex < storyTexts.length) {
            String text = storyTexts[currentIndex];
            storyTextView.setText(""); // 텍스트 뷰 초기화
            isTextFullyDisplayed = false; // 텍스트가 완전히 표시되지 않았음을 표시
            typeText(text, 0);
        } else {
            // 모든 텍스트가 표시된 후에는 버튼들을 나타나게 함
            enterButton.setVisibility(View.VISIBLE);
            returnButton.setVisibility(View.VISIBLE);
        }
    }

    private void typeText(final String text, final int index) {
        if (index < text.length()) {
            storyTextView.append(String.valueOf(text.charAt(index))); // 한 글자씩 추가

            // 텍스트 소리 재생
            if (textSound != null) {
                textSound.start();
                textSound.setOnCompletionListener(mp -> mp.seekTo(0)); // 소리 반복을 위해
            }

            handler.postDelayed(() -> typeText(text, index + 1), TYPE_SPEED); // 설정된 속도로 다음 글자 표시
        } else {
            // 텍스트가 모두 표시된 후
            isTextFullyDisplayed = true;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (isTextFullyDisplayed) {
                // 텍스트가 완전히 표시된 경우 다음 텍스트로 이동
                currentIndex++;
                displayNextText();
            } else {
                // 텍스트가 완전히 표시되지 않은 경우 텍스트를 즉시 모두 표시
                handler.removeCallbacksAndMessages(null);
                storyTextView.setText(storyTexts[currentIndex]);
                isTextFullyDisplayed = true; // 현재 텍스트가 모두 표시된 것으로 간주
            }
            return true;
        }
        return super.onTouchEvent(event);
    }

    private void setMediaPlayerVolume(MediaPlayer mediaPlayer, float leftVolume, float rightVolume) {
        if (mediaPlayer != null) {
            mediaPlayer.setVolume(leftVolume, rightVolume);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (buttonSound != null) {
            buttonSound.release(); // MediaPlayer 자원 해제
        }
        if (returnSound != null) {
            returnSound.release(); // MediaPlayer 자원 해제
        }
        if (textSound != null) {
            textSound.release(); // MediaPlayer 자원 해제
        }
        if (backgroundMusic != null) {
            backgroundMusic.release(); // MediaPlayer 자원 해제
        }
    }
}
