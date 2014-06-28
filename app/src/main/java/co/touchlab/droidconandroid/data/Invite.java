package co.touchlab.droidconandroid.data;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by kgalligan on 6/28/14.
 */
@DatabaseTable
public class Invite
{
    @DatabaseField(id = true)
    public Long id;

    @DatabaseField(foreign = true, canBeNull = false)
    public Event event;

    @DatabaseField
    public String uuid;
}
