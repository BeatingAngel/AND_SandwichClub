package com.udacity.sandwichclub;

import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.udacity.sandwichclub.model.Sandwich;
import com.udacity.sandwichclub.utils.JsonUtils;

import java.util.List;

public class DetailActivity extends AppCompatActivity {

    public static final String EXTRA_POSITION = "extra_position";
    private static final int DEFAULT_POSITION = -1;

    public ConstraintLayout mDetailConstraintLayout;
    public ImageView mImageIv;
    public TextView mAlsoKnownTv;
    public TextView mOriginTv;
    public TextView mIngredientsTv;
    public TextView mDescriptionTv;
    public TextView mAlsoKnownLabelTv;
    public TextView mOriginLabelTv;
    public TextView mIngredientsLabelTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mDetailConstraintLayout = findViewById(R.id.detail_constraint_layout);
        mImageIv = findViewById(R.id.image_iv);
        mAlsoKnownTv = findViewById(R.id.also_known_tv);
        mOriginTv = findViewById(R.id.origin_tv);
        mIngredientsTv = findViewById(R.id.ingredients_tv);
        mDescriptionTv = findViewById(R.id.description_tv);
        mAlsoKnownLabelTv = findViewById(R.id.also_known_label_tv);
        mOriginLabelTv = findViewById(R.id.origin_label_tv);
        mIngredientsLabelTv = findViewById(R.id.ingredients_label_tv);

        int position = DEFAULT_POSITION;
        Intent intent = getIntent();
        if (intent == null) {
            closeOnError();
        } else {
            position = intent.getIntExtra(EXTRA_POSITION, DEFAULT_POSITION);
        }

        if (position == DEFAULT_POSITION) {
            // EXTRA_POSITION not found in intent
            closeOnError();
            return;
        }

        String[] sandwiches = getResources().getStringArray(R.array.sandwich_details);
        String json = sandwiches[position];
        Sandwich sandwich = JsonUtils.parseSandwichJson(json);
        if (sandwich == null) {
            // Sandwich data unavailable
            closeOnError();
            return;
        }

        populateUI(sandwich);
        Picasso.with(this)
                .load(sandwich.getImage())
                .into(mImageIv);

        setTitle(sandwich.getMainName());
    }

    private void closeOnError() {
        finish();
        Toast.makeText(this, R.string.detail_error_message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Displays the data of a sandwich onto the screen.
     *
     * @param sandwich  the sandwich object to display.
     */
    private void populateUI(Sandwich sandwich) {

        // only display AlsoKnownAs if it exists OR ELSE remove it.
        if (sandwich.getAlsoKnownAs() != null) {
            mAlsoKnownTv.setText(listToStrEnumeration(sandwich.getAlsoKnownAs()));
        } else {
            mAlsoKnownLabelTv.setVisibility(View.GONE);
            mAlsoKnownTv.setVisibility(View.GONE);
        }

        // only display PlaceOfOrigin if it exists OR ELSE remove it.
        if (sandwich.getPlaceOfOrigin() != null) {
            mOriginTv.setText(sandwich.getPlaceOfOrigin());
        } else {
            mOriginLabelTv.setVisibility(View.GONE);
            mOriginTv.setVisibility(View.GONE);
        }

        mIngredientsTv.setText(listToStrEnumeration(sandwich.getIngredients()));
        mDescriptionTv.setText(sandwich.getDescription());

        // If the AlsoKnownAs-field (and Origin-field) is gone then correct the Top-Margin.
        if (mAlsoKnownLabelTv.getVisibility() == View.GONE
                && mOriginLabelTv.getVisibility() == View.GONE) {
            correctMargin(mIngredientsLabelTv);
        } else if (mAlsoKnownLabelTv.getVisibility() == View.GONE) {
            correctMargin(mOriginLabelTv);
        }

    }

    /**
     * Convert a List into a readable String.
     *
     * @param list  the list to convert into a string.
     * @return      the list as a readable string.
     */
    private String listToStrEnumeration(List<String> list) {
        int listSize = list.size();
        int positionBeforeAnd = listSize - 2;
        int positionOfLastItem = listSize - 1;

        // StringBuilder used because of the performance.
        StringBuilder strBuilder = new StringBuilder();

        for (int i = 0; i < listSize; i++) {
            strBuilder.append(list.get(i));

            if (i < positionBeforeAnd) {
                strBuilder.append(", ");
            } else if (i < positionOfLastItem) {
                strBuilder.append(" and ");
            }
        }

        return strBuilder.toString();
    }

    /**
     * connects a TextView below the image with a specific margin.
     * This is used to correct margins, if a TextView is gone.
     *
     * @param textView  the textView to connect to the image and correct margin.
     */
    private void correctMargin(TextView textView) {
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(mDetailConstraintLayout);
        constraintSet.connect(textView.getId(), ConstraintSet.TOP,
                mImageIv.getId(), ConstraintSet.BOTTOM,
                (int) getResources().getDimension(R.dimen.top_margin_to_image));
        constraintSet.applyTo(mDetailConstraintLayout);
    }
}
