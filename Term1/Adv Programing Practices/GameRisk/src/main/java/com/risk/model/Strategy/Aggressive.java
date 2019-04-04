package com.risk.model.Strategy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.risk.business.IStrategy;
import com.risk.business.impl.ManagePlayer;
import com.risk.model.Attack;
import com.risk.model.GamePlay;
import com.risk.model.GamePlayTerritory;
import com.risk.model.Player;

/**
 * Concrete implementation of Aggressive strategy in terms of Strategy design
 * Pattern, during our GamePlay.
 * 
 * @author <a href="mailto:apoorv.semwal20@gmail.com">Apoorv Semwal</a>
 * @version 0.0.3
 */
public class Aggressive implements IStrategy {

	/**
	 * Reinforcement for a Aggressive Player as per Strategy Design Pattern. This
	 * Player main goal is to reinforce on stronger country first and keep
	 * maintaining this strategy
	 * 
	 * @see com.risk.business.IStrategy#reinforce(GamePlay)
	 * @author <a href="mailto:a_semwal@encs.concordia.ca">ApoorvSemwal</a>
	 */
	public GamePlay reinforce(GamePlay game_play) {

		ManagePlayer player_manager = new ManagePlayer();
		Player current_player = null;
		GamePlayTerritory strongest_territory = null;
		if (game_play != null) {
			for (Player player : game_play.getGame_state()) {
				if (player.getId() == game_play.getCurrent_player()) {
					current_player = player;
					break;
				} else {
					continue;
				}
			}
			if (current_player != null) {

				if (current_player.getCard_list()!=null && current_player.getCard_list().size() > 4) {
					game_play.setCard_trade(player_manager.prepareCardTrade(current_player));
					if (game_play.getCard_trade() != null) {
						game_play.setStatus("Army Stock before Card Trade: " + current_player.getArmy_stock()
								+ "\n3 Cards Traded.\n");
						player_manager.tradeCards(game_play);
						game_play.setStatus("\n" + "New Army Stock after Card Trade: " + current_player.getArmy_stock()
								+ game_play.getStatus());
					} else {
						game_play.setStatus("No Card Trading.\n");
					}
				}
				strongest_territory = player_manager.findStrongestTerritory(current_player);
			}

			if (strongest_territory != null) {
				strongest_territory.setNumber_of_armies(
						strongest_territory.getNumber_of_armies() + current_player.getArmy_stock());
				current_player.setArmy_stock(0);
			}
		}
		game_play.setStatus(" Strongest Territory : " + strongest_territory.getTerritory_name() + " Reinforced\n"
				+ " New Army count on strongest territory:" + strongest_territory.getNumber_of_armies()
				+ game_play.getStatus() + "\n");
		return game_play;
	}

