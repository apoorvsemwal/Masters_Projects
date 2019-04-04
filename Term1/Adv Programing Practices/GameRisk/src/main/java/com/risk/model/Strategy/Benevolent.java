package com.risk.model.Strategy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

import com.risk.business.IStrategy;
import com.risk.model.GamePlay;
import com.risk.model.GamePlayTerritory;
import com.risk.model.Player;
import com.risk.model.gui.Territory;

/**
 * Concrete implementation of Benevolent Strategy in terms of Strategy design
 * Pattern, during our GamePlay.
 * 
 * @author <a href="mailto:mayankjariwala1994@gmail.com">MayankJariwala</a>
 * @version 0.0.1
 */
public class Benevolent implements IStrategy {

	// All Territories currently owned by Player
	private List<GamePlayTerritory> player_occupied_territory = null;

	// Use during reinforcement phase
	private int armies_to_place = 0;

	/**
	 * Reinforcement for a Benevolent Player as per Strategy Design Pattern. This
	 * Player main goal is to reinforce on weaker country first and keep maintaining
	 * this strategy
	 * 
	 * @see com.risk.business.IStrategy#reinforce(com.risk.model.GamePlay)
	 * @author <a href="mailto:mayankjariwala1994@gmail.com">MayankJariwala</a>
	 * @param game_play state of the game i.e. entire game related info when
	 *                  reinforcement starts for a player.
	 * @return GamePlay updated state of the game after reinforcement phase ends.
	 */
	public GamePlay reinforce(GamePlay game_play) {
		Player current_player = game_play.getGame_state().get(game_play.getCurrent_player() - 1);
		armies_to_place = current_player.getArmy_stock();
		String message = "Got " + armies_to_place + " armies to place";
		List<Integer> armies_no_list = getListofArmiesValues(current_player.getTerritory_list());
		int weak_territory_index = armies_no_list.indexOf(Collections.min(armies_no_list));
		while (armies_to_place != 0) {
			int current_armies_on_weak_territory = current_player.getTerritory_list().get(weak_territory_index)
					.getNumber_of_armies();
			current_player.getTerritory_list().get(weak_territory_index)
					.setNumber_of_armies(current_armies_on_weak_territory + 1);
			String old_message = "Placed 1 Army on "
					+ current_player.getTerritory_list().get(weak_territory_index).getTerritory_name() + "\n";
			message = old_message + message;
			armies_to_place--;
			current_player.setArmy_stock(current_player.getArmy_stock() - 1);
		}
		game_play.setStatus(message);
		return game_play;
	}

	/**
	 * Benevolent Player never believes to attack any of his opponent
	 * 
	 * @see com.risk.business.IStrategy#attack(com.risk.model.GamePlay)
	 * @author <a href="mailto:mayankjariwala1994@gmail.com">MayankJariwala</a>
	 * @param game_play state of the game i.e. entire game related info when
	 *                  fortification starts for a player.
	 * @return GamePlay Object
	 */
	@Override
	public GamePlay attack(GamePlay game_play) {
		game_play.setStatus("Benevolent Player will not attack\n");
		return game_play;
	}

	/**
	 * Fortify for a Benevolent Player as per Strategy Design Pattern. Player main
	 * goal is to fortify on weaker country from strongest country
	 * 
	 * @see com.risk.business.IStrategy#fortify(com.risk.model.GamePlay)
	 * @author <a href="mailto:mayankjariwala1994@gmail.com">MayankJariwala</a>
	 * @param game_play state of the game i.e. entire game related info when
	 *                  fortification starts for a player.
	 * @return GamePlay Object
	 */
	public GamePlay fortify(GamePlay game_play) {
		Player current_player = game_play.getGame_state().get(game_play.getCurrent_player() - 1);
		player_occupied_territory = new ArrayList<>();
		List<Integer> armies_no_list = getListofArmiesValues(current_player.getTerritory_list());
		moveArmiesFromStrongToWeakTerritory(armies_no_list, current_player.getTerritory_list(), game_play);
		return game_play;
	}

