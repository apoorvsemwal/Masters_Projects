package com.risk.business;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.risk.business.impl.ManageGamePlay;
import com.risk.business.impl.ManagePlayer;
import com.risk.model.GamePlay;
import com.risk.model.Tournament;
import com.risk.model.TournamentResults;
import com.risk.model.gui.PlayerDetails;
import com.risk.model.gui.SinglePlayer;
import com.risk.model.gui.TournamentChoices;

/**
 * @author <a href="mailto:a_semwal@encs.concordia.ca">ApoorvSemwal</a>
 * @version 0.0.1
 */
public class ManageGamePlayTest {

	private IManagePlayer player_manager;
	private IManageGamePlay game_manager;

	@Before
	public void initMapManager() {
		player_manager = new ManagePlayer();
		game_manager = new ManageGamePlay();
	}

	/**
	 * Creating a game state using AutoAllocationMode - A. Auto Allocation Mode
	 * Performs an initial automatic allocation of armies during startup phase and
	 * then calls calculateArmiesReinforce to give an additional amount of armies
	 * based on the Continent score if any player holds control over an multiple
	 * continents.
	 *
	 * @author <a href="mailto:a_semwal@encs.concordia.ca">ApoorvSemwal</a>
	 */
	@Test
	public void testCalculateArmiesReinforceMultiContinent() {

		/**
		 * Using auto allocation Player 2 captures entire "Lowlands" Continent - Score =
		 * 1 "Basel" Continent - Score = 1 Initial army stock 0 - New Stock after
		 * calculation = 5 = (3 + 2) 3 on the basis of 5 Territories it holds and 2 from
		 * Continent Scores
		 */
		PlayerDetails single_game_input = new PlayerDetails();
		single_game_input.setAllocationType("A");
		single_game_input.setFileName("Switzerland.map");
		single_game_input.setPlayersNo(6);
		List<SinglePlayer> players = new ArrayList<>();
		for (int i = 1; i <= 6; i++) {
			SinglePlayer player = new SinglePlayer();
			player.setId(Integer.toString(i));
			player.setType("Human");
			player.setBehaviour("Human");
			players.add(player);
		}
		single_game_input.setPlayers(players);

		GamePlay game_state = player_manager.createPlayer(single_game_input);

		game_state.setCurrent_player(2);
		game_manager.calculateArmiesReinforce(game_state.getGame_state(), game_state.getMap(),
				game_state.getCurrent_player());
		assertEquals(5, game_state.getGame_state().get(1).getArmy_stock());

	}

	/**
	 * Creating a game state using AutoAllocationMode - A. Auto Allocation Mode
	 * Performs an initial automatic allocation of armies during startup phase and
	 * then calls calculateArmiesReinforce to give an additional amount of armies
	 * based on the Continent score if any player holds control over an entire
	 * continent.
	 *
	 * 
	 * @author <a href="mailto:a_semwal@encs.concordia.ca">ApoorvSemwal</a>
	 */
	@Test
	public void testCalculateArmiesReinforceSingleContinent() {

		/**
		 * Using auto allocation Player 3 captures entire "Liechtenstien" Continent -
		 * Score = 1 Initial army stock 0 - New Stock after calculation = 4 = (3 + 1) 3
		 * on the basis of 5 Territories it holds and 1 from Continent Score.
		 */
		PlayerDetails single_game_input = new PlayerDetails();
		single_game_input.setAllocationType("A");
		single_game_input.setFileName("Switzerland.map");
		single_game_input.setPlayersNo(6);
		List<SinglePlayer> players = new ArrayList<>();
		for (int i = 1; i <= 6; i++) {
			SinglePlayer player = new SinglePlayer();
			player.setId(Integer.toString(i));
			player.setType("Human");
			player.setBehaviour("Human");
			players.add(player);
		}
		single_game_input.setPlayers(players);

		GamePlay game_state = player_manager.createPlayer(single_game_input);

		game_state.setCurrent_player(3);
		game_manager.calculateArmiesReinforce(game_state.getGame_state(), game_state.getMap(),
				game_state.getCurrent_player());
		assertEquals(4, game_state.getGame_state().get(2).getArmy_stock());
	}
	
	/**
	 * Cheater Winner Test in single player mode
	 * 
	 * @author <a href="mailto:zinnia.rana.22@gmail.com">Zinnia Rana</a>
	 */
	@Test
	public void testForCheaterWinner() {
		PlayerDetails single_game_input = new PlayerDetails();
		single_game_input.setAllocationType("A");
		single_game_input.setFileName("Switzerland.map");
		single_game_input.setPlayersNo(2);
		List<SinglePlayer> players = new ArrayList<>();
		SinglePlayer player = new SinglePlayer();
		player.setId(Integer.toString(1));
		player.setType("Computer");
		player.setBehaviour("Benevolent");
		players.add(player);
		single_game_input.setPlayers(players);
		player.setId(Integer.toString(2));
		player.setType("Computer");
		player.setBehaviour("Cheater");
		players.add(player);
		single_game_input.setPlayers(players);
		GamePlay game_state = player_manager.createPlayer(single_game_input);
		game_state.getGame_state().get(game_state.getCurrent_player() - 1).executeStrategy("ATTACK", game_state);
		assertEquals("Player1 Behaviour : Cheater", game_state.getWinner());
	}
	
