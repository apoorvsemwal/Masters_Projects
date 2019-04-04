package com.risk.business.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;

import org.springframework.stereotype.Service;

import com.risk.business.IManagePlayer;
import com.risk.model.Attack;
import com.risk.model.Card;
import com.risk.model.CardTrade;
import com.risk.model.Continent;
import com.risk.model.Domination;
import com.risk.model.GamePlay;
import com.risk.model.GamePlayTerritory;
import com.risk.model.Map;
import com.risk.model.Player;
import com.risk.model.gui.PlayerDetails;
import com.risk.model.gui.SinglePlayer;
import com.risk.model.Territory;
import com.risk.model.Strategy.Aggressive;
import com.risk.model.Strategy.Benevolent;
import com.risk.model.Strategy.Cheater;
import com.risk.model.Strategy.Human;

/**
 * This class is the Concrete Implementation for interface IManagePlayer
 * 
 * @author <a href="mailto:himansipatel1994@gmail.com">Himansi Patel</a>
 * @author <a href="mailto:mayankjariwala1994@gmail.com">Mayank Jariwala</a>
 * @version 0.0.1
 */
@Service
public class ManagePlayer implements IManagePlayer {

	private String roll_dice_message = "";

	/**
	 * @see com.risk.business.IManagePlayer#createPlayer(PlayerDetails)
	 * @author <a href="mailto:himansipatel1994@gmail.com">Himansi Patel</a>
	 * @author <a href="mayankjariwala1994@gmail.com"> Mayank Jariwala </a>
	 * @author <a href="apoorv.semwal20@gmail.com"> Apoorv Semwal </a>
	 */
	@Override
	public GamePlay createPlayer(PlayerDetails single_game_input) {

		int num_of_players = single_game_input.getPlayersNo();
		String file_name = single_game_input.getFileName();
		String army_allocation_type = single_game_input.getAllocationType();

		GamePlay game_play = new GamePlay();

		List<Player> player_info_list = new ArrayList<Player>();

		int army_stock = getArmyStock(num_of_players);

		int i = 1;
		for (SinglePlayer player : single_game_input.getPlayers()) {

			Player p = new Player();
			String player_name = "Player" + i;
			List<GamePlayTerritory> gameplay_territory_list = new ArrayList<>();
			List<Card> card_list = new ArrayList<Card>();
			p.setId(i);
			p.setName(player_name);
			p.setArmy_stock(army_stock);
			p.setTerritory_list(gameplay_territory_list);
			p.setCard_list(card_list);
			p.setTrade_count(0);
			p.setType(player.getType());

			if (player.getType().equalsIgnoreCase("Human")) {
				p.setStrategy(new Human());
				p.setStrategy_name("Human");
			} else if (player.getBehaviour().equalsIgnoreCase("Aggressive")) {
				p.setStrategy(new Aggressive());
				p.setStrategy_name("Aggressive");
			} else if (player.getBehaviour().equalsIgnoreCase("Benevolent")) {
				p.setStrategy(new Benevolent());
				p.setStrategy_name("Benevolent");
			} else if (player.getBehaviour().equalsIgnoreCase("Random")) {
				p.setStrategy(new com.risk.model.Strategy.Random());
				p.setStrategy_name("Random");
			} else if (player.getBehaviour().equalsIgnoreCase("Cheater")) {
				p.setStrategy(new Cheater());
				p.setStrategy_name("Cheater");
			}

			player_info_list.add(p);
			i++;
		}

		ManageMap manage_map_object = new ManageMap();
		Map map = manage_map_object.getFullMap(file_name);

		if (map != null && map.getStatus().equalsIgnoreCase("")) {
			assingTerritoriesToPlayers(map, player_info_list);
			if (army_allocation_type.equalsIgnoreCase("A")) {
				assignArmiesOnTerritories(army_stock, player_info_list);
			}
			ManageGamePlay game_manager = new ManageGamePlay();
			game_play.setGui_map(manage_map_object.fetchMap(file_name));
			game_play.setMap(map);

			String game_phase;
			int current_player = 1;
			game_phase = army_allocation_type.equalsIgnoreCase("A") ? "REINFORCEMENT" : "STARTUP";
			file_name = (file_name.endsWith(".map") ? file_name.split("\\.")[0] : file_name) + "_"
					+ String.valueOf(System.currentTimeMillis());
			game_play.setFile_name(file_name);
			game_play.setCurrent_player(current_player);
			game_play.setGame_phase(game_phase);
			game_play.setGame_state(player_info_list);
			game_play.setCard_trade(new CardTrade());
			List<Card> free_cards = getFreeCards(map);
			game_play.setFree_cards(free_cards);
			if (army_allocation_type.equalsIgnoreCase("A")) {
				game_manager.calculateArmiesReinforce(game_play.getGame_state(), map, game_play.getCurrent_player());
			}
		} else if (map != null && map.getStatus() != "") {
			game_play.setStatus(map.getStatus());
			return game_play;
		} else {
			game_play.setStatus("Invalid Map");
			return game_play;
		}
		Domination domination = new Domination();
		ManageDomination manage_domination = new ManageDomination();
		domination.addObserver(manage_domination);
		domination.updateDomination(game_play);
		for (Player player : game_play.getGame_state()) {
			if (player.getId() == game_play.getCurrent_player()
					&& (player.getType().equalsIgnoreCase("Computer") || player.getType().equalsIgnoreCase("Human"))) {
				if (game_play.getStatus() != null) {
					game_play.setStatus(game_play.getStatus() + "\n" + "REINFORCEMENT WILL START FOR PLAYER: "
							+ game_play.getCurrent_player() + "\n");
				} else {
					game_play.setStatus("REINFORCEMENT WILL START FOR PLAYER: " + game_play.getCurrent_player() + "\n");
				}
				break;
			}
		}
		return game_play;
	}

