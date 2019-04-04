package com.risk.model.Strategy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.risk.business.IStrategy;
import com.risk.business.impl.ManagePlayer;
import com.risk.model.GamePlay;
import com.risk.model.GamePlayTerritory;
import com.risk.model.Player;

/**
 * Concrete implementation of Cheating Strategy in terms of Strategy design
 * Pattern, during our GamePlay.
 * 
 * @author <a href="mailto:himansipatel1994@gmail.com">Himansi Patel</a>
 * @version 0.0.3
 */
public class Cheater implements IStrategy {

	/**
	 * Reinforcement for a Cheater Player as per Strategy Design Pattern.This Player
	 * doubles the number of armies on all its countries.
	 * 
	 * @see com.risk.business.IStrategy#reinforce(com.risk.model.GamePlay)
	 * @author <a href="mailto:himansipatel1994@gmail.com">Himansi Patel</a>
	 */
	@Override
	public GamePlay reinforce(GamePlay game_play) {
		String territories = "";
		for (Player player : game_play.getGame_state()) {
			if (player.getId() == game_play.getCurrent_player()) {
				player.setArmy_stock(0);
				for (GamePlayTerritory territory : player.getTerritory_list()) {
					int old_army_value = territory.getNumber_of_armies();
					int new_army_value = old_army_value * 2;
					territory.setNumber_of_armies(new_army_value);
					territories = territories.concat(territory.getTerritory_name()).concat(" , ");
				}
			}
		}
		territories = territories.substring(0, territories.length() - 3);
		game_play.setStatus("Cheater Doubled Armies on Territories: " + territories + "\n");
		return game_play;
	}

	/**
	 * Attack for a Cheater Player as per Strategy Design Pattern.This Player
	 * automatically conquers all the neighbors of all its countries.
	 * 
	 * @see com.risk.business.IStrategy#attack(com.risk.model.GamePlay)
	 * @author <a href="mailto:himansipatel1994@gmail.com">Himansi Patel</a>
	 */
	@Override
	public GamePlay attack(GamePlay game_play) {
		ManagePlayer manage_player = new ManagePlayer();
		String territories_won = "";
		boolean any_territory_occupied = false;
		Player current_player = null;
		Player defender_player = null;
		List<String> neighbours = null;
		List<String> player_territories = new ArrayList<>();
		GamePlayTerritory defender_territory_data = null;
		GamePlayTerritory attacker_territory_data = null;
		for (Player player : game_play.getGame_state()) {
			if (player.getId() == game_play.getCurrent_player()) {
				current_player = player;
				break;
			} else {
				continue;
			}
		}
		if (current_player != null) {
			for (GamePlayTerritory territory : current_player.getTerritory_list()) {
				player_territories.add(territory.getTerritory_name());
			}
			List<GamePlayTerritory> attacker_territories = new ArrayList<>();
			for (GamePlayTerritory gamePlayTerritory : current_player.getTerritory_list()) {
				GamePlayTerritory newTerritory = new GamePlayTerritory();
				newTerritory.setContinent_name(gamePlayTerritory.getContinent_name());
				newTerritory.setNumber_of_armies(gamePlayTerritory.getNumber_of_armies());
				newTerritory.setTerritory_name(gamePlayTerritory.getTerritory_name());
				attacker_territories.add(newTerritory);
			}

			for (GamePlayTerritory territory : attacker_territories) {
				attacker_territory_data = territory;
//				For finding neighbour of attacker territory
				for (com.risk.model.gui.Territory neighbour : game_play.getGui_map().getTerritories()) {
					if (territory.getTerritory_name().equalsIgnoreCase(neighbour.getName())) {
						neighbours = new ArrayList<>();
						neighbours = Arrays.asList(neighbour.getNeighbours().split(";"));
						break;
					} else {
						continue;
					}
				}
				for (com.risk.model.gui.Territory defender_territory : game_play.getGui_map().getTerritories()) {
					if (defender_territory.getName().equalsIgnoreCase(territory.getTerritory_name())
							|| player_territories.contains(defender_territory.getName())
							|| !neighbours.contains(defender_territory.getName())) {
						continue;
					} else {
						Boolean flag_attack_over = false;
						for (Player defender : game_play.getGame_state()) {
							if (defender.getId() == current_player.getId()) {
								continue;
							}
							for (GamePlayTerritory defend_territory : defender.getTerritory_list()) {
								if (attacker_territory_data.getNumber_of_armies() <= 1) {
									flag_attack_over = true;
									break;
								}
								if (defend_territory.getTerritory_name()
										.equalsIgnoreCase(defender_territory.getName())) {
									defender_player = defender;
									defender_territory_data = defend_territory;
									defender_territory_data.setNumber_of_armies(1);
									current_player.getTerritory_list().add(defender_territory_data);
									player_territories.add(defender_territory_data.getTerritory_name());
									attacker_territory_data
											.setNumber_of_armies(attacker_territory_data.getNumber_of_armies() - 1);
									current_player.getTerritory_list().set(
											current_player.getTerritory_list().indexOf(attacker_territory_data),
											attacker_territory_data);
									defender_player.getTerritory_list().remove(
											defender_player.getTerritory_list().indexOf(defender_territory_data));
									territories_won = territories_won
											.concat(defender_territory_data.getTerritory_name()).concat(" , ");
									flag_attack_over = true;
									any_territory_occupied = true;
									current_player.setAny_territory_occupied(any_territory_occupied);
									break;
								}
							}

							if (flag_attack_over) {
								break;
							}
						}
					}
				}
			}
		}
		if (territories_won.length() >= 4) {
			territories_won = territories_won.substring(0, territories_won.length() - 3);
		}
		String old_message = "\nTerritories won by Cheater: " + territories_won + "\n";
		manage_player.checkForWinner(game_play);

		if (game_play.getStatus() != null && game_play.getStatus().length() > 0) {

			old_message = game_play.getStatus() + old_message;
			game_play.setStatus(old_message);
		} else {
			game_play.setStatus(old_message);
		}
		return game_play;
	}

