package com.example.quizapp.activities;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import androidx.cardview.widget.CardView;

import com.example.quizapp.R;

public class AvatarAdapter extends BaseAdapter {
    private Context context;
    private int[] avatarResources;
    private int selectedIndex;

    public AvatarAdapter(Context context, int[] avatarResources, int selectedIndex) {
        this.context = context;
        this.avatarResources = avatarResources;
        this.selectedIndex = selectedIndex;
    }

    public void setSelectedIndex(int index) {
        this.selectedIndex = index;
    }

    @Override
    public int getCount() {
        return avatarResources.length;
    }

    @Override
    public Object getItem(int position) {
        return avatarResources[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CardView cardView;
        ImageView imageView;

        if (convertView == null) {
            // Create CardView wrapper
            cardView = new CardView(context);
            cardView.setRadius(50);
            cardView.setCardElevation(8);
            cardView.setUseCompatPadding(true);

            // Create ImageView
            imageView = new ImageView(context);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

            // Set layout params
            CardView.LayoutParams params = new CardView.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    200 // Height in pixels
            );
            imageView.setLayoutParams(params);

            cardView.addView(imageView);
        } else {
            cardView = (CardView) convertView;
            imageView = (ImageView) cardView.getChildAt(0);
        }

        // Set avatar image
        imageView.setImageResource(avatarResources[position]);

        // Highlight selected avatar
        if (position == selectedIndex) {
            cardView.setCardBackgroundColor(context.getResources().getColor(R.color.purple_200));
            cardView.setCardElevation(16);
        } else {
            cardView.setCardBackgroundColor(context.getResources().getColor(android.R.color.white));
            cardView.setCardElevation(8);
        }

        return cardView;
    }
}