	/**
	 * This method is used for assigning armies on territories
	 * 
	 * @author <a href="mailto:himansipatel1994@gmail.com">Himansi Patel</a>
	 * @param army_stock       number of armies assigned to territories
	 * @param player_info_list List of Players in the GamePlay
	 */
	public void assignArmiesOnTerritories(int army_stock, List<Player> player_info_list) {
		for (int player_index = 0; player_index < player_info_list.size(); player_index++) {
			player_info_list.get(player_index).setArmy_stock(0);
			int i = 0;
			for (int territory_list_index = 0; territory_list_index < player_info_list.get(player_index)
					.getTerritory_list().size(); territory_list_index++) {
				if (army_stock >= player_info_list.get(player_index).getTerritory_list().size()) {
					if (i < army_stock) {
						int sum_armies = player_info_list.get(player_index).getTerritory_list()
								.get(territory_list_index).getNumber_of_armies() + 1;
						player_info_list.get(player_index).getTerritory_list().get(territory_list_index)
								.setNumber_of_armies(sum_armies);
						if (territory_list_index + 1 == player_info_list.get(player_index).getTerritory_list().size()) {
							territory_list_index = -1;
						}
						i++;
					} else {
						break;
					}
				} else {
					int sum_armies = player_info_list.get(player_index).getTerritory_list().get(territory_list_index)
							.getNumber_of_armies() + 1;
					player_info_list.get(player_index).getTerritory_list().get(territory_list_index)
							.setNumber_of_armies(sum_armies);
				}
			}
		}
	}

	/**
	 * This method set cards according to territory and army name
	 * 
	 * @param map Currently selected Map
	 * @return List of Cards Total No. of Cards in entire game before game starts
	 */
	public List<Card> getFreeCards(Map map) {
		List<Card> card_list = new ArrayList<>();
		List<GamePlayTerritory> territories = getTerritories(map);
		List<List<String>> card_type_list = new ArrayList<>();
		List<String> Infantry = new ArrayList<>();
		List<String> Cavalry = new ArrayList<>();
		List<String> Artillery = new ArrayList<>();
		Infantry.add("Infantry");
		Cavalry.add("Cavalry");
		Artillery.add("Artillery");
		card_type_list.add(Infantry);
		card_type_list.add(Cavalry);
		card_type_list.add(Artillery);
		int total_card_type = card_type_list.size();
		int count = -1;
		for (int i = 0; i < territories.size(); i++) {
			count++;
			if (card_type_list.get(count) != null) {
				try {
					card_type_list.get(count).add(territories.get(i).getTerritory_name());

				} catch (NullPointerException e) {
					e.printStackTrace();
				}
			} else {
				continue;
			}
			if (count == total_card_type - 1) {
				count = -1;
			}
		}

		for (int i = 0; i < card_type_list.size(); i++) {
			String army_type = null;
			for (int j = 0; j < card_type_list.get(i).size(); j++) {

				if (j == 0) {
					army_type = card_type_list.get(i).get(j);
				} else {
					Card card = new Card();
					card.setTerritory_name(card_type_list.get(i).get(j));
					card.setArmy_type(army_type);
					card_list.add(card);
				}
			}
		}
		return card_list;
	}

	/**
	 * This method finds and returns the strongest territory that the current player
	 * has.
	 * 
	 * @author <a href="mailto:apoorv.semwal20@gmail.com">Apoorv Semwal</a>
	 * @param current_player Currently playing player.
	 * @return GamePlayTerritory Strongest Territory.
	 */
	public GamePlayTerritory findStrongestTerritory(Player current_player) {
		GamePlayTerritory strongest_territory = null;
		int max = 0;
		for (GamePlayTerritory territory : current_player.getTerritory_list()) {
			if (territory.getNumber_of_armies() > max) {
				max = territory.getNumber_of_armies();
				strongest_territory = territory;
			}
		}
		return strongest_territory;
	}

	/**
	 * This method is responsible for an initial distribution of armies in Startup
	 * Phase of the game.
	 * 
	 * @author <a href="mailto:himansipatel1994@gmail.com">Himansi Patel</a>
	 * @param num_of_players Number of players playing the game
	 * @return Army count being received by each Player.
	 */
	public int getArmyStock(int num_of_players) {
		int army_stock = 0;
		if (num_of_players == 2) {
			army_stock = 40;
		} else if (num_of_players == 3) {
			army_stock = 35;
		} else if (num_of_players == 4) {
			army_stock = 30;
		} else if (num_of_players == 5) {
			army_stock = 25;
		} else if (num_of_players == 6) {
			army_stock = 20;
		}
		return army_stock;
	}

	/**
	 * This method is an abstraction for the process of initially allocating
	 * territories to each player, in a round robin fashion.
	 * 
	 * @author <a href="mailto:himansipatel1994@gmail.com">Himansi Patel</a>
	 * @param map              Map Object for the active map in Game.
	 * @param player_info_list Player list
	 */
	public void assingTerritoriesToPlayers(Map map, List<Player> player_info_list) {
		List<GamePlayTerritory> territories = getTerritories(map);
		int total_player = player_info_list.size();
		int count = -1;
		for (int i = 0; i < territories.size(); i++) {
			count++;
			if (player_info_list.get(count) != null) {
				try {
					player_info_list.get(count).getTerritory_list().add(territories.get(i));
				} catch (NullPointerException e) {
					e.printStackTrace();
				}
			} else {
				continue;
			}
			if (count == total_player - 1) {
				count = -1;
			}
		}
	}

