package com.lezhi.beziercure;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class ShoppingCartActivity extends AppCompatActivity {

    private TextView mShoppingcountView;
    private ImageView mShoppingCart;
    private PathMeasure mPathMeasure;
    private RecyclerView mRecyclerView;
    private RelativeLayout mRlLayout;
    private int mShoppingcount = 0;
    private float[] mCurrentPosition = new float[2];
    private ArrayList<Bitmap> mBitmapList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_cart);
        initView();
        initImg();
        initAdapter();
    }

    private void initView() {
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mShoppingCart = (ImageView) findViewById(R.id.iv_shoppingcart);
        mRlLayout = (RelativeLayout) findViewById(R.id.rl);
        mShoppingcountView = (TextView) findViewById(R.id.count);
    }

    private void initImg() {
        mBitmapList.add(BitmapFactory.decodeResource(getResources(), R.drawable.coin));
        mBitmapList.add(BitmapFactory.decodeResource(getResources(), R.drawable.coin1));
        mBitmapList.add(BitmapFactory.decodeResource(getResources(), R.drawable.coin91));
    }

    private void initAdapter() {
        MyAdapter myAdapter = new MyAdapter(mBitmapList);
        mRecyclerView.setAdapter(myAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(ShoppingCartActivity.this));
    }

    private void addShoppingCart(ImageView clickGood) {
        ImageView currentGood = new ImageView(ShoppingCartActivity.this);
        currentGood.setImageDrawable(clickGood.getDrawable());
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(100, 100);
        mRlLayout.addView(currentGood, params);

        getPathMeasure(clickGood);

        startAnimater(currentGood);
    }

    private void getPathMeasure(ImageView mClickGood) {
        int parentLocation[] = new int[2];
        int startLoc[] = new int[2];
        int endLoc[] = new int[2];
        mRlLayout.getLocationInWindow(parentLocation);
        mClickGood.getLocationInWindow(startLoc);
        mShoppingCart.getLocationInWindow(endLoc);

        float startX = startLoc[0] - parentLocation[0] + mClickGood.getWidth() / 2;
        float startY = startLoc[1] - parentLocation[1] + mClickGood.getHeight() / 2;
        float toX = endLoc[0] - parentLocation[0] + mShoppingCart.getWidth() / 5;
        float toY = endLoc[1] - parentLocation[1];

        Path path = new Path();
        path.moveTo(startX, startY);
        path.quadTo((startX + toX) / 2, startY, toX, toY);
        mPathMeasure = new PathMeasure(path, false);
    }

    private void startAnimater(final ImageView currentGood) {
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, mPathMeasure.getLength());
        valueAnimator.setDuration(1000);
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (Float) animation.getAnimatedValue();
                mPathMeasure.getPosTan(value, mCurrentPosition, null);
                currentGood.setTranslationX(mCurrentPosition[0]);
                currentGood.setTranslationY(mCurrentPosition[1]);
            }
        });
        valueAnimator.start();
        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mShoppingcount++;
                mShoppingcountView.setText(String.valueOf(mShoppingcount));
                mRlLayout.removeView(currentGood);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

    }

    class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {
        private ArrayList<Bitmap> mCurrentBitmapList;

        public MyAdapter(ArrayList<Bitmap> bitmapList) {
            this.mCurrentBitmapList = bitmapList;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(ShoppingCartActivity.this);
            View itemView = inflater.inflate(R.layout.item, parent, false);
            MyViewHolder myViewHolder = new MyViewHolder(itemView);
            return myViewHolder;
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int position) {
            holder.mIvGoods.setImageBitmap(mCurrentBitmapList.get(position));
            holder.mTVbuy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addShoppingCart(holder.mIvGoods);
                }
            });
        }

        @Override
        public int getItemCount() {
            return mCurrentBitmapList.size();
        }
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        private ImageView mIvGoods;
        private TextView mTVbuy;

        public MyViewHolder(View itemView) {
            super(itemView);
            mIvGoods = (ImageView) itemView.findViewById(R.id.iv_goods);
            mTVbuy = (TextView) itemView.findViewById(R.id.tv_buy);
        }
    }

}
