package com.abc.foaled.DatabaseDescription;

/**
 * Created by Brendan on 13/01/2017.
 *
 */

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "children")
public class ChildrenTable {

    @DatabaseField(generatedId = true)
    int id;                             //ID
    @DatabaseField(foreign = true)
    HorseTable horse_id;                //parent horse id
    @DatabaseField(foreign = true)
    HorseTable child_id;                //Child horse id



    public ChildrenTable(HorseTable horse_id, HorseTable child_id) {
        this.horse_id = horse_id;
        this.child_id = child_id;
    }

    public ChildrenTable() {
        this.horse_id = null;
        this.child_id = null;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("id=").append(id);
        sb.append(", ").append("horse_id=").append(horse_id);
        sb.append(", ").append("child_id=").append(child_id);
        return sb.toString();
    }

}