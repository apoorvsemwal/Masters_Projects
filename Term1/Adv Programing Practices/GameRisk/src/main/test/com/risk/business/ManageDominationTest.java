package com.risk.business;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.risk.business.impl.ManagePlayer;
import com.risk.model.GamePlay;
import com.risk.model.gui.PlayerDetails;
import com.risk.model.gui.SinglePlayer;

/**
 * 
 * @author <a href="mayankjariwala1994@gmail.com">Mayank Jariwala</a>
 * @author <a href="himansipatel1994@gmail.com">Himansi Patel</a>
 * @version 0.0.1
 */
public class ManageDominationTest {

	private GamePlay game_play;

	@Before
	public void createObject() {
		game_play = new GamePlay();
	}

	/**
	 * Domination Test for Map Coverage of each Player
	 * @author <a href="mayankjariwala1994@gmail.com">Mayank Jariwala</a>
	 * @author <a href="himansipatel1994@gmail.com">Himansi Patel</a>
	 * 
	 */
	@Test
	public void checkDominationResultPlayerMapCoverageTest() {
		ManagePlayer       manage_player     = new ManagePlayer();
		PlayerDetails      single_game_input = new PlayerDetails();
		single_game_input.setAllocationType("A");
		single_game_input.setFileName("Switzerland.map");
		single_game_input.setPlayersNo(2);
		List<SinglePlayer> players = new ArrayList<>();
		for (int i = 1; i <= 2; i++) {
			SinglePlayer player = new SinglePlayer();
			player.setId(Integer.toString(i));
			player.setType("Human");
			player.setBehaviour("Human");
			players.add(player);
		}
		single_game_input.setPlayers(players);

		game_play = manage_player.createPlayer(single_game_input);
		assertEquals(51.9, game_play.getDomination().get(0).getMap_coverage(), 0.0);
		assertEquals(48.1, game_play.getDomination().get(1).getMap_coverage(), 0.0);
	}

	/**
	 * Domination Test for Total Armies of each Player
	 * @author <a href="mayankjariwala1994@gmail.com">Mayank Jariwala</a>
	 * @author <a href="himansipatel1994@gmail.com">Himansi Patel</a>
	 * 
	 */
	@Test
	public void checkDominationResultPlayerTotalArmiesTest() {
		ManagePlayer       manage_player     = new ManagePlayer();
		PlayerDetails      single_game_input = new PlayerDetails();
		single_game_input.setAllocationType("A");
		single_game_input.setFileName("Switzerland.map");
		single_game_input.setPlayersNo(2);
		List<SinglePlayer> players = new ArrayList<>();
		for (int i = 1; i <= 2; i++) {
			SinglePlayer player = new SinglePlayer();
			player.setId(Integer.toString(i));
			player.setType("Human");
			player.setBehaviour("Human");
			players.add(player);
		}
		single_game_input.setPlayers(players);

		game_play = manage_player.createPlayer(single_game_input);
		assertEquals(40, game_play.getDomination().get(0).getPlayer_army_count());
		assertEquals(40, game_play.getDomination().get(1).getPlayer_army_count());
	}

	/**
	 * Domination Test for Continent Ows of each Player
	 * @author <a href="mayankjariwala1994@gmail.com">Mayank Jariwala</a>
	 * @author <a href="himansipatel1994@gmail.com">Himansi Patel</a>
	 * 
	 */
	@Test
	public void checkDominationResultPlayerContinentOwsTest() {
		ManagePlayer       manage_player     = new ManagePlayer();
		PlayerDetails      single_game_input = new PlayerDetails();
		single_game_input.setAllocationType("A");
		single_game_input.setFileName("Switzerland.map");
		single_game_input.setPlayersNo(2);
		List<SinglePlayer> players = new ArrayList<>();
		for (int i = 1; i <= 2; i++) {
			SinglePlayer player = new SinglePlayer();
			player.setId(Integer.toString(i));
			player.setType("Human");
			player.setBehaviour("Human");
			players.add(player);
		}
		single_game_input.setPlayers(players);

		game_play = manage_player.createPlayer(single_game_input);
		assertEquals(Arrays.asList("Liechtenstien"), game_play.getDomination().get(0).getPlayer_continent_list());
		assertEquals(Arrays.asList("Basel", "Lowlands"), game_play.getDomination().get(1).getPlayer_continent_list());
	}
}
