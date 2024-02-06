package com.example.aplicatie_licenta.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.aplicatie_licenta.R;
import com.example.aplicatie_licenta.activities.ReportVisualisationScreen;
import com.example.aplicatie_licenta.adapters.ReportAdapter;
import com.example.aplicatie_licenta.classes.Report;
import com.example.aplicatie_licenta.database.RequestHandler;
import com.example.aplicatie_licenta.utils.DBConstants;
import com.example.aplicatie_licenta.utils.StaticAttributes;
import com.example.aplicatie_licenta.utils.UserSession;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ReportsFragment extends Fragment {

    ArrayList<Report> reports = new ArrayList<>();
    GridView reportsGrid;
    ReportAdapter reportAdapter;
    private onReportChangedListener listener;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reports, container, false);
        reportsGrid = view.findViewById(R.id.reportsGrid);
        getReportsFromUser();

        reportsGrid.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                reportAdapter.toggleSelection(position);

                SpannableString spannableString = new SpannableString(String.valueOf(reportsGrid.getCheckedItemCount()));
                ForegroundColorSpan white = new ForegroundColorSpan(Color.WHITE);
                spannableString.setSpan(white, 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                mode.setTitle(spannableString);
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                mode.getMenuInflater().inflate(R.menu.menu_delete, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                if (item.getItemId() == R.id.delete_icon){
                    new AlertDialog.Builder(getContext())
                            .setIcon(R.drawable.ic_baseline_error_outline)
                            .setTitle(R.string.alertDialogTitle)
                            .setMessage(R.string.alertDialogDescription)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    SparseBooleanArray selected = reportAdapter.getSelectedProperties();
                                    for(int in = selected.size() - 1; in>=0; in--){
                                        if(selected.valueAt(in)){
                                            Report deletedReport = reportAdapter.getItem(selected.keyAt(in));
                                            deleteReportFromDB(deletedReport.getId());
                                            reportAdapter.removeItem(selected.keyAt(in));
                                        }
                                    }
                                    reportAdapter.notifyDataSetChanged();
                                    selected.clear();
                                    mode.finish();
                                }
                            })
                            .setNegativeButton("No", null)
                            .show();

                }
                return true;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                reportAdapter.removeSelection();
            }
        });

        reportsGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Report report = (Report) parent.getItemAtPosition(position);
                Intent intent = new Intent(getContext(), ReportVisualisationScreen.class);
                intent.putExtra(StaticAttributes.KEY_REPORT, report);
                startActivity(intent);
            }
        });


        return view;
    }

    private void getReportsFromUser(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, DBConstants.URL_GET_REPORTS_FROM_USER, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray reportsArray = jsonObject.getJSONArray("reports");
                    for(int i=0; i<reportsArray.length(); i++){
                        JSONArray reportArray = reportsArray.getJSONArray(i);

                        int id = reportArray.getInt(0);
                        String name = reportArray.getString(1);
                        double value = reportArray.getDouble(2);
                        String supplier = reportArray.getString(3);
                        int propertyId = reportArray.getInt(4);
                        reports.add(new Report(id, name, value, supplier, propertyId));
                    }

                    reportAdapter = new ReportAdapter(getContext(), reports);
                    reportsGrid.setAdapter(reportAdapter);
                    reportsGrid.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE_MODAL);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("userId", String.valueOf(UserSession.getInstance(getContext()).getUserId()));

                return params;
            }
        };

        RequestHandler.getInstance(getContext()).addToRequestQueue(stringRequest);
    }


    private void deleteReportFromDB(int id){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, DBConstants.URL_DELETE_REPORT,
                new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("SUCCESS", "Success!");
                listener.onReportsDeleted();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Volley Error", error.toString());
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("id", String.valueOf(id));

                return params;
            }
        };

        RequestHandler.getInstance(getContext()).addToRequestQueue(stringRequest);
    }

    public interface onReportChangedListener{
        void onReportsDeleted();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        listener = (onReportChangedListener) context;
    }


}