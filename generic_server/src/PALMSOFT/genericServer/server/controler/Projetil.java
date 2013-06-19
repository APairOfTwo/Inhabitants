package PALMSOFT.genericServer.server.controler;

import java.awt.Color;
import java.awt.Graphics2D;

public class Projetil extends Sprite {

	int frame = 0;
	int anim = 0;
	int animspd = 500;
	int somatimeanim = 0;
	public float velx = 0;
	public float vely = 0;
	Object pai = null;
	float raio = 2;
	int dano = 200;
	float gravidade = 9.8f;
	float massa = 1f;

	public float objetivoX = 0;
	public float objetivoY = 0;
	
	public boolean vivo = true;

	public Projetil(float X, float Y, float velx, float vely, Object pai) {
		super();
		this.X = X;
		this.Y = Y;
		this.velx = velx;
		this.vely = vely;
		this.pai = pai;

		
	}

	@Override
	public void SimulaSe(long diftime) {
		somatimeanim += diftime;
		frame = (somatimeanim / animspd) % 3;

		X += velx * diftime / 1000.0f;
		Y += vely * diftime / 1000.0f;
		if (X < 0) {
			vivo = false;
		} else if (Y < 0) {
			vivo = false;
		} else if (this.Y > 400) {
			vivo = false;
			double ang = Math.atan2(vely, velx);
			ang += Math.PI;
			for (int j = 0; j < 20; j++) {
				double ang2 = ang - (Math.PI / 4)
						+ ((Math.PI / 2) * Math.random());
				float vel = (float) (150 + 100 * Math.random());
				float vx = (float) (Math.cos(ang2) * vel);
				float vy = (float) (Math.sin(ang2) * vel);

			}
		}

	}

	@Override
	public void DesenhaSe(Graphics2D dbg, int mapx, int mapy) {
		dbg.setColor(Color.black);
		dbg.fillOval((int) (X - 2), (int) (Y - 2), 4, 4);
	}

	public boolean colisaoCircular(Personagem p) {
		float dx = (p.X + 5) - (X);
		float dy = (p.Y + 5) - (Y);
		float r2 = p.raio + raio;
		r2 = r2 * r2;
		if (r2 > ((dx * dx) + (dy * dy))) {
			return true;
		}
		return false;
	}

}
