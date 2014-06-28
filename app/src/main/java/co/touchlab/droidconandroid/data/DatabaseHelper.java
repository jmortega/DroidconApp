package co.touchlab.droidconandroid.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import co.touchlab.android.superbus.errorcontrol.StorageException;
import co.touchlab.android.superbus.storage.CommandPersistenceProvider;
import co.touchlab.android.superbus.storage.sqlite.ClearSQLiteDatabase;
import co.touchlab.droidconandroid.data.staff.EventAttendee;
import co.touchlab.droidconandroid.data.staff.UserAccount;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.misc.TransactionManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.util.concurrent.Callable;

/**
 * Created by kgalligan on 6/28/14.
 */
public class DatabaseHelper extends OrmLiteSqliteOpenHelper
{

    private static final String DATABASE_FILE_NAME = "droidcon";
    private static final int VERSION = 1;
    private static DatabaseHelper instance;

    private DatabaseHelper(Context context)
    {
        super(context, DATABASE_FILE_NAME, null, VERSION);
    }

    public static synchronized DatabaseHelper getInstance(Context context)
    {
        if(instance == null)
        {
            instance = new DatabaseHelper(context);
        }

        return instance;
    }


    @Override
    public void onOpen(SQLiteDatabase db)
    {
        super.onOpen(db);
        db.execSQL("PRAGMA foreign_keys=ON;");
    }

    // @reminder Ordering matters, create foreign key dependant classes later
    private final Class[] tableClasses = new Class[]{
            Venue.class
            , Event.class
            , Invite.class
            , UserAccount.class
            , EventAttendee.class
    };

    //todo blow this up
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource) {
        try
        {
            for (Class mTableClass : tableClasses)
            {
                TableUtils.getCreateTableStatements(connectionSource, mTableClass);
                TableUtils.createTable(connectionSource, mTableClass);
            }

            CommandPersistenceProvider.createTables(new ClearSQLiteDatabase(sqLiteDatabase));
        }
        catch (StorageException e)
        {
            throw new RuntimeException(e);
        } catch (SQLException e)
        {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void onUpgrade(final SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource, int oldVersion, int newVersion)
    {

        for(int i=tableClasses.length - 1; i >= 0; i--)
        {
            Class tableClass = tableClasses[i];
            try
            {
                TableUtils.dropTable(connectionSource, tableClass, true);
            }
            catch (SQLException e)
            {
                throw new RuntimeException(e);
            }
        }

        onCreate(sqLiteDatabase, connectionSource);

    }

    public TransactionManager createTransactionManager()
    {
        return new TransactionManager(getConnectionSource());
    }

    /**
     * @param transaction .
     * @throws RuntimeException on {@link SQLException}
     */
    public void performTransactionOrThrowRuntime(Callable<Void> transaction) {
        try {
            TransactionManager transactionManager = createTransactionManager();
            transactionManager.callInTransaction(transaction);
        } catch (SQLException e) {
            Log.e(DatabaseHelper.class.getName(), e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