	/**
	 * Attack for an Aggressive Player as per Strategy Design Pattern.This Player
	 * main goal is always attack with strongest territory until it cannot attack
	 * anymore.
	 * 
	 * @see com.risk.business.IStrategy#attack(com.risk.model.GamePlay)
	 * @author <a href="mailto:himansipatel1994@gmail.com">HimansiPatel</a>
	 */
	public GamePlay attack(GamePlay game_play) {

		String aggressive_attack_message = "";
		String old_message = "";
		boolean is_territory_occupied = false;
		List<GamePlayTerritory> temp_defender_list = new ArrayList<>();
		List<GamePlayTerritory> attacker_territory_list = new ArrayList<>();
		List<GamePlayTerritory> defender_territory_list = new ArrayList<>();

		ManagePlayer player_manager = new ManagePlayer();
		Attack attack = null;
		if (game_play.getAttack() != null) {
			attack = game_play.getAttack();
		} else {
			attack = new Attack();
			game_play.setAttack(attack);
		}

		GamePlayTerritory strongest_territory = null;
		List<String> neighbours = new ArrayList<>();
		List<String> player_territories = new ArrayList<>();

//		For finding Strongest Territory
		for (Player player : game_play.getGame_state()) {
			if (player.getId() == game_play.getCurrent_player()) {
				int max = 0;
				for (GamePlayTerritory territory : player.getTerritory_list()) {
					player_territories.add(territory.getTerritory_name());
					if (territory.getNumber_of_armies() > max) {
						max = territory.getNumber_of_armies();
						strongest_territory = territory;
					}
				}
//				add Strongest Territory to attacker territory list
				attacker_territory_list.add(strongest_territory);
				break;
			} else {
				continue;
			}
		}

//		For finding Neighbour 
		for (com.risk.model.gui.Territory territory : game_play.getGui_map().getTerritories()) {
			if (territory.getName().equalsIgnoreCase(strongest_territory.getTerritory_name())) {
				neighbours = Arrays.asList(territory.getNeighbours().split(";"));
				break;
			} else {
				continue;
			}
		}

		for (com.risk.model.gui.Territory defender_territory : game_play.getGui_map().getTerritories()) {
			if (strongest_territory.getNumber_of_armies() <= 1) {
				break;
			}
			if (defender_territory.getName().equalsIgnoreCase(strongest_territory.getTerritory_name())
					|| player_territories.contains(defender_territory.getName())
					|| !neighbours.contains(defender_territory.getName())) {
				continue;
			} else {
				for (Player defender : game_play.getGame_state()) {

					if (defender.getId() == game_play.getCurrent_player()) {
						continue;
					}
					for (GamePlayTerritory defend_territory : defender.getTerritory_list()) {
						if (defend_territory.getTerritory_name().equalsIgnoreCase(defender_territory.getName())) {
//							add defender territory to temporary defender territory list
							temp_defender_list.add(defend_territory);
							game_play.setGame_phase("ATTACK_ALL_OUT");
						}
					}
				}
			}
		}

		for (int defender_territory = 0; defender_territory < temp_defender_list.size(); defender_territory++) {
			int attacker_dice_no = 0;
			int defender_dice_no = 0;
			defender_territory_list = new ArrayList<>();
			if (attacker_territory_list.get(0).getNumber_of_armies() > 1) {
				if (temp_defender_list.get(defender_territory).getNumber_of_armies() > 0) {
					defender_territory_list.add(temp_defender_list.get(defender_territory));
					old_message = "\nAttacker territory: " + attacker_territory_list.get(0).getTerritory_name()
							+ " Defender Territory: " + defender_territory_list.get(0).getTerritory_name() + "\n";
					aggressive_attack_message = old_message + aggressive_attack_message;
					player_manager.setAttackerDefenderDiceNo(attacker_territory_list, defender_territory_list, attack);
					attacker_dice_no = game_play.getAttack().getAttacker_dice_no();
					defender_dice_no = game_play.getAttack().getDefender_dice_no();
					int attacker_terattrtiory_armies = attacker_territory_list.get(0).getNumber_of_armies();
					int defender_territory_armies = defender_territory_list.get(0).getNumber_of_armies();

					String valid_attack_message = player_manager.checkForValidAttack(attacker_terattrtiory_armies,
							defender_territory_armies, attacker_dice_no, defender_dice_no);

					if (valid_attack_message.trim().length() == 0) {
						// Roll Dice Result
						List<Integer> attack_result = player_manager.rollDiceDecision(attacker_dice_no,
								defender_dice_no);

						old_message = player_manager.getRollDiceMessage();
						aggressive_attack_message = old_message + aggressive_attack_message;

						for (int i = 0; i < attack_result.size(); i++) {
							int result = attack_result.get(i);
							if (result == 1) {
								// Attacker Won
								GamePlayTerritory def_obj = defender_territory_list.get(0);
								def_obj.setNumber_of_armies(def_obj.getNumber_of_armies() - 1);

								if (def_obj.getNumber_of_armies() == 0) {

									attacker_territory_list.add(def_obj);
									if (attacker_territory_list.get(0).getNumber_of_armies() > 1) {
										attacker_territory_list.get(0).setNumber_of_armies(
												attacker_territory_list.get(0).getNumber_of_armies() - 1);

										old_message = "Attacker territory: "
												+ attacker_territory_list.get(0).getTerritory_name()
												+ " occupies Defender Territory: " + def_obj.getTerritory_name() + "\n";
										aggressive_attack_message = old_message + aggressive_attack_message;
									} else {
										attacker_territory_list.get(0).setNumber_of_armies(1);

										old_message = "Attacker territory: "
												+ attacker_territory_list.get(0).getTerritory_name()
												+ " occupies Defender Territory: " + def_obj.getTerritory_name() + "\n";
										aggressive_attack_message = old_message + aggressive_attack_message;
									}

								}
								for (GamePlayTerritory temp_terr_list : temp_defender_list) {
									if (def_obj.getTerritory_name()
											.equalsIgnoreCase(temp_terr_list.getTerritory_name())) {
										temp_terr_list.setNumber_of_armies(def_obj.getNumber_of_armies());
									}
								}
								defender_territory = -1;

							} else {
//								Defender won

								GamePlayTerritory att_obj = attacker_territory_list.get(0);
								att_obj.setNumber_of_armies(att_obj.getNumber_of_armies() - 1);

								defender_territory = -1;
							}
						}

					}
				}
			} else {
				old_message = "Can't attack because strongest selected territory : "
						+ attacker_territory_list.get(0).getTerritory_name() + " has only "
						+ attacker_territory_list.get(0).getNumber_of_armies() + " army \n";
				aggressive_attack_message = old_message + aggressive_attack_message;
				break;
			}
		}

//		update defender's territory list according temporary defender list 
		for (GamePlayTerritory temp_defender_territory : temp_defender_list) {
			for (Player defender : game_play.getGame_state()) {
				if (defender.getId() == game_play.getCurrent_player()) {
					continue;
				} else {
					for (GamePlayTerritory player_territory : defender.getTerritory_list()) {
						if (temp_defender_territory.getTerritory_name()
								.equalsIgnoreCase(player_territory.getTerritory_name())) {

							if (temp_defender_territory.getNumber_of_armies() == 0) {
								defender.getTerritory_list().remove(player_territory);
								break;
							} else {
								player_territory.setNumber_of_armies(temp_defender_territory.getNumber_of_armies());
								break;
							}
						}
					}
				}
			}
		}

//		update attacker's territory list according attacker territory list 
		for (GamePlayTerritory attacker_territory : attacker_territory_list) {
			for (GamePlayTerritory player_territory : game_play.getGame_state().get(game_play.getCurrent_player() - 1)
					.getTerritory_list()) {
				if (attacker_territory.getTerritory_name().equalsIgnoreCase(player_territory.getTerritory_name())) {
					player_territory.setNumber_of_armies(attacker_territory.getNumber_of_armies());
					break;
				} else if (!game_play.getGame_state().get(game_play.getCurrent_player() - 1).getTerritory_list()
						.contains(attacker_territory)) {
					attacker_territory.setNumber_of_armies(1);
					game_play.getGame_state().get(game_play.getCurrent_player() - 1).getTerritory_list()
							.add(attacker_territory);
					is_territory_occupied = true;
					game_play.getGame_state().get(game_play.getCurrent_player() - 1)
							.setAny_territory_occupied(is_territory_occupied);
					break;
				}
			}
		}

		if (game_play.getGame_state().get(game_play.getCurrent_player() - 1).isAny_territory_occupied()) {
			player_manager.giveCardAtAttackEnd(game_play);
			if (game_play.getStatus() != null && game_play.getStatus().length() > 0) {

				old_message = game_play.getStatus();
				aggressive_attack_message = old_message + aggressive_attack_message;
				game_play.setStatus(aggressive_attack_message);
			}
		} else {
			game_play.setStatus(aggressive_attack_message);
		}
		game_play.setGame_phase("ATTACK");
		player_manager.checkForWinner(game_play);
		return game_play;
	}

