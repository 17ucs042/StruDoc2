package com.appsaga.strudoc;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.print.PrintAttributes;
import android.print.pdf.PrintedPdfDocument;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static android.content.Context.PRINT_SERVICE;

public class MessageFragment extends Fragment {

    View view;
    ImageView logOut;
    FirebaseAuth firebaseAuth;

    ImageDatabaseHelper imageDatabaseHelper;
    Cursor cursor;
    ImageView original_view;
    ImageView processed_view;
    Button createPDF;
    Bitmap original_bitmap;
    Bitmap processed_bitmap;
    EditText enterTitle;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if(view==null) {
            view = inflater.inflate(R.layout.fragment_message, container, false);

            original_view=view.findViewById(R.id.original_image);
            processed_view=view.findViewById(R.id.processed_image);
            createPDF=view.findViewById(R.id.convert_button);
            enterTitle=view.findViewById(R.id.enter_title);

            logOut = view.findViewById(R.id.log_out);
            firebaseAuth=FirebaseAuth.getInstance();

            imageDatabaseHelper=new ImageDatabaseHelper(getContext());

            logOut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    firebaseAuth.signOut();
                    startActivity(new Intent(getActivity(), LoginPage.class));
                    getActivity().finish();
                }
            });

            createPDF.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String title = enterTitle.getText().toString();
                    if(title.equals(""))
                    {
                        Toast.makeText(getContext(), "Please enter a title", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        if(original_bitmap!=null && processed_bitmap!=null)
                        {
                            createPdf(title);
                        }
                        else
                        {
                            Toast.makeText(getContext(), "There is no task available", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });

            return view;
        }
        else
        {
            return view;
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        cursor = imageDatabaseHelper.getLastItem();

        if(imageDatabaseHelper.getTotalItems()!=0) {
            original_bitmap = BitmapFactory.decodeByteArray(cursor.getBlob(1), 0, cursor.getBlob(1).length);
            processed_bitmap = BitmapFactory.decodeByteArray(cursor.getBlob(2), 0, cursor.getBlob(2).length);

            original_view.setImageBitmap(original_bitmap);
            processed_view.setImageBitmap(processed_bitmap);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        Toast.makeText(getActivity(), "completed", Toast.LENGTH_SHORT).show();
    }

    private void createPdf(String sometext){
        // create a new document
//        PdfDocument document = new PdfDocument();
//        // crate a page description
//        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(300, 600, 1).create();
        // start a page
        PrintAttributes printAttrs = new PrintAttributes.Builder().setColorMode(PrintAttributes.COLOR_MODE_COLOR).setMediaSize(PrintAttributes.MediaSize.NA_LETTER).setResolution(new PrintAttributes.Resolution("zooey", PRINT_SERVICE, 300, 300)).setMinMargins(PrintAttributes.Margins.NO_MARGINS).build();
        PdfDocument document = new PrintedPdfDocument(getContext(), printAttrs);
        // crate a page description
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(original_bitmap.getWidth(), original_bitmap.getHeight(), 1).create();
        PdfDocument.Page page = document.startPage(pageInfo);

        Canvas canvas = page.getCanvas();
        Paint paint = new Paint();
        paint.setColor(Color.RED);

        canvas.drawBitmap(original_bitmap,0,0,paint);
        paint.setColor(Color.BLACK);
        document.finishPage(page);
        //canvas.drawText(sometext, 80, 50, paint);

        // draw text on the graphics object of the page
        pageInfo = new PdfDocument.PageInfo.Builder(processed_bitmap.getWidth(), processed_bitmap.getHeight(), 2).create();
        page = document.startPage(pageInfo);
        canvas = page.getCanvas();
        //paint = new Paint();
       // paint.setColor(Color.BLUE);

        canvas.drawBitmap(processed_bitmap,0,0,paint);
        document.finishPage(page);
        // write the document content
        String directory_path = Environment.getExternalStorageDirectory().getPath() + "/StruDoc/";
        File file = new File(directory_path);
        if (!file.exists()) {
            file.mkdirs();
        }
        String targetPdf = directory_path+sometext+".pdf";
        File filePath = new File(targetPdf);
        try {
            document.writeTo(new FileOutputStream(filePath));
            Toast.makeText(getContext(), "PDF saved to storage", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            Log.e("main", "error "+e.toString());
            Toast.makeText(getContext(), "Something wrong: " + e.toString(),  Toast.LENGTH_LONG).show();
        }
        // close the document
        document.close();
    }
}
