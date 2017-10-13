package br.com.savingfood.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;

import br.com.savingfood.R;
import br.com.savingfood.model.User;
import br.com.savingfood.utils.Utils;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


/**
 * Created by brunolemgruber on 14/07/16.
 */

public class IntroActivity extends AppCompatActivity {

    private Button btnEnter;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wizard1);

        mAuth = FirebaseAuth.getInstance();

        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                AuthCredential credential = FacebookAuthProvider
                        .getCredential(loginResult.getAccessToken().getToken());
                signInCredential(credential);
            }

            @Override
            public void onCancel() {
                Utils.openSnack(IntroActivity.this.findViewById(android.R.id.content),"Login cancelado.");

            }

            @Override
            public void onError(FacebookException error) {
                Utils.openSnack(IntroActivity.this.findViewById(android.R.id.content), "Erro ao fazer login.");
            }
        });

        btnEnter = (Button) findViewById(R.id.btn_enter);
        btnEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               LoginManager.getInstance().logInWithReadPermissions(IntroActivity.this,
                        Arrays.asList("public_profile", "email"));
            }
        });
    }

    private void signInCredential(AuthCredential credential)
    {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                        {
                            FirebaseUser usuario = mAuth.getCurrentUser();
                            Intent intent = new Intent(IntroActivity.this,WizardActivity.class);
                            startActivity(intent);

                        }
                        else
                        {
                            Utils.openSnack(IntroActivity.this.findViewById(android.R.id.content), "Erro ao fazer login.");
                        }
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onStart() {
        super.onStart();
        currentUser = mAuth.getCurrentUser();
        Log.d("savingfood" , currentUser.getUid() + " " + currentUser.getDisplayName());
    }
}
