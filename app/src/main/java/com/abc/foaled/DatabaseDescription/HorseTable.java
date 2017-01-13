package com.abc.foaled.DatabaseDescription;

/**
 * Created by PC on 13/01/2017.
 */

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "horse")

public class HorseTable {

    @DatabaseField(generatedId = true)
    int id;
    @DatabaseField
    Date date;
    @DatabaseField(index = true)
    String string;
    @DatabaseField
    long millis;
    @DatabaseField
    boolean even;



    public HorseTable(long millis) {
        this.date = new Date(millis);
        this.string = (millis % 1000) + "ms";
        this.millis = millis;
        this.even = ((millis % 2) == 0);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("id=").append(id);
        sb.append(", ").append("str=").append(string);
        sb.append(", ").append("ms=").append(millis);
        SimpleDateFormat dateFormatter = new SimpleDateFormat("MM/dd HH:mm:ss", Locale.US);
        sb.append(", ").append("date=").append(dateFormatter.format(date));
        sb.append(", ").append("even=").append(even);
        return sb.toString();
    }

}
