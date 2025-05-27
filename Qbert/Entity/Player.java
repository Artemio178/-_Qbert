package Entity;

import Main.GamePanel;
import Main.KeyHandler;
import Main.Map;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class Player extends Entity {
    GamePanel gp;        // Ссылка на главную панель игры
    KeyHandler keyH;     // Обработчик нажатий клавиш
    Map map;            // Карта игры

    // Позиция на карте
    public int mapRow = 0;    // Текущая строка на карте
    public int mapCol = 0;    // Текущий столбец на карте
    private int cubeSize = 70;  // Размер куба (спрайта) игрока
    private int displaySize = 40;  // Новый размер для отображения
    private int xOffset = 300;  // Смещение по X от левого края экрана
    private int yOffset = 50;   // Смещение по Y от верхнего края экрана
    private float horizontalSpacing = 0.5f;  // Горизонтальный интервал между кубами
    private float verticalSpacing = 0.7f;    // Вертикальный интервал между кубами

    // Характеристики игрока
    private int maxHealth = 1;              // Максимальное здоровье
    private int currentHealth;              // Текущее здоровье

    // Изображения для разных направлений движения
    public BufferedImage upleft, upright, downleft, downright;

    private boolean alive = true; // Добавляем переменную для отслеживания состояния игрока

    // Функция для плавного перехода между двумя значениями
    private float lerp(float a, float b, float t) {
        return a + (b - a) * t;
    }

    // Конструктор игрока
    public Player(GamePanel gp, KeyHandler keyH) {
        this.gp = gp;
        this.keyH = keyH;
        this.map = new Map();
        this.currentHealth = maxHealth;

        setDefaultValues();
        getPlayerImage();
    }

    // Установка начальных значений
    public void setDefaultValues() {
        mapRow = 0;    // Начальная строка
        mapCol = 0;    // Начальный столбец
        
        // Расчет начальной позиции на экране
        x = xOffset + mapCol * cubeSize - (int)(mapRow * cubeSize * horizontalSpacing);
        y = yOffset + (int)(mapRow * cubeSize * verticalSpacing);
        
        speed = 4;     // Скорость движения
        direction = "down";  // Начальное направление
        isJumping = false;   // Сбрасываем состояние прыжка
        jumpProgress = 0;    // Сбрасываем прогресс прыжка
    }

    public void setMapPosition(int row, int col) {
        mapRow = row;
        mapCol = col;
        // Обновляем позицию на экране
        x = xOffset + mapCol * cubeSize - (int)(mapRow * cubeSize * horizontalSpacing);
        y = yOffset + (int)(mapRow * cubeSize * verticalSpacing);
    }

    // Загрузка изображений игрока
    public void getPlayerImage() {
        try {
            upleft = ImageIO.read(new File("src/res/player/playerupleft.png"));      // Вверх-влево
            upright = ImageIO.read(new File("src/res/player/playerupright.png"));    // Вверх-вправо
            downleft = ImageIO.read(new File("src/res/player/playerleftdown.png"));  // Вниз-влево
            downright = ImageIO.read(new File("src/res/player/playerdownright.png")); // Вниз-вправо
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Начало прыжка
    public void startJump(int targetRow, int targetCol) {
        if (isJumping) return;  // Если уже прыгаем, выходим

        // Проверяем, не выпрыгивает ли игрок за пределы карты
        if (targetRow < 0 || targetRow >= map.map.length || 
            targetCol < 0 || targetCol >= map.map[targetRow].length) {
            takeDamage(1);  // Наносим урон сразу
            return;
        }

        isJumping = true;       // Устанавливаем флаг прыжка
        jumpProgress = 0;       // Сбрасываем прогресс прыжка

        startX = x;             // Запоминаем начальную позицию X
        startY = y;             // Запоминаем начальную позицию Y

        // Расчет конечной позиции прыжка с более точным округлением
        float targetX = xOffset + targetCol * cubeSize - (targetRow * cubeSize * horizontalSpacing);
        float targetY = yOffset + (targetRow * cubeSize * verticalSpacing);
        
        // Округляем до ближайшего целого числа
        endX = Math.round(targetX);
        endY = Math.round(targetY);
    }

    // Обновление состояния игрока
    public void update(float deltaTime) {
        // Проверяем столкновение с монстрами
        if (map != null && map.getMonsters() != null) {
            checkMonsterCollision();
        }
        
        // Обработка прыжка
        if (isJumping) {
            jumpProgress += deltaTime / jumpDuration;  // Увеличиваем прогресс прыжка
            if (jumpProgress >= 1.0f) {               // Если прыжок завершен
                jumpProgress = 1.0f;
                isJumping = false;
                x = endX;                             // Устанавливаем конечную позицию
                y = endY;
                
                // Проверяем, не выпрыгнул ли игрок за пределы карты
                if (!isOffMap()) {
                    // Обновляем позицию на карте с более точным расчетом
                    float exactRow = (y - yOffset) / (cubeSize * verticalSpacing);
                    float exactCol = (x - xOffset + exactRow * cubeSize * horizontalSpacing) / cubeSize;
                    mapRow = Math.round(exactRow);
                    mapCol = Math.round(exactCol);
                    
                    // Окрашиваем плитку при приземлении
                    if (gp.map != null) {
                        gp.map.colorTile(mapRow, mapCol);
                    }
                } else {
                    // Если игрок выпрыгнул за пределы карты, он погибает
                    takeDamage(1);
                }
            } else {
                // Плавное движение во время прыжка
                float t = jumpProgress;
                x = lerp(startX, endX, t);
                y = lerp(startY, endY, t) - (float)Math.sin(t * Math.PI) * jumpHeight;
            }
        } else {
            // Обработка движения
            if (keyH.downPressed) {           // Если нажата клавиша вниз
                direction = "down";
                startJump(mapRow + 1, mapCol);
            } else if (keyH.upPressed) {      // Если нажата клавиша вверх
                direction = "up";
                startJump(mapRow - 1, mapCol);
            } else if (keyH.leftPressed) {    // Если нажата клавиша влево
                direction = "left";
                startJump(mapRow, mapCol - 1);
            } else if (keyH.rightPressed) {   // Если нажата клавиша вправо
                direction = "right";
                startJump(mapRow, mapCol + 1);
            }
        }
    }

    // Получение урона
    public void takeDamage(int damage) {
        currentHealth -= damage;          // Уменьшаем здоровье
        if (currentHealth <= 0) {         // Если здоровье достигло 0 или меньше
            currentHealth = 0;
            alive = false;                // Игрок погибает
        }
    }

    // Проверка, жив ли игрок
    public boolean isAlive() {
        return alive;
    }

    // Получение текущего здоровья
    public int getCurrentHealth() {
        return currentHealth;
    }

    // Отрисовка игрока
    public void draw(Graphics2D g2) {
        BufferedImage image = null;

        // Выбор изображения в зависимости от направления
        switch(direction) {
            case "up":
                image = upleft;
                break;
            case "down":
                image = downleft;
                break;
            case "left":
                image = upleft;
                break;
            case "right":
                image = upright;
                break;
        }

        // Отрисовка изображения
        g2.drawImage(image, (int)x, (int)y, displaySize, displaySize, null);
    }

    private void checkMonsterCollision() {
        // Проверяем столкновение с монстрами
        for (Monster monster : map.getMonsters()) {
            if (monster != null) {
                // Получаем позицию монстра
                int monsterX = monster.getX();
                int monsterY = monster.getY();
                
                // Выводим отладочную информацию
                System.out.println("Player position: (" + x + ", " + y + ")");
                System.out.println("Monster position: (" + monsterX + ", " + monsterY + ")");
                System.out.println("Distance: " + Math.abs(x - monsterX) + ", " + Math.abs(y - monsterY));
                
                // Проверяем столкновение с меньшим порогом
                if (Math.abs(x - monsterX) < displaySize && Math.abs(y - monsterY) < displaySize) {
                    // Наносим урон игроку при столкновении
                    takeDamage(1);
                    System.out.println("Игра окончена! Игрок столкнулся с монстром!");
                    return;
                }
            }
        }
    }

    public int getDisplaySize() {
        return displaySize;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    // Проверка, находится ли игрок за пределами карты
    private boolean isOffMap() {
        return mapRow < 0 || mapRow >= map.map.length || 
               mapCol < 0 || mapCol >= map.map[mapRow].length;
    }

    public int getMapRow() {
        return mapRow;
    }

    public int getMapCol() {
        return mapCol;
    }
}