	/**
	 * This method is randomly linking territories to game play territory object
	 * 
	 * @see com.risk.business.IManagePlayer#getTerritories(Map)
	 * @author <a href="mailto:himansipatel1994@gmail.com">Himansi Patel</a>
	 * @param map Map Object for retrieving Territories.
	 * @return List of territories
	 */
	@Override
	public List<GamePlayTerritory> getTerritories(Map map) {
		Continent map_continent;
		Territory map_territory;
		HashMap<String, Continent> continents = new HashMap<String, Continent>();
		continents = map.getContinents();
		List<Territory> temp_territory_list;
		String continent_name;
		ArrayList<GamePlayTerritory> total_territory_list = new ArrayList<GamePlayTerritory>();
		for (Entry<String, Continent> m : continents.entrySet()) {
			continent_name = m.getKey();
			map_continent = m.getValue();
			temp_territory_list = map_continent.getTerritories();
			for (int i = 0; i < temp_territory_list.size(); i++) {
				map_territory = temp_territory_list.get(i);
				GamePlayTerritory game_play_territory = new GamePlayTerritory();
				game_play_territory.setTerritory_name(map_territory.getName());
				game_play_territory.setContinent_name(continent_name);
				game_play_territory.setNumber_of_armies(0);
				total_territory_list.add(game_play_territory);
			}
		}
		return total_territory_list;
	}

	/**
	 * This method handles the trading of cards during reinforcement phase of the
	 * game-play.
	 * 
	 * @author <a href="mailto:a_semwal@encs.concordia.ca">ApoorvSemwal</a>
	 * @param game_state State of the game at point of time holding the entire info
	 *                   about game-play. Like the current phase and player.
	 * @return game_state after updating info on trading of cards.
	 */
	public GamePlay tradeCards(GamePlay game_state) {

		CardTrade trade_card = game_state.getCard_trade();
		if (trade_card != null) {
			if (trade_card.getCard1() == null || trade_card.getCard2() == null || trade_card.getCard3() == null) {
				game_state.setStatus("Trading requires a minimum of three cards to be selected.\n");
			} else {

				/**
				 * First part of condition (before OR) checks if all images on the three cards
				 * are same and the second part (after OR) checks if all the three are
				 * different.
				 */
				if ((trade_card.getCard1().getArmy_type().equalsIgnoreCase(trade_card.getCard2().getArmy_type())
						&& trade_card.getCard1().getArmy_type().equalsIgnoreCase(trade_card.getCard3().getArmy_type()))
						|| (!trade_card.getCard1().getArmy_type().equalsIgnoreCase(trade_card.getCard2().getArmy_type())
								&& !trade_card.getCard2().getArmy_type()
										.equalsIgnoreCase(trade_card.getCard3().getArmy_type())
								&& !trade_card.getCard3().getArmy_type()
										.equalsIgnoreCase(trade_card.getCard1().getArmy_type()))) {

					int current_player = game_state.getCurrent_player();

					for (Player player : game_state.getGame_state()) {

						if (player.getId() == current_player) {

							updateTradedArmies(player);
							updateCardLists(player, game_state.getFree_cards(), trade_card);

							/**
							 * Check if the Player controls any territory which is present in one of the
							 * cards being traded.
							 */
							List<GamePlayTerritory> player_territory_list = player.getTerritory_list();

							if (player_territory_list != null) {

								for (GamePlayTerritory gamePlayTerritory : player_territory_list) {

									if (gamePlayTerritory.getTerritory_name()
											.equalsIgnoreCase(trade_card.getCard1().getTerritory_name())
											|| gamePlayTerritory.getTerritory_name()
													.equalsIgnoreCase(trade_card.getCard2().getTerritory_name())
											|| gamePlayTerritory.getTerritory_name()
													.equalsIgnoreCase(trade_card.getCard3().getTerritory_name())) {

										/**
										 * An additional two armies given if the Player controls any territory which is
										 * present in one of the cards being traded.
										 */
										gamePlayTerritory
												.setNumber_of_armies(gamePlayTerritory.getNumber_of_armies() + 2);
										break;
									}
								}
							}
							break;
						}
					}
				} else {
					game_state.setStatus("Either all three cards should have same image or all three different.\n");
				}
			}
		} else {
			game_state.setStatus("Inavlid Trade State during Gameplay.\n");
		}
		return game_state;
	}

	/**
	 * This method prepares a CardTrade Object for any Computer Player
	 * 
	 * @author <a href="mailto:a_semwal@encs.concordia.ca">ApoorvSemwal</a>
	 * @param current_player Current player who is about to trade cards.
	 * @return CardTrade has the set of three cards to be traded.
	 */
	public CardTrade prepareCardTrade(Player current_player) {

		Boolean trade_prepared = false;

		CardTrade card_trade = null;
		Card card1 = null;
		Card card2 = null;
		Card card3 = null;

		for (int i = 0; i < current_player.getCard_list().size() - 2; i++) {
			card1 = current_player.getCard_list().get(i);
			for (int j = i + 1; j < current_player.getCard_list().size() - 1; j++) {
				card2 = current_player.getCard_list().get(j);
				for (int k = j + 1; k < current_player.getCard_list().size(); k++) {
					card3 = current_player.getCard_list().get(k);
					if ((card1.getArmy_type().equalsIgnoreCase(card2.getArmy_type())
							&& card1.getArmy_type().equalsIgnoreCase(card3.getArmy_type()))
							|| (!card1.getArmy_type().equalsIgnoreCase(card2.getArmy_type())
									&& !card2.getArmy_type().equalsIgnoreCase(card3.getArmy_type())
									&& !card3.getArmy_type().equalsIgnoreCase(card1.getArmy_type()))) {
						trade_prepared = true;
						break;
					}
					if (trade_prepared) {
						break;
					}

				}
				if (trade_prepared) {
					break;
				}
			}
		}
		if (trade_prepared && card1 != null && card2 != null && card3 != null) {
			card_trade = new CardTrade();
			card_trade.setCard1(card1);
			card_trade.setCard2(card2);
			card_trade.setCard3(card3);
		}
		return card_trade;
	}

