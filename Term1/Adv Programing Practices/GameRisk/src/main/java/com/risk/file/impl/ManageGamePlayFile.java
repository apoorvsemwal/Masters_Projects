package com.risk.file.impl;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.risk.business.impl.ManageMap;

import com.risk.file.IManageGamePlayFile;
import com.risk.model.Card;
import com.risk.model.Domination;
import com.risk.model.GamePlay;
import com.risk.model.GamePlayTerritory;
import com.risk.model.Map;
import com.risk.model.Player;
import com.risk.model.Strategy.Aggressive;
import com.risk.model.Strategy.Benevolent;
import com.risk.model.Strategy.Cheater;
import com.risk.model.Strategy.Human;
import com.risk.model.Strategy.Random;

/**
 * This class is use to Manage Game Play Information including map name,
 * Domination Detail, players information and their state
 * 
 * @author <a href="mailto:himansipatel1994@gmail.com">Himansi Patel</a>
 * @author <a href="mailto:mayankjariwala1994@gmail.com">Mayank Jariwala</a> -
 *         Added Class Description , Logger and checkError Function
 * @version 0.0.1
 */
@Service
public class ManageGamePlayFile implements IManageGamePlayFile {

	/**
	 * @see com.risk.file.IManageGamePlayFile#saveGameStateToDisk(GamePlay)
	 * @author <a href="mailto:himansipatel1994@gmail.com">Himansi Patel</a>
	 * @author <a href="mailto:mayankjariwala1994@gmail.com">Mayank Jariwala</a> *
	 */
	@Override
	public Boolean saveGameStateToDisk(GamePlay game_play) {
		boolean file_writer_message = false;
		String file_name = game_play.getFile_name();
		String game_phase = game_play.getGame_phase();
		int current_player_id = game_play.getCurrent_player();
		String status_message = "";
		if (game_play.getStatus() == "") {
			status_message = null;
		} else {
			status_message = game_play.getStatus().trim();
			status_message = status_message.replace("\n", "##");
		}

		int game_play_id = 1;
		int game_play_turn = game_play.getGame_play_turns();
		List<Domination> domination_list = game_play.getDomination();

		List<Player> player_list = game_play.getGame_state();
		if(file_name.endsWith(".map")) {
			file_name = file_name.endsWith(".map") ? file_name.split("\\.")[0] : file_name;	
		}else if(file_name.contains("_")) {
			file_name = file_name.split("_")[0];
		}
		file_name = file_name + "_" + String.valueOf(System.currentTimeMillis());

		try (PrintStream player_file_writer = new PrintStream(
				new BufferedOutputStream(new FileOutputStream("src/main/resource/gameplay/" + file_name + ".txt")))) {
			player_file_writer.println("[Gameplay]");
			player_file_writer.println("Map name=" + file_name);
			player_file_writer.println("Phase=" + game_phase);
			player_file_writer.println("Current Player Id=" + current_player_id);
			player_file_writer.println("Status=" + status_message);
			player_file_writer.println("Game Play Id=" + game_play_id);
			player_file_writer.println("Game Play Turns=" + game_play_turn);
			player_file_writer.println();
			player_file_writer.println("[Free Cards]");
			for (int i = 0; i < game_play.getFree_cards().size(); i++) {
				player_file_writer.println(game_play.getFree_cards().get(i).getTerritory_name() + ","
						+ game_play.getFree_cards().get(i).getArmy_type());
			}
			player_file_writer.println();

//			Fetching Domination data for all Current Players Object Playing in the Game
			if (domination_list.size() > 0) {
				for (int domination_index = 0; domination_index < domination_list.size(); domination_index++) {
					player_file_writer.println("[Domination]");
					player_file_writer.println("Id=" + domination_list.get(domination_index).getPlayer_id());
					player_file_writer
							.println("Map Coverage=" + domination_list.get(domination_index).getMap_coverage());
					player_file_writer.println(
							"Player Army Count=" + domination_list.get(domination_index).getPlayer_army_count());
					player_file_writer.println();
//				Getting territories occupied by each player and writing it to file
					player_file_writer.println("[Player Continent list]");
					if (domination_list.get(domination_index).getPlayer_continent_list() != null
							&& domination_list.get(domination_index).getPlayer_continent_list().size() > 0) {
						for (int j = 0; j < domination_list.get(domination_index).getPlayer_continent_list()
								.size(); j++) {
							player_file_writer
									.println(domination_list.get(domination_index).getPlayer_continent_list().get(j));
						}
						player_file_writer.println();
					} else {
						player_file_writer.println();
					}
				}
			}

//			Fetching all Current Players Object Playing in the Game
			for (int player_index = 0; player_index < player_list.size(); player_index++) {
				player_file_writer.println("[Player]");
				player_file_writer.println("Id=" + player_list.get(player_index).getId());
				player_file_writer.println("Name=" + player_list.get(player_index).getName());
				player_file_writer.println("Armies Stock=" + player_list.get(player_index).getArmy_stock());
				player_file_writer
						.println("Any Territory Occupied=" + player_list.get(player_index).isAny_territory_occupied());
				player_file_writer.println("Trade Count=" + player_list.get(player_index).getTrade_count());
				player_file_writer.println("Type=" + player_list.get(player_index).getType());
				player_file_writer.println("Strategy Name=" + player_list.get(player_index).getStrategy_name());
				player_file_writer.println();
				player_file_writer.println("[Territories]");
//				Getting territories occupied by each player and writing it to file
				for (int j = 0; j < player_list.get(player_index).getTerritory_list().size(); j++)
					player_file_writer
							.println(player_list.get(player_index).getTerritory_list().get(j).getTerritory_name() + ","
									+ player_list.get(player_index).getTerritory_list().get(j).getContinent_name() + ","
									+ player_list.get(player_index).getTerritory_list().get(j).getNumber_of_armies());
				player_file_writer.println();
				player_file_writer.println("[Cards]");
//				Getting cards occupied by each player and writing it to file
				if (player_list.get(player_index).getCard_list() != null) {
					for (int k = 0; k < player_list.get(player_index).getCard_list().size(); k++)
						player_file_writer
								.println(player_list.get(player_index).getCard_list().get(k).getTerritory_name() + ","
										+ player_list.get(player_index).getCard_list().get(k).getArmy_type());
				}
				player_file_writer.println();
			}
			if (player_file_writer.checkError()) {
				file_writer_message = false;
			} else {
				file_writer_message = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return file_writer_message;
	}

	/**
	 * @see com.risk.file.IManageGamePlayFile#fetchGamePlay(java.lang.String)
	 * @author <a href="mailto:himansipatel1994@gmail.com">Himansi Patel</a>
	 * @author <a href="mailto:mayankjariwala1994@gmail.com">Mayank Jariwala</a>
	 */
	@Override
	public GamePlay fetchGamePlay(String file_name) {
		List<GamePlayTerritory> player_territory_info = new ArrayList<>();
		List<Card> player_card_info = new ArrayList<>();
		Player player_info = null;
		List<Player> players_list = new ArrayList<>();
		List<Card> free_cards_list = new ArrayList<>();
		List<Domination> domination_list = new ArrayList<>();
		List<String> domination_player_continent = new ArrayList<>();
		GamePlay game_play = new GamePlay();
		Domination player_domination_info = null;
		try (BufferedReader game_file_reader = new BufferedReader(
				new FileReader("src/main/resource/gameplay/" + file_name))) {
			String file_line, current_section = "";

			while ((file_line = game_file_reader.readLine()) != null) {
				if (file_line.equalsIgnoreCase("[Gameplay]")) {
					current_section = "Gameplay";
					file_line = game_file_reader.readLine();
				}
				if (file_line.equalsIgnoreCase("[Free Cards]")) {
					current_section = "Free Cards";
					file_line = game_file_reader.readLine();
				}
				if (file_line.equalsIgnoreCase("[Domination]")) {
					player_domination_info = new Domination();
					current_section = "Domination";
					file_line = game_file_reader.readLine();
				}
				if (file_line.equalsIgnoreCase("[Player Continent list]")) {
					domination_player_continent = new ArrayList<>();
					current_section = "Player Continent list";
					file_line = game_file_reader.readLine();
				}
				if (file_line.equalsIgnoreCase("[Player]")) {
					player_info = new Player();
					current_section = "player";
					file_line = game_file_reader.readLine();
				}
				if (file_line.equalsIgnoreCase("[Territories]")) {
					player_territory_info = new ArrayList<GamePlayTerritory>();
					current_section = "territories";
					file_line = game_file_reader.readLine();
				}
				if (file_line.equalsIgnoreCase("[Cards]")) {
					player_card_info = new ArrayList<Card>();
					current_section = "cards";
					file_line = game_file_reader.readLine();
				}

//					check the current section of file
				if (current_section.equalsIgnoreCase("Gameplay")) {
					setGamePlayInfo(file_line, game_play);
				}
				if (current_section.equalsIgnoreCase("Free Cards")) {
					setFreeCardInfo(file_line, game_play, free_cards_list);
				}
				if (current_section.equalsIgnoreCase("Domination")) {
					setDominationPlayerInfo(file_line, player_domination_info);
				}
				if (current_section.equalsIgnoreCase("Player Continent list")) {
					if (file_line.length() > 0) {
						setDominationPlayerContinentInfo(file_line, domination_player_continent,
								player_domination_info);
					} else {
						domination_list.add(player_domination_info);
						game_play.setDomination(domination_list);
					}
				}
				if (current_section.equals("player")) {
					setPlayerBasicInfo(file_line, player_info);
				}
				if (current_section.equals("territories")) {
					setPlayerTerritoryInfo(player_info, player_territory_info, file_line);
				}
				if (current_section.equals("cards")) {
					if (file_line.length() > 0) {
						setPlayerCardInfo(player_info, player_card_info, file_line);

					} else {
						current_section = "";
						players_list.add(player_info);
						game_play.setGame_state(players_list);
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return game_play;
	}

	@Override
	/**
	 * @see com.risk.file.IManageGamePlayFile#fetchGamePlayFilesFromResource()
	 * @author <a href="mailto:apoorv.semwal20@gmail.com">Apoorv Semmwal</a>
	 * @return List of GamePlay Files.
	 */
	public List<String> fetchGamePlayFilesFromResource() {
		List<String> list_of_gameplay_files = new ArrayList<>();
		java.io.File resource_folder = new java.io.File("src/main/resource/gameplay");
		java.io.File[] listOfGamePlayFiles = resource_folder.listFiles();
		if (listOfGamePlayFiles.length > 0) {
			for (java.io.File file : listOfGamePlayFiles) {
				if (file.isFile() && file.getName().endsWith(".txt")) {
					list_of_gameplay_files.add(file.getName());
				}
			}
		}
		return list_of_gameplay_files;
	}

	/**
	 * This function is use to set domination information for each player's
	 * continent list
	 * 
	 * @author <a href="mailto:himansipatel1994@gmail.com">Himansi Patel</a>
	 * @param file_line                        : file one line data
	 * @param domination_player_continent_list : occupied list of continent
	 * @param player_domination_info           : domination object
	 */
	private void setDominationPlayerContinentInfo(String file_line, List<String> domination_player_continent_list,
			Domination player_domination_info) {
		if (file_line.length() > 0) {
			domination_player_continent_list.add(file_line);
			player_domination_info.setPlayer_continent_list(domination_player_continent_list);
		}
	}

	/**
	 * This function is use to set domination information for each player like
	 * player's id, map coverage, player army count
	 * 
	 * @author <a href="mailto:himansipatel1994@gmail.com">Himansi Patel</a>
	 * @param file_line              : file one line data
	 * @param player_domination_info : domination object
	 */
	private void setDominationPlayerInfo(String file_line, Domination player_domination_info) {
		if (file_line.length() > 0) {
			String key = file_line.split("=")[0];
			String value = file_line.split("=")[1];

			if (key.equalsIgnoreCase("Id")) {
				player_domination_info.setPlayer_id(Integer.parseInt(value));
			} else if (key.equalsIgnoreCase("Map Coverage")) {
				player_domination_info.setMap_coverage(Double.parseDouble(value));
			} else if (key.equalsIgnoreCase("Player Army Count")) {
				player_domination_info.setPlayer_army_count(Integer.parseInt(value));
			}
		}
	}

	/**
	 * This function is use to set free card list to play game
	 * 
	 * @author <a href="mailto:himansipatel1994@gmail.com">Himansi Patel</a>
	 * @param file_line       : file one line data
	 * @param game_play       : GamePlay object
	 * @param free_cards_list : free card list to play game
	 */
	private void setFreeCardInfo(String file_line, GamePlay game_play, List<Card> free_cards_list) {
		if (file_line.length() > 0) {
			Card card = new Card();
			String[] card_info = file_line.split(",");
			card.setTerritory_name(card_info[0]);
			card.setArmy_type(card_info[1]);
			free_cards_list.add(card);
			game_play.setFree_cards(free_cards_list);
		}
	}

	/**
	 * This function is use to set GamePlay basic information
	 * 
	 * @author <a href="mailto:himansipatel1994@gmail.com">Himansi Patel</a>
	 * @param file_line : file one line data
	 * @param game_play : GamePlay object
	 */
	private void setGamePlayInfo(String file_line, GamePlay game_play) {
		if (file_line.length() > 0) {
			String key = file_line.split("=")[0];
			String value = file_line.split("=")[1];

			if (key.equalsIgnoreCase("Map name")) {
				String file_name = (value.split("_")[0]) + ".map";
				ManageMap manage_map_object = new ManageMap();
				Map map = manage_map_object.getFullMap(file_name);
				game_play.setFile_name(file_name);
				game_play.setMap(map);
				game_play.setGui_map(manage_map_object.fetchMap(file_name));
			} else if (key.equalsIgnoreCase("Phase")) {
				game_play.setGame_phase(value);
			} else if (key.equalsIgnoreCase("Current Player Id")) {
				game_play.setCurrent_player(Integer.parseInt(value));
			} else if (key.equalsIgnoreCase("Status")) {
				value = value.replace("##", System.getProperty("line.separator"));
				game_play.setStatus(value);
			} else if (key.equalsIgnoreCase("Game Play Id")) {
				game_play.setGame_play_id(Integer.parseInt(value));
			} else if (key.equalsIgnoreCase("Game Play Turns")) {
				game_play.setGame_play_turns(Integer.parseInt(value));
			}
		}
	}

	/**
	 * This function is use to set each player basic information.
	 * 
	 * @author <a href="mailto:himansipatel1994@gmail.com">Himansi Patel</a>
	 * @param file_line   : file one line data
	 * @param player_info : player object
	 */
	private void setPlayerBasicInfo(String file_line, Player player_info) {
		if (file_line.length() > 0) {
			String key = file_line.split("=")[0];
			String value = file_line.split("=")[1];
			if (key.equalsIgnoreCase("Id")) {
				player_info.setId(Integer.parseInt(value));
			} else if (key.equalsIgnoreCase("Name")) {
				player_info.setName(value);
			} else if (key.equalsIgnoreCase("Armies Stock")) {
				player_info.setArmy_stock(Integer.parseInt(value));
			} else if (key.equalsIgnoreCase("Any Territory Occupied")) {
				player_info.setAny_territory_occupied(Boolean.parseBoolean(value));
			} else if (key.equalsIgnoreCase("Trade Count")) {
				player_info.setTrade_count(Integer.parseInt(value));
			} else if (key.equalsIgnoreCase("Type")) {
				player_info.setType(value);
			} else if (key.equalsIgnoreCase("Strategy Name")) {
				player_info.setStrategy_name(value);
				if (value.equalsIgnoreCase("Human")) {
					player_info.setStrategy(new Human());
				} else if (value.equalsIgnoreCase("Aggressive")) {
					player_info.setStrategy(new Aggressive());
				} else if (value.equalsIgnoreCase("Benevolent")) {
					player_info.setStrategy(new Benevolent());
				} else if (value.equalsIgnoreCase("Random")) {
					player_info.setStrategy(new Random());
				} else if (value.equalsIgnoreCase("Cheater")) {
					player_info.setStrategy(new Cheater());
				}
			}
		}
	}

	/**
	 * This function is use to set player territory information.
	 * 
	 * @author <a href="mailto:himansipatel1994@gmail.com">Himansi Patel</a>
	 * @param player_game_info      : player object
	 * @param player_territory_info : territory list of player
	 * @param file_line             : file one line data
	 */
	private void setPlayerTerritoryInfo(Player player_game_info, List<GamePlayTerritory> player_territory_info,
			String file_line) {
		if (file_line.length() > 0) {
			GamePlayTerritory gamePlayTerritory = new GamePlayTerritory();
			String[] territory_info = file_line.split(",");
			gamePlayTerritory.setTerritory_name(territory_info[0]);
			gamePlayTerritory.setContinent_name(territory_info[1]);
			gamePlayTerritory.setNumber_of_armies(Integer.parseInt(territory_info[2]));
			player_territory_info.add(gamePlayTerritory);
			player_game_info.setTerritory_list(player_territory_info);
		}
	}

	/**
	 * This function is use to set player card information.
	 * 
	 * @author <a href="mailto:himansipatel1994@gmail.com">Himansi Patel</a>
	 * @param player_info      : player object
	 * @param player_card_info : card list of player
	 * @param file_line        : file one line data
	 */
	private void setPlayerCardInfo(Player player_info, List<Card> player_card_info, String file_line) {

		if (file_line.length() > 0) {
			Card card = new Card();
			String[] card_info = file_line.split(",");
			card.setTerritory_name(card_info[0]);
			card.setArmy_type(card_info[1]);
			player_card_info.add(card);
			player_info.setCard_list(player_card_info);
		}
	}
}