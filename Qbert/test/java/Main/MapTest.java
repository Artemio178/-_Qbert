package Main;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import Entity.Monster;
import Entity.Player;

public class MapTest {
    private Map map;
    private Player player;

    @BeforeEach
    void setUp() {
        map = new Map();
        player = new Player(null, null);
        map.setPlayer(player);
    }

    @Test
    void testInitialMapState() {
        assertNotNull(map.map);
        assertTrue(map.map.length > 0);
        assertTrue(map.map[0].length > 0);
    }

    @Test
    void testAddMonster() {
        map.clearMonsters();
        map.addMonster(2, 1, 0.8f);
        assertEquals(1, map.getMonsters().size());
        Monster monster = map.getMonsters().get(0);
        assertEquals(2, monster.mapRow);
        assertEquals(1, monster.mapCol);
    }

    @Test
    void testClearMonsters() {
        map.addMonster(2, 1, 0.8f);
        map.addMonster(3, 1, 0.8f);
        assertEquals(2, map.getMonsters().size());
        map.clearMonsters();
        assertEquals(0, map.getMonsters().size());
    }

    @Test
    void testColorTile() {
        // Убедимся, что карта инициализирована
        assertNotNull(map.map);
        assertTrue(map.map.length > 0);
        
        // Закрашиваем все плитки
        for (int row = 0; row < map.map.length; row++) {
            for (int col = 0; col < map.map[row].length; col++) {
                if (map.map[row][col] == 1) {
                    map.colorTile(row, col);
                }
            }
        }
        
        // Проверяем, что все плитки закрашены
        assertTrue(map.areAllTilesColored());
    }

    @Test
    void testSetPlayer() {
        map.setPlayer(player);
        assertEquals(player, map.getPlayer());
    }

    @Test
    void testUpdateMonsters() {
        map.addMonster(2, 1, 0.8f);
        map.updateMonsters(0.016f); // Обновляем с дельтой времени
        // Проверяем, что монстры обновились без ошибок
        assertTrue(true);
    }

    @Test
    void testAreAllTilesColored() {
        // Изначально не все плитки закрашены
        assertFalse(map.areAllTilesColored());
        
        // Закрашиваем все плитки
        for (int row = 0; row < map.map.length; row++) {
            for (int col = 0; col < map.map[row].length; col++) {
                if (map.map[row][col] == 1) {
                    map.colorTile(row, col);
                }
            }
        }
        
        assertTrue(map.areAllTilesColored());
    }
} 