package com.rankfps.bean;

import java.util.Date;
import java.util.List;

public class Player {
	private String nickname;
	private Integer streak;
	private List<Date> kills;
	private List<Date> deaths;
	private Gun betterGun;
	private List<Gun> usedGuns;
	
	
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public Integer getStreak() {
		return streak;
	}
	public void setStreak(Integer streak) {
		this.streak = streak;
	}
	public Gun getBetterGun() {
		return betterGun;
	}
	public void setBetterGun(Gun betterGun) {
		this.betterGun = betterGun;
	}
	public List<Gun> getUsedGuns() {
		return usedGuns;
	}
	public void setUsedGuns(List<Gun> usedGuns) {
		this.usedGuns = usedGuns;
	}
	public List<Date> getKills() {
		return kills;
	}
	public void setKills(List<Date> kills) {
		this.kills = kills;
	}
	public List<Date> getDeaths() {
		return deaths;
	}
	public void setDeaths(List<Date> deaths) {
		this.deaths = deaths;
	}
	
}
