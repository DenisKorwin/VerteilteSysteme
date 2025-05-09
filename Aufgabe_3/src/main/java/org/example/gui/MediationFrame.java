package org.example.gui;

import java.awt.Button;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.TextField;
import java.util.UUID;

import javax.swing.JFrame;

import org.example.GameManager;
import org.example.GameMediator;
import org.example.jsonMapping.Client;
import org.example.jsonMapping.Player;

public class MediationFrame extends JFrame {
	private GameMediator gameMediator;
	
	private TextField playerName;
	private TextField clientName;
	private Button p1Start;
	private Button p2Start;
	
	public MediationFrame(GameMediator gameMediator) {
		super();
        this.setTitle("Linetris - Spielsuche");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
        
        this.gameMediator = gameMediator;
        
        this.setLayout(new GridLayout(4, 1));
        
        playerName = new TextField("player");
        this.add(playerName);
        
        clientName = new TextField("client");
        this.add(clientName);
        
        p1Start = new Button("Spiel starten");
        p1Start.setForeground(Color.WHITE);
        p1Start.setBackground(Color.RED);
        p1Start.addActionListener(e -> {
        	gameMediator.proposeGame(UUID.randomUUID(), new Player(playerName.getText()), new Client(clientName.getText()));
        	this.setVisible(false);
        });
        this.add(p1Start);
        
        p2Start = new Button("Auf Spiel warten");
        p2Start.setForeground(Color.WHITE);
        p2Start.setBackground(Color.BLUE);
        p2Start.addActionListener(e -> {
        	gameMediator.waitForProposal(new Player(playerName.getText()), new Client(clientName.getText()));
        	this.setVisible(false);
        });
        this.add(p2Start);
        
        this.pack();
        this.setVisible(true);
	}
}
