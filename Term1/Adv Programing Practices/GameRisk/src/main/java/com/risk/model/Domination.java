package com.risk.model;

import java.util.List;
import java.util.Observable;

/**
 * This class represents the domination view implemented of player.
 * 
 * @author <a href="zinnia.rana.22@gmail.com">Zinnia Rana</a>
 * @author <a href="mayankjariwala1994@gmail.com">Mayank Jariwala</a>
 * @version 0.0.1
 */

public class Domination extends Observable {

	private int player_id;

	/**
	 * Continents Occupy by Player
	 */
	private List<String> player_continent_list;

	/**
	 * Total Map Cover By Player (in %)
	 */
	private double map_coverage;

	/**
	 * Total Army Player Have(Including All Territories)
	 */
	private int player_army_count;

	/**
	 * @return the player_id
	 */
	public int getPlayer_id() {
		return player_id;
	}

	/**
	 * @param player_id the player_id to set
	 */
	public void setPlayer_id(int player_id) {
		this.player_id = player_id;
	}

	/**
	 * @return the player_continent_list
	 */
	public List<String> getPlayer_continent_list() {
		return player_continent_list;
	}

	/**
	 * @param player_continent_list the player_continent_list to set
	 */
	public void setPlayer_continent_list(List<String> player_continent_list) {
		this.player_continent_list = player_continent_list;
	}

	/**
	 * @return the map_coverage
	 */
	public double getMap_coverage() {
		return map_coverage;
	}

	/**
	 * @param map_coverage the map_coverage to set
	 */
	public void setMap_coverage(double map_coverage) {
		this.map_coverage = map_coverage;
	}

	/**
	 * @return the player_army_count
	 */
	public int getPlayer_army_count() {
		return player_army_count;
	}

	/**
	 * @param player_army_count the player_army_count to set
	 */
	public void setPlayer_army_count(int player_army_count) {
		this.player_army_count = player_army_count;
	}

	/**
	 * This method updates player statistics
	 * 
	 * @param game_play GamePlay object
	 */
	public void updateDomination(GamePlay game_play) {
		setChanged();
		notifyObservers(game_play);
	}
}