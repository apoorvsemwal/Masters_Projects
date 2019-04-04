package com.risk.model;

/**
 * Model class for Card
 * 
 * @author <a href="mailto:zinnia.rana.22@gmail.com">Zinnia Rana</a>
 * @author <a href="mailto:mayankjariwala1994@gmail.com">Mayank Jariwala</a>
 *         Functions and comments added by Mayank Jariwala
 * @version 0.0.1
 */
public class Fortification {

	/**
	 * Territory from where to move the army
	 */
	private String source_territory;

	/**
	 * Territory to where to move the army
	 */
	private String destination_territory;

	/**
	 * Number of armies to be moved
	 */
	private int army_count;

	public Fortification() {
	}

	/**
	 * @return the source_territory
	 */
	public String getSource_territory() {
		return source_territory;
	}

	/**
	 * @param source_territory the source_territory to set
	 */
	public void setSource_territory(String source_territory) {
		this.source_territory = source_territory;
	}

	/**
	 * @return the destination_territory
	 */
	public String getDestination_territory() {
		return destination_territory;
	}

	/**
	 * @param destination_territory the destination_territory to set
	 */
	public void setDestination_territory(String destination_territory) {
		this.destination_territory = destination_territory;
	}

	/**
	 * @return the army_count
	 */
	public int getArmy_count() {
		return army_count;
	}

	/**
	 * @param army_count the army_count to set
	 */
	public void setArmy_count(int army_count) {
		this.army_count = army_count;
	}

}
