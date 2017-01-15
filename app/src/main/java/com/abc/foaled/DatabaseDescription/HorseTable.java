package com.abc.foaled.DatabaseDescription;

/**
 * Created by Brendan on 13/01/2017.
 *
 */

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "horse")
public class HorseTable {

    @DatabaseField(generatedId = true)
    int id;                             //ID
    @DatabaseField
    public String name;                        //NAME
    @DatabaseField(dataType = DataType.DATE_STRING)
    public Date dob;                           //DOB
    @DatabaseField
    public String colour;                        //COLOUR
    @DatabaseField
    public boolean sex;                       //SEX
    @DatabaseField
    public String photoLocation;



    public HorseTable(String name, Date dob, String colour, boolean sex) {
        this.name = name;
        this.dob = dob;
        this.colour = colour;
        this.sex = sex;
    }

    public HorseTable() {
        this.name = null;
        this.dob = null;
        this.colour = null;
        this.sex = false;
        this.photoLocation = null;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("id=").append(id);
        sb.append(", ").append("name=").append(name);
        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MMM/yyyy", Locale.US);
        sb.append(", ").append("date=").append(dateFormatter.format(dob));
        sb.append(", ").append("colour=").append(colour);
        sb.append(", ").append("sex=").append(sex);
        sb.append(", ").append("photoLocation=").append(photoLocation);
        return sb.toString();
    }

}
