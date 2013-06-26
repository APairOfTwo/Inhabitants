package game_connection;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Random;

import javax.imageio.ImageIO;
//import java.util.zip.ZipEntry;
//import java.util.zip.ZipOutputStream;

import main.MainCanvas;
import main.Personagem;


public class Constantes {

	public static FileWriter outw;

	public static SocketConectionManager connectionManager = null;

	public static String pathFile = "";//"."+File.separator+"img"+File.separator;

	public static boolean logado = false;

	public static Personagem meuPersonagem = null;

	public static Random rand = new Random();

	static{
		pathFile = System.getProperty("java.io.tmpdir")+File.separator+"serverTest"+File.separator+"img"+File.separator;

		File f = new File(pathFile);
		if(!f.exists()){
			f.mkdirs();
		}
	}

	public static void loadStatus(){
		File f = new File(pathFile+"dadosys.conf");
		if(f.exists()){
			try {
				FileReader fr = new FileReader(f);
				String sr = "";
				BufferedReader bfr = new BufferedReader(fr);
				while ((sr = bfr.readLine())!=null) {
					String strs[] = sr.split(";");
					String key = strs[0];
					String imgname = strs[1];
					Double points = Double.parseDouble(strs[2]);
				}
				bfr.close();
				fr.close();

			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			try {
				outw = new FileWriter(f,true);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}else{
			try {
				//zipout = new ZipOutputStream(new FileOutputStream(f));
				//zipout.putNextEntry(new ZipEntry("dados.csv"));
				outw = new FileWriter(f);//new OutputStreamWriter(new FileOutputStream(f));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void addToLog(String key,String str){
		try {
			outw.write(str+";\n");
			outw.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static BufferedImage loadImageFromResorces(String imagename){
		BufferedImage img = null;

		try {
			img = ImageIO.read(MainCanvas.instance.getClass().getResourceAsStream("\\"+imagename));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return img;
	}

	public static BufferedImage loadImageFromFile(String filename){
		BufferedImage img = null;

		try {
			img = ImageIO.read(new File(filename));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return img;
	}

}
