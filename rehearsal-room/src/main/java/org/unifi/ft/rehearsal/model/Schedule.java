package org.unifi.ft.rehearsal.model;

import org.joda.time.DateTime;
import org.springframework.stereotype.Component;

@Component
public class Schedule {

	private Band band;
	private DateTime startDate;
	private DateTime endDate;
	private RehearsalRoom room;
	
	public Schedule(Band band, DateTime startDate, DateTime endDate, RehearsalRoom room) {
		this.band = band;
		this.startDate = startDate;
		this.endDate = endDate;
		this.room = room;
	}

	public Band getBand() {
		return this.band;
	}

	public DateTime getStartDate() {
		return this.startDate;
	}
	
	public DateTime getEndDate() {
		return this.endDate;
	}
	
	public RehearsalRoom getRoom() {
		return this.room;
	}
	
}
