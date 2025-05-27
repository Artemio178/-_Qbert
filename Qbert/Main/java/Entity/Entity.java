package Entity;

import Main.GamePanel;
import java.awt.image.BufferedImage;

public class Entity {
    protected GamePanel gamePanel;
    public boolean isJumping = false;
    public float jumpProgress = 0;
    public float startX, startY;
    public float endX, endY;
    public float jumpDuration = 0.3f; // длина прыжка в секундах (можно подстроить)
    public float jumpHeight = 10f;    // высота дуги прыжка
    public float x, y;
    public int speed;
    public BufferedImage upleft, upright, downleft, downright;
    public String direction;
    protected int mapRow = 0;
    protected int mapCol = 0;
    protected int currentHealth = 1;

    public Entity(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
    }

    public void startJump(int targetRow, int targetCol) {
        isJumping = true;
        jumpProgress = 0;
        startX = x;
        startY = y;
        endX = targetCol * gamePanel.tileSize;
        endY = targetRow * gamePanel.tileSize;
    }

    public float lerp(float start, float end, float t) {
        return start + (end - start) * t;
    }

    public int getMapRow() {
        return mapRow;
    }

    public int getMapCol() {
        return mapCol;
    }

    public void setMapPosition(int row, int col) {
        this.mapRow = row;
        this.mapCol = col;
    }

    public int getCurrentHealth() {
        return currentHealth;
    }

    public boolean isAlive() {
        return currentHealth > 0;
    }

    public void takeDamage(int damage) {
        currentHealth -= damage;
        if (currentHealth < 0) currentHealth = 0;
    }

    public int getDisplaySize() {
        return 40;
    }
}
