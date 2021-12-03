package com.sreerammuthyam.abhyahas;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.sreerammuthyam.abhyahas.Utility.RealPathUtil;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class Register_Activity extends AppCompatActivity {

    RadioGroup radioGroup;
    RadioButton male, female;
    Button signup;
    TextView title,name, pictext;
    View edits,bg;
    EditText password, confirmpassword, fullname, email, yearofstudy, college;
    CheckBox check;
    ImageView profilepic, visibility;
    View choosepic;
    ProgressBar progressBar;
    boolean isShowPassword = false;
    String userID, path, gender;
    public Uri imageUri;
    private Integer []photos = {R.drawable.default_male_profile_pic, R.drawable.default_female_profile_pic};

    FirebaseAuth fAuth;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    FirebaseFirestore fStore;
    ProgressDialog pdd;
    LinearLayout registeractivity;
    DocumentReference mref;

    Map<String, Object> users = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_register_);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        signup = findViewById(R.id.signup);
        title = findViewById(R.id.title2);
        name = findViewById(R.id.name2);
        edits = findViewById(R.id.logedits2);
        bg = findViewById(R.id.logbg2);
        radioGroup = findViewById(R.id.radiogroupgender);
        profilepic =  findViewById(R.id.profilepic);
        visibility = findViewById(R.id.visibility);
        choosepic = findViewById(R.id.choosepic);
        password = findViewById(R.id.password);
        confirmpassword = findViewById(R.id.confirmpassword);
        fullname = findViewById(R.id.fullname);
        email = findViewById(R.id.email);
        college = findViewById(R.id.college);
        yearofstudy = findViewById(R.id.yearofstudy);
        check = findViewById(R.id.acceptvheckbox);
        male = findViewById(R.id.male);
        female = findViewById(R.id.female);
        progressBar = findViewById(R.id.progress);
        pictext = findViewById(R.id.pictext);
        registeractivity = findViewById(R.id.registeractivity);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        pdd = new ProgressDialog(Register_Activity.this);


        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int i) {
                RadioButton radioButton = (RadioButton) radioGroup.findViewById(i);
                int index = radioGroup.indexOfChild(radioButton);
                if (imageUri == null) {
                    profilepic.setImageResource(photos[index]);
                }
            }
        });

        visibility.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isShowPassword) {
                    password.setTransformationMethod(new PasswordTransformationMethod());
                    confirmpassword.setTransformationMethod(new PasswordTransformationMethod());
                    ((ImageView)(v)).setImageResource(R.drawable.ic_baseline_visibility_24);
                    isShowPassword = false;
                } else{
                    password.setTransformationMethod(null);
                    confirmpassword.setTransformationMethod(null);
                    ((ImageView)(v)).setImageResource(R.drawable.ic_baseline_visibility_off_24);
                    isShowPassword = true;
                }
            }
        });

        signup.setOnClickListener(new View.OnClickListener()  {
            @Override
            public void onClick(View v) {
                if (isOnline()) {
                    String emailid = email.getText().toString().trim();
                    String passwordd = password.getText().toString().trim();
                    String password2 = confirmpassword.getText().toString().trim();
                    String Name = fullname.getText().toString().trim();
                    String College = college.getText().toString().trim();
                    String Year = yearofstudy.getText().toString().trim();
                    int yearof;

                    //validations.
                    if(TextUtils.isEmpty(emailid)) {
                        email.setError("Email is Required.");
                        return;
                    }

                    if(TextUtils.isEmpty(passwordd)) {
                        password.setError("Password is Required.");
                        return;
                    }

                    if(passwordd.length() < 6) {
                        password.setError("Password Must be at least 6 Characters");
                        return;
                    }

                    if(!passwordd.equals(password2)){
                        confirmpassword.setError("Password and Confirm Password doesn't match");
                        return;
                    }

                    if(TextUtils.isEmpty(Name)) {
                        fullname.setError("Full Name is required");
                        return;
                    }

                    if(TextUtils.isEmpty(College)) {
                        college.setError("College Name is required");
                        return;
                    }

                    if(TextUtils.isEmpty(Year))
                    {
                        yearofstudy.setError("Year of study is required");
                        return;
                    } else {
                        yearof = Integer.parseInt(Year);
                    }


                    if(yearof > 5) {
                        yearofstudy.setError("Year of Study must be less than or equal to 5");
                        return;
                    }

                    //getCheckedRadioButton() -> returns -1 if no radio button is checked here.
                    int isSelected = radioGroup.getCheckedRadioButtonId();
                    if(isSelected == -1){
                        Snackbar.make(registeractivity, "You have not selected gender", Snackbar.LENGTH_LONG).show();
                        return;
                    }
                    //checkbox validation
                    if(!check.isChecked()) {
                        Snackbar.make(registeractivity, "Accept the Terms and Conditions", Snackbar.LENGTH_LONG).show();
                        return;
                    }



                    AlertDialog.Builder builder = new AlertDialog.Builder(Register_Activity.this);
                    builder.setMessage("We will send you a verification mail id to the "+emailid+" to confirm the mail belongs to you.\n\nSo, please verify whether\n"+emailid+"\nis your perfect mail id or not without any errors.\n\n");
                    builder.setTitle("Mail Id Confirmation");
                    builder.setCancelable(false);
                    builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            pdd.setCancelable(false);
                            pdd.setTitle("Account Creation");
                            pdd.show();
                            pdd.setMessage("Creating Abhyahas account. Wait for a while");

                            // register the user in firebase
                            fAuth.createUserWithEmailAndPassword(emailid,passwordd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()) {

                                        if (male.isChecked()) {
                                            gender = "M";
                                        } else {
                                            gender = "F";
                                        }

                                        upload();

                                    }else {
                                        pdd.dismiss();
                                        Snackbar.make(registeractivity, "Error ! "+task.getException().getMessage(), Snackbar.LENGTH_LONG).show();
                                    }
                                }
                            });

                        }
                    });

                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            email.setText("");
                            email.setError("Enter email !!");
                            Snackbar.make(registeractivity, "Enter the perfect mail id", Snackbar.LENGTH_LONG).show();
                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                } else {
                    Toast.makeText(Register_Activity.this, " Check your INTERNET connection ", Toast.LENGTH_SHORT).show();
                }
            }
        });

        choosepic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(pictext.getText().equals("Remove Picture")) {
                    imageUri = null;
                    pictext.setText("Upload Picture");
                    if (female.isChecked()){
                        profilepic.setImageResource(R.drawable.default_female_profile_pic);
                    } else {
                        profilepic.setImageResource(R.drawable.default_male_profile_pic);
                    }
                } else {
                    choosePicture();
                }
            }
        });

    }


    private boolean isOnline() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    public void login(View view) {
        Intent intent = new Intent(Register_Activity.this, com.sreerammuthyam.abhyahas.Login_Activity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.slide_down,R.anim.slide_down_down);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(Register_Activity.this, com.sreerammuthyam.abhyahas.Login_Activity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.slide_down,R.anim.slide_down_down);

    }

    private void choosePicture() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == RESULT_OK ) {
            if (data.getData() != null) {

                path = RealPathUtil.getRealPath(Register_Activity.this, data.getData());

                if(path==null) {
                    Toast.makeText(Register_Activity.this, "Picture is not supported from this folder. Try from another folder", Toast.LENGTH_SHORT).show();
                }
                else {
                    File file = new File(path);
                    long length = file.length();

                    if (length < 1000000) {
                        pictext.setText("Remove Picture");
                        imageUri = data.getData();
                        profilepic.setImageURI(imageUri);
                    } else {
                        Toast.makeText(this, "Select picture less than 1 MB", Toast.LENGTH_SHORT).show();
                        Intent intent;
                        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
                        {
                            intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
                        }
                        else
                        {
                            intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.INTERNAL_CONTENT_URI);
                        }
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        intent.putExtra("return-data", true);
                        startActivityForResult(intent,1);
                    }
                }
            } else {
                Snackbar.make(registeractivity, "Failed to select picture", Snackbar.LENGTH_LONG).show();
            }
        }
    }

    public void upload() {
        FirebaseUser mUser;
        mUser = fAuth.getCurrentUser();
        userID  = mUser.getUid();

        DocumentReference documentReference = fStore.collection("USERS").document(userID);

        users.put("Full_Name",fullname.getText().toString());
        users.put("Name_search",fullname.getText().toString().toLowerCase());
        users.put("Email",email.getText().toString());
        users.put("College_Name",college.getText().toString());
        users.put("Year_of_Study",yearofstudy.getText().toString());
        users.put("User_ID",userID);
        users.put("Followers","0");
        users.put("Following","0");
        users.put("courses_uploaded","0");
        users.put("courses_accessed","0");
        users.put("isLogged","0");
        users.put("Gender",gender);

        documentReference.set(users).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(Register_Activity.this, "Data saved successfully", Toast.LENGTH_SHORT).show();
            }
        });

        final StorageReference ref = storageReference.child("USERS").child(userID).child("Image");
        final DocumentReference usersdb = fStore.collection("USERS").document(userID);

        if (imageUri != null ) {

            ref.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            HashMap<String,String> image = new HashMap<>();

                            image.put("Image", String.valueOf(uri));
                            usersdb.set(image,SetOptions.merge());
                            mref = fStore.collection("ADMIN").document("users");
                            mref.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    Double number = documentSnapshot.getDouble("number");
                                    mref.update("number",(number+1.00)).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            mUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        pdd.dismiss();
                                                        Toast.makeText(Register_Activity.this, "Account created successfully ", Toast.LENGTH_LONG).show();
                                                        new AlertDialog.Builder(Register_Activity.this)
                                                                .setIcon(R.drawable.ic_baseline_email_24)
                                                                .setTitle("Verification Email Sent")
                                                                .setMessage("Verify your email id by clicking on the link that we sent to your registered mail id before logging in ...")
                                                                .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(DialogInterface dialog, int which) {
                                                                        Intent intent = new Intent(Register_Activity.this, com.sreerammuthyam.abhyahas.Login_Activity.class);
                                                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                                        startActivity(intent);
                                                                        finish();
                                                                        overridePendingTransition(R.anim.slide_down,R.anim.slide_down_down);
                                                                    }
                                                                }).show();
                                                    } else {
                                                        pdd.dismiss();
                                                        Snackbar.make(registeractivity, "Something went wrong. Contact Team Abhyahas", Snackbar.LENGTH_LONG).show();
                                                    }
                                                }
                                            });
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            pdd.dismiss();
                                            Snackbar.make(registeractivity, "Failed to create account", Snackbar.LENGTH_LONG).show();
                                        }
                                    });
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    pdd.dismiss();
                                    Snackbar.make(registeractivity, "Failed to create account", Snackbar.LENGTH_LONG).show();
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            pdd.dismiss();
                            Snackbar.make(registeractivity, "Failed to upload picture", Snackbar.LENGTH_LONG).show();
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    pdd.dismiss();
                    Snackbar.make(registeractivity, "Failed to upload picture", Snackbar.LENGTH_LONG).show();
                }
            });


        } else {

            if (male.isChecked()) {
                HashMap<String,String> image = new HashMap<>();
                image.put("Image", "https://firebasestorage.googleapis.com/v0/b/abhyahas-beta-version-1.appspot.com/o/Defaults%2Fdefault_male_profile_pic.png?alt=media&token=e683824d-3adc-4d46-82e6-82d7f8e3e3d5");
                usersdb.set(image,SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        mref = fStore.collection("ADMIN").document("users");
                        mref.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                Double number = documentSnapshot.getDouble("number");
                                mref.update("number",(number+1.00)).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        mUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    pdd.dismiss();
                                                    Toast.makeText(Register_Activity.this, "Account created successfully ", Toast.LENGTH_LONG).show();
                                                    new AlertDialog.Builder(Register_Activity.this)
                                                            .setIcon(R.drawable.ic_baseline_email_24)
                                                            .setTitle("Verification Email Sent")
                                                            .setMessage("Verify your email id by clicking on the link that we sent to your registered mail id before logging in ...")
                                                            .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialog, int which) {
                                                                    Intent intent = new Intent(Register_Activity.this, com.sreerammuthyam.abhyahas.Login_Activity.class);
                                                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                                    startActivity(intent);
                                                                    finish();
                                                                    overridePendingTransition(R.anim.slide_down,R.anim.slide_down_down);
                                                                }
                                                            }).show();
                                                } else {
                                                    pdd.dismiss();
                                                    Snackbar.make(registeractivity, "Something went wrong. Contact Team Abhyahas", Snackbar.LENGTH_LONG).show();
                                                }
                                            }
                                        });
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        pdd.dismiss();
                                        Snackbar.make(registeractivity, "Failed to create account", Snackbar.LENGTH_LONG).show();
                                    }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                pdd.dismiss();
                                Snackbar.make(registeractivity, "Failed to create account", Snackbar.LENGTH_LONG).show();
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pdd.dismiss();
                        Snackbar.make(registeractivity, "Failed to upload picture", Snackbar.LENGTH_LONG).show();
                    }
                });
            } else {
                HashMap<String,String> image = new HashMap<>();
                image.put("Image","https://firebasestorage.googleapis.com/v0/b/abhyahas-beta-version-1.appspot.com/o/Defaults%2Fdefault_female_profile_pic.png?alt=media&token=cd5a1f54-b52a-499e-891d-9a4fd694b9e1");
                usersdb.set(image,SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        mref = fStore.collection("ADMIN").document("users");
                        mref.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                Double number = documentSnapshot.getDouble("number");
                                mref.update("number",(number+1.00)).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        mUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    pdd.dismiss();
                                                    Toast.makeText(Register_Activity.this, "Account created successfully ", Toast.LENGTH_LONG).show();
                                                    new AlertDialog.Builder(Register_Activity.this)
                                                            .setIcon(R.drawable.ic_baseline_email_24)
                                                            .setTitle("Verification Email Sent")
                                                            .setMessage("Verify your email id by clicking on the link that we sent to your registered mail id before logging in ...")
                                                            .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialog, int which) {
                                                                    Intent intent = new Intent(Register_Activity.this, com.sreerammuthyam.abhyahas.Login_Activity.class);
                                                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                                    startActivity(intent);
                                                                    finish();
                                                                    overridePendingTransition(R.anim.slide_down,R.anim.slide_down_down);
                                                                }
                                                            }).show();
                                                } else {
                                                    pdd.dismiss();
                                                    Snackbar.make(registeractivity, "Something went wrong. Contact Team Abhyahas", Snackbar.LENGTH_LONG).show();
                                                }
                                            }
                                        });
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        pdd.dismiss();
                                        Snackbar.make(registeractivity, "Failed to create account", Snackbar.LENGTH_LONG).show();
                                    }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                pdd.dismiss();
                                Snackbar.make(registeractivity, "Failed to create account", Snackbar.LENGTH_LONG).show();
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pdd.dismiss();
                        Snackbar.make(registeractivity, "Failed to upload picture", Snackbar.LENGTH_LONG).show();
                    }
                });
            }

        }

    }

    public void termsandconditions(View view) {
        Toast.makeText(Register_Activity.this, "Abhyahas Terms and Conditions", Toast.LENGTH_SHORT).show();
        Intent BroeseIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://sites.google.com/view/abhyahas-terms-and-conditions/home"));
        startActivity(BroeseIntent);
    }
}