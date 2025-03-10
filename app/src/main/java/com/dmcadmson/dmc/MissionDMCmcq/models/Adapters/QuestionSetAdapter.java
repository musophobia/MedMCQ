//package com.example.dmc.MissionDMCmcq.models.Adapters;
//
//import android.content.Context;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ArrayAdapter;
//import android.widget.TextView;
//
//import com.example.dmc.MissionDMCmcq.R;
//import com.example.dmc.MissionDMCmcq.models.QuestionSet;
//
//import java.util.List;
//
//
///**
// * Created by norman on 12/26/16.
// */
//
//public class QuestionSetAdapter extends ArrayAdapter<QuestionSet> {
//
//    private Context context;
//    private List<QuestionSet> values;
//
//    public QuestionSetAdapter(Context context, List<QuestionSet> values) {
//        super(context, R.layout.question_pagination_item, values);
//
//        this.context = context;
//        this.values = values;
//    }
//
//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//        View row = convertView;
//
//        if (row == null) {
//            LayoutInflater inflater =
//                    (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//            row = inflater.inflate(R.layout.question_pagination_item, parent, false);
//        }
//
//        TextView textView = (TextView) row.findViewById(R.id.question_item_pagination_text);
//
//        QuestionSet item = values.get(position);
//        String message = item.getQuestionName();
//        textView.setText(message);
//
//        return row;
//    }
//}