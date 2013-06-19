package game_connection;
/*
* @project : GameServer Framework
* @file Name : NetMessage.java
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


public class NetMessage {
	private int id;
	private int size;
	private byte[] data;
	
	public NetMessage (int id, byte[] data) {		
		this.id = id;
		this.data = data;
		
		if (data != null) {
			this.size = data.length;
		} else {
			this.size = 0;
		}
	}

	public int getId() {
		return id;
	}
	
	public int getSize() {
		return size;
	}

	public byte[] getData() {
		return data;
	}
	
	public void destroy() {
		this.id = 0;
		this.size = 0;
		this.data = null;
	}
	
	public String toString() {
		return "NETMESSAGE:ID=" + id + ";SIZE=" + size;
	}
}
