/**
 * 
 */
package com.rankfps.bean;

import java.util.Date;
import java.util.List;

/**
 * @author Felipe
 *
 */
public class Match {
	private Long id;
	private Date begin;
	private Date end;
	private List<Player> players;
	
	public Match(Long id, Date begin, List<Player> players) {
		super();
		this.id = id;
		this.begin = begin;
		this.players = players;
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public List<Player> getPlayers() {
		return players;
	}
	public void setPlayers(List<Player> players) {
		this.players = players;
	}

	public Date getBegin() {
		return begin;
	}

	public void setBegin(Date begin) {
		this.begin = begin;
	}

	public Date getEnd() {
		return end;
	}

	public void setEnd(Date end) {
		this.end = end;
	}
}
