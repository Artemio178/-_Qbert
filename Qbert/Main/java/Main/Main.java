package Main;


import javax.swing.*;

public class Main {
    private static JFrame window;


    public static void main(String[] args) {
        JFrame window = new JFrame();

        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //window.add(panel);
        window.setResizable(false);
        window.setTitle("Qbert");
        GamePanel gamePanel = new GamePanel();

        window.add(gamePanel);
        window.pack();

        window.setLocationRelativeTo(null);
        window.setVisible(true);

        gamePanel.startGameThread();

    }
}


// mvn clean compile exec:java -Dexec.mainClass="Main.Main"