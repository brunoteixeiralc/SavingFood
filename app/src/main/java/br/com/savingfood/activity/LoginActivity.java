package br.com.savingfood.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import br.com.savingfood.R;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by brunolemgruber on 10/07/17.
 */

public class LoginActivity extends AppCompatActivity {

    private Button btnEnter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btnEnter = (Button) findViewById(R.id.btn_enter);
        btnEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadMainActivity();
            }
        });
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    private void loadMainActivity(){

        Intent intent = new Intent(LoginActivity.this,WizardActivity.class);
        startActivity(intent);
    }
}
