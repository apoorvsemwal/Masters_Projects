package com.risk.model.Strategy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.risk.business.IManagePlayer;
import com.risk.business.impl.ManagePlayer;
import com.risk.model.Attack;
import com.risk.model.AttackArmyMove;
import com.risk.model.GamePlay;
import com.risk.model.gui.PlayerDetails;
import com.risk.model.gui.SinglePlayer;

public class HumanTest {
	private static IManagePlayer manage_player;
	private GamePlay game_play;

	@Before
	public void initManagePlayer() {
		game_play = new GamePlay();
		manage_player = new ManagePlayer();
	}

	/**
	 * This test validate that if player is rolling no. of dice greater than no. of
	 * armies present on selected attacking territory then as per game rules player
	 * is not allow to attack on any neighboring territory
	 * 
	 * @author <a href="mailto:mayankjariwala1994@gmail.com">Mayank Jariwala</a>
	 *         Function description added by Mayank Jariwala
	 * @author <a href="mailto:himansipatel1994@gmail.com">Himansi Patel</a>
	 */
	@Test
	public void checkValidAttackTestDiceGreater() {
		Attack attack = new Attack();

		PlayerDetails single_game_input = new PlayerDetails();
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

		attack.setAttacker_territory("Neuchtel");
		attack.setDefender_territory("Varduz");
		attack.setAttacker_dice_no(6);
		attack.setDefender_dice_no(2);
		game_play.setAttack(attack);
		game_play.getGame_state().get(0).executeStrategy("ATTACK", game_play);
		String message = game_play.getStatus();
		assertTrue(containsInvalid(message));
	}

	/**
	 * This test validates that if player want to attack neighboring territory then
	 * the player is only allow to roll maximum number of dice by keeping one army
	 * on his territory.
	 * 
	 * @author <a href="mailto:mayankjariwala1994@gmail.com">Mayank Jariwala</a>
	 *         Function description added by Mayank Jariwala
	 * @author <a href="mailto:himansipatel1994@gmail.com">Himansi Patel</a>
	 */
	@Test
	public void checkValidAttackTestDiceLesser() {
		Attack attack = new Attack();

		PlayerDetails single_game_input = new PlayerDetails();
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

		attack.setAttacker_territory("Neuchtel");
		attack.setDefender_territory("Varduz");
		attack.setAttacker_dice_no(3);
		attack.setDefender_dice_no(2);
		game_play.setAttack(attack);
		game_play.getGame_state().get(0).executeStrategy("ATTACK", game_play);
		String message = game_play.getStatus();
		assertTrue(message.contains("Winner"));
	}

	/**
	 * This test checks for <i>Invalid</i> attack to defender only if defender
	 * territory is not neighboring territory of attacker or attacker is attacking
	 * on his own territory.
	 * 
	 * @author <a href="mailto:mayankjariwala1994@gmail.com">Mayank Jariwala</a>
	 *         Function and Comments modification by Mayank Jariwala
	 * @author <a href="himansipatel1994@gmail.com">Himansi Patel</a>
	 */
	@Test
	public void attackArmyMoveInvalidNeighbouringTest() {
		AttackArmyMove attack_army_move = new AttackArmyMove();

		PlayerDetails single_game_input = new PlayerDetails();
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

		attack_army_move.setAttacker_territory("Fribourg");
		attack_army_move.setDefender_territory("Leistal Canton");
		attack_army_move.setAmry_count(2);
		game_play.setArmy_move(attack_army_move);
		game_play.setGame_phase("ATTACK_ARMY_MOVE");
		game_play.getGame_state().get(0).executeStrategy("ATTACK", game_play);
		String message = game_play.getStatus();
		assertEquals("Invalid Move (Not Neighboring Territory)", message);
	}

	/**
	 * This test checks for <i>Valid</i> attack to defender only if defender
	 * territory is neighboring territory of attacker.
	 * 
	 * @author <a href="mailto:mayankjariwala1994@gmail.com">Mayank Jariwala</a>
	 *         Function and Comments modification by Mayank Jariwala
	 * @author <a href="himansipatel1994@gmail.com">Himansi Patel</a>
	 */
	@Test
	public void attackArmyMoveValidNeighbouringTest() {
		AttackArmyMove attack_army_move = new AttackArmyMove();

		PlayerDetails single_game_input = new PlayerDetails();
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

		attack_army_move.setAttacker_territory("Fribourg");
		attack_army_move.setDefender_territory("Jura Canton");
		attack_army_move.setAmry_count(2);
		game_play.setArmy_move(attack_army_move);
		game_play.setGame_phase("ATTACK_ARMY_MOVE");
		game_play.getGame_state().get(0).executeStrategy("ATTACK", game_play);
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
	 *         Functions comments added by Mayank Jariwala
	 * @author <a href="himansipatel1994@gmail.com">Himansi Patel</a>
	 */
	@Test
	public void checkInvalidAttackArmyMoveTest() {
		AttackArmyMove attack_army_move = new AttackArmyMove();

		PlayerDetails single_game_input = new PlayerDetails();
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

		attack_army_move.setAttacker_territory("Fribourg");
		attack_army_move.setDefender_territory("Jura Canton");
		attack_army_move.setAmry_count(3);
		game_play.setArmy_move(attack_army_move);
		game_play.setGame_phase("ATTACK_ARMY_MOVE");
		game_play.getGame_state().get(0).executeStrategy("ATTACK", game_play);
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
	 *         Functions comments added by Mayank Jariwala
	 * @author <a href="himansipatel1994@gmail.com">Himansi Patel</a>
	 */
	@Test
	public void checkValidAttackArmyMoveTest() {
		AttackArmyMove attack_army_move = new AttackArmyMove();

		PlayerDetails single_game_input = new PlayerDetails();
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

		attack_army_move.setAttacker_territory("Fribourg");
		attack_army_move.setDefender_territory("Jura Canton");
		attack_army_move.setAmry_count(2);
		game_play.setArmy_move(attack_army_move);
		game_play.setGame_phase("ATTACK_ARMY_MOVE");
		game_play.getGame_state().get(0).executeStrategy("ATTACK", game_play);
		String message = game_play.getStatus();
		assertTrue(message.contains("move"));
	}

	public boolean containsInvalid(String s) {
		return s.contains("Invalid");
	}

}
