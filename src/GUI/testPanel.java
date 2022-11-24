package GUI;

import javax.swing.*;
import java.awt.*;

public class testPanel extends JPanel {

    public static void main(String[] args) {
        //frame
        JFrame frame = new JFrame();

        GUIBoard guiBoard = new GUIBoard();
        guiBoard.loadImage();

        GamePanel panel = new GamePanel();
        frame.add(panel);

        frame.setBounds(400, 120, 512, 512);
        frame.setUndecorated(true);
        frame.setDefaultCloseOperation(3);
        frame.setVisible(true);
    }
}