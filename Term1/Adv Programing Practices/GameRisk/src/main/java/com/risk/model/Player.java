package com.risk.model;

import java.util.List;

import com.risk.business.IStrategy;

/**
 * This Player Model represents a Player during our GamePlay. In terms of
 * Strategy design Pattern this is our Context Class having an attribute for
 * Strategy *
 * 
 * @author <a href="mailto:himansipatel1994@gmail.com">Himansi Patel</a>
 * @author <a href="mailto:mayankjariwala1994@gmail.com">Mayank Jariwala</a> -
 *         Added Model Description
 * @version 0.0.1
 */
public class Player {

	private int id;
	private String name;
	private int army_stock;
	private List<GamePlayTerritory> territory_list;
	private List<Card> card_list;

	/**
	 * During Attack Phase If Player is occupying territory then flag will set to
	 * true and will get one card as per risk rule at the end of attack.
	 */
	private boolean any_territory_occupied;

	/**
	 * To identify how many card sets are traded for a particular Player.
	 */
	private int trade_count;

	/**
	 * To store the type of player - Human or Computer
	 */
	private String type;

	/**
	 * To store the kind of strategy a player holds.
	 */
	private String strategy_name;

	/**
	 * Represents a particular strategy a Player has.
	 */
	private IStrategy strategy;

	/**
	 * @param strategy Strategy object to set
	 */
	public void setStrategy(IStrategy strategy) {
		this.strategy = strategy;
	}

	/**
	 * This method is used to execute player's reinforce, attack, fortification
	 * method
	 * 
	 * @param method_name Like REINFORCE, ATTACK, FORTIFY
	 * @param game_play   GamePlay Object
	 * @return updated GamePlay object
	 */
	public GamePlay executeStrategy(String method_name, GamePlay game_play) {
		if (method_name.equalsIgnoreCase("REINFORCE")) {
			this.strategy.reinforce(game_play);
		} else if (method_name.equalsIgnoreCase("ATTACK")) {
			this.strategy.attack(game_play);
		} else if (method_name.equalsIgnoreCase("FORTIFY")) {
			this.strategy.fortify(game_play);
		}

		return game_play;
	}

	/**
	 * @return Type of Player (Human, Computer)
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type Player's type name (Computer Player or Human Player) to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return Strategy name of player (Aggressive,Random,Cheater,Benevolent)
	 */
	public String getStrategy_name() {
		return strategy_name;
	}

	/**
	 * @param strategy_name the strategy name of player to set
	 */
	public void setStrategy_name(String strategy_name) {
		this.strategy_name = strategy_name;
	}

	/**
	 * @return the any_territory_occupied
	 */
	public boolean isAny_territory_occupied() {
		return any_territory_occupied;
	}

	/**
	 * @param any_territory_occupied the any_territory_occupied to set
	 */
	public void setAny_territory_occupied(boolean any_territory_occupied) {
		this.any_territory_occupied = any_territory_occupied;
	}

	/**
	 * @return the trade_count
	 */
	public int getTrade_count() {
		return trade_count;
	}

	/**
	 * @param trade_count the trade_count to set
	 */
	public void setTrade_count(int trade_count) {
		this.trade_count = trade_count;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the army_stock
	 */
	public int getArmy_stock() {
		return army_stock;
	}

	/**
	 * @param army_stock the army_stock to set
	 */
	public void setArmy_stock(int army_stock) {
		this.army_stock = army_stock;
	}

	/**
	 * @return the territory_list
	 */
	public List<GamePlayTerritory> getTerritory_list() {
		return territory_list;
	}

	/**
	 * @param territory_list the territory_list to set
	 */
	public void setTerritory_list(List<GamePlayTerritory> territory_list) {
		this.territory_list = territory_list;
	}

	/**
	 * @return the card_list
	 */
	public List<Card> getCard_list() {
		return card_list;
	}

	/**
	 * @param card_list the card_list to set
	 */
	public void setCard_list(List<Card> card_list) {
		this.card_list = card_list;
	}

}
