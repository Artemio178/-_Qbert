package Entity;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Random;

import javax.imageio.ImageIO;

import Main.GamePanel;
import Main.Map;

public class Monster extends Entity {
    private Map map;
    private Random random;
    private float moveTimer = 0;
    private float moveInterval = 0.7f; // Интервал между движениями в секундах
    private int damage = 1;
    private int attackRange = 40; // Дистанция атаки в пикселях

    // Map position
    public int mapRow;
    public int mapCol;
    private int cubeSize = 70;  // Размер для логики движения
    private int displaySize = 30;  // Размер для отображения
    private int xOffset = 300;
    private int yOffset = 50;
    private float horizontalSpacing = 0.5f;
    private float verticalSpacing = 0.7f;

    // Monster size control
    private float sizeMultiplier = 1.0f;
    private int baseSize = 70;

    private BufferedImage monsterImage;
    private BufferedImage jumpUpLeft;
    private BufferedImage jumpUpRight;
    private BufferedImage jumpDownLeft;
    private BufferedImage jumpDownRight;
    private BufferedImage upLeft;
    private BufferedImage upRight;
    private BufferedImage downLeft;
    private BufferedImage downRight;
    private String currentDirection = "down";

    @Override
    public float lerp(float start, float end, float t) {
        return start + (end - start) * t;
    }

    public Monster(GamePanel gamePanel, Map map, int startRow, int startCol) {
        super(gamePanel);
        this.map = map;
        this.random = new Random();
        this.mapRow = startRow;
        this.mapCol = startCol;
        
        x = xOffset + mapCol * cubeSize - (int)(mapRow * cubeSize * horizontalSpacing);
        y = yOffset + (int)(mapRow * cubeSize * verticalSpacing);
        
        loadImage();
    }

    // Метод для установки размера монстра
    public void setSize(float multiplier) {
        this.sizeMultiplier = multiplier;
        this.displaySize = (int)(baseSize * multiplier);
    }

    // Метод для получения текущего размера монстра
    public float getSize() {
        return sizeMultiplier;
    }

    private void loadImage() {
        try {
            monsterImage = ImageIO.read(getClass().getClassLoader().getResourceAsStream("player/snake.png"));
            jumpUpLeft = ImageIO.read(getClass().getClassLoader().getResourceAsStream("player/snakejumpupleft.png"));
            jumpUpRight = ImageIO.read(getClass().getClassLoader().getResourceAsStream("player/snakejumpupright.png"));
            jumpDownLeft = ImageIO.read(getClass().getClassLoader().getResourceAsStream("player/snakejumpdownleft.png"));
            jumpDownRight = ImageIO.read(getClass().getClassLoader().getResourceAsStream("player/snakejumpdownright.png"));
            upLeft = ImageIO.read(getClass().getClassLoader().getResourceAsStream("player/snakeupleft.png"));
            upRight = ImageIO.read(getClass().getClassLoader().getResourceAsStream("player/snakeupright.png"));
            downLeft = ImageIO.read(getClass().getClassLoader().getResourceAsStream("player/snakedownleft.png"));
            downRight = ImageIO.read(getClass().getClassLoader().getResourceAsStream("player/snakedownright.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void update(float deltaTime) {
        if (isJumping) {
            jumpProgress += deltaTime / jumpDuration;
            if (jumpProgress >= 1.0f) {
                jumpProgress = 1.0f;
                isJumping = false;
                x = endX;
                y = endY;
                // Обновляем позицию на карте с более точным расчетом
                float exactRow = (y - yOffset) / (cubeSize * verticalSpacing);
                float exactCol = (x - xOffset + exactRow * cubeSize * horizontalSpacing) / cubeSize;
                mapRow = Math.round(exactRow);
                mapCol = Math.round(exactCol);
            } else {
                float t = jumpProgress;
                x = lerp(startX, endX, t);
                y = lerp(startY, endY, t) - (float)Math.sin(t * Math.PI) * jumpHeight;
            }
        } else {
            moveTimer += deltaTime;
            if (moveTimer >= moveInterval) {
                moveTimer = 0;
                moveRandomly();
            }
        }

        // Проверяем столкновение с игроком
        if (map != null && map.getPlayer() != null) {
            Player player = map.getPlayer();
            float dx = x - player.x;
            float dy = y - player.y;
            float distance = (float) Math.sqrt(dx * dx + dy * dy);
            
            // Если расстояние меньше суммы размеров монстра и игрока
            if (distance < (displaySize + player.getDisplaySize()) / 2) {
                System.out.println("Монстр наносит урон игроку!");
                player.takeDamage(1);
            }
        }
    }

    private void moveRandomly() {
        int[] possibleMoves = {0, 1, 2, 3}; // 0: up, 1: right, 2: down, 3: left
        int move = possibleMoves[random.nextInt(4)];
        
        switch (move) {
            case 0: // up
                if (mapRow - 1 >= 0 && mapCol < map.map[mapRow - 1].length) {
                    currentDirection = "up";
                    startJump(mapRow - 1, mapCol);
                }
                break;
            case 1: // right
                if (mapCol + 1 < map.map[mapRow].length) {
                    currentDirection = "right";
                    startJump(mapRow, mapCol + 1);
                }
                break;
            case 2: // down
                if (mapRow + 1 < map.map.length && mapCol < map.map[mapRow + 1].length) {
                    currentDirection = "down";
                    startJump(mapRow + 1, mapCol);
                }
                break;
            case 3: // left
                if (mapCol - 1 >= 0) {
                    currentDirection = "left";
                    startJump(mapRow, mapCol - 1);
                }
                break;
        }
    }

    public void startJump(int targetRow, int targetCol) {
        if (isJumping) return;

        isJumping = true;
        jumpProgress = 0;

        startX = x;
        startY = y;

        // Расчет конечной позиции прыжка с более точным округлением
        float targetX = xOffset + targetCol * cubeSize - (targetRow * cubeSize * horizontalSpacing);
        float targetY = yOffset + (targetRow * cubeSize * verticalSpacing);
        
        // Округляем до ближайшего целого числа
        endX = Math.round(targetX);
        endY = Math.round(targetY);
    }

    public void draw(Graphics2D g2) {
        BufferedImage imageToDraw = monsterImage;
        
        if (isJumping) {
            switch(currentDirection) {
                case "up":
                    if (endX < x) imageToDraw = jumpUpLeft;
                    else imageToDraw = jumpUpRight;
                    break;
                case "down":
                    if (endX < x) imageToDraw = jumpDownLeft;
                    else imageToDraw = jumpDownRight;
                    break;
                case "left":
                    imageToDraw = jumpUpLeft;
                    break;
                case "right":
                    imageToDraw = jumpUpRight;
                    break;
            }
        } else {
            switch(currentDirection) {
                case "up":
                    if (endX < x) imageToDraw = upLeft;
                    else imageToDraw = upRight;
                    break;
                case "down":
                    if (endX < x) imageToDraw = downLeft;
                    else imageToDraw = downRight;
                    break;
                case "left":
                    imageToDraw = upLeft;
                    break;
                case "right":
                    imageToDraw = upRight;
                    break;
            }
        }
        
        // Используем displaySize для отрисовки
        g2.drawImage(imageToDraw, (int)x, (int)y, displaySize, displaySize, null);
    }

    public int getX() {
        return (int)x;
    }

    public int getY() {
        return (int)y;
    }
} 