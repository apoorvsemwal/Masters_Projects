package com.risk.model;

/**
 * Model class for Attack
 * 
 * @author <a href="mailto:himansipatel1994@gmail.com">Himansi Patel</a>
 * @author <a href="mailto:mayankjariwala1994@gmail.com">Mayank Jariwala</a>
 * @version 0.0.1
 */
public class Attack {

	private String attacker_territory;
	private String defender_territory;
	private int attacker_dice_no;
	private int defender_dice_no;

	public Attack() {
	}

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
	 * @return the attacker_dice_no
	 */
	public int getAttacker_dice_no() {
		return attacker_dice_no;
	}

	/**
	 * @param attacker_dice_no the attacker_dice_no to set
	 */
	public void setAttacker_dice_no(int attacker_dice_no) {
		this.attacker_dice_no = attacker_dice_no;
	}

	/**
	 * @return the defender_dice_no
	 */
	public int getDefender_dice_no() {
		return defender_dice_no;
	}

	/**
	 * @param defender_dice_no the defender_dice_no to set
	 */
	public void setDefender_dice_no(int defender_dice_no) {
		this.defender_dice_no = defender_dice_no;
	}

}
