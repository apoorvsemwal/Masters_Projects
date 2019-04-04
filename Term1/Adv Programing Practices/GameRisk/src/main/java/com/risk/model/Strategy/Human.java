package com.risk.model.Strategy;

import java.util.ArrayList;
import java.util.List;
import com.risk.business.IStrategy;
import com.risk.business.impl.ManagePlayer;
import com.risk.model.Attack;
import com.risk.model.AttackArmyMove;
import com.risk.model.GamePlay;
import com.risk.model.GamePlayTerritory;
import com.risk.model.Player;

/**
 * Concrete implementation of Human Strategy in terms of Strategy design
 * Pattern, during our GamePlay.
 * 
 * @author <a href="mailto:apoorv.semwal20@gmail.com">Apoorv Semwal</a>
 * @version 0.0.1
 */
public class Human implements IStrategy {

	/**
	 * Reinforcement for a Human Player as per Strategy Design Pattern.
	 * 
	 * @see com.risk.business.IStrategy#reinforce(com.risk.model.GamePlay)
	 * @author <a href="mailto:a_semwal@encs.concordia.ca">ApoorvSemwal</a>
	 * @param game_play state of the game i.e. entire game related info when
	 *                  reinforcement starts for a player.
	 * @return GamePlay updated state of the game after reinforcement phase ends.
	 */
	public GamePlay reinforce(GamePlay game_play) {
		ManagePlayer manage_player = new ManagePlayer();
		if (game_play.getGame_phase().equalsIgnoreCase("TRADE_CARDS")) {
			manage_player.tradeCards(game_play);
		}
		return game_play;
	}

	/**
	 * Attack for a Human Player as per Strategy Design Pattern.
	 * 
	 * @see com.risk.business.IStrategy#attack(com.risk.model.GamePlay)
	 * @author <a href="mailto:mayankjariwala1994@gmail.com">MayankJariwala</a>
	 * @param game_play state of the game i.e. entire game related info when attack
	 *                  starts for a player.
	 * @return GamePlay updated state of the game after attack phase ends.
	 */
	public GamePlay attack(GamePlay game_play) {

		boolean is_territory_occupied = false;

		ManagePlayer player_manager = new ManagePlayer();

		if (game_play.getGame_phase().equalsIgnoreCase("ATTACK_ARMY_MOVE")) {
			player_manager.attackArmyMove(game_play);
			player_manager.checkForWinner(game_play);
			return game_play;
		}

		if (game_play.getGame_phase().equalsIgnoreCase("ATTACK_END")) {
			player_manager.giveCardAtAttackEnd(game_play);
			return game_play;
		}

		int attacker_id = 0;
		int defender_id = 0;
		String attack_message = "";
		String old_message = "";
		// Counter Variable : Keep track of territory found for attacker and defender
		int found_territory = 0;
		List<GamePlayTerritory> attacker_territory_list = new ArrayList<>();
		List<GamePlayTerritory> defender_territory_list = new ArrayList<>();
		List<String> attack_message_list = new ArrayList<>();
		Attack attack = game_play.getAttack();

		// Local Variable : To get actual army values from getter
		int attacker_terrtiory_armies = 0;
		int defender_territory_armies = 0;
		String attacker_territory_name = game_play.getAttack().getAttacker_territory();
		String defender_territory_name = game_play.getAttack().getDefender_territory();
		attack_message += "\nAttacker territory: " + attacker_territory_name + " Defender Territory: "
				+ defender_territory_name + "\n";
		List<Player> players_list = game_play.getGame_state();

		for (Player player : players_list) {
			List<GamePlayTerritory> territory_list = player.getTerritory_list();
			for (GamePlayTerritory territory : territory_list) {
				// Attacker Territory Object
				if (territory.getTerritory_name().equalsIgnoreCase(attacker_territory_name)) {
					attacker_id = player.getId();
					attacker_terrtiory_armies = territory.getNumber_of_armies();
					attacker_territory_list.add(territory);
					found_territory++;
				}
				// Defender Territory Object
				if (territory.getTerritory_name().equalsIgnoreCase(defender_territory_name)) {
					defender_id = player.getId();
					defender_territory_armies = territory.getNumber_of_armies();
					defender_territory_list.add(territory);
					found_territory++;
				}
				if (found_territory == 2) {
					break;
				}
			}
		}
		int attacker_dice_no = 0;
		int defender_dice_no = 0;
		String valid_territory_message = player_manager.checkForValidAttackTerritory(attacker_id, defender_id);
		if (valid_territory_message.trim().length() == 0) {
			if (game_play.getGame_phase().equalsIgnoreCase("ATTACK_ALL_OUT")) {
				player_manager.setAttackerDefenderDiceNo(attacker_territory_list, defender_territory_list, attack);
			}
			attacker_dice_no = game_play.getAttack().getAttacker_dice_no();
			defender_dice_no = game_play.getAttack().getDefender_dice_no();

			String valid_attack_message = player_manager.checkForValidAttack(attacker_terrtiory_armies,
					defender_territory_armies, attacker_dice_no, defender_dice_no);
			if (valid_attack_message.trim().length() == 0) {
				// Roll Dice
				List<Integer> attack_result = player_manager.rollDiceDecision(attacker_dice_no, defender_dice_no);
				attack_message_list.add(player_manager.getRollDiceMessage());
				for (int i = 0; i < attack_result.size(); i++) {
					int result = attack_result.get(i);
					if (result == 1) {
						// Attacker Won
						GamePlayTerritory def_obj = defender_territory_list.get(0);
						def_obj.setNumber_of_armies(def_obj.getNumber_of_armies() - 1);
						if (def_obj.getNumber_of_armies() == 0) {
							attacker_territory_list.add(def_obj);
							old_message = "\nAttacker Occupies Defender Territory\n";
							attack_message = old_message + attack_message;
						}
					} else {
						// Defender Won
						GamePlayTerritory att_obj = attacker_territory_list.get(0);
						att_obj.setNumber_of_armies(att_obj.getNumber_of_armies() - 1);
					}
				}

				// Iterating Attacker Territory List for performing actions regarding dice
				// result
				for (GamePlayTerritory att_territory : attacker_territory_list) {
					for (Player player : players_list) {
						if (player.getId() == attacker_id) {
							List<GamePlayTerritory> territory_list = player.getTerritory_list();
							for (int j = 0; j < territory_list.size(); j++) {
								if (att_territory.getTerritory_name()
										.equalsIgnoreCase(territory_list.get(j).getTerritory_name())) {
									if (attacker_territory_list.size() > 1) {
										if (att_territory.getNumber_of_armies() == 1) {
											territory_list.get(j)
													.setNumber_of_armies(att_territory.getNumber_of_armies() + 1);
										} else {
											territory_list.get(j)
													.setNumber_of_armies(att_territory.getNumber_of_armies());
										}
									} else {
										territory_list.get(j).setNumber_of_armies(att_territory.getNumber_of_armies());
									}
								} else if (!territory_list.contains(att_territory)) {
									territory_list.add(att_territory);
									is_territory_occupied = true;
									break;
								}
							}
							player.setAny_territory_occupied(is_territory_occupied);
						}
					}
				}

				// Iterating Defender Territory List for performing actions regarding dice
				// result
				for (GamePlayTerritory deff_territory : defender_territory_list) {
					for (Player player : players_list) {
						if (player.getId() == defender_id) {
							List<GamePlayTerritory> territory_list = player.getTerritory_list();
							for (GamePlayTerritory territory : territory_list) {
								if (deff_territory.getTerritory_name()
										.equalsIgnoreCase(territory.getTerritory_name())) {
									if (deff_territory.getNumber_of_armies() == 0) {
										territory_list.remove(territory);
										game_play.setGame_phase("ATTACK_ARMY_MOVE");
										AttackArmyMove attack_army_move = game_play.getArmy_move();
										if (attack_army_move == null) {
											attack_army_move = new AttackArmyMove();
										}
										attack_army_move
												.setAttacker_territory(game_play.getAttack().getAttacker_territory());
										attack_army_move
												.setDefender_territory(game_play.getAttack().getDefender_territory());
										attack_army_move.setAmry_count(0);
										game_play.setArmy_move(attack_army_move);
										break;
									} else {
										territory.setNumber_of_armies(deff_territory.getNumber_of_armies());
									}
								}
							}
						}
					}
				}
				attack_message = attack_message + String.join("\n", attack_message_list);
				game_play.setStatus(attack_message);
			} else {
				game_play.setStatus(valid_attack_message);
			}
			if (game_play.getGame_phase().equalsIgnoreCase("ATTACK_ALL_OUT")
					&& attacker_territory_list.get(0).getNumber_of_armies() > 1) {
				attack(game_play);
			}
		} else {
			game_play.setStatus(valid_territory_message);
		}
		return game_play;
	}

