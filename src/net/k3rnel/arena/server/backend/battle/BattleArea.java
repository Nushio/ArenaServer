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

import net.k3rnel.arena.server.backend.entity.PlayerChar;


/**
 * @author Nushio
 *
 */
public class BattleArea {

	private PlayerChar player;
	/**
	 * @param player1
	 */
	public BattleArea(PlayerChar player) {
		this.player = player;
	}
	/**
	 * @return the player
	 */
	public PlayerChar getPlayer() {
		return player;
	}
	/**
	 * @param player the player to set
	 */
	public void setPlayer(PlayerChar player) {
		this.player = player;
	}
}
