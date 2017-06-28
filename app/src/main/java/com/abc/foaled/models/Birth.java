package com.abc.foaled.models;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import org.joda.time.DateTime;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Brendan on 6/02/2017.
 *
 */
@DatabaseTable(tableName = "births")
public class Birth implements Serializable {


    @DatabaseField(generatedId = true)
    private int id;
    @DatabaseField(foreign = true)
    private Horse horse;        //Foreign horse
	@DatabaseField(foreign = true)
	private Horse mare;          //Foreign mother
    @DatabaseField
    private String sire;        //Name of dad
	@DatabaseField(canBeNull = false)
	private String notes;
    @DatabaseField(dataType = DataType.DATE_STRING)
    private Date estConception;
    @DatabaseField(dataType = DataType.DATE_TIME)
    private DateTime birthTime;


    public Birth() {
    }

    public Birth(Horse mother, String father, Date conception, DateTime dob){
	    horse = null;
        mare = mother;
        sire = father;
	    notes = "";
        estConception = conception;
        birthTime = dob;
    }

	public int getId() {
		return id;
	}

	public Horse getHorse() {
		return horse;
	}

	public void setHorse(Horse horse) {
		this.horse = horse;
	}

	public Horse getMare() {
		return mare;
	}

	public void setMare(Horse mare) {
		this.mare = mare;
	}

	public String getSire() {
		return sire;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public Date getEstConception() {
		return estConception;
	}

	public void setEstConception(Date estConception) {
		this.estConception = estConception;
	}

	public DateTime getBirthTime() {
		return birthTime;
	}

	public void setBirthTime(DateTime birth_time) {
		this.birthTime = birth_time;
	}

	public String getYearOfBirth() {
		return Integer.toString(birthTime.getYear());
	}
}