	/**
	 * Fortify for a Human Player as per Strategy Design Pattern.
	 * 
	 * @see com.risk.business.IStrategy#fortify(com.risk.model.GamePlay)
	 * @author <a href="mailto:mayankjariwala1994@gmail.com">Mayank Jariwala</a>
	 *         Comments and Function added by Mayank Jariwala
	 * @param game_play state of the game i.e. entire game related info when
	 *                  fortification starts for a player.
	 * @return GamePlay Object
	 */
	public GamePlay fortify(GamePlay game_play) {

		String source_territory = game_play.getFortification().getSource_territory();
		String destination_territory = game_play.getFortification().getDestination_territory();

//		checks source territory and destination territory is same or not
		if (source_territory.equalsIgnoreCase(destination_territory)) {
			game_play.setStatus("Fortification cannot be performed because same territory is selected in destination.");
			return game_play;
		}

		int army_count = game_play.getFortification().getArmy_count();

		if (army_count == 0) {
			game_play.setStatus("Atleast 1 army should be moved.");
			return game_play;
		}

		GamePlayTerritory source_territory_instance = null, dest_territory_instance = null;

		for (Player player : game_play.getGame_state()) {

			if (player.getId() != game_play.getCurrent_player()) {
				continue;
			}

			for (GamePlayTerritory each_territory : player.getTerritory_list()) {

				if (each_territory.getTerritory_name().equalsIgnoreCase(source_territory)) {
					source_territory_instance = each_territory;
				} else if (each_territory.getTerritory_name().equalsIgnoreCase(destination_territory)) {
					dest_territory_instance = each_territory;
				}
				if (source_territory_instance != null && dest_territory_instance != null) {
					if (source_territory_instance.getNumber_of_armies() <= army_count) {
						game_play.setStatus(source_territory + " is not having minimum armies to transfer.");
						return game_play;
					} else {
						source_territory_instance
								.setNumber_of_armies(source_territory_instance.getNumber_of_armies() - army_count);
						dest_territory_instance
								.setNumber_of_armies(dest_territory_instance.getNumber_of_armies() + army_count);
						game_play.setStatus(
								army_count + " army moved from " + source_territory_instance.getTerritory_name()
										+ " to " + dest_territory_instance.getTerritory_name());
					}
					break;
				}
			}
			if (source_territory_instance == null || dest_territory_instance == null) {
				game_play.setStatus("Invalid Move (Not Neighboring Territory)");
				return game_play;
			}
		}
		return game_play;
	}

}
