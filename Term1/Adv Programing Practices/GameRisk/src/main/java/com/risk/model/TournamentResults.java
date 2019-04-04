package com.risk.model;

import java.util.Map;
import java.util.List;

/**
 * This class represents tournament results for each game played on each map by
 * different computer players
 * 
 * @author <a href="mayankjariwala1994@gmail.com">Mayank Jariwala</a>
 * @author <a href="apoorvsemwal20@gmail.com">Apoorv Semwal</a>
 * @version 0.0.3
 */
public class TournamentResults {

	// Map of total game played on each map
	Map<String, List<GamePlay>> each_map_results;

	/**
	 * @return the each_map_results
	 */
	public Map<String, List<GamePlay>> getEach_map_results() {
		return each_map_results;
	}

	/**
	 * @param each_map_results the each_map_results to set
	 */
	public void setEach_map_results(Map<String, List<GamePlay>> each_map_results) {
		this.each_map_results = each_map_results;
	}

}
