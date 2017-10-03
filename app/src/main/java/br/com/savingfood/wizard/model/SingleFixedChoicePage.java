package br.com.savingfood.wizard.model;

import android.support.v4.app.Fragment;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import br.com.savingfood.wizard.ui.SingleChoiceFragment;

/**
 * Created by Ronan Lima on 16/02/2016.
 */
public class SingleFixedChoicePage extends Page {
    private ArrayList<String> mChoices = new ArrayList<String>();

    public SingleFixedChoicePage(ModelCallbacks callbacks, String title) {
        super(callbacks, title);
    }

    @Override
    public Fragment createFragment() {
        return SingleChoiceFragment.create(getKey());
    }

    public String getOptionAt(int position) {
        return getmChoices().get(position);
    }

    public int getOptionCount() {
        return getmChoices().size();
    }

    @Override
    public void getReviewItems(ArrayList<ReviewItem> dest) {
        dest.add(new ReviewItem(getTitle(), mData.getString(SIMPLE_DATA_KEY), getKey()));
    }

    @Override
    public boolean isCompleted() {
        return !TextUtils.isEmpty(mData.getString(SIMPLE_DATA_KEY));
    }

    public SingleFixedChoicePage setChoices(String... choices) {
        getmChoices().addAll(Arrays.asList(choices));
        return this;
    }

    public SingleFixedChoicePage setChoices(List<String> choices){
        for (String item: choices) {
            getmChoices().add(item);
        }
        return this;
    }

    public SingleFixedChoicePage setValue(String value) {
        mData.putString(SIMPLE_DATA_KEY, value);
        return this;
    }

    public ArrayList<String> getmChoices() {
        return mChoices;
    }
}