package org.example.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Frame extends JFrame {
    private int columns = 0;
    private int rows = 0;

    public Frame() {
        super();
        this.setTitle("Linetris");
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setVisible(true);
    }

    public void setDimensions(int columns, int rows) {
        this.setLayout(new GridLayout(rows, columns));
        this.columns = columns;
        this.rows = rows;
        for(int y = rows; y > 0; y--) {
            for(int x = 0; x < columns; x++) {
                Button button = new Button(x + " " + y);
                int finalY = y;
                int finalX = x;
                button.addActionListener(e -> {
                    System.out.println(finalX + " " + finalY);
                });
                button.setSize(100, 100);
                this.add(button);
            }
        }
        this.pack();
    }

    public void updateButton(int column, int row, Color color) {
        this.getComponent((rows - row -1) * columns + column).setBackground(color);
    }
}
