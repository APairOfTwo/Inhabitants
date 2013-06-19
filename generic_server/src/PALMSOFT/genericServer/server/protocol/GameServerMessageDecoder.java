/*
* @project : GameServer Framework
* @file Name : GameServerMessageDecoder.java
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
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

import PALMSOFT.genericServer.server.controler.DadosServer;

public class GameServerMessageDecoder extends CumulativeProtocolDecoder {
	//private static final Logger LOGGER = Logger.getLogger(GameServerMessageDecoder.class);
	
	public GameServerMessageDecoder() {
		
	}
	
	@Override
	protected boolean doDecode(IoSession session, IoBuffer in, ProtocolDecoderOutput out) throws Exception {
		if (in.prefixedDataAvailable (GameServerCodecConfig.HEADER_LEN)) {
			int size = in.getInt();
			short id = in.getShort();
			
			//size = size - 2;
			
			
			if (size > 0) {
				byte[] data = new byte[size];
				in.get(data, 0, size);
				NetMessage message = new NetMessage (id, data);
				out.write (message);
			} else {
				NetMessage message = new NetMessage (id, null);
				out.write (message);
			}
					
			return true;
		}
		
		return false;
	}
}
