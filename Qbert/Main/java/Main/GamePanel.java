package Main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import Entity.GameBot;
import Entity.Player;

public class GamePanel extends JPanel implements Runnable {

    //Настройки экрана
    final int originalTileSize = 15; // 16x16 tile
    final int scale = 3;

    public final int tileSize = originalTileSize * scale; // 40x40 tile
    final int maxScreenCol = 16;
    final int maxScreenRow = 12;
    final int screenWidth = tileSize *maxScreenCol; // 768 pixels
    final int screenHeight = tileSize *maxScreenRow; // 576 pixels

    //FPS
    int FPS = 60;

    // Game states
    public enum GameState {
        START_SCREEN,
        MODE_SELECTION,
        LEVEL_SCREEN,
        PLAYING,
        DEATH_SCREEN,
        RESTART_SCREEN
    }
    public GameState gameState = GameState.START_SCREEN;
    private BufferedImage startScreenImage;
    private BufferedImage modeSelectionImage;
    private BufferedImage levelScreenImage;
    private BufferedImage level2Image;
    private BufferedImage level3Image;
    private BufferedImage level4Image;
    private BufferedImage deathScreenImage;
    private float levelScreenTimer = 0;
    private float levelScreenDuration = 2.0f; // Длительность показа экрана уровня в секундах
    private String deathMessage = ""; // Добавляем переменную для сообщения о смерти
    private boolean isBotMode = false;
    private GameBot gameBot;

    // Level management
    public int currentLevel = 1;
    private static final String SAVE_FILE = "game_save.dat";

    // Managers
    private AnimationManager animationManager;

    public KeyHandler keyH = new KeyHandler();
    Thread gameThread;
    public Player player = new Player(this,keyH);
    public Map map = new Map();

    //чел в дефеолте
    int playerX = 100;
    int playerY = 100;
    int playerSpeed = 4;

