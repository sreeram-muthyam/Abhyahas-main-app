package Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;
import com.squareup.picasso.Picasso;
import com.sreerammuthyam.abhyahas.MainActivity;
import com.sreerammuthyam.abhyahas.R;
import com.sreerammuthyam.abhyahas.Utility.RealPathUtil;

import java.io.File;
import java.util.HashMap;
import static android.app.Activity.RESULT_OK;

public class MyprofileFragment extends Fragment {

    private FirebaseFirestore mfirestore;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    ChipNavigationBar chipNavigationBar;

    ListenerRegistration registration1;

    String userid, image, pathh, gender;
    View vv;
    Uri imageUri;
    ImageView back, profile;
    Button connect, delete;
    TextView followers,following, edit,save, textofpic;
    EditText full,id,college,yr,phone,contact;
    boolean flag = true;
    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(savedInstanceState==null) {
            view =  inflater.inflate(R.layout.fragment_myprofile, container, false);
        }
        //View view =  inflater.inflate(R.layout.fragment_myprofile, container, false);

        profile = view.findViewById(R.id.profile_image_myprofile);

        chipNavigationBar = getActivity().findViewById(R.id.bottom_nav_menu_id);

        mfirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        mUser = mAuth.getCurrentUser();

        userid = mUser.getUid();

        SearchView searchView = (SearchView) getActivity().findViewById(R.id.task);
        searchView.setVisibility(View.GONE);

        final StorageReference ref = storageReference.child("USERS").child(userid).child("Image");

        back = view.findViewById(R.id.backprofile);
        connect = view.findViewById(R.id.connect);
        followers = view.findViewById(R.id.followersno);
        following = view.findViewById(R.id.followingno);
        phone = view.findViewById(R.id.phone);
        contact = view.findViewById(R.id.contact);
        full = view.findViewById(R.id.fullnameep);
        id = view.findViewById(R.id.emailep);
        college = view.findViewById(R.id.collegeep);
        yr = view.findViewById(R.id.yearep);
        edit = view.findViewById(R.id.edit);
        save = view.findViewById(R.id.savee);
        vv = view.findViewById(R.id.vv);
        textofpic = view.findViewById(R.id.textofpic);
        delete = view.findViewById(R.id.deleteaccount);

        DocumentReference dref = mfirestore.collection("USERS").document(userid);

        HashMap<String ,String> editt = new HashMap<>();

