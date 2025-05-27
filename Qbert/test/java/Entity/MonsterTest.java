package Entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import Main.GamePanel;
import Main.Map;

public class MonsterTest {
    private Monster monster;
    private GamePanel gamePanel;
    private Map map;

    @BeforeEach
    void setUp() {
        gamePanel = new GamePanel();
        map = new Map();
        monster = new Monster(gamePanel, map, 0, 0);
    }

    @Test
    void testInitialState() {
        assertEquals(0, monster.getMapRow());
        assertEquals(0, monster.getMapCol());
        assertEquals(1, monster.getCurrentHealth());
        assertTrue(monster.isAlive());
    }

    @Test
    void testTakeDamage() {
        monster.takeDamage(1);
        assertEquals(0, monster.getCurrentHealth());
        assertFalse(monster.isAlive());
    }

    @Test
    void testSetMapPosition() {
        monster.setMapPosition(2, 3);
        assertEquals(2, monster.getMapRow());
        assertEquals(3, monster.getMapCol());
    }

    @Test
    void testStartJump() {
        monster.startJump(1, 1);
        assertTrue(monster.isJumping);
        assertEquals(0, monster.jumpProgress);
    }

    @Test
    void testDisplaySize() {
        assertEquals(40, monster.getDisplaySize());
    }
} 