	/**
	 * Creating a game state using AutoAllocationMode - A. Auto Allocation Mode
	 * Performs an initial automatic allocation of armies during startup phase and
	 * then calls calculateArmiesReinforce ,after that checks the next valid game
	 * play phase is Reinforcement
	 *
	 * 
	 * @author <a href="mailto:himansipatel1994@gmail.com">Himansi Patel</a>
	 */
	@Test
	public void checkValidGamePlayPhaseTest() {
		PlayerDetails single_game_input = new PlayerDetails();
		single_game_input.setAllocationType("A");
		single_game_input.setFileName("Switzerland.map");
		single_game_input.setPlayersNo(6);
		List<SinglePlayer> players = new ArrayList<>();
		for (int i = 1; i <= 6; i++) {
			SinglePlayer player = new SinglePlayer();
			player.setId(Integer.toString(i));
			player.setType("Human");
			player.setBehaviour("Human");
			players.add(player);
		}
		single_game_input.setPlayers(players);

		GamePlay game_state = player_manager.createPlayer(single_game_input);

		game_state.setCurrent_player(3);
		game_manager.calculateArmiesReinforce(game_state.getGame_state(), game_state.getMap(),
				game_state.getCurrent_player());
		assertEquals("REINFORCEMENT", game_state.getGame_phase());
	}

	/**
	 * Tournament on Invalid Map Test
	 * 
	 * @author <a href="mailto:mayankjariwala1994@gmail.com">Mayank Jariwala</a>
	 */
	@Test
	public void testForInvalidMapTournament() {
		TournamentChoices tournament_info = new TournamentChoices();
		tournament_info.setMapNames(Arrays.asList("USA - Disconnected Continent.map"));
		tournament_info.setMultipleStrategies(Arrays.asList("Benevolent", "Cheater", "Aggressive"));
		tournament_info.setMaxTurns(20);
		tournament_info.setNoOfGamesToPlay(2);
		Tournament tournament = game_manager.prepareTournamentGamePlay(tournament_info);
		assertTrue(tournament.getStatus().contains("Disconnected"));
	}

	/**
	 * Tournament on Valid Maps Test
	 * 
	 * @author <a href="mailto:mayankjariwala1994@gmail.com">Mayank Jariwala</a>
	 */
	@Test
	public void testForTournamentValidMapsTournament() {
		TournamentChoices tournament_info = new TournamentChoices();
		tournament_info.setMapNames(Arrays.asList("Asia.map", "Alberta.map"));
		tournament_info.setMultipleStrategies(Arrays.asList("Benevolent", "Cheater", "Aggressive"));
		tournament_info.setMaxTurns(20);
		tournament_info.setNoOfGamesToPlay(2);
		Tournament tournament = game_manager.prepareTournamentGamePlay(tournament_info);
		assertTrue(tournament.getStatus().contains("Tournament Ready"));
	}

	/**
	 * Tournament Winner Test For Cheater Player
	 * 
	 * @author <a href="mailto:mayankjariwala1994@gmail.com">Mayank Jariwala</a>
	 */
	@Test
	public void testForTournamentWinner() {
		TournamentChoices tournament_info = new TournamentChoices();
		TournamentResults tournament_results = new TournamentResults();
		tournament_info.setMapNames(Arrays.asList("TestAB.map"));
		tournament_info.setMultipleStrategies(Arrays.asList("Benevolent", "Cheater", "Aggressive"));
		tournament_info.setMaxTurns(20);
		tournament_info.setNoOfGamesToPlay(2);
		Tournament tournament = game_manager.prepareTournamentGamePlay(tournament_info);
		while (!tournament.getStatus().equalsIgnoreCase("TOURNAMENT_OVER\n")) {
			tournament = game_manager.playTournamentMode(tournament);
			tournament_results = tournament.getTournament_results();
		}
		List<String> winner = new ArrayList<>();
		List<String> expected_winner = Arrays.asList("Player2 Behaviour : Cheater", "Player2 Behaviour : Cheater");
		tournament_results.getEach_map_results().forEach((map, results) -> {
			results.forEach(result -> {
				winner.add(result.getWinner());
			});
		});
		assertEquals(winner, expected_winner);
	}

	/**
	 * Tournament Draw Test For Aggressive Player vs Benevolent Player
	 * 
	 * @author <a href="mailto:mayankjariwala1994@gmail.com">Mayank Jariwala</a>
	 */
	@Test
	public void testForTournamentDraw() {
		TournamentChoices tournament_info = new TournamentChoices();
		TournamentResults tournament_results = new TournamentResults();
		tournament_info.setMapNames(Arrays.asList("World.map"));
		tournament_info.setMultipleStrategies(Arrays.asList("Benevolent", "Aggressive"));
		tournament_info.setMaxTurns(20);
		tournament_info.setNoOfGamesToPlay(2);
		Tournament tournament = game_manager.prepareTournamentGamePlay(tournament_info);
		while (!tournament.getStatus().equalsIgnoreCase("TOURNAMENT_OVER\n")) {
			tournament = game_manager.playTournamentMode(tournament);
			tournament_results = tournament.getTournament_results();
		}
		List<String> winner = new ArrayList<>();
		List<String> expected_winner = Arrays.asList("Draw", "Draw");
		tournament_results.getEach_map_results().forEach((map, results) -> {
			results.forEach(result -> {
				winner.add(result.getWinner());
			});
		});
		assertEquals(winner, expected_winner);
	}

}