	/**
	 * Fortify for a Cheater Player as per Strategy Design Pattern.This player
	 * doubles the number of armies on its countries that have neighbors that belong
	 * to other players.
	 * 
	 * @see com.risk.business.IStrategy#fortify(com.risk.model.GamePlay)
	 * @author <a href="mailto:himansipatel1994@gmail.com">Himansi Patel</a>
	 */
	@Override
	public GamePlay fortify(GamePlay game_play) {

		String territories = "";
		Player current_player = null;
		List<String> neighbours = null;
		List<String> player_territories = new ArrayList<>();
		GamePlayTerritory cheater_territory_data = null;

		for (Player player : game_play.getGame_state()) {
			if (player.getId() == game_play.getCurrent_player()) {
				current_player = player;
				break;
			} else {
				continue;
			}
		}
		if (current_player != null) {
			for (GamePlayTerritory territory : current_player.getTerritory_list()) {
				player_territories.add(territory.getTerritory_name());
			}
			for (GamePlayTerritory territory : current_player.getTerritory_list()) {
				cheater_territory_data = territory;
				for (com.risk.model.gui.Territory neighbour : game_play.getGui_map().getTerritories()) {
					if (territory.getTerritory_name().equalsIgnoreCase(neighbour.getName())) {
						neighbours = new ArrayList<>();
						neighbours = Arrays.asList(neighbour.getNeighbours().split(";"));
						break;
					} else {
						continue;
					}
				}
				for (com.risk.model.gui.Territory neighbour_territory : game_play.getGui_map().getTerritories()) {
					if (neighbour_territory.getName().equalsIgnoreCase(territory.getTerritory_name())
							|| player_territories.contains(neighbour_territory.getName())
							|| !neighbours.contains(neighbour_territory.getName())) {
						continue;
					} else {
						cheater_territory_data.setNumber_of_armies(cheater_territory_data.getNumber_of_armies() * 2);
						territories = territories.concat(cheater_territory_data.getTerritory_name()).concat(" , ");
						current_player.getTerritory_list().set(
								current_player.getTerritory_list().indexOf(cheater_territory_data),
								cheater_territory_data);
						break;
					}
				}
			}
		}
		if (territories.length() >= 4) {
			territories = territories.substring(0, territories.length() - 3);
			game_play.setStatus("Territories fortified by Cheater: " + territories + "\n");
		} else {
			game_play.setStatus("No territories to fortify for the Cheater.\n");
		}
		return game_play;
	}
}
