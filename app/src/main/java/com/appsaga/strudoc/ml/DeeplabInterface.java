package com.appsaga.strudoc.ml;

import android.content.Context;
import android.graphics.Bitmap;

public interface DeeplabInterface {

    boolean initialize(Context context);

    boolean isInitialized();

    int getInputSize();

    long[][][] segment(Bitmap bitmap);

}


