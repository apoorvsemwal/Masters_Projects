package com.risk.business.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Observable;
import java.util.Observer;
import java.util.SortedSet;
import java.util.TreeSet;

import org.springframework.stereotype.Service;

import com.risk.business.IManageGamePlay;
import com.risk.file.IManageGamePlayFile;
import com.risk.file.impl.ManageGamePlayFile;
import com.risk.model.Card;
import com.risk.model.Continent;
import com.risk.model.Domination;
import com.risk.model.GamePlay;
import com.risk.model.GamePlayTerritory;
import com.risk.model.Map;
import com.risk.model.Player;
import com.risk.model.Territory;
import com.risk.model.Tournament;
import com.risk.model.TournamentResults;
import com.risk.model.Strategy.Aggressive;
import com.risk.model.Strategy.Benevolent;
import com.risk.model.Strategy.Cheater;
import com.risk.model.Strategy.Random;
import com.risk.model.gui.TournamentChoices;

/**
 * This class is the Concrete Implementation for interface IManageGamePlay.
 * 
 * @author <a href="mailto:a_semwal@encs.concordia.ca">ApoorvSemwal</a>
 * @version 0.0.1
 */
@Service
public class ManageGamePlay implements IManageGamePlay, Observer {

	/**
	 * @see com.risk.business.IManageGamePlay#managePhase(GamePlay)
	 * @author <a href="mailto:a_semwal@encs.concordia.ca">ApoorvSemwal</a>
	 */
	@Override
	public GamePlay managePhase(GamePlay game_play) {

		Player current_player = null;

		if (game_play != null) {

			for (Player player : game_play.getGame_state()) {
				if (player.getId() == game_play.getCurrent_player()) {
					current_player = player;
					break;
				}
			}

			if (current_player != null) {
				Map map = game_play.getMap();

				if (map == null) {
					game_play.setStatus("Inavlid Map.");
					return game_play;
				} else if (!map.getStatus().equalsIgnoreCase("")) {
					game_play.setStatus(map.getStatus());
					return game_play;
				}

				if (current_player.getType().equalsIgnoreCase("Human")) {
					switch (game_play.getGame_phase()) {

					case "STARTUP":
						game_play.setStatus("");
						calculateArmiesReinforce(game_play.getGame_state(), map, 1);
						setCurrentPlayerAndPhase(game_play, game_play.getGame_phase());
						break;

					case "REINFORCEMENT":
						game_play.setStatus("");
						game_play = current_player.executeStrategy("REINFORCE", game_play);
						setCurrentPlayerAndPhase(game_play, game_play.getGame_phase());
						if (game_play.getGame_play_turns() == 100) {
							game_play.setGame_phase("GAME_FINISH");
							game_play.setWinner("Draw");
							game_play.setStatus("This game was declared a Draw after 100 turns.\n");
							break;
						}
						break;

					case "TRADE_CARDS":
						game_play.setStatus("");
						game_play = current_player.executeStrategy("REINFORCE", game_play);
						setCurrentPlayerAndPhase(game_play, game_play.getGame_phase());
						break;

					case "ATTACK_ON":
						game_play.setStatus("");
						current_player.executeStrategy("ATTACK", game_play);
						break;

					case "ATTACK_ALL_OUT":
						game_play.setStatus("");
						current_player.executeStrategy("ATTACK", game_play);
						break;

					case "ATTACK_ARMY_MOVE":
						current_player.executeStrategy("ATTACK", game_play);
						break;

					case "ATTACK_END":
						game_play.setStatus("");
						current_player.executeStrategy("ATTACK", game_play);
						setCurrentPlayerAndPhase(game_play, game_play.getGame_phase());
						if (game_play.getGame_play_turns() == 100) {
							game_play.setGame_phase("GAME_FINISH");
							game_play.setWinner("Draw");
							game_play.setStatus("This game was declared a Draw after 100 turns.\n");
							break;
						}
						break;

					case "FORTIFICATION":
						game_play.setStatus("");
						current_player.executeStrategy("FORTIFY", game_play);
						break;

					case "FORTIFICATION_END":
						game_play.setStatus("");
						setCurrentPlayerAndPhase(game_play, game_play.getGame_phase());
						if (game_play.getGame_play_turns() == 100) {
							game_play.setGame_phase("GAME_FINISH");
							game_play.setWinner("Draw");
							game_play.setStatus("This game was declared a Draw after 100 turns.\n");
							break;
						}
						break;

					default:
						break;
					}

				} else if (current_player.getType().equalsIgnoreCase("Computer")) {

					switch (game_play.getGame_phase()) {

					case "STARTUP":
						game_play.setStatus("");
						calculateArmiesReinforce(game_play.getGame_state(), map, 1);
						setCurrentPlayerAndPhase(game_play, game_play.getGame_phase());
						break;

					case "REINFORCEMENT":
						game_play.setStatus("");
						current_player.executeStrategy("REINFORCE", game_play);
						setCurrentPlayerAndPhase(game_play, game_play.getGame_phase());
						if (game_play.getGame_play_turns() == 100) {
							game_play.setGame_phase("GAME_FINISH");
							game_play.setWinner("Draw");
							game_play.setStatus("This game was declared a Draw after 100 turns.\n");
							break;
						}
						break;

					case "ATTACK":
						game_play.setStatus("");
						current_player.executeStrategy("ATTACK", game_play);
						setCurrentPlayerAndPhase(game_play, game_play.getGame_phase());
						if (game_play.getGame_play_turns() == 100) {
							game_play.setGame_phase("GAME_FINISH");
							game_play.setWinner("Draw");
							game_play.setStatus("This game was declared a Draw after 100 turns.\n");
							break;
						}
						break;

					case "FORTIFICATION":
						game_play.setStatus("");
						current_player.executeStrategy("FORTIFY", game_play);
						if (game_play.getCurrent_player() == game_play.getGame_state().size()) {
							game_play.setGame_play_turns(game_play.getGame_play_turns() + 1);
						}
						if (game_play.getGame_play_turns() == 100) {
							game_play.setGame_phase("GAME_FINISH");
							game_play.setWinner("Draw");
							game_play.setStatus("This game was declared a Draw after 100 turns.\n");
							break;
						}
						game_play.setGame_phase("REINFORCEMENT");
						if (game_play.getCurrent_player() + 1 > game_play.getGame_state().size()) {
							game_play.setCurrent_player(1);
						} else {
							game_play.setCurrent_player(game_play.getCurrent_player() + 1);
						}
						while (checkIfCurrentPlayerOut(game_play)) {
							if (game_play.getCurrent_player() + 1 > game_play.getGame_state().size()) {
								game_play.setCurrent_player(1);
							} else {
								game_play.setCurrent_player(game_play.getCurrent_player() + 1);
							}
						}
						for (Player player : game_play.getGame_state()) {
							if (player.getId() == game_play.getCurrent_player()
									&& (player.getType().equalsIgnoreCase("Computer")
											|| player.getType().equalsIgnoreCase("Human"))) {
								game_play.setStatus("REINFORCEMENT WILL START FOR PLAYER: "
										+ game_play.getCurrent_player() + game_play.getGame_state()
												.get(game_play.getCurrent_player() - 1).getStrategy_name()
										+ "\n" + game_play.getStatus() + "\n");
								break;
							}
						}
						calculateArmiesReinforce(game_play.getGame_state(), map, game_play.getCurrent_player());
						break;

					default:
						break;
					}
				}
				Domination domination = new Domination();
				ManageDomination manage_domination = new ManageDomination();
				domination.addObserver(manage_domination);
				domination.updateDomination(game_play);
				return game_play;
			} else {
				game_play.setStatus("Current Player is not Valid!");
				return game_play;
			}
		} else {
			game_play = new GamePlay();
			game_play.setStatus("Invalid Game State!");
			return game_play;
		}
	}

