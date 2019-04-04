package com.risk.business;

import com.risk.model.GamePlay;

/**
 * This Interface defines a Strategy in accordance with Strategy Design Pattern.
 * Every concrete implementation of it will be a class representing a specific 
 * strategy to be implemented.  
 * In our Game we have Human, Aggressive, Benevolent, Random and Cheater as specific 
 * Strategies.
 * Core methods in this interface which will vary as per different strategies are: 
 * reinforce, attack and fortify.
 *  
 * @author <a href="mailto:a_semwal@encs.concordia.ca">ApoorvSemwal</a>
 * @version 0.0.1
 */
public interface IStrategy {

	/**
	 * This method is called to execute reinforcement phase for a particular strategy
	 * and updates the state of the game accordingly.
	 * It also handles the trading of cards during game play.
	 *  
	 * @author <a href="mailto:a_semwal@encs.concordia.ca">ApoorvSemwal</a>
	 * @param game_play state of the game i.e. entire game related info when
	 *                  reinforcement starts for a player.
	 * @return GamePlay updated state of the game after reinforcement phase ends.
	 */
	public abstract GamePlay reinforce(GamePlay game_play);

	/**
	 * This method is called to execute attack phase for a particular strategy and
	 * updates the state of the game accordingly.
     *
	 * It will have the functionality for attack which initially check for
	 * each attack validation based on which it perform a valid attack move involving
	 * actions like populating message, increment/decrement armies from respective
	 * player and last if attacker occupy defender territory then add defender
	 * territory to attacker territory list and remove from defender territory list
 
	 * @author <a href="mailto:mayankjariwala1994@gmail.com">MayankJariwala</a>
	 * @param game_play state of the game i.e. entire game related info when attack
	 *                  starts for a player.
	 * @return GamePlay updated state of the game after attack phase ends.
	 */	
	public abstract GamePlay attack(GamePlay game_play);

	/**
	 * This method initially checks for each fortification validation based on which
	 * it executes a valid fortify move involving certain actions like populating messages,
	 * increment/decrement armies from respective player territories.
	 * 
	 * @author <a href="mailto:zinnia.rana.22@gmail.com">Zinnia Rana</a>
	 * @param game_play : GamePlay Object
	 * @return GamePlay updated state of the game after fortification phase.
	 */
	public abstract GamePlay fortify(GamePlay game_play);

}