	/**
	 * Fortify for a Aggressive Player as per Strategy Design Pattern..Focus on
	 * aggregating armies on one country.
	 * 
	 * @see com.risk.business.IStrategy#fortify(GamePlay)
	 * @author <a href="mailto:a_semwal@encs.concordia.ca">ApoorvSemwal</a>
	 */
	public GamePlay fortify(GamePlay game_play) {

		Player current_player = null;
		Boolean neighbour_flag = false;
		ManagePlayer player_manager = new ManagePlayer();
		String aggressive_fortify_message = "";
		String old_message = "";

		for (Player player : game_play.getGame_state()) {
			if (player.getId() == game_play.getCurrent_player()) {
				current_player = player;
				break;
			} else {
				continue;
			}
		}

		if (current_player != null) {

			for (GamePlayTerritory territory_a : current_player.getTerritory_list()) {

				if (territory_a.getNumber_of_armies() == 1) {
					continue;
				}
				for (GamePlayTerritory territory_b : current_player.getTerritory_list()) {

					neighbour_flag = false;

					if (territory_a.getTerritory_name().equalsIgnoreCase(territory_b.getTerritory_name())
							|| territory_b.getNumber_of_armies() == 1) {
						continue;
					} else {
						neighbour_flag = player_manager.checkIfNeighbours(territory_a.getTerritory_name(),
								territory_b.getTerritory_name(), game_play.getGui_map());
					}

					if (neighbour_flag == true) {

						old_message = (territory_b.getNumber_of_armies() - 1) + " army moved from "
								+ territory_b.getTerritory_name() + " to " + territory_a.getTerritory_name() + "\n";
						aggressive_fortify_message = old_message + aggressive_fortify_message;
						territory_a.setNumber_of_armies(
								territory_a.getNumber_of_armies() + territory_b.getNumber_of_armies() - 1);
						territory_b.setNumber_of_armies(1);
					}
				}
			}
		}

		game_play.setStatus(aggressive_fortify_message);
		return game_play;
	}

}