	/**
	 * This method updates the player's army count during the trade of cards.
	 * 
	 * @author <a href="mailto:a_semwal@encs.concordia.ca">ApoorvSemwal</a>
	 * @param player State of the current Player.
	 */
	public void updateTradedArmies(Player player) {
		if (player.getTrade_count() == 0) {
			player.setArmy_stock(player.getArmy_stock() + 4);
			player.setTrade_count(1);
		} else if (player.getTrade_count() == 1) {
			player.setArmy_stock(player.getArmy_stock() + 6);
			player.setTrade_count(2);
		} else if (player.getTrade_count() == 2) {
			player.setArmy_stock(player.getArmy_stock() + 8);
			player.setTrade_count(3);
		} else if (player.getTrade_count() == 3) {
			player.setArmy_stock(player.getArmy_stock() + 10);
			player.setTrade_count(4);
		} else if (player.getTrade_count() == 4) {
			player.setArmy_stock(player.getArmy_stock() + 12);
			player.setTrade_count(5);
		} else if (player.getTrade_count() == 5) {
			player.setArmy_stock(player.getArmy_stock() + 15);
			player.setTrade_count(6);
		} else if (player.getTrade_count() > 5) {
			player.setArmy_stock(player.getArmy_stock() + 15 + ((player.getTrade_count() - 5) * 5));
			player.setTrade_count(player.getTrade_count() + 1);
		}
	}

	/**
	 * This method updates the player's card list and well as the free card list
	 * after the trading is over.
	 * 
	 * @author <a href="mailto:a_semwal@encs.concordia.ca">ApoorvSemwal</a>
	 * @param player       State of the current Player.
	 * @param free_cards   List of cards which are free for allocation
	 * @param traded_cards The set of three cards being traded.
	 */
	public void updateCardLists(Player player, List<Card> free_cards, CardTrade traded_cards) {
		free_cards.add(traded_cards.getCard1());
		free_cards.add(traded_cards.getCard2());
		free_cards.add(traded_cards.getCard3());
		Iterator<Card> i = player.getCard_list().iterator();
		while (i.hasNext()) {
			Card card = (Card) i.next();
			if ((card.getArmy_type().equalsIgnoreCase(traded_cards.getCard1().getArmy_type())
					&& card.getTerritory_name().equalsIgnoreCase(traded_cards.getCard1().getTerritory_name()))
					|| (card.getArmy_type().equalsIgnoreCase(traded_cards.getCard2().getArmy_type())
							&& card.getTerritory_name().equalsIgnoreCase(traded_cards.getCard2().getTerritory_name()))
					|| (card.getArmy_type().equalsIgnoreCase(traded_cards.getCard3().getArmy_type()) && card
							.getTerritory_name().equalsIgnoreCase(traded_cards.getCard3().getTerritory_name()))) {
				i.remove();
			}
		}
	}

