package main;

import game_connection.Constantes;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class Npc extends Sprite {

	public BufferedImage sprite;

	public Npc(float x, float y) {
		this.X = x;
		this.Y = y;
		sprite = Constantes.loadImageFromFile("npc.png");
	}

	@Override
	public void SimulaSe(long diftime) {

	}

	@Override
	public void DesenhaSe(Graphics2D dbg, int mapx, int mapy) {
		dbg.drawImage(sprite, (int) X - mapx, (int) Y - mapy, null);
	}

}
