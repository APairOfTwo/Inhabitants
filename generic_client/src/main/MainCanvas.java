package main;

import game_connection.Constantes;
import game_connection.SocketConectionManager;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
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
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

public class MainCanvas extends Canvas implements Runnable {
	public static final int PWIDTH = 640;
	public static final int PHEIGHT = 480;

	public static MainCanvas instance = null;

	public Thread animator;
	public boolean running = false;
	public boolean gameOver = false;

	public Graphics2D dbg = null;
	public BufferStrategy strategy = null;

	int FPS, SFPS;
	int fpscount;

	int respawnTime = 2000;

	public ArrayList<Personagem> listaDePersonagens = new ArrayList<Personagem>();
	public ArrayList<Projetil> listaDeProjetil = new ArrayList<Projetil>();

	public static Random rnd = new Random();
	// BufferedImage imagemcharsets;
	BufferedImage fundo;

	boolean LEFT, RIGHT, UP, DOWN, FIRE;

	int MouseX, MouseY;
	double posx, posy;

	public static Mapa_Grid mapa;

	public BufferedImage ultimaImagemBaixada = null;
	public String ultimaMSGServer = "";

	public MainCanvas() {
		instance = this;

		setBackground(Color.white);
		setPreferredSize(new Dimension(PWIDTH, PHEIGHT));

		fundo = Constantes.loadImageFromFile("espaco.jpg");

		setFocusable(true);

		requestFocus();

		addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				int key = e.getKeyCode();
				if (key == KeyEvent.VK_F1) {
					String usuario = JOptionPane.showInputDialog(null,
							"Usuario : ", "Login", 1);
					String senha = JOptionPane.showInputDialog(null,
							"Senha : ", "Login", 1);

					if (Constantes.connectionManager != null
							&& Constantes.connectionManager.socket
									.isConnected()) {
						Constantes.connectionManager.sendMsgLogin(usuario,
								senha);
					}

				}

				if (key == KeyEvent.VK_F2) {
					String usuario = JOptionPane.showInputDialog(null,
							"Usuario : ", "Login", 1);
					String senha = JOptionPane.showInputDialog(null,
							"Senha : ", "Login", 1);
					int raca = JOptionPane.showConfirmDialog(null,
							"Seu personagem será humano?");
					System.out.println(raca);
					if (Constantes.connectionManager != null
							&& Constantes.connectionManager.socket
									.isConnected()) {
						Constantes.connectionManager.sendMsgCadastra(usuario,
								senha, raca);
					}
				}
				if (key == KeyEvent.VK_LEFT) {
					LEFT = true;
				}
				if (key == KeyEvent.VK_RIGHT) {
					RIGHT = true;
				}
				if (key == KeyEvent.VK_UP) {
					UP = true;
				}
				if (key == KeyEvent.VK_DOWN) {
					DOWN = true;
				}

			}

