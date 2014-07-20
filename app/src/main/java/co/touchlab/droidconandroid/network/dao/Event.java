package co.touchlab.droidconandroid.network.dao;

import com.j256.ormlite.field.DatabaseField;

/**
 * Created by kgalligan on 7/19/14.
 */
public class Event extends co.touchlab.droidconandroid.data.Event
{
    @DatabaseField
    public String startDate;

    @DatabaseField
    public String endDate;
}