	/**
	 * This function is use to move armies from strong territory to weak territory
	 * by getting weak territory and its strongest neighbors from which this player
	 * want to move armies
	 * 
	 * @author <a href="mailto:mayankjariwala1994@gmail.com">MayankJariwala</a>
	 * @param armies_no_list          List which contains No of armies on each
	 *                                territory
	 * @param player_territories_list List of Current Player Territories
	 * @param game_play               GameState Object
	 */
	private void moveArmiesFromStrongToWeakTerritory(List<Integer> armies_no_list,
			List<GamePlayTerritory> player_territories_list, GamePlay game_play) {
		GamePlayTerritory weak_territory = null;
		GamePlayTerritory strong_territory = null;
		String message = "";
		setListofPlayerTerritories(player_territories_list);
		List<GamePlayTerritory> weak_territories_list = getWeakTerritoriesOfPlayer(armies_no_list,
				player_territories_list);
		HashMap<String, List<GamePlayTerritory>> territory_to_neighbouring = getNeighboursForFortifyPhase(
				weak_territories_list, game_play);
		if (territory_to_neighbouring != null) {
			for (Entry<String, List<GamePlayTerritory>> neighbor : territory_to_neighbouring.entrySet()) {
				weak_territory = getTerritoryObjectFromString(weak_territories_list, neighbor.getKey());
				int max = 0;
				List<GamePlayTerritory> neigbours_list = neighbor.getValue();
				if (neigbours_list.size() > 0) {
					for (int neighbours_index = 0; neighbours_index < neigbours_list.size(); neighbours_index++) {
						if (neigbours_list.get(neighbours_index).getNumber_of_armies() > max) {
							max = neigbours_list.get(neighbours_index).getNumber_of_armies();
							strong_territory = neigbours_list.get(neighbours_index);
						}
					}
					int diff = (int) Math.floor(
							Math.abs(strong_territory.getNumber_of_armies() - weak_territory.getNumber_of_armies())
									/ 2);
					if (diff < 1) {
						message += "Fortification not possible because ";
						message += " Strong Territory Info : [ " + strong_territory.getTerritory_name() + ","
								+ strong_territory.getNumber_of_armies() + " ] and ";
						message += " Weak Territory Info : [ " + weak_territory.getTerritory_name() + ","
								+ weak_territory.getNumber_of_armies() + " ]\n";
						game_play.setStatus(message);
						continue;
					} else {
						weak_territory.setNumber_of_armies(weak_territory.getNumber_of_armies() + diff);
						strong_territory.setNumber_of_armies(strong_territory.getNumber_of_armies() - diff);
						message += "Fortification Successful\n";
						message += "Strong Territory Info : [ " + strong_territory.getTerritory_name() + ","
								+ strong_territory.getNumber_of_armies() + " ] and ";
						message += "Weak Territory Info : [ " + weak_territory.getTerritory_name() + ","
								+ weak_territory.getNumber_of_armies() + " ]\n";
						message += diff + " armies moved from " + strong_territory.getTerritory_name() + " to "
								+ weak_territory.getTerritory_name();
						game_play.setStatus(message);
						return;
					}
				} else {
					message += "No Own Strong Territory Neighbours Found For Territory "
							+ weak_territory.getTerritory_name() + "\n";
					game_play.setStatus(message);
					continue;
				}
			}
		}
		return;
	}

	/**
	 * @author <a href="mailto:mayankjariwala1994@gmail.com">MayankJariwala</a>
	 * @param armies_no_list          List which contains No of armies on each
	 *                                territory
	 * @param player_territories_list List of Current Player Territories
	 * @return List of WeakTerritory List contains weak territories object
	 */
	public List<GamePlayTerritory> getWeakTerritoriesOfPlayer(List<Integer> armies_no_list,
			List<GamePlayTerritory> player_territories_list) {
		List<GamePlayTerritory> weak_territories_list = new ArrayList<>();
		int min = Collections.min(armies_no_list);
		for (int i = 0; i < armies_no_list.size(); i++) {
			if (armies_no_list.get(i) <= min) {
				weak_territories_list.add(player_territories_list.get(i));
			}
		}
		return weak_territories_list;
	}

	// Helpers Function Section Begins