	/**
	 * This method decides the next player and the phase during game-play.
	 * 
	 * @author <a href="mailto:a_semwal@encs.concordia.ca">ApoorvSemwal</a>
	 * @param game_play  State of the game at point of time holding the entire info
	 *                   about game-play. Like the current phase and player.
	 * @param game_phase Name of the phase which is ending.
	 */
	private void setCurrentPlayerAndPhase(GamePlay game_play, String game_phase) {

		ManagePlayer player_manager = new ManagePlayer();
		String old_message = "";

		Boolean attack_ok = false;
		Boolean fortify_ok = false;

		switch (game_phase) {

		case "STARTUP":
			game_play.setCurrent_player(1);
			game_play.setGame_phase("REINFORCEMENT");
			break;

		case "REINFORCEMENT":
			game_play.setGame_phase("ATTACK");
			old_message = game_play.getStatus();
			game_play.setStatus("");
			player_manager.checkAttackIsPossible(game_play);
			if (game_play.getStatus().length() == 0) {
				game_play.setStatus(old_message);
				attack_ok = true;
			} else {
				game_play.setGame_phase("FORTIFICATION");
				game_play.setStatus(game_play.getStatus() + "\n" + old_message + "\n");
				old_message = game_play.getStatus();
				game_play.setStatus("");
				player_manager.checkFortificationIsPossible(game_play);
				if (game_play.getStatus().length() == 0) {
					game_play.setStatus(old_message);
					fortify_ok = true;
				} else {
					if (game_play.getCurrent_player() == game_play.getGame_state().size()) {
						game_play.setGame_play_turns(game_play.getGame_play_turns() + 1);
					}
					old_message = game_play.getStatus() + "\n" + old_message + "\n";
					if (game_play.getCurrent_player() + 1 > game_play.getGame_state().size()) {
						game_play.setCurrent_player(1);
					} else {
						game_play.setCurrent_player(game_play.getCurrent_player() + 1);
					}
					while (checkIfCurrentPlayerOut(game_play)) {
						if (game_play.getCurrent_player() + 1 > game_play.getGame_state().size()) {
							game_play.setCurrent_player(1);
						} else {
							game_play.setCurrent_player(game_play.getCurrent_player() + 1);
						}
					}
					game_play.setGame_phase("REINFORCEMENT");
				}
			}
			for (Player player : game_play.getGame_state()) {
				if (player.getId() == game_play.getCurrent_player() && (player.getType().equalsIgnoreCase("Computer")
						|| player.getType().equalsIgnoreCase("Human"))) {
					if (attack_ok) {
						game_play.setStatus("ATTACK WILL START FOR PLAYER: " + game_play.getCurrent_player() + "-"
								+ game_play.getGame_state().get(game_play.getCurrent_player() - 1).getStrategy_name()
								+ "\n" + old_message + "\n");
					} else if (!attack_ok && fortify_ok) {
						game_play
								.setStatus("FORTIFICATION WILL START FOR PLAYER: "
										+ game_play.getCurrent_player() + "-" + game_play.getGame_state()
												.get(game_play.getCurrent_player() - 1).getStrategy_name()
										+ "\n" + old_message + "\n");
					} else if (!attack_ok && !fortify_ok) {
						game_play
								.setStatus("REINFORCEMENT WILL START FOR PLAYER: "
										+ game_play.getCurrent_player() + "-" + game_play.getGame_state()
												.get(game_play.getCurrent_player() - 1).getStrategy_name()
										+ "\n" + old_message + "\n");
						calculateArmiesReinforce(game_play.getGame_state(), game_play.getMap(),
								game_play.getCurrent_player());
					}
					break;
				}
			}
			break;

		case "TRADE_CARDS":
			game_play.setGame_phase("REINFORCEMENT");
			break;

		case "ATTACK":
			game_play.setGame_phase("FORTIFICATION");

			old_message = game_play.getStatus();
			game_play.setStatus("");
			player_manager.checkFortificationIsPossible(game_play);
			if (game_play.getStatus().length() == 0) {
				game_play.setStatus(old_message);
				fortify_ok = true;
			} else {
				if (game_play.getCurrent_player() == game_play.getGame_state().size()) {
					game_play.setGame_play_turns(game_play.getGame_play_turns() + 1);
				}
				game_play.setGame_phase("REINFORCEMENT");
				old_message = game_play.getStatus() + "\n" + old_message + "\n";
				if (game_play.getCurrent_player() + 1 > game_play.getGame_state().size()) {
					game_play.setCurrent_player(1);
				} else {
					game_play.setCurrent_player(game_play.getCurrent_player() + 1);
				}
				while (checkIfCurrentPlayerOut(game_play)) {
					if (game_play.getCurrent_player() + 1 > game_play.getGame_state().size()) {
						game_play.setCurrent_player(1);
					} else {
						game_play.setCurrent_player(game_play.getCurrent_player() + 1);
					}
				}
			}

			for (Player player : game_play.getGame_state()) {
				if (player.getId() == game_play.getCurrent_player() && (player.getType().equalsIgnoreCase("Computer")
						|| player.getType().equalsIgnoreCase("Human"))) {
					if (fortify_ok) {
						game_play
								.setStatus("FORTIFICATION WILL START FOR PLAYER: "
										+ game_play.getCurrent_player() + "-" + game_play.getGame_state()
												.get(game_play.getCurrent_player() - 1).getStrategy_name()
										+ "\n" + old_message + "\n");
					} else {
						game_play
								.setStatus("REINFORCEMENT WILL START FOR PLAYER: "
										+ game_play.getCurrent_player() + "-" + game_play.getGame_state()
												.get(game_play.getCurrent_player() - 1).getStrategy_name()
										+ "\n" + old_message + "\n");
						calculateArmiesReinforce(game_play.getGame_state(), game_play.getMap(),
								game_play.getCurrent_player());
					}
					break;
				}
			}
			break;

		case "ATTACK_END":
			game_play.setGame_phase("FORTIFICATION");
			old_message = game_play.getStatus();
			game_play.setStatus("");
			fortify_ok = false;
			player_manager.checkFortificationIsPossible(game_play);
			if (game_play.getStatus().length() == 0) {
				game_play.setStatus(old_message);
				fortify_ok = true;
			} else {
				if (game_play.getCurrent_player() == game_play.getGame_state().size()) {
					game_play.setGame_play_turns(game_play.getGame_play_turns() + 1);
				}
				old_message = game_play.getStatus() + "\n" + old_message + "\n";
				if (game_play.getCurrent_player() + 1 > game_play.getGame_state().size()) {
					game_play.setCurrent_player(1);
				} else {
					game_play.setCurrent_player(game_play.getCurrent_player() + 1);
				}
				while (checkIfCurrentPlayerOut(game_play)) {
					if (game_play.getCurrent_player() + 1 > game_play.getGame_state().size()) {
						game_play.setCurrent_player(1);
					} else {
						game_play.setCurrent_player(game_play.getCurrent_player() + 1);
					}
				}
				game_play.setGame_phase("REINFORCEMENT");
			}
			for (Player player : game_play.getGame_state()) {
				if (player.getId() == game_play.getCurrent_player() && (player.getType().equalsIgnoreCase("Computer")
						|| player.getType().equalsIgnoreCase("Human"))) {
					if (fortify_ok) {
						game_play
								.setStatus("FORTIFICATION WILL START FOR PLAYER: "
										+ game_play.getCurrent_player() + "-" + game_play.getGame_state()
												.get(game_play.getCurrent_player() - 1).getStrategy_name()
										+ "\n" + old_message + "\n");
					} else {
						game_play
								.setStatus("REINFORCEMENT WILL START FOR PLAYER: "
										+ game_play.getCurrent_player() + "-" + game_play.getGame_state()
												.get(game_play.getCurrent_player() - 1).getStrategy_name()
										+ "\n" + old_message + "\n");
						calculateArmiesReinforce(game_play.getGame_state(), game_play.getMap(),
								game_play.getCurrent_player());
					}
					break;
				}
			}
			break;

		case "FORTIFICATION_END":
			if (game_play.getCurrent_player() == game_play.getGame_state().size()) {
				game_play.setGame_play_turns(game_play.getGame_play_turns() + 1);
			}
			if (game_play.getCurrent_player() + 1 > game_play.getGame_state().size()) {
				game_play.setCurrent_player(1);
			} else {
				game_play.setCurrent_player(game_play.getCurrent_player() + 1);
			}
			while (checkIfCurrentPlayerOut(game_play)) {
				if (game_play.getCurrent_player() + 1 > game_play.getGame_state().size()) {
					game_play.setCurrent_player(1);
				} else {
					game_play.setCurrent_player(game_play.getCurrent_player() + 1);
				}
			}
			game_play.setGame_phase("REINFORCEMENT");
			for (Player player : game_play.getGame_state()) {
				if (player.getId() == game_play.getCurrent_player() && (player.getType().equalsIgnoreCase("Computer")
						|| player.getType().equalsIgnoreCase("Human"))) {
					game_play.setStatus("REINFORCEMENT WILL START FOR PLAYER: " + game_play.getCurrent_player() + "-"
							+ game_play.getGame_state().get(game_play.getCurrent_player() - 1).getStrategy_name() + "\n"
							+ old_message + "\n");
					calculateArmiesReinforce(game_play.getGame_state(), game_play.getMap(),
							game_play.getCurrent_player());
					break;
				}
			}
			break;

		default:
			break;
		}
	}

