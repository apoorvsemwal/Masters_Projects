package com.risk.business.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Observable;
import java.util.Observer;

import com.risk.business.IManageDomination;
import com.risk.model.Continent;
import com.risk.model.Domination;
import com.risk.model.GamePlay;
import com.risk.model.GamePlayTerritory;
import com.risk.model.Player;

/**
 * This class is the Concrete Implementation for interface IManageDomination.
 * 
 * @author <a href="mailto:mayankjariwala1994@gmail.com">Mayank Jariwala</a>
 * @version 0.0.2
 */
public class ManageDomination implements IManageDomination, Observer {

	/**
	 * @see com.risk.business.IManageDomination#dominationView(com.risk.model.GamePlay)
	 * 
	 * @author <a href="mailto:zinnia.rana.22@gmail.com">Zinnia Rana</a>
	 * @author <a href="mailto:mayankjariwala1994@gmail.com">Mayank Jariwala</a>
	 */
	@Override
	public GamePlay dominationView(GamePlay game_play) {
		int player_id = 0;
		int player_armies_count;
		List<Domination> player_domination_object = new ArrayList<>();
		Domination domination;
		ManagePlayer manage_player = new ManagePlayer();
		int player_territories_count = 0;
		int total_map_territories = manage_player.getTerritories(game_play.getMap()).size();
		for (Player player : game_play.getGame_state()) {
			List<String> continent_occupy = new ArrayList<>();
			HashMap<String, Integer> continent_territory_count = continentTerritoryCount(game_play);
			domination = new Domination();
			player_armies_count = 0;
			player_id = player.getId();
			domination.setPlayer_id(player_id);
			player_territories_count = player.getTerritory_list().size();
			double coverage = (double) player_territories_count / (double) total_map_territories;
			coverage = Double.valueOf(String.format("%.1f", coverage * 100));
			domination.setMap_coverage(coverage);
			for (GamePlayTerritory territory : player.getTerritory_list()) {
				player_armies_count += territory.getNumber_of_armies();
			}
			domination.setPlayer_army_count(player_armies_count);
			for (GamePlayTerritory territory : player.getTerritory_list()) {
				if (continent_territory_count.containsKey(territory.getContinent_name())) {
					int territory_count = continent_territory_count.get(territory.getContinent_name());
					continent_territory_count.put(territory.getContinent_name(), territory_count - 1);
				}
			}
			for (Entry<String, Integer> entry : continent_territory_count.entrySet()) {
				if (entry.getValue() == 0)
					continent_occupy.add(entry.getKey());
			}
			continent_occupy = continent_occupy.size() > 0 ? continent_occupy : null;
			domination.setPlayer_continent_list(continent_occupy);
			player_domination_object.add(domination);
		}
		game_play.setDomination(player_domination_object);
		return game_play;
	}

	/**
	 * This function is use to store information about total no. of territory count
	 * in Continent (Mapping of Terrtiory Count in Continent)
	 * 
	 * @author <a href="mailto:mayankjariwala1994@gmail.com">Mayank Jariwala</a>
	 * @param game_play State of Game
	 * @return HashMap Count of Territory in respective Continent
	 */
	private HashMap<String, Integer> continentTerritoryCount(GamePlay game_play) {
		// Count of Territory in Specific Continent
		HashMap<String, Integer> continent_territory_count = new HashMap<>();
		HashMap<String, Continent> continents_list = game_play.getMap().getContinents();
		for (Entry<String, Continent> entry : continents_list.entrySet()) {
			continent_territory_count.put(entry.getValue().getName(), entry.getValue().getTerritories().size());
		}
		return continent_territory_count;
	}

	/**
	 * This method here serves for the implementation of Observer Pattern in our
	 * Project. It handles Domination view during game play as per risk rules.This
	 * class here is being observed by ManageDomination as an observer.
	 * 
	 * @author <a href="mailto:mayankjariwala1994@gmail.com">Mayank Jariwala</a>
	 */
	@Override
	public void update(Observable o, Object arg) {
		dominationView((GamePlay) arg);
	}

}
