package com.appsaga.strudoc;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.io.ByteArrayOutputStream;

public class SharePictureFragment extends Fragment {

    View view;
    Button share;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if(view==null) {
            view = inflater.inflate(R.layout.fragment_share_picture, container, false);

            share = view.findViewById(R.id.share_button);

            share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (ContextCompat.checkSelfPermission(getContext(),
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {

                        ActivityCompat.requestPermissions(getActivity(),
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                101);
                    } else {

                        Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.logo);
                        Intent share1 = new Intent(Intent.ACTION_SEND);
                        share1.setType("image/png");
                        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                        b.compress(Bitmap.CompressFormat.PNG, 100, bytes);
                        String path = MediaStore.Images.Media.insertImage(getActivity().getContentResolver(),
                                b, "Title", null);
                        Uri imageUri1 = Uri.parse(path);
                        share1.putExtra(Intent.EXTRA_STREAM, imageUri1);
                        startActivity(Intent.createChooser(share1, "Share Image using"));
                    }
                }
            });

            return view;
        }
        else {
            return view;
        }
    }
}
