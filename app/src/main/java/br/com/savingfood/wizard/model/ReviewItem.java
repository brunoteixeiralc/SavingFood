package br.com.savingfood.wizard.model;

/**
 * Created by Ronan Lima on 16/02/2016.
 */
public class ReviewItem {
    public static final int DEFAULT_WEIGHT = 0;

    private int mWeight;
    private String mTitle;
    private String mDisplayValue;
    private String mPageKey;

    public ReviewItem(String title, String displayValue, String pageKey){
        this(title, displayValue, pageKey, DEFAULT_WEIGHT);
    }

    public ReviewItem(String title, String displayValue, String pageKey, int weight){
        mTitle = title;
        mDisplayValue = displayValue;
        mPageKey = pageKey;
        mWeight = weight;
    }

    public static int getDefaultWeight() {
        return DEFAULT_WEIGHT;
    }

    public int getmWeight() {
        return mWeight;
    }

    public void setmWeight(int mWeight) {
        this.mWeight = mWeight;
    }

    public String getmTitle() {
        return mTitle;
    }

    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public String getmDisplayValue() {
        return mDisplayValue;
    }

    public void setmDisplayValue(String mDisplayValue) {
        this.mDisplayValue = mDisplayValue;
    }

    public String getmPageKey() {
        return mPageKey;
    }

    public void setmPageKey(String mPageKey) {
        this.mPageKey = mPageKey;
    }
}
