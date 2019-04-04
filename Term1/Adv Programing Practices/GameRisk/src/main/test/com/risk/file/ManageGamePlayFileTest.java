package com.risk.file;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.risk.business.IManagePlayer;
import com.risk.business.impl.ManagePlayer;
import com.risk.file.impl.ManageGamePlayFile;
import com.risk.model.GamePlay;
import com.risk.model.gui.PlayerDetails;
import com.risk.model.gui.SinglePlayer;

/**
 * @author <a href="mailto:himansipatel1994@gmail.com">Himansi Patel</a>
 * @version 0.0.3
 */
public class ManageGamePlayFileTest {
	private static IManagePlayer manage_player;
	private GamePlay game_play;

	@Before
	public void initManagePlayer() {
		game_play = new GamePlay();
		manage_player = new ManagePlayer();
	}

	/**
	 * This test case checks the state of GamePlay Object is written in the file
	 */
	@Test
	public void checkValidSaveGameStateToDiskTest() {
		PlayerDetails single_game_input = new PlayerDetails();
		single_game_input.setAllocationType("A");
		single_game_input.setFileName("Switzerland.map");
		single_game_input.setPlayersNo(2);
		List<SinglePlayer> players = new ArrayList<>();
		for (int i = 1; i <= 2; i++) {
			SinglePlayer player = new SinglePlayer();
			player.setId(Integer.toString(i));
			player.setType("Computer");
			player.setBehaviour("Cheater");
			players.add(player);
		}
		single_game_input.setPlayers(players);
		game_play = manage_player.createPlayer(single_game_input);
		ManageGamePlayFile manage_game_file = new ManageGamePlayFile();
		boolean result = manage_game_file.saveGameStateToDisk(game_play);
		assertTrue(result);
	}

	/**
	 * This test case checks the valid GamePlay object is returned as result from
	 * saved file
	 */
	@Test
	public void checkValidFetchGamePlayTest() {
		PlayerDetails single_game_input = new PlayerDetails();
		single_game_input.setAllocationType("A");
		single_game_input.setFileName("Switzerland.map");
		single_game_input.setPlayersNo(2);
		List<SinglePlayer> players = new ArrayList<>();
		for (int i = 1; i <= 2; i++) {
			SinglePlayer player = new SinglePlayer();
			player.setId(Integer.toString(i));
			player.setType("Computer");
			player.setBehaviour("Cheater");
			players.add(player);
		}
		single_game_input.setPlayers(players);
		game_play = manage_player.createPlayer(single_game_input);
		ManageGamePlayFile manage_game_file = new ManageGamePlayFile();
		game_play = manage_game_file.fetchGamePlay("Switzerland_1543026821718.txt");
		assertEquals(1, game_play.getGame_state().get(0).getId());
	}
}
