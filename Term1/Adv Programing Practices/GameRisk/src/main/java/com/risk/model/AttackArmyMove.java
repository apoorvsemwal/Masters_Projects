package com.risk.model;

/**
 * This model here represents the move of transferring armies once an attacker
 * captures some defender territory.
 * 
 * @author <a href="apoorv.semwal20@gmail.com">Apoorv Semwal</a>
 * @version 0.0.1
 */
public class AttackArmyMove {

	/**
	 * Territory from where to move the army
	 */
	private String attacker_territory;

	/**
	 * Territory to where to move the army
	 */
	private String defender_territory;

	/**
	 * Number of armies to be moved
	 */
	private int amry_count;

	/**
	 * @return the attacker_territory
	 */
	public String getAttacker_territory() {
		return attacker_territory;
	}

	/**
	 * @param attacker_territory the attacker_territory to set
	 */
	public void setAttacker_territory(String attacker_territory) {
		this.attacker_territory = attacker_territory;
	}

	/**
	 * @return the defender_territory
	 */
	public String getDefender_territory() {
		return defender_territory;
	}

	/**
	 * @param defender_territory the defender_territory to set
	 */
	public void setDefender_territory(String defender_territory) {
		this.defender_territory = defender_territory;
	}

	/**
	 * @return the amry_count
	 */
	public int getAmry_count() {
		return amry_count;
	}

	/**
	 * @param amry_count the amry_count to set
	 */
	public void setAmry_count(int amry_count) {
		this.amry_count = amry_count;
	}

}
