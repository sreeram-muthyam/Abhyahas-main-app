package Fragments;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;
import com.sreerammuthyam.abhyahas.R;
import com.sreerammuthyam.abhyahas.Utility.NetworkChangeListener;
import com.sreerammuthyam.abhyahas.Utility.RealPathUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import static java.lang.String.valueOf;

public class Newcourseuploadfragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private static final int REQ_TOKEN = 21;
    private static final int REQ_TOKEN2 = 7;

    Double num,pend, admin;
    FirebaseFirestore mfirestore;
    FirebaseAuth mAuth;
    StorageReference mstorage;
    Uri path, image;
    String text, name, description, cost, UserID, pathh, route;
    Button saves, uploadpdffile, selectimage, proceed, cancel, proceed2;
    EditText coursename,coursedescription;
    ImageView back2, photo;
    RadioGroup type,pdf,pricegroup;
    RadioButton uploadpdfbtn, nopdfbtn, premium, free, academic, nonacademic, competative;
    EditText price;
    Spinner spinner;
    SecretKey skey;
    ProgressDialog pd;
    HashMap<String,String> clg = new HashMap<>();
    HashMap<String,String> data = new HashMap<>();
    View view;
    NetworkChangeListener networkChangeListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if(savedInstanceState==null) {
            view = inflater.inflate(R.layout.fragment_newcourseuploadfragment, container, false);
        }
        //View view = inflater.inflate(R.layout.fragment_newcourseuploadfragment, container, false);

        saves = view.findViewById(R.id.save);
        back2 = view.findViewById(R.id.back2);
        uploadpdfbtn = view.findViewById(R.id.uploadpdfbtn);
        nopdfbtn = view.findViewById(R.id.nopdfbtn);
        uploadpdffile = view.findViewById(R.id.uploadpdffile);
        premium = view.findViewById(R.id.premium);
        free = view.findViewById(R.id.free);
        price = view.findViewById(R.id.price);
        spinner = view.findViewById(R.id.spinner);
        academic = view.findViewById(R.id.academic);
        nonacademic = view.findViewById(R.id.nonacademic);
        competative = view.findViewById(R.id.competative);
        coursename = view.findViewById(R.id.coursename);
        coursedescription = view.findViewById(R.id.coursedescription);
        selectimage = view.findViewById(R.id.selectimage);
        photo = view.findViewById(R.id.photo);
        type = view.findViewById(R.id.type);
        pdf = view.findViewById(R.id.pdf);
        pricegroup = view.findViewById(R.id.pricegroup);
        proceed = view.findViewById(R.id.proceed);
        proceed2 = view.findViewById(R.id.proceed2);
        cancel = view.findViewById(R.id.cancel);

        spinner.setOnItemSelectedListener(this);

        networkChangeListener = new NetworkChangeListener();

        ImageView profile = getActivity().findViewById(R.id.profilepp);
        profile.setEnabled(false);

        ChipNavigationBar chipNavigationBar = getActivity().findViewById(R.id.bottom_nav_menu_id);
        chipNavigationBar.setItemEnabled(R.id.b,false);
        chipNavigationBar.setItemEnabled(R.id.a,false);

        mfirestore = FirebaseFirestore.getInstance();

        byte[] encodedKey     = Base64.decode( "fRIxFDSHKrBDUjseir8TSg==\n"+"    ", Base64.DEFAULT);
        skey = new SecretKeySpec(encodedKey, 0, encodedKey.length, "AES");

        mAuth = FirebaseAuth.getInstance();
        UserID = mAuth.getCurrentUser().getUid();

        pd = new ProgressDialog(getContext());

        mstorage = FirebaseStorage.getInstance().getReference();

        SearchView searchView = (SearchView) getActivity().findViewById(R.id.task);
        searchView.setVisibility(View.GONE);

        saves.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                name = coursename.getText().toString();
                description = coursedescription.getText().toString();

                if (premium.isChecked()) {
                    if (Integer.parseInt(price.getText().toString())<50 || Integer.parseInt(price.getText().toString())>100) {
                        Toast.makeText(getContext(), "Price must be in between 50.Rs and 100.Rs", Toast.LENGTH_SHORT).show();
                        price.setError("Price must be in between 50.Rs and 100.Rs");
                        return;
                    } else {
                        cost = price.getText().toString();
                    }
                } else {
                    cost = "FREE";
                }

                data.put("Status","pending");
                data.put("Creater_ID",UserID);
                data.put("Creator_ID",UserID);
                data.put("Enrolments","0");
                data.put("Total_Enrolments","0");

                if (!name.isEmpty()) {
                    data.put("Course_Name",name);
                    data.put("Course_search",name.toLowerCase());
                    if (!description.isEmpty()) {
                        data.put("Course_Description", description);
                        if (!(text == null)) {
                            clg.put("College_Name",text);
                            if (!(cost.isEmpty())) {
                                data.put("Course_Price",cost);
                                if (!(image==null)) {
                                    if (uploadpdfbtn.isChecked()) {
                                        if (!(path==null)) {

                                            selectimage.setEnabled(false);
                                            coursename.setEnabled(false);
                                            coursedescription.setEnabled(false);
                                            spinner.setEnabled(false);
                                            academic.setEnabled(false);
                                            nonacademic.setEnabled(false);
                                            competative.setEnabled(false);
                                            uploadpdfbtn.setEnabled(false);
                                            nopdfbtn.setEnabled(false);
                                            premium.setEnabled(false);
                                            free.setEnabled(false);
                                            uploadpdffile.setEnabled(false);
                                            price.setEnabled(false);

                                            saves.setVisibility(View.GONE);
                                            proceed.setVisibility(View.VISIBLE);
                                            cancel.setVisibility(View.VISIBLE);


                                        } else {
                                            Toast.makeText(getContext(), "Select PDF File", Toast.LENGTH_SHORT).show();
                                        }
                                    } else if (nopdfbtn.isChecked()) {
                                        data.put("Course_PDF", "No PDF");

                                            selectimage.setEnabled(false);
                                            coursename.setEnabled(false);
                                            coursedescription.setEnabled(false);
                                            spinner.setEnabled(false);
                                            academic.setEnabled(false);
                                            nonacademic.setEnabled(false);
                                            competative.setEnabled(false);
                                            uploadpdfbtn.setEnabled(false);
                                            nopdfbtn.setEnabled(false);
                                            premium.setEnabled(false);
                                            free.setEnabled(false);
                                            uploadpdffile.setEnabled(false);
                                            price.setEnabled(false);

                                            saves.setVisibility(View.GONE);
                                            proceed2.setVisibility(View.VISIBLE);
                                            cancel.setVisibility(View.VISIBLE);


                                    } else {
                                        Toast.makeText(getContext(), "Select whether you provide PDF or not !", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(getContext(), "Select the course Thumbnail to proceed", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(getContext(), "Enter the price for your course", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getContext(), "Select the Category to proceed", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        coursedescription.setError("Course description required");
                        Toast.makeText(getContext(), "Enter Course description", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    coursename.setError("Course Name required");
                    Toast.makeText(getContext(), "Enter Course Name", Toast.LENGTH_SHORT).show();
                }

            }
        });

        academic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(view.getContext(),R.array.Academic, android.R.layout.simple_spinner_item);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(adapter);
            }
        });

        nonacademic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(view.getContext(),R.array.NonAcademic, android.R.layout.simple_spinner_item);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(adapter);
            }
        });

        competative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(view.getContext(),R.array.Competative, android.R.layout.simple_spinner_item);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(adapter);
            }
        });

        uploadpdfbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadpdffile.setVisibility(View.VISIBLE);
            }
        });

        nopdfbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadpdffile.setVisibility(View.INVISIBLE);
            }
        });

        premium.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DocumentReference dbs = mfirestore.collection("USERS").document(UserID);
                dbs.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String r = documentSnapshot.getString("Phone");
                        if (r!=null) {
                            new AlertDialog.Builder(getContext())
                                    .setTitle("UPI ID verification")
                                    .setMessage("Amount will be transferred to the "+r+" UPI ID.")
                                    .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    }).setNegativeButton("change", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    EditText change = new EditText(v.getContext());
                                    AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(v.getContext());
                                    passwordResetDialog.setTitle("Change UPI ID ");
                                    passwordResetDialog.setMessage("Enter your UPI ID.");
                                    passwordResetDialog.setView(change);
                                    passwordResetDialog.setCancelable(false);

                                    passwordResetDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                            String mail = change.getText().toString();

                                            if (!(mail.isEmpty())) {
                                                dbs.update("Phone",mail).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        new AlertDialog.Builder(getContext())
                                                                .setTitle("UPI ID verification")
                                                                .setMessage("Amount will be transferred to the "+mail+" UPI ID.")
                                                                .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(DialogInterface dialog, int which) {
                                                                        dialog.dismiss();
                                                                    }
                                                                }).show();
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(getContext(), "Error while updating your data. Please contact Team Abhyahas" , Toast.LENGTH_LONG).show();
                                                    }
                                                });

                                            } else {
                                                change.setError("Enter UPI ID !");
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
                                }
                            }).show();
                        }
                        else  {

                            EditText change = new EditText(v.getContext());
                            AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(v.getContext());
                            passwordResetDialog.setTitle("UPI ID REQUIRED");
                            passwordResetDialog.setMessage("Enter your UPI ID.");
                            passwordResetDialog.setView(change);
                            passwordResetDialog.setCancelable(false);

                            passwordResetDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    String mail = change.getText().toString();

                                    if (!(mail.isEmpty())) {
                                        dbs.update("Phone",mail).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                new AlertDialog.Builder(getContext())
                                                        .setTitle("UPI ID verification")
                                                        .setMessage("Amount will be transfered to the "+mail+" UPI ID.")
                                                        .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                dialog.dismiss();
                                                            }
                                                        }).show();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(getContext(), "Error while updating your data. Please contact Team Abhyahas" , Toast.LENGTH_LONG).show();
                                            }
                                        });

                                    } else {
                                        change.setError("Enter UPI ID !");
                                    }
                                }
                            }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    premium.setChecked(false);
                                    free.setChecked(true);
                                }
                            }).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Your UPI ID is not updated. Update them in My Profile Page", Toast.LENGTH_SHORT).show();
                    }
                });
                price.setVisibility(View.VISIBLE);
            }
        });

        free.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                price.setVisibility(View.INVISIBLE);
            }
        });

        back2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction ftt = getFragmentManager().beginTransaction();
                ftt.replace(R.id.fragment_container, new Fragments.UploadFragment()).commit();
            }
        });

        uploadpdffile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent filechooser = new Intent(Intent.ACTION_GET_CONTENT);
                filechooser.setType("application/pdf");
                filechooser = Intent.createChooser(filechooser, "Select your PDF file");
                startActivityForResult(filechooser, REQ_TOKEN);
            }
        });

        selectimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent chooser = new Intent(Intent.ACTION_GET_CONTENT);
                chooser.setType("image/*");
                chooser = Intent.createChooser(chooser, "Select your Course Thumbnail");
                startActivityForResult(chooser, REQ_TOKEN2);
            }
        });

        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mfirestore.collection("HOME").document(text).collection("LISTITEM").document(name).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            Toast.makeText(getContext(), " There is already a course named "+name+" in the "+text+" category. Try to give course another name ", Toast.LENGTH_SHORT).show();
                        } else {
                            proceed.setVisibility(View.GONE);
                            cancel.setVisibility(View.GONE);

                            pd.setCancelable(false);
                            pd.setTitle("Saving data");
                            pd.show();
                            pd.setMessage("Saving all your data please wait for a while and don't quit");

                            loaddata();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), " Something went wrong ! Try again ", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        proceed2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mfirestore.collection("HOME").document(text).collection("LISTITEM").document(name).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            Toast.makeText(getContext(), " There is already a course named "+name+" in the "+text+" category. Try to give course another name ", Toast.LENGTH_SHORT).show();
                        } else {
                            proceed.setVisibility(View.GONE);
                            cancel.setVisibility(View.GONE);

                            pd.setCancelable(false);
                            pd.setTitle("Saving data");
                            pd.show();
                            pd.setMessage("Saving all your data please wait for a while and don't quit");

                            loaddata2();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), " Something went wrong ! Try again ", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                selectimage.setEnabled(true);
                coursename.setEnabled(true);
                coursedescription.setEnabled(true);
                spinner.setEnabled(true);
                academic.setEnabled(true);
                nonacademic.setEnabled(true);
                competative.setEnabled(true);
                uploadpdfbtn.setEnabled(true);
                nopdfbtn.setEnabled(true);
                premium.setEnabled(true);
                free.setEnabled(true);
                uploadpdffile.setEnabled(true);
                price.setEnabled(true);

                proceed.setVisibility(View.GONE);
                proceed2.setVisibility(View.GONE);
                cancel.setVisibility(View.GONE);
                saves.setVisibility(View.VISIBLE);

            }
        });

        return view;
    }

    private void encryptFile(String filePath,String encnamevideo) throws IOException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        int read;
        FileInputStream fis = new FileInputStream(new File(filePath));
        File enc = new File(valueOf(getActivity().getExternalFilesDir("/Uploaded Courses/"+name)));
        File outfile = new File(enc,encnamevideo);
        if(!outfile.exists())
            outfile.createNewFile();

        FileOutputStream fos = new FileOutputStream(outfile);

        Cipher encipher = Cipher.getInstance("AES");
        encipher.init(Cipher.ENCRYPT_MODE, skey);
        CipherInputStream cis = new CipherInputStream(fis, encipher);

        byte[] buffer = new byte[1024]; // buffer can read file line by line to increase speed
        while((read = cis.read(buffer)) >= 0)
        {
            fos.write(buffer, 0, read);
            fos.flush();
        }
        fos.close();
    }


    public void loaddata() {

        mfirestore.collection("HOME").document(text).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                num = documentSnapshot.getDouble(UserID);
                pend = documentSnapshot.getDouble("pending");
                admin = documentSnapshot.getDouble("amount_status");

                if(admin == null) {
                    mfirestore.collection("HOME").document(text).update("amount_status",0.00);
                }

                if(num == null) {
                    num = 0.00;
                }

                if(pend == null) {
                    pend = 0.00;
                }

                HashMap<String,Double> hmap = new HashMap<>();
                hmap.put(UserID,num+1.00);
                hmap.put("pending",pend+1.00);

                mfirestore.collection("HOME").document(text).set(clg,SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {


                        mfirestore.collection("HOME").document(text).set(hmap,SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                mfirestore.collection("HOME").document(text).collection("LISTITEM").document(name).set(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        HashMap<String,String> extra = new HashMap<>();

                                        final StorageReference pdfref = mstorage.child("Courses").child(name).child("Course PDF");
                                        try {
                                            encryptFile(route,"course Pdf");
                                        } catch (IOException e) {
                                            Toast.makeText(getContext(), "Error while uploading. Try after sometime! "+e.toString(), Toast.LENGTH_SHORT).show();
                                            FragmentTransaction ftt = getFragmentManager().beginTransaction();
                                            ftt.replace(R.id.fragment_container, new Fragments.UploadFragment()).commit();
                                        } catch (NoSuchPaddingException e) {
                                            Toast.makeText(getContext(), "Error while uploading. Try after sometime! "+e.toString(), Toast.LENGTH_SHORT).show();
                                            FragmentTransaction ftt = getFragmentManager().beginTransaction();
                                            ftt.replace(R.id.fragment_container, new Fragments.UploadFragment()).commit();
                                        } catch (NoSuchAlgorithmException e) {
                                            Toast.makeText(getContext(), "Error while uploading. Try after sometime! "+e.toString(), Toast.LENGTH_SHORT).show();
                                            FragmentTransaction ftt = getFragmentManager().beginTransaction();
                                            ftt.replace(R.id.fragment_container, new Fragments.UploadFragment()).commit();
                                        } catch (InvalidKeyException e) {
                                            Toast.makeText(getContext(), "Error while uploading. Try after sometime! "+e.toString(), Toast.LENGTH_SHORT).show();
                                            FragmentTransaction ftt = getFragmentManager().beginTransaction();
                                            ftt.replace(R.id.fragment_container, new Fragments.UploadFragment()).commit();
                                        }
                                        File pdf = new File(valueOf(getActivity().getExternalFilesDir("/Uploaded Courses/"+name)),"course Pdf");
                                        pdfref.putFile(Uri.fromFile(pdf)).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                            @Override
                                            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                                                double progressPercent = (100.00 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                                                pd.setMessage("Progress: " + (int) progressPercent + "%");
                                            }
                                        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                pdfref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                    @Override
                                                    public void onSuccess(Uri uri) {
                                                        extra.put("Course_PDF", valueOf(uri));
                                                        mfirestore.collection("HOME").document(text).collection("LISTITEM").document(name).set(extra, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {

                                                                HashMap<String,String> extraa = new HashMap<>();

                                                                final StorageReference imageref = mstorage.child("Courses").child(name).child("Course Image");

                                                                imageref.putFile(image).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                                    @Override
                                                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                                        imageref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                                            @Override
                                                                            public void onSuccess(Uri uri) {
                                                                                extraa.put("Course_image", valueOf(uri));
                                                                                mfirestore.collection("HOME").document(text).collection("LISTITEM").document(name).set(extraa,SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                    @Override
                                                                                    public void onSuccess(Void aVoid) {
                                                                                        HashMap<String,Double> cash = new HashMap<>();
                                                                                        cash.put("amount_generated",0.00);
                                                                                        cash.put("amount_pending",0.00);
                                                                                        mfirestore.collection("HOME").document(text).collection("LISTITEM").document(name).set(cash,SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                            @Override
                                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                                pd.dismiss();
                                                                                                Bundle bundle1 = new Bundle();
                                                                                                bundle1.putString("name",name);
                                                                                                bundle1.putString("text",text);

                                                                                                Toast.makeText(getContext(), "Data saved successfully. Continue on uploading videos", Toast.LENGTH_SHORT).show();

                                                                                                VideoUploadingFragment frag1 = new VideoUploadingFragment();
                                                                                                frag1.setArguments(bundle1);
                                                                                                getFragmentManager()
                                                                                                        .beginTransaction()
                                                                                                        .replace(R.id.fragment_container,frag1)
                                                                                                        .commit();
                                                                                            }
                                                                                        }).addOnFailureListener(new OnFailureListener() {
                                                                                            @Override
                                                                                            public void onFailure(@NonNull Exception e) {
                                                                                                pd.dismiss();
                                                                                                Toast.makeText(getContext(), "Error ! "+ e.toString(), Toast.LENGTH_SHORT).show();
                                                                                                FragmentTransaction ftt = getFragmentManager().beginTransaction();
                                                                                                ftt.replace(R.id.fragment_container, new Fragments.UploadFragment()).commit();
                                                                                            }
                                                                                        });
                                                                                    }
                                                                                }).addOnFailureListener(new OnFailureListener() {
                                                                                    @Override
                                                                                    public void onFailure(@NonNull Exception e) {
                                                                                        pd.dismiss();
                                                                                        Toast.makeText(getContext(), "Error ! "+ e.toString(), Toast.LENGTH_SHORT).show();
                                                                                        FragmentTransaction ftt = getFragmentManager().beginTransaction();
                                                                                        ftt.replace(R.id.fragment_container, new Fragments.UploadFragment()).commit();
                                                                                    }
                                                                                });
                                                                            }
                                                                        }).addOnFailureListener(new OnFailureListener() {
                                                                            @Override
                                                                            public void onFailure(@NonNull Exception e) {
                                                                                pd.dismiss();
                                                                                Toast.makeText(getContext(), "Error ! "+ e.toString(), Toast.LENGTH_SHORT).show();
                                                                                FragmentTransaction ftt = getFragmentManager().beginTransaction();
                                                                                ftt.replace(R.id.fragment_container, new Fragments.UploadFragment()).commit();
                                                                            }
                                                                        });
                                                                    }
                                                                }).addOnFailureListener(new OnFailureListener() {
                                                                    @Override
                                                                    public void onFailure(@NonNull Exception e) {
                                                                        pd.dismiss();
                                                                        Toast.makeText(getContext(), "Error ! "+ e.toString(), Toast.LENGTH_SHORT).show();
                                                                        FragmentTransaction ftt = getFragmentManager().beginTransaction();
                                                                        ftt.replace(R.id.fragment_container, new Fragments.UploadFragment()).commit();
                                                                    }
                                                                });

                                                            }
                                                        }).addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                pd.dismiss();
                                                                Toast.makeText(getContext(), "Error ! "+ e.toString(), Toast.LENGTH_SHORT).show();
                                                                FragmentTransaction ftt = getFragmentManager().beginTransaction();
                                                                ftt.replace(R.id.fragment_container, new Fragments.UploadFragment()).commit();
                                                            }
                                                        });
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        pd.dismiss();
                                                        Toast.makeText(getContext(), "Error ! "+ e.toString(), Toast.LENGTH_SHORT).show();
                                                        FragmentTransaction ftt = getFragmentManager().beginTransaction();
                                                        ftt.replace(R.id.fragment_container, new Fragments.UploadFragment()).commit();
                                                    }
                                                });
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                pd.dismiss();
                                                Toast.makeText(getContext(), "Error ! "+ e.toString(), Toast.LENGTH_SHORT).show();
                                                FragmentTransaction ftt = getFragmentManager().beginTransaction();
                                                ftt.replace(R.id.fragment_container, new Fragments.UploadFragment()).commit();
                                            }
                                        });

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        pd.dismiss();
                                        Toast.makeText(getContext(), "Error ! "+ e.toString(), Toast.LENGTH_SHORT).show();
                                        FragmentTransaction ftt = getFragmentManager().beginTransaction();
                                        ftt.replace(R.id.fragment_container, new Fragments.UploadFragment()).commit();
                                    }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                pd.dismiss();
                                Toast.makeText(getContext(), "Error ! "+ e.toString(), Toast.LENGTH_SHORT).show();
                                FragmentTransaction ftt = getFragmentManager().beginTransaction();
                                ftt.replace(R.id.fragment_container, new Fragments.UploadFragment()).commit();
                            }
                        });


                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), e.toString(), Toast.LENGTH_SHORT).show();
                        FragmentTransaction ftt = getFragmentManager().beginTransaction();
                        ftt.replace(R.id.fragment_container, new Fragments.UploadFragment()).commit();
                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                num = 0.00;
                Toast.makeText(getContext(), e.toString(), Toast.LENGTH_SHORT).show();
                FragmentTransaction ftt = getFragmentManager().beginTransaction();
                ftt.replace(R.id.fragment_container, new Fragments.UploadFragment()).commit();
            }
        });

    }

    public void loaddata2() {


        mfirestore.collection("HOME").document(text).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                num = documentSnapshot.getDouble(UserID);
                pend = documentSnapshot.getDouble("pending");
                admin = documentSnapshot.getDouble("amount_status");

                if(admin == null) {
                    mfirestore.collection("HOME").document(text).update("amount_status",0.00);
                }

                if(num == null) {
                    num = 0.00;
                }

                if(pend == null) {
                    pend = 0.00;
                }


                HashMap<String,Double> hmap = new HashMap<>();
                hmap.put(UserID,num+1.00);
                hmap.put("pending",pend+1.00);

                mfirestore.collection("HOME").document(text).set(clg,SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {


                        mfirestore.collection("HOME").document(text).set(hmap,SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                mfirestore.collection("HOME").document(text).collection("LISTITEM").document(name).set(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        HashMap<String,String> extra = new HashMap<>();

                                        final StorageReference imageref = mstorage.child("Courses").child(name).child("Course Image");

                                        imageref.putFile(image).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                imageref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                    @Override
                                                    public void onSuccess(Uri uri) {
                                                        extra.put("Course_image", String.valueOf(uri));
                                                        mfirestore.collection("HOME").document(text).collection("LISTITEM").document(name).set(extra,SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {

                                                                HashMap<String,Double> cash = new HashMap<>();
                                                                cash.put("amount_generated",0.00);
                                                                cash.put("amount_pending",0.00);

                                                                mfirestore.collection("HOME").document(text).collection("LISTITEM").document(name).set(cash, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        pd.dismiss();
                                                                        Bundle bundle1 = new Bundle();
                                                                        bundle1.putString("name",name);
                                                                        bundle1.putString("text",text);

                                                                        Toast.makeText(getContext(), "Data saved successfully. Continue on uploading videos", Toast.LENGTH_SHORT).show();

                                                                        VideoUploadingFragment frag1 = new VideoUploadingFragment();
                                                                        frag1.setArguments(bundle1);
                                                                        getFragmentManager()
                                                                                .beginTransaction()
                                                                                .replace(R.id.fragment_container,frag1)
                                                                                .commit();
                                                                    }
                                                                });
                                                            }
                                                        }).addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                pd.dismiss();
                                                                Toast.makeText(getContext(), "Error ! "+ e.toString(), Toast.LENGTH_SHORT).show();
                                                                FragmentTransaction ftt = getFragmentManager().beginTransaction();
                                                                ftt.replace(R.id.fragment_container, new Fragments.UploadFragment()).commit();
                                                            }
                                                        });
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        pd.dismiss();
                                                        Toast.makeText(getContext(), "Error ! "+ e.toString(), Toast.LENGTH_SHORT).show();
                                                        FragmentTransaction ftt = getFragmentManager().beginTransaction();
                                                        ftt.replace(R.id.fragment_container, new Fragments.UploadFragment()).commit();
                                                    }
                                                });
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                pd.dismiss();
                                                Toast.makeText(getContext(), "Error ! "+ e.toString(), Toast.LENGTH_SHORT).show();
                                                FragmentTransaction ftt = getFragmentManager().beginTransaction();
                                                ftt.replace(R.id.fragment_container, new Fragments.UploadFragment()).commit();
                                            }
                                        });

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        pd.dismiss();
                                        Toast.makeText(getContext(), "Error ! "+ e.toString(), Toast.LENGTH_SHORT).show();
                                        FragmentTransaction ftt = getFragmentManager().beginTransaction();
                                        ftt.replace(R.id.fragment_container, new Fragments.UploadFragment()).commit();
                                    }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                pd.dismiss();
                                Toast.makeText(getContext(), "Error ! "+ e.toString(), Toast.LENGTH_SHORT).show();
                                FragmentTransaction ftt = getFragmentManager().beginTransaction();
                                ftt.replace(R.id.fragment_container, new Fragments.UploadFragment()).commit();
                            }
                        });


                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), e.toString(), Toast.LENGTH_SHORT).show();
                        FragmentTransaction ftt = getFragmentManager().beginTransaction();
                        ftt.replace(R.id.fragment_container, new Fragments.UploadFragment()).commit();
                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), e.toString(), Toast.LENGTH_SHORT).show();
                FragmentTransaction ftt = getFragmentManager().beginTransaction();
                ftt.replace(R.id.fragment_container, new Fragments.UploadFragment()).commit();
            }
        });

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        text = parent.getItemAtPosition(position).toString();
        Toast.makeText(view.getContext(), text+" Selected ", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        Toast.makeText(getView().getContext(), "Select the category", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode ==REQ_TOKEN && resultCode == -1 && data != null) {
            if (data.getData() != null) {

                path = data.getData();

                // Get the Uri of the selected file
                Uri uri = data.getData();
                String uriString = uri.toString();
                File myFile = new File(uriString);
                //    route = myFile.getAbsolutePath();
                route = RealPathUtil.getRealPath(getContext(), data.getData());
                if(route==null) {
                    Toast.makeText(getContext(), "PDF from the folder you selected is not supportable. Try from another folder", Toast.LENGTH_SHORT).show();
                } else {
                    File file = new File(route);
                    long length = file.length();

                    if (length < 5000000) {
                        path = data.getData();
                        uploadpdffile.setText("CHANGE PDF");
                        Toast.makeText(getContext(), "Course PDF Selected", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Select PDF than 5 MB", Toast.LENGTH_SHORT).show();
                        Intent intent;
                        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
                        {
                            intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
                        }
                        else
                        {
                            intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.INTERNAL_CONTENT_URI);
                        }
                        intent.setType("application/pdf");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        intent.putExtra("return-data", true);
                        startActivityForResult(intent,REQ_TOKEN);
                    }
                    String displayName = null;

                    if (uriString.startsWith("content://")) {
                        Cursor cursor = null;
                        try {
                            cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
                            if (cursor != null && cursor.moveToFirst()) {
                                displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                            }
                        } finally {
                            cursor.close();
                        }
                    } else if (uriString.startsWith("file://")) {
                        displayName = myFile.getName();
                    }
                    uploadpdffile.setText(displayName);
                }
            } else {
                Toast.makeText(getContext(), "Failed to select PDF", Toast.LENGTH_LONG).show();
            }
        }
        if (requestCode == REQ_TOKEN2 && resultCode == -1 && data != null) {

            if (data.getData() != null) {

                pathh = RealPathUtil.getRealPath(getContext(), data.getData());

                if(pathh==null) {
                    Toast.makeText(getContext(), "Thumbnail from the folder you selected is not supportable. Try from another folder", Toast.LENGTH_SHORT).show();
                } else {

                    File file = new File(pathh);
                    long length = file.length();

                    if (length < 1000000) {
                        image = data.getData();
                        photo.setImageURI(image);
                        selectimage.setText("CHANGE COURSE THUMBNAIL");
                    } else {
                        Toast.makeText(getContext(), "Select thumbnail than 1 MB", Toast.LENGTH_SHORT).show();
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
                        startActivityForResult(intent,REQ_TOKEN2);
                    }
                }
            } else {
                Toast.makeText(getContext(), "Failed to select thumbnail", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        getActivity().registerReceiver(networkChangeListener,filter);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        getActivity().unregisterReceiver(networkChangeListener);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }
}