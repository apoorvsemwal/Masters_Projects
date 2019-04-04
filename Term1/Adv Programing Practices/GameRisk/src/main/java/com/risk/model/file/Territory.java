package com.risk.model.file;

import java.util.ArrayList;
import java.util.List;

/**
 * This is an Model Class for Territory.
 * 
 * @author <a href="mailto:mayankjariwala1994@gmail.com">MayankJariwala</a>
 * @version 0.0.1
 */
public class Territory {

	private String name;
	private int x_coordinate;
	private int y_coordinate;

	/**
	 * @part_of_continent Territory belongs to Continent
	 */
	private String part_of_continent;
	private List<String> adj_territories;

	public Territory() {
		adj_territories = new ArrayList<String>();
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name : the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the x_coordinate
	 */
	public int getX_coordinate() {
		return x_coordinate;
	}

	/**
	 * @param x_coordinate : the x_coordinate to set
	 */
	public void setX_coordinate(int x_coordinate) {
		this.x_coordinate = x_coordinate;
	}

	/**
	 * @return the y_coordinate
	 */
	public int getY_coordinate() {
		return y_coordinate;
	}

	/**
	 * @param y_coordinate : the y_coordinate to set
	 */
	public void setY_coordinate(int y_coordinate) {
		this.y_coordinate = y_coordinate;
	}

	/**
	 * @return the part_of_continent
	 */
	public String getPart_of_continent() {
		return part_of_continent;
	}

	/**
	 * @param part_of_continent : the part_of_continent to set
	 */
	public void setPart_of_continent(String part_of_continent) {
		this.part_of_continent = part_of_continent;
	}

	/**
	 * @return the adj_territories
	 */
	public List<String> getAdj_territories() {
		return adj_territories;
	}

	/**
	 * @param adj_territories : the adj_territories to set
	 */
	public void setAdj_territories(List<String> adj_territories) {
		this.adj_territories = adj_territories;
	}

}
