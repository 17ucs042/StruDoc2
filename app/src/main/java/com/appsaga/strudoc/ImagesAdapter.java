package com.appsaga.strudoc;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;

public class ImagesAdapter extends CursorAdapter {

    private LayoutInflater mLayoutInflater;
    ImageDatabaseHelper imageDatabaseHelper;

    public ImagesAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
        mLayoutInflater = LayoutInflater.from(context);
        imageDatabaseHelper = new ImageDatabaseHelper(context);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        View v = LayoutInflater.from(context).inflate(R.layout.processed_images_view, parent, false);
        return v;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        ImageView original = view.findViewById(R.id.original);
        ImageView processed = view.findViewById(R.id.processed);

        Bitmap original_bitmap = BitmapFactory.decodeByteArray(cursor.getBlob(1), 0, cursor.getBlob(1).length);
        Bitmap processed_bitmap = BitmapFactory.decodeByteArray(cursor.getBlob(2), 0, cursor.getBlob(2).length);

        original.setImageBitmap(original_bitmap);
        processed.setImageBitmap(processed_bitmap);
    }
}
