package main;

import game_connection.Constantes;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class Item extends Sprite{
	BufferedImage life = null;
	public boolean vivo;
	int raio = 10;
	int tamanho = 5;

	public Item(float x, float y) {
		
		System.out.println(" item "+x+y);

		this.X = x;
		this.Y = y;
		vivo = true;
		life = Constantes.loadImageFromFile("up.png");

	}

	@Override
	public void SimulaSe(long diftime) {
		// TODO Auto-generated method stub

	}

	@Override
	public void DesenhaSe(Graphics2D dbg, int mapx, int mapy) {
		// TODO Auto-generated method stub
		if(vivo){
			dbg.drawImage(life,  (int) X - mapx, (int) Y - mapy, (int)  30 ,  30, null);}
	}

	public boolean getVivo() {
		return vivo;
	}

	public void setVivo(boolean ativo) {
		vivo = ativo;
	}
}
