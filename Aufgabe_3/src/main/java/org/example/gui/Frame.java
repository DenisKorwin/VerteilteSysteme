package org.example.gui;

import javax.swing.*;

import org.example.GameManager;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

public class Frame extends JFrame {
    private int columns = 0;
    private int rows = 0;
    private GameManager gameManager;
    private Button[][] buttons;

    public Frame(GameManager gameManager) {
        super();
        this.setTitle("Linetris");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
        
        this.gameManager = gameManager;
    }

    public void setDimensions(int columns, int rows) {
        this.setLayout(new GridLayout(rows, columns));
        this.columns = columns;
        this.rows = rows;
        buttons = new Button[columns][rows];
        for(int y = rows - 1; y >= 0; y--) {
            for(int x = 0; x < columns; x++) {
                Button button = new Button(x + " " + y);
                int finalY = y;
                int finalX = x;
                button.addActionListener(e -> {
                    gameManager.sendMove(finalX);
                });
                button.setPreferredSize(new Dimension((int) Toolkit.getDefaultToolkit().getScreenSize().getWidth() / (2*columns + 5), (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight() / (2*rows + 5)));
                buttons[x][y] = button;
                this.add(button);
            }
        }
        this.pack();
    }

    public void updateButton(int column, int row, Color color) {
    	buttons[column][row].setBackground(color);
    }
}
