package com.risk.model.gui;

import java.util.List;

public class PlayerDetails {
	private List<SinglePlayer> players;
	private int playersNo;
	private String fileName;
	private String allocationType;

	public int getPlayersNo() {
		return playersNo;
	}

	public void setPlayersNo(int playersNo) {
		this.playersNo = playersNo;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getAllocationType() {
		return allocationType;
	}

	public void setAllocationType(String allocationType) {
		this.allocationType = allocationType;
	}

	public List<SinglePlayer> getPlayers() {
		return players;
	}

	public void setPlayers(List<SinglePlayer> players) {
		this.players = players;
	}

}
