package com.risk.model.Strategy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.risk.business.IManagePlayer;
import com.risk.business.impl.ManagePlayer;
import com.risk.model.GamePlay;
import com.risk.model.GamePlayTerritory;
import com.risk.model.Player;
import com.risk.model.Strategy.Benevolent;
import com.risk.model.gui.PlayerDetails;
import com.risk.model.gui.SinglePlayer;

/**
 * This is a test class for benevolent player
 * 
 * @author <a href="mailto:mayankjariwala1994@gmail.com">Mayank Jariwala</a>
 * @version 0.0.3
 */
public class BenevolentTest {

	private static IManagePlayer manage_player;
	private GamePlay game_play;

	@Before
	public void initManagePlayer() {
		game_play = new GamePlay();
		manage_player = new ManagePlayer();
	}

	/**
	 * This method is use create benevolent player
	 * 
	 * @author <a href="mailto:mayankjariwala1994@gmail.com">MayankJariwala</a>
	 */
	public void createBenevolentPlayer() {
		PlayerDetails single_game_input = new PlayerDetails();
		single_game_input.setAllocationType("A");
		single_game_input.setFileName("Switzerland.map");
		single_game_input.setPlayersNo(2);
		List<SinglePlayer> players = new ArrayList<>();
		for (int i = 1; i <= 2; i++) {
			SinglePlayer player = new SinglePlayer();
			player.setId(Integer.toString(i));
			player.setType("Computer");
			player.setBehaviour("Benevolent");
			players.add(player);
		}
		single_game_input.setPlayers(players);
		game_play = manage_player.createPlayer(single_game_input);
	}

	/**
	 * This test function checks for valid reinforcement for benevolent player
	 * 
	 * @author <a href="mailto:mayankjariwala1994@gmail.com">MayankJariwala</a>
	 */
	@Test
	public void checkValidReinforceTest() {
		createBenevolentPlayer();
		game_play.getGame_state().get(game_play.getCurrent_player() - 1).executeStrategy("REINFORCE", game_play);
		assertEquals(3, game_play.getGame_state().get(game_play.getCurrent_player() - 1).getTerritory_list().get(0)
				.getNumber_of_armies());
	}

	/**
	 * This test function checks for valid fortification for benevolent player
	 * 
	 * @author <a href="mailto:mayankjariwala1994@gmail.com">MayankJariwala</a>
	 */
	@Test
	public void checkValidForitificationTest() {
		createBenevolentPlayer();
		game_play = game_play.getGame_state().get(game_play.getCurrent_player() - 1).executeStrategy("FORTIFY",
				game_play);
		assertTrue(game_play.getStatus().contains("Fortification not possible"));
	}

	/**
	 * This test function checks for expected weak territories list for benevolent
	 * player
	 * 
	 * @author <a href="mailto:mayankjariwala1994@gmail.com">MayankJariwala</a>
	 */
	@Test
	public void getWeakTerritoryTest() {
		createBenevolentPlayer();
		Benevolent benevolent = new Benevolent();
		Player benevolent_player = game_play.getGame_state().get(game_play.getCurrent_player() - 1);
		List<Integer> armies_value = benevolent.getListofArmiesValues(benevolent_player.getTerritory_list());
		List<GamePlayTerritory> weak_territories = benevolent.getWeakTerritoriesOfPlayer(armies_value,
				benevolent_player.getTerritory_list());
		List<String> weak_territories_name = new ArrayList<>();
		List<String> expected_weak_territories_name = Arrays.asList("Graubunden", "Varduz");
		for (int i = 0; i < weak_territories.size(); i++) {
			weak_territories_name.add(weak_territories.get(i).getTerritory_name());
		}
		assertEquals(expected_weak_territories_name, weak_territories_name);
	}
}
