/**
 *  Copyright (C) 2012 K3RNEL Developer Team
 *  
 *  This file is part of Distro Wars (Server).
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *  
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.k3rnel.arena.server.feature;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import net.k3rnel.arena.server.network.NetworkProtocols.ChatData;
import net.k3rnel.arena.server.network.NetworkProtocols.ChatData.ChatType;

import com.esotericsoftware.kryonet.Connection;


/**
 * Handles chat messages sent by players
 * @author shadowkanji
 * @author Nushio
 *
 */
public class ChatManager implements Runnable {
	private Thread m_thread;
	@SuppressWarnings("unused")
	private boolean m_isRunning;
	/*
	 * Local chat queue
	 * [Message, x, y]
	 */
	private Queue<ChatContainer> m_localQueue;
	/*
	 * Private chat queue
	 * [session, sender, message]
	 */
	private Queue<ChatContainer> m_privateQueue;
	
	/**
	 * Default Constructor
	 */
	public ChatManager() {
		m_thread = new Thread(this);
		m_localQueue = new ConcurrentLinkedQueue<ChatContainer>();
		m_privateQueue = new ConcurrentLinkedQueue<ChatContainer>();
	}
	
	/**
	 * Returns how many messages are queued in this chat manager
	 * @return
	 */
	public int getProcessingLoad() {
		return m_localQueue.size() + m_privateQueue.size();
	}
	
	/**
	 * Queues a local chat message
	 * @param message
	 */
	public void queueLocalChatMessage(Connection conn,ChatData data) {
		m_localQueue.add(new ChatContainer(conn,data));
	}
	
	/**
	 * Queues a private chat message
	 * @param message
	 * @param receiver
	 * @param sender
	 */
	public void queuePrivateMessage(Connection conn,ChatData data) {
		m_privateQueue.add(new ChatContainer(conn,data));
	}
	
	/**
	 * Called by m_thread.start()
	 */
	public void run() {
		Connection conn;
		ChatData data;
		while(true) {
			//Send next local chat message
			if(m_localQueue.peek() != null) {
				conn = m_localQueue.poll().conn;
			}
			//Send next private chat message
			if(m_privateQueue.peek() != null) {
				data = m_privateQueue.poll().data;
				conn = m_privateQueue.poll().conn; 
				if(conn.isConnected()){
					/**
					 * RegistrationData ro = new RegistrationData();
						ro.state = 5; //State 5 is Error, Something went wrong.    
						conn.sendTCP(ro);
					 */
					data.type = ChatType.PRIVATE;
					conn.sendTCP(data);
				}
			}
			try {
				Thread.sleep(250);
			} catch (Exception e) {}
		}
	}
	
	/**
	 * Start this chat manager
	 */
	public void start() {
		m_isRunning = true;
		m_thread.start();
	}
	
	/**
	 * Stop this chat manager
	 */
	public void stop() {
		m_isRunning = false;
	}
	class ChatContainer{
		Connection conn;
		ChatData data;
		
		ChatContainer(Connection connection, ChatData chat){
			conn = connection;
			data = chat;
		}		
	}
}
