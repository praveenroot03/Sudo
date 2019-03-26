package com.project.sudo;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class CustomListView extends ArrayAdapter {
    //to reference the Activity
    private final Activity context;

    //to store the cost
    private final ArrayList<String> moneyarray;

    //to store the list of donated Org's
    private final ArrayList<String> nameArray;

    //to store the list of timestamps
    private final ArrayList<String> timeArray;


    CustomListView(Activity context, ArrayList<String> moneyarray, ArrayList<String> nameArray, ArrayList<String> timeArray) {
        super(context, R.layout.listview_row, nameArray);
        this.context = context;
        this.moneyarray = moneyarray;
        this.nameArray = nameArray;
        this.timeArray = timeArray;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.listview_row, null, true);

        //this code gets references to objects in the listview_row.xml file

        TextView dMoney = rowView.findViewById(R.id.don_money);
        TextView dOrgName = rowView.findViewById(R.id.org_name);
        TextView dtimestamp = rowView.findViewById(R.id.timestamp);

        String s = "₹" + moneyarray.get(position);
        dMoney.setText(s);
        dOrgName.setText(nameArray.get(position));
        String date = new Date(new Timestamp(Long.parseLong(timeArray.get(position))).getTime()).toString();
        String split[] = date.split(" ");
        date = split[0] + " " + split[1] + " " + split[2] + " " + split[3] + " " + split[5];
        dtimestamp.setText(date);


        return rowView;
    }
}