	/**
	 * @see com.risk.business.IManageGamePlay#calculateArmiesReinforce(java.util.List,
	 *      com.risk.model.Map, int)
	 * 
	 * @author <a href="mailto:a_semwal@encs.concordia.ca">ApoorvSemwal</a>
	 */
	@Override
	public List<Player> calculateArmiesReinforce(List<Player> gameplay, Map map, int current_player) {

		List<Continent> continents = new ArrayList<>();
		List<Territory> territories;

		java.util.Map<String, SortedSet<String>> continents_territories = new HashMap<>();
		java.util.Map<Integer, SortedSet<String>> player_territories = new HashMap<>();

		SortedSet<String> territories_game;
		SortedSet<String> territories_player;

		java.util.Map<String, Integer> continents_score = new HashMap<>();
		java.util.Map<Integer, Integer> players_army = new HashMap<>();

		for (java.util.Map.Entry<String, Continent> continent : map.getContinents().entrySet()) {
			continents.add(continent.getValue());
		}

		// Preparing a list of all players along with the continents they hold.
		for (Player player : gameplay) {
			if (player.getId() == current_player) {
				List<GamePlayTerritory> player_territories_game = player.getTerritory_list();
				territories_player = new TreeSet<>();
				for (GamePlayTerritory territority : player_territories_game) {
					territories_player.add(territority.getTerritory_name());
				}
				player_territories.put(player.getId(), territories_player);
			}
		}

		// Preparing List of all players along with their current army stock.
		for (Player player : gameplay) {
			if (player.getId() == current_player) {
				players_army.put(player.getId(), player.getArmy_stock());
			}
		}

		// Preparing a list of all continents along with their territories.
		for (Continent continent : continents) {
			territories = new ArrayList<>();
			territories_game = new TreeSet<>();
			territories = continent.getTerritories();
			for (Territory territory : territories) {
				territories_game.add(territory.getName());
			}
			continents_score.put(continent.getName(), continent.getScore());
			continents_territories.put(continent.getName(), territories_game);
		}

		// Updating Player's army stock on the basis of territories it hold.
		// If the player holds less than 9 territories then allocate 3 army elements as
		// per Risk Rules
		for (Player player : gameplay) {
			if (player.getId() == current_player) {
				int army_count = player_territories.get(player.getId()).size() / 3;
				if (army_count < 3) {
					army_count = 3;
				}
				army_count = army_count + players_army.get(player.getId());
				players_army.replace(player.getId(), army_count);
			}
		}

		// Verifying if a player holds the entire continent and updating its army stock.
		for (Iterator<Entry<Integer, SortedSet<String>>> iterator_player = player_territories.entrySet()
				.iterator(); iterator_player.hasNext();) {
			java.util.Map.Entry<Integer, SortedSet<String>> player = iterator_player.next();
			territories_player = player.getValue();
			for (Iterator<Entry<String, SortedSet<String>>> iterator_continent = continents_territories.entrySet()
					.iterator(); iterator_continent.hasNext();) {
				java.util.Map.Entry<String, SortedSet<String>> continent = iterator_continent.next();
				territories_game = continent.getValue();
				if (territories_player.containsAll(territories_game)) {
					int continent_score_val = continents_score.get(continent.getKey());
					int player_army_count = players_army.get(player.getKey()) + continent_score_val;
					players_army.replace(player.getKey(), player_army_count);
				}
			}
		}

		// Preparing List of all players along with their updated army stock.
		for (Player player : gameplay) {
			if (player.getId() == current_player) {
				player.setArmy_stock(players_army.get(player.getId()));
			}
		}

		return gameplay;
	}

