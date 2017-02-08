package com.abc.foaled.DatabaseTables;

import com.abc.foaled.R;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by Brendan on 29/12/16.
 *
 */

@DatabaseTable(tableName = "horse")
public class Horse {

    @DatabaseField(generatedId = true)
    private int id;                             //ID
    @DatabaseField
    public String name;                        //NAME
    @DatabaseField(canBeNull = false, foreign = true)
    public Births birth = new Births(this);
    @DatabaseField
    private boolean sex;                       //SEX
    @DatabaseField
    private String markings;
    @DatabaseField
    private String notes;

    //TODO need to update database to include photo location OR actual photo
    public int photo;

    public Horse() {
        this.name = null;
        this.birth = new Births(this);
        this.markings = null;
        this.notes = null;
        this.sex = false;
        this.photo = R.drawable.christie;
    }

    /**
     * @return A string representation of the row in the database
     * TODO build this to be proper again
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("id=").append(id);
        sb.append(", ").append("name=").append(name);
        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MMM/yyyy", Locale.US);
        sb.append(", ").append("sex=").append(sex);
        sb.append(", ").append("photo=").append(photo+"");
        return sb.toString();
    }

    //TODO return method for age (done through birth dob field)

}