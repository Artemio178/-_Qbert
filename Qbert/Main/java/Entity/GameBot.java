package Entity;

import Main.GamePanel;
import Main.Map;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;

public class GameBot {
    private GamePanel gp;
    private Map map;
    private int currentRow;
    private int currentCol;
    private boolean[][] visitedTiles;
    private List<int[]> pathToNextTile;
    private boolean isMoving;
    private float moveTimer;
    private float moveDelay = 0.5f; // Задержка между движениями в секундах
    
    // Спрайты для бота
    private BufferedImage upleft, upright, downleft, downright;
    private String direction = "down";
    private boolean isFirstMove = true;  // Добавляем флаг для первого хода

    public GameBot(GamePanel gp, Map map) {
        this.gp = gp;
        this.map = map;
        this.currentRow = 0;
        this.currentCol = 0;
        this.visitedTiles = new boolean[map.map.length][];
        for (int i = 0; i < map.map.length; i++) {
            visitedTiles[i] = new boolean[map.map[i].length];
        }
        this.pathToNextTile = new ArrayList<>();
        this.isMoving = false;
        this.moveTimer = 0;
        this.isFirstMove = true;
        
        // Загрузка спрайтов
        getBotImage();
    }

    private void getBotImage() {
        try {
            upleft = ImageIO.read(getClass().getClassLoader().getResourceAsStream("player/playerupleft.png"));
            upright = ImageIO.read(getClass().getClassLoader().getResourceAsStream("player/playerupright.png"));
            downleft = ImageIO.read(getClass().getClassLoader().getResourceAsStream("player/playerleftdown.png"));
            downright = ImageIO.read(getClass().getClassLoader().getResourceAsStream("player/playerdownright.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void update(float deltaTime) {
        if (isFirstMove) {
            // Первый ход - движение вниз
            moveTimer += deltaTime;
            if (moveTimer >= moveDelay) {
                moveTimer = 0;
                currentRow = 1;
                currentCol = 0;
                direction = "down";
                visitedTiles[currentRow][currentCol] = true;
                // Закрашиваем начальную клетку
                map.colorTile(0, 0);
                // Закрашиваем клетку после первого хода
                map.colorTile(currentRow, currentCol);
                isFirstMove = false;
                System.out.println("Bot: Made first move down to " + currentRow + "," + currentCol);
            }
            return;
        }

        if (!isMoving) {
            // Находим следующий непосещенный куб
            int[] nextTile = findNextUnvisitedTile();
            if (nextTile != null) {
                System.out.println("Bot: Found next tile at " + nextTile[0] + "," + nextTile[1]);
                // Строим путь к следующему кубу
                pathToNextTile = findPathToTile(nextTile[0], nextTile[1]);
                System.out.println("Bot: Path length: " + pathToNextTile.size());
                isMoving = true;
            } else {
                System.out.println("Bot: No more unvisited tiles found");
            }
        } else {
            moveTimer += deltaTime;
            if (moveTimer >= moveDelay) {
                moveTimer = 0;
                if (!pathToNextTile.isEmpty()) {
                    // Берем следующую точку из пути
                    int[] nextMove = pathToNextTile.remove(0);
                    // Обновляем направление движения
                    updateDirection(nextMove[0], nextMove[1]);
                    // Закрашиваем текущую клетку перед движением
                    map.colorTile(currentRow, currentCol);
                    currentRow = nextMove[0];
                    currentCol = nextMove[1];
                    System.out.println("Bot: Moving to " + currentRow + "," + currentCol);
                    // Отмечаем куб как посещенный
                    visitedTiles[currentRow][currentCol] = true;
                    // Закрашиваем новую клетку
                    map.colorTile(currentRow, currentCol);
                } else {
                    System.out.println("Bot: Finished current path");
                    isMoving = false;
                }
            }
        }
    }

    private void updateDirection(int nextRow, int nextCol) {
        if (nextRow > currentRow) {
            direction = "down";
        } else if (nextRow < currentRow) {
            direction = "up";
        } else if (nextCol > currentCol) {
            direction = "right";
        } else if (nextCol < currentCol) {
            direction = "left";
        }
    }

    private int[] findNextUnvisitedTile() {
        // Ищем ближайший непосещенный куб
        int minDistance = Integer.MAX_VALUE;
        int[] closestTile = null;

        System.out.println("Bot: Current position: " + currentRow + "," + currentCol);
        System.out.println("Bot: Searching for unvisited tiles...");

        for (int row = 0; row < map.map.length; row++) {
            for (int col = 0; col < map.map[row].length; col++) {
                if (map.map[row][col] == 1 && !visitedTiles[row][col]) {
                    // Вычисляем расстояние до текущей позиции
                    int distance = Math.abs(row - currentRow) + Math.abs(col - currentCol);
                    System.out.println("Bot: Found unvisited tile at " + row + "," + col + " with distance " + distance);
                    
                    // Проверяем, есть ли путь к этой плитке
                    if (hasPathToTile(row, col)) {
                        if (distance < minDistance) {
                            minDistance = distance;
                            closestTile = new int[]{row, col};
                            System.out.println("Bot: New closest tile found at " + row + "," + col);
                        }
                    } else {
                        System.out.println("Bot: No path to tile at " + row + "," + col);
                    }
                }
            }
        }

        if (closestTile != null) {
            System.out.println("Bot: Selected closest tile at " + closestTile[0] + "," + closestTile[1] + 
                             " with distance " + minDistance);
        } else {
            System.out.println("Bot: No reachable unvisited tiles found!");
        }
        return closestTile;
    }

    private boolean hasPathToTile(int targetRow, int targetCol) {
        // Проверяем, есть ли путь к целевой плитке
        int currentR = currentRow;
        int currentC = currentCol;
        int steps = 0;
        int maxSteps = 100; // Предотвращаем бесконечный цикл
        
        // Безопасная инициализация массива visited
        boolean[][] visited = new boolean[map.map.length][];
        for (int i = 0; i < map.map.length; i++) {
            visited[i] = new boolean[map.map[i].length];
        }

        while ((currentR != targetRow || currentC != targetCol) && steps < maxSteps) {
            visited[currentR][currentC] = true;
            
            // Проверяем все возможные соседние клетки
            int[][] directions = {
                {0, 1},  // вправо
                {1, 0},  // вниз
                {0, -1}, // влево
                {-1, 0}  // вверх
            };

            boolean foundNext = false;
            for (int[] dir : directions) {
                int nextR = currentR + dir[0];
                int nextC = currentC + dir[1];

                // Проверяем, что следующая позиция существует и является активной плиткой
                if (nextR >= 0 && nextR < map.map.length && 
                    nextC >= 0 && nextC < map.map[nextR].length && 
                    map.map[nextR][nextC] == 1 && 
                    !visited[nextR][nextC]) {
                    
                    // Проверяем, приближает ли это нас к цели
                    int currentDist = Math.abs(currentR - targetRow) + Math.abs(currentC - targetCol);
                    int nextDist = Math.abs(nextR - targetRow) + Math.abs(nextC - targetCol);
                    
                    if (nextDist <= currentDist) {
                        currentR = nextR;
                        currentC = nextC;
                        foundNext = true;
                        break;
                    }
                }
            }

            if (!foundNext) {
                // Если не нашли подходящего следующего шага, пробуем любой непосещенный соседний
                for (int[] dir : directions) {
                    int nextR = currentR + dir[0];
                    int nextC = currentC + dir[1];

                    if (nextR >= 0 && nextR < map.map.length && 
                        nextC >= 0 && nextC < map.map[nextR].length && 
                        map.map[nextR][nextC] == 1 && 
                        !visited[nextR][nextC]) {
                        
                        currentR = nextR;
                        currentC = nextC;
                        foundNext = true;
                        break;
                    }
                }
            }

            if (!foundNext) {
                return false; // Не можем найти путь
            }

            steps++;
        }

        return currentR == targetRow && currentC == targetCol;
    }

    private List<int[]> findPathToTile(int targetRow, int targetCol) {
        List<int[]> path = new ArrayList<>();
        int currentR = currentRow;
        int currentC = currentCol;
        
        // Безопасная инициализация массива visited
        boolean[][] visited = new boolean[map.map.length][];
        for (int i = 0; i < map.map.length; i++) {
            visited[i] = new boolean[map.map[i].length];
        }

        System.out.println("Bot: Finding path from " + currentR + "," + currentC + " to " + targetRow + "," + targetCol);

        while (currentR != targetRow || currentC != targetCol) {
            visited[currentR][currentC] = true;
            
            // Проверяем все возможные соседние клетки
            int[][] directions = {
                {0, 1},  // вправо
                {1, 0},  // вниз
                {0, -1}, // влево
                {-1, 0}  // вверх
            };

            boolean foundNext = false;
            for (int[] dir : directions) {
                int nextR = currentR + dir[0];
                int nextC = currentC + dir[1];

                if (nextR >= 0 && nextR < map.map.length && 
                    nextC >= 0 && nextC < map.map[nextR].length && 
                    map.map[nextR][nextC] == 1 && 
                    !visited[nextR][nextC]) {
                    
                    // Проверяем, приближает ли это нас к цели
                    int currentDist = Math.abs(currentR - targetRow) + Math.abs(currentC - targetCol);
                    int nextDist = Math.abs(nextR - targetRow) + Math.abs(nextC - targetCol);
                    
                    if (nextDist <= currentDist) {
                        path.add(new int[]{nextR, nextC});
                        currentR = nextR;
                        currentC = nextC;
                        foundNext = true;
                        System.out.println("Bot: Added step to path: " + nextR + "," + nextC);
                        break;
                    }
                }
            }

            if (!foundNext) {
                // Если не нашли оптимального пути, пробуем любой непосещенный соседний
                for (int[] dir : directions) {
                    int nextR = currentR + dir[0];
                    int nextC = currentC + dir[1];

                    if (nextR >= 0 && nextR < map.map.length && 
                        nextC >= 0 && nextC < map.map[nextR].length && 
                        map.map[nextR][nextC] == 1 && 
                        !visited[nextR][nextC]) {
                        
                        path.add(new int[]{nextR, nextC});
                        currentR = nextR;
                        currentC = nextC;
                        foundNext = true;
                        System.out.println("Bot: Added alternative step to path: " + nextR + "," + nextC);
                        break;
                    }
                }
            }

            if (!foundNext) {
                System.out.println("Bot: Cannot find path to target");
                return path;
            }
        }

        System.out.println("Bot: Path length: " + path.size());
        return path;
    }

    public void draw(Graphics2D g2) {
        int x = map.xOffset + currentCol * map.cubeSize - (int)(currentRow * map.cubeSize * map.horizontalSpacing);
        int y = map.yOffset + (int)(currentRow * map.cubeSize * map.verticalSpacing);
        
        // Выбираем спрайт в зависимости от направления
        BufferedImage currentSprite = downright; // По умолчанию
        switch (direction) {
            case "up":
                if (currentCol < map.map[currentRow].length / 2) {
                    currentSprite = upleft;
                } else {
                    currentSprite = upright;
                }
                break;
            case "down":
                if (currentCol < map.map[currentRow].length / 2) {
                    currentSprite = downleft;
                } else {
                    currentSprite = downright;
                }
                break;
        }
        
        // Отрисовка бота
        g2.drawImage(currentSprite, x + 10, y - 10, 40, 40, null);
    }

    public void setMap(Map newMap) {
        this.map = newMap;
        // Пересоздаем массив visitedTiles для новой карты
        this.visitedTiles = new boolean[map.map.length][];
        for (int i = 0; i < map.map.length; i++) {
            visitedTiles[i] = new boolean[map.map[i].length];
        }
        // Сбрасываем текущий путь
        pathToNextTile.clear();
        isMoving = false;
        moveTimer = 0;
    }

    public void reset() {
        currentRow = 0;
        currentCol = 0;
        for (int i = 0; i < visitedTiles.length; i++) {
            for (int j = 0; j < visitedTiles[i].length; j++) {
                visitedTiles[i][j] = false;
            }
        }
        pathToNextTile.clear();
        isMoving = false;
        moveTimer = 0;
        direction = "down";
        isFirstMove = true;  // Сбрасываем флаг первого хода
    }

    public int getCurrentRow() {
        return currentRow;
    }

    public int getCurrentCol() {
        return currentCol;
    }
} 