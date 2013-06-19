package PALMSOFT.genericServer.server.controler;


import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.Random;

import javax.imageio.ImageIO;

import org.apache.mina.core.session.IoSession;

import PALMSOFT.genericServer.server.protocol.NetMessage;

public class FileSender implements Runnable{
    Thread thisthead;
    
	public static LinkedList<FileToSend> listaDeArquivosParaEnviar = null;
	
	boolean rodando = false;
	
	Random rnd = new Random();
	
	public FileSender() {
		// TODO Auto-generated constructor stub
		this.listaDeArquivosParaEnviar = new LinkedList<FileToSend>();
	}
	
    public void inicia(){
    	thisthead = new Thread(this);
    	thisthead.start();
    }
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		FileToSend filetosend = null;
		
		rodando = true;
		
		while(rodando){
			if(listaDeArquivosParaEnviar.size()>0){
				synchronized (listaDeArquivosParaEnviar) {
					filetosend = listaDeArquivosParaEnviar.remove();
				}
				File file = null;

				file = new File(DadosServer.pathdados+filetosend.imgname);

				
				if(file.exists()){
					try {
						FileInputStream fin = new FileInputStream(file);
						int size = fin.available();
						System.out.println("SEND FILE "+filetosend.imgname+" -> FileSize "+size+" "+filetosend.codigo);
						

						ByteArrayOutputStream bout = new ByteArrayOutputStream();
						DataOutputStream dbout = new DataOutputStream(bout);
						
						dbout.writeUTF(filetosend.imgname);
						dbout.writeInt(filetosend.codigo);
						dbout.writeInt(size);

						
						filetosend.session.write(new NetMessage(7, bout.toByteArray()));
						bout.close();
						

						byte[] imgbytes = new byte[1024];
						int nbytes = 0;
						int datasend = 0;
						while((nbytes = fin.read(imgbytes))> 0){
							bout = new ByteArrayOutputStream();
							dbout = new DataOutputStream(bout);
							dbout.writeInt(filetosend.codigo);
							dbout.write(imgbytes,0,nbytes);
							filetosend.session.write(new NetMessage(8, bout.toByteArray()));
							bout.close();
							datasend+=nbytes;
							if(datasend>1500000){
								datasend = 0;
								try {
									Thread.sleep(10);
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						}
						
						imgbytes = null;
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}else{
					ByteArrayOutputStream bout = new ByteArrayOutputStream();
					DataOutputStream dbout = new DataOutputStream(bout);
					try {
						dbout.writeUTF(filetosend.imgname);
						dbout.writeInt((byte)-1);
						filetosend.session.write(new NetMessage(7, bout.toByteArray()));
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					System.out.println("IMG "+file.getAbsolutePath()+" NAO EXISTE");
				}
			}else{
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			//forchanDownloader.douwnloadcounter++;
		}
	}
	
	public void addFileToSend(IoSession session,String imgname,int codigo){
		listaDeArquivosParaEnviar.add(new FileToSend(session, imgname, codigo));
	}
}

class FileToSend{
	IoSession session;
	String imgname;
	int codigo;
	public FileToSend(IoSession session,String imgname,int codigo) {
		this.session = session;
		this.imgname = imgname;
		this.codigo = codigo;
	}
}
