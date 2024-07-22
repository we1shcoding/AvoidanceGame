package com.iot.mazegame;

import android.animation.ObjectAnimator;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameTrueClearActivity2 extends AppCompatActivity {
    private List<ImageView> monsterImageViews = new ArrayList<>();
    private TextView monsterTextView;
    private Handler handler = new Handler();
    private Random random = new Random();
    private MediaPlayer mediaPlayer;

    private static final int MONSTER_MOVE_DELAY = 2000; // 몬스터 이동 딜레이 (밀리초)
    private static final int CELL_SIZE = 100; // 미로의 셀 크기 (픽셀)
    private static final int ANIMATION_DURATION = 1000; // 애니메이션 지속 시간 (밀리초)
    private static final int[][] maze = {
            {1, 1, 1, 1, 1, 1},
            {1, 1, 1, 1, 1, 1},
            {1, 1, 1, 1, 1, 1},
            {1,1, 1,1, 1, 1},
            {1, 0, 0, 0, 0, 1},
            {1, 0, 0, 0, 0, 1},
            {1, 0, 0, 0, 0, 1},
            {1, 0, 0, 0, 0, 1},
            {1, 0, 0, 0, 0, 1},
            {1, 0, 0, 0, 0, 1},
            {1, 1, 1, 1, 1, 1},
            {1, 1, 1, 1, 1, 1},
            {1, 1, 1, 1, 1, 1}
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_true_clear2);

        // 몬스터 이미지뷰들 초기화
        monsterImageViews.add(findViewById(R.id.monsterImageView1));
        monsterImageViews.add(findViewById(R.id.monsterImageView2));
        monsterImageViews.add(findViewById(R.id.monsterImageView3));
        monsterImageViews.add(findViewById(R.id.monsterImageView4));
        monsterImageViews.add(findViewById(R.id.monsterImageView5));
        monsterImageViews.add(findViewById(R.id.monsterImageView6));
        monsterImageViews.add(findViewById(R.id.monsterImageView7));
        monsterImageViews.add(findViewById(R.id.monsterImageView8));
        monsterImageViews.add(findViewById(R.id.monsterImageView9));
        monsterImageViews.add(findViewById(R.id.monsterImageView10));
        monsterImageViews.add(findViewById(R.id.monsterImageView11));
        monsterTextView = findViewById(R.id.monsterTextView);

        // MediaPlayer 초기화 및 재생
        mediaPlayer = MediaPlayer.create(this, R.raw.mainsound);
        mediaPlayer.setLooping(true); // 사운드를 반복 재생할지 설정
        mediaPlayer.start();

        // 몬스터를 화면에 표시하고 이동시키기
        initializeMonsters();
        showMonsters();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release(); // MediaPlayer 자원 해제
            mediaPlayer = null;
        }
    }

    private void initializeMonsters() {
        int mazeWidth = maze[0].length;
        int mazeHeight = maze.length;
        List<int[]> freeCells = new ArrayList<>();

        // 모든 빈 셀을 리스트에 추가
        for (int y = 0; y < mazeHeight; y++) {
            for (int x = 0; x < mazeWidth; x++) {
                if (maze[y][x] == 0) {
                    freeCells.add(new int[]{x, y});
                }
            }
        }

        // 셔플 알고리즘 (Fisher-Yates Shuffle)
        Random rng = new Random();
        int n = freeCells.size();
        for (int i = n - 1; i > 0; i--) {
            int index = rng.nextInt(i + 1);
            int[] temp = freeCells.get(index);
            freeCells.set(index, freeCells.get(i));
            freeCells.set(i, temp);
        }

        // 몬스터 위치 초기화
        for (int i = 0; i < monsterImageViews.size(); i++) {
            ImageView monster = monsterImageViews.get(i);
            if (i < freeCells.size()) {
                int[] cell = freeCells.get(i);
                int startX = cell[0];
                int startY = cell[1];
                monster.setX(startX * CELL_SIZE);
                monster.setY(startY * CELL_SIZE);
                monster.setVisibility(View.VISIBLE);
            }
        }
    }

    private void showMonsters() {
        for (ImageView monster : monsterImageViews) {
            monster.setVisibility(View.VISIBLE);
            moveMonsterRandomly(monster);
        }

        // 잠시 후 몬스터 텍스트 표시
        handler.postDelayed(() -> {
            monsterTextView.setVisibility(View.VISIBLE);
        }, 2000); // 2초 후 텍스트 표시
    }

    private void moveMonsterRandomly(final ImageView monster) {
        final int mazeWidth = maze[0].length;
        final int mazeHeight = maze.length;

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                int startX = (int) monster.getX();
                int startY = (int) monster.getY();

                int startCellX = startX / CELL_SIZE;
                int startCellY = startY / CELL_SIZE;

                int newX = startX;
                int newY = startY;

                // 가능한 이동 방향을 배열에 저장
                int[] directions = new int[]{0, 1, 2, 3}; // 오른쪽, 왼쪽, 아래쪽, 위쪽
                Random rng = new Random();
                for (int i = directions.length - 1; i > 0; i--) {
                    int index = rng.nextInt(i + 1);
                    int temp = directions[index];
                    directions[index] = directions[i];
                    directions[i] = temp;
                }

                boolean moved = false;

                for (int direction : directions) {
                    int cellX = startCellX;
                    int cellY = startCellY;

                    switch (direction) {
                        case 0: // 오른쪽
                            if (cellX + 1 < mazeWidth && maze[cellY][cellX + 1] == 0) {
                                cellX++;
                                moved = true;
                            }
                            break;
                        case 1: // 왼쪽
                            if (cellX - 1 >= 0 && maze[cellY][cellX - 1] == 0) {
                                cellX--;
                                moved = true;
                            }
                            break;
                        case 2: // 아래쪽
                            if (cellY + 1 < mazeHeight && maze[cellY + 1][cellX] == 0) {
                                cellY++;
                                moved = true;
                            }
                            break;
                        case 3: // 위쪽
                            if (cellY - 1 >= 0 && maze[cellY - 1][cellX] == 0) {
                                cellY--;
                                moved = true;
                            }
                            break;
                    }

                    if (moved) {
                        newX = cellX * CELL_SIZE;
                        newY = cellY * CELL_SIZE;
                        break;
                    }
                }

                // 몬스터의 위치를 부드럽게 이동
                ObjectAnimator animX = ObjectAnimator.ofFloat(monster, "x", startX, newX);
                ObjectAnimator animY = ObjectAnimator.ofFloat(monster, "y", startY, newY);

                animX.setDuration(ANIMATION_DURATION);
                animY.setDuration(ANIMATION_DURATION);

                animX.setInterpolator(new AccelerateDecelerateInterpolator());
                animY.setInterpolator(new AccelerateDecelerateInterpolator());

                animX.start();
                animY.start();

                handler.postDelayed(this, MONSTER_MOVE_DELAY); // 다음 이동 예약
            }
        }, MONSTER_MOVE_DELAY); // 최초 이동 예약
    }
}
