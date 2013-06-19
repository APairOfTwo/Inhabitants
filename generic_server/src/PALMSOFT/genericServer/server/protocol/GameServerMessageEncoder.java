/*
* @project : GameServer Framework
* @file Name : GameServerMessageEncoder.java
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

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

import PALMSOFT.genericServer.server.controler.DadosServer;

public class GameServerMessageEncoder implements ProtocolEncoder {
	//private static final Logger LOGGER = Logger.getLogger(GameServerMessageEncoder.class);
	
	public GameServerMessageEncoder() {
		
	}

	@Override
	public void dispose(IoSession session) throws Exception {

	}

	@Override
	public void encode(IoSession session, Object _message, ProtocolEncoderOutput out) throws Exception {
		NetMessage message = (NetMessage) _message;
		int size = message.getSize();
		short id = (short) message.getId();
		
		IoBuffer buf = IoBuffer.allocate(size + 4 + 2);
		buf.setAutoExpand(false);
		
		buf.putInt(size);
		buf.putShort(id);
		if (size > 0) {
			buf.put(message.getData());
		}
		
		buf.flip();
		out.write(buf);
	}
}