    public GamePanel() {
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.BLACK);
        this.setDoubleBuffered(true);
        this.addKeyListener(keyH);
        this.setFocusable(true);
        this.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (gameState == GameState.DEATH_SCREEN) {
                    currentLevel = 1; // Reset level to 1
                    setupLevel(); // Сначала настраиваем уровень
                    player.setAlive(true); // Возвращаем игрока к жизни
                    gameState = GameState.LEVEL_SCREEN;
                    levelScreenTimer = 0;
                    animationManager.reset();
                }
            }
        });

        // Инициализация менеджеров
        animationManager = new AnimationManager();
        gameBot = new GameBot(this, map);

        // Загрузка изображений
        try {
            startScreenImage = ImageIO.read(getClass().getClassLoader().getResourceAsStream("player/startscreenqbert.png"));
            modeSelectionImage = ImageIO.read(getClass().getClassLoader().getResourceAsStream("player/ChooseMode.png"));
            if (modeSelectionImage == null) {
                System.out.println("Ошибка: изображение ChooseMode.png не загружено");
            }
            levelScreenImage = ImageIO.read(getClass().getClassLoader().getResourceAsStream("player/level1.png"));
            level2Image = ImageIO.read(getClass().getClassLoader().getResourceAsStream("player/level2.png"));
            level3Image = ImageIO.read(getClass().getClassLoader().getResourceAsStream("player/level3.png"));
            level4Image = ImageIO.read(getClass().getClassLoader().getResourceAsStream("player/level4.png"));
            deathScreenImage = ImageIO.read(getClass().getClassLoader().getResourceAsStream("player/deathscreen.png"));
        } catch (Exception e) {
            System.out.println("Ошибка при загрузке изображений: " + e.getMessage());
            e.printStackTrace();
        }

        // Загрузка сохраненного прогресса
        loadGameProgress();

        currentLevel = 1;

        // Инициализация карты и монстров
        map.setPlayer(player);
        setupLevel();
    }

    public void setupLevel() {
        // Создаем новую карту и очищаем старую
        map = new Map();
        map.setPlayer(player);
        
        // Обновляем ссылку на карту у бота
        if (gameBot != null) {
            gameBot.setMap(map);
        }
        
        // Сбрасываем позицию игрока
        player.setDefaultValues();
        
        // Добавляем монстров в зависимости от уровня
        if (currentLevel == 1) {
            // Для первого уровня добавляем только одного монстра
            map.clearMonsters();
            map.addMonster(4, 1, 0.8f);
        } else {
            // Для остальных уровней добавляем больше монстров
            int monsterCount = currentLevel;
            for (int i = 0; i < currentLevel; i++) {
                map.addMonster(4 + i, 1, 0.8f);
            }
        }
    }

    public void saveGameProgress() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(SAVE_FILE))) {
            oos.writeInt(currentLevel);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadGameProgress() {
        File saveFile = new File(SAVE_FILE);
        if (saveFile.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(SAVE_FILE))) {
                currentLevel = ois.readInt();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    public void run() {
        double drawInterval = 1000000000/FPS; // 0.01666 сек
        double nextDrawTime = System.nanoTime() + drawInterval;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;
        long timer = 0;
        int drawCount = 0;

        while(gameThread != null) {
            currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / drawInterval;
            timer += (currentTime - lastTime);
            lastTime = currentTime;

            if(delta >= 1) {
                update();
                repaint();
                delta--;
                drawCount++;
            }

            if(timer >= 1000000000) {
                System.out.println("FPS: " + drawCount);
                drawCount = 0;
                timer = 0;
            }
        }
    }

    public void update() {
        // Обновление анимации
        animationManager.update();

        // Проверяем нажатие любой клавиши на стартовом экране или экране перезапуска
        if (gameState == GameState.START_SCREEN || gameState == GameState.RESTART_SCREEN) {
            if (keyH.anyKeyPressed()) {
                System.out.println("Переход к экрану выбора режима");
                gameState = GameState.MODE_SELECTION;
                animationManager.reset();
            }
            return;
        }

        // Обработка экрана выбора режима
        if (gameState == GameState.MODE_SELECTION) {
            if (keyH.qPressed) {
                // Режим игры за бота
                System.out.println("Switching to bot mode");
                isBotMode = true;
                currentLevel = 1;
                setupLevel();
                player.setAlive(true);
                gameState = GameState.LEVEL_SCREEN;
                levelScreenTimer = 0;
                animationManager.reset();
                gameBot.reset();
            }
            else if (keyH.ePressed) {
                // Режим игры за игрока
                System.out.println("Switching to player mode");
                isBotMode = false;
                currentLevel = 1;
                setupLevel();
                player.setAlive(true);
                gameState = GameState.LEVEL_SCREEN;
                levelScreenTimer = 0;
                animationManager.reset();
            }
            return;
        }

        // Обработка экрана уровня
        if (gameState == GameState.LEVEL_SCREEN) {
            levelScreenTimer += 0.016f; // Примерно 60 FPS
            if (levelScreenTimer >= levelScreenDuration) {
                gameState = GameState.PLAYING;
            }
            return;
        }

        // Обновляем игрока и монстров только если игра активна
        if (gameState == GameState.PLAYING) {
            if (isBotMode) {
                gameBot.update(0.016f);
                // Обновляем позицию игрока для синхронизации с ботом
                player.setMapPosition(gameBot.getCurrentRow(), gameBot.getCurrentCol());
            } else {
                player.update(0.016f);
            }
            map.updateMonsters(0.016f);

            // Проверка на смерть игрока
            if (!player.isAlive()) {
                gameState = GameState.DEATH_SCREEN;
                // Проверяем, выпрыгнул ли игрок за карту
                if (player.getMapRow() < 0 || player.getMapRow() >= map.map.length || 
                    player.getMapCol() < 0 || player.getMapCol() >= map.map[player.getMapRow()].length) {
                    deathMessage = "Вы выпрыгнули за карту!";
                } else {
                    deathMessage = "Игра окончена!";
                }
                // Сбрасываем позицию бота при смерти
                if (isBotMode) {
                    gameBot.reset();
                    player.setMapPosition(0, 0); // Сбрасываем позицию игрока тоже
                }
                return;
            }

            // Проверка на победу
            if (map.areAllTilesColored()) {
                currentLevel++;
                saveGameProgress();
                setupLevel(); // Здесь тоже сбрасывается позиция игрока
                gameState = GameState.LEVEL_SCREEN;
                levelScreenTimer = 0;
                animationManager.reset();
                if (isBotMode) {
                    gameBot.reset();
                }
            }
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        switch (gameState) {
            case START_SCREEN:
                animationManager.drawFade(g2, startScreenImage, 0, 0, screenWidth, screenHeight);
                break;
            case MODE_SELECTION:
                animationManager.drawFade(g2, modeSelectionImage, 0, 0, screenWidth, screenHeight);
                break;
            case LEVEL_SCREEN:
                // Вычисляем размеры для центрирования изображения уровня
                int levelImageWidth = screenWidth / 2;  // Половина ширины экрана
                int levelImageHeight = screenHeight / 6; // Половина высоты экрана
                int levelImageX = (screenWidth - levelImageWidth) / 2;  // Центрирование по X
                int levelImageY = (screenHeight - levelImageHeight) / 2; // Центрирование по Y
                
                // Выбираем изображение в зависимости от текущего уровня
                BufferedImage currentLevelImage = levelScreenImage;
                if (currentLevel == 2) currentLevelImage = level2Image;
                else if (currentLevel == 3) currentLevelImage = level3Image;
                else if (currentLevel == 4) currentLevelImage = level4Image;
                
                animationManager.drawFade(g2, currentLevelImage, levelImageX, levelImageY, levelImageWidth, levelImageHeight);
                break;
            case DEATH_SCREEN:
                // Отрисовка основного экрана
                map.paintComponent(g2);
                player.draw(g2);
                
                // Отрисовка экрана смерти
                if (deathScreenImage != null) {
                    int deathImageWidth = screenWidth / 2;
                    int deathImageHeight = screenHeight / 4;
                    int deathImageX = (screenWidth - deathImageWidth) / 2;
                    int deathImageY = (screenHeight - deathImageHeight) / 2;
                    animationManager.drawFade(g2, deathScreenImage, deathImageX, deathImageY, deathImageWidth, deathImageHeight);
                }
                
                // Отрисовка сообщения о смерти
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Arial", Font.BOLD, 30));
                FontMetrics fm = g2.getFontMetrics();
                int textX = (screenWidth - fm.stringWidth(deathMessage)) / 2;
                int textY = screenHeight / 2;
                g2.drawString(deathMessage, textX, textY);
                
                // Отрисовка текста "Нажмите, чтобы продолжить"
                g2.setFont(new Font("Arial", Font.BOLD, 20));
                String continueMessage = "Нажмите, чтобы продолжить";
                fm = g2.getFontMetrics();
                textX = (screenWidth - fm.stringWidth(continueMessage)) / 2;
                textY = screenHeight / 2 + 50;
                g2.drawString(continueMessage, textX, textY);
                break;
            case RESTART_SCREEN:
                // Отрисовка основного экрана
                map.paintComponent(g2);
                player.draw(g2);
                
                // Отрисовка текста "Начать игру заново?"
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Arial", Font.BOLD, 40));
                String restartMessage = "Начать игру заново?";
                fm = g2.getFontMetrics();
                textX = (screenWidth - fm.stringWidth(restartMessage)) / 2;
                textY = screenHeight / 2;
                g2.drawString(restartMessage, textX, textY);
                break;
            case PLAYING:
                map.paintComponent(g2);
                if (isBotMode) {
                    gameBot.draw(g2);
                } else {
                    player.draw(g2);
                }
                break;
        }

        g2.dispose();
    }
}
