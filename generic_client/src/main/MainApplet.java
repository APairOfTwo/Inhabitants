package main;

import game_connection.Constantes;
import game_connection.SocketConectionManager;
import java.awt.BorderLayout;
import java.awt.Container;
import javax.swing.JApplet;


public class MainApplet extends JApplet {
	
	public static MainCanvas mpanel = null;
	
	@Override
	public void init() {
		super.init();
		
		String tudo = "";
		System.out.println("ININICANDO.....");
		
		Constantes.connectionManager = new SocketConectionManager();
		Constantes.connectionManager.connect();
		System.out.println("CONECTOU");

		Constantes.loadStatus();
		
		mpanel = new MainCanvas();
		
		setSize(MainCanvas.PWIDTH, MainCanvas.PHEIGHT);
		Container c = getContentPane();
		c.setLayout(new BorderLayout());
		c.add(mpanel, BorderLayout.CENTER);

		setVisible(true);
	}
	
}
