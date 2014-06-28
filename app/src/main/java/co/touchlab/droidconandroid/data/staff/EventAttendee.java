package co.touchlab.droidconandroid.data.staff;

import co.touchlab.droidconandroid.data.Event;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

/**
 * Created by kgalligan on 6/28/14.
 */
@DatabaseTable
public class EventAttendee
{
    @DatabaseField(id = true)
    public Long id;

    @DatabaseField(foreign = true, canBeNull = false)
    public Event event;

    @DatabaseField(foreign = true, canBeNull = false)
    public UserAccount userAccount;

    @DatabaseField
    public Long startDate;

    @DatabaseField
    public Long endDate;

    @DatabaseField
    public String uuid;
}
