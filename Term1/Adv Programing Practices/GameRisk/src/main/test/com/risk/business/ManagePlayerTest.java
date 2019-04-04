/**
 * 
 */
package com.risk.business;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.risk.business.impl.ManageGamePlay;
import com.risk.business.impl.ManagePlayer;
import com.risk.model.Card;
import com.risk.model.CardTrade;
import com.risk.model.Fortification;
import com.risk.model.GamePlay;
import com.risk.model.GamePlayTerritory;
import com.risk.model.Player;
import com.risk.model.gui.PlayerDetails;
import com.risk.model.gui.SinglePlayer;

/**
 * @author <a href="mailto:himansipatel1994@gmail.com">Himansi Patel</a>
 * @author <a href="mailto:mayankjariwala1994@gmail.com">Mayank Jariwala</a>
 * @version 0.0.1
 */
public class ManagePlayerTest {

	private static IManagePlayer manage_player;
	private GamePlay game_play;

	@Before
	public void initManagePlayer() {
		game_play = new GamePlay();
		manage_player = new ManagePlayer();
	}


	/**
	 * This test performs check on creation of card which is equal to no. of
	 * territories in map (Inserting Free Cards to card stock) if map is valid and
	 * game is ready for players to play.
	 * 
	 * @author <a href="mailto:mayankjariwala1994@gmail.com">Mayank Jariwala</a>
	 *         Function description added by Mayank Jariwala
	 * @author <a href="mailto:himansipatel1994@gmail.com">Himansi Patel</a>
	 */
	@Test
	public void checkValidCardCreationTest() {

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

		List<GamePlayTerritory> map_territory_list = manage_player.getTerritories(game_play.getMap());
		int map_territory_list_size = map_territory_list.size();
		List<Card> free_card_list = game_play.getFree_cards();
		int free_card_list_size = free_card_list.size();
		assertEquals(map_territory_list_size, free_card_list_size);
	}

	/**
	 * This test performs validation of placing armies manually by player on their
	 * assign territories in round robin fashion during startup phase.
	 * 
	 * @author <a href="mailto:mayankjariwala1994@gmail.com">Mayank Jariwala</a>
	 *         Function description added by Mayank Jariwala
	 * @author <a href="mailto:himansipatel1994@gmail.com">Himansi Patel</a>
	 */
	@Test
	public void validateManuallyAssignArmyStock() {
		PlayerDetails      single_game_input = new PlayerDetails();
		single_game_input.setAllocationType("M");
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

		for (int i = 0; i < game_play.getGame_state().size(); i++) {
			assertEquals(40, game_play.getGame_state().get(i).getArmy_stock());
		}

	}

	/**
	 * This test performs validation of placing armies automatically on player
	 * assign territories in round robin fashion during startup phase.
	 * 
	 * @author <a href="mailto:mayankjariwala1994@gmail.com">Mayank Jariwala</a>
	 *         Function description added by Mayank Jariwala
	 * @author <a href="mailto:himansipatel1994@gmail.com">Himansi Patel</a>
	 */
	@Test
	public void validateAutomaticallyAssignArmyStock() {
		PlayerDetails      single_game_input = new PlayerDetails();
		single_game_input.setAllocationType("A");
		single_game_input.setFileName("Switzerland.map");
		List<SinglePlayer> players = new ArrayList<>();
		single_game_input.setPlayersNo(2);
		for (int i = 1; i <= 2; i++) {
			SinglePlayer player = new SinglePlayer();
			player.setId(Integer.toString(i));
			player.setType("Human");
			player.setBehaviour("Human");
			players.add(player);
		}
		single_game_input.setPlayers(players);
				
		game_play = manage_player.createPlayer(single_game_input);
		assertEquals(14, game_play.getGame_state().get(0).getTerritory_list().size());
		assertEquals(13, game_play.getGame_state().get(1).getTerritory_list().size());
	}

	/**
	 * This test checks for <i>Invalid</i> fortification move to player <b>
	 * Neighboring Territory </b>
	 * 
	 * @author <a href="mailto:mayankjariwala1994@gmail.com">Mayank Jariwala</a>
	 *         Function and Comments modification by Mayank Jariwala
	 * @author <a href="mailto:zinnia.rana.22@gmail.com">Zinnia Rana</a>
	 */
	@Test
	public void checkFortifyPhaseInvalidNeighbouringTerritoryTest() {
		Fortification fortify = new Fortification();

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
		
		fortify.setSource_territory("Fribourg");
		fortify.setDestination_territory("Leistal Canton");
		fortify.setArmy_count(2);
		game_play.setFortification(fortify);
		game_play.getGame_state().get(0).executeStrategy("FORTIFY", game_play);		
		String message = game_play.getStatus();
		assertEquals("Invalid Move (Not Neighboring Territory)", message);
	}

