package com.risk.model.gui;

/**
 * Model Class for Continent.
 * 
 * @author <a href="mailto:l_grew@encs.concordia.ca">LoveshantGrewal</a>
 * @version 0.0.1
 */
public class Continent {

	String name;
	String score;

	public Continent() {
		super();
	}
	
	public Continent(String name, String score) {
		super();
		this.name = name;
		this.score = score;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getScore() {
		return score;
	}

	public void setScore(String score) {
		this.score = score;
	}

}
