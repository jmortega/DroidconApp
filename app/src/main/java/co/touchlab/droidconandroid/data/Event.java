package co.touchlab.droidconandroid.data;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

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

    @NotNull
    @DatabaseField(foreign = true, canBeNull = false, foreignAutoRefresh = true)
    public Venue venue;

    @DatabaseField
    public Long startDateLong;

    @DatabaseField
    public Long endDateLong;

    @DatabaseField
    public boolean publicEvent;

    @DatabaseField
    public Integer rsvpLimit;

    @DatabaseField
    public Integer rsvpCount;

    @DatabaseField
    public String rsvpUuid;

    @ForeignCollectionField(eager = true, columnName = "eventSpeaker_id")
    public Collection<EventSpeaker> speakerList;

    public boolean isRsvped()
    {
        return rsvpUuid != null;
    }
}
