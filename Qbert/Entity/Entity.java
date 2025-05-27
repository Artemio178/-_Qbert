package Entity;

import java.awt.image.BufferedImage;

public class Entity {

    boolean isJumping = false;
    float jumpProgress = 0;
    float startX, startY;
    float endX, endY;
    float jumpDuration = 0.3f; // длина прыжка в секундах (можно подстроить)
    float jumpHeight = 10f;    // высота дуги прыжка




    public float x, y;
    public int speed;

    public BufferedImage upleft, upright, downleft, downright;
    public String direction;

}