	/**
	 * @see com.risk.business.IManageGamePlay#prepareTournamentGamePlay(com.risk.model.gui.TournamentChoices)
	 * @author <a href="mailto:a_semwal@encs.concordia.ca">ApoorvSemwal</a>
	 */
	@Override
	public Tournament prepareTournamentGamePlay(TournamentChoices tournament_inp) {

		Tournament tournament = new Tournament();
		List<GamePlay> game_play_set = new ArrayList<>();
		ManageMap map_manager = new ManageMap();
		ManagePlayer player_manager = new ManagePlayer();
		int game_play_count = 0;
		for (int i = 1; i <= tournament_inp.getNoOfGamesToPlay(); i++) {

			for (String map : tournament_inp.getMapNames()) {
				game_play_count++;
				com.risk.model.Map map_model = map_manager.getFullMap(map);

				if (map_model == null) {
					tournament.setStatus(map + " : Unable to load Map.\n");
					return tournament;
				} else if (!map_model.getStatus().equals("")) {
					tournament.setStatus(map + " : " + map_model.getStatus() + "\n");
					return tournament;
				} else {

					com.risk.model.gui.Map gui_map = map_manager.fetchMap(map);
					List<Card> free_cards = player_manager.getFreeCards(map_model);
					GamePlay game_play = new GamePlay();
					game_play.setCurrent_player(1);
					game_play.setFile_name(map);
					game_play.setMap(map_model);
					game_play.setGui_map(gui_map);
					game_play.setGame_play_turns(0);
					game_play.setGame_phase("REINFORCEMENT");
					game_play.setFree_cards(free_cards);
					game_play.setGame_play_id(game_play_count);

					String status = "REINFORCEMENT WILL START FOR PLAYER: 1-"
							+ tournament_inp.getMultipleStrategies().get(0) + "\n" + "Game ID: " + i
							+ " being played on Map: " + map + "\n";
					game_play.setStatus(status);

					update_tournament_gameplay(game_play, tournament_inp.getMultipleStrategies());
					game_play_set.add(game_play);
				}
			}
		}

		tournament.setTournament(game_play_set);
		tournament.setMax_turns(tournament_inp.getMaxTurns());
		tournament.setCurrent_game_play_id(1);
		tournament.setStatus("Tournament Ready. Now starting various Games within it....\n");
		return tournament;
	}

