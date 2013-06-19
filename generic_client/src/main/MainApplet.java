package main;
import game_connection.Constantes;
import game_connection.SocketConectionManager;

import java.awt.BorderLayout;
import java.awt.Container;

import javax.swing.JApplet;
import javax.swing.JFrame;


public class MainApplet extends JApplet {
	
	public static MainCanvas mpanel = null;
	
	@Override
	public void init() {
		// TODO Auto-generated method stub
		super.init();
		
		String tudo = "";
		System.out.println("ININICANDO.....");
		
		Constantes.connectionManager = new SocketConectionManager();
		Constantes.connectionManager.connect();
		System.out.println("CONECTOU");

		Constantes.loadStatus();
		
		
		mpanel = new MainCanvas();
		
		// create a JFrame to hold the timer test JPanel
		//JFrame app = new JFrame("Swing Timer Test");

		setSize(MainCanvas.PWIDTH, MainCanvas.PHEIGHT);
		Container c = getContentPane();
		c.setLayout(new BorderLayout());
		c.add(mpanel, BorderLayout.CENTER);

		//mpanel.startGame();
		setVisible(true);
		
		//mpanel.stopGame();
	}
	
}
