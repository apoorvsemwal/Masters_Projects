package com.risk.model;

import java.util.List;

/**
 * This class represents a collection of Game Plays to be executed in tournament
 * mode.
 * 
 * @author <a href="apoorv.semwal20@gmail.com">Apoorv Semwal</a>
 * @version 0.0.3
 */

public class Tournament {

	/**
	 * List of GamePlay to be executed in tournament mode.
	 */
	private List<GamePlay> tournament;

	/**
	 * Represents the currently active GamePlay in tournament.
	 */
	private int current_game_play_id;

	/**
	 * Status message for Tournament. Contains messages from various validations
	 * during Tournament Creation and Execution.
	 */
	private String status;

	/**
	 * Maximum number of turns in any GamePlay
	 */
	private int max_turns;

	/**
	 * Result of the tournament
	 */
	private TournamentResults tournament_results;

	/**
	 * @return the tournament
	 */
	public List<GamePlay> getTournament() {
		return tournament;
	}

	/**
	 * @param tournament the tournament to set
	 */
	public void setTournament(List<GamePlay> tournament) {
		this.tournament = tournament;
	}

	/**
	 * @return the current_game_play_id
	 */
	public int getCurrent_game_play_id() {
		return current_game_play_id;
	}

	/**
	 * @param current_game_play_id the current_game_play_id to set
	 */
	public void setCurrent_game_play_id(int current_game_play_id) {
		this.current_game_play_id = current_game_play_id;
	}

	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * @return the max_turns
	 */
	public int getMax_turns() {
		return max_turns;
	}

	/**
	 * @param max_turns the max_turns to set
	 */
	public void setMax_turns(int max_turns) {
		this.max_turns = max_turns;
	}

	/**
	 * @return the tournament_results
	 */
	public TournamentResults getTournament_results() {
		return tournament_results;
	}

	/**
	 * @param tournament_results the tournament_results to set
	 */
	public void setTournament_results(TournamentResults tournament_results) {
		this.tournament_results = tournament_results;
	}
}