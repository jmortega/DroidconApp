package co.touchlab.droidconandroid.ui;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.os.AsyncTask;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import co.touchlab.android.threading.eventbus.EventBusExt;
import co.touchlab.android.threading.tasks.BaseTaskQueue;
import co.touchlab.android.threading.tasks.Task;
import co.touchlab.android.threading.tasks.TaskQueue;
import co.touchlab.droidconandroid.network.dao.UserAccount;
import co.touchlab.droidconandroid.tasks.Queues;
import co.touchlab.droidconandroid.tasks.SearchUsersTask;

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
        setThreshold(3);
        addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                TaskQueue taskQueue = Queues.networkQueue(getContext());
                taskQueue.query(new BaseTaskQueue.QueueQuery()
                {
                    @Override
                    public void query(BaseTaskQueue queue, Task task)
                    {
                        if(task instanceof SearchUsersTask)
                        {
                            ((SearchUsersTask)task).cancel();
                        }
                    }
                }); taskQueue.execute(new SearchUsersTask(s.toString()));
            }

            @Override
            public void afterTextChanged(Editable s)
            {

            }
        });
    }

    @Override
    protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();
        EventBusExt.getDefault().register(this);
    }

    @Override
    protected void onDetachedFromWindow()
    {
        super.onDetachedFromWindow();
        EventBusExt.getDefault().unregister(this);
    }

    @SuppressWarnings("unused")
    public void onEventMainThread(SearchUsersTask task)
    {
        UserAccount[] results;

        if(task.getUserSearchResponse() != null && task.getUserSearchResponse().getResults() != null)
        {
            results = task.getUserSearchResponse().getResults();
        }
        else
        {
            results = new UserAccount[0];
        }

        setAdapter(new ResultsAdapter(getContext(), results));
        showDropDown();
    }

    public static class ResultsAdapter extends ArrayAdapter<UserAccount>
    {

        public ResultsAdapter(Context context, UserAccount[] objects)
        {
            super(context, android.R.layout.simple_list_item_1, objects);

        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            View row = LayoutInflater.from(getContext())
                                         .inflate(android.R.layout.simple_list_item_1, null);

            ((TextView)row.findViewById(android.R.id.text1)).setText(getItem(position).getName());

            return row;
        }
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
