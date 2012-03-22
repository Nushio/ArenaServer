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
package net.k3rnel.arena.server.database;

import java.io.Serializable;

/**
 * The Misc Bean. It stores a name-value pair. 
 * @author Nushio
 *
 */
public class Misc implements Serializable{

	private static final long serialVersionUID = -8233353125826029839L;
    private String name;
	private String value;
	
	/**
	 * Main constructor
	 */
	public Misc(){
	    
	}
	
	/**
	 * Overloaded constructor using String, int
	 * @param name
	 * @param value
	 */
    public Misc(String name, int value) {
        this.name = name;
        this.value = value+"";
    }
    
    /**
     * Overloaded constructor using String, String
     * @param name
     * @param value
     */
    public Misc(String name,String value){
        this.name = name;
        this.value = value;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * @param value the value to set
     */
    public void setValue(String value) {
        this.value = value;
    }
}
