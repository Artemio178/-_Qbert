package Main;

import java.awt.event.KeyEvent;

import javax.swing.JPanel;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class KeyHandlerTest {
    private KeyHandler keyHandler;
    private JPanel source;

    @BeforeEach
    void setUp() {
        keyHandler = new KeyHandler();
        source = new JPanel();
    }

    @Test
    void testInitialState() {
        assertFalse(keyHandler.upPressed);
        assertFalse(keyHandler.downPressed);
        assertFalse(keyHandler.leftPressed);
        assertFalse(keyHandler.rightPressed);
        assertFalse(keyHandler.qPressed);
        assertFalse(keyHandler.ePressed);
    }

    @Test
    void testKeyPressed() {
        // Тестируем нажатие клавиш
        KeyEvent wEvent = new KeyEvent(source, KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0, KeyEvent.VK_W);
        keyHandler.keyPressed(wEvent);
        assertTrue(keyHandler.upPressed);

        KeyEvent sEvent = new KeyEvent(source, KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0, KeyEvent.VK_S);
        keyHandler.keyPressed(sEvent);
        assertTrue(keyHandler.downPressed);

        KeyEvent aEvent = new KeyEvent(source, KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0, KeyEvent.VK_A);
        keyHandler.keyPressed(aEvent);
        assertTrue(keyHandler.leftPressed);

        KeyEvent dEvent = new KeyEvent(source, KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0, KeyEvent.VK_D);
        keyHandler.keyPressed(dEvent);
        assertTrue(keyHandler.rightPressed);

        KeyEvent qEvent = new KeyEvent(source, KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0, KeyEvent.VK_Q);
        keyHandler.keyPressed(qEvent);
        assertTrue(keyHandler.qPressed);

        KeyEvent eEvent = new KeyEvent(source, KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0, KeyEvent.VK_E);
        keyHandler.keyPressed(eEvent);
        assertTrue(keyHandler.ePressed);
    }

    @Test
    void testKeyReleased() {
        // Сначала нажимаем клавиши
        keyHandler.keyPressed(new KeyEvent(source, KeyEvent.KEY_PRESSED, 0, 0, KeyEvent.VK_UP));
        keyHandler.keyPressed(new KeyEvent(source, KeyEvent.KEY_PRESSED, 0, 0, KeyEvent.VK_DOWN));
        
        // Затем отпускаем их
        keyHandler.keyReleased(new KeyEvent(source, KeyEvent.KEY_RELEASED, 0, 0, KeyEvent.VK_UP));
        keyHandler.keyReleased(new KeyEvent(source, KeyEvent.KEY_RELEASED, 0, 0, KeyEvent.VK_DOWN));
        
        assertFalse(keyHandler.upPressed);
        assertFalse(keyHandler.downPressed);
    }

    @Test
    void testAnyKeyPressed() {
        assertFalse(keyHandler.anyKeyPressed());
        
        KeyEvent wEvent = new KeyEvent(source, KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0, KeyEvent.VK_W);
        keyHandler.keyPressed(wEvent);
        assertTrue(keyHandler.anyKeyPressed());
        
        KeyEvent wReleaseEvent = new KeyEvent(source, KeyEvent.KEY_RELEASED, System.currentTimeMillis(), 0, KeyEvent.VK_W);
        keyHandler.keyReleased(wReleaseEvent);
        assertFalse(keyHandler.anyKeyPressed());
    }
} 