package com.abc.foaled.Models;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import org.joda.time.DateTime;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Brendan on 6/02/2017.
 *
 */
@DatabaseTable(tableName = "births")
public class Births {

    public static String MARE_COLUMN_NAME = "mare";

    @DatabaseField(generatedId = true)
    private int id;
    @DatabaseField()
    public Integer horse;        //Foreign horse id //TODO make these ints, not foreign objects (too hard)
    @DatabaseField(canBeNull = true)
    private Integer sire;
    @DatabaseField()
    public Integer mare;

    @DatabaseField(dataType = DataType.DATE_STRING)
    public Date est_conception;

    @DatabaseField(dataType = DataType.DATE_TIME)
    public DateTime birth_time;

    @DatabaseField
    public String notes;

    public Births() {
        this.horse = null;
        this.sire = null;
        this.mare = null;
        this.est_conception = null;
        this.birth_time = new DateTime();
        this.notes = null;
    }


    /**
     * @return A string representation of the row in the database
     * TODO build this to be proper
     */
/*    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("id=").append(id);
        sb.append(", ").append("name=").append(name);
        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MMM/yyyy", Locale.US);
        sb.append(", ").append("sex=").append(sex);
        return sb.toString();
    }*/
}
