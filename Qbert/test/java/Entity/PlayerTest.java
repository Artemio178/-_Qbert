package Entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import Main.GamePanel;
import Main.KeyHandler;

public class PlayerTest {
    private Player player;
    private GamePanel gamePanel;
    private KeyHandler keyHandler;

    @BeforeEach
    void setUp() {
        gamePanel = new GamePanel();
        keyHandler = new KeyHandler();
        player = new Player(gamePanel, keyHandler);
    }

    @Test
    void testInitialState() {
        assertTrue(player.isAlive());
        assertEquals(1, player.getCurrentHealth());
        assertEquals(0, player.getMapRow());
        assertEquals(0, player.getMapCol());
    }

    @Test
    void testTakeDamage() {
        player.takeDamage(1);
        assertEquals(0, player.getCurrentHealth());
        assertFalse(player.isAlive());
    }

    @Test
    void testSetMapPosition() {
        player.setMapPosition(2, 3);
        assertEquals(2, player.getMapRow());
        assertEquals(3, player.getMapCol());
    }

    @Test
    void testSetDefaultValues() {
        player.setMapPosition(5, 5);
        player.setDefaultValues();
        assertEquals(0, player.getMapRow());
        assertEquals(0, player.getMapCol());
    }

    @Test
    void testSetAlive() {
        player.setAlive(false);
        assertFalse(player.isAlive());
        player.setAlive(true);
        assertTrue(player.isAlive());
    }

    @Test
    void testUpdate() {
        // Проверяем обновление состояния игрока
        player.update(0.016f);
        // Проверяем, что обновление прошло без ошибок
        assertTrue(true);
    }

    @Test
    void testGetDisplaySize() {
        assertTrue(player.getDisplaySize() > 0);
    }
} 