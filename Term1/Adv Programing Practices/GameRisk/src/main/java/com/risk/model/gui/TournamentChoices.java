package com.risk.model.gui;

import java.util.List;

public class TournamentChoices {
	List<String> mapNames;
	List<String> multipleStrategies;
	int noOfGamesToPlay;
	int maxTurns;

	public List<String> getMapNames() {
		return mapNames;
	}

	public void setMapNames(List<String> mapNames) {
		this.mapNames = mapNames;
	}

	public List<String> getMultipleStrategies() {
		return multipleStrategies;
	}

	public void setMultipleStrategies(List<String> multipleStrategies) {
		this.multipleStrategies = multipleStrategies;
	}

	public int getNoOfGamesToPlay() {
		return noOfGamesToPlay;
	}

	public void setNoOfGamesToPlay(int noOfGamesToPlay) {
		this.noOfGamesToPlay = noOfGamesToPlay;
	}

	public int getMaxTurns() {
		return maxTurns;
	}

	public void setMaxTurns(int maxTurns) {
		this.maxTurns = maxTurns;
	}

}
