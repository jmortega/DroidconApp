package co.touchlab.droidconandroid.data;

import android.text.TextUtils;

import com.j256.ormlite.android.squeaky.Dao;
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
    public static final String HTTPS_S3_AMAZONAWS_COM_DROIDCONIMAGES = "https://s3.amazonaws.com/droidconimages/";

    @DatabaseField(id = true)
    public Long id;

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
    public String facebook;

    @DatabaseField
    public String twitter;

    @DatabaseField
    public String linkedIn;

    @DatabaseField
    public String website;

    @DatabaseField
    public boolean following;

    @DatabaseField
    public String email;

    @DatabaseField
    public String gPlus;

    @DatabaseField
    public String phone;

    @DatabaseField
    public String coverKey;

    @DatabaseField
    public Boolean emailPublic;

    public String avatarImageUrl()
    {
        if(TextUtils.isEmpty(avatarKey)) return null;
        else if(avatarKey.startsWith("http")) return avatarKey;
        else return HTTPS_S3_AMAZONAWS_COM_DROIDCONIMAGES + avatarKey;
    }

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
        if (facebook != null ? !facebook.equals(that.twitter) : that.facebook != null) return false;
        if (userCode != null ? !userCode.equals(that.userCode) : that.userCode != null) return false;
        if (website != null ? !website.equals(that.website) : that.website != null) return false;
        if (email != null ? !email.equals(that.email) : that.email != null) return false;
        if (phone != null ? !phone.equals(that.phone) : that.phone != null) return false;
        if (gPlus != null ? !gPlus.equals(that.gPlus) : that.gPlus != null) return false;
        if (coverKey != null ? !coverKey.equals(that.coverKey) : that.coverKey != null) return false;
        if (emailPublic != null ? ! emailPublic.equals(that.emailPublic) : that.emailPublic != null) return false;

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (profile != null ? profile.hashCode() : 0);
        result = 31 * result + (avatarKey != null ? avatarKey.hashCode() : 0);
        result = 31 * result + (userCode != null ? userCode.hashCode() : 0);
        result = 31 * result + (company != null ? company.hashCode() : 0);
        result = 31 * result + (twitter != null ? twitter.hashCode() : 0);
        result = 31 * result + (facebook != null ? facebook.hashCode() : 0);
        result = 31 * result + (linkedIn != null ? linkedIn.hashCode() : 0);
        result = 31 * result + (website != null ? website.hashCode() : 0);
        result = 31 * result + (email != null ? email.hashCode() : 0);
        result = 31 * result + (phone != null ? phone.hashCode() : 0);
        result = 31 * result + (gPlus != null ? gPlus.hashCode() : 0);
        result = 31 * result + (coverKey != null ? coverKey.hashCode() : 0);
        result = 31 * result + (emailPublic != null ? emailPublic.hashCode() : 0);
        return result;
    }

    public static UserAccount findByCode(DatabaseHelper databaseHelper, String code) throws SQLException
    {
        Dao<UserAccount, Long> dao = databaseHelper.getUserAccountDao();
        List<UserAccount> list = dao.createWhere().eq("userCode", code).query();
        return list.size() == 0 ? null : list.get(0);
    }

}
