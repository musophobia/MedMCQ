package com.example.sakib.myapplication.models.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.sakib.myapplication.R;
import com.example.sakib.myapplication.models.Questions;

import java.util.List;


/**
 * Created by norman on 12/26/16.
 */

public class QuestionAdapter extends ArrayAdapter<Questions> {

    private Context context;
    private List<Questions> values;

    public QuestionAdapter(Context context, List<Questions> values) {
        super(context, R.layout.question_pagination_item, values);

        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;

        if (row == null) {
            LayoutInflater inflater =
                    (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.question_pagination_item, parent, false);
        }

        TextView textView = (TextView) row.findViewById(R.id.question_item_pagination_text);

        Questions item = values.get(position);
        String message = item.getQuestion();
//        Button button =(Button) row.findViewById(R.id.childButton);
//        button.setText(message);
//        button.setFocusable(false);
//        button.setClickable(false);

        textView.setText(message);

        return row;
    }
}