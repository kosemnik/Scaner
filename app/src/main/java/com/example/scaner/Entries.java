package com.example.scaner;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Entries extends AppCompatActivity {

    DBHelper dbHelper;
    private TextView result;
    private SQLiteDatabase dataBase;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entries);

        dbHelper = new DBHelper(this);
        dataBase = dbHelper.getWritableDatabase();
        result = (TextView) findViewById(R.id.Found);
        result.setText("Найдено:" + "\n" + "\n");

        Cursor cursor = dataBase.query(DBHelper.TABLE_INVENTORY, null, null,
                null, null, null, null);

        if (cursor.moveToFirst())
        {
            int idIndex = cursor.getColumnIndex(DBHelper.KEY_ID);
            int cabinetIndex = cursor.getColumnIndex(DBHelper.KEY_CABINET_SCAN);
            int itemIndex = cursor.getColumnIndex(DBHelper.KEY_ITEM_SCAN);
            int countIndex = cursor.getColumnIndex(DBHelper.KEY_COUNT);

            do {
                result.setText(result.getText() + "id = " + cursor.getInt(idIndex) +
                        ", код кабинета = " + cursor.getString(cabinetIndex) +
                        ", код предмета = " + cursor.getString(itemIndex) +
                        ", количество = " + cursor.getInt(countIndex) + "\n" + "\n");
            } while(cursor.moveToNext());
        }

        cursor.close();
    }

    public void Save(View view) throws IOException {
        EditText et = (EditText) findViewById(R.id.FIleName);
        String fileName = ((EditText) findViewById(R.id.FIleName)).getText().toString();
        if (fileName.equals(""))
        {
            Toast.makeText(Entries.this, "Укажите название файла", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED))
            return;
        File sdPath = Environment.getExternalStoragePublicDirectory(Environment.MEDIA_MOUNTED);
        sdPath.mkdirs();
        File sdFile = new File(sdPath, fileName + ".txt");
        BufferedWriter bw = new BufferedWriter(new FileWriter(sdFile));
        bw.write((String) result.getText());
        bw.close();

        Toast.makeText(Entries.this, "Файл сохранен", Toast.LENGTH_SHORT).show();
    }

    public void OpenScan(View view)
    {
        result.setText("");
        finish();
    }
}