	/**
	 * This method creates Computer Players, each with some strategies, for the
	 * tournament.
	 * 
	 * @param game_play  GamePlay being created for the tournament.
	 * @param strategies Input from UI where the user selects Players with some
	 *                   strategy.
	 */
	private void update_tournament_gameplay(GamePlay game_play, List<String> strategies) {

		ManagePlayer player_manager = new ManagePlayer();
		List<Player> players = new ArrayList<>();

		int i = 1;

		int army_stock = player_manager.getArmyStock(strategies.size());

		for (String string : strategies) {

			Player p = new Player();

			String player_name = "Player" + 1;

			List<GamePlayTerritory> gameplay_territory_list = new ArrayList<>();

			List<Card> card_list = new ArrayList<Card>();

			if (string.equalsIgnoreCase("Aggressive")) {
				p.setStrategy(new Aggressive());
				p.setType("Computer");
				p.setStrategy_name("Aggressive");
			} else if (string.equalsIgnoreCase("Benevolent")) {
				p.setStrategy(new Benevolent());
				p.setType("Computer");
				p.setStrategy_name("Benevolent");
			} else if (string.equalsIgnoreCase("Cheater")) {
				p.setStrategy(new Cheater());
				p.setType("Computer");
				p.setStrategy_name("Cheater");
			} else if (string.equalsIgnoreCase("Random")) {
				p.setStrategy(new Random());
				p.setType("Computer");
				p.setStrategy_name("Random");
			}

			p.setId(i);
			p.setName(player_name);
			i++;

			p.setArmy_stock(army_stock);
			p.setTerritory_list(gameplay_territory_list);
			p.setCard_list(card_list);
			p.setTrade_count(0);
			players.add(p);
		}
		player_manager.assingTerritoriesToPlayers(game_play.getMap(), players);
		player_manager.assignArmiesOnTerritories(army_stock, players);
		game_play.setGame_state(players);
	}

