package co.touchlab.droidconandroid.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.concurrent.Callable;

import co.touchlab.droidconandroid.data.staff.EventAttendee;
import co.touchlab.squeaky.android.squeaky.Dao;
import co.touchlab.squeaky.android.squeaky.SqueakyOpenHelper;
import co.touchlab.squeaky.table.TableUtils;

/**
 * Created by kgalligan on 6/28/14.
 */
public class DatabaseHelper extends SqueakyOpenHelper
{

    private static final String DATABASE_FILE_NAME = "droidcon";
    private static final int VERSION = 2;
    private static DatabaseHelper instance;

    private DatabaseHelper(Context context)
    {
        super(context, DATABASE_FILE_NAME, null, VERSION);
    }

    @NotNull
    public static synchronized DatabaseHelper getInstance(Context context)
    {
        if (instance == null)
        {
            instance = new DatabaseHelper(context);
        }

        return instance;
    }


    @Override
    public void onCreate(SQLiteDatabase db)
    {
        try
        {
            TableUtils.createTables(db, tableClasses);
        }
        catch(SQLException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        try
        {
            TableUtils.dropTables(db, true, tableClasses);
        }
        catch(SQLException e)
        {
            throw new RuntimeException(e);
        }

        onCreate(db);
    }

    @Override
    public void onOpen(SQLiteDatabase db)
    {
        super.onOpen(db);
        db.execSQL("PRAGMA foreign_keys=ON;");
    }

    // @reminder Ordering matters, create foreign key dependant classes later
    private final Class[] tableClasses = new Class[] {Venue.class, Event.class, Block.class, Invite.class, UserAccount.class, EventAttendee.class, EventSpeaker.class};

    @NotNull
    public Dao<Venue, Long> getVenueDao()
    {
        return (Dao<Venue, Long>) getDao(Venue.class);
    }

    @NotNull
    public Dao<Event, Long> getEventDao()
    {
        return (Dao<Event, Long>) getDao(Event.class);
    }

    @NotNull
    public Dao<UserAccount, Long> getUserAccountDao()
    {
        return (Dao<UserAccount, Long>) getDao(UserAccount.class);
    }

    @NotNull
    public Dao<EventSpeaker, Long> getEventSpeakerDao()
    {
        return (Dao<EventSpeaker, Long>) getDao(EventSpeaker.class);
    }

    @NotNull
    public Dao<Block, Long> getBlockDao()
    {
        return (Dao<Block, Long>) getDao(Block.class);
    }

    /**
     * @param transaction .
     * @throws RuntimeException on {@link SQLException}
     */
    public void performTransactionOrThrowRuntime(Callable<Void> transaction)
    {
        SQLiteDatabase db = getWritableDatabase();
        try
        {
            db.beginTransaction();
            transaction.call();
            db.setTransactionSuccessful();
        }
        catch (Exception e)
        {
            Log.e(DatabaseHelper.class.getName(), e.getMessage());
            throw new RuntimeException(e);
        }
        finally
        {
            db.endTransaction();
        }
    }

    public void inTransaction(final Runnable r)
    {
        performTransactionOrThrowRuntime(new Callable<Void>()
        {
            @Override
            public Void call() throws Exception
            {
                r.run();
                return null;
            }
        });
    }
}
