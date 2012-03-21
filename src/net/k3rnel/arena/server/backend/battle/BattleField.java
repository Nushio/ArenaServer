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
package net.k3rnel.arena.server.backend.battle;

import java.util.ArrayList;

import net.k3rnel.arena.server.backend.entity.PlayerChar;


/**
 * @author Nushio
 *
 */
public class BattleField {
	
	private BattleArea player1;
	private BattleArea player2;
	private ArrayList<PlayerChar> spectators;
	/**
	 * @param playerChar
	 * @param otherPlayer
	 */
	public BattleField(PlayerChar player1, PlayerChar player2) {
		this.player1 = new BattleArea(player1);
		this.player2 = new BattleArea(player2);
	}
	/**
	 * @return the player1
	 */
	public BattleArea getPlayer1() {
		return player1;
	}
	/**
	 * @param player1 the player1 to set
	 */
	public void setPlayer1(BattleArea player1) {
		this.player1 = player1;
	}
	/**
	 * @return the player2
	 */
	public BattleArea getPlayer2() {
		return player2;
	}
	/**
	 * @param player2 the player2 to set
	 */
	public void setPlayer2(BattleArea player2) {
		this.player2 = player2;
	}
	/**
	 * @return the spectators
	 */
	public ArrayList<PlayerChar> getSpectators() {
		return spectators;
	}
	/**
	 * @param spectators the spectators to set
	 */
	public void setSpectators(ArrayList<PlayerChar> spectators) {
		this.spectators = spectators;
	}
}
