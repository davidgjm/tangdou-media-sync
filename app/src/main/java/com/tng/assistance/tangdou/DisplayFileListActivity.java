package com.tng.assistance.tangdou;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.StringJoiner;

public class DisplayFileListActivity extends AppCompatActivity {
    TextView externalStorageInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_file_list);
        externalStorageInfo = findViewById(R.id.externalStorageInfo);

        String storageInfo = String.format("Simulated? %s, State: %s", Environment.isExternalStorageEmulated(), Environment.getExternalStorageState());
        externalStorageInfo.setText(storageInfo);

        Intent intent = getIntent();
        ArrayList<String> files = intent.getStringArrayListExtra(MainActivity.EXTRA_FILE_LIST);
        System.out.println("received content length: "+ files.size());

        StringBuilder lines = new StringBuilder();
        for (String file : files) {
            lines.append(file).append("\n");
        }
        lines.trimToSize();


        TextView textView = findViewById(R.id.file_list);
        textView.setText(lines);
    }
}
