package Main;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyHandler implements KeyListener {

    public boolean upPressed, downPressed, leftPressed, rightPressed;
    public boolean enterPressed; // For mode selection
    public boolean qPressed, ePressed; // For mode selection
    public int selectedOption = 0; // 0 for manual play, 1 for bot play

    @Override
    public void keyTyped(KeyEvent e) {

    }


    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();

        if (code == KeyEvent.VK_W) {
            upPressed = true;

        }
        if (code == KeyEvent.VK_S) {
            downPressed = true;

        }
        if (code == KeyEvent.VK_A) {
            leftPressed = true;

        }
        if (code == KeyEvent.VK_D) {
            rightPressed = true;

        }
        if (code == KeyEvent.VK_ENTER) {
            enterPressed = true;
        }
        if (code == KeyEvent.VK_Q) {
            qPressed = true;
        }
        if (code == KeyEvent.VK_E) {
            ePressed = true;
        }
        if (code == KeyEvent.VK_UP) {
            selectedOption = Math.max(0, selectedOption - 1);
            System.out.println("Selected option: " + selectedOption);
        }
        if (code == KeyEvent.VK_DOWN) {
            selectedOption = Math.min(1, selectedOption + 1);
            System.out.println("Selected option: " + selectedOption);
        }

    }


        @Override
        public void keyReleased (KeyEvent e) {
            int code = e.getKeyCode();

            if (code == KeyEvent.VK_W) {
                upPressed = false;


            }
            if (code == KeyEvent.VK_S) {
                downPressed = false;

            }
            if (code == KeyEvent.VK_A) {
                leftPressed = false;

            }
            if (code == KeyEvent.VK_D) {
                rightPressed = false;


            }
            if (code == KeyEvent.VK_ENTER) {
                enterPressed = false;
            }
            if (code == KeyEvent.VK_Q) {
                qPressed = false;
            }
            if (code == KeyEvent.VK_E) {
                ePressed = false;
            }

        }

    // Метод для проверки нажатия любой клавиши
    public boolean anyKeyPressed() {
        return upPressed || downPressed || leftPressed || rightPressed || enterPressed || qPressed || ePressed;
    }
    }

