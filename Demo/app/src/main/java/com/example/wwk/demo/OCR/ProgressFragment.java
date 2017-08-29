package com.example.wwk.demo.OCR;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.wwk.demo.R;


public class ProgressFragment extends Fragment {

    private TextView textView;
    private ProgressBar progressBar;
    public ProgressFragment() {
        // Required empty public constructor
    }

    public void updateProgress(int percent){
        textView.setText(percent+"%");
        progressBar.setProgress(percent);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_progress, container, false);
        textView= (TextView) view.findViewById(R.id.text_progress_frg);
        progressBar= (ProgressBar) view.findViewById(R.id.progress_bar);
        return view;
    }




}
