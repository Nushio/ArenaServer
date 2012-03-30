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
package net.k3rnel.arena.server.backend.entity;

/**
 * Represents an item.
 * @author HeikaHaku
 *
 */
public class Item {

	/*
	 * An Enum for the type of item.
	 */
	public enum Type { ITEM, WEAPON, ARMOR, MONEY }
	//id, name, description, maxquantity (int), tradeable (boolean)
	private Type m_type;
	private String m_name;
	private int m_id;
	private String m_desc;
	private int m_max;
	private boolean m_trade;
	
	/**
	 * Creates an Item with default parameters.
	 */
	public Item() {
		this(Type.ITEM, "", "", 1, true);
	}
	
	/**
	 * Construct an Item.
	 * @param Type type - The type of item.
	 * @param String name - The name of the item.
	 * @param String description - This item's description.
	 * @param Int maxQuantity - The maximum number of this item you can hold.
	 * @param Boolean tradeable - Can this item be traded?
	 */
	public Item(Type type, String name, String description, int maxQuantity, boolean tradeable) {
		m_type = type;
		m_name = name;
		m_desc = description;
		m_max = maxQuantity;
		m_trade = tradeable;
	}
	
	/**
	 * Sets the type of item.
	 * @param type
	 */
	public void setType(Type type) {
		m_type = type;
	}
	
	/**
	 * Sets the item's name.
	 * @param name
	 */
	public void setName(String name) {
		m_name = name;
	}
	
	/**
	 * Set's the item's description
	 * @param description
	 */
	public void setDescription(String description) {
		m_desc = description;
	}
	
	/**
	 * Set's the item's Max Quantity.
	 * @param max
	 */
	public void setMax(int max) {
		m_max = max;
	}
	
	/**
	 * Sets whether or not this item can be traded.
	 * @param tradeable
	 */
	public void setTradeable(boolean tradeable) {
		m_trade = tradeable;
	}
	
	/**
	 * Returns the item's type.
	 * @return Type
	 */
	public Type getType() {
		return m_type;
	}
	
	/**
	 * Returns the item's name
	 * @return String
	 */
	public String getName() {
		return m_name;
	}
	
	/**
	 * Returns the item's description.
	 * @return String
	 */
	public String getDescription() {
		return m_desc;
	}
	
	/**
	 * Returns the item's Max Quantity.
	 * @return int
	 */
	public int getMax() {
		return m_max;
	}
	
	/**
	 * Return's whether or not the item can be traded.
	 * @return boolean
	 */
	public boolean getTradeable() {
		return m_trade;
	}
	
}