	/**
	 * This method is use to get list of armies values on each territory current
	 * player owes in game
	 * 
	 * @author <a href="mailto:mayankjariwala1994@gmail.com">MayankJariwala</a>
	 * @param game_play_territory List of Current Player Territories in Game
	 * @return IntegerList List of army values on each territory current player owes
	 */
	public List<Integer> getListofArmiesValues(List<GamePlayTerritory> game_play_territory) {
		List<Integer> armies = new ArrayList<>();
		for (GamePlayTerritory territory : game_play_territory) {
			armies.add(territory.getNumber_of_armies());
		}
		return armies;
	}

	/**
	 * This function is use to get List of Current Player Territories and set to
	 * global player_occupied_territory list
	 * 
	 * @author <a href="mailto:mayankjariwala1994@gmail.com">MayankJariwala</a>
	 * @param game_play_territory List of Current Player Territories in Game
	 */
	private void setListofPlayerTerritories(List<GamePlayTerritory> game_play_territory) {
		for (GamePlayTerritory territory : game_play_territory) {
			player_occupied_territory.add(territory);
		}
	}

	/**
	 * This function is use to get neighbors of current player territories which is
	 * his own territorry (TerritoryA = Player Own Territory as neigbors of
	 * TerritoryA )
	 * 
	 * @author <a href="mailto:mayankjariwala1994@gmail.com">MayankJariwala</a>
	 * @param current_player_territories List of Territories Current Player Owes
	 * @param game_play                  Current GameState Object
	 * @return HashMap Territory with its neighbors
	 */
	private HashMap<String, List<GamePlayTerritory>> getNeighboursForFortifyPhase(
			List<GamePlayTerritory> current_player_territories, GamePlay game_play) {
		HashMap<String, List<GamePlayTerritory>> territory_to_neighbouring = new LinkedHashMap<>();
		for (GamePlayTerritory player_territory : current_player_territories) {
			for (Territory territory : game_play.getGui_map().getTerritories()) {
				if (player_territory.getTerritory_name().equalsIgnoreCase(territory.getName())) {
					List<GamePlayTerritory> player_own_occupied_neighbours_list = new ArrayList<>();
					String[] territories = territory.getNeighbours().split(";");
					for (int territory_index = 0; territory_index < territories.length; territory_index++) {
						GamePlayTerritory territory_object = isPlayerOwnTerritory(territories[territory_index]);
						if (territory_object != null)
							player_own_occupied_neighbours_list.add(territory_object);
					}
					territory_to_neighbouring.put(territory.getName(), player_own_occupied_neighbours_list);
				}
			}
		}
		return territory_to_neighbouring;
	}

	/**
	 * This function just checks that whether given territory is occupied by this
	 * current player
	 * 
	 * @author <a href="mailto:mayankjariwala1994@gmail.com">MayankJariwala</a>
	 * @param territory_name Name of Territory
	 * @return GamePlayTerritory Territory Object
	 */
	private GamePlayTerritory isPlayerOwnTerritory(String territory_name) {
		for (int i = 0; i < player_occupied_territory.size(); i++) {
			if (player_occupied_territory.get(i).getTerritory_name().equalsIgnoreCase(territory_name)) {
				return player_occupied_territory.get(i);
			}
		}
		return null;
	}

	/**
	 * This function just returns territory object by receiving territory name as
	 * parameter
	 * 
	 * @author <a href="mailto:mayankjariwala1994@gmail.com">MayankJariwala</a>
	 * @param weak_territories_list List of Current Player Weak Territories
	 * @param territory_name        Name of Territory
	 * @return GamePlayTerritory TerritoryObject
	 */
	private GamePlayTerritory getTerritoryObjectFromString(List<GamePlayTerritory> weak_territories_list,
			String territory_name) {
		GamePlayTerritory weak_territory_object = null;
		for (int i = 0; i < weak_territories_list.size(); i++) {
			if (weak_territories_list.get(i).getTerritory_name().equalsIgnoreCase(territory_name)) {
				weak_territory_object = weak_territories_list.get(i);
			}
		}
		return weak_territory_object;
	}
}
