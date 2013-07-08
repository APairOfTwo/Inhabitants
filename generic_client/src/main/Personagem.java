package main;

import game_connection.Constantes;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;


public class Personagem extends Sprite {

	public float objetivoX = 0;
	public float objetivoY = 0;

	public int ID = 0;

	public int vel = 100;

	public boolean isAlive = true;
	public int life = 100;
	float raio = 20;
	int tamanho = 10;
	int tiroTimer = 0;
	String nome;

	// daqui pra baixo não importa no server

	public int respawnCountTime;

	BufferedImage mira;
	public Color cor = Color.BLUE;

	public BufferedImage img = null;
	public BufferedImage sprite;

	public int frame = 0;
	public int frametimer = 0;
	public int animspeed = 200;

	public int anim = 0;

	public int poscharx = 0;
	public int poschary = 0;
	public int raca;

	public Personagem(float x,float y,BufferedImage img,int idpersonagem, String nome) {
		X = x;
		Y = y;

		objetivoX =(x/16);
		objetivoY =(y/16);

		this.img = img;
		this.nome=nome;

		poscharx = (idpersonagem%4)*96;
		poschary = (idpersonagem/4)*192;
	}
	
	public Personagem(int id, float x, float y, int raca) {
		X = x;
		Y = y;
		ID = id;
		this.raca = raca;
		sprite = raca == 0 ? Constantes.loadImageFromFile("human_spaceship.png") : Constantes.loadImageFromFile("alien_spaceship.png");
	}

	double theta = 0;

	@Override
	public void SimulaSe(long diftime) {
		if(isAlive) {
			frametimer+=diftime;
			frame = (frametimer/animspeed)%3;
			tiroTimer += diftime;

			deslocaSe(diftime);
		}
	}

	public void deslocaSe(long diftime){
		float dx = objetivoX - (X/16);
		float dy = objetivoY - (Y/16);

		double ang = Math.atan2(dy, dx);

		double dist2 = dx*dx+dy*dy;

		if(dist2>9){
			X = (float)(X + (vel*Math.cos(ang))*diftime/1000.0f);
			Y = (float)(Y + (vel*Math.sin(ang))*diftime/1000.0f);
		}
	}

	public void atira(float posX, float posY, float dirX, float dirY) {
		if (MainCanvas.instance.FIRE && tiroTimer > 100) {
			tiroTimer = 0;
			float vproj = 400;
			

			float px = (posX + 5)-MainCanvas.instance.mapa.MapX;
			float py = (posY + 5)-MainCanvas.instance.mapa.MapY;

			float dx = dirX - px;
			float dy = dirY - py;

			double ang2 = Math.atan2(dy, dx);

			float vx = (float) (vproj * Math.cos(ang2));
			float vy = (float) (vproj * Math.sin(ang2));

			Projetil proj = new Projetil(posX + 5, posY + 5, vx, vy, this);
			System.out.println("merlin"+(int)vx+ " "+(int)vy+" px "+px+" py "+py+" dx "+dx+" dy "+dy);
			MainCanvas.instance.listaDeProjetil.add(proj);
		}
	}


	@Override
	public void DesenhaSe(Graphics2D dbg,int mapx,int mapy) {
		if(isAlive) {
			dbg.setColor(cor);
			//dbg.fillRect((int)(X-5)-mapx, (int)(Y-5)-mapy, 10, 10);
			dbg.drawImage(sprite, (int)X-mapx, (int)Y-mapy, (int)(X + 47) - mapx, (int)(Y + 50) - mapy, 0, 0, 94, 100, null);
			
			//dbg.drawString(nome,(int)(X-5)-mapx, (int)(Y-25)-mapy);
		
			dbg.setColor(Color.orange);
			dbg.drawOval((int)(X-tamanho)-mapx,(int) (Y-tamanho)-mapy,(int) raio,(int) raio);
			dbg.setColor(Color.green);
			dbg.fillRect((int)(X-15)-mapx, (int) (Y-15)-mapy, (int)life/3, 5);
		}
	}

	public boolean colisaoCircular(Personagem p) {
		float dx = (p.X -tamanho) - (X -tamanho);
		float dy = (p.Y -tamanho) - (Y -tamanho);
		float r2 = p.raio + raio;
		r2 = r2 * r2;
		if (r2 > ((dx * dx) + (dy * dy))) {
			return true;
		}
		return false;
	}

	public void levaDano(int dano) {
		life -= dano;
		if (life <= 0) {
			isAlive = false;
		}
	}

}
