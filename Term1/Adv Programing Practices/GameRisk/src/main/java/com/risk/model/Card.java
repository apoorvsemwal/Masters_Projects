package com.risk.model;

/**
 * Model class for Card
 * 
 * @author <a href="mailto:himansipatel1994@gmail.com">Himansi Patel</a>
 * @version 0.0.1
 */
public class Card {
	private String territory_name;
	private String army_type;

	/**
	 * @return the territory_name
	 */
	public String getTerritory_name() {
		return territory_name;
	}

	/**
	 * @param territory_name : the territory_name to set
	 */
	public void setTerritory_name(String territory_name) {
		this.territory_name = territory_name;
	}

	/**
	 * @return the army_type
	 */
	public String getArmy_type() {
		return army_type;
	}

	/**
	 * @param army_type : the army_type to set
	 */
	public void setArmy_type(String army_type) {
		this.army_type = army_type;
	}
}
