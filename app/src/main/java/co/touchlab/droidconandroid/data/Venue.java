package co.touchlab.droidconandroid.data;

import co.touchlab.squeaky.field.DatabaseField;
import co.touchlab.squeaky.table.DatabaseTable;

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
