package PALMSOFT.genericServer.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.filter.logging.LogLevel;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

import PALMSOFT.genericServer.server.controler.DadosServer;
import PALMSOFT.genericServer.server.controler.ServerControler;
import PALMSOFT.genericServer.server.protocol.GameServerCodecFactory;

public class MainServer {
	private static final int PORT = 5321;
	
	public static void main(String[] args) throws IOException
	{
		//TODO
		DadosServer.carregaDadosDisco();
		
		DadosServer.servercontroler = new ServerControler();
		DadosServer.servercontroler.inicia();
		
		
		IoAcceptor acceptor = new NioSocketAcceptor();
		LoggingFilter loggingFilter = new LoggingFilter();
		loggingFilter.setMessageReceivedLogLevel(LogLevel.NONE);
		loggingFilter.setMessageSentLogLevel(LogLevel.NONE);
		
		acceptor.getFilterChain().addLast("logger",loggingFilter);
		acceptor.getFilterChain().addLast("codec",new ProtocolCodecFilter(new GameServerCodecFactory()));
		acceptor.setHandler(new MinaServerHandler());
		acceptor.getSessionConfig().setReadBufferSize(2048);
		acceptor.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, 10);
		acceptor.bind(new InetSocketAddress(PORT));
		System.out.println("Iniciou o Server");
	}

}