	/**
	 * This test checks for <i>Valid</i> fortification move to player <b>
	 * Neighboring Territory </b>
	 * 
	 * @author <a href="mailto:mayankjariwala1994@gmail.com">Mayank Jariwala</a>
	 *         Function and Comments modification by Mayank Jariwala
	 * @author <a href="mailto:zinnia.rana.22@gmail.com">Zinnia Rana</a>
	 */
	@Test
	public void checkFortifyPhaseValidNeighbouringTerritoryTest() {
		Fortification fortify = new Fortification();

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
		
		fortify.setSource_territory("Fribourg");
		fortify.setDestination_territory("Jura Canton");
		fortify.setArmy_count(2);
		game_play.setFortification(fortify);
		game_play.getGame_state().get(0).executeStrategy("FORTIFY", game_play);
		String message = game_play.getStatus();
		assertTrue(message.contains("move"));
	}

	/**
	 * Test to check if there is only 1 army on source territory and player want to
	 * move from source to destination then as per risk rule player is not allow to
	 * move armies.
	 * 
	 * 
	 * @author <a href="mailto:mayankjariwala1994@gmail.com">Mayank Jariwala</a>
	 */
	@Test
	public void checkFortifyInvalidFortificationArmyMoveTest() {
		Fortification fortify = new Fortification();
		fortify.setSource_territory("Fribourg");
		fortify.setDestination_territory("Jura Canton");
		fortify.setArmy_count(3);

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
		
		game_play.setFortification(fortify);
		game_play.getGame_state().get(0).executeStrategy("FORTIFY", game_play);
		String message = game_play.getStatus();
		assertTrue(message.contains("not having minimum armies"));
	}

	/**
	 * Test to check if there is more than one army on source territory and player
	 * want to move from source to destination then as per risk rule player is allow
	 * to move to few armies destination territory
	 * 
	 * 
	 * @author <a href="mailto:mayankjariwala1994@gmail.com">Mayank Jariwala</a>
	 */
	@Test
	public void checkFortifyValidFortificationArmyMoveTest() {
		Fortification fortify = new Fortification();

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
		
		fortify.setSource_territory("Fribourg");
		fortify.setDestination_territory("Jura Canton");
		fortify.setArmy_count(2);
		game_play.setFortification(fortify);
		game_play.getGame_state().get(0).executeStrategy("FORTIFY", game_play);
		String message = game_play.getStatus();
		assertTrue(message.contains("move"));
	}

	/**
	 * Test to check if automatic allocation in reinforcement phase is working fine.
	 * 
	 * @author <a href="mailto:a_semwal@encs.concordia.ca">ApoorvSemwal</a>
	 */
	@Test
	public void testReinforceAutoAllocate() {

		PlayerDetails      single_game_input = new PlayerDetails();
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

		game_play = manage_player.createPlayer(single_game_input);

		// 6 Players should have been created.
		assertEquals(6, game_play.getGame_state().size());

		// Player 1 should be set as the current player.
		assertEquals(1, game_play.getCurrent_player());

		// REINFORCEMENT Phase should be set a the current phase.
		assertEquals("REINFORCEMENT", game_play.getGame_phase());
	}

