package com.iot.mazegame;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

public class SubActivity extends AppCompatActivity {

    private int[][] maze = {
            {1, 1, 1, 1, 1, 1},
            {1, 1, 1, 1, 1, 1},
            {1, 1, 1, 1, 1, 1},
            {1, 0, 0, 0, 0, 1},
            {1, 0, 0, 0, 0, 1},
            {1, 0, 0, 0, 0, 1},
            {1, 0, 0, 0, 0, 1},
            {1, 0, 0, 0, 0, 1},
            {1, 0, 0, 0, 0, 1},
            {1, 0, 0, 0, 0, 1},
            {1, 0, 0, 0, 0, 1},
            {1, 0, 0, 0, 0, 1},
            {1, 1, 1, 1, 1, 1}
    };

    private int playerX = 1;
    private int playerY = 1;
    private int monsterX = 3;
    private int monsterY = 3;
    private int monster2X = 4;
    private int monster2Y = 4;
    private int monster3X = 2;
    private int monster3Y = 5;

    private ImageView player;
    private ImageView monster;
    private ImageView monster2;
    private ImageView monster3;
    private ImageView clearImageView; // 게임 클리어 이미지
    private TextView timerText;
    private View darkOverlay;
    private View darkOverlay80;

    private Handler handler = new Handler();
    private Random random = new Random();

    private Runnable monsterMoveRunnable;
    private Runnable monster2MoveRunnable;
    private Runnable monster3MoveRunnable;
    private Runnable timerRunnable;
    private int monsterSpeed = 1000;
    private int monster2Speed = 1500;
    private int monster3Speed = 2000;
    private int remainingTime = 3; // 남은 시간 초기값 조정
    private boolean isFlashing = false;

