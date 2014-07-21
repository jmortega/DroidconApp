package co.touchlab.droidconandroid.network.dao;

import com.j256.ormlite.field.DatabaseField;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kgalligan on 7/19/14.
 */
public class Convention
{
    public Long id;
    public String description;
    public String locationName;

    @DatabaseField
    public String startDate;

    @DatabaseField
    public String endDate;

    @NotNull
    public List<Venue> venues = new ArrayList<Venue>();
}
