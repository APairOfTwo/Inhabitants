package game_connection;


import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.zip.ZipInputStream;

import javax.imageio.ImageIO;

import main.MainCanvas;
import main.Personagem;
import main.Projetil;


//  msg 0 - C S - conection message
//  msg 1 - C - ping
//  msg 2 - S - pong

//  msg 3 - C - manda dados login
//  msg 4 - S - Status login  0 não aceito 1 aceito

//  msg 6 - c - Client Pedindo um Arquivo
//  msg 7 - S - Manda Header do Arquivo
//  msg 8 - S - Manda Dados do Arquivo

//  msg 10 - S Manda personagem player
//  msg 11 - S Manda personagem outros players
//  msg 12 - S Player Deslogou

//  msg 13 - C movimenta player
//  msg 14 - S player Movimentou

//	msg 15 - C - Atira projétil player
//	msg 16 - S - Player atirou (0 errou o tiro e 1 acertou)

//	msg 17 - C - Respawn do player
//	msg 18 - S - Player respawnou


public class SocketConectionManager implements Runnable{
	private static final int PORT = 5321;
	private static final String IP = "127.0.0.1";

	DataOutputStream sockdout = null;
	DataInputStream sockdin = null;

	public Socket socket = null;
	boolean running = false;

	Thread thisthread = null;

	public static int lastpingreceived = -1;

	public HashMap<Integer, ReceiveData> dadosPendetes = new HashMap<Integer, ReceiveData>();

	public SocketConectionManager() {
	}

