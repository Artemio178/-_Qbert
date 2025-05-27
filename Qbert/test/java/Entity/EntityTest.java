package Entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import Main.GamePanel;

public class EntityTest {
    private Entity entity;
    private GamePanel gamePanel;

    @BeforeEach
    void setUp() {
        gamePanel = new GamePanel();
        entity = new Entity(gamePanel);
    }

    @Test
    void testInitialState() {
        assertFalse(entity.isJumping);
        assertEquals(0, entity.jumpProgress);
        assertEquals(0, entity.startX);
        assertEquals(0, entity.startY);
        assertEquals(0, entity.endX);
        assertEquals(0, entity.endY);
        assertEquals(0.3f, entity.jumpDuration);
        assertEquals(10f, entity.jumpHeight);
    }

    @Test
    void testStartJump() {
        entity.startJump(1, 1);
        assertTrue(entity.isJumping);
        assertEquals(0, entity.jumpProgress);
        assertEquals(0, entity.startX);
        assertEquals(0, entity.startY);
        assertEquals(gamePanel.tileSize, entity.endX);
        assertEquals(gamePanel.tileSize, entity.endY);
    }

    @Test
    void testLerp() {
        assertEquals(5, entity.lerp(0, 10, 0.5f));
    }
} 