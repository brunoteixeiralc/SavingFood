package br.com.savingfood.wizard.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import br.com.savingfood.R;
import br.com.savingfood.wizard.model.InstructionPage;
import br.com.savingfood.wizard.model.Page;

/**
 * Created by Ronan Lima on 16/02/2016.
 */
public class InstructionFragment extends Fragment {
    private static final String ARG_KEY = "key";

    private PageFragmentCallbacks mCallbacks;
    private List<String> mChoices;
    private String mKey;
    private Page mPage;

    public static InstructionFragment create(String key) {
        Bundle args = new Bundle();
        args.putString(ARG_KEY, key);

        InstructionFragment fragment = new InstructionFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public InstructionFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        mKey = args.getString(ARG_KEY);
        mPage = mCallbacks.onGetPage(mKey);

        InstructionPage fixedChoicePage = (InstructionPage) mPage;
        mChoices = new ArrayList<String>();
        for (int i = 0; i < fixedChoicePage.getOptionCount(); i++) {
            mChoices.add(fixedChoicePage.getOptionAt(i));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.complement_instruction_fragment, container, false);
        ((TextView) rootView.findViewById(R.id.tituloPagina1)).setText("Seja Bem Vindo");
        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (!(activity instanceof PageFragmentCallbacks)) {
            throw new ClassCastException("Activity must implement PageFragmentCallbacks");
        }

        mCallbacks = (PageFragmentCallbacks) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }
}
