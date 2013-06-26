package PALMSOFT.genericServer.server;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Date;
import java.util.Iterator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.util.byteaccess.ByteArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;

import PALMSOFT.genericServer.server.controler.DadosServer;
import PALMSOFT.genericServer.server.controler.Jogador;
import PALMSOFT.genericServer.server.controler.Personagem;
import PALMSOFT.genericServer.server.protocol.NetMessage;

public class MinaServerHandler extends IoHandlerAdapter {
	private final Logger logger = (Logger) LoggerFactory.getLogger(getClass());

	@Override
	public void sessionOpened(IoSession session) {
		// set idle time to 10 seconds
		session.getConfig().setIdleTime(IdleStatus.BOTH_IDLE, 260);
		//session.setAttribute("Values: ");
		session.write(createTextMessage(0,"CONECTADO AO SERVER..."));
		session.write(createTextMessage(0,"Bem vindo ao servidor da turminha de jogos "));
	}

	@Override
	public void messageReceived(IoSession session, Object message) {
		try {
			Thread.sleep(DadosServer.rnd.nextInt(50));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		trataMsgRecebida(session,(NetMessage)message);
	}

	@Override
	public void sessionIdle(IoSession session, IdleStatus status) {
		logger.info("Disconnecting the idle.");
		// disconnect an idle client
		session.close();
	}

	@Override
	public void exceptionCaught(IoSession session, Throwable cause)

	{
		session.close();

	}

	@Override
	public void sessionClosed(IoSession session) throws Exception {
		if(session.containsAttribute("jogador")){
			Jogador jogador = (Jogador)session.getAttribute("jogador"); 

			for (Iterator iterator = DadosServer.listaDeJogadoresLogados.iterator(); iterator.hasNext();) {
				Jogador outrojogador = (Jogador) iterator.next();
				if(outrojogador.personagem.ID==jogador.personagem.ID){
					iterator.remove();
					break;
				}
			}
			for (Iterator iterator = DadosServer.listaDeJogadoresLogados.iterator(); iterator.hasNext();) {
				Jogador outrojogador = (Jogador) iterator.next();
				sendJogadorDeslogouMessage(outrojogador.session,jogador.personagem.ID);
			}
		}

		super.sessionClosed(session);
	}

	public void trataMsgRecebida(IoSession session,NetMessage msg){
		Jogador jogador = null;
		if(session.containsAttribute("jogador")){
			jogador = (Jogador)session.getAttribute("jogador"); 
		}

		switch (msg.getId()) {
		case 0:
			ByteArrayInputStream bin = new ByteArrayInputStream(msg.getData());
			DataInputStream dbin = new DataInputStream(bin);
			String str;
			try {
				System.out.println(" "+msg.getSize()+" "+msg.getId());
				str = dbin.readUTF();
				logger.info("Message is: " + str);
			} catch (IOException e) {
				e.printStackTrace();
			}
			break;
		case 1:
			NetMessage nmsg = new NetMessage(2, msg.getData());
			session.write(nmsg);
			break;
		case 2:
			bin = new ByteArrayInputStream(msg.getData());
			dbin = new DataInputStream(bin);
			try {
				long pingtime = dbin.readLong();
				if(jogador!=null){
					jogador.timeping = (int)(System.currentTimeMillis()-pingtime);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			break;
		case 3:
			bin = new ByteArrayInputStream(msg.getData());
			dbin = new DataInputStream(bin);

			try {
				String usuario = dbin.readUTF();
				String senha = dbin.readUTF();

				if(DadosServer.hashJogadores.containsKey(usuario)){
					Jogador jog = DadosServer.hashJogadores.get(usuario);

					if(jog.Senha.equals(senha)){
						session.setAttribute("jogador", jog);
						jog.session = session;
						session.write(createTextMessage(0,"Login Aceito "+usuario+" "+senha));
						sendLoginStatusMessage(session,1);
						sendPersonagemMessage(session,10,jog.personagem);
						for (Iterator iterator = DadosServer.listaDeJogadoresLogados.iterator(); iterator.hasNext();) {
							Jogador outrojogador = (Jogador) iterator.next();
							sendPersonagemMessage(outrojogador.session,11,jog.personagem);
							sendPersonagemMessage(session,11,outrojogador.personagem);
						}
						DadosServer.listaDeJogadoresLogados.add(jog);
					} else {
						session.write(createTextMessage(0,"Senha Invalida "+usuario+" "+senha));
						sendLoginStatusMessage(session,0);
					}
				} else {
					Jogador jog = new Jogador(usuario,senha);
					DadosServer.hashJogadores.put(usuario, jog);

					session.setAttribute("jogador", jog);
					jog.session = session;
					session.write(createTextMessage(0,"Login Aceito "+usuario+" "+senha));
					sendLoginStatusMessage(session,1);
					sendPersonagemMessage(session,10,jog.personagem);
					for (Iterator iterator = DadosServer.listaDeJogadoresLogados.iterator(); iterator.hasNext();) {
						Jogador outrojogador = (Jogador) iterator.next();
						sendPersonagemMessage(outrojogador.session,11,jog.personagem);
						sendPersonagemMessage(session,11,outrojogador.personagem);
					}
					DadosServer.listaDeJogadoresLogados.add(jog);
				}
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			break;
		case 6:
			if(jogador!=null){
				bin = new ByteArrayInputStream(msg.getData());
				dbin = new DataInputStream(bin);
				try {
					int codigo = dbin.readInt();
					String imgname = dbin.readUTF();
					DadosServer.filesender.addFileToSend(session, imgname, codigo);
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				session.write(createTextMessage(0,"Primeiro Faça Login"));
			}
			break;
		case 13:
			if(jogador!=null){
				bin = new ByteArrayInputStream(msg.getData());
				dbin = new DataInputStream(bin);
				try {
					float x = dbin.readFloat();
					float y = dbin.readFloat();
					float objx = dbin.readFloat();
					float objy = dbin.readFloat();

					jogador.personagem.X = x;
					jogador.personagem.Y = y;
					jogador.personagem.objetivoX = objx;
					jogador.personagem.objetivoY = objy;

					jogador.personagem.deslocaSe(jogador.timeping/2);

					for (Iterator iterator = DadosServer.listaDeJogadoresLogados.iterator(); iterator.hasNext();) {
						Jogador outrojogador = (Jogador) iterator.next();
						if(jogador.personagem.ID!=outrojogador.personagem.ID){
							sendPosicaoPersonagemMessage(outrojogador.session, jogador.personagem.ID, x, y, objx, objy);
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			break;
		case 15:
			if(jogador!=null){
				bin = new ByteArrayInputStream(msg.getData());
				dbin = new DataInputStream(bin);
				try {
					float x = dbin.readFloat();
					float y = dbin.readFloat();
					float objx = dbin.readFloat();
					float objy = dbin.readFloat();
					jogador.projetil.SimulaSe(jogador.timeping/2);

					for (Iterator iterator = DadosServer.listaDeJogadoresLogados.iterator(); iterator.hasNext();) {
						Jogador outrojogador = (Jogador) iterator.next();
						if (jogador.personagem.ID != outrojogador.personagem.ID) {
							sendPosicaoTiro(outrojogador.session, jogador.personagem.ID, x, y, objx, objy);
						}
					}
					//					for (Iterator iterator = DadosServer.listaDeJogadoresLogados.iterator(); iterator.hasNext();) {
					//						Jogador outrojogador = (Jogador) iterator.next();
					//						sendMsgAtirou(outrojogador.session, jogador.personagem.ID, 1);
					//					}	
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			break;
		case 17:
			if(jogador!=null){
				bin = new ByteArrayInputStream(msg.getData());
				dbin = new DataInputStream(bin);

				try {
					int life = dbin.readInt();
					float x = dbin.readFloat();
					float y = dbin.readFloat();

					for (Iterator iterator = DadosServer.listaDeJogadoresLogados.iterator(); iterator.hasNext();) {
						Jogador outrojogador = (Jogador) iterator.next();
						sendMsgRespawnou(outrojogador.session, jogador.personagem.ID, life, x, y);
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

	public NetMessage createTextMessage(int id, String txt){
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		DataOutputStream dout = new DataOutputStream(bout);

		try {
			dout.writeUTF(txt);
			return new NetMessage(0, bout.toByteArray());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void sendLoginStatusMessage(IoSession session,int loginstatus){
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		DataOutputStream dout = new DataOutputStream(bout);

		try {
			dout.writeInt(loginstatus);
			session.write(new  NetMessage(4, bout.toByteArray()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void sendPersonagemMessage(IoSession session,int codMessage,Personagem pers){
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		DataOutputStream dout = new DataOutputStream(bout);

		try {
			dout.writeInt(pers.ID);
			dout.writeFloat(pers.X);
			dout.writeFloat(pers.Y);
			dout.writeFloat(pers.objetivoX);
			dout.writeFloat(pers.objetivoY);

			session.write(new  NetMessage(codMessage, bout.toByteArray()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void sendJogadorDeslogouMessage(IoSession session,int ID){
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		DataOutputStream dout = new DataOutputStream(bout);

		try {
			dout.writeInt(ID);			
			session.write(new  NetMessage(12, bout.toByteArray()));

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void sendPosicaoPersonagemMessage(IoSession session,int ID,float x,float y,float objX,float objY){
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		DataOutputStream dout = new DataOutputStream(bout);

		try {
			dout.writeInt(ID);	
			dout.writeFloat(x);	
			dout.writeFloat(y);	
			dout.writeFloat(objX);	
			dout.writeFloat(objY);	

			session.write(new  NetMessage(14, bout.toByteArray()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void sendMsgAtirou(IoSession session, int ID, int atirou){
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		DataOutputStream dout = new DataOutputStream(bout);

		try {
			dout.writeInt(ID);
			dout.writeInt(atirou);

			session.write(new NetMessage(16, bout.toByteArray()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void sendMsgRespawnou(IoSession session, int ID, int life, float x, float y){
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		DataOutputStream dout = new DataOutputStream(bout);

		try {
			dout.writeInt(ID);
			dout.writeInt(life);
			dout.writeFloat(x);
			dout.writeFloat(y);
			session.write(new NetMessage(18, bout.toByteArray()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void sendPosicaoTiro(IoSession session, int ID, float x, float y,
			float objX, float objY) {
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		DataOutputStream dout = new DataOutputStream(bout);

		try {
			dout.writeInt(ID);
			dout.writeFloat(x);
			dout.writeFloat(y);
			dout.writeFloat(objX);
			dout.writeFloat(objY);

			session.write(new NetMessage(16, bout.toByteArray()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