			@Override
			public void keyReleased(KeyEvent e) {
				int keyCode = e.getKeyCode();
				if (keyCode == KeyEvent.VK_LEFT) {
					LEFT = false;
				}
				if (keyCode == KeyEvent.VK_RIGHT) {
					RIGHT = false;
				}
				if (keyCode == KeyEvent.VK_UP) {
					UP = false;
				}

				if (keyCode == KeyEvent.VK_DOWN) {
					DOWN = false;
				}

			}
		});

		addMouseMotionListener(new MouseMotionListener() {

			@Override
			public void mouseMoved(MouseEvent e) {
			}

			@Override
			public void mouseDragged(MouseEvent e) {
			}
		});

		addMouseListener(new MouseListener() {

			@Override
			public void mouseReleased(MouseEvent e) {
				FIRE = false;
			}

			@Override
			public void mousePressed(MouseEvent e) {
				MouseX = (e.getX());
				MouseY = (e.getY());

				if (e.getButton() == 1) {
					if (Constantes.logado == true) {
						Constantes.meuPersonagem.objetivoX = MouseX;
						Constantes.meuPersonagem.objetivoY = MouseY;
						if (Constantes.connectionManager.socket.isConnected()) {
							Constantes.connectionManager
									.sendMsgMovimentaPersonagem(
											Constantes.meuPersonagem.X,
											Constantes.meuPersonagem.Y,
											Constantes.meuPersonagem.objetivoX,
											Constantes.meuPersonagem.objetivoY);
						}
					}
				} else if (e.getButton() == 3) {
					FIRE = true;
					if (Constantes.logado == true) {
						Constantes.connectionManager.sendMsgtiro(
								Constantes.meuPersonagem.X,
								Constantes.meuPersonagem.Y, MouseX, MouseY);
						Constantes.meuPersonagem.atira(
								Constantes.meuPersonagem.X,
								Constantes.meuPersonagem.Y, MouseX, MouseY);
					}
				}
				System.out.println(" mx " + MouseX + " my " + MouseY);
			}

			@Override
			public void mouseExited(MouseEvent e) {
			}

			@Override
			public void mouseEntered(MouseEvent e) {
			}

			@Override
			public void mouseClicked(MouseEvent e) {
			}
		});

		MouseX = MouseY = 50;
		mapa = new Mapa_Grid(100, 100, 50, 32);
	}

	public void addNotify() {
		super.addNotify();
		startGame();
	}

	public void startGame() {
		if (animator == null || !running) {
			animator = new Thread(this);
			animator.start();
		}
	}

	public void stopGame() {
		running = false;
	}

	public void run() {
		running = true;

		long DifTime, TempoAnterior;

		int segundo = 0;
		DifTime = 0;
		TempoAnterior = System.currentTimeMillis();

		createBufferStrategy(2);
		strategy = getBufferStrategy();

		while (running) {
			gameUpdate(DifTime);
			dbg = (Graphics2D) strategy.getDrawGraphics();
			dbg.setClip(0, 0, PWIDTH, PHEIGHT);
			dbg.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
			gameRender();
			dbg.dispose();
			strategy.show();
			try {
				Thread.sleep(0);
			} catch (InterruptedException ex) {
			}

			DifTime = System.currentTimeMillis() - TempoAnterior;
			TempoAnterior = System.currentTimeMillis();

			if (segundo != ((int) (TempoAnterior / 1000))) {
				FPS = SFPS;
				SFPS = 1;
				segundo = ((int) (TempoAnterior / 1000));
			} else {
				SFPS++;
			}
		}
		System.exit(0);
	}

	@Override
	public void paint(Graphics arg0) {
		// super.paint(arg0);
	}

	@Override
	public void update(Graphics arg0) {
		// super.update(arg0);
	}

	int timerfps = 0;
	int pingtimer = 0;
	int timerDownload = 0;

	private void gameUpdate(long diftime) {
		pingtimer += diftime;

		timerDownload += diftime;

		if (pingtimer > 1000) {
			if (Constantes.connectionManager != null) {
				if (Constantes.connectionManager.socket.isConnected()) {
					Constantes.connectionManager.sendMsgPing();
				}
			}
			pingtimer = 0;
		}

		for (int i = 0; i < listaDePersonagens.size(); i++) {
			listaDePersonagens.get(i).SimulaSe(diftime);
//			System.out.println(""+i+" i " + (int) (listaDePersonagens.get(i).X)
//					+ " y " + ((int) listaDePersonagens.get(i).Y));
		}

		for (int i = 0; i < listaDeProjetil.size(); i++) {
			listaDeProjetil.get(i).SimulaSe(diftime);
		}

		if (Constantes.meuPersonagem != null) {
			if (!Constantes.meuPersonagem.isAlive) {
				Constantes.meuPersonagem.respawnCountTime += diftime;
				if (Constantes.meuPersonagem.respawnCountTime >= respawnTime) {
					Constantes.meuPersonagem.respawnCountTime = 0;
					Constantes.connectionManager.sendMsgRespawn(100,
							Constantes.rand.nextInt(300) + 50,
							Constantes.rand.nextInt(300) + 50);
				}
			}
		}
		// if (LEFT) {
		// posx -= 200 * diftime / 1000.0;
		// }
		// if (RIGHT) {
		// posx += 200 * diftime / 1000.0;
		// }
		// if (UP) {
		// posy -= 200 * diftime / 1000.0;
		// }
		// if (DOWN) {
		// posy += 200 * diftime / 1000.0;
		// }

		if (Constantes.meuPersonagem != null) {
//			System.out.println("x " + (int) (Constantes.meuPersonagem.X)
//					+ " y " + ((int) Constantes.meuPersonagem.Y));
			mapa.Posiciona((int) (Constantes.meuPersonagem.X * 2),
					(int) (Constantes.meuPersonagem.Y * 2));
		}

		// mapa.Posiciona((int)posx,(int)posy);
	}

	private void gameRender() {
		dbg.setColor(Color.BLACK);
		dbg.fillRect(0, 0, PWIDTH, PHEIGHT);

		// if (ultimaImagemBaixada != null) {
		dbg.drawImage(fundo, 0 - mapa.MapX, 0 - mapa.MapY, mapa.Altura * 16,
				mapa.Altura * 16, null);
		//mapa.DesenhaSe(dbg);
		// }

		for (int i = 0; i < listaDePersonagens.size(); i++) {
//			System.out.println("mapx " + (int) (mapa.MapX)
//					+ " y " + ((int) mapa.MapY));
				listaDePersonagens.get(i).DesenhaSe(dbg, mapa.MapX, mapa.MapY);
			
		}

		for (int i = 0; i < listaDeProjetil.size(); i++) {
			listaDeProjetil.get(i).DesenhaSe(dbg, mapa.MapX, mapa.MapY);
		}

		dbg.setColor(Color.BLUE);
		dbg.drawString("FPS: " + FPS + " " + MouseX + " " + MouseY + " PING: "
				+ SocketConectionManager.lastpingreceived + " "
				+ Constantes.logado, 10, 10);
		dbg.drawString(ultimaMSGServer, 10, PHEIGHT - 30);
	}

	public static BufferedImage LoadImageUrl(String endereco) {
		URL url = null;

		try {
			url = new URL(endereco);
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		}

		if (url != null) {
			try {
				HttpURLConnection connection = (HttpURLConnection) url
						.openConnection();

				connection
						.setRequestProperty(
								"User-Agent",
								"Mozilla/5.0 (Windows; U; Windows NT 6.1; en-GB;     rv:1.9.2.13) Gecko/20101203 Firefox/3.6.13 (.NET CLR 3.5.30729)");

				int filesize = connection.getContentLength();
				System.out.print("FILE SIZE ----> " + filesize);
				if (connection.getContentLength() > 850000) {
					System.out.println("NAO");
					return null;
				} else {
					System.out.println("SIM");
				}

				if (filesize <= 0) {
					return null;
				}

				InputStream in = connection.getInputStream();
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				byte bb[] = new byte[50000];
				int size = -1;

				while ((size = in.read(bb)) != -1) {
					baos.write(bb, 0, size);
				}

				InputStream inimage = new ByteArrayInputStream(
						baos.toByteArray());
				BufferedImage image = ImageIO.read(inimage);

				return image;

			} catch (IOException e) {
				System.out.println("ERRO DOWNLOAD IMAGE: " + endereco);
				return null;
			}
		}
		return null;
	}

}
