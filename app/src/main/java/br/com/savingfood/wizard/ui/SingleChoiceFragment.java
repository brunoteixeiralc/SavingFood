package br.com.savingfood.wizard.ui;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import br.com.savingfood.R;
import br.com.savingfood.wizard.model.Page;
import br.com.savingfood.wizard.model.SingleFixedChoicePage;

/**
 * Created by Ronan Lima on 16/02/2016.
 */
public class SingleChoiceFragment extends ListFragment {
    private static final String ARG_KEY = "key";
    private PageFragmentCallbacks mCallbacks;
    private List<String> mChoices;
    private String mKey;
    private Page mPage;

    public static SingleChoiceFragment create(String key){
        Bundle args = new Bundle();
        args.putString(ARG_KEY, key);

        SingleChoiceFragment fragment = new SingleChoiceFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public SingleChoiceFragment(){
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        mKey = args.getString(ARG_KEY);
        mPage = mCallbacks.onGetPage(mKey);

        SingleFixedChoicePage fixedChoicePage = (SingleFixedChoicePage) mPage;
        mChoices = new ArrayList<String>();
        for(int i = 0; i < fixedChoicePage.getOptionCount(); i++){
            mChoices.add(fixedChoicePage.getOptionAt(i));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_page, container, false);
        ((TextView) rootView.findViewById(android.R.id.title)).setText(mPage.getTitle());

        final ListView listView = (ListView) rootView.findViewById(android.R.id.list);
        setListAdapter(new ArrayAdapter<String>(getActivity(), R.layout.single_choice_checked, android.R.id.text1, mChoices));
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                String selection = mPage.getData().getString(Page.SIMPLE_DATA_KEY);
                for(int i = 0; i < mChoices.size(); i++){
                    if (mChoices.get(i).equals(selection)){
                        listView.setItemChecked(i, true);
                        break;
                    }
                }
            }
        });

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if(!(activity instanceof PageFragmentCallbacks)){
            throw new ClassCastException("Activity must implements PageFragmentCallbacks");
        }

        mCallbacks = (PageFragmentCallbacks) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        mPage.getData().putString(Page.SIMPLE_DATA_KEY, getListAdapter().getItem(position).toString());
        mPage.notifyDataChanged();
    }
}