	/**
	 * Test to check if an Valid trade move is executed when all three cards have
	 * the same army image.
	 * 
	 * @author <a href="mailto:a_semwal@encs.concordia.ca">ApoorvSemwal</a>
	 */
	@Test
	public void testTradeCardsSameImage() {

		IManageGamePlay game_manager = new ManageGamePlay();
		IManagePlayer player_manager = new ManagePlayer();

		// Creating a game state using AutoAllocationMode - A.
		// Player 3 has an initial army count 4 and zero cards.
		GamePlay game_state = new GamePlay();

		PlayerDetails      single_game_input = new PlayerDetails();
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
		
		game_state = player_manager.createPlayer(single_game_input);

		// Setting Player 3 as current player
		game_state.setCurrent_player(3);

		// Preparing a CardTrade scenario and adding it game_state.
		CardTrade card_trade = new CardTrade();

		Card test_card_1 = new Card();
		test_card_1.setTerritory_name("Geneva");
		test_card_1.setArmy_type("Artillery");
		card_trade.setCard1(test_card_1);

		Card test_card_2 = new Card();
		test_card_2.setTerritory_name("Varduz");
		test_card_2.setArmy_type("Artillery");
		card_trade.setCard2(test_card_2);

		Card test_card_3 = new Card();
		test_card_3.setTerritory_name("Sarnen");
		test_card_3.setArmy_type("Artillery");
		card_trade.setCard3(test_card_3);

		game_state.setCard_trade(card_trade);

		// Setting the phase of game to TRADE_CARDS
		game_state.setGame_phase("TRADE_CARDS");

		// Setting the card list for player 3
		List<Card> card_list = new ArrayList<>();
		card_list.add(test_card_1);
		card_list.add(test_card_2);
		card_list.add(test_card_3);

		// Setting an empty free card list and current trade count for the player as 6.
		game_state.setFree_cards(new ArrayList<Card>());

		for (Player player : game_state.getGame_state()) {
			if (player.getId() != 3) {
				continue;
			} else {
				player.setCard_list(card_list);
				player.setTrade_count(6);
			}
		}

		/**
		 * Presently the list of free cards is empty and Player 3 has three cards of
		 * same image to be traded. So once the trade is over those three cards will be
		 * removed from Player's list and added to the free stock of cards. In addition
		 * to that player will receiving a specified set of armies based on risk rules.
		 * 
		 * Input Data Defined for this case: Current Player - 3 and has clicked on
		 * TRADE_CARD Player holds 3 cards of same image Number of trades player has
		 * already done - 6 Current free army stock of Player 3 = 0 New army stock after
		 * trade should be = 0 + 20 = 20
		 */
		game_manager.managePhase(game_state);

		// Three cards became available after trade.
		assertEquals(3, game_state.getFree_cards().size());

		/**
		 * Player's army stock becomes - 20 Player's card list becomes empty. Player's
		 * number of trades become - 7
		 */
		for (Player player : game_state.getGame_state()) {
			if (player.getId() != 3) {
				continue;
			} else {
				assertEquals(7, player.getTrade_count());
				assertEquals(0, player.getCard_list().size());
				assertEquals(20, player.getArmy_stock());
				break;
			}
		}
	}

	/**
	 * Test to check if an Valid trade move is executed when all three cards have a
	 * different army image.
	 * 
	 * @author <a href="mailto:a_semwal@encs.concordia.ca">ApoorvSemwal</a>
	 */
	@Test
	public void testTradeCardsDiffImage() {
		IManageGamePlay game_manager = new ManageGamePlay();
		IManagePlayer player_manager = new ManagePlayer();

		// Creating a game state using AutoAllocationMode - A.
		// Player 3 has an initial army count 4 and zero cards.
		GamePlay game_state = new GamePlay();
		PlayerDetails      single_game_input = new PlayerDetails();
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

		game_state = player_manager.createPlayer(single_game_input);
		
		// Setting Player 3 as current player
		game_state.setCurrent_player(3);

		// Preparing a CardTrade scenario and adding it game_state.
		CardTrade card_trade = new CardTrade();

		Card test_card_1 = new Card();
		test_card_1.setTerritory_name("Geneva");
		test_card_1.setArmy_type("Artillery");
		card_trade.setCard1(test_card_1);

		Card test_card_2 = new Card();
		test_card_2.setTerritory_name("Stans");
		test_card_2.setArmy_type("Infantry");
		card_trade.setCard2(test_card_2);

		Card test_card_3 = new Card();
		test_card_3.setTerritory_name("Solothum");
		test_card_3.setArmy_type("Cavalry");
		card_trade.setCard3(test_card_3);

		game_state.setCard_trade(card_trade);

		// Setting the phase of game to TRADE_CARDS
		game_state.setGame_phase("TRADE_CARDS");

		// Setting the card list for player 3
		List<Card> card_list = new ArrayList<>();
		card_list.add(test_card_1);
		card_list.add(test_card_2);
		card_list.add(test_card_3);

		// Setting an empty free card list and current trade count for the player as 4.
		game_state.setFree_cards(new ArrayList<Card>());

		for (Player player : game_state.getGame_state()) {
			if (player.getId() != 3) {
				continue;
			} else {
				player.setCard_list(card_list);
				player.setTrade_count(4);
			}
		}

		/**
		 * Presently the list of free cards is empty and Player 3 has three cards of
		 * different image to be traded. So once the trade is over those three cards
		 * will be removed from Player's list and added to the free stock of cards. In
		 * addition to that player will be receiving a specified set of armies based on
		 * risk rules.
		 * 
		 * Input Data Defined for this case: Current Player - 3 and has clicked on
		 * TRADE_CARD Player holds 3 cards of different image Number of trades player
		 * has already done - 4 Current free army stock of Player 3 = 0 New army stock
		 * after trade should be = 0 + 12 = 12
		 */
		game_manager.managePhase(game_state);

		// Three cards became available after trade.
		assertEquals(3, game_state.getFree_cards().size());

		/**
		 * Player's army stock becomes - 12 Player's card list becomes empty. Player's
		 * number of trades become - 5
		 */
		for (Player player : game_state.getGame_state()) {
			if (player.getId() != 3) {
				continue;
			} else {
				assertEquals(5, player.getTrade_count());
				assertEquals(0, player.getCard_list().size());
				assertEquals(12, player.getArmy_stock());
				break;
			}
		}
	}

