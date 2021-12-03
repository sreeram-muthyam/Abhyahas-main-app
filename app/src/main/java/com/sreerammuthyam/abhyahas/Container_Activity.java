package com.sreerammuthyam.abhyahas;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.squareup.picasso.Picasso;
import java.io.File;
import java.util.List;
import static java.lang.String.valueOf;

public class Container_Activity extends AppCompatActivity {

    private FirebaseFirestore mfirestore;
    FirebaseAuth fAuth;
    DocumentReference dref;
    ImageView profile;

    ListenerRegistration registration;

    ChipNavigationBar chipNavigationBar;

    SearchView searchView;

    String userId, img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_container_);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        mfirestore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            userId = fAuth.getCurrentUser().getUid();
        }

        dref = mfirestore.collection("USERS").document(userId);

        registration = dref.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                try {
                    img = value.getString("Image");
                    if (img!=null) {
                        Uri image = Uri.parse(img);
                        Glide.with(Container_Activity.this)
                                .load(image)
                                .into(profile);
                    }
                } catch (Exception e) {

                }
            }
        });

        searchView = (SearchView) findViewById(R.id.task);
        searchView.setVisibility(View.VISIBLE);

        profile = findViewById(R.id.profilepp);

        Dexter.withActivity(this)
                .withPermissions(new String[] {
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                })
                .withListener(new MultiplePermissionsListener() {

                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        new AlertDialog.Builder(Container_Activity.this)
                                .setIcon(R.mipmap.ic_launcher)
                                .setTitle("PERMISSION DENIED")
                                .setMessage(" Allow Abhyahas to access your Storage from your device settings.")
                                .setCancelable(false)
                                .setPositiveButton("OKAY", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        finishAffinity();
                                        System.exit(0);
                                    }
                                })
                                .show();
                     }
                })
                .check();


        Toolbar toolbar = findViewById(R.id.toolbar_id);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Abhyahas");
        toolbar.setTitleTextColor(getResources().getColor(R.color.black));

        chipNavigationBar = findViewById(R.id.bottom_nav_menu_id);
        chipNavigationBar.setItemSelected(R.id.b,true);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.fragment_container,new Fragments.Home());
        ft.commit();
        bottomMenu();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {

            case R.id.ln :
                Toast.makeText(Container_Activity.this, "Abhyahas LinkedIn", Toast.LENGTH_SHORT).show();
                Intent BroeseIntent7 = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.linkedin.com/in/team-abhyahas-286548208"));
                startActivity(BroeseIntent7);
                break;

            case R.id.fb :
                Toast.makeText(Container_Activity.this, "Abhyahas Facebook", Toast.LENGTH_SHORT).show();
                Intent BroeseIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://m.facebook.com/Abhyahas-108178647983598/?tsid=0.36484707220844026&source=result"));
                startActivity(BroeseIntent);
                break;

            case R.id.gm :
                Toast.makeText(Container_Activity.this, "Abhyahas G-mail", Toast.LENGTH_SHORT).show();
                Intent mail = new Intent(Intent.ACTION_VIEW, Uri.parse("mailto:teamabhyahas@gmail.com"));
                startActivity(mail);
                break;

            case  R.id.logout :
                logout();
                break;

            case R.id.insta :
                Toast.makeText(Container_Activity.this, "Abhyahas Instagram", Toast.LENGTH_SHORT).show();
                Intent BroeseIntent5 = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.instagram.com/invites/contact/?i=11sgk2wfz9rzx&utm_content=l1jp4dn"));
                startActivity(BroeseIntent5);
                break;

            case R.id.share :
                try {
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("text/plain");
                    intent.putExtra(Intent.EXTRA_SUBJECT,"Abhyahas");
                    //String sharemessage = "https://play.google.com/store/apps/details?id=com.google.android.apps.searchlite";
                    String sharemessage = "https://play.google.com/store/apps/details?id="+ BuildConfig.APPLICATION_ID;
                    intent.putExtra(Intent.EXTRA_TEXT,sharemessage);
                    startActivity(Intent.createChooser(intent,"Share by"));
                } catch (Exception e) {
                    Toast.makeText(this, "Error ! Try after a while...", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.reortprblm :
                Toast.makeText(Container_Activity.this, "Report a problem", Toast.LENGTH_SHORT).show();
                Intent BroeseIntent8 = new Intent(Intent.ACTION_VIEW, Uri.parse("https://docs.google.com/forms/d/e/1FAIpQLSdiAFJyaWsQEXaSzLjgc-psG3A7_oeZi66cRW6ZhvsLYxVOMg/viewform"));
                startActivity(BroeseIntent8);
                break;

            case R.id.visitwebsite :
                Toast.makeText(Container_Activity.this, "Redirecting to Abhyahas Website", Toast.LENGTH_SHORT).show();
                Intent BroeseIntent2 = new Intent(Intent.ACTION_VIEW, Uri.parse("https://sites.google.com/view/sampleabhyahas/home"));
                startActivity(BroeseIntent2);
                break;

            case R.id.termsandconditions :
                Toast.makeText(Container_Activity.this, "Redirecting to Abhyahas Website", Toast.LENGTH_SHORT).show();
                Intent BroeseIntent3 = new Intent(Intent.ACTION_VIEW, Uri.parse("https://sites.google.com/view/abhyahas-terms-and-conditions/home"));
                startActivity(BroeseIntent3);
                break;

            case R.id.contribute :
                Uri uri = new Uri.Builder()
                        .scheme("upi")
                        .authority("pay")
                        .appendQueryParameter("pa", "8688519078@ybl")                       // virtual ID
                        .appendQueryParameter("pn", "Abhyahas")                             // name
                        .appendQueryParameter("tn", "Thank you for supporting us")          // any note about payment
                        .build();
                Intent upiPayIntent = new Intent(Intent.ACTION_VIEW);
                upiPayIntent.setData(uri);// will always show a dialog to user to choose an app
                Intent chooser = Intent.createChooser(upiPayIntent, "Contribute using");       // check if intent resolves
                if(null != chooser.resolveActivity(getPackageManager())) {
                    startActivity(chooser);
                } else {
                    Toast.makeText(Container_Activity.this,"No UPI app found, please install one to continue",Toast.LENGTH_SHORT).show();
                }
                break;
        }
        return true;
    }

    private void logout() {
        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(Container_Activity.this);
        builder.setMessage("Are you sure, you want to log out ?");
        builder.setCancelable(true);
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                registration.remove();
                dref.update("isLogged", "0").addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Intent Start = new Intent(Container_Activity.this, com.sreerammuthyam.abhyahas.MainActivity.class);
                        Start.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);//makesure user cant go back
                        fAuth.signOut();
                        startActivity(Start);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Container_Activity.this, "Error while performing log out. Contact Team Abhyahas", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });
        android.app.AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void bottomMenu() {

        chipNavigationBar.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int i) {
                Fragment fragment = null;
                switch (i) {
                    case R.id.b:
                        fragment = new Fragments.Home();
                        break;
                    case R.id.a:
                        fragment = new Fragments.AccessFragment();
                        break;
                    case R.id.c:
                        fragment = new Fragments.UploadFragment();
                        break;
                }
                File del = new File(valueOf(getExternalFilesDir("/data")));
                if(del.exists()){
                    deleteDirectory(del);
                }
                File deletefile = new File(valueOf(getExternalFilesDir("/Downloaded file")));
                if (deletefile.exists()){
                    deleteDirectory(deletefile);
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,fragment).commit();
            }
        });

    }

    static public boolean deleteDirectory(File path) {
        if( path.exists() ) {
            File[] files = path.listFiles();
            if (files == null) {
                return true;
            }
            for(int i=0; i<files.length; i++) {
                if(files[i].isDirectory()) {
                    deleteDirectory(files[i]);
                }
                else {
                    files[i].delete();
                }
            }
        }
        return( path.delete() );
    }

    public void myprofile(View view) {
        File del = new File(valueOf(getExternalFilesDir("/data")));
        if(del.exists()){
            deleteDirectory(del);
        }
        File deletefile = new File(valueOf(getExternalFilesDir("/Downloaded file")));
        if (deletefile.exists()){
            deleteDirectory(deletefile);
        }
        chipNavigationBar.setItemSelected(R.id.b,false);
        chipNavigationBar.setItemSelected(R.id.a,false);
        chipNavigationBar.setItemSelected(R.id.c,false);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container,new Fragments.MyprofileFragment());
        ft.commit();
    }

    @Override
    protected void onPause() {
        super.onPause();
        File del = new File(valueOf(getExternalFilesDir("/data")));
        if(del.exists()){
            deleteDirectory(del);
        }
        File deletefile = new File(valueOf(getExternalFilesDir("/Downloaded file")));
        if (deletefile.exists()){
            deleteDirectory(deletefile);
        }
    }


    @Override
    protected void onStop() {
        super.onStop();
        File del = new File(valueOf(getExternalFilesDir("/data")));
        if(del.exists()){
            deleteDirectory(del);
        }
        File deletefile = new File(valueOf(getExternalFilesDir("/Downloaded file")));
        if (deletefile.exists()){
            deleteDirectory(deletefile);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        File del = new File(valueOf(getExternalFilesDir("/data")));
        if(del.exists()){
            deleteDirectory(del);
        }
        File deletefile = new File(valueOf(getExternalFilesDir("/Downloaded file")));
        if (deletefile.exists()){
            deleteDirectory(deletefile);
        }
    }

    @Override
    public void onBackPressed() {
            new AlertDialog.Builder(this)
                    .setIcon(R.drawable.ic_baseline_close_24)
                    .setTitle("Closing App")
                    .setMessage("Are you sure you want to close the app?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finishAffinity();
                            System.exit(0);
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();
    }

}