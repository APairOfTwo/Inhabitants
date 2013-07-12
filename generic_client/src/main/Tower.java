package main;

import game_connection.Constantes;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class Tower extends Sprite {

	public BufferedImage sprite;
	public int tiroTimer = 0;
	int raio = 200;
	int tamanho=150;
	boolean visaoTorre = false;

	public Tower(float x, float y) {

		this.X = x;
		this.Y = y;
		sprite = Constantes.loadImageFromFile("torre.png");

	}

	@Override
	public void SimulaSe(long diftime) {
		// TODO Auto-generated method stub
		tiroTimer += diftime;

		if (MainCanvas.instance.listaDePersonagens != null) {
			for (int i = 0; i < MainCanvas.instance.listaDePersonagens.size(); i++) {
				Personagem p = MainCanvas.instance.listaDePersonagens.get(i);

				if (colisaoCircular(p)) {
					visaoTorre = true;
					atira(X+sprite.getWidth()/2,Y+sprite.getHeight()/2,(p.X-MainCanvas.instance.mapa.MapX)+p.tamanho,
							(p.Y-MainCanvas.instance.mapa.MapY)+p.tamanho);
					break;
				} else
					visaoTorre = false;

			}
		}

	}

	public void atira(float posX, float posY, float dirX, float dirY) {
		if (tiroTimer > 300) {
			tiroTimer = 0;
			float vproj = 400;

			float px = (posX + 5) - MainCanvas.instance.mapa.MapX;
			float py = (posY + 5) - MainCanvas.instance.mapa.MapY;

			float dx = dirX - px;
			float dy = dirY - py;

			double ang2 = Math.atan2(dy, dx);

			float vx = (float) (vproj * Math.cos(ang2));
			float vy = (float) (vproj * Math.sin(ang2));

			Projetil proj = new Projetil(posX + 5, posY + 5, vx, vy, this);
			MainCanvas.instance.listaDeProjetil.add(proj);
		}
	}

	@Override
	public void DesenhaSe(Graphics2D dbg, int mapx, int mapy) {
		// TODO Auto-generated method stub
		dbg.drawImage(sprite, (int) X - mapx, (int) Y - mapy, null);

//		if (visaoTorre) {
//			dbg.setColor(Color.red);
//		} else
//			dbg.setColor(Color.yellow);
//
//		dbg.drawOval((int) ((X)-tamanho) - mapx, (int) (Y-tamanho)- mapy, raio*2, raio*2);
	}

	public boolean colisaoCircular(Personagem p) {
		float dx = (X-(raio/2)) - (p.X-tamanho);
		float dy = (Y-(raio/2)) - (p.Y-tamanho);
		float r2 = p.raio + raio;
		r2 = r2 * r2;
		if (r2 > ((dx * dx) + (dy * dy))) {
			return true;
		}
		return false;
	}

}
