package com.risk.business;

import com.risk.model.GamePlay;

public interface IManageDomination {

	/**
	 * This function is use to show updated domination view of each player in game
	 * i.e Total Armies player have in map,Map Coverage by Player
	 * 
	 * @author <a href="mailto:zinnia.rana.22@gmail.com">Zinnia Rana</a>
	 * @author <a href="mailto:mayankjariwala1994@gmail.com">Mayank Jariwala</a>
	 * @param game_play Current state of entire Game.
	 * @return GamePlay Updated Game State Object.
	 */
	GamePlay dominationView(GamePlay game_play);

}
