package com.kana2011.mcshop.shop;

import android.animation.Animator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.transition.Transition;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.kana2011.mcshop.R;
import com.kana2011.mcshop.anim.ReverseInterpolator;
import com.kana2011.mcshop.libs.McShop;
import com.kana2011.mcshop.libs.ObservableScrollView;
import com.kana2011.mcshop.libs.OnScrollChangedCallback;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class ShopDetailActivity extends ActionBarActivity implements OnScrollChangedCallback {
    private View mContentView;
    private Toolbar mToolbar;
    private ImageView mItemPhoto;
    private TextView mItemName;
    private JSONObject itemInfo;
    private Button mBuyButton;
    private View mStatusBarTint;
    private Handler mHandler;
    private int mLastDampedScroll;
    private String itemName;
    private ObservableScrollView mScrollView;
    private boolean toolbarIsTranslucent;
    private boolean statusBarIsTranslucent;
    private Transition.TransitionListener mTransitionListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_detail);
        mContentView = findViewById(android.R.id.content);

        mHandler = new Handler(Looper.getMainLooper());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

            Transition enterTransition = getWindow().getSharedElementEnterTransition();
            mTransitionListener = new Transition.TransitionListener() {
                @Override
                public void onTransitionStart(Transition transition) {
                }

                @Override
                public void onTransitionEnd(Transition transition) {
                    animateRevealShow(findViewById(R.id.item_info), findViewById(R.id.item_photo));
                }

                @Override
                public void onTransitionCancel(Transition transition) {
                }

                @Override
                public void onTransitionPause(Transition transition) {
                }

                @Override
                public void onTransitionResume(Transition transition) {
                }
            };
            enterTransition.addListener(mTransitionListener);
        } else {
            findViewById(R.id.item_info).setVisibility(View.VISIBLE);
        }

        itemInfo = null;
        JSONParser parser = new JSONParser();
        try {
            itemInfo = (JSONObject)parser.parse(getIntent().getStringExtra("itemInfo"));
        } catch(Exception e) {

        }

        itemName = (String)itemInfo.get("dispname");

        mToolbar = (Toolbar)findViewById(R.id.app_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mStatusBarTint = (View)findViewById(R.id.tint);

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();

        final float dpWidth = displayMetrics.widthPixels;

        mItemPhoto = (ImageView)findViewById(R.id.item_photo);
        mItemPhoto.getLayoutParams().width = (int)dpWidth;
        mItemPhoto.getLayoutParams().height = (int)dpWidth * 4 / 7;

        mItemName = (TextView)findViewById(R.id.item_name);
        mItemName.setText(itemName);

        mScrollView = (ObservableScrollView)findViewById(R.id.scroll_view);
        mScrollView.setOnScrollChangedCallback(this);

        final ShopDetailActivity context = this;
        mBuyButton = (Button)findViewById(R.id.buy_button);
        if((int)(long)itemInfo.get("price") != 0) {
            mBuyButton.setText(McShop.getCurrencyUnit() + Integer.toString((int) (long) itemInfo.get("price")));
        }
        mBuyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                McShop.Shop.buy(context, (int) (long) itemInfo.get("id"), (String) itemInfo.get("dispname"));
            }
        });

        ViewCompat.setTransitionName(mItemPhoto, "item_photo");

        toolbarIsTranslucent = true;
        statusBarIsTranslucent = true;

        onScroll(-1, 0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_shop_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                back();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static void launch(Activity activity, View photoView, JSONObject itemInfo) {
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, Pair.create(photoView, "item_photo"));
        Intent intent = new Intent(activity, ShopDetailActivity.class);
        intent.putExtra("itemInfo", itemInfo.toString());
        intent.putExtra("showExitAnimation", true);
        ActivityCompat.startActivity(activity, intent, options.toBundle());
    }

    private void animateRevealShow(View showView, View sourceView) {
        int cx = (showView.getLeft() + showView.getRight()) / 2;
        int cy = 0 - (sourceView.getHeight() / 4);

        int finalRadius = Math.max(showView.getWidth(), showView.getHeight());

        Animator anim =
                ViewAnimationUtils.createCircularReveal(showView, cx, cy, 0, finalRadius);

        showView.setVisibility(View.VISIBLE);
        anim.start();
    }

    private void animateRevealHide(View showView, View sourceView) {
        int cx = (showView.getLeft() + showView.getRight()) / 2;
        int cy = 0 - (sourceView.getHeight() / 4);

        int finalRadius = Math.max(showView.getWidth(), showView.getHeight());

        Animator anim =
                ViewAnimationUtils.createCircularReveal(showView, cx, cy, 0, finalRadius);
        anim.setInterpolator(new ReverseInterpolator());

        anim.start();
    }

    private void animateDecelerateShow(View showView) {
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.zoomin);
        anim.setInterpolator(new DecelerateInterpolator());
        anim.setDuration(200);
        showView.setVisibility(View.VISIBLE);
        showView.startAnimation(anim);
    }

    private void animateAccelerateHide(View showView) {
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.zoomout);
        anim.setInterpolator(new AccelerateInterpolator());
        anim.setDuration(200);
        showView.setVisibility(View.INVISIBLE);
        showView.startAnimation(anim);
    }

    @Override
    public void onBackPressed() {
        back();
    }

    private void back() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().getSharedElementEnterTransition().removeListener(mTransitionListener);
            animateRevealHide(findViewById(R.id.item_info), findViewById(R.id.item_photo));
            final ShopDetailActivity activity = this;
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    findViewById(R.id.item_info).setVisibility(View.INVISIBLE);
                    activity.supportFinishAfterTransition();
                    mHandler.postDelayed(this, 300);
                }
            }, 300);
        } else {
            finish();
        }
    }

    @Override
    public void onScroll(int l, int t) {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels;
        int appBarSize = mToolbar.getHeight();
        int statusBarSize = getResources().getDimensionPixelSize(R.dimen.app_bar_top_padding);
        int calculatedScroll = (int)(t + statusBarSize + appBarSize - mItemPhoto.getLayoutParams().height);
        updateToolbar(calculatedScroll);
        updateStatusBarTint(calculatedScroll);
        updateParallaxEffect(t);
    }

    private void updateToolbar(int calculatedScroll) {
        if (Build.VERSION.SDK_INT >= 16) {
            if (calculatedScroll >= 0) {
                if (toolbarIsTranslucent) {
                    mToolbar.setBackgroundResource(R.drawable.fading_toolbar_background);
                    TransitionDrawable transition = (TransitionDrawable) mToolbar.getBackground();
                    transition.startTransition(200);
                    toolbarIsTranslucent = false;
                }
                mToolbar.setTitle(itemName);
            } else {
                if (!toolbarIsTranslucent) {
                    mToolbar.setBackgroundResource(R.drawable.fading_toolbar_background);
                    TransitionDrawable transition = (TransitionDrawable) mToolbar.getBackground();
                    transition.reverseTransition(200);
                    toolbarIsTranslucent = true;
                }
                mToolbar.setTitle("");
            }
        }
    }

    private void updateStatusBarTint(int calculatedScroll) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if(calculatedScroll >= 0) {
                if(statusBarIsTranslucent) {
                    mStatusBarTint.setBackgroundResource(R.drawable.fading_status_bar_background);
                    TransitionDrawable transition = (TransitionDrawable) mStatusBarTint.getBackground();
                    transition.startTransition(200);
                    statusBarIsTranslucent = false;
                }
            } else {
                if(!statusBarIsTranslucent) {
                    mStatusBarTint.setBackgroundResource(R.drawable.fading_status_bar_background);
                    TransitionDrawable transition = (TransitionDrawable) mStatusBarTint.getBackground();
                    transition.reverseTransition(200);
                    statusBarIsTranslucent = true;
                }
            }
        }
    }

    private void updateParallaxEffect(int scrollPosition) {
        float damping = 0.5f;
        int dampedScroll = (int) (scrollPosition * damping);
        int offset = mLastDampedScroll - dampedScroll;
        mItemPhoto.offsetTopAndBottom(-offset);

        mLastDampedScroll = dampedScroll;
    }

    public View getContentView() {
        return mContentView;
    }

}