	/**
	 * @see com.risk.business.IManageGamePlay#playTournamentMode(Tournament)
	 * @author <a href="mailto:a_semwal@encs.concordia.ca">ApoorvSemwal</a>
	 */
	@Override
	public Tournament playTournamentMode(Tournament tournament) {

		GamePlay current_game_play = null;
		Player current_player = null;

		if (tournament != null) {

			for (GamePlay game_play : tournament.getTournament()) {
				if (tournament.getCurrent_game_play_id() == game_play.getGame_play_id()) {
					current_game_play = game_play;
					break;
				}
			}

			if (current_game_play != null) {

				for (Player player : current_game_play.getGame_state()) {
					if (player.getId() == current_game_play.getCurrent_player()) {
						current_player = player;
						break;
					}
				}

				if (current_player != null) {

					switch (current_game_play.getGame_phase()) {

					case "REINFORCEMENT":
						current_game_play.setStatus("");
						tournament.setStatus("");
						current_player.executeStrategy("REINFORCE", current_game_play);
						setCurrentPlayerAndPhase(current_game_play, current_game_play.getGame_phase());
						if (current_game_play.getGame_play_turns() == tournament.getMax_turns()) {
							current_game_play.setGame_phase("GAME_FINISH");
							current_game_play.setStatus("Game was a Draw...\n");
							tournament.setCurrent_game_play_id(tournament.getCurrent_game_play_id() + 1);
							if (tournament.getCurrent_game_play_id() > tournament.getTournament().size()) {
								tournament.setStatus("TOURNAMENT_OVER\n");
								fillTournamentResult(tournament);
								break;
							}
							for (GamePlay game_play : tournament.getTournament()) {
								if (game_play.getGame_play_id() == tournament.getCurrent_game_play_id()) {
									game_play.setStatus(game_play.getStatus() + "\n" + "Game was a Draw..." + "\n");
									break;
								}
							}
						}
						break;

					case "ATTACK":
						current_game_play.setStatus("");
						tournament.setStatus("");
						current_player.executeStrategy("ATTACK", current_game_play);
						if (current_game_play.getGame_phase().equalsIgnoreCase("GAME_FINISH")) {
							updateNewGamePlay(tournament, current_game_play.getStatus());
							// checkIfTournamentOver(tournament);
						}
						if (tournament.getStatus().equalsIgnoreCase("TOURNAMENT_OVER\n")) {
							break;
						}
						setCurrentPlayerAndPhase(current_game_play, current_game_play.getGame_phase());
						if (current_game_play.getGame_play_turns() == tournament.getMax_turns()) {
							current_game_play.setGame_phase("GAME_FINISH");
							current_game_play.setStatus("Game was a Draw...\n");
							tournament.setCurrent_game_play_id(tournament.getCurrent_game_play_id() + 1);
							if (tournament.getCurrent_game_play_id() > tournament.getTournament().size()) {
								tournament.setStatus("TOURNAMENT_OVER\n");
								fillTournamentResult(tournament);
								break;
							}
							for (GamePlay game_play : tournament.getTournament()) {
								if (game_play.getGame_play_id() == tournament.getCurrent_game_play_id()) {
									game_play.setStatus(game_play.getStatus() + "\n" + "Game was a Draw..." + "\n");
									break;
								}
							}
						}
						break;

					case "FORTIFICATION":
						current_game_play.setStatus("");
						tournament.setStatus("");
						current_player.executeStrategy("FORTIFY", current_game_play);
						if (current_game_play.getCurrent_player() == current_game_play.getGame_state().size()) {
							current_game_play.setGame_play_turns(current_game_play.getGame_play_turns() + 1);
						}
						if (current_game_play.getGame_play_turns() == tournament.getMax_turns()) {
							current_game_play.setGame_phase("GAME_FINISH");
							current_game_play.setStatus("Game was a Draw...\n");
							tournament.setCurrent_game_play_id(tournament.getCurrent_game_play_id() + 1);
							if (tournament.getCurrent_game_play_id() > tournament.getTournament().size()) {
								tournament.setStatus("TOURNAMENT_OVER\n");
								fillTournamentResult(tournament);
								break;
							}
							for (GamePlay game_play : tournament.getTournament()) {
								if (game_play.getGame_play_id() == tournament.getCurrent_game_play_id()) {
									game_play.setStatus(game_play.getStatus() + "\n" + "Game was a Draw..." + "\n");
									break;
								}
							}
						}

						current_game_play.setGame_phase("REINFORCEMENT");
						if (current_game_play.getCurrent_player() + 1 > current_game_play.getGame_state().size()) {
							current_game_play.setCurrent_player(1);
						} else {
							current_game_play.setCurrent_player(current_game_play.getCurrent_player() + 1);
						}
						while (checkIfCurrentPlayerOut(current_game_play)) {
							if (current_game_play.getCurrent_player() + 1 > current_game_play.getGame_state().size()) {
								current_game_play.setCurrent_player(1);
							} else {
								current_game_play.setCurrent_player(current_game_play.getCurrent_player() + 1);
							}
						}
						current_game_play.setStatus("REINFORCEMENT WILL START FOR PLAYER: "
								+ current_game_play.getCurrent_player() + "-" + current_game_play.getGame_state()
										.get(current_game_play.getCurrent_player() - 1).getStrategy_name()
								+ "\n" + current_game_play.getStatus() + "\n");
						calculateArmiesReinforce(current_game_play.getGame_state(), current_game_play.getMap(),
								current_game_play.getCurrent_player());
						break;

					default:
						break;
					}

					return tournament;
				} else {
					current_game_play.setStatus("Current Player is not Valid!");
					return tournament;
				}
			}
		}
		return tournament;
	}

