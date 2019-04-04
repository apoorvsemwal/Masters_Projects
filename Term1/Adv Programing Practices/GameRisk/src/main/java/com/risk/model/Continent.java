package com.risk.model;

import java.util.List;

/**
 * Model class for Continent
 * 
 * @author <a href="mailto:a_semwal@encs.concordia.ca">ApoorvSemwal</a>
 * @version 0.0.1
 */
public class Continent {

	private String name;

	/**
	 * Number of armies received if you capture the entire continent.
	 */
	private int score;

	/**
	 * Continent represented as a collection of Territories
	 */
	private List<Territory> territories;

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
	 * @return the score
	 */
	public int getScore() {
		return score;
	}

	/**
	 * @param score the score to set
	 */
	public void setScore(int score) {
		this.score = score;
	}

	/**
	 * @return the territories
	 */
	public List<Territory> getTerritories() {
		return territories;
	}

	/**
	 * @param territories the territories to set
	 */
	public void setTerritories(List<Territory> territories) {
		this.territories = territories;
	}

}