	/**
	 * Test to check if an Invalid trade is detected. Either all three cards should
	 * have same army image or all three should have a different one.
	 * 
	 * @author <a href="mailto:a_semwal@encs.concordia.ca">ApoorvSemwal</a>
	 */
	@Test
	public void testTradeCardsInvalidTrade() {
		IManageGamePlay game_manager = new ManageGamePlay();
		IManagePlayer player_manager = new ManagePlayer();

		// Creating a game state using AutoAllocationMode - A.
		// Player 4 has an initial army count 3 and zero cards.
		GamePlay game_state = new GamePlay();
		PlayerDetails      single_game_input = new PlayerDetails();
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
		
		game_state = player_manager.createPlayer(single_game_input);

		// Setting Player 4 as current player
		game_state.setCurrent_player(4);

		// Preparing a CardTrade scenario and adding it game_state.
		CardTrade card_trade = new CardTrade();

		Card test_card_1 = new Card();
		test_card_1.setTerritory_name("Geneva");
		test_card_1.setArmy_type("Artillery");
		card_trade.setCard1(test_card_1);

		Card test_card_2 = new Card();
		test_card_2.setTerritory_name("Zug Zug");
		test_card_2.setArmy_type("Artillery");
		card_trade.setCard2(test_card_2);

		Card test_card_3 = new Card();
		test_card_3.setTerritory_name("Solothum");
		test_card_3.setArmy_type("Cavalry");
		card_trade.setCard3(test_card_3);

		game_state.setCard_trade(card_trade);

		// Setting the phase of game to TRADE_CARDS
		game_state.setGame_phase("TRADE_CARDS");

		// Setting the card list for player 3
		List<Card> card_list = new ArrayList<>();
		card_list.add(test_card_1);
		card_list.add(test_card_2);
		card_list.add(test_card_3);

		// Setting an empty free card list and current trade count for the player as 2.
		game_state.setFree_cards(new ArrayList<Card>());

		for (Player player : game_state.getGame_state()) {
			if (player.getId() != 4) {
				continue;
			} else {
				player.setCard_list(card_list);
				player.setTrade_count(2);
			}
		}

		/**
		 * Presently the list of free cards is empty and Player 4 has two cards of same
		 * image and one different to be traded. So once the trade is over those three
		 * cards will still be there in Player's list and no new addition to free stock
		 * of cards. In addition to that player will be receiving a specified set of
		 * armies based on risk rules.
		 * 
		 * Input Data Defined for this case: Current Player - 4 and has clicked on
		 * TRADE_CARD Player holds 2 cards of same image and 1 different Number of
		 * trades player has already done - 2 Current free army stock of Player 3 = 0
		 * New army stock after trade should be = 0 + 0 = 0
		 * 
		 * No new armies as per risk rules with a proper status message.
		 * 
		 */
		game_manager.managePhase(game_state);

		// No new cards in free cards list.
		assertEquals(0, game_state.getFree_cards().size());

		/**
		 * Player's army stock stays - 0 Player's card list unchanged. Player's number
		 * of trades unchanged - 2
		 */
		for (Player player : game_state.getGame_state()) {
			if (player.getId() != 4) {
				continue;
			} else {
				assertEquals(2, player.getTrade_count());
				assertEquals(3, player.getCard_list().size());
				assertEquals(0, player.getArmy_stock());
				assertEquals("Either all three cards should have same image or all three different.\n",
						game_state.getStatus());
				break;
			}
		}
	}

