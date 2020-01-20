package com.appsaga.strudoc;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.sql.Blob;
import java.util.ArrayList;

public class ImageDatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME=" image.db";
    public static final String TABLE_NAME="ImageTable";
    public static final String COL_1=" _id";
    public static final String COL_2=" original";
    public static final String COL_3=" processed";

    public  ImageDatabaseHelper(Context context){
        super(context,DATABASE_NAME,null,1);
    }

    // DatabaseHelper mDatabaseHelper ;

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("create table " + TABLE_NAME+" ( _id INTEGER,original BLOB,processed BLOB)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(db);
    }

    public  boolean insertData(byte[] original, byte[] processed)
    {
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues contentValues= new ContentValues();
        contentValues.put(COL_2,original);
        contentValues.put(COL_3,processed);

        long result=db.insert(TABLE_NAME,null,contentValues);
        if(result==-1)
        {return false;}
        else
            return  true;
    }

    public Cursor getAllData(){
        SQLiteDatabase db=this.getWritableDatabase();
        Cursor res= db.rawQuery("select * from "+TABLE_NAME,null);
        return res;
    }

    public int getQuantity(String item)
    {
        int quantity=0;
        String i;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res= db.rawQuery("select * from "+TABLE_NAME,null);

        if(res.moveToFirst())
        {
            do
            {
                i=res.getString(0);
                if(i.equals(item))
                {
                    quantity = res.getInt(1);
                    break;
                }
            }while (res.moveToNext());
        }
        return quantity;
    }

    public String getPrice(String item)
    {
        String price="";
        String i;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res= db.rawQuery("select * from "+TABLE_NAME,null);

        if(res.moveToFirst())
        {
            do
            {
                i=res.getString(0);
                if(i.equals(item))
                {
                    price = res.getString(2);
                    break;
                }
            }while (res.moveToNext());
        }
        return price;
    }

    public int getTotalItems()
    {
        ArrayList<String> items = new ArrayList<>();
        int totalItems=0;
        String i;
        int q;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res= db.rawQuery("select * from "+TABLE_NAME,null);

        return res.getCount();
        /*if (res.moveToFirst())
        {
            do
            {
                i=res.getString(0);
                q=res.getInt(1);
                if(!items.contains(i) && q!=0)
                {
                    items.add(i);
                }
            }while (res.moveToNext());
        }
        return items.size();*/
    }

    public void deleteData(byte[] image)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL ("delete from "+TABLE_NAME +" WHERE image = " + image);
    }

    public Cursor getLastItem()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from "+ TABLE_NAME,null);
        res.moveToLast();
        return res;
    }
}