        registration1 = dref.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {


                full.setText(value.getString("Full_Name"));
                id.setText(value.getString("Email"));
                college.setText(value.getString("College_Name"));
                yr.setText(value.getString("Year_of_Study"));
                phone.setText(value.getString("Phone"));
                contact.setText(value.getString("Contact"));
                followers.setText(value.getString("Followers"));
                following.setText(value.getString("Following"));
                image = value.getString("Image");
                gender = value.getString("Gender");
                Picasso.get().load(Uri.parse(image)).into(profile);
            }
        });


        vv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (textofpic.getText().equals("Set as Default")) {
                    textofpic.setText("Change Picture");
                    if(gender.equals("M")){
                        editt.put("Image", "https://firebasestorage.googleapis.com/v0/b/abhyahas-beta-version-1.appspot.com/o/Defaults%2Fdefault_male_profile_pic.png?alt=media&token=e683824d-3adc-4d46-82e6-82d7f8e3e3d5");
                        profile.setImageResource(R.drawable.default_male_profile_pic);
                    } else {
                        editt.put("Image","https://firebasestorage.googleapis.com/v0/b/abhyahas-beta-version-1.appspot.com/o/Defaults%2Fdefault_female_profile_pic.png?alt=media&token=cd5a1f54-b52a-499e-891d-9a4fd694b9e1");
                        profile.setImageResource(R.drawable.default_female_profile_pic);
                    }
                } else {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(intent, 1);
                }
            }
        });

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isOnline()) {
                    phone.setEnabled(true);
                    contact.setEnabled(true);
                    full.setEnabled(true);
                    college.setEnabled(true);
                    yr.setEnabled(true);

                    save.setVisibility(View.VISIBLE);
                    vv.setVisibility(View.VISIBLE);
                    edit.setVisibility(View.GONE);
                    connect.setVisibility(View.GONE);
                    delete.setVisibility(View.VISIBLE);
                } else {
                    Toast.makeText(getContext(), " Check your INTERNET connection ", Toast.LENGTH_SHORT).show();
                }
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isOnline()) {
                    dref.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            String courses_uploaded = documentSnapshot.getString("courses_uploaded");
                            String courses_accessed = documentSnapshot.getString("courses_accessed");
                            String followers = documentSnapshot.getString("Followers");
                            String following = documentSnapshot.getString("Following");
                            if (courses_uploaded.equals("0")) {
                                if (courses_accessed.equals("0")) {
                                    if (followers.equals("0")) {
                                        if (following.equals("0")) {
                                            ProgressDialog pd = new ProgressDialog(getContext());
                                            pd.setCancelable(false);
                                            pd.setTitle("DELETING ACCOUNT !");
                                            pd.setMessage("Please wait for a while until your account is deleted");
                                            new AlertDialog.Builder(getContext())
                                                    .setIcon(R.drawable.ic_baseline_close_24)
                                                    .setTitle("DELETE ACCOUNT !!")
                                                    .setMessage("Are you sure you want to delete your Abhyahas account ? After deletion all the data will be erased and can't be retrieved back. Do you want to continue ?")
                                                    .setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            registration1.remove();
                                                            pd.show();
                                                            dref.update("User_ID","deleted").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful()) {
                                                                        mUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                if(task.isSuccessful()) {
                                                                                    pd.dismiss();
                                                                                    Intent Start = new Intent(getActivity(), MainActivity.class);
                                                                                    Start.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);//makesure user cant go back
                                                                                    startActivity(Start);
                                                                                    getActivity().finish();
                                                                                } else {
                                                                                    Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                                                    dref.update("User_ID",userid).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                        @Override
                                                                                        public void onComplete(@NonNull Task<Void> task2) {
                                                                                            pd.dismiss();
                                                                                        }
                                                                                    });
                                                                                }
                                                                            }
                                                                        });
                                                                    } else {
                                                                        pd.dismiss();
                                                                        Toast.makeText(getContext(), " Something went wrong, Try after sometime ... ", Toast.LENGTH_SHORT).show();
                                                                    }
                                                                }
                                                            });
                                                        }
                                                    })
                                                    .setNegativeButton("DISMISS", null)
                                                    .show();
                                        } else {
                                            Toast.makeText(getContext(), " Un follow all the users you are following currently before deleting your account ", Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        Toast.makeText(getContext(), " Remove all your followers before deleting your account ", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(getContext(), " Remove all the courses you enrolled before deleting account !", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(getContext(), " Delete all the courses you uploaded before deleting account !", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getContext(), " Something went wrong! Try after sometime ", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(getContext(), " Check your INTERNET connection ", Toast.LENGTH_SHORT).show();
                }
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isOnline()) {
                    phone.setEnabled(false);
                    contact.setEnabled(false);
                    full.setEnabled(false);
                    college.setEnabled(false);
                    yr.setEnabled(false);

                    save.setVisibility(View.GONE);
                    vv.setVisibility(View.GONE);
                    edit.setVisibility(View.VISIBLE);
                    connect.setVisibility(View.VISIBLE);
                    delete.setVisibility(View.GONE);

                    String name = full.getText().toString();
                    String phonenumber = phone.getText().toString();
                    String Contactnumber = contact.getText().toString();
                    String clg = college.getText().toString();
                    String year = yr.getText().toString();

                    if(!name.isEmpty()) {
                        editt.put("Full_Name",name);
                    }

                    if(!clg.isEmpty()) {
                        editt.put("College_Name",clg);
                    }

                    if(!year.isEmpty()) {
                        editt.put("Year_of_Study",year);
                    }

                    if(!phonenumber.isEmpty()) {
                        editt.put("Phone",phonenumber);
                    }


                    if(!Contactnumber.isEmpty()) {
                        if (Contactnumber.length()!=10){
                            contact.setError("Enter valid Number");
                            flag = false;
                        } else {
                            editt.put("Contact", Contactnumber);
                        }
                    }

                    if (!editt.isEmpty() && flag ) {

                        if(imageUri != null) {
                            ref.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            editt.put("Image",uri.toString());
                                            dref.set(editt, SetOptions.merge());
                                        }
                                    });
                                }
                            });
                        } else {
                            dref.set(editt, SetOptions.merge());

                        }

                        Toast.makeText(view.getContext(), "Profile Updated", Toast.LENGTH_SHORT).show();


                    } else {
                        Toast.makeText(view.getContext(), "No change has been done !", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), " Check your INTERNET connection ", Toast.LENGTH_SHORT).show();
                }
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chipNavigationBar.setItemSelected(R.id.b,true);
                FragmentTransaction ftt = getFragmentManager().beginTransaction();
                ftt.replace(R.id.fragment_container, new Fragments.Home()).commit();
            }
        });

        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction ftt = getFragmentManager().beginTransaction();
                ftt.replace(R.id.fragment_container, new Fragments.ConnectmoreFragment()).commit();
            }
        });

        followers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction ftt = getFragmentManager().beginTransaction();
                ftt.replace(R.id.fragment_container, new Fragments.FollowersFragment()).commit();
            }
        });

        following.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction ftt = getFragmentManager().beginTransaction();
                ftt.replace(R.id.fragment_container, new Fragments.FollowingFragment()).commit();
            }
        });

        return view;
    }

    private boolean isOnline() {
        ConnectivityManager connMgr = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == RESULT_OK ) {

            if (data.getData() != null) {
                pathh = RealPathUtil.getRealPath(getContext(), data.getData());

                if(pathh==null) {
                    Toast.makeText(getContext(), "Picture is not supported from this folder. Try from another folder", Toast.LENGTH_SHORT).show();
                } else {

                    File file = new File(pathh);
                    long length = file.length();

                    if (length < 1000000) {
                        textofpic.setText("Set as Default");
                        imageUri = data.getData();
                        profile.setImageURI(imageUri);
                    } else {
                        Toast.makeText(getContext(), "Select Profile Pic less than 1 MB", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(getContext(), "Failed to select thumbnail", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        registration1.remove();
    }

    @Override
    public void onPause() {
        super.onPause();
        registration1.remove();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        registration1.remove();
    }
}