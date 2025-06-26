package com.example.rental.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.rental.Rent;
import com.example.rental.params.Params;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MyDbHandler extends SQLiteOpenHelper {
    public MyDbHandler(Context context) {
        super(context, Params.DB_NAME, null, Params.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableQuery = "CREATE TABLE " + Params.TABLE_NAME + " ("
                + Params.KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + Params.KEY_IMAGE + " TEXT,"
                + Params.KEY_NAME + " TEXT, "
                + Params.KEY_COMPANY + " TEXT, "
                + Params.SELLER_NAME + " TEXT, "
                + Params.SELLER_MAIL + " TEXT, "
                + Params.KEY_DESCRIPTION + " TEXT, "
                + Params.KEY_PRICE + " TEXT, "
                + Params.KEY_LOCATION + " TEXT"
                + ")";

        Log.d("MyDbHandler","Successfully Created");
        db.execSQL(createTableQuery);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + Params.TABLE_NAME);
        onCreate(db);
    }

    public void addRental(Rent rent){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Params.KEY_IMAGE, rent.getImageUri());
        values.put(Params.KEY_NAME, rent.getName());
        values.put(Params.KEY_COMPANY, rent.getCompany());
        values.put(Params.SELLER_NAME, rent.getSname());
        values.put(Params.SELLER_MAIL, rent.getSmail());
        values.put(Params.KEY_DESCRIPTION, rent.getDescription());
        values.put(Params.KEY_PRICE, rent.getPrice());
        values.put(Params.KEY_LOCATION, rent.getLocation());
        Log.d("MyDbHandler", "Saving Image Path: " + rent.getImageUri());
        db.insert(Params.TABLE_NAME,null,values);
        Log.d("evilafzal","Successfully Inserted");
        db.close();
    }

    public List<Rent> getAllRentals() {
        List<Rent> rentList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + Params.TABLE_NAME;
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            Log.d("Database", "Available columns: " + Arrays.toString(cursor.getColumnNames()));
            do {
                Rent rent = new Rent();
                String imageUri = cursor.isNull(cursor.getColumnIndexOrThrow(Params.KEY_IMAGE)) ? "" : cursor.getString(cursor.getColumnIndexOrThrow(Params.KEY_IMAGE));
                rent.setImageUri(imageUri);
                rent.setName(cursor.getString(cursor.getColumnIndexOrThrow(Params.KEY_NAME)));
                rent.setCompany(cursor.getString(cursor.getColumnIndexOrThrow(Params.KEY_COMPANY)));
                rent.setSname(cursor.getString(cursor.getColumnIndexOrThrow(Params.SELLER_NAME)));
                rent.setSmail(cursor.getString(cursor.getColumnIndexOrThrow(Params.SELLER_MAIL)));
                rent.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(Params.KEY_DESCRIPTION)));
                rent.setPrice(cursor.getString(cursor.getColumnIndexOrThrow(Params.KEY_PRICE)));
                rent.setLocation(cursor.getString(cursor.getColumnIndexOrThrow(Params.KEY_LOCATION)));

                rentList.add(rent);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return rentList;
    }

}