	public void connect(){
		try {
			socket = new Socket(IP, PORT);
			System.out.println("Conecto...");

			InputStream in = socket.getInputStream();
			OutputStream out = socket.getOutputStream();
			sockdout = new DataOutputStream(out);
			sockdin = new DataInputStream(in);

			running = true;

			thisthread = new Thread(this);
			thisthread.start();

			//			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			//			DataOutputStream dbout = new DataOutputStream(bout);
			//			dbout.writeUTF("FUCK THIS SHIT fdfsdfsdf");
			//			NetMessage msg = new NetMessage(0, bout.toByteArray());
			//			sendNetMessage(dout, msg);

		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void close(){
		try {
			socket.close();
			System.out.println("CLOSE");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void sendNetMessage(NetMessage msg){
		if(socket.isConnected()){
			try {
				sockdout.writeInt(msg.getSize());
				sockdout.writeShort(msg.getId());
				sockdout.write(msg.getData());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void run() {
		while(running) {
			try {
				if(sockdin.available()>0) {
					int size = sockdin.readInt();
					short id = sockdin.readShort();
					while(sockdin.available()<size){
						try {
							Thread.sleep(1);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}

					byte[] osbytes = new byte[size];
					sockdin.read(osbytes);

					NetMessage msg = new NetMessage(id, osbytes);

					trataMsgRecebida(msg);
				} else {
					try {
						Thread.sleep(1);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void trataMsgRecebida(NetMessage msg){
		switch (msg.getId()) {
		case 0:
			ByteArrayInputStream bin = new ByteArrayInputStream(msg.getData());
			DataInputStream dbin = new DataInputStream(bin);
			String str;
			try {
				str = dbin.readUTF();
				System.out.println("Message: "+str+" ->"+msg.getSize()+" "+msg.getId());
				if(MainCanvas.instance!=null){
					MainCanvas.instance.ultimaMSGServer = str;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			break;
		case 1:
			NetMessage msg2 = new NetMessage(2, msg.getData());
			sendNetMessage(msg2);
			break;
		case 2:
			bin = new ByteArrayInputStream(msg.getData());
			dbin = new DataInputStream(bin);
			try {
				long pingtime = dbin.readLong();
				lastpingreceived = (int)(System.currentTimeMillis()-pingtime);
			} catch (IOException e) {
				e.printStackTrace();
			}
			break;
		case 4:
			bin = new ByteArrayInputStream(msg.getData());
			dbin = new DataInputStream(bin);
			try {
				int loginstatus = dbin.readInt();
				if(loginstatus==0){
					Constantes.logado = false;
				}else if(loginstatus==1){
					Constantes.logado = true;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

			break;
		case 7:
			if(msg.getData()!=null){
				bin = new ByteArrayInputStream(msg.getData());
				dbin = new DataInputStream(bin);
				try {
					System.out.println(" RECEBE PREVIEW "+dbin.available());
					String imgname = dbin.readUTF();
					System.out.println(" NOME "+imgname);
					int codigo = dbin.readInt();
					if(codigo == -1){
						System.out.println("Arquivo não existe no server");
						break;
					}					

					int size = dbin.readInt();

					ReceiveData rdata = new ReceiveData();
					rdata.nome = imgname;
					rdata.codigo = codigo;
					rdata.size = size;

					dadosPendetes.put(codigo, rdata);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			break;
		case 8:
			if(msg.getData()!=null){
				bin = new ByteArrayInputStream(msg.getData());
				dbin = new DataInputStream(bin);
				try {
					int codigo = dbin.readInt();
					if(dadosPendetes.containsKey(codigo)){
						ReceiveData rdata = dadosPendetes.get(codigo);
						byte data[] = new byte[dbin.available()];
						dbin.read(data);
						rdata.bout.write(data);

						if(rdata.bout.size()>=rdata.size){
							File f = null;

							f = new File(Constantes.pathFile+rdata.nome);
							System.out.println("RECEBE ARQUIVO -> "+Constantes.pathFile+rdata.nome);

							FileOutputStream fout = new FileOutputStream(f);
							fout.write(rdata.bout.toByteArray());
							fout.close();

							System.out.println("SERVER FILE "+rdata.nome+" size "+rdata.size);

							dadosPendetes.remove(codigo);

							MainCanvas.instance.ultimaImagemBaixada = Constantes.loadImageFromFile(Constantes.pathFile+rdata.nome);
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		case 10:
			if(msg.getData()!=null){
				bin = new ByteArrayInputStream(msg.getData());
				dbin = new DataInputStream(bin);

				try {
					int id = dbin.readInt();
					float perx = dbin.readFloat();
					float pery = dbin.readFloat();
					float objx = dbin.readFloat();
					float objy = dbin.readFloat();

					Constantes.meuPersonagem = new Personagem(id, perx, pery);
					Constantes.meuPersonagem.objetivoX = objx;
					Constantes.meuPersonagem.objetivoY = objy;
					Constantes.meuPersonagem.cor = Color.blue;

					Constantes.meuPersonagem.deslocaSe(lastpingreceived/2);

					MainCanvas.instance.listaDePersonagens.add(Constantes.meuPersonagem);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			break;		
		case 11:
			if(msg.getData()!=null){
				bin = new ByteArrayInputStream(msg.getData());
				dbin = new DataInputStream(bin);

				try {
					int id = dbin.readInt();
					float perx = dbin.readFloat();
					float pery = dbin.readFloat();
					float objx = dbin.readFloat();
					float objy = dbin.readFloat();

					Personagem pers = new Personagem(id, perx, pery);
					pers.cor = Color.red;
					pers.objetivoX = objx;
					pers.objetivoY = objy;

					pers.deslocaSe(lastpingreceived/2);

					MainCanvas.instance.listaDePersonagens.add(pers);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			break;	
		case 12:
			if(msg.getData()!=null){
				bin = new ByteArrayInputStream(msg.getData());
				dbin = new DataInputStream(bin);

				try {
					int id = dbin.readInt();

					for(int i = 0; i < MainCanvas.instance.listaDePersonagens.size();i++){
						Personagem pers = MainCanvas.instance.listaDePersonagens.get(i);
						if(pers.ID == id){
							MainCanvas.instance.listaDePersonagens.remove(i);
							break;
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			break;	
		case 14:
			if(msg.getData()!=null){
				bin = new ByteArrayInputStream(msg.getData());
				dbin = new DataInputStream(bin);

				try {
					int id = dbin.readInt();
					float x = dbin.readFloat();
					float y = dbin.readFloat();
					float objx = dbin.readFloat();
					float objy = dbin.readFloat();

					for(int i = 0; i < MainCanvas.instance.listaDePersonagens.size();i++){
						Personagem pers = MainCanvas.instance.listaDePersonagens.get(i);
						if(pers.ID == id){
							pers.X = x;
							pers.Y = y;
							pers.objetivoX = objx;
							pers.objetivoY = objy;	
							break;
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			break;
		case 16:
			if(msg.getData()!=null){
				bin = new ByteArrayInputStream(msg.getData());
				dbin = new DataInputStream(bin);

				try {
					int id = dbin.readInt();
					float x = dbin.readFloat();
					float y = dbin.readFloat();
					float objx = dbin.readFloat();
					float objy = dbin.readFloat();

					float px = x + 5;
					float py = y + 5;

					float dx =objx- px;
					float dy = objy - py;

					float vproj = 400;
					double ang = Math.atan2(dy, dx);
					float vx = (float) (vproj * Math.cos(ang));
					float vy = (float) (vproj * Math.sin(ang));

					for(int i = 0; i < MainCanvas.instance.listaDePersonagens.size();i++) {
						Personagem pers = MainCanvas.instance.listaDePersonagens.get(i);
						if(pers.ID == id) {
							Projetil pro = new Projetil(x, y, vx, vy, pers);
							MainCanvas.instance.listaDeProjetil.add(pro);
						}
					}

					//for(int i = 0; i < MainCanvas.instance.listaDePersonagens.size();i++){
					//	Personagem pers = MainCanvas.instance.listaDePersonagens.get(i);
					//	if(pers.ID != id){
					//		MainCanvas.instance.listaDePersonagens.get(i).isAlive = false;
					//		//break;
					//	}
					//}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			break;
		case 18:
			if(msg.getData()!=null){
				bin = new ByteArrayInputStream(msg.getData());
				dbin = new DataInputStream(bin);

				try {
					int id = dbin.readInt();
					int life = dbin.readInt();
					float x = dbin.readFloat();
					float y = dbin.readFloat();

					for(int i = 0; i < MainCanvas.instance.listaDePersonagens.size();i++){
						Personagem pers = MainCanvas.instance.listaDePersonagens.get(i);
						if(pers.ID == id){
							MainCanvas.instance.listaDePersonagens.get(i).isAlive = true;
							MainCanvas.instance.listaDePersonagens.get(i).life = life;
							MainCanvas.instance.listaDePersonagens.get(i).X = x;
							MainCanvas.instance.listaDePersonagens.get(i).Y = y;
							break;
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			break;
		default:
			break;
		}
	}

	public void sendMsgPing(){
		ByteArrayOutputStream bout = new ByteArrayOutputStream(4);
		DataOutputStream dout = new DataOutputStream(bout);
		try {
			dout.writeLong(System.currentTimeMillis());
		} catch (IOException e) {
			e.printStackTrace();
		}

		NetMessage msg = new NetMessage(1, bout.toByteArray());
		sendNetMessage(msg);
	}

	public void sendMsgLogin(String usuario, String senha){
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		DataOutputStream dout = new DataOutputStream(bout);
		try {
			dout.writeUTF(usuario);
			dout.writeUTF(senha);
		} catch (IOException e) {
			e.printStackTrace();
		}
		NetMessage msg = new NetMessage(3, bout.toByteArray());
		sendNetMessage(msg);
	}

	/*
	 * @param imgname nome da imagen 
	 * @param tipo tipo do comando 0 - pede preview 1 - pede imagem grande
	 */
	public void sendMsgPedeArquivo(String imgname,int codigo){
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		DataOutputStream dout = new DataOutputStream(bout);
		try {
			dout.writeInt(codigo);
			dout.writeUTF(imgname);
		} catch (IOException e) {
			e.printStackTrace();
		}

		NetMessage msg = new NetMessage(6, bout.toByteArray());
		sendNetMessage(msg);
	}
	
	public void sendMsgCadastra(String usuario, String senha, int raca) {
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		DataOutputStream dout = new DataOutputStream(bout);
		try {
			dout.writeUTF(usuario);
			dout.writeUTF(senha);
			dout.writeInt(raca);
		} catch (IOException e) {
			e.printStackTrace();
		}
		NetMessage msg = new NetMessage(4, bout.toByteArray());
		sendNetMessage(msg);
	}

	public void sendMsgMovimentaPersonagem(float x,float y,float objX,float objY){
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		DataOutputStream dout = new DataOutputStream(bout);
		try {
			dout.writeFloat(x);
			dout.writeFloat(y);
			dout.writeFloat(objX);
			dout.writeFloat(objY);
		} catch (IOException e) {
			e.printStackTrace();
		}
		NetMessage msg = new NetMessage(13, bout.toByteArray());
		sendNetMessage(msg);
	}

	public void sendMsgtiro(float x, float y, float objX, float objY) {
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		DataOutputStream dout = new DataOutputStream(bout);
		try {
			dout.writeFloat(x);
			dout.writeFloat(y);
			dout.writeFloat(objX);
			dout.writeFloat(objY);
		} catch (IOException e) {
			e.printStackTrace();
		}
		NetMessage msg = new NetMessage(15, bout.toByteArray());
		sendNetMessage(msg);
	}


	public void sendMsgRespawn(int life, float rndX, float rndY){
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		DataOutputStream dout = new DataOutputStream(bout);

		try {
			dout.writeInt(life);
			dout.writeFloat(rndX);
			dout.writeFloat(rndY);
		} catch (IOException e) {
			e.printStackTrace();
		}

		NetMessage msg = new NetMessage(17, bout.toByteArray());
		sendNetMessage(msg);
	}
}

class ReceiveData{
	String nome = "";
	int codigo = -1;
	int size = 0;
	ByteArrayOutputStream bout = new ByteArrayOutputStream();
}
