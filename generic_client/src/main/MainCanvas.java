package main;
import game_connection.Constantes;
import game_connection.SocketConectionManager;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.plaf.SliderUI;


public class MainCanvas extends Canvas implements Runnable
	{
	public static final int PWIDTH = 400;
	public static final int PHEIGHT = 400;
	
	public static MainCanvas instance = null;
	
	public Thread animator;
	public boolean running = false;
	public boolean gameOver = false; 

    public Graphics2D dbg = null;
    public BufferStrategy strategy = null;

	int FPS,SFPS;
	int fpscount;
	
	int respawnTime = 2000;
	
	public ArrayList<Personagem> listaDePersonagens = new ArrayList<Personagem>();
	public ArrayList<Projetil> listaDeProjetil = new ArrayList<Projetil>();
	
	public static Random rnd = new Random();
	//BufferedImage imagemcharsets;
	//BufferedImage fundo;

	boolean LEFT, RIGHT,UP,DOWN, FIRE;

	int MouseX,MouseY;

	public BufferedImage ultimaImagemBaixada = null;
	public String  ultimaMSGServer = "";

	public MainCanvas()
	{
		instance = this;
		
		setBackground(Color.white);
		setPreferredSize( new Dimension(PWIDTH, PHEIGHT));

		// create game components
		setFocusable(true);

		requestFocus(); // JPanel now receives key events

		// Adiciona um Key Listner
		addKeyListener( new KeyAdapter() {
			public void keyPressed(KeyEvent e)
				{ 
					int key = e.getKeyCode();
					if(key == KeyEvent.VK_F1){
						 String usuario = JOptionPane.showInputDialog(null, "Usuario : ", "Login", 1);
						 String senha = JOptionPane.showInputDialog(null, "Senha : ", "Login", 1);
						 
						 if(Constantes.connectionManager!=null&&Constantes.connectionManager.socket.isConnected()){
							 Constantes.connectionManager.sendMsgLogin(usuario, senha);
						 }
					}
					
					if(key == KeyEvent.VK_F2){
						if(Constantes.connectionManager!=null&&Constantes.connectionManager.socket.isConnected()){
							Constantes.connectionManager.sendMsgPedeArquivo("ImagenNet.jpg", rnd.nextInt());
						}
					}

				}
			@Override
				public void keyReleased(KeyEvent e ) {

				}
		});
		
		addMouseMotionListener(new MouseMotionListener() {
			
			@Override
			public void mouseMoved(MouseEvent e) {
				// TODO Auto-generated method stub
			}
			
			@Override
			public void mouseDragged(MouseEvent e) {
				// TODO Auto-generated method stub
			}
		});
		
		addMouseListener(new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent e) {
				FIRE = false;
			}
			
			@Override
			public void mousePressed(MouseEvent e) {
				MouseX = e.getX();
				MouseY = e.getY();
				
				if(e.getButton()==1){
					if(Constantes.logado==true){
						Constantes.meuPersonagem.objetivoX = MouseX;
						Constantes.meuPersonagem.objetivoY = MouseY;
						if(Constantes.connectionManager.socket.isConnected()){
							Constantes.connectionManager.sendMsgMovimentaPersonagem(Constantes.meuPersonagem.X, Constantes.meuPersonagem.Y, Constantes.meuPersonagem.objetivoX, Constantes.meuPersonagem.objetivoY);
						}
					}
				}else if(e.getButton()==3){
					FIRE = true;
					if(Constantes.logado==true){
						Constantes.connectionManager.sendMsgtiro(Constantes.meuPersonagem.X, Constantes.meuPersonagem.Y,MouseX,MouseY);
						Constantes.meuPersonagem.atira(Constantes.meuPersonagem.X,  Constantes.meuPersonagem.Y,MouseX,MouseY);
					}
				}
			}
			
			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		
		MouseX = MouseY = 0;
		
	} // end of GamePanel()	

	public void addNotify()
	{
		super.addNotify(); // creates the peer
		startGame(); // start the thread
	}

	public void startGame()
	// initialise and start the thread
	{
		if (animator == null || !running) {
			animator = new Thread(this);
			animator.start();
		}
	} // end of startGame()

	public void stopGame()
	// called by the user to stop execution
	{ running = false; }


	public void run()
	/* Repeatedly update, render, sleep */
	{
		running = true;
		
		long DifTime,TempoAnterior;
		
		int segundo = 0;
		DifTime = 0;
		TempoAnterior = System.currentTimeMillis();
		
		
		
		createBufferStrategy(2);

		strategy = getBufferStrategy();
			
		while(running) {
		
    		gameUpdate(DifTime);
			dbg = (Graphics2D) strategy.getDrawGraphics();
			dbg.setClip(0, 0, PWIDTH, PHEIGHT);
			dbg.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			gameRender();
			dbg.dispose();
			strategy.show();
		
			try {
				Thread.sleep(0); // sleep a bit
			}	
			catch(InterruptedException ex){}
			
			DifTime = System.currentTimeMillis() - TempoAnterior;
			TempoAnterior = System.currentTimeMillis();
			
			if(segundo!=((int)(TempoAnterior/1000))){
				FPS = SFPS;
				SFPS = 1;
				segundo = ((int)(TempoAnterior/1000));
			}else{
				SFPS++;
			}
		
		}
	System.exit(0); // so enclosing JFrame/JApplet exits
	} // end of run()
	
	
	@Override
	public void paint(Graphics arg0) {
		// TODO Auto-generated method stub
//		super.paint(arg0);
		
	}	
	
	@Override
	public void update(Graphics arg0) {
		// TODO Auto-generated method stub
//		super.update(arg0);
	}

	int timerfps = 0;
	
	int pingtimer = 0;
	int timerDownload = 0;

	private void gameUpdate(long diftime)
	{ 
		pingtimer+=diftime;
		
		timerDownload+= diftime;
		
		if(pingtimer>1000){
			if(Constantes.connectionManager!=null){
				if(Constantes.connectionManager.socket.isConnected()){
					Constantes.connectionManager.sendMsgPing();
				}
			}
			pingtimer = 0;
		}
		
		for(int i = 0; i < listaDePersonagens.size();i++){
			listaDePersonagens.get(i).SimulaSe(diftime);
		}
		
		for(int i = 0; i < listaDeProjetil.size();i++){
			listaDeProjetil.get(i).SimulaSe(diftime);
		}
		
		if(Constantes.meuPersonagem != null) {
			if(!Constantes.meuPersonagem.isAlive) {
				Constantes.meuPersonagem.respawnCountTime+=diftime;
				if(Constantes.meuPersonagem.respawnCountTime >= respawnTime) {
					Constantes.meuPersonagem.respawnCountTime = 0;
					Constantes.connectionManager.sendMsgRespawn(100, Constantes.rand.nextInt(300)+50, Constantes.rand.nextInt(300)+50);
				}
			}
		}
	}

	private void gameRender()
	// draw the current frame to an image buffer
	{
		// clear the background
		dbg.setColor(Color.white);
		dbg.fillRect (0, 0, PWIDTH, PHEIGHT);
		
		if(ultimaImagemBaixada!=null){
			dbg.drawImage(ultimaImagemBaixada, 0, 0, PWIDTH, PHEIGHT, 0, 0, ultimaImagemBaixada.getWidth(), ultimaImagemBaixada.getHeight(), null);
		}
		
		for(int i = 0; i < listaDePersonagens.size();i++){
			listaDePersonagens.get(i).DesenhaSe(dbg, 0, 0);
		}
		
		for(int i = 0; i < listaDeProjetil.size();i++){
			listaDeProjetil.get(i).DesenhaSe(dbg, 0, 0);
		}
		
		dbg.setColor(Color.BLUE);
		dbg.drawString("FPS: "+FPS+" "+MouseX+" "+MouseY+" PING: "+SocketConectionManager.lastpingreceived+" "+Constantes.logado, 10, 10);	
		dbg.drawString(ultimaMSGServer, 10, PHEIGHT-30);
	}

	
	public static BufferedImage LoadImageUrl(String endereco){
		URL url = null;
		
		try {
			url = new URL(endereco);
		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		if (url != null) {
			try {
				//System.out.print(" Downloading ");
		        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		        
		        //System.out.println("conection "+yc.getContentType());
		        connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 6.1; en-GB;     rv:1.9.2.13) Gecko/20101203 Firefox/3.6.13 (.NET CLR 3.5.30729)");
		        
		        //TODO nao baixar imgs muito grandes
		        int filesize = connection.getContentLength();
		        System.out.print("FILE SIZE ----> "+filesize);
				if(connection.getContentLength()>850000){
					System.out.println("NAO");
					return null;
				}else{
					System.out.println("SIM");
				}
				
				if(filesize<=0){
					return null;
				}
		        
				InputStream in = connection.getInputStream();//url.openStream();
				
				//System.out.println("FILE SIZE ----> "+connection.getContentLength());
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				// fout = new FileOutputStream(filename);

				byte bb[] = new byte[50000];
				int size = -1;

				while ((size = in.read(bb)) != -1) {
					baos.write(bb, 0, size);
				}

				//System.out.print("OK");
				//fout.close();
				
				InputStream inimage = new ByteArrayInputStream(baos.toByteArray());
				BufferedImage image = ImageIO.read(inimage);
				
				return image;
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				System.out.println("ERRO DOWNLOAD IMAGE: "+endereco);
				return null;
			}
		}
		return null;
	}	
	
}
