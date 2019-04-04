package com.risk.file;

import java.util.List;

import com.risk.model.GamePlay;

/**
 * Manage GamePlay Interface - It is use to save current game playing status to
 * file.
 * 
 * @author <a href="mailto:himansipatel1994@gmail.com">Himansi Patel</a>
 * @author <a href="mailto:mayankjariwala1994@gmail.com">Mayank Jariwala</a> -
 *         Added Interface comments
 * @version 0.0.1
 */
public interface IManageGamePlayFile {

	/**
	 * This function is use to write player data to new or existing Game Play file.
	 * 
	 * @author <a href="mailto:himansipatel1994@gmail.com">Himansi Patel</a>
	 * @param gamePlay game_state State of Game during any phase
	 * @return True - If file successfully written otherwise return false
	 */
	Boolean saveGameStateToDisk(GamePlay gamePlay);

	/**
	 * This function is use to load the state of ongoing and saved state of game.
	 * 
	 * @author <a href="mailto:himansipatel1994@gmail.com">Himansi Patel</a>
	 * @author <a href="mayankjariwala1994@gmail.com"> Mayank Jariwala </a>
	 * @param file_name The Playing Game File Name
	 * @return GamePlay : The current ongoing game state object
	 */
	GamePlay fetchGamePlay(String file_name);

	/**
	 * This method is used to load and display the list of available GamePlays from
	 * resource folder.
	 * 
	 * @author <a href="apoorv.semwal20@gmail.com">Apoorv Semwal</a>
	 * @return Name List of GamePlay Files.
	 */
	List<String> fetchGamePlayFilesFromResource();

}