    private MediaPlayer gameMusic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub);

        initializeViews();
        initializeGameMusic();
        updateInitialPositions();
        startGame();
    }

    private void initializeViews() {
        player = findViewById(R.id.player);
        monster = findViewById(R.id.monster);
        monster2 = findViewById(R.id.monster2);
        monster3 = findViewById(R.id.monster3);
        timerText = findViewById(R.id.timerText);
        darkOverlay = findViewById(R.id.darkOverlay);
        darkOverlay80 = findViewById(R.id.darkOverlay80);
        clearImageView = findViewById(R.id.clearImageView); // clearImageView 초기화

        // clearImageView가 null일 경우 로그 메시지를 출력
        if (clearImageView == null) {
            Log.e("SubActivity", "clearImageView is not initialized. Check your layout file.");
        } else {
            clearImageView.setVisibility(View.GONE); // 처음에는 숨김
        }

        monster2.setVisibility(View.GONE);
        monster3.setVisibility(View.GONE);
    }

    private void initializeGameMusic() {
        gameMusic = MediaPlayer.create(this, R.raw.maingame);
        gameMusic.setLooping(true);
        gameMusic.start();
    }

    private void updateInitialPositions() {
        updatePlayerPosition();
        updateMonsterPosition();
        updateMonster2Position();
        updateMonster3Position();
    }

    private void startGame() {
        monster.setVisibility(View.VISIBLE); // 몬스터 1을 보이도록 설정
        startMonsterMovement();

        monster2.setVisibility(View.GONE); // 몬스터 2는 처음에는 숨김
        monster3.setVisibility(View.GONE); // 몬스터 3은 처음에는 숨김

        startMonster2Movement(); // 게임 시작 시 몬스터 2도 이동 시작
        startMonster3Movement(); // 게임 시작 시 몬스터 3도 이동 시작

        startGameTimer();
        startCollisionCheck();
    }

    private void updatePlayerPosition() {
        player.setX(playerX * 100);
        player.setY(playerY * 100);
    }

    private void updateMonsterPosition() {
        monster.setX(monsterX * 100);
        monster.setY(monsterY * 100);
    }

    private void updateMonster2Position() {
        monster2.setX(monster2X * 100);
        monster2.setY(monster2Y * 100);
    }

    private void updateMonster3Position() {
        monster3.setX(monster3X * 100);
        monster3.setY(monster3Y * 100);
    }

    private void movePlayerTo(float x, float y) {
        float targetX = x / 100;
        float targetY = y / 100;

        if (isWithinBounds(targetX, targetY) && maze[(int) targetY][(int) targetX] == 0) {
            player.animate()
                    .x(targetX * 100)
                    .y(targetY * 100)
                    .setDuration(300)
                    .start();

            playerX = Math.round(targetX);
            playerY = Math.round(targetY);

            checkCollisions(); // 충돌 체크
        }
    }

    private boolean isWithinBounds(float x, float y) {
        return x >= 0 && x < maze[0].length && y >= 0 && y < maze.length;
    }

    private void moveMonster(ImageView monsterView, int[] monsterPos, int speed) {
        int[] directions = {-1, 0, 1};
        int dx = directions[random.nextInt(3)];
        int dy = directions[random.nextInt(3)];

        int newX = monsterPos[0] + dx;
        int newY = monsterPos[1] + dy;

        // 유효한 이동인지 확인하고, 맵 경계를 넘지 않는지 확인
        if (isValidMove(newX, newY)) {
            monsterView.animate()
                    .x(newX * 100)
                    .y(newY * 100)
                    .setDuration(300)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            monsterPos[0] = newX;
                            monsterPos[1] = newY;
                            checkCollisions(); // 몬스터 위치가 업데이트된 후 충돌 체크
                        }
                    })
                    .start();
        }

        handler.postDelayed(() -> moveMonster(monsterView, monsterPos, speed), speed);
    }

    private void moveToPlayer(ImageView monsterView, int[] monsterPos, int speed) {
        int dx = playerX - monsterPos[0];
        int dy = playerY - monsterPos[1];

        // x와 y 방향을 조정하여 몬스터가 플레이어를 향하도록 함
        int directionX = Integer.signum(dx);
        int directionY = Integer.signum(dy);

        int newX = monsterPos[0] + directionX;
        int newY = monsterPos[1] + directionY;

        // 유효한 이동인지 확인하고, 맵 경계를 넘지 않는지 확인
        if (isValidMove(newX, newY)) {
            monsterView.animate()
                    .x(newX * 100)
                    .y(newY * 100)
                    .setDuration(300)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            monsterPos[0] = newX;
                            monsterPos[1] = newY;
                            checkCollisions(); // 몬스터 위치가 업데이트된 후 충돌 체크
                        }
                    })
                    .start();
        }

        handler.postDelayed(() -> moveToPlayer(monsterView, monsterPos, speed), speed);
    }

    private boolean isValidMove(int x, int y) {
        return x >= 0 && x < maze[0].length && y >= 0 && y < maze.length && maze[y][x] == 0;
    }

    private void startMonsterMovement() {
        monsterMoveRunnable = () -> moveMonster(monster, new int[]{monsterX, monsterY}, monsterSpeed);
        handler.postDelayed(monsterMoveRunnable, monsterSpeed);
    }

    private void startMonster2Movement() {
        monster2MoveRunnable = () -> moveToPlayer(monster2, new int[]{monster2X, monster2Y}, monster2Speed);
        handler.postDelayed(monster2MoveRunnable, monster2Speed);
    }

    private void startMonster3Movement() {
        monster3MoveRunnable = () -> moveToPlayer(monster3, new int[]{monster3X, monster3Y}, monster3Speed);
        handler.postDelayed(monster3MoveRunnable, monster3Speed);
    }

    private void startGameTimer() {
        timerRunnable = new Runnable() {
            @Override
            public void run() {
                remainingTime--;
                updateTimerText();

                adjustMonsterSpeed();

                if (remainingTime <= 8) {
                    darkOverlay80.setVisibility(View.VISIBLE);
                    darkOverlay.setVisibility(View.GONE);
                } else if (remainingTime <= 15) {
                    darkOverlay.setVisibility(View.VISIBLE);
                    darkOverlay80.setVisibility(View.GONE);
                } else {
                    darkOverlay.setVisibility(View.GONE);
                    darkOverlay80.setVisibility(View.GONE);
                }

                if (remainingTime > 0) {
                    handler.postDelayed(this, 1000);
                } else {
                    gameClear(); // 게임 클리어 처리
                }
            }
        };
        handler.postDelayed(timerRunnable, 1000);
    }

    private void updateTimerText() {
        timerText.setText(String.valueOf(remainingTime));

        if (remainingTime <= 10) {
            if (isFlashing) {
                timerText.setTextColor(Color.RED);
            } else {
                timerText.setTextColor(Color.WHITE);
            }
            isFlashing = !isFlashing;
        } else {
            timerText.setTextColor(Color.WHITE);
        }
    }

    private void adjustMonsterSpeed() {
        if (remainingTime == 20) {
            if (monster2.getVisibility() == View.GONE) {
                monster2.setVisibility(View.VISIBLE);
                startMonster2Movement();
            }
            monsterSpeed = Math.max(200, monsterSpeed - 200);
        } else if (remainingTime == 10) {
            if (monster3.getVisibility() == View.GONE) {
                monster3.setVisibility(View.VISIBLE);
                startMonster3Movement();
            }
            monster2Speed = Math.max(200, monster2Speed - 200);
            monster3Speed = Math.max(200, monster3Speed - 200);
        }
    }

    private void startCollisionCheck() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                checkCollisions();
                handler.postDelayed(this, 100); // 주기적으로 충돌 체크
            }
        }, 100);
    }
    private void checkCollisions() {
        int collisionThreshold =30; // 플레이어와 몬스터의 충돌 범위 (픽셀 단위)

        // 플레이어와 몬스터 간의 충돌 검사
        boolean collision1 = monster.getVisibility() == View.VISIBLE &&
                Math.abs(player.getX() - monster.getX()) < collisionThreshold &&
                Math.abs(player.getY() - monster.getY()) < collisionThreshold;

        boolean collision2 = monster2.getVisibility() == View.VISIBLE &&
                Math.abs(player.getX() - monster2.getX()) < collisionThreshold &&
                Math.abs(player.getY() - monster2.getY()) < collisionThreshold;

        boolean collision3 = monster3.getVisibility() == View.VISIBLE &&
                Math.abs(player.getX() - monster3.getX()) < collisionThreshold &&
                Math.abs(player.getY() - monster3.getY()) < collisionThreshold;

        if (collision1 || collision2 || collision3) {
            gameOver(); // 게임 오버 처리
        }
    }

    private void gameClear() {
        stopGameMusic();
        showClearImage(); // Show the game clear image
        handler.postDelayed(() -> navigateToActivity(GameTrueClearActivity.class), 2000); // Navigate to GameTrueClearActivity after 2 seconds
    }

    private void gameOver() {
        stopGameMusic();
        navigateToActivity(GameOverActivity.class);
    }

    private void stopGameMusic() {
        if (gameMusic != null) {
            try {
                if (gameMusic.isPlaying()) {
                    gameMusic.stop();
                }
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } finally {
                gameMusic.release();
                gameMusic = null;
            }
        }
    }

    private void showClearImage() {
        if (clearImageView != null) {
            clearImageView.setVisibility(View.VISIBLE); // clear.png 이미지 표시
        } else {
            Log.e("SubActivity", "clearImageView is null. Cannot display clear image.");
        }
    }

    private void navigateToActivity(Class<?> activityClass) {
        handler.removeCallbacksAndMessages(null);
        Intent intent = new Intent(this, activityClass);
        intent.putExtra("remainingTime", remainingTime);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            movePlayerTo(event.getX(), event.getY());
            return true;
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopGameMusic();
        handler.removeCallbacksAndMessages(null);
    }
}
