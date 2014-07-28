package co.touchlab.droidconandroid.ui;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kgalligan on 7/27/14.
 */
public class EmailAccountsEditText extends AutoCompleteTextView
{

    public static final String GOOGLE_ACCOUNT_TYPE = "com.google";

    public EmailAccountsEditText(Context context)
    {
        super(context);
        init();
    }

    public EmailAccountsEditText(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public EmailAccountsEditText(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        init();
    }

    private void init()
    {
        setThreshold(1);
        new AsyncTask<Void, Void, List<String>>()
        {
            @Override
            protected List<String> doInBackground(Void... params)
            {
                return findEmailAccounts();
            }

            @Override
            protected void onPostExecute(List<String> o)
            {
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, o);
                setAdapter(adapter);

                if(o.size() == 1)
                {
                    setText(o.get(0));
                }
            }
        }.execute();
    }

    private List<String> findEmailAccounts()
    {
        List<String> emails = new ArrayList<String>();
        AccountManager accountManager = AccountManager.get(getContext());
        Account[] accounts = accountManager.getAccountsByType(GOOGLE_ACCOUNT_TYPE);
        for (Account account : accounts)
        {
            if (isValidEmail(account.name))
                emails.add(account.name);
        }

        return emails;
    }

    private static boolean isValidEmail(CharSequence target)
    {
        return target != null && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }
}
