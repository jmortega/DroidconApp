package co.touchlab.droidconandroid.data;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.sql.SQLException;
import java.util.List;

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

//    @DatabaseField
//    public boolean following;

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserAccount that = (UserAccount) o;

        if (avatarKey != null ? !avatarKey.equals(that.avatarKey) : that.avatarKey != null) return false;
        if (company != null ? !company.equals(that.company) : that.company != null) return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (linkedIn != null ? !linkedIn.equals(that.linkedIn) : that.linkedIn != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (profile != null ? !profile.equals(that.profile) : that.profile != null) return false;
        if (twitter != null ? !twitter.equals(that.twitter) : that.twitter != null) return false;
        if (userCode != null ? !userCode.equals(that.userCode) : that.userCode != null) return false;
        if (uuid != null ? !uuid.equals(that.uuid) : that.uuid != null) return false;
        if (website != null ? !website.equals(that.website) : that.website != null) return false;

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (uuid != null ? uuid.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (profile != null ? profile.hashCode() : 0);
        result = 31 * result + (avatarKey != null ? avatarKey.hashCode() : 0);
        result = 31 * result + (userCode != null ? userCode.hashCode() : 0);
        result = 31 * result + (company != null ? company.hashCode() : 0);
        result = 31 * result + (twitter != null ? twitter.hashCode() : 0);
        result = 31 * result + (linkedIn != null ? linkedIn.hashCode() : 0);
        result = 31 * result + (website != null ? website.hashCode() : 0);
        return result;
    }

    public static UserAccount findByCode(DatabaseHelper databaseHelper, String code) throws SQLException
    {
        Dao<UserAccount, Long> dao = databaseHelper.getUserAccountDao();
        List<UserAccount> list = dao.queryBuilder().where().eq("userCode", code).query();
        return list.size() == 0 ? null : list.get(0);
    }

}
