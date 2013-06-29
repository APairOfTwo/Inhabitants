package main;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;


public class Personagem extends Sprite {

	public float objetivoX = 0;
	public float objetivoY = 0;

	public int ID = 0;

	public int vel = 50;

	public boolean isAlive = true;
	public int life = 100;
	float raio = 20;
	int tamanho = 10;
	int tiroTimer = 0;

	// daqui pra baixo não importa no server

	public int respawnCountTime;

	BufferedImage mira;
	public Color cor = Color.BLUE;

	public BufferedImage img = null;

	public int frame = 0;
	public int frametimer = 0;
	public int animspeed = 200;

	public int anim = 0;

	public int poscharx = 0;
	public int poschary = 0;

	public Personagem(float x,float y,BufferedImage img,int idpersonagem) {
		X = x;
		Y = y;

		objetivoX = x;
		objetivoY = y;

		this.img = img;

		poscharx = (idpersonagem%4)*96;
		poschary = (idpersonagem/4)*192;
	}

	public Personagem(int id,float x,float y) {
		X = x;
		Y = y;
		ID = id;
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
		float dx = objetivoX - X;
		float dy = objetivoY - Y;

		double ang = Math.atan2(dy, dx);

		double dist2 = dx*dx+dy*dy;

		if(dist2>9){
			X = (float)(X + (vel*Math.cos(ang))*diftime/1000.0f);
			Y = (float)(Y + (vel*Math.sin(ang))*diftime/1000.0f);
		}
	}

	public void atira(float posX, float posY, int dirX, int dirY) {
		if (MainCanvas.instance.FIRE && tiroTimer > 100) {
			tiroTimer = 0;
			float vproj = 400;

			float px = posX + 5;
			float py = posY + 5;

			float dx = MainCanvas.instance.MouseX - px;
			float dy = MainCanvas.instance.MouseY - py;

			double ang = Math.atan2(dy, dx);

			float vx = (float) (vproj * Math.cos(ang));
			float vy = (float) (vproj * Math.sin(ang));

			Projetil proj = new Projetil(posX + 5, posY + 5, vx, vy, this);
			MainCanvas.instance.listaDeProjetil.add(proj);
		}
	}


	@Override
	public void DesenhaSe(Graphics2D dbg,int mapx,int mapy) {
		if(isAlive) {
			dbg.setColor(cor);
			dbg.fillRect((int)X-5, (int)Y-5, 10, 10);
			dbg.drawLine((int)X, (int)Y, (int)(objetivoX), (int)(objetivoY));
			dbg.setColor(Color.orange);
			dbg.drawOval((int)(X-tamanho),(int) Y-tamanho,(int) raio,(int) raio);
			dbg.setColor(Color.green);
			dbg.fillRect((int)X-15, (int) Y-15, (int)life/3, 5);
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
