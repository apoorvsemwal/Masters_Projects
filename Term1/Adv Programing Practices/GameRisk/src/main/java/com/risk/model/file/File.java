package com.risk.model.file;

import java.util.List;

/**
 * This represents file as a whole and will be returned to our business layer.
 * 
 * @author <a href="mailto:a_semwal@encs.concordia.ca">ApoorvSemwal</a>
 * @version 0.0.1
 */

public class File {

	/**
	 * The three sections marked in a Map file i.e. [Map], [Continents],
	 * [Territories] have an exact equivalent here in form of objects.
	 */
	private Map map;
	private List<Continent> continents;
	private List<Territory> Territories;

	/**
	 * @return the map
	 */
	public Map getMap() {
		return map;
	}

	/**
	 * @param map the map to set
	 */
	public void setMap(Map map) {
		this.map = map;
	}

	/**
	 * @return the continents
	 */
	public List<Continent> getContinents() {
		return continents;
	}

	/**
	 * @param continents the continents to set
	 */
	public void setContinents(List<Continent> continents) {
		this.continents = continents;
	}

	/**
	 * @return the territories
	 */
	public List<Territory> getTerritories() {
		return Territories;
	}

	/**
	 * @param territories the territories to set
	 */
	public void setTerritories(List<Territory> territories) {
		Territories = territories;
	}
}
