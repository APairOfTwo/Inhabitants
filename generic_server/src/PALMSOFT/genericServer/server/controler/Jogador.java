package PALMSOFT.genericServer.server.controler;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.mina.core.session.IoSession;

import PALMSOFT.genericServer.server.protocol.NetMessage;

public class Jogador {
	public String Nome = "";
	public String Senha = null;
	public int raca;
	public IoSession session = null;
	
	public Personagem personagem = null;
	
	public long timeping = 0;
	public Projetil projetil = null;
	
	public Jogador(String nome, String senha, int raca) {
		this.Nome = nome;
		this.Senha = senha;
		this.raca = raca;
		
//		switch(raca){
//		case 0:
//		personagem = new Personagem(DadosServer.rnd.nextInt(),DadosServer.rnd.nextInt(100), DadosServer.rnd.nextInt(100),raca);
//		break;
//		case 1:
//			personagem = new Personagem(DadosServer.rnd.nextInt(),DadosServer.rnd.nextInt(100), DadosServer.rnd.nextInt(100),raca);
//			break;
//		}
		personagem = new Personagem(DadosServer.rnd.nextInt(),DadosServer.rnd.nextInt(100), DadosServer.rnd.nextInt(100));
		projetil = new Projetil(-1, -1, 0, 0, this);
	}
	
	public void SimulaSe(int diftime){
		personagem.SimulaSe(diftime);
		projetil.SimulaSe(diftime);
	}
	
	public void ping(long time){
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		DataOutputStream dout = new DataOutputStream(bout);
		
		try {
			dout.writeLong(time);		
			session.write(new  NetMessage(1, bout.toByteArray()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
