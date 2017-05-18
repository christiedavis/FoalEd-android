package com.abc.foaled.Models;

import com.abc.foaled.Helpers.DateTimeHelper;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import org.joda.time.DateTime;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Brendan on 6/02/2017.
 *
 */
@DatabaseTable(tableName = "births")
public class Birth implements Serializable {

    public static String MARE_COLUMN_NAME = "mare";

    @DatabaseField(generatedId = true)
    private int id;
    @DatabaseField()
    public int horse;        //Foreign horse id //TODO make these ints, not foreign objects (too hard)
    @DatabaseField(canBeNull = true)
    private int sire;
    @DatabaseField(foreign = true)
    public Horse mare;


    @DatabaseField(dataType = DataType.DATE_STRING)
    public Date est_conception;

    @DatabaseField(dataType = DataType.DATE_TIME)
    public DateTime birth_time;

    @DatabaseField
    public String notes;

    public String getYearOfBirth() {

        return Integer.toString(DateTimeHelper.getYear(est_conception));
    }

    public Birth() {
        this.horse = 0;
        this.sire = 0;
        this.mare = null;
        this.est_conception = null;
        this.birth_time = new DateTime();
        this.notes = null;
    }

    public Birth(Horse mother, int fatherID, Date conception){
        this.horse = 0;
        this.horse = 0;
        this.mare = mother;
        this.sire = fatherID;
        this.est_conception = conception;
        this.birth_time = null;
        this.notes = null;
    }
}
