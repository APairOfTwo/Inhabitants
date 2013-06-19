
/*
* @project : GameServer Framework
* @file Name : GameServerCodecFactory.java
* @date : 20/12/2010
*
* Copyright (c) 2005-2010 PalmSoft Tecnologia, Inc. All Rights Reserved.
*
* This software is the confidential and proprietary information of PalmSoft Tecnologia, Inc. ("Confidential Information"). You shall not
* disclose such Confidential Information and shall use it only in 
* accordance with the terms of the license agreement you entered into
* with PALMSOFT.
*
* PALMSOFT MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF
* THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
* TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
* PARTICULAR PURPOSE, OR NON-INFRINGEMENT. PALMSOFT SHALL NOT BE LIABLE FOR
* ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR
* DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
*/
package PALMSOFT.genericServer.server.protocol;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;

public class GameServerCodecFactory implements ProtocolCodecFactory {
	private final ProtocolEncoder encoder;
	private final ProtocolDecoder decoder;
	
	public GameServerCodecFactory() {
		encoder = new GameServerMessageEncoder();
		decoder = new GameServerMessageDecoder();
	}

	@Override
	public ProtocolDecoder getDecoder(IoSession arg0) throws Exception {
		return decoder;
	}

	@Override
	public ProtocolEncoder getEncoder(IoSession arg0) throws Exception {
		return encoder;
	}
}
