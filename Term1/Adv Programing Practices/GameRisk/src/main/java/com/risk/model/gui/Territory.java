package com.risk.model.gui;

/**
 * Model Class for Territory.
 * 
 * @author <a href="mailto:l_grew@encs.concordia.ca">LoveshantGrewal</a>
 * @version 0.0.1
 */
public class Territory {
	String name;
	String continentName;
	String neighbours;

	public Territory(String name, String continentName, String neighbours) {
		super();
		this.name = name;
		this.continentName = continentName;
		this.neighbours = neighbours;
	}

	public Territory() {
		super();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getContinentName() {
		return continentName;
	}

	public void setContinentName(String continentName) {
		this.continentName = continentName;
	}

	public String getNeighbours() {
		return neighbours;
	}

	public void setNeighbours(String neighbours) {
		this.neighbours = neighbours;
	}

}
