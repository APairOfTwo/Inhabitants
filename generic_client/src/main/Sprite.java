package main;

import java.awt.Graphics2D;

abstract public class Sprite {
	
	public float X;
	public float Y;
	
	public abstract void SimulaSe(long diftime);
	public abstract void DesenhaSe(Graphics2D dbg,int mapx,int mapy);
}
