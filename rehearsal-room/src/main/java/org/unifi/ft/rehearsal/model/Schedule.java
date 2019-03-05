package org.unifi.ft.rehearsal.model;

import java.util.Date;

import org.springframework.stereotype.Component;

@Component
public class Schedule {

	private String bandName;
	private Date date;
	private RehearsalRooms room;
	
	public Schedule(String bandName, Date date, RehearsalRooms room) {
		this.bandName = bandName;
		this.date = date;
		this.room = room;
	}

	public String getBandName() {
		return bandName;
	}

	public Date getDate() {
		return date;
	}
	
	public RehearsalRooms getRoom() {
		return this.room;
	}
	
}
