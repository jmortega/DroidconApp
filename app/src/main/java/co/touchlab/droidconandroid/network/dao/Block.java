package co.touchlab.droidconandroid.network.dao;
import com.j256.ormlite.field.DatabaseField;

/**
 * Created by izzyoji :) on 8/12/15.
 */
public class Block extends co.touchlab.droidconandroid.data.Block
{
    @DatabaseField
    public String startDate;

    @DatabaseField
    public String endDate;

}
