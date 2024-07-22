package com.iot.mazegame;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.vectordrawable.graphics.drawable.Animatable2Compat;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.bumptech.glide.load.resource.gif.GifDrawable;

public class GameOverActivity extends AppCompatActivity {

    private static final String TAG = "GameOverActivity";
    private ImageView gameoverimg; // Renamed from gameOverImageView
    private Button restartButton; // Renamed from retryButton
    private MediaPlayer screamPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);

        gameoverimg = findViewById(R.id.gameoverimg);
        restartButton = findViewById(R.id.restartButton);
        Button exitButton = findViewById(R.id.exitButton); // Added exitButton

        // Load and display GIF
        Glide.with(this)
                .asGif()
                .load(R.raw.gameover2)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(new CustomTarget<GifDrawable>() {
                    @Override
                    public void onResourceReady(@NonNull GifDrawable resource, @Nullable Transition<? super GifDrawable> transition) {
                        gameoverimg.setImageDrawable(resource);
                        resource.setLoopCount(1); // Play the animation once
                        resource.registerAnimationCallback(new Animatable2Compat.AnimationCallback() {
                            @Override
                            public void onAnimationEnd(Drawable drawable) {
                                runOnUiThread(() -> {
                                    Log.d(TAG, "Animation ended");
                                    getWindow().getDecorView().setBackgroundColor(android.graphics.Color.BLACK);
                                    restartButton.setVisibility(View.VISIBLE); // Show button after animation ends
                                });
                            }
                        });
                        resource.start();
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                        Log.d(TAG, "Resource cleared");
                    }

                    @Override
                    public void onLoadFailed(@Nullable Drawable errorDrawable) {
                        Log.e(TAG, "GIF load failed");
                    }
                });

        // Play scream sound once
        playScreamSound();

        restartButton.setOnClickListener(v -> {
            Intent intent = new Intent(GameOverActivity.this, MainActivity.class);
            // Clear back stack and start MainActivity
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish(); // Finish GameOverActivity
        });

        exitButton.setOnClickListener(v -> {
            finishAffinity(); // Close all activities and exit the app
        });
    }

    private void playScreamSound() {
        if (screamPlayer == null) {
            screamPlayer = MediaPlayer.create(this, R.raw.scream2);
            screamPlayer.setOnCompletionListener(mp -> {
                // Release MediaPlayer resources when sound is done
                mp.release();
                screamPlayer = null;
            });
            screamPlayer.start();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (screamPlayer != null) {
            try {
                screamPlayer.stop();
            } catch (IllegalStateException e) {
                e.printStackTrace(); // 예외를 로그로 출력
            } finally {
                screamPlayer.release(); // 소리 리소스 해제
                screamPlayer = null;
            }
        }
    }
}
