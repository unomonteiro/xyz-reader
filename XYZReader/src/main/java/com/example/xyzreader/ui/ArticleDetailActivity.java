package com.example.xyzreader.ui;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.widget.ImageView;

import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.example.xyzreader.R;
import com.example.xyzreader.data.ArticleLoader;
import com.example.xyzreader.data.ItemsContract;
import com.example.xyzreader.util.ColorUtils;
import com.example.xyzreader.util.GlideApp;

import static com.bumptech.glide.load.engine.DiskCacheStrategy.RESOURCE;

/**
 * An activity representing a single Article detail screen, letting you swipe between articles.
 */
public class ArticleDetailActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int LOADER_ID_ARTICLE_PAGES = 2;

    private static final String KEY_SELECTED_ITEM_ID = "selected_item_id";
    static final String KEY_PALETTE_COLOR = "KEY_PALETTE_COLOR";
    private Cursor mCursor;
    private long mStartId;

    private long mSelectedItemId;
    private ImageView mPhotoView;

    private ViewPager mPager;
    private MyPagerAdapter mPagerAdapter;
    private CollapsingToolbarLayout mCollapsingToolbar;
    private ActionBar mActionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_detail);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        mActionBar = getSupportActionBar();
        if (mActionBar != null) {
            mActionBar.setDisplayHomeAsUpEnabled(true);
        }

        getSupportLoaderManager().initLoader(LOADER_ID_ARTICLE_PAGES, null, this);
        mCollapsingToolbar = findViewById(R.id.toolbar_layout);

        mPhotoView = mCollapsingToolbar.findViewById(R.id.photo);

        mPagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
        mPager = findViewById(R.id.pager);
        mPager.setAdapter(mPagerAdapter);
        mPager.setPageMargin((int) TypedValue
                .applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, getResources().getDisplayMetrics()));
        mPager.setPageMarginDrawable(new ColorDrawable(0x22000000));

        mPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                if (mCursor != null) {
                    mCursor.moveToPosition(position);
                    mSelectedItemId = mCursor.getLong(ArticleLoader.Query._ID);
                    updateCollapsingToolbar();
                }
            }
        });

        if (savedInstanceState == null) {
            Intent intent = getIntent();
            if (intent != null && intent.getData() != null) {
                mStartId = ItemsContract.Items.getItemId(getIntent().getData());
                mSelectedItemId = mStartId;
            }
        }
    }

    private void updateCollapsingToolbar() {
        mCollapsingToolbar.setTitle(mCursor.getString(ArticleLoader.Query.TITLE));
        GlideApp.with(this)
                .asBitmap()
                .load(mCursor.getString(ArticleLoader.Query.PHOTO_URL))
                .override(Target.SIZE_ORIGINAL)
                .diskCacheStrategy(RESOURCE)
                .into(new BitmapImageViewTarget(mPhotoView) {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        super.onResourceReady(resource, transition);
                        onPalette(Palette.from(resource).generate());
                        //mPhotoView.setImageBitmap(resource);
                    }

                    void onPalette(Palette palette) {
                        if (null != palette) {
                            int darkVibrantColor = palette.getDarkVibrantColor(
                                    palette.getDarkMutedColor(
                                            getResources().getColor(
                                                    R.color.default_dark_vibrant)));
                            updateCollapsingToolbarColor(darkVibrantColor);
                        }
                    }
                });
    }

    private void updateCollapsingToolbarColor(int darkColor) {
        //set color action bar
//            mActionBar.setBackgroundDrawable();
        //set color status bar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ColorUtils.manipulateColor(darkColor, 0.60f));
        }
        //new ColorDrawable(ColorUtils.manipulateColor(darkColor, 0.62f));
        mCollapsingToolbar.setBackgroundColor(darkColor);
        mCollapsingToolbar.setStatusBarScrimColor(darkColor);
        mCollapsingToolbar.setContentScrimColor(darkColor);
        mCollapsingToolbar.setExpandedTitleColor(ContextCompat.getColor(ArticleDetailActivity.this,
                R.color.white_transparent));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(KEY_SELECTED_ITEM_ID, mSelectedItemId);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle bundle) {
        switch (loaderId){
            case LOADER_ID_ARTICLE_PAGES:
                return ArticleLoader.newAllArticlesInstance(this);
            default:
                throw new RuntimeException("Loader Not Implemented: " + loaderId);
        }
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> cursorLoader, Cursor cursor) {

        int loaderId = cursorLoader.getId();
        switch (loaderId){
            case LOADER_ID_ARTICLE_PAGES:
                mCursor = cursor;
                mPagerAdapter.notifyDataSetChanged();
                // Select the pager CurrentItem
                if (mSelectedItemId > 0) {
                    mCursor.moveToPosition(-1);
                    while (mCursor.moveToNext()) {
                        if (mCursor.getLong(ArticleLoader.Query._ID) == mSelectedItemId) {
                            final int position = mCursor.getPosition();
                            mPager.setCurrentItem(position, false);
                            updateCollapsingToolbar();
                            break;
                        }
                    }
                    mStartId = 0;
                }
                break;
            default:
                throw new RuntimeException("Loader Not Implemented: " + loaderId);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> cursorLoader) {
        int loaderId = cursorLoader.getId();
        switch (loaderId){
            case LOADER_ID_ARTICLE_PAGES:
                mCursor = null;
                mPagerAdapter.notifyDataSetChanged();
                break;
            default:
                throw new RuntimeException("Loader Not Implemented: " + loaderId);
        }
    }

    private class MyPagerAdapter extends FragmentStatePagerAdapter {

        MyPagerAdapter(FragmentManager fm) {
            super(fm);
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
    }
}
