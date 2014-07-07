package co.touchlab.droidconandroid.data;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by kgalligan on 6/28/14.
 */
@DatabaseTable
public class Event
{
    @DatabaseField(id = true)
    public long id;

    @DatabaseField
    public String name;

    @DatabaseField
    public String description;

    @DatabaseField(foreign = true, canBeNull = false, foreignAutoRefresh = true)
    public Venue venue;

    @DatabaseField
    public Long startDate;

    @DatabaseField
    public Long endDate;

    @DatabaseField
    public boolean publicEvent;

    @DatabaseField
    public Integer rsvpLimit;

    @DatabaseField
    public Integer rsvpCount;

    @DatabaseField
    public String rsvpUuid;

}