	/**
	 * This method performs army move when attacker occupies defender territory.
	 * 
	 * @author <a href="himansipatel1994@gmail.com"> Himansi Patel </a>
	 * @param game_play : GamePlay Object
	 * @return GamePlay updated state of the game after army move during attack
	 *         phase.
	 */
	public GamePlay attackArmyMove(GamePlay game_play) {
		String source_territory = game_play.getArmy_move().getAttacker_territory();
		String destination_territory = game_play.getArmy_move().getDefender_territory();
		if (source_territory.equalsIgnoreCase(destination_territory)) {
			game_play.setStatus(" cannot move armies because same territory is selected in destination.");
			game_play.setGame_phase("ATTACK_ARMY_MOVE");
			return game_play;
		}

		int army_count = game_play.getArmy_move().getAmry_count();

		if (army_count == 0) {
			game_play.setStatus("Atleast 1 army should be moved.");
			game_play.setGame_phase("ATTACK_ARMY_MOVE");
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
						game_play.setGame_phase("ATTACK_ARMY_MOVE");
						return game_play;
					} else {
						source_territory_instance
								.setNumber_of_armies(source_territory_instance.getNumber_of_armies() - army_count);
						dest_territory_instance
								.setNumber_of_armies(dest_territory_instance.getNumber_of_armies() + army_count);
						game_play.setStatus(
								army_count + " army moved from " + source_territory_instance.getTerritory_name()
										+ " to " + dest_territory_instance.getTerritory_name());
						game_play.setGame_phase("ATTACK_ARMY_ON");
					}
					break;
				}
			}
			if (source_territory_instance == null || dest_territory_instance == null) {
				game_play.setStatus("Invalid Move (Not Neighboring Territory)");
				game_play.setGame_phase("ATTACK_ARMY_MOVE");
				return game_play;
			}
		}
		return game_play;

	}

	/**
	 * This function is call at the end of each player attack phase , which performs
	 * checks that whether player has occupied any territory during each turn and if
	 * any territory occupied then as per risk rules player should get one card from
	 * card deck.
	 * 
	 * @author <a href="mailto:himansipatel1994@gmail.com">Himansi Patel</a>
	 *         Description added by Mayank Jariwala
	 * @param game_play : GamePlay Object
	 */
	public void giveCardAtAttackEnd(GamePlay game_play) {
		int current_player_id = game_play.getCurrent_player();
		boolean is_territory_occupied = false;
		for (int player_list_index = 0; player_list_index < game_play.getGame_state().size(); player_list_index++) {
			if (current_player_id == game_play.getGame_state().get(player_list_index).getId()) {
				is_territory_occupied = game_play.getGame_state().get(player_list_index).isAny_territory_occupied();
			}
		}
		if (is_territory_occupied) {
			if (game_play.getFree_cards().size() > 0) {

				Random rand = new Random();
				int idx = rand.nextInt(game_play.getFree_cards().size());

				Card card = game_play.getFree_cards().get(idx);
				game_play.getFree_cards().remove(idx);
				for (int player_list_index = 0; player_list_index < game_play.getGame_state()
						.size(); player_list_index++) {
					if (current_player_id == game_play.getGame_state().get(player_list_index).getId()) {
						game_play.getGame_state().get(player_list_index).getCard_list().add(card);
						String message = "Card - ".concat(card.getTerritory_name()).concat(" Army - ")
								.concat(card.getArmy_type()).concat(" has been assigned to Attacker.");
						game_play.setStatus(message);
						game_play.getGame_state().get(player_list_index).setAny_territory_occupied(false);
					}
				}
			}
		} else {
			game_play.setStatus("");
		}
	}

	/**
	 * This function decide how many maximum dice attacker and defender can roll max
	 * 
	 * @author <a href="mailto:mayankjariwala1994@gmail.com">Mayank Jariwala</a>
	 * @author <a href="mailto:himansipatel1994@gmail.com">Himansi Patel</a>
	 * @param attacker_territory_list List of Territories Attacker owes
	 * @param defender_territory_list List of Territories Defender owes
	 * @param attack                  Attack object to set attacker and defender
	 *                                dice number
	 */
	public void setAttackerDefenderDiceNo(List<GamePlayTerritory> attacker_territory_list,
			List<GamePlayTerritory> defender_territory_list, Attack attack) {
		int attacker_dice = attacker_territory_list.get(0).getNumber_of_armies() - 1;
		int defender_dice = defender_territory_list.get(0).getNumber_of_armies();
		if (attacker_dice >= 3) {
			attack.setAttacker_dice_no(3);
		} else {
			attack.setAttacker_dice_no(attacker_dice);
		}
		if (defender_dice >= 2) {
			attack.setDefender_dice_no(2);
		} else {
			attack.setDefender_dice_no(defender_dice);
		}
	}

	/**
	 * This function performs check for valid attack on defender territory (If
	 * Territory is occupy by attacker then player cannot attack on his own
	 * territory)
	 * 
	 * @author <a href="mailto:mayankjariwala1994@gmail.com">Mayank Jariwala</a>
	 * @author <a href="mailto:himansipatel1994@gmail.com">Himansi Patel</a>
	 * @param attacker_id Attacker Id
	 * @param defender_id Defender Id
	 * @return Valid Attack Message
	 */
	public String checkForValidAttackTerritory(int attacker_id, int defender_id) {
		String message = "";
		if (attacker_id == defender_id) {
			message = "This territory is already with the attacker";
		}
		return message;
	}

	/**
	 * This function performs valid attack check.
	 * 
	 * @author <a href="mailto:mayankjariwala1994@gmail.com">Mayank Jariwala</a>
	 * @author <a href="mailto:himansipatel1994@gmail.com">Himansi Patel</a>
	 * @param attacker_territory_armies The number of armies of Attacker Territory
	 * @param defender_territory_armies The number of armies of Defender Territory
	 * @param attacker_dice_no          The number of dice to be rolled by attacker
	 * @param defender_dice_no          The number of dice to be rolled by defender
	 * @return Valid Attack Message
	 */
	public String checkForValidAttack(int attacker_territory_armies, int defender_territory_armies,
			int attacker_dice_no, int defender_dice_no) {

		String message = "";
		if (attacker_territory_armies <= attacker_dice_no || attacker_territory_armies == 1) {
			if (attacker_territory_armies - 1 == 0) {
				message = "Invalid Attack By Attacker (You can't attack with this territory)";
			} else {
				if (attacker_territory_armies - 1 <= 3) {
					message = "Invalid Attack By Attacker (You can roll max " + (attacker_territory_armies - 1)
							+ " dice)";
				} else {
					message = "Invalid Attack By Attacker (You can roll max 3 dice)";
				}
			}
		}

		if (defender_territory_armies < defender_dice_no) {
			if (defender_territory_armies <= 2) {
				message = "Invalid Defend(You can roll max " + defender_territory_armies + " dice )";
			} else {
				message = "Invalid defend By defender (You can roll max 2 dice)";
			}
		}

		return message;
	}

	/**
	 * This function is use to detect if current player(i.e attacker) is winner once
	 * attacker occupy defender territory
	 * 
	 * @author <a href="mayankjariwala1994@gmail.com">Mayank Jariwala</a>
	 * @author <a href="himansipatel1994@gmail.com">Himansi Patel</a>
	 * @param game_play Cuurent GameState
	 */
	public void checkForWinner(GamePlay game_play) {
		List<GamePlayTerritory> player_occupied_territory = game_play.getGame_state()
				.get(game_play.getCurrent_player() - 1).getTerritory_list();
		int total_territory_in_game = getTerritories(game_play.getMap()).size();
		if (total_territory_in_game > 0) {
			// Current Player(Attacker) Territory Size
			int player_territory_count = player_occupied_territory.size();
			if (player_territory_count == total_territory_in_game) {
				game_play.setGame_phase("GAME_FINISH");
				game_play.setStatus("\nCongrats Player" + game_play.getCurrent_player() + "[ "
						+ game_play.getGame_state().get(game_play.getCurrent_player() - 1).getStrategy_name()
						+ " ] You are the Winner for this Game!!!!\n");
				game_play.setWinner("Player" + game_play.getCurrent_player() + " Behaviour : "
						+ game_play.getGame_state().get(game_play.getCurrent_player() - 1).getStrategy_name());
			}
		}
	}

	/**
	 * This method rolls dice for both player and return dice result for respective
	 * round as list.
	 *
	 * @author <a href="mailto:mayankjariwala1994@gmail.com">Mayank Jariwala</a>
	 * @author <a href="mailto:himansipatel1994@gmail.com">Himansi Patel</a>
	 * @param attacker_dice_no : No. of dice attacker decided to roll
	 * @param defender_dice_no : No. of dice defender decided to roll
	 * @return List of Attack Result
	 */
	public List<Integer> rollDiceDecision(int attacker_dice_no, int defender_dice_no) {
		List<Integer> dice_roll_result = new ArrayList<>();
		String dice_case = attacker_dice_no + "_" + defender_dice_no;
		switch (dice_case) {
		case "1_1":
			dice_roll_result = rollDiceOneOnOne();
			break;
		case "1_2":
			dice_roll_result = rollDiceOneOnTwo();
			break;
		case "2_1":
			dice_roll_result = rollDiceTwoOnOne();
			break;
		case "2_2":
			dice_roll_result = rollDiceTwoOnTwo();
			break;
		case "3_1":
			dice_roll_result = rollDiceThreeOnOne();
			break;
		case "3_2":
			dice_roll_result = rollDiceThreeOnTwo();
			break;
		default:
			break;
		}
		return dice_roll_result;
	}

	/**
	 * This method roll dice for the case of one attacker army v/s one defender
	 * army.
	 * 
	 * @author <a href="mailto:mayankjariwala1994@gmail.com">Mayank Jariwala</a>
	 * @author <a href="mailto:himansipatel1994@gmail.com">Himansi Patel</a>
	 * @return Attack Result List
	 */
	private List<Integer> rollDiceOneOnOne() {
		roll_dice_message = "";
		Random random = new Random();
		int dice_result_flag = 0;
		int attacker_dice_result = random.nextInt(6) + 1;
		int defender_dice_result = random.nextInt(6) + 1;
		roll_dice_message += "Attacker Rolls 1 dice on which max showed number " + attacker_dice_result + " and ";
		roll_dice_message += "Defender Rolls 1 dice on which max showed number " + defender_dice_result + "\n";
		if (attacker_dice_result > defender_dice_result) {
			dice_result_flag = 1;
			roll_dice_message = "Winner : Attacker\n" + roll_dice_message;
		} else if (attacker_dice_result <= defender_dice_result) {
			dice_result_flag = 0;
			roll_dice_message = "Winner : Defender\n" + roll_dice_message;
		}
		return Arrays.asList(dice_result_flag);
	}

	/**
	 * This method roll dice for the case of one attacker army v/s two defender
	 * army.
	 * 
	 * @author <a href="mailto:mayankjariwala1994@gmail.com">Mayank Jariwala</a>
	 * @author <a href="mailto:himansipatel1994@gmail.com">Himansi Patel</a>
	 * @return Attack Result List
	 */
	private List<Integer> rollDiceOneOnTwo() {
		roll_dice_message = "";
		Random random = new Random();
		int dice_result_flag = 0;
		int attacker_dice_result = random.nextInt(6) + 1;
		int defender_dice_result = Math.max(random.nextInt(6) + 1, random.nextInt(6) + 1);
		roll_dice_message += "Attacker Rolls 1 dice on which max showed number " + attacker_dice_result + " and ";
		roll_dice_message += "Defender Rolls 2 dice on which max showed number " + defender_dice_result + "\n";
		if (attacker_dice_result > defender_dice_result) {
			dice_result_flag = 1;
			roll_dice_message = "Winner : Attacker\n" + roll_dice_message;
		} else if (attacker_dice_result <= defender_dice_result) {
			dice_result_flag = 0;
			roll_dice_message = "Winner : Defender\n" + roll_dice_message;
		}
		return Arrays.asList(dice_result_flag);
	}

	/**
	 * This method roll dice for the case of two attacker army v/s one defender
	 * army.
	 * 
	 * @author <a href="mailto:mayankjariwala1994@gmail.com">Mayank Jariwala</a>
	 * @author <a href="mailto:himansipatel1994@gmail.com">Himansi Patel</a>
	 * @return Attack Result List
	 */
	private List<Integer> rollDiceTwoOnOne() {
		roll_dice_message = "";
		Random random = new Random();
		int dice_result_flag = 0;
		int attacker_dice_result = Math.max(random.nextInt(6) + 1, random.nextInt(6) + 1);
		int defender_dice_result = random.nextInt(6) + 1;
		roll_dice_message += "Attacker Rolls 2 dice on which max showed number " + attacker_dice_result + " and ";
		roll_dice_message += "Defender Rolls 1 dice on which max showed number " + defender_dice_result + "\n";
		if (attacker_dice_result > defender_dice_result) {
			dice_result_flag = 1;
			roll_dice_message = "Winner : Attacker\n" + roll_dice_message;
		} else if (attacker_dice_result <= defender_dice_result) {
			dice_result_flag = 0;
			roll_dice_message = "Winner : Defender\n" + roll_dice_message;
		}

		return Arrays.asList(dice_result_flag);
	}

	/**
	 * This method roll dice for the case of two attacker army v/s two defender
	 * army.
	 * 
	 * @author <a href="mailto:mayankjariwala1994@gmail.com">Mayank Jariwala</a>
	 * @author <a href="mailto:himansipatel1994@gmail.com">Himansi Patel</a>
	 * @return Attack Result List
	 */
	private List<Integer> rollDiceTwoOnTwo() {
		String old_message = "";
		roll_dice_message = "";
		Random random = new Random();
		HashMap<String, List<Integer>> dice_result_list = new HashMap<>();
		int attacker_roll_one = random.nextInt(6) + 1;
		int attacker_roll_two = random.nextInt(6) + 1;
		dice_result_list.put("attacker", Arrays.asList(attacker_roll_one, attacker_roll_two));
		int defender_roll_one = random.nextInt(6) + 1;
		int defender_roll_two = random.nextInt(6) + 1;
		dice_result_list.put("defender", Arrays.asList(defender_roll_one, defender_roll_two));
		int attacker_max = Collections.max(dice_result_list.get("attacker"));
		int defender_max = Collections.max(dice_result_list.get("defender"));
		int max_result = attacker_max > defender_max ? 1 : 0;
		roll_dice_message += "Attacker Rolls 2 dice on which max showed number " + attacker_max + " and ";
		roll_dice_message += "Defender Rolls 2 dice on which max showed number " + defender_max + "\n";
		old_message = roll_dice_message;
		roll_dice_message = "";
		roll_dice_message = max_result == 1 ? "Winner : Attacker\n" : "Winner : Defender\n";
		roll_dice_message = roll_dice_message + old_message;
		int attacker_min = Collections.min(dice_result_list.get("attacker"));
		int defender_min = Collections.min(dice_result_list.get("defender"));
		int min_result = attacker_min > defender_min ? 1 : 0;
		old_message = roll_dice_message;
		roll_dice_message = "";
		roll_dice_message += "Attacker Rolls 2 dice on which min showed number " + attacker_min + " and ";
		roll_dice_message += "Defender Rolls 2 dice on which min showed number " + defender_min + "\n";
		roll_dice_message = roll_dice_message + old_message;
		old_message = roll_dice_message;
		roll_dice_message = "";
		roll_dice_message = min_result == 1 ? "Winner : Attacker\n" : "Winner : Defender\n";
		roll_dice_message = roll_dice_message + old_message;
		return Arrays.asList(max_result, min_result);
	}

	/**
	 * This method roll dice for the case of three attacker army v/s two defender
	 * army.
	 * 
	 * @author <a href="mailto:mayankjariwala1994@gmail.com">Mayank Jariwala</a>
	 * @author <a href="mailto:himansipatel1994@gmail.com">Himansi Patel</a>
	 * @return Attack Result List
	 */
	private List<Integer> rollDiceThreeOnTwo() {
		String old_message = "";
		roll_dice_message = "";
		Random random = new Random();
		List<Integer> attacker_list = new ArrayList<>();
		List<Integer> defender_list = new ArrayList<>();

		attacker_list.add(random.nextInt(6) + 1);
		attacker_list.add(random.nextInt(6) + 1);
		attacker_list.add(random.nextInt(6) + 1);

		defender_list.add(random.nextInt(6) + 1);
		defender_list.add(random.nextInt(6) + 1);

		int attacker_max = Collections.max(attacker_list);
		attacker_list.remove((attacker_list.indexOf(attacker_max)));
		int defender_max = Collections.max(defender_list);
		defender_list.remove((defender_list.indexOf(defender_max)));
		int first_result = attacker_max > defender_max ? 1 : 0;
		roll_dice_message += "Attacker Rolls 3 dice on which first max showed number " + attacker_max + " and ";
		roll_dice_message += "Defender Rolls 2 dice on which first max showed number " + defender_max + "\n";
		old_message = roll_dice_message;
		roll_dice_message = "";
		roll_dice_message = first_result == 1 ? "Winner : Attacker\n" : "Winner : Defender\n";
		roll_dice_message = roll_dice_message + old_message;
		int attacker_second_max = Collections.max(attacker_list);
		attacker_list.remove((attacker_list.indexOf(attacker_second_max)));
		int defender_second_max = Collections.max(defender_list);
		defender_list.remove((defender_list.indexOf(defender_second_max)));
		int second_result = attacker_second_max > defender_second_max ? 1 : 0;
		old_message = roll_dice_message;
		roll_dice_message = "";
		roll_dice_message += "Attacker Rolls 3 dice on which second max showed number " + attacker_second_max + " and ";
		roll_dice_message += "Defender Rolls 2 dice on which second max showed number " + defender_second_max + "\n";
		roll_dice_message = roll_dice_message + old_message;

		old_message = roll_dice_message;
		roll_dice_message = "";
		roll_dice_message = second_result == 1 ? "Winner : Attacker\n" : "Winner : Defender\n";
		roll_dice_message = roll_dice_message + old_message;

		return Arrays.asList(first_result, second_result);
	}

	/**
	 * This method roll dice for the case of three attacker army v/s one defender
	 * army.
	 * 
	 * @author <a href="mailto:mayankjariwala1994@gmail.com">Mayank Jariwala</a>
	 * @author <a href="mailto:himansipatel1994@gmail.com">Himansi Patel</a>
	 * @return Attack Result List
	 */
	private List<Integer> rollDiceThreeOnOne() {
		roll_dice_message = "";
		Random random = new Random();
		int dice_result_flag = 0;
		int attacker_dice_result = Math.max(Math.max(random.nextInt(6) + 1, random.nextInt(6) + 1),
				random.nextInt(6) + 1);
		int defender_dice_result = random.nextInt(6) + 1;
		roll_dice_message += "Attacker Rolls 3 dice on which max showed number " + attacker_dice_result + " and ";
		roll_dice_message += "Defender Rolls 1 dice on which max showed number " + defender_dice_result + "\n";

		if (attacker_dice_result > defender_dice_result) {
			dice_result_flag = 1;
			roll_dice_message = "Winner : Attacker\n" + roll_dice_message;
		} else if (attacker_dice_result <= defender_dice_result) {
			dice_result_flag = 0;
			roll_dice_message = "Winner : Defender\n" + roll_dice_message;
		}
		return Arrays.asList(dice_result_flag);
	}

	/**
	 * This method checks if two given territories are neighbors in a Map
	 * 
	 * @author <a href="mailto:mayankjariwala1994@gmail.com">Mayank Jariwala</a>
	 * @author <a href="mailto:himansipatel1994@gmail.com">Himansi Patel</a>
	 * @param territory_a First Territory
	 * @param territory_b Second Territory
	 * @param map         Current Map being played
	 * @return True-If territories are neighbors False-Otherwise
	 */
	public boolean checkIfNeighbours(String territory_a, String territory_b, com.risk.model.gui.Map map) {
		Boolean neighbour_flag = false;
		for (com.risk.model.gui.Territory territory : map.getTerritories()) {
			if (!territory.getName().equalsIgnoreCase(territory_a)) {
				continue;
			} else {
				if (Arrays.asList(territory.getNeighbours().split(";")).contains(territory_b)) {
					neighbour_flag = true;
					break;
				} else {
					neighbour_flag = false;
					break;
				}
			}
		}
		return neighbour_flag;
	}

	/**
	 * This function checks attack is possible or not and return updated game play
	 * object
	 * 
	 * @author <a href="mailto:mayankjariwala1994@gmail.com">Mayank Jariwala</a>
	 * @author <a href="mailto:himansipatel1994@gmail.com">Himansi Patel</a>
	 * @param game_play The Current GameState
	 * @return GamePlay object
	 */
	public GamePlay checkAttackIsPossible(GamePlay game_play) {
		boolean is_attack_possible = false;
		for (Player player : game_play.getGame_state()) {
			if (player.getId() == game_play.getCurrent_player()) {
				for (GamePlayTerritory player_territory_list : player.getTerritory_list()) {
					if (player_territory_list.getNumber_of_armies() != 1) {
						is_attack_possible = getPlayerTerritoryNeighbours(player_territory_list.getTerritory_name(),
								game_play, player.getTerritory_list());
						if (is_attack_possible)
							break;
					}
				}
			}
		}
		if (!is_attack_possible) {
			game_play.setStatus("No more attack");
		}
		return game_play;
	}

	/**
	 * This function is use to get Neigbouring Territories of current Attacking
	 * Territory
	 * 
	 * @author <a href="mailto:mayankjariwala1994@gmail.com">Mayank Jariwala</a>
	 * @author <a href="mailto:himansipatel1994@gmail.com">Himansi Patel</a>
	 * @param territory_name        The Attacker Territory Name
	 * @param game_play             The Current GameState Object of Game
	 * @param player_territory_list Territory list of player
	 * @return result
	 */
	private boolean getPlayerTerritoryNeighbours(String territory_name, GamePlay game_play,
			List<GamePlayTerritory> player_territory_list) {
		List<String> neighbours = new ArrayList<>();
		List<String> player_territory = new ArrayList<>();
		for (Continent entry : game_play.getMap().getContinents().values()) {
			for (Territory territory : entry.getTerritories()) {
				if (territory.getName().equalsIgnoreCase(territory_name)) {
					for (String neighbour_name : territory.getNeighbours()) {
						neighbours.add(neighbour_name);
					}
					neighbours.add(territory_name);
					break;
				}
			}
		}
		for (GamePlayTerritory territory : player_territory_list) {
			player_territory.add(territory.getTerritory_name());
		}
		if (player_territory.equals(neighbours)) {
			// Attack Not Possible but List Equals
			return false;
		} else {
			// Attack Possible but List Not Equals
			return true;
		}
	}

	/**
	 * This function checks fortification is possible or not and return updated game
	 * play object
	 * 
	 * @author <a href="mailto:mayankjariwala1994@gmail.com">Mayank Jariwala</a>
	 * @author <a href="mailto:himansipatel1994@gmail.com">Himansi Patel</a>
	 * @param game_play The Current GameState
	 * @return GamePlay object
	 */
	public GamePlay checkFortificationIsPossible(GamePlay game_play) {
		boolean is_fortification_possible = false;
		for (Player player : game_play.getGame_state()) {
			if (player.getId() == game_play.getCurrent_player()) {
				for (GamePlayTerritory territory : player.getTerritory_list()) {

					if (territory.getNumber_of_armies() != 1) {
						is_fortification_possible = checkValidNeighbourFortification(territory.getTerritory_name(),
								game_play, player.getTerritory_list());
						if (is_fortification_possible)
							break;
					}
				}
			}
		}

		if (!is_fortification_possible) {
			game_play.setStatus("Fortification is not possible");
		}
		return game_play;

	}

	/**
	 * This function is use to get Neighbour Territories of current fortifying
	 * Territory
	 * 
	 * @author <a href="mailto:mayankjariwala1994@gmail.com">Mayank Jariwala</a>
	 * @author <a href="mailto:himansipatel1994@gmail.com">Himansi Patel</a>
	 * @param territory_name        The Attacker Territory Name
	 * @param game_play             The Current GameState Object of Game
	 * @param player_territory_list Territory list of player
	 * @return result
	 */
	private boolean checkValidNeighbourFortification(String territory_name, GamePlay game_play,
			List<GamePlayTerritory> player_territory_list) {
		List<String> neighbours = new ArrayList<>();
		for (Continent entry : game_play.getMap().getContinents().values()) {
			for (Territory territory : entry.getTerritories()) {
				if (territory.getName().equalsIgnoreCase(territory_name)) {
					for (String neighbour_name : territory.getNeighbours()) {
						neighbours.add(neighbour_name);
					}
					break;
				}
			}
		}
		for (String neighbour_territory : neighbours) {
			for (GamePlayTerritory player_territory : player_territory_list) {
				if (neighbour_territory.equalsIgnoreCase(player_territory.getTerritory_name())) {
					return true;
				}
			}
		}
		return false;
	}

	public String getRollDiceMessage() {
		return roll_dice_message;
	}
}