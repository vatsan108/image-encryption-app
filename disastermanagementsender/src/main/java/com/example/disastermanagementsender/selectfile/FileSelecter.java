package com.example.disastermanagementsender.selectfile;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ListView;

import com.example.disastermanagementsender.R;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

public class FileSelecter extends ListActivity {

    private File currentDir;
    private FileArrayAdapter adapter;
    static String path = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String sdCard = Environment.getExternalStorageDirectory().getAbsolutePath();

        currentDir = new File(sdCard);
        fill(currentDir);
    }

    private void fill(File f) {
        File[] dirs = f.listFiles();
        this.setTitle("Current Dir: " + f.getName());
        List<Option> dir = new ArrayList<Option>();
        List<Option> fls = new ArrayList<Option>();
        try {
            for (File ff : dirs) {
                if (ff.isDirectory())
                    dir.add(new Option(ff.getName(), "Folder", ff.getAbsolutePath()));
                else {
                    fls.add(new Option(ff.getName(), "File Size: " + ff.length(), ff.getAbsolutePath()));
                }
            }
        } catch (Exception e) {

        }
        Collections.sort(dir);
        Collections.sort(fls);
        dir.addAll(fls);
        if (!f.getName().equalsIgnoreCase("sdcard"))
            dir.add(0, new Option("..", "Parent Directory", f.getParent()));
        adapter = new FileArrayAdapter(FileSelecter.this, R.layout.file_view, dir);
        this.setListAdapter(adapter);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        // TODO Auto-generated method stub
        super.onListItemClick(l, v, position, id);
        Option o = adapter.getItem(position);
        if (o.getData().equalsIgnoreCase("folder") || o.getData().equalsIgnoreCase("parent directory")) {
            currentDir = new File(o.getPath());
            fill(currentDir);
        } else {
            onFileClick(o);
        }
    }


    private void onFileClick(Option o) {
        //Toast.makeText(this, "File Clicked: "+o.getName()+"\n path..."+o.getPath(), Toast.LENGTH_SHORT).show();
        String FileName = o.getName();
        StringTokenizer stringTokenizer = new StringTokenizer(FileName, ".");
        String MainName = stringTokenizer.nextElement().toString();
        String Exten = stringTokenizer.nextElement().toString();
        String ExtenLower = Exten.toLowerCase();
        path = o.getPath();
        Intent data = new Intent();
        data.putExtra("name", path);
        setResult(2000, data);
        finish();

    }

    public interface OnFileSelectedListener {
        public void onFileSelected(String filePath);
    }
}