	/**
	 * This method shifts the tournament to a new GamePlay as the previous one is
	 * over.
	 * 
	 * @param tournament Tournament object
	 * @Status Status of the current GamePlay within tournament
	 */
	private void updateNewGamePlay(Tournament tournament, String status) {
		tournament.setCurrent_game_play_id(tournament.getCurrent_game_play_id() + 1);
		if (tournament.getCurrent_game_play_id() > tournament.getTournament().size()) {
			tournament.setStatus("TOURNAMENT_OVER\n");
			fillTournamentResult(tournament);
		} else {
			for (GamePlay game_play : tournament.getTournament()) {
				if (game_play.getGame_play_id() == tournament.getCurrent_game_play_id()) {
					game_play.setStatus(game_play.getStatus() + "\n" + status + "\n");
					break;
				}
			}
		}
	}

	/**
	 * This method prepares and populates the final result of the tournament
	 * 
	 * @param tournament Tournament object
	 */
	private void fillTournamentResult(Tournament tournament) {

		TournamentResults tournament_results = new TournamentResults();

		java.util.Map<String, List<GamePlay>> each_map_results = new HashMap<>();
		List<GamePlay> game_plays = null;
		List<String> unique_maps = new ArrayList<>();

		for (GamePlay game_play : tournament.getTournament()) {
			unique_maps.add(game_play.getFile_name());
		}

		for (String string : unique_maps) {
			game_plays = new ArrayList<>();
			for (GamePlay game_play : tournament.getTournament()) {
				if (game_play.getFile_name().equalsIgnoreCase(string)) {
					if (game_play.getWinner() == null || game_play.getWinner().equals("")) {
						game_play.setWinner("Draw");
					}
					game_plays.add(game_play);
				}
			}
			each_map_results.put(string, game_plays);
		}

		tournament_results.setEach_map_results(each_map_results);
		tournament.setTournament_results(tournament_results);
	}

