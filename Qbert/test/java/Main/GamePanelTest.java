package Main;

import java.awt.event.KeyEvent;

import javax.swing.JPanel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class GamePanelTest {
    private GamePanel gamePanel;
    private JPanel source;

    @BeforeEach
    void setUp() {
        gamePanel = new GamePanel();
        source = new JPanel();
    }

    @Test
    void testInitialState() {
        assertNotNull(gamePanel.map);
        assertNotNull(gamePanel.player);
        assertEquals(1, gamePanel.currentLevel);
    }

    @Test
    void testSetupLevel() {
        gamePanel.setupLevel();
        assertNotNull(gamePanel.map);
        assertTrue(gamePanel.map.getMonsters().size() > 0);
    }

    @Test
    void testSaveAndLoadGameProgress() {
        // Сохраняем прогресс
        gamePanel.currentLevel = 3;
        gamePanel.saveGameProgress();
        
        // Сбрасываем уровень
        gamePanel.currentLevel = 1;
        
        // Загружаем прогресс
        gamePanel.loadGameProgress();
        assertEquals(3, gamePanel.currentLevel);
    }

    @Test
    void testGameStateTransitions() {
        // Проверяем начальное состояние
        assertEquals(GamePanel.GameState.START_SCREEN, gamePanel.gameState);
        
        // Проверяем переход к выбору режима
        gamePanel.keyH.keyPressed(new KeyEvent(source, KeyEvent.KEY_PRESSED, 0, 0, KeyEvent.VK_W));
        gamePanel.update();
        assertEquals(GamePanel.GameState.MODE_SELECTION, gamePanel.gameState);
    }

    @Test
    void testPlayerDeath() {
        // Устанавливаем состояние игры в PLAYING
        gamePanel.gameState = GamePanel.GameState.PLAYING;
        
        // Устанавливаем игрока в состояние смерти
        gamePanel.player.setAlive(false);
        gamePanel.update();
        
        // Проверяем, что игра перешла в состояние экрана смерти
        assertEquals(GamePanel.GameState.DEATH_SCREEN, gamePanel.gameState);
    }

    @Test
    void testLevelCompletion() {
        // Устанавливаем состояние игры в PLAYING
        gamePanel.gameState = GamePanel.GameState.PLAYING;
        
        // Закрашиваем все плитки
        for (int row = 0; row < gamePanel.map.map.length; row++) {
            for (int col = 0; col < gamePanel.map.map[row].length; col++) {
                if (gamePanel.map.map[row][col] == 1) {
                    gamePanel.map.colorTile(row, col);
                }
            }
        }
        
        // Проверяем, что все плитки закрашены
        assertTrue(gamePanel.map.areAllTilesColored());
        
        // Обновляем состояние игры
        gamePanel.update();
        
        // Проверяем, что уровень увеличился
        assertEquals(2, gamePanel.currentLevel);
    }
} 