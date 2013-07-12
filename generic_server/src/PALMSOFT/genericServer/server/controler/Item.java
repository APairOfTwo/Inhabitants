package PALMSOFT.genericServer.server.controler;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class Item extends Sprite{
	BufferedImage life = null;
	public boolean vivo;

	public Item(float x, float y) {

		this.X = x;
		this.Y = y;
		vivo = false;
		//life = Constantes.loadImageFromFile("mira.png");

	}

	@Override
	public void SimulaSe(long diftime) {
		// TODO Auto-generated method stub

	}

	@Override
	public void DesenhaSe(Graphics2D dbg, int mapx, int mapy) {
		// TODO Auto-generated method stub
		if(vivo){
			dbg.drawImage(life,  (int) X - mapx, (int) Y - mapy, (int)  30 - mapx,  30 - mapx, null);}
	}

	public boolean getVivo() {
		return vivo;
	}

	public void setVivo(boolean ativo) {
		vivo = ativo;
	}
}

