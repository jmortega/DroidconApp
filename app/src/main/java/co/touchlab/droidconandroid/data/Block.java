package co.touchlab.droidconandroid.data;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by izzyoji :) on 8/12/15.
 */
@DatabaseTable
public class Block implements ScheduleBlock
{
    @DatabaseField(id = true)
    public long id;

    @DatabaseField
    public String name;

    @DatabaseField
    public String description;

    @DatabaseField
    public Long startDateLong;

    @DatabaseField
    public Long endDateLong;

    @Override
    public boolean isBlock()
    {
        return true;
    }

    @Override
    public Long getStartLong()
    {
        return startDateLong;
    }

    @Override
    public Long getEndLong()
    {
        return endDateLong;
    }
}
