package com.appsaga.strudoc;

import android.content.Context;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class FragmentAdapter extends FragmentPagerAdapter {

    private Context mContext;

    public FragmentAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {

        if (position == 0) {
            return new MessageFragment();
        }
        else if (position == 1) {
            return new PhotoFragment();
        }
        else if (position == 2) {
            return new SavePictureFragment();
        }
        else if(position==3)
        {
            return new SharePictureFragment();
        }
        else
        {
            return new DeleteFragment();
        }
    }

    @Override
    public int getCount() {
        return 5;
    }
}
