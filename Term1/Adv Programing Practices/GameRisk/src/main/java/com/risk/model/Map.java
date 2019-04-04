package com.risk.model;

import java.util.HashMap;

/**
 * Model Class for Map.
 * 
 * @author <a href="mailto:a_semwal@encs.concordia.ca">ApoorvSemwal</a>
 * @version 0.0.1
 */

public class Map {

	/**
	 * Map is represented by a collection of continents.
	 */
	private HashMap<String, Continent> continents;

	/**
	 * Contains the status message for validity of a map.
	 */
	private String status;

	/**
	 * @return the continents
	 */
	public HashMap<String, Continent> getContinents() {
		return continents;
	}

	/**
	 * @param continents the continents to set
	 */
	public void setContinents(HashMap<String, Continent> continents) {
		this.continents = continents;
	}

	/**
	 * @return the status message for map validity
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param status the status message for map validity to be set
	 */
	public void setStatus(String status) {
		this.status = status;
	}

}
