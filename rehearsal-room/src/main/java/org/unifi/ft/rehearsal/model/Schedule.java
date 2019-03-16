package org.unifi.ft.rehearsal.model;

import java.math.BigInteger;

import org.joda.time.DateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("Schedule")
public class Schedule {

	@Id
	private BigInteger id;
	private String band;
	private DateTime startDate;
	private DateTime endDate;
	private RehearsalRoom room;
	
	public Schedule(String band, DateTime startDate, DateTime endDate, RehearsalRoom room) {
		this.band = band;
		this.startDate = startDate;
		this.endDate = endDate;
		this.room = room;
	}

	public BigInteger getId() {
		return this.id;
	}
	
	public void setId(BigInteger id) {
		this.id = id;
	}
	
	public String getBand() {
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
	
	@Override
	public String toString() {
		return this.band + " scheduled for room: " + this.room + " on date: "+ this.startDate.toString("yyyy/MM/dd - HH:mm");
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((band == null) ? 0 : band.hashCode());
		result = prime * result + ((endDate == null) ? 0 : endDate.hashCode());
		result = prime * result + ((room == null) ? 0 : room.hashCode());
		result = prime * result + ((startDate == null) ? 0 : startDate.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Schedule other = (Schedule) obj;
		if (band == null) {
			if (other.band != null)
				return false;
		} else if (!band.equals(other.band))
			return false;
		if (endDate == null) {
			if (other.endDate != null)
				return false;
		} else if (!endDate.equals(other.endDate))
			return false;
		if (room != other.room)
			return false;
		if (startDate == null) {
			if (other.startDate != null)
				return false;
		} else if (!startDate.equals(other.startDate))
			return false;
		return true;
	}
	
}
