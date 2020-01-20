package com.appsaga.strudoc;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class SavePictureFragment extends Fragment {

    View view;
    Cursor cursor;
    ImageDatabaseHelper imageDatabaseHelper;
    ListView imagelistView;
    ImagesAdapter imagesAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if(view==null) {
            view = inflater.inflate(R.layout.fragment_save_picture, container, false);

            imageDatabaseHelper=new ImageDatabaseHelper(getContext());
            imagelistView=view.findViewById(R.id.images_list_view);
            return view;
        }
        else {
            return view;
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        String[] columns = new String[] { ImageDatabaseHelper.COL_2,ImageDatabaseHelper.COL_3 };
        cursor = imageDatabaseHelper.getAllData();

        imagesAdapter= new ImagesAdapter(getContext(),cursor);
        imagelistView.setAdapter(imagesAdapter);
    }
}
