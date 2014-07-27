package co.touchlab.droidconandroid.data.staff;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by kgalligan on 6/28/14.
 */
@DatabaseTable
public class UserAccount
{
    @DatabaseField(id = true)
    public Long id;

    @DatabaseField
    public String uuid;

    @DatabaseField
    public String name;

    @DatabaseField
    public String profile;

    @DatabaseField
    public String avatarKey;

    @DatabaseField
    public String userCode;

    @DatabaseField
    public String company;

    @DatabaseField
    public String twitter;

    @DatabaseField
    public String linkedIn;

    @DatabaseField
    public String website;
}
