package com.example.scaner;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class MainActivity extends AppCompatActivity{
    private Button scanCabinet, scanItem;
    private TextView cabinet, item;
    private int processNumber;
    private String cabinetResult, itemResult;

    DBHelper dbHelper;
    SQLiteDatabase dataBase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        scanCabinet = (Button) findViewById(R.id.ScanCabinet);
        cabinet = (TextView) findViewById(R.id.Cabinet);
        scanItem = (Button) findViewById(R.id.ScanItem);
        item = (TextView) findViewById(R.id.Item);

        dbHelper = new DBHelper(this);
        dataBase = dbHelper.getWritableDatabase();
        dataBase.delete(DBHelper.TABLE_INVENTORY, null, null);

        scanCabinet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { openScanCabinet(); }
        });

        scanItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openScantItem();
            }
        });
    }

    public void SaveEntry(View view)
    {
        if (cabinet.getText() != "" && item.getText() != "") {
            Cursor cursor = dataBase.query(DBHelper.TABLE_INVENTORY, null, null,
                    null, null, null, null);
            if (cursor.moveToFirst())
            {
                int idIndex = cursor.getColumnIndex(DBHelper.KEY_ID);
                int cabinetScanIndex = cursor.getColumnIndex(DBHelper.KEY_CABINET_SCAN);
                int itemScanIndex = cursor.getColumnIndex(DBHelper.KEY_ITEM_SCAN);
                int countIndex = cursor.getColumnIndex(DBHelper.KEY_COUNT);

                do
                    if (cursor.getString(cabinetScanIndex).equals(cabinetResult) && cursor.getString(itemScanIndex).equals(itemResult))
                    {
                        ContentValues newContentValues = new ContentValues();
                        newContentValues.put(DBHelper.KEY_ID, cursor.getInt(idIndex));
                        newContentValues.put(DBHelper.KEY_CABINET_SCAN, cursor.getString(cabinetScanIndex));
                        newContentValues.put(DBHelper.KEY_ITEM_SCAN, cursor.getString(itemScanIndex));
                        newContentValues.put(DBHelper.KEY_COUNT, cursor.getInt(countIndex) + 1);
                        dataBase.update(dbHelper.TABLE_INVENTORY, newContentValues, "_id=" + cursor.getInt(idIndex), null);
                        return;
                    }
                while(cursor.moveToNext());
            }

            ContentValues contentValues = new ContentValues();
            contentValues.put(DBHelper.KEY_CABINET_SCAN, cabinetResult);
            contentValues.put(DBHelper.KEY_ITEM_SCAN, itemResult);
            contentValues.put(DBHelper.KEY_COUNT, 1);
            dataBase.insert(DBHelper.TABLE_INVENTORY, null, contentValues);

            Toast.makeText(MainActivity.this, "Запись сохранена", Toast.LENGTH_SHORT).show();
        }
        else
            Toast.makeText(MainActivity.this, "Заполните оба поля", Toast.LENGTH_SHORT).show();
    }

    private void openScanCabinet() {
        processNumber = 0;
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setCaptureActivity(CaptureAct.class);
        integrator.setOrientationLocked(false);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        integrator.setPrompt("Сканирование кабинета");
        integrator.initiateScan();
    }

    private void openScantItem() {
        processNumber = 1;
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setCaptureActivity(CaptureAct.class);
        integrator.setOrientationLocked(false);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        integrator.setPrompt("Сканирование предмета");
        integrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null)
            if (result.getContents() != null)
                if (processNumber == 0) {
                    cabinet.setText(result.getContents());
                    cabinetResult = result.getContents();
                }
                else {
                    item.setText(result.getContents());
                    itemResult = result.getContents();
                }
            else
                Toast.makeText(this, "Нет результатов", Toast.LENGTH_LONG).show();
        else
            super.onActivityResult(requestCode, resultCode, data);
    }

    public void NewPage(View view)
    {
        Intent intent = new Intent(this, Entries.class);
        startActivity(intent);
    }
}