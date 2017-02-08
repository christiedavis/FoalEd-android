package com.abc.foaled.DatabaseTables;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Brendan on 6/02/2017.
 *
 */
@DatabaseTable(tableName = "births")
public class Births {

    @DatabaseField(generatedId = true)
    private int id;
    @DatabaseField(canBeNull = false, foreign = true)
    private Horse horse;        //Foreign horse id
    @DatabaseField(foreign = true)
    private Horse sire;
    @DatabaseField(foreign = true)
    private Horse mare;

    @DatabaseField(dataType = DataType.DATE_STRING)
    public Date est_conception;

    @DatabaseField(dataType = DataType.DATE_STRING)
    public Date birth_time;

    @DatabaseField
    public String notes;


    public Births(Horse horse) {
        this.horse = horse;
        this.sire = null;
        this.mare = null;
        this.est_conception = null;
        this.birth_time = Calendar.getInstance().getTime();
        this.notes = null;
    }

    public Births() {

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
