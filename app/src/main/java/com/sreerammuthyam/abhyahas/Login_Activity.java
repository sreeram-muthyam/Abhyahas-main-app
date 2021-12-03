package com.sreerammuthyam.abhyahas;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sreerammuthyam.abhyahas.Container_Activity;
import com.sreerammuthyam.abhyahas.R;

public class Login_Activity extends AppCompatActivity {

    EditText mEmail,mPassword;
    Button mLoginBtn;
    TextView mCreateBtn,mresetBtn;
    FirebaseAuth fAuth;
    FirebaseFirestore mfirestore;
    ImageView imgPassword;
    boolean isShowPassword = false;
    ProgressDialog pdd;
    String userID;
    LinearLayout loginactivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login_);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        mLoginBtn = findViewById(R.id.login);
        mEmail = findViewById(R.id.email_id);
        mPassword = findViewById(R.id.password);
        mCreateBtn = findViewById(R.id.signup);
        mresetBtn = findViewById(R.id.forgot);
        imgPassword = findViewById(R.id.eyelogin);
        loginactivity = findViewById(R.id.loginactivity);

        pdd = new ProgressDialog(Login_Activity.this);

        fAuth = FirebaseAuth.getInstance();
        mfirestore = FirebaseFirestore.getInstance();

        if(fAuth.getCurrentUser() != null && fAuth.getCurrentUser().isEmailVerified()) {
            startActivity(new Intent(getApplicationContext(), Container_Activity.class));
            finish();
        }

        imgPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isShowPassword) {
                    mPassword.setTransformationMethod(new PasswordTransformationMethod());
                    ((ImageView)(v)).setImageResource(R.drawable.ic_baseline_visibility_24);
                    isShowPassword = false;
                } else{
                    mPassword.setTransformationMethod(null);
                    ((ImageView)(v)).setImageResource(R.drawable.ic_baseline_visibility_off_24);
                    isShowPassword = true;
                }
            }
        });

        mLoginBtn.setOnClickListener(new View.OnClickListener()  {
            @Override
            public void onClick(View v) {
                if (isOnline()) {
                        String email = mEmail.getText().toString().trim();
                        String password = mPassword.getText().toString().trim();

                        if(TextUtils.isEmpty(email)) {
                            mEmail.setError("Email is Required.");
                            return;
                        }

                        if(TextUtils.isEmpty(password)) {
                            mPassword.setError("Password is Required.");
                            return;
                        }

                        if(password.length() < 6) {
                            mPassword.setError("Password Must be >= 6 Characters.");
                            return;
                        }
                        //authenticate the user

                        pdd.setCancelable(false);
                        pdd.setTitle("Abhyahas LogIn");
                        pdd.setMessage("Logging into your account. Wait for a while");
                        pdd.show();

                        fAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()) {
                                    if (fAuth.getCurrentUser().isEmailVerified()) {
                                        userID = fAuth.getCurrentUser().getUid();
                                        DocumentReference dref = mfirestore.collection("USERS").document(userID);
                                        dref.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                            @Override
                                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                if (documentSnapshot.getString("isLogged").equals("0")) {
                                                    dref.update("isLogged","1").addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            pdd.dismiss();
                                                            Toast.makeText(Login_Activity.this,"Log In Success", Toast.LENGTH_SHORT).show();
                                                            startActivity(new Intent(getApplicationContext(),Container_Activity.class));
                                                            finish();
                                                        }
                                                    }).addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            pdd.dismiss();
                                                            Snackbar.make(loginactivity, "Error while performing Login. Contact Team Abhyahas", Snackbar.LENGTH_LONG).show();
                                                        }
                                                    });
                                                } else {
                                                    pdd.dismiss();
                                                    Snackbar.make(loginactivity, "Account is logged in another device", Snackbar.LENGTH_LONG).show();
                                                }
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                pdd.dismiss();
                                                Snackbar.make(loginactivity, "Error while performing Login. Contact Team Abhyahas", Snackbar.LENGTH_LONG).show();
                                            }
                                        });
                                    } else {
                                        pdd.dismiss();
                                        new AlertDialog.Builder(Login_Activity.this)
                                                .setIcon(R.drawable.ic_baseline_close_24)
                                                .setTitle("Email Not Verified")
                                                .setMessage("Verify your email id by clicking on the link that we sent to your "+email+" mail id before logging in ...")
                                                .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        fAuth.signOut();
                                                       dialog.dismiss();
                                                    }
                                                }).setNegativeButton("RESEND LINK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                fAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        fAuth.signOut();
                                                        if (task.isSuccessful()) {
                                                            Toast.makeText(Login_Activity.this, " Verification link sent again to your "+email+" mail id ", Toast.LENGTH_SHORT).show();
                                                        } else {
                                                            Snackbar.make(loginactivity, "Error ! "+task.getException().getMessage(), Snackbar.LENGTH_LONG).show();
                                                        }
                                                    }
                                                });
                                            }
                                        }).show();
                                    }
                                }else {
                                    pdd.dismiss();
                                    Snackbar.make(loginactivity, "Error ! "+task.getException().getMessage(), Snackbar.LENGTH_LONG).show();

                                }
                            }
                        });
                } else {
                    Toast.makeText(Login_Activity.this, " Check your INTERNET connection ", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mresetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isOnline()) {
                    EditText resetMail = new EditText(v.getContext());
                    AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(v.getContext());
                    passwordResetDialog.setTitle("Reset Password? ");
                    passwordResetDialog.setMessage("Enter your registered email to receive link.");
                    passwordResetDialog.setView(resetMail);
                    passwordResetDialog.setCancelable(false);

                    passwordResetDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            /// extract the email and send reset link

                            String mail = resetMail.getText().toString();

                            if (!(mail.isEmpty())) {
                                fAuth.sendPasswordResetEmail(mail).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Snackbar.make(loginactivity, "Reset Link sent to your Registered Mail", Snackbar.LENGTH_LONG).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Snackbar.make(loginactivity, "Error ! Link is not sent : "+e.getMessage(), Snackbar.LENGTH_LONG).show();
                                    }
                                });

                            } else {
                                Snackbar.make(loginactivity, "Email ID is mandatory", Snackbar.LENGTH_LONG).show();
                            }


                        }
                    });

                    passwordResetDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    passwordResetDialog.create().show();
                } else {
                    Toast.makeText(Login_Activity.this, " Check your INTERNET connection ", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private boolean isOnline() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    public void intoregister(View view) {
        Intent intent = new Intent(Login_Activity.this, com.sreerammuthyam.abhyahas.Register_Activity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.slide_up,R.anim.slide_up_up);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}