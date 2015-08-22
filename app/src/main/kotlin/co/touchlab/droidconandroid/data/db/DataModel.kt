package co.touchlab.droidconandroid.data.db

import co.touchlab.droidconandroid.data.ScheduleBlock
import com.j256.ormlite.field.DatabaseField
import com.j256.ormlite.table.DatabaseTable

/**
 * Created by kgalligan on 8/22/15.
 */
@DatabaseTable
class Block(
        @DatabaseField(id = true) val id: Long,
        @DatabaseField val name: String,
        @DatabaseField val description: String,
        @DatabaseField val startDateLong: Long,
        @DatabaseField val endDateLong: Long
        ): ScheduleBlock {

    override fun isBlock(): Boolean {
        return false
    }

    override fun getStartLong(): Long? {
        return startDateLong
    }

    override fun getEndLong(): Long? {
        return endDateLong
    }
}