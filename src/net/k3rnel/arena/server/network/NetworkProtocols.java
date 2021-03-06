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
package net.k3rnel.arena.server.network;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;

/**
 * New class!
 * The NetworkProtocol outlines a lot of things, so pay attention!
 * Communication between clients and servers is determined here.
 * This class should be shared between client and server.  
 * @author Nushio
 *
 */
public class NetworkProtocols {
	public static  final int tcp_port = 7002; //This is the TCP Port. Duh 

	/**
	 * This registers all classes that will be sent over the network
	 * If the class isn't here, we won't know what to do with the received packet!
	 * Soylent Beans is People
	 * @param endPoint
	 */
	public static void register (EndPoint endPoint) {
		Kryo kryo = endPoint.getKryo();
		
		kryo.register(LoginData.class); //This is the Login bean.
		kryo.register(RegistrationData.class); //This is the Registration bean.
		kryo.register(PasswordChangeData.class); //This is the PasswordChange bean.
		kryo.register(ChatData.class); //This is the ChatMessage, written below.
		
		
		//kyro.register(Sample.class);//If you don't register your class, we won't know what to do!
	}

	//Used to send login packets
	static public class LoginData {
		public String username;
		public String password;
		public int language;
		public boolean force;
		/**
		 * State 0 is Successful login. Awesome. 
		 * State 1 is Error, Player Limit Reached.<br>
		 * State 2 is Error, Database is AFK.<br>
		 * State 3 is Error, Username or Password Wrong.<br>
		 * State 99 is Error, F.U.B.A.R. Check Logs.<br>
		 */
		public int state;
		public int hours;
        public int minutes;
	}
	
	static public class RegistrationData {
		public String username;
		public String password;
		public String email;
		/**
		 * State 0 means Success, everything is A-Ok!<br>
	     * State 1 means Error, Username exists or is 'forbidden'.<br>
	     * State 2 means Error, Email taken.<br>
	     * State 3 is Error, email's too long.<br>
	     * State 4 is Error, Database is offline.<br>
	     * State 99 is Error, What a Terrible Failure.<br>
	     *
	     */
		public int state;
	}
	static public class PasswordChangeData {
		public String username;
		public String oldPassword;
		public String newPassword;
		/**
		 * State 0 means Password Changed
		 * State 1 means Wrong old password
		 * State 99 means What a Terrible Failure
		 */
		public int state;
	}
	//Used for the chatroom. Target is the username, or room name. Rooms start with _
	static public class ChatData {
		public enum ChatType { LOCAL, PRIVATE, NPC }
		public String target;
		public ChatType type;
		public String message;
	}
}