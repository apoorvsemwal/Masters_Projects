package com.risk.business;

import java.util.List;

import com.risk.model.GamePlay;
import com.risk.model.GamePlayTerritory;
import com.risk.model.Map;
import com.risk.model.gui.PlayerDetails;

/**
 * This interface is responsible for handling all player related Utility
 * functionalities.
 * 
 * @author <a href="mailto:himansipatel1994@gmail.com">Himansi Patel</a>
 * @version 0.0.1
 */
public interface IManagePlayer {

	/**
	 * This method is an abstraction for the process of retrieving A Player Object
	 * 
	 * @author <a href="mailto:himansipatel1994@gmail.com">Himansi Patel</a>
	 * @param single_game_input Inputs for Single Game Mode
	 * @return Newly created Game Play object holding the entire Game State
	 */
	GamePlay createPlayer(PlayerDetails single_game_input);

	/**
	 * This method is randomly linking territories to game play territory object
	 * 
	 * @author <a href="mailto:himansipatel1994@gmail.com">Himansi Patel</a>
	 * @param map Map Object for retrieving Territories.
	 * @return List of territories
	 */
	List<GamePlayTerritory> getTerritories(Map map);
}
