package PALMSOFT.genericServer.server.controler;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.ParseException;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class DadosServer {
	public static ServerControler servercontroler = null;
	public static FileSender filesender = null;
	
	public static Hashtable<String, Jogador> hashJogadores = new Hashtable<String, Jogador>();
	
	public static LinkedList<Jogador> listaDeJogadoresLogados = new LinkedList<Jogador>();
	
	public static String pathdados = "."+File.separator+"dados"+File.separator;

	
	public static Random rnd = new Random();
	//TODO
	public static void carregaDadosDisco(){
		File fileini = new File(pathdados+"dados.zip");

		if(fileini.exists()){
			try {
				ZipInputStream zin = new ZipInputStream(new FileInputStream(fileini));
				zin.getNextEntry();
				InputStreamReader inr = new InputStreamReader(zin);
				BufferedReader bfr = new BufferedReader(inr);
	
				String str = "";
				int regcount = 0;
				while((str = bfr.readLine())!=null){
					System.out.println(""+regcount+"-> "+str);
					String[] strs = str.split(";");
					Jogador jog = new Jogador(strs[0],strs[1]);
					jog.pontos = Integer.parseInt(strs[2]);
					
					hashJogadores.put(jog.Nome, jog);
					regcount++;
				}
				zin.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
		}else{
			System.out.println("File not Ecxiste");
		}
	}
	
}
