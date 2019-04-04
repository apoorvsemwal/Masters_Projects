package com.risk.model;

/**
 * GamePlay Territory Model - Basic needs for players in order start to playing
 * a game.
 * 
 * @author <a href="mailto:himansipatel1994@gmail.com">Himansi Patel</a>
 * @author <a href="mailto:mayankjariwala1994@gmail.com">Mayank Jariwala</a> -
 *         Added Model Description
 * @version 0.0.1
 */
public class GamePlayTerritory {
	private String territory_name;
	private String continent_name;
	private int number_of_armies;

	/**
	 * @return the territory_name
	 */
	public String getTerritory_name() {
		return territory_name;
	}

	/**
	 * @param territory_name the territory_name to set
	 */
	public void setTerritory_name(String territory_name) {
		this.territory_name = territory_name;
	}

	/**
	 * @return the continent_name
	 */
	public String getContinent_name() {
		return continent_name;
	}

	/**
	 * @param continent_name the continent_name to set
	 */
	public void setContinent_name(String continent_name) {
		this.continent_name = continent_name;
	}

	/**
	 * @return the number_of_armies
	 */
	public int getNumber_of_armies() {
		return number_of_armies;
	}

	/**
	 * @param number_of_armies the number_of_armies to set
	 */
	public void setNumber_of_armies(int number_of_armies) {
		this.number_of_armies = number_of_armies;
	}

	@Override
	public boolean equals(Object obj) {
		return this.getTerritory_name().equalsIgnoreCase(((GamePlayTerritory) obj).getTerritory_name());
	}

}
