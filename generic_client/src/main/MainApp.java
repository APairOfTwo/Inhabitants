package main;
import game_connection.Constantes;
import game_connection.SocketConectionManager;

import java.awt.BorderLayout;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Hashtable;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.plaf.basic.BasicInternalFrameTitlePane.MaximizeAction;

public class MainApp {

	public static MainCanvas mpanel = null;

	public static void main(String[] args) {
		String tudo = "";
		System.out.println("ININICANDO.....");
		
		Constantes.connectionManager = new SocketConectionManager();
		Constantes.connectionManager.connect();
		System.out.println("CONECTOU");

		Constantes.loadStatus();
		
		mpanel = new MainCanvas();

		
		// create a JFrame to hold the timer test JPanel
		JFrame app = new JFrame("Swing Timer Test");
		app.getContentPane().add(mpanel, BorderLayout.CENTER);
		app.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		app.pack();
		app.setResizable(false);
		app.setVisible(true);
		
		//mpanel.stopGame();
	}

}
