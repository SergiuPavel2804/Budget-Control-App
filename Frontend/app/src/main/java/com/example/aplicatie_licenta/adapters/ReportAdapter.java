package com.example.aplicatie_licenta.adapters;

import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.aplicatie_licenta.R;
import com.example.aplicatie_licenta.classes.Property;
import com.example.aplicatie_licenta.classes.Report;

import java.util.ArrayList;

public class ReportAdapter extends ArrayAdapter<Report> {

    private SparseBooleanArray selectedReports;
    private ArrayList<Report> reports = new ArrayList<>();

    public ReportAdapter(@NonNull Context context, ArrayList<Report> reports) {
        super(context, 0, reports);
        this.reports = reports;
        selectedReports = new SparseBooleanArray();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        HolderView holderView;

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.gridview_report, parent, false);
            holderView = new HolderView(convertView);
            convertView.setTag(holderView);
        }
        else{
            holderView = (HolderView) convertView.getTag();
        }

        Report report = getItem(position);
        holderView.reportImage.setImageResource(R.drawable.seo_report);
        holderView.tvReport.setText(report.getName());

        return convertView;
    }

    private static class HolderView{
        private final TextView tvReport;
        private final ImageView reportImage;

        public HolderView(View view) {
            this.tvReport = view.findViewById(R.id.tvReport);
            this.reportImage = view.findViewById(R.id.reportImage);
        }
    }

    @Override
    public int getCount() {
        return reports.size();
    }

    @Nullable
    @Override
    public Report getItem(int position) {
        return reports.get(position);
    }

    public void removeItem(int position){
        reports.remove(position);
    }


    public void toggleSelection(int position){
        selectView(position, !selectedReports.get(position));
    }

    public void removeSelection() {
        selectedReports = new SparseBooleanArray();
        notifyDataSetChanged();
    }

    public void selectView(int position, boolean value){
        if(value) {
            selectedReports.put(position, value);
        } else {
            selectedReports.delete(position);
        }
    }

    public SparseBooleanArray getSelectedProperties() {
        return selectedReports;
    }
}
