package co.touchlab.droidconandroid.data;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import org.jetbrains.annotations.NotNull;

/**
 * Created by kgalligan on 6/28/14.
 */
@DatabaseTable
public class Venue
{
    @DatabaseField(id = true)
    public long id;

    @DatabaseField(canBeNull = false)
    public String name;

    @DatabaseField
    public String description;

    @DatabaseField
    public String mapImageUrl;
}
