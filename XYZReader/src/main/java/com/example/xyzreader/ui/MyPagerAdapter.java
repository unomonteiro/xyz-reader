package com.example.xyzreader.ui;

import android.database.Cursor;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.xyzreader.data.ArticleLoader;

class MyPagerAdapter extends FragmentStatePagerAdapter {

    private Cursor mCursor;

    MyPagerAdapter(Cursor cursor, FragmentManager fm) {
        super(fm);
        mCursor = cursor;
    }

    @Override
    public Fragment getItem(int position) {
        mCursor.moveToPosition(position);
        return ArticleDetailFragment.newInstance(mCursor.getLong(ArticleLoader.Query._ID));
    }

    @Override
    public int getCount() {
        return (mCursor != null) ? mCursor.getCount() : 0;
    }

    public void setCursor(Cursor cursor) {
        mCursor = cursor;
        notifyDataSetChanged();
    }
}