	/**
	 * This method checks if the current player is out of the game or not.
	 * 
	 * @return True If the current player is out of the game.
	 */
	private boolean checkIfCurrentPlayerOut(GamePlay game_play) {
		Player current_player = null;
		for (Player player : game_play.getGame_state()) {
			if (player.getId() == game_play.getCurrent_player()) {
				current_player = player;
				break;
			}
		}
		if (current_player.getTerritory_list().size() == 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * @see com.risk.business.IManageGamePlay#fetchGamePlay(String)
	 * @author <a href="mailto:a_semwal@encs.concordia.ca">ApoorvSemwal</a>
	 */
	@Override
	public GamePlay fetchGamePlay(String file) {
		IManageGamePlayFile game_file_manager = new ManageGamePlayFile();
		return game_file_manager.fetchGamePlay(file);
	}

	/**
	 * @see com.risk.business.IManageGamePlay#fetchGamePlays()
	 * @author <a href="mailto:a_semwal@encs.concordia.ca">ApoorvSemwal</a>
	 */
	@Override
	public List<String> fetchGamePlays() {
		List<String> game_play_list = new ArrayList<>();
		IManageGamePlayFile file_object = new ManageGamePlayFile();
		game_play_list = file_object.fetchGamePlayFilesFromResource();
		return game_play_list;
	}

	/**
	 * This method here serves for the implementation of Observer Pattern in our
	 * Project. It handles multiple phases during game play as per risk rules. As
	 * the GUI captures data and events for a particular phase and triggers a state
	 * change in GamePlay Object, this class here is being observed by
	 * ManageGamePlay as an observer.
	 * 
	 * @author <a href="mailto:a_semwal@encs.concordia.ca">ApoorvSemwal</a>
	 */
	@Override
	public void update(Observable o, Object arg) {
		managePhase((GamePlay) arg);
	}
}