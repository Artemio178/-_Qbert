package Main;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import Entity.Monster;
import Entity.Player;

public class Map extends JPanel {

    public int[][] map = {
            {1,},
            {1, 1},
            {1, 1, 1},
            {1, 1, 1, 1},
            {1, 1, 1, 1, 1},
            {1, 1, 1, 1, 1, 1},
            {1, 1, 1, 1, 1, 1, 1}
    };

    // Track colored tiles
    private boolean[][] coloredTiles;

    public int cubeSize = 75;
    public float horizontalSpacing = 0.5f; // Множитель для горизонтального смещения
    public float verticalSpacing = 0.7f;   // Множитель для вертикального смещения
    public Image player;
    public int playerRow = 0;
    public int playerCol = 0;
    public int xOffset = 290;
    public int yOffset = 70;
    private BufferedImage cubeTexture;
    private BufferedImage coloredCubeTexture;
    private List<Monster> monsters;
    private Player playerRef;

    public Map() {
        try {
            cubeTexture = ImageIO.read(getClass().getClassLoader().getResourceAsStream("player/Kub.png"));
            coloredCubeTexture = ImageIO.read(getClass().getClassLoader().getResourceAsStream("player/KubGreen.png"));
            if (cubeTexture == null || coloredCubeTexture == null) {
                System.out.println("Error: Failed to load cube textures");
            } else {
                System.out.println("Successfully loaded cube textures");
            }
        } catch (Exception e) {
            System.out.println("Error loading textures: " + e.getMessage());
            e.printStackTrace();
        }
        monsters = new ArrayList<>();
        
        // Initialize colored tiles array
        coloredTiles = new boolean[map.length][];
        for (int i = 0; i < map.length; i++) {
            coloredTiles[i] = new boolean[map[i].length];
            System.out.println("Initialized row " + i + " with " + map[i].length + " tiles");
        }
    }

    public void setPlayer(Player player) {
        this.playerRef = player;
    }

    public void clearMonsters() {
        monsters.clear();
    }

    public void addMonster(int row, int col) {
        monsters.add(new Monster(null, this, row, col));
    }

    // Метод для добавления монстра с указанным размером
    public void addMonster(int row, int col, float size) {
        Monster monster = new Monster(null, this, row, col);
        monster.setSize(size);
        monsters.add(monster);
    }

    // Метод для изменения размера всех монстров
    public void setAllMonstersSize(float size) {
        for (Monster monster : monsters) {
            monster.setSize(size);
        }
    }

    // Метод для изменения размера конкретного монстра
    public void setMonsterSize(int index, float size) {
        if (index >= 0 && index < monsters.size()) {
            monsters.get(index).setSize(size);
        }
    }

    public void updateMonsters(float deltaTime) {
        for (Monster monster : monsters) {
            monster.update(deltaTime);
        }
    }

    // Метод для закрашивания плитки когда игрок наступает на неё
    public void colorTile(int row, int col) {
        if (row >= 0 && row < map.length && col >= 0 && col < map[row].length && map[row][col] == 1) {
            coloredTiles[row][col] = true;
            System.out.println("Coloring tile at " + row + "," + col);
            // Проверяем состояние массива после закрашивания
            System.out.println("Current state of coloredTiles[" + row + "][" + col + "] = " + coloredTiles[row][col]);
            // Принудительно обновляем отображение
            repaint();
            // Убеждаемся, что изменения применены
            revalidate();
        } else {
            System.out.println("Failed to color tile at " + row + "," + col + 
                             " (row valid: " + (row >= 0 && row < map.length) + 
                             ", col valid: " + (col >= 0 && col < map[row].length) + 
                             ", is active: " + (map[row][col] == 1) + ")");
        }
    }

    // Method to check if all tiles are colored
    public boolean areAllTilesColored() {
        for (int row = 0; row < map.length; row++) {
            for (int col = 0; col < map[row].length; col++) {
                if (map[row][col] == 1 && !coloredTiles[row][col]) {
                    return false;
                }
            }
        }
        return true;
    }

    public List<Monster> getMonsters() {
        return monsters;
    }

    public Player getPlayer() {
        return playerRef;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        
        // Включаем сглаживание для лучшего качества отображения
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        for (int row = 0; row < map.length; row++) {
            for (int col = 0; col < map[row].length; col++) {
                if (map[row][col] == 1) {
                    int x = xOffset + col * cubeSize - (int)(row * cubeSize * horizontalSpacing);
                    int y = yOffset + (int)(row * cubeSize * verticalSpacing);
                    
                    // Проверяем состояние текстуры перед отрисовкой
                    if (cubeTexture == null || coloredCubeTexture == null) {
                        System.out.println("Error: Textures not loaded properly");
                        return;
                    }
                    
                    // Draw either colored or uncolored texture
                    if (coloredTiles[row][col]) {
                        g2.drawImage(coloredCubeTexture, x, y, cubeSize, cubeSize, this);
                        System.out.println("Drawing colored texture at " + row + "," + col);
                    } else {
                        g2.drawImage(cubeTexture, x, y, cubeSize, cubeSize, this);
                    }
                }
            }
        }

        // Draw monsters
        for (Monster monster : monsters) {
            monster.draw(g2);
        }

        int playerX = xOffset + playerCol * cubeSize - playerRow * cubeSize / 2 + 10;
        int playerY = yOffset + playerRow * cubeSize - 10;
        g2.drawImage(player, playerX, playerY, 40, 40, this);
    }
}



