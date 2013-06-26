package PALMSOFT.genericServer.server.controler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ServerControler implements Runnable{
    Thread thisthead;
    
//    public LinkedList<Jogador> listaDeRequisicoesDeJogadores = null;
    public long tempobackup = 0;
    public long temporanking = 0;
    
//    public LinkedList<Jogador> topList = new LinkedList<Jogador>();
    
    DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy-hh_mm_ss");
    
	public ServerControler() {
//		listaDeRequisicoesDeJogadores = new LinkedList<Jogador>();
	}
    
    public void inicia(){
    	System.out.println("INICIOU MANAGERS");
    	
    	thisthead = new Thread(this);
    	
    	DadosServer.filesender = new FileSender();
    	DadosServer.filesender.inicia();
    	
//    	geratoplist();
    	
    	thisthead.start();
    	
    	tempobackup = 0;
    }
    
	@Override
	public void run() {
		Jogador requisicoes = null;
		long tempoatual = System.currentTimeMillis();
		int diftime = 0;
		int pingtime = 0;
		while(thisthead.isAlive()){
			
			pingtime+=diftime;
			if(pingtime>=1000){
				pingtime = 0;
				for (Iterator iterator = DadosServer.listaDeJogadoresLogados.iterator(); iterator.hasNext();) {
					Jogador outrojogador = (Jogador) iterator.next();
					outrojogador.ping(System.currentTimeMillis());
				}
			}
			
			for (Iterator iterator = DadosServer.listaDeJogadoresLogados.iterator(); iterator.hasNext();) {
				Jogador outrojogador = (Jogador) iterator.next();
				outrojogador.SimulaSe(diftime);
			}
			
			if((tempoatual-tempobackup)>60000){//3600000
				tempobackup = tempoatual;
				
				File fileini = new File(DadosServer.pathdados+"dados.zip");
				if(fileini.exists()){
					Calendar calendar = Calendar.getInstance();
					calendar.setTimeInMillis(System.currentTimeMillis());
					File newfile = new File(DadosServer.pathdados+"backupDado"+formatter.format(calendar.getTime())+".zip");
					fileini.renameTo(newfile);
				}
				fileini = new File(DadosServer.pathdados+"dados.zip");

				try {
					ZipOutputStream zout = new ZipOutputStream(new FileOutputStream(fileini));
					zout.putNextEntry(new ZipEntry("dados.csv"));
					OutputStreamWriter outw = new OutputStreamWriter(zout);

					Set<String> set = DadosServer.hashJogadores.keySet();
					for (Iterator iterator = set.iterator(); iterator.hasNext();) {
						String key = (String) iterator.next();
						Jogador jog = DadosServer.hashJogadores.get(key);
						outw.write(""+jog.Nome+";"+jog.Senha+";"+jog.pontos+";"+"\n");
					}
					outw.close();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
//			if((tempoatual-temporanking)>60000){
//				temporanking = tempoatual;
//				geratoplist();
//			}
			diftime =(int)( System.currentTimeMillis()-tempoatual);
			tempoatual = System.currentTimeMillis();
			
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
