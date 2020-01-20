package com.appsaga.strudoc;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.appsaga.strudoc.ml.DeeplabInterface;
import com.appsaga.strudoc.ml.DeeplabModel;
import com.dailystudio.app.utils.ArrayUtils;
import com.dailystudio.development.Logger;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static android.app.Activity.RESULT_OK;
import static androidx.core.content.ContextCompat.checkSelfPermission;
import static com.dailystudio.app.utils.ActivityLauncher.launchActivityForResult;

public class PhotoFragment extends Fragment {

    View view;
    private static final int CAMERA_REQUEST = 1888;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    Bitmap photo;
    ImageView imageView;
    Button select,capture;
    TextView type;
    Bitmap bmOverlay;
    ImageDatabaseHelper imageDatabaseHelper;

    private final static int REQUEST_REQUIRED_PERMISSION = 0x01;
    private final static int REQUEST_PICK_IMAGE = 0x02;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_photo, container, false);
            imageDatabaseHelper= new ImageDatabaseHelper(getActivity());

            imageView = view.findViewById(R.id.photo);
            type = view.findViewById(R.id.type);
            select = view.findViewById(R.id.button_select);
            capture=view.findViewById(R.id.button_capture);

            select.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent;

                    if (Build.VERSION.SDK_INT >= 19) {
                        intent = new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
                        intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
                    } else {
                        intent = new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    }

                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    intent.setType("image/*");

                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                    startActivityForResult(Intent.createChooser(intent, getString(R.string.app_name)),
                            REQUEST_PICK_IMAGE);

                    Log.d("Test", "test1");
                }
            });

            capture.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (checkSelfPermission(getContext(),Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                        {
                            requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
                        }
                        else
                        {
                            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(cameraIntent, CAMERA_REQUEST);
                        }
                    }
                }
            });

            return view;
        } else {
            return view;
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        syncUIWithPermissions(true);
    }

    @Override
    public void onResume() {
        super.onResume();

        syncUIWithPermissions(false);
    }

    private void syncUIWithPermissions(boolean requestIfNeed) {
        final boolean granted = checkRequiredPermissions(requestIfNeed);

        if (granted && !DeeplabModel.getInstance().isInitialized()) {
            initModel();
        }
    }

    private boolean checkRequiredPermissions(boolean requestIfNeed) {
        final boolean writeStoragePermGranted =
                checkSelfPermission(getActivity(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED;

        Logger.debug("storage permission granted: %s", writeStoragePermGranted);

        if (!writeStoragePermGranted
                && requestIfNeed) {
            requestRequiredPermissions();
        }

        return writeStoragePermGranted;
    }

    private void requestRequiredPermissions() {
        ActivityCompat.requestPermissions(getActivity(),
                new String[]{
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                },
                REQUEST_REQUIRED_PERMISSION);
    }

    private void initModel() {
        new InitializeModelAsyncTask().execute((Void) null);
    }

    private class InitializeModelAsyncTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... voids) {
            final boolean ret = DeeplabModel.getInstance().initialize(
                    getContext());
            Logger.debug("initialize deeplab model: %s", ret);
            Log.d("Test", "test3");
            Log.d("Test", ret + "");

            return ret;
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Logger.debug("requestCode = 0x%02x, permission = [%s], grant = [%s]",
                requestCode,
                ArrayUtils.stringArrayToString(permissions, ","),
                ArrayUtils.intArrayToString(grantResults));
        if (requestCode == REQUEST_REQUIRED_PERMISSION) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Logger.debug("permission granted, initialize model.");
                initModel();

                Log.d("Test", "test2");
            } else {
                Logger.debug("permission denied, disable fab.");
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Logger.debug("requestCode = %d, resultCode = %d, data = %s",
                requestCode,
                resultCode,
                data);
        Log.d("Test", resultCode + "");
        if (requestCode == REQUEST_PICK_IMAGE
                && resultCode == RESULT_OK) {
            Uri pickedImageUri = data.getData();
            Logger.debug("picked: %s", pickedImageUri);
            Log.d("Test", "test4");

            if (pickedImageUri != null) {
                if (Build.VERSION.SDK_INT >= 19) {
                    try {
                        photo = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), pickedImageUri);
                        Log.d("Test", "test5");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    final int takeFlags = data.getFlags() & Intent.FLAG_GRANT_READ_URI_PERMISSION;
                    /*getActivity().getContentResolver()
                            .takePersistableUriPermission(pickedImageUri, takeFlags);*/
                }

                segmentImage();
            }
        }else if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            photo = (Bitmap) data.getExtras().get("data");

            segmentImage();
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void segmentImage() {

        DeeplabInterface deeplabInterface = DeeplabModel.getInstance();
       /* final int w = photo.getWidth();
        final int h = photo.getHeight();

        float resizeRatio = (float) deeplabInterface.getInputSize() / Math.max(photo.getWidth(), photo.getHeight());
        int rw = Math.round(w * resizeRatio);
        int rh = Math.round(h * resizeRatio);

        Bitmap resized = ImageUtils.tfResizeBilinear(photo, rw, rh);

        Bitmap mask = deeplabInterface.segment(resized);
        Log.d("Test",rw+"");
        Log.d("Test","test6");
        Log.d("Test",mask.getByteCount()+"");
        if (mask != null) {
            mask = BitmapUtils.createClippedBitmap(mask,
                    (mask.getWidth() - rw) / 2,
                    (mask.getHeight() - rh) / 2,
                    rw, rh);
            mask = BitmapUtils.scaleBitmap(mask, w, h);
            final Bitmap cropped = cropBitmapWithMask(photo, mask);

            imageView.setImageBitmap(cropped);
            Log.d("Test","test7");
        }*/
        photo = Bitmap.createScaledBitmap(photo, 512, 512, false);
        Log.d("Test", "reached");
        Log.d("Test", deeplabInterface+"");
        long result[][][] = deeplabInterface.segment(photo);

        int colors[] = new int[512*512];
        int flag[] = new int[4];
        for(int i=0;i<4;i++)
        {
            flag[i]=0;
        }
        for(int i=0;i<512*512;i++)
        {
            if(result[0][i][0]==1)
            {
                colors[i]= Color.RED;
                Log.d("color","true1");
                flag[1]++;
            }
            else if(result[0][i][0]==2)
            {
                colors[i]=Color.GREEN;
                Log.d("color","true2");
                flag[2]++;
            }
            else if(result[0][i][0]==3)
            {
                colors[i]=Color.BLUE;
                Log.d("color","true3");
                flag[3]++;
            }
            else
            {
                colors[i]=0;
                flag[0]++;
            }
        }

        int max=0;

        for(int i=0;i<4;i++)
        {
            if(max<flag[i])
            {
                max=i;
            }
        }

        if(max==0)
        {
            type.setText("Nothing much detected");
        }
        else if(max==1)
        {
            type.setText("Brick");
        }
        else if(max==2)
        {
            type.setText("Corrosion");
        }
        else if(max==3)
        {
            type.setText("Concrete");
        }

        Bitmap mask = Bitmap.createBitmap(colors,512,512, Bitmap.Config.ARGB_8888);
        Log.d("Bitmap",""+mask.getHeight());
        Log.d("Testing", mask + "");
        bmOverlay = Bitmap.createBitmap(photo.getWidth(), photo.getHeight(), photo.getConfig());
        Canvas canvas = new Canvas(bmOverlay);
        canvas.drawBitmap(photo, 0, 0, null);
        Log.d("Testing", bmOverlay + "");
        Log.d("Testing", canvas + "");

        try {
            canvas.drawBitmap(mask, 0, 0, null);
            imageView.setImageBitmap(bmOverlay);

            ByteArrayOutputStream stream1 = new ByteArrayOutputStream();
            photo.compress(Bitmap.CompressFormat.PNG, 100, stream1);
            byte[] photoByteArray = stream1.toByteArray();
            ByteArrayOutputStream stream2 = new ByteArrayOutputStream();
            bmOverlay.compress(Bitmap.CompressFormat.PNG, 100, stream2);
            byte[] bmOverlayByteArray = stream2.toByteArray();

            imageDatabaseHelper.insertData(photoByteArray,bmOverlayByteArray);

            Log.d("size_test",imageDatabaseHelper.getTotalItems()+"");
        }
        catch (RuntimeException e)
        {
            Toast.makeText(getActivity(), "Image size too large", Toast.LENGTH_SHORT).show();
        }

    }

   /* private Bitmap cropBitmapWithMask(Bitmap original, Bitmap mask) {
        if (original == null
                || mask == null) {
            return null;
        }

        final int w = original.getWidth();
        final int h = original.getHeight();
        if (w <= 0 || h <= 0) {
            return null;
        }

        Bitmap cropped = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);


        Canvas canvas = new Canvas(cropped);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        canvas.drawBitmap(original, 0, 0, null);
        canvas.drawBitmap(mask, 0, 0, paint);
        paint.setXfermode(null);

        return cropped;
    }*/
}