	/**
	 * Test to check if trading works fine when Player holds a card for one of his
	 * captured territories.
	 * 
	 * @author <a href="mailto:a_semwal@encs.concordia.ca">ApoorvSemwal</a>
	 */
	@Test
	public void testTradeCardsAdditionalArmies() {

		IManageGamePlay game_manager = new ManageGamePlay();
		IManagePlayer player_manager = new ManagePlayer();

		// Creating a game state using AutoAllocationMode - A.
		// With 6 players playing initially each gets an army count - an zero cards.
		GamePlay game_state = new GamePlay();
		PlayerDetails      single_game_input = new PlayerDetails();
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
		
		game_state = player_manager.createPlayer(single_game_input);

		// Setting Player 3 as current player
		game_state.setCurrent_player(3);

		// Preparing a CardTrade scenario and adding it game_state.
		CardTrade card_trade = new CardTrade();

		Card test_card_1 = new Card();
		test_card_1.setTerritory_name("Geneva");
		test_card_1.setArmy_type("Artillery");
		card_trade.setCard1(test_card_1);

		Card test_card_2 = new Card();
		test_card_2.setTerritory_name("Varduz");
		test_card_2.setArmy_type("Artillery");
		card_trade.setCard2(test_card_2);

		Card test_card_3 = new Card();
		test_card_3.setTerritory_name("Sarnen");
		test_card_3.setArmy_type("Artillery");
		card_trade.setCard3(test_card_3);

		game_state.setCard_trade(card_trade);

		// Setting the phase of game to TRADE_CARDS
		game_state.setGame_phase("TRADE_CARDS");

		// Setting the card list for player 3
		List<Card> card_list = new ArrayList<>();
		card_list.add(test_card_1);
		card_list.add(test_card_2);
		card_list.add(test_card_3);

		// Setting an empty free card list and current trade count for the player as 5.
		// Player should hold Geneva as a territory and he already holds a card with
		// Geneva.
		game_state.setFree_cards(new ArrayList<Card>());

		for (Player player : game_state.getGame_state()) {
			if (player.getId() != 3) {
				continue;
			} else {
				player.setCard_list(card_list);
				player.setTrade_count(5);
			}
		}

		/**
		 * Presently the list of free cards is empty and Player 3 has three cards of
		 * same image to be traded. So once the trade is over those three cards will be
		 * removed from Player's list and added to the free stock of cards. In addition
		 * to that player will receiving a specified set of armies based on risk rules.
		 * 
		 * Input Data Defined for this case: Current Player - 3 and has clicked on
		 * TRADE_CARD Player holds 3 cards of same image Number of trades player has
		 * already done - 5 Current free army stock of Player 3 = 0 New army stock after
		 * trade should be = 0 + 15 = 15 Player even holds a territory of same name
		 * which is present in one his cards i.e. Geneva - So player will get an extra
		 * bonus of 2 Armies on Geneva. Before trade he had 4 armies on Geneva after
		 * trade it should be 6.
		 * 
		 */
		game_manager.managePhase(game_state);

		// Three cards became available after trade.
		assertEquals(game_state.getFree_cards().size(), 3);

		/**
		 * Player's army stock becomes - 15 Player's card list becomes empty. Player's
		 * number of trades become - 6 Player's army count on Geneva Should become - 6
		 */
		for (Player player : game_state.getGame_state()) {
			if (player.getId() != 3) {
				continue;
			} else {
				assertEquals(6, player.getTrade_count());
				assertEquals(0, player.getCard_list().size());
				assertEquals(15, player.getArmy_stock());
				for (GamePlayTerritory territory : player.getTerritory_list()) {
					if (territory.getTerritory_name().equalsIgnoreCase("Geneva")) {
						assertEquals(6, territory.getNumber_of_armies());
						break;
					} else {
						continue;
					}
				}
				break;
			}
		}
	}
}
