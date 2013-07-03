package PALMSOFT.genericServer.server.controler;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;


public class Personagem extends Sprite {

	public float objetivoX = 0;
	public float objetivoY = 0;
	
	public int ID = 0;
	
	public int vel = 200;
	float raio = 20;
	public int tipo;

	
	public boolean isAlive = true;
	
	// daqui pra baixo não inporta no server
	
	public BufferedImage img = null;
	
	public int frame = 0;
	public int frametimer = 0;
	public int animspeed = 200;
	
	public int anim = 0;
	
	public int poscharx = 0;
	public int poschary = 0;
	
	public Personagem(float x,float y,BufferedImage img,int idpersonagem) {
		// TODO Auto-generated constructor stub
		X = x;
		Y = y;
		
		objetivoX = x;
		objetivoY = y;
		
		this.img = img;
		
		poscharx = (idpersonagem%4)*96;
		poschary = (idpersonagem/4)*192;
	}
	
//	public Personagem(int id,float x,float y,int tipo) {
//		// TODO Auto-generated constructor stub
//		X = x;
//		Y = y;
//		ID = id;
//
//		this.tipo=tipo;
//	}
	public Personagem(int id,float x,float y) {
		// TODO Auto-generated constructor stub
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

	@Override
	public void DesenhaSe(Graphics2D dbg,int mapx,int mapy) {
		// TODO Auto-generated method stub
//		AffineTransform trans = dbg.getTransform();
//		
//		dbg.translate((int)X+16, (int)Y+24);
//		
//		dbg.rotate(theta);
//		//dbg.scale(2, 2);
//		dbg.translate((int)-16, (int)-24);
//		
//		dbg.drawImage(img, 0, 0,+32,+48, frame*32+poscharx, anim*48+poschary,(frame*32)+32+poscharx, anim*48+48+poschary, null);
//		
//		dbg.setTransform(trans);
		
//		dbg.drawImage(img, (int)X-mapx, (int)Y-mapy,(int)X+32-mapx, (int)Y+48-mapy, frame*32+poscharx, anim*48+poschary,(frame*32)+32+poscharx, anim*48+48+poschary, null);
		
		if(isAlive) {
			dbg.setColor(Color.BLUE);
			dbg.fillRect((int)X-mapx-5, (int)Y-mapy-5, 10, 10);
			dbg.drawLine((int)X-mapx, (int)Y-mapy, (int)(X-objetivoX), (int)(Y-objetivoY));
		}	
	}
}
