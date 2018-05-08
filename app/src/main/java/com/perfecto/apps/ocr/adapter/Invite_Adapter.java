package com.perfecto.apps.ocr.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import com.perfecto.apps.ocr.R;
import com.perfecto.apps.ocr.models.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Hosam Azzam on 15/10/2017.
 */

public class Invite_Adapter extends ArrayAdapter<User> {
    private final Context mContext;
    private final ArrayList<User> users;
    private final ArrayList<User> users_All;
    private final ArrayList<User> users_Suggestion;
    private final int mLayoutResourceId;

    public Invite_Adapter(Context context, int resource, ArrayList<User> users) {
        super(context, resource, users);
        this.mContext = context;
        this.mLayoutResourceId = resource;
        this.users = new ArrayList<>(users);
        this.users_All = new ArrayList<>(users);
        this.users_Suggestion = new ArrayList<>();
    }

    public int getCount() {
        return users.size();
    }

    public User getItem(int position) {
        return users.get(position);
    }

    public long getItemId(int position) {
        return users.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        try {
            if (convertView == null) {
                LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
                convertView = inflater.inflate(mLayoutResourceId, parent, false);
            }
            User user = getItem(position);
            TextView name = (TextView) convertView.findViewById(R.id.invite_user_name_txt);
            name.setText(user.getName());
            TextView email = (TextView) convertView.findViewById(R.id.invite_user_email_txt);
            email.setText(user.getEmail());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return convertView;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            public String convertResultToString(Object resultValue) {
                return ((User) resultValue).getEmail();
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                if (constraint != null) {
                    users_Suggestion.clear();
                    for (User department : users_All) {
                        if (department.getName().toLowerCase().startsWith(constraint.toString().toLowerCase())
                                || department.getEmail().toLowerCase().startsWith(constraint.toString().toLowerCase())) {
                            users_Suggestion.add(department);
                        }
                    }
                    FilterResults filterResults = new FilterResults();
                    filterResults.values = users_Suggestion;
                    filterResults.count = users_Suggestion.size();
                    return filterResults;
                } else {
                    return new FilterResults();
                }
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                users.clear();
                if (results != null && results.count > 0) {
                    // avoids unchecked cast warning when using mDepartments.addAll((ArrayList<Department>) results.values);
                    List<?> result = (List<?>) results.values;
                    for (Object object : result) {
                        if (object instanceof User) {
                            users.add((User) object);
                        }
                    }
                } else if (constraint == null) {
                    // no filter, add entire original list back in
                    users.addAll(users_All);
                }
                notifyDataSetChanged();
            }
        };
    }
}

