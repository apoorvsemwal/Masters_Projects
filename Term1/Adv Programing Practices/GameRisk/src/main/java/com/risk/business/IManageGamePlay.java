package com.risk.business;

import java.util.List;

import com.risk.model.GamePlay;
import com.risk.model.Map;
import com.risk.model.Player;
import com.risk.model.Tournament;
import com.risk.model.gui.TournamentChoices;

/**
 * This Interface handles the GamePlay as per RiskRules and manages game state
 * flow between UI and Data Access Layer.
 * 
 * @author <a href="mailto:a_semwal@encs.concordia.ca">ApoorvSemwal</a>
 * @version 0.0.1
 */
public interface IManageGamePlay {

	/**
	 * This method here acts as our Game Manager method. This method decides the
	 * entire flow of the game. Updates the phase of the game as per inputs received
	 * from GUI and returns a new Game State back to GUI. It executes the action
	 * corresponding the given phase, update the state of the game and decides the
	 * upcoming phases as per risk rules.
	 * 
	 * @author <a href="mailto:a_semwal@encs.concordia.ca">ApoorvSemwal</a>
	 * @param game_state State of Game during any phase
	 * @return state of the game updated so far.
	 * 
	 */
	GamePlay managePhase(GamePlay game_state);

	/**
	 * This method prepares a GamePlay Object for Tournament Mode. It then initiates
	 * the Tournament.
	 * 
	 * @param tournament_inp Inputs from UI to prepare a tournament.
	 * @return Tournament containing the current as well as the set of pending and
	 *         executed GamePlays.
	 */
	Tournament prepareTournamentGamePlay(TournamentChoices tournament_inp);

	/**
	 * This method is responsible for managing the flow of Game in Tournament Mode.
	 * 
	 * @param tournament Tournament containing the current as well as the set of
	 *                   pending and executed GamePlays.
	 * @return Tournament with updates in the currently active GamePlay.
	 */
	Tournament playTournamentMode(Tournament tournament);

	/**
	 * This method calculates the number of Armies to be allocated to each player at
	 * the start of their Reinforcement Phase, based on Risk Rules . Rule 1 : Number
	 * of armies = max(floor(Total captured Territories / 3),3) Rule 2 : Number of
	 * extra armies added = continent score if Player has captured the entire
	 * continent.
	 * 
	 * @param gameplay       Current state of entire Game.
	 * @param map            Current map on which game is being played.
	 * @param current_player Current player for which Armies are to be
	 *                       re-calculated.
	 * @return State of the game in form of a List of Players.
	 */
	List<Player> calculateArmiesReinforce(List<Player> gameplay, Map map, int current_player);

	/**
	 * This method retrieves a list of saved GamePlay Files from Resource Folder.
	 * User then selects a GamePlay file from the given list.
	 * 
	 * @author <a href="mailto:a_semwal@encs.concordia.ca">ApoorvSemwal</a>
	 * @return List of existing GamePlay Files from Resource Folder.
	 */
	List<String> fetchGamePlays();

	/**
	 * This method reads a selected GamePlay File, parses it and the converts it to
	 * a GamePlay Object while loading a GamePlay.
	 * 
	 * @author <a href="mailto:a_semwal@encs.concordia.ca">ApoorvSemwal</a>
	 * @param file File Name of the currently selected GamePlay file.
	 * @return GamePlay Object representing the GamePlay to be loaded.
	 */
	GamePlay fetchGamePlay(String file);

}