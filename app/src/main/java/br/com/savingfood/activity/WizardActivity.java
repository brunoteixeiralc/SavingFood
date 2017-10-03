package br.com.savingfood.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.List;

import br.com.savingfood.R;
import br.com.savingfood.wizard.WizardQuestion;
import br.com.savingfood.wizard.model.AbstractWizardModel;
import br.com.savingfood.wizard.model.ModelCallbacks;
import br.com.savingfood.wizard.model.Page;
import br.com.savingfood.wizard.ui.PageFragmentCallbacks;
import br.com.savingfood.wizard.ui.ReviewFragment;
import br.com.savingfood.wizard.ui.StepPagerStrip;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class WizardActivity extends AppCompatActivity implements PageFragmentCallbacks
        , ReviewFragment.Callbacks, ModelCallbacks {
    private static final String welComeWizard = "wizardDisplayed";

    private AbstractWizardModel wizardModel = new WizardQuestion(this);
    private MyPagerAdapter pagerAdapter;
    private List<Page> listaPaginasWizard;
    private ViewPager viewPager;
    private StepPagerStrip stepPage;
    public Toolbar toolbar;
    private Button prevButton, nextButton;
    private boolean consumePageSelectedEvent, editingAfterReview;
    private SharedPreferences myPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        myPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        Boolean wizardDisplayed = myPrefs.getBoolean(welComeWizard, false);

        gerenciaChamadaWizard(savedInstanceState);

    }

    private void gerenciaChamadaWizard(Bundle savedInstanceState) {
        setContentView(R.layout.fragment_instruction);

        wizardModel.registerListener(this);
        pagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(pagerAdapter);
        stepPage = (StepPagerStrip) findViewById(R.id.strip);
        stepPage.setOnPageSelectedListener(new StepPagerStrip.OnPageSelectedListener() {
            @Override
            public void onPageStripSelected(int position) {
                position = Math.min(pagerAdapter.getCount() - 1, position);
                if (position != viewPager.getCurrentItem()) {
                    viewPager.setCurrentItem(position);
                }
            }
        });

        prevButton = (Button) findViewById(R.id.prev_button);
        nextButton = (Button) findViewById(R.id.next_button);

        viewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                stepPage.setCurrentPage(position);

                if (consumePageSelectedEvent) {
                    consumePageSelectedEvent = false;
                    return;
                }
                editingAfterReview = false;
                updateBottomBar();
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewPager.getCurrentItem() == listaPaginasWizard.size()) {
                    addWizardInSharedPreferences();
                    Intent i = new Intent(getApplicationContext(), MainActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                    finish();
                } else {
                    if (editingAfterReview) {
                        viewPager.setCurrentItem(pagerAdapter.getCount() - 1);
                    } else {
                        viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
                    }
                }
            }
        });

        prevButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
            }
        });

        if (savedInstanceState != null) {
            wizardModel.load(savedInstanceState.getBundle("model"));

        }

        onPageTreeChanged();
        updateBottomBar();

    }

    private void addWizardInSharedPreferences(){
        SharedPreferences.Editor editor = myPrefs.edit();
        editor.putBoolean(welComeWizard, true);
        editor.commit();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBundle("model", wizardModel.save());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        wizardModel.unregisterListener(this);
    }

    private void updateBottomBar() {
        int position = viewPager.getCurrentItem();
        if (position == listaPaginasWizard.size()){
            nextButton.setText(R.string.txt_botao_fim_questionario);
        } else {
            nextButton.setText(editingAfterReview ? R.string.txt_botao_revisao_informacoes : R.string.txt_botao_proximo);
            TypedValue value = new TypedValue();
            getTheme().resolveAttribute(android.R.attr.textAppearanceMedium, value, true);
            nextButton.setEnabled(position != pagerAdapter.getCutOffPage());

        }
        prevButton.setVisibility(position <= 0 ? View.INVISIBLE : View.VISIBLE);
    }

    @Override
    public void onPageDataChanged(Page page) {
        if (page.isRequired()){
            if (recalculateCutOffPage()) {
                pagerAdapter.notifyDataSetChanged();
                updateBottomBar();
            }
        }
    }

    private boolean recalculateCutOffPage() {
        int cutOffPage = listaPaginasWizard.size() + 1;
        for (int i = 0 ; i < listaPaginasWizard.size(); i++) {
            Page page = listaPaginasWizard.get(i);
            if (page.isRequired() && !page.isCompleted()){
                cutOffPage = i;
                break;
            }
        }

        if (pagerAdapter.getCutOffPage() != cutOffPage){
            pagerAdapter.setCutOffPage(cutOffPage);
            return true;
        }
        return false;
    }

    @Override
    public void onPageTreeChanged() {
        listaPaginasWizard = wizardModel.getCurrentPageSequence();
        recalculateCutOffPage();
        stepPage.setPageCount(listaPaginasWizard.size() + 1);
        pagerAdapter.notifyDataSetChanged();
        updateBottomBar();
    }

    @Override
    public Page onGetPage(String key) {
        return wizardModel.findByKey(key);
    }

    @Override
    public AbstractWizardModel onGetModel() {
        return wizardModel;
    }

    @Override
    public void onEditScreenAfterReview(String pageKey) {
        for (int i = listaPaginasWizard.size() -1; i >= 0; i--){
            if (listaPaginasWizard.get(i).getKey().equals(pageKey)){
                consumePageSelectedEvent = true;
                editingAfterReview = true;
                viewPager.setCurrentItem(i);
                updateBottomBar();
                break;
            }
        }
    }

    public class MyPagerAdapter extends FragmentStatePagerAdapter {
        private int cutOffPage;
        private Fragment mPrimaryItem;

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position >= listaPaginasWizard.size() ){
                return new ReviewFragment();
            }
            return listaPaginasWizard.get(position).createFragment();
        }

        @Override
        public int getItemPosition(Object object) {
            if (object == mPrimaryItem){
                return POSITION_UNCHANGED;
            }
            return POSITION_NONE;
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            super.setPrimaryItem(container, position, object);
            mPrimaryItem = (Fragment) object;
        }

        @Override
        public int getCount() {
            if (listaPaginasWizard == null){
                return 0;
            }
            return Math.min(cutOffPage + 1, listaPaginasWizard.size() + 1);
        }

        public void setCutOffPage(int cutOffPage) {
            if (cutOffPage < 0) {
                cutOffPage = Integer.MAX_VALUE;
            }
            this.cutOffPage = cutOffPage;
        }

        public int getCutOffPage() {
            return this.cutOffPage;
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

}
