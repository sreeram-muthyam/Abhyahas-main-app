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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.Transaction;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;
import com.squareup.picasso.Picasso;
import com.sreerammuthyam.abhyahas.R;
import com.sreerammuthyam.abhyahas.payment_Activity;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import Interface.SubOnClickInterface;
import Model.CollegeItemModel;
import Model.ItemModel;

import static java.lang.String.valueOf;

public class UploadFragment extends Fragment {

    FloatingActionButton fab;
    String creatorname, userID, creatorImg, title, message;
    private RecyclerView UploadRec;
    private FirebaseFirestore firebaseFirestore2;
    private FirebaseAuth mAuth;
    private FirestoreRecyclerAdapter adapter6;
    private FirestoreRecyclerAdapter adapter7;
    DocumentReference introdbref, mref;
    StorageReference mstorage;
    ProgressDialog pd, sd;
    View view;
    ChipNavigationBar navBar;
    Double upload_status;
    SearchView searchView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if(savedInstanceState==null) {
            view= inflater.inflate(R.layout.fragment_upload, container, false);
        }
        //View view= inflater.inflate(R.layout.fragment_upload, container, false);

        UploadRec = view.findViewById(R.id.Upload_Rec);
        firebaseFirestore2 = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        UploadRec.setHasFixedSize(true);
        UploadRec.setLayoutManager(new LinearLayoutManager(view.getContext()));
        fab =  view.findViewById(R.id.fab_btn);

        mstorage = FirebaseStorage.getInstance().getReference();

        navBar = getActivity().findViewById(R.id.bottom_nav_menu_id);

        userID = mAuth.getCurrentUser().getUid();

        pd = new ProgressDialog(getContext());
        sd = new ProgressDialog(getContext());

        ImageView profile = getActivity().findViewById(R.id.profilepp);
        profile.setEnabled(true);

        ChipNavigationBar chipNavigationBar = getActivity().findViewById(R.id.bottom_nav_menu_id);
        chipNavigationBar.setItemEnabled(R.id.b,true);
        chipNavigationBar.setItemEnabled(R.id.a,true);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isOnline()) {
                    firebaseFirestore2.collection("USERS").document(userID).collection("TRANSACTIONS").document("ontheway").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            upload_status = documentSnapshot.getDouble("upload_status");
                            if (upload_status==null) {
                                upload_status = 0.00;
                            }
                            //admin accepting or denying
                            mref = firebaseFirestore2.collection("ADMIN").document("upload");
                            mref.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    title = documentSnapshot.getString("title");
                                    message = documentSnapshot.getString("message");
                                    Double d = documentSnapshot.getDouble("d");
                                    if(d==1.00 && upload_status == 0.00) {
                                        new AlertDialog.Builder(getContext())
                                                .setIcon(R.mipmap.ic_launcher)
                                                .setTitle(title)
                                                .setMessage(message)
                                                .setCancelable(false)
                                                .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        return;
                                                    }
                                                }).show();
                                    } else {
                                        //if accepting show instructions
                                        new AlertDialog.Builder(getContext())
                                                .setCancelable(false)
                                                .setIcon(R.mipmap.ic_launcher)
                                                .setTitle("INSTRUCTIONS BEFORE UPLOADING THE COURSE")
                                                .setMessage("\nYOU NEED TO PAY 10 Rs. TO UPLOAD A NEW COURSE AND AFTER PAYING 10 Rs. YOU WILL GET MAX 3 ATTEMPTS TO UPLOAD THE COURSE\n\n"+"1) Your course Thumbnail must follow an aspect ratio of 16:9 (Preferred 1280x720 pixels) and must be less than 1 MB\n\n" +
                                                        "2) Your course must contain Introduction Video of Minimum 30-45 seconds and the size must be less than 5 MB\n\n3) Your course videos should be in 16:9 ratio and you can upload up to max 10 videos and size of each video must be less than 40 MB" +
                                                        "\n\n4) Course description should contain complete details regarding your course like introduction, contents, etc...\n\n" +
                                                        "5) Course with PDF notes is mostly Preferred and in case you upload PDF, size must be less than 5 MB\n\n6) If the course is Premium, price must be in between 50.Rs and 100.Rs\n\n" +
                                                        "7) Once your course is enrolled by a student, Course price will be directly transferred to your UPI ID on every Saturdays (Only if Course is Premium)" +
                                                        "\n\n8) Your course will be reviewed by Team Abhyahas and accept if everything is fine or else course will be rejected and removed from the servers"+
                                                        "\n\nFor more details visit Uploading rules and regulations in the website.")
                                                .setNeutralButton("BACK", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        dialog.dismiss();
                                                    }
                                                })
                                                .setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        //after continue show dialog box to pay or skip if already payed
                                                        firebaseFirestore2.collection("USERS").document(userID).collection("TRANSACTIONS").document("ontheway").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                            @Override
                                                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                                upload_status = documentSnapshot.getDouble("upload_status");
                                                                if (upload_status==null) {
                                                                    upload_status = 0.00;
                                                                }
                                                                if(upload_status>0.00) {
                                                                    //dialog box for confirmation
                                                                    new AlertDialog.Builder(getContext())
                                                                            .setTitle("CONFIRMATION")
                                                                            .setMessage("If you click on CONTINUE, your chances for uploading will reduce from "+upload_status+" to "+(upload_status-1.00)+".\nAre you sure you want to continue ?")
                                                                            .setPositiveButton("CONTINUE", new DialogInterface.OnClickListener() {
                                                                                @Override
                                                                                public void onClick(DialogInterface dialog, int which) {
                                                                                    firebaseFirestore2.collection("USERS").document(userID).collection("TRANSACTIONS").document("ontheway").update("upload_status",upload_status-1.00);
                                                                                    dialog.dismiss();
                                                                                    FragmentTransaction ftt = getFragmentManager().beginTransaction();
                                                                                    ftt.replace(R.id.fragment_container, new Newcourseuploadfragment()).commit();
                                                                                }
                                                                            }).setNegativeButton("BACK", new DialogInterface.OnClickListener() {
                                                                        @Override
                                                                        public void onClick(DialogInterface dialog, int which) {
                                                                            return;
                                                                        }
                                                                    }).show();
                                                                } else {
                                                                    //move to payment
                                                                    new AlertDialog.Builder(getContext())
                                                                            .setIcon(R.mipmap.ic_launcher)
                                                                            .setTitle("Upload New Course")
                                                                            .setMessage("You need to pay 10 Rs before you start uploading a new course. You will get up to 3 chances to upload the course.")
                                                                            .setPositiveButton("PAY", new DialogInterface.OnClickListener() {
                                                                                @Override
                                                                                public void onClick(DialogInterface dialog, int which) {
                                                                                    HashMap<String, String> hashMap = new HashMap<>();
                                                                                    hashMap.put("from","upload");
                                                                                    firebaseFirestore2.collection("USERS").document(userID).collection("TRANSACTIONS").document("ontheway").set(hashMap,SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                        @Override
                                                                                        public void onSuccess(Void aVoid) {
                                                                                            Intent intent = new Intent(getActivity(), payment_Activity.class);
                                                                                            startActivity(intent);
                                                                                            getActivity().finish();
                                                                                        }
                                                                                    }).addOnFailureListener(new OnFailureListener() {
                                                                                        @Override
                                                                                        public void onFailure(@NonNull Exception e) {
                                                                                            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                                                                        }
                                                                                    });
                                                                                }
                                                                            })
                                                                            .setNegativeButton("CLOSE", null)
                                                                            .show();
                                                                }
                                                            }
                                                        });
                                                    }

                                                }).setNegativeButton("Visit website", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                                Toast.makeText(getContext(), "Redirecting to Abhyahas website", Toast.LENGTH_SHORT).show();
                                                Intent BroeseIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://sites.google.com/view/sampleabhyahas/home"));
                                                startActivity(BroeseIntent);
                                            }

                                        }).show();
                                    }
                                }
                            });
                        }
                    });
                } else {
                    Toast.makeText(getContext(), " Check your INTERNET connection ", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Query query = firebaseFirestore2.collection("HOME").whereGreaterThan(userID,0);

        FirestoreRecyclerOptions<CollegeItemModel> options7 = new  FirestoreRecyclerOptions.Builder<CollegeItemModel>()
                .setQuery(query,CollegeItemModel.class)
                .build();

        searchView = (SearchView) getActivity().findViewById(R.id.task);
        searchView.setVisibility(View.VISIBLE);

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                UploadFragment frag = new UploadFragment();
                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, frag)
                        .commit();
                return false;
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                proccessSearch(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                proccessSearch(s);
                return false;
            }
        });

        adapter7 = new FirestoreRecyclerAdapter<CollegeItemModel,UpColViewHolder>(options7){
            @NonNull
            @Override
            public UpColViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view1 = LayoutInflater.from(parent.getContext()).inflate(R.layout.college_item,parent,false);
                return new UpColViewHolder(view1);
            }

            @Override
            protected void onBindViewHolder(@NonNull UpColViewHolder holder, int position, @NonNull CollegeItemModel model) {

                Query query6 = firebaseFirestore2.collection("HOME").document(model.getCollege_Name()).collection("LISTITEM").whereEqualTo("Creator_ID",userID);

                FirestoreRecyclerOptions<ItemModel> options6 = new FirestoreRecyclerOptions.Builder<ItemModel>()
                        .setQuery(query6,ItemModel.class)
                        .build();

                adapter6 = new FirestoreRecyclerAdapter<ItemModel,UploadViewHolder>(options6){
                    @NonNull
                    @Override
                    public UploadViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item,parent,false);
                        return new UploadViewHolder(v);
                    }

                    @Override
                    protected void onBindViewHolder(@NonNull UploadViewHolder holder6, int position, @NonNull ItemModel model6) {

                        introdbref = firebaseFirestore2.collection("HOME").document(model.getCollege_Name()).collection("LISTITEM").document(model6.getCourse_Name());

                        String final_status = model6.getFinal_submit();
                        if(final_status==null) {
                            final_status = "PENDING";
                        }

                        if (model6.getIntro_Url() != null && final_status.equals("DONE")) {

                            holder.but1.setVisibility(View.INVISIBLE);
                            holder.list_Colname.setVisibility(View.VISIBLE);
                            holder.list_Colname.setText(model.getCollege_Name());

                            DocumentReference dref = firebaseFirestore2.collection("USERS").document(model6.getCreater_ID());

                            dref.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    creatorname = documentSnapshot.getString("Full_Name");
                                    creatorImg = documentSnapshot.getString("Image");
                                }
                            });


                                if (model6.getStatus().equals("pending")) {
                                    holder6.txt_item__title.setText("STATUS : Pending");
                                } else {
                                    holder6.txt_item__title.setText("No. of students enrolled : " + model6.getTotal_Enrolments());
                                }


                            holder6.txt_Videoname.setText(model6.getCourse_Name());
                            Picasso.get().load(model6.getCourse_image()).into(holder6.image_item);
                            if (model6.getCourse_Price().equals("FREE")) {
                                holder6.PriceId.setText("Free");
                            } else {
                                holder6.PriceId.setText("Rs. " + model6.getCourse_Price() + "/-");
                            }

                            holder6.SubInterfaceClick(new SubOnClickInterface() {
                                @Override
                                public void OnClick(View view, boolean isLongPressed) {
                                    Bundle bundle6 = new Bundle();

                                    File Upfile = new File(valueOf(getActivity().getExternalFilesDir("/Uploaded Courses/")),model6.getCourse_Name());

                                    if(Upfile.exists()){

                                        if (model6.getTotal_Enrolments()!=null&&creatorname!=null&&creatorImg!=null&&model6.getCourse_Price()!=null&&model6.getCourse_PDF()!=null&&model6.getIntro_Url()!=null&&
                                        model6.getCourse_Description()!=null&&model.getCollege_Name()!=null&&model6.getEnrolments()!=null&&model6.getCourse_Name()!=null&&model6.getIntro_Name()!=null) {

                                            bundle6.putString("CreName",creatorname);
                                            bundle6.putString("CreImg",creatorImg);
                                            bundle6.putString("Pri",model6.getCourse_Price());
                                            bundle6.putString("PDF",model6.getCourse_PDF());
                                            bundle6.putString("Vurl",model6.getIntro_Url());
                                            bundle6.putString("Desc",model6.getCourse_Description());
                                            bundle6.putString("CoID",model.getCollege_Name());
                                            bundle6.putString("enrol",model6.getEnrolments());
                                            bundle6.putString("Total_Enrolments",model6.getTotal_Enrolments());
                                            bundle6.putString("coursename",model6.getCourse_Name());
                                            bundle6.putString("ViName",model6.getIntro_Name());
                                            bundle6.putString("amount_generated",String.valueOf(model6.getAmount_generated()));
                                            UploadedcoursesFragment frag6 = new UploadedcoursesFragment();
                                            frag6.setArguments(bundle6);
                                            getFragmentManager()
                                                    .beginTransaction()
                                                    .replace(R.id.fragment_container,frag6)
                                                    .commit();

                                        } else {
                                            Toast.makeText(getContext(), "   Loading...   ", Toast.LENGTH_SHORT).show();
                                        }

                                    }else {
                                   //     Toast.makeText(getContext(), "This course is not present in your mobile, this is because you may have uninstalled app. If any problem contact Team Abhyahas", Toast.LENGTH_SHORT).show();
                                        new AlertDialog.Builder(getContext())
                                                .setCancelable(false)
                                                .setTitle("Course not available")
                                                .setMessage("This course is not present in your mobile, this is because you might have uninstalled app. If any problem contact Team Abhyahas")
                                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {
                                                        dialogInterface.dismiss();
                                                    }
                                                }).setNegativeButton("Transactions", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                                if(model6.getCourse_Price().equals("FREE")) {
                                                    Toast.makeText(getContext(), "There is no transaction details for the FREE courses", Toast.LENGTH_SHORT).show();
                                                } else {

                                                    Bundle bundle6 = new Bundle();
                                                    bundle6.putString("CreName",creatorname);
                                                    bundle6.putString("CreImg",creatorImg);
                                                    bundle6.putString("ViName",model6.getIntro_Name());
                                                    bundle6.putString("Pri",model6.getCourse_Price());
                                                    bundle6.putString("PDF",model6.getCourse_PDF());
                                                    bundle6.putString("Vurl",model6.getIntro_Url());
                                                    bundle6.putString("Desc",model6.getCourse_Description());
                                                    bundle6.putString("CoID",model.getCollege_Name());
                                                    bundle6.putString("enrol",model6.getEnrolments());
                                                    bundle6.putString("Total_Enrolments",model6.getTotal_Enrolments());
                                                    bundle6.putString("coursename",model6.getCourse_Name());
                                                    bundle6.putString("to","upload");

                                                    navBar.setItemSelected(R.id.a,false);
                                                    navBar.setItemSelected(R.id.b,false);
                                                    navBar.setItemSelected(R.id.c,false);

                                                    TermsandPrivacyFragment frag = new TermsandPrivacyFragment();
                                                    frag.setArguments(bundle6);
                                                    getFragmentManager()
                                                            .beginTransaction()
                                                            .replace(R.id.fragment_container, frag)
                                                            .commit();
                                                }
                                            }
                                        }).setNeutralButton("Remove", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                new AlertDialog.Builder(getContext())
                                                        .setCancelable(false)
                                                        .setTitle("Confirmation")
                                                        .setMessage("This course will be removed entirely from the Abhyahas platform and you can never retrieve it back and all  your transactions related to this course will be closed( if it is a premium course ")
                                                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialogInterface, int i) {

                                                                // 1) Clgref userupload reduce by 1
                                                                // 2) Courseupload change CREATORID
                                                                // 3) Change status to delete
                                                                // 4) Delete local files
                                                                // 5) If pending decrease pending by 1
                                                                // 6) If accepted decrease accepted by 1
                                                                // 7) If enrolments are 0 then increase deleted by 1 and change status to deleted

                                                                pd.setTitle("Removing...");
                                                                pd.setMessage("Wait for a while until "+model6.getCourse_Name()+" is removed from here");
                                                                pd.setCancelable(false);
                                                                pd.show();

                                                                firebaseFirestore2.runTransaction(new Transaction.Function<Void>() {
                                                                    @Nullable
                                                                    @Override
                                                                    public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                                                                        DocumentReference userref = firebaseFirestore2.collection("USERS").document(userID);
                                                                        DocumentSnapshot usersnap = transaction.get(userref);
                                                                        DocumentReference clgref = firebaseFirestore2.collection("HOME").document(model.getCollege_Name());
                                                                        DocumentReference crsref = firebaseFirestore2.collection("HOME").document(model.getCollege_Name()).collection("LISTITEM").document(model6.getCourse_Name());
                                                                        DocumentSnapshot clgsnap = transaction.get(clgref);
                                                                        DocumentSnapshot crssnap = transaction.get(crsref);

                                                                        Double userupload = clgsnap.getDouble(userID);
                                                                        String status = crssnap.getString("Status");
                                                                        String enrolments = crssnap.getString("Enrolments");
                                                                        String courses_uploaded = usersnap.getString("courses_uploaded");

                                                                        transaction.update(userref,"courses_uploaded",String.valueOf(Integer.parseInt(courses_uploaded)-1));

                                                                        if (status.equals("accepted")) {
                                                                            Double accepted = clgsnap.getDouble("accepted");
                                                                            HashMap<String, Double> accepthash = new HashMap<>();
                                                                            accepthash.put("accepted",accepted-1.00);
                                                                            accepthash.put(userID,userupload-1.00);
                                                                            transaction.set(clgref,accepthash,SetOptions.merge());
                                                                            HashMap<String, String> crshash = new HashMap<>();
                                                                            crshash.put("Status","delete");
                                                                            crshash.put("Creator_ID",userID+" deleted");
                                                                            transaction.set(crsref,crshash,SetOptions.merge());
                                                                        }

                                                                        if (status.equals("pending")) {
                                                                            Double pending = clgsnap.getDouble("pending");
                                                                            HashMap<String, Double> accepthash = new HashMap<>();
                                                                            accepthash.put("pending",pending-1.00);
                                                                            accepthash.put(userID,userupload-1.00);
                                                                            transaction.set(clgref,accepthash,SetOptions.merge());
                                                                            HashMap<String, String> crshash = new HashMap<>();
                                                                            crshash.put("Status","delete");
                                                                            crshash.put("Creator_ID",userID+" deleted");
                                                                            transaction.set(crsref,crshash,SetOptions.merge());
                                                                        }

                                                                        if (enrolments.equals("0")) {
                                                                            Double deleted = clgsnap.getDouble("deleted");
                                                                            if (deleted==null) {
                                                                                deleted = 0.00;
                                                                            }
                                                                            transaction.update(clgref,"deleted",deleted+1.00);
                                                                            transaction.update(crsref,"Status","deleted");
                                                                        }

                                                                        return null;
                                                                    }
                                                                }).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void aVoid) {
                                                                        File delfile = new File(valueOf(getActivity().getExternalFilesDir("Uploaded Courses/" + model6.getCourse_Name())));
                                                                        deleteDirectory(delfile);
                                                                        pd.dismiss();
                                                                        Toast.makeText(getContext(), "Course Removed successfully", Toast.LENGTH_SHORT).show();
                                                                        FragmentTransaction ftt = getFragmentManager().beginTransaction();
                                                                        ftt.replace(R.id.fragment_container, new UploadFragment()).commit();
                                                                    }
                                                                }).addOnFailureListener(new OnFailureListener() {
                                                                    @Override
                                                                    public void onFailure(@NonNull Exception e) {
                                                                        pd.dismiss();
                                                                        Toast.makeText(getContext(), "Error while removing the course. Contact Team Abhyahas", Toast.LENGTH_SHORT).show();
                                                                    }
                                                                });
                                                            }
                                                        }).setNeutralButton("Back", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        dialog.dismiss();
                                                    }
                                                })
                                                        .show();
                                            }
                                        }).show();
                                    }
                                }
                            });

                        } else {


                            sd.setTitle("Removing unwanted data");
                            sd.setMessage("Wait for few seconds");
                            sd.setCancelable(false);
                            sd.show();

                            File delfile1 = new File(valueOf(getActivity().getExternalFilesDir("Uploaded Courses/" + model6.getCourse_Name())));

                            firebaseFirestore2.collection("HOME").document(model.getCollege_Name()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    if (delfile1.exists()) {
                                        deleteDirectory(delfile1);
                                    }
                                    HashMap<String, Double> hmap = new HashMap<>();
                                    hmap.put(userID, documentSnapshot.getDouble(userID) - 1.00);
                                    hmap.put("pending", documentSnapshot.getDouble("pending") - 1.00);

                                    firebaseFirestore2.collection("HOME").document(model.getCollege_Name()).set(hmap, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            CollectionReference collectionReference = introdbref.collection("ALL VIDEOS");
                                            collectionReference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                @Override
                                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                    if (!(queryDocumentSnapshots.isEmpty())) {
                                                        List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                                                        if (!(list.isEmpty())) {
                                                            for (DocumentSnapshot d : list) {
                                                                String id = d.getId();
                                                                firebaseFirestore2.collection("HOME").document(model.getCollege_Name()).collection("LISTITEM").document(model6.getCourse_Name()).collection("ALL VIDEOS").document(d.getId())
                                                                        .delete();
                                                                final StorageReference vidref = mstorage.child("Courses").child(model6.getCourse_Name()).child(id);
                                                                vidref.delete();
                                                            }
                                                        }
                                                        if (model6.getIntro_Name()!=null) {
                                                            final StorageReference intr = mstorage.child("Courses").child(model6.getCourse_Name()).child(model6.getIntro_Name());
                                                            intr.delete();
                                                        }
                                                    }
                                                }
                                            });
                                            introdbref.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {

                                                    final StorageReference imageref = mstorage.child("Courses").child(model6.getCourse_Name()).child("Course Image");
                                                    final StorageReference pdfref = mstorage.child("Courses").child(model6.getCourse_Name()).child("Course PDF");

                                                    imageref.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            if(model6.getCourse_PDF()!=null) {
                                                                pdfref.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void aVoid) {
                                                                        sd.dismiss();
                                                                        FragmentTransaction ftt = getFragmentManager().beginTransaction();
                                                                        ftt.replace(R.id.fragment_container, new UploadFragment()).commit();
                                                                    }
                                                                }).addOnFailureListener(new OnFailureListener() {
                                                                    @Override
                                                                    public void onFailure(@NonNull Exception e) {
                                                                        sd.dismiss();
                                                                        FragmentTransaction ftt = getFragmentManager().beginTransaction();
                                                                        ftt.replace(R.id.fragment_container, new UploadFragment()).commit();
                                                                    }
                                                                });
                                                            } else {
                                                                sd.dismiss();
                                                                FragmentTransaction ftt = getFragmentManager().beginTransaction();
                                                                ftt.replace(R.id.fragment_container, new UploadFragment()).commit();
                                                            }
                                                        }
                                                    }).addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            sd.dismiss();
                                                            Toast.makeText(getContext(), e.toString(), Toast.LENGTH_SHORT).show();
                                                            FragmentTransaction ftt = getFragmentManager().beginTransaction();
                                                            ftt.replace(R.id.fragment_container, new UploadFragment()).commit();
                                                        }
                                                    });
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    sd.dismiss();
                                                    Toast.makeText(getContext(), "Something error occured", Toast.LENGTH_SHORT).show();
                                                    FragmentTransaction ftt = getFragmentManager().beginTransaction();
                                                    ftt.replace(R.id.fragment_container, new UploadFragment()).commit();
                                                }
                                            });
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(getContext(), e.toString(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getContext(), e.toString(), Toast.LENGTH_SHORT).show();
                                }
                            });

                        }

                    }
                };

                adapter6.startListening();
                adapter6.notifyDataSetChanged();
                holder.RecViewInner.setAdapter(adapter6);

            }
        };
        adapter7.startListening();
        adapter7.notifyDataSetChanged();
        UploadRec.setAdapter(adapter7);
        setHasOptionsMenu(true);

        return view;
    }

    private boolean isOnline() {
        ConnectivityManager connMgr = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    private class UploadViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView txt_item__title;
        TextView txt_Videoname,PriceId;
        ImageView image_item;
        private SubOnClickInterface subOnClickInterface;

        public UploadViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_item__title = itemView.findViewById(R.id.tv_sub_Title);
            txt_Videoname = itemView.findViewById(R.id.tvTitle);
            image_item = itemView.findViewById(R.id.itemImage);
            PriceId = itemView.findViewById(R.id.item_price);

            itemView.setOnClickListener(this);

        }
        public void SubInterfaceClick(SubOnClickInterface subOnClickInterface){
            this.subOnClickInterface = subOnClickInterface;
        }

        @Override
        public void onClick(View vi) {
            subOnClickInterface.OnClick(vi,false);
        }
    }

    private class UpColViewHolder extends RecyclerView.ViewHolder {
        RecyclerView RecViewInner;
        public RecyclerView.LayoutManager manager;
        private TextView list_Colname;
        private TextView but1;
        View v;

        public UpColViewHolder(@NonNull View itemView) {
            super(itemView);
            RecViewInner = itemView.findViewById(R.id.recycler_view_list);

            manager = new LinearLayoutManager(itemView.getContext(),LinearLayoutManager.HORIZONTAL,false);
            RecViewInner.setLayoutManager(manager);

            list_Colname = itemView.findViewById(R.id.list_Col_name);
            but1 = itemView.findViewById(R.id.SHOW_ALL);
            but1.setVisibility(View.INVISIBLE);
            list_Colname.setVisibility(View.INVISIBLE);
        }


    }

    private class UpColViewHolder2 extends RecyclerView.ViewHolder {
        RecyclerView RecViewInner;
        public RecyclerView.LayoutManager manager;

        public UpColViewHolder2(@NonNull View itemView) {
            super(itemView);
            RecViewInner = itemView.findViewById(R.id.recycler_view_list);
            manager = new LinearLayoutManager(itemView.getContext(),LinearLayoutManager.HORIZONTAL,false);
            RecViewInner.setLayoutManager(manager);
        }


    }

    private void proccessSearch(String s) {

        String SR = s.toLowerCase();

        Query query = firebaseFirestore2.collection("HOME").whereGreaterThan(userID,0);

        FirestoreRecyclerOptions<CollegeItemModel> options7 = new  FirestoreRecyclerOptions.Builder<CollegeItemModel>()
                .setQuery(query,CollegeItemModel.class)
                .build();

        adapter7 = new FirestoreRecyclerAdapter<CollegeItemModel,UpColViewHolder2>(options7){
            @NonNull
            @Override
            public UpColViewHolder2 onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view1 = LayoutInflater.from(parent.getContext()).inflate(R.layout.searchcollegeitem,parent,false);
                return new UpColViewHolder2(view1);
            }

            @Override
            protected void onBindViewHolder(@NonNull UpColViewHolder2 holder, int position, @NonNull CollegeItemModel model) {

                Query query6 = firebaseFirestore2.collection("HOME").document(model.getCollege_Name()).collection("LISTITEM").whereEqualTo("Creator_ID",userID);

                FirestoreRecyclerOptions<ItemModel> options6 = new FirestoreRecyclerOptions.Builder<ItemModel>()
                        .setQuery(query6.orderBy("Course_search").startAt(SR).endAt(SR + "\uf8ff"),ItemModel.class)
                        .build();

                adapter6 = new FirestoreRecyclerAdapter<ItemModel,UploadViewHolder>(options6){
                    @NonNull
                    @Override
                    public UploadViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item,parent,false);
                        return new UploadViewHolder(v);
                    }

                    @Override
                    protected void onBindViewHolder(@NonNull UploadViewHolder holder6, int position, @NonNull ItemModel model6) {

                        introdbref = firebaseFirestore2.collection("HOME").document(model.getCollege_Name()).collection("LISTITEM").document(model6.getCourse_Name());

                        String final_status = model6.getFinal_submit();
                        if(final_status==null) {
                            final_status = "PENDING";
                        }

                        if (model6.getIntro_Url() != null && final_status.equals("DONE")) {

                            DocumentReference dref = firebaseFirestore2.collection("USERS").document(model6.getCreater_ID());

                            dref.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    creatorname = documentSnapshot.getString("Full_Name");
                                    creatorImg = documentSnapshot.getString("Image");
                                }
                            });


                            if (model6.getStatus().equals("pending")) {
                                holder6.txt_item__title.setText("STATUS : Pending");
                            } else {
                                holder6.txt_item__title.setText("No. of students enrolled : " + model6.getTotal_Enrolments());
                            }


                            holder6.txt_Videoname.setText(model6.getCourse_Name());
                            Picasso.get().load(model6.getCourse_image()).into(holder6.image_item);
                            if (model6.getCourse_Price().equals("FREE")) {
                                holder6.PriceId.setText("Free");
                            } else {
                                holder6.PriceId.setText("Rs. " + model6.getCourse_Price() + "/-");
                            }

                            holder6.SubInterfaceClick(new SubOnClickInterface() {
                                @Override
                                public void OnClick(View view, boolean isLongPressed) {
                                    Bundle bundle6 = new Bundle();

                                    File Upfile = new File(valueOf(getActivity().getExternalFilesDir("/Uploaded Courses/")),model6.getCourse_Name());

                                    if(Upfile.exists()){

                                        if (model6.getTotal_Enrolments()!=null&&creatorname!=null&&creatorImg!=null&&model6.getCourse_Price()!=null&&model6.getCourse_PDF()!=null&&model6.getIntro_Url()!=null&&
                                                model6.getCourse_Description()!=null&&model.getCollege_Name()!=null&&model6.getEnrolments()!=null&&model6.getCourse_Name()!=null&&model6.getIntro_Name()!=null) {

                                            bundle6.putString("CreName",creatorname);
                                            bundle6.putString("CreImg",creatorImg);
                                            bundle6.putString("Pri",model6.getCourse_Price());
                                            bundle6.putString("PDF",model6.getCourse_PDF());
                                            bundle6.putString("Vurl",model6.getIntro_Url());
                                            bundle6.putString("Desc",model6.getCourse_Description());
                                            bundle6.putString("CoID",model.getCollege_Name());
                                            bundle6.putString("enrol",model6.getEnrolments());
                                            bundle6.putString("Total_Enrolments",model6.getTotal_Enrolments());
                                            bundle6.putString("coursename",model6.getCourse_Name());
                                            bundle6.putString("ViName",model6.getIntro_Name());
                                            bundle6.putString("amount_generated",String.valueOf(model6.getAmount_generated()));
                                            UploadedcoursesFragment frag6 = new UploadedcoursesFragment();
                                            frag6.setArguments(bundle6);
                                            getFragmentManager()
                                                    .beginTransaction()
                                                    .replace(R.id.fragment_container,frag6)
                                                    .commit();

                                        } else {
                                            Toast.makeText(getContext(), "   Loading...   ", Toast.LENGTH_SHORT).show();
                                        }

                                    }else {
                                        //     Toast.makeText(getContext(), "This course is not present in your mobile, this is because you may have uninstalled app. If any problem contact Team Abhyahas", Toast.LENGTH_SHORT).show();
                                        new AlertDialog.Builder(getContext())
                                                .setCancelable(false)
                                                .setTitle("Course not available")
                                                .setMessage("This course is not present in your mobile, this is because you might have uninstalled app. If any problem contact Team Abhyahas")
                                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {
                                                        dialogInterface.dismiss();
                                                    }
                                                }).setNegativeButton("Transactions", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                                if(model6.getCourse_Price().equals("FREE")) {
                                                    Toast.makeText(getContext(), "There is no transaction details for the FREE courses", Toast.LENGTH_SHORT).show();
                                                } else {

                                                    Bundle bundle6 = new Bundle();
                                                    bundle6.putString("CreName",creatorname);
                                                    bundle6.putString("CreImg",creatorImg);
                                                    bundle6.putString("ViName",model6.getIntro_Name());
                                                    bundle6.putString("Pri",model6.getCourse_Price());
                                                    bundle6.putString("PDF",model6.getCourse_PDF());
                                                    bundle6.putString("Vurl",model6.getIntro_Url());
                                                    bundle6.putString("Desc",model6.getCourse_Description());
                                                    bundle6.putString("CoID",model.getCollege_Name());
                                                    bundle6.putString("enrol",model6.getEnrolments());
                                                    bundle6.putString("Total_Enrolments",model6.getTotal_Enrolments());
                                                    bundle6.putString("coursename",model6.getCourse_Name());
                                                    bundle6.putString("to","upload");

                                                    navBar.setItemSelected(R.id.a,false);
                                                    navBar.setItemSelected(R.id.b,false);
                                                    navBar.setItemSelected(R.id.c,false);

                                                    TermsandPrivacyFragment frag = new TermsandPrivacyFragment();
                                                    frag.setArguments(bundle6);
                                                    getFragmentManager()
                                                            .beginTransaction()
                                                            .replace(R.id.fragment_container, frag)
                                                            .commit();
                                                }
                                            }
                                        }).setNeutralButton("Remove", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                new AlertDialog.Builder(getContext())
                                                        .setCancelable(false)
                                                        .setTitle("Confirmation")
                                                        .setMessage("This course will be removed entirely from the Abhyahas platform and you can never retrieve it back and all  your transactions related to this course will be closed( if it is a premium course ")
                                                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialogInterface, int i) {

                                                                // 1) Clgref userupload reduce by 1
                                                                // 2) Courseupload change CREATORID
                                                                // 3) Change status to delete
                                                                // 4) Delete local files
                                                                // 5) If pending decrease pending by 1
                                                                // 6) If accepted decrease accepted by 1
                                                                // 7) If enrolments are 0 then increase deleted by 1 and change status to deleted

                                                                pd.setTitle("Removing...");
                                                                pd.setMessage("Wait for a while until "+model6.getCourse_Name()+" is removed from here");
                                                                pd.setCancelable(false);
                                                                pd.show();

                                                                firebaseFirestore2.runTransaction(new Transaction.Function<Void>() {
                                                                    @Nullable
                                                                    @Override
                                                                    public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                                                                        DocumentReference userref = firebaseFirestore2.collection("USERS").document(userID);
                                                                        DocumentSnapshot usersnap = transaction.get(userref);
                                                                        DocumentReference clgref = firebaseFirestore2.collection("HOME").document(model.getCollege_Name());
                                                                        DocumentReference crsref = firebaseFirestore2.collection("HOME").document(model.getCollege_Name()).collection("LISTITEM").document(model6.getCourse_Name());
                                                                        DocumentSnapshot clgsnap = transaction.get(clgref);
                                                                        DocumentSnapshot crssnap = transaction.get(crsref);

                                                                        Double userupload = clgsnap.getDouble(userID);
                                                                        String status = crssnap.getString("Status");
                                                                        String enrolments = crssnap.getString("Enrolments");
                                                                        String courses_uploaded = usersnap.getString("courses_uploaded");

                                                                        transaction.update(userref,"courses_uploaded",String.valueOf(Integer.parseInt(courses_uploaded)-1));

                                                                        if (status.equals("accepted")) {
                                                                            Double accepted = clgsnap.getDouble("accepted");
                                                                            HashMap<String, Double> accepthash = new HashMap<>();
                                                                            accepthash.put("accepted",accepted-1.00);
                                                                            accepthash.put(userID,userupload-1.00);
                                                                            transaction.set(clgref,accepthash,SetOptions.merge());
                                                                            HashMap<String, String> crshash = new HashMap<>();
                                                                            crshash.put("Status","delete");
                                                                            crshash.put("Creator_ID",userID+" deleted");
                                                                            transaction.set(crsref,crshash,SetOptions.merge());
                                                                        }

                                                                        if (status.equals("pending")) {
                                                                            Double pending = clgsnap.getDouble("pending");
                                                                            HashMap<String, Double> accepthash = new HashMap<>();
                                                                            accepthash.put("pending",pending-1.00);
                                                                            accepthash.put(userID,userupload-1.00);
                                                                            transaction.set(clgref,accepthash,SetOptions.merge());
                                                                            HashMap<String, String> crshash = new HashMap<>();
                                                                            crshash.put("Status","delete");
                                                                            crshash.put("Creator_ID",userID+" deleted");
                                                                            transaction.set(crsref,crshash,SetOptions.merge());
                                                                        }

                                                                        if (enrolments.equals("0")) {
                                                                            Double deleted = clgsnap.getDouble("deleted");
                                                                            if (deleted==null) {
                                                                                deleted = 0.00;
                                                                            }
                                                                            transaction.update(clgref,"deleted",deleted+1.00);
                                                                            transaction.update(crsref,"Status","deleted");
                                                                        }

                                                                        return null;
                                                                    }
                                                                }).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void aVoid) {
                                                                        File delfile = new File(valueOf(getActivity().getExternalFilesDir("Uploaded Courses/" + model6.getCourse_Name())));
                                                                        deleteDirectory(delfile);
                                                                        pd.dismiss();
                                                                        Toast.makeText(getContext(), "Course Removed successfully", Toast.LENGTH_SHORT).show();
                                                                        FragmentTransaction ftt = getFragmentManager().beginTransaction();
                                                                        ftt.replace(R.id.fragment_container, new UploadFragment()).commit();
                                                                    }
                                                                }).addOnFailureListener(new OnFailureListener() {
                                                                    @Override
                                                                    public void onFailure(@NonNull Exception e) {
                                                                        pd.dismiss();
                                                                        Toast.makeText(getContext(), "Error while removing the course. Contact Team Abhyahas", Toast.LENGTH_SHORT).show();
                                                                    }
                                                                });
                                                            }
                                                        }).setNeutralButton("Back", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        dialog.dismiss();
                                                    }
                                                })
                                                        .show();
                                            }
                                        }).show();
                                    }
                                }
                            });

                        } else {


                            sd.setTitle("Removing unwanted data");
                            sd.setMessage("Wait for few seconds");
                            sd.setCancelable(false);
                            sd.show();

                            File delfile1 = new File(valueOf(getActivity().getExternalFilesDir("Uploaded Courses/" + model6.getCourse_Name())));

                            firebaseFirestore2.collection("HOME").document(model.getCollege_Name()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    if (delfile1.exists()) {
                                        deleteDirectory(delfile1);
                                    }
                                    HashMap<String, Double> hmap = new HashMap<>();
                                    hmap.put(userID, documentSnapshot.getDouble(userID) - 1.00);
                                    hmap.put("pending", documentSnapshot.getDouble("pending") - 1.00);

                                    firebaseFirestore2.collection("HOME").document(model.getCollege_Name()).set(hmap, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            CollectionReference collectionReference = introdbref.collection("ALL VIDEOS");
                                            collectionReference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                @Override
                                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                    if (!(queryDocumentSnapshots.isEmpty())) {
                                                        List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                                                        if (!(list.isEmpty())) {
                                                            for (DocumentSnapshot d : list) {
                                                                String id = d.getId();
                                                                firebaseFirestore2.collection("HOME").document(model.getCollege_Name()).collection("LISTITEM").document(model6.getCourse_Name()).collection("ALL VIDEOS").document(d.getId())
                                                                        .delete();
                                                                final StorageReference vidref = mstorage.child("Courses").child(model6.getCourse_Name()).child(id);
                                                                vidref.delete();
                                                            }
                                                        }
                                                        if (model6.getIntro_Name()!=null) {
                                                            final StorageReference intr = mstorage.child("Courses").child(model6.getCourse_Name()).child(model6.getIntro_Name());
                                                            intr.delete();
                                                        }
                                                    }
                                                }
                                            });
                                            introdbref.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {

                                                    final StorageReference imageref = mstorage.child("Courses").child(model6.getCourse_Name()).child("Course Image");
                                                    final StorageReference pdfref = mstorage.child("Courses").child(model6.getCourse_Name()).child("Course PDF");

                                                    imageref.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            if(model6.getCourse_PDF()!=null) {
                                                                pdfref.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void aVoid) {
                                                                        sd.dismiss();
                                                                        FragmentTransaction ftt = getFragmentManager().beginTransaction();
                                                                        ftt.replace(R.id.fragment_container, new UploadFragment()).commit();
                                                                    }
                                                                }).addOnFailureListener(new OnFailureListener() {
                                                                    @Override
                                                                    public void onFailure(@NonNull Exception e) {
                                                                        sd.dismiss();
                                                                        FragmentTransaction ftt = getFragmentManager().beginTransaction();
                                                                        ftt.replace(R.id.fragment_container, new UploadFragment()).commit();
                                                                    }
                                                                });
                                                            } else {
                                                                sd.dismiss();
                                                                FragmentTransaction ftt = getFragmentManager().beginTransaction();
                                                                ftt.replace(R.id.fragment_container, new UploadFragment()).commit();
                                                            }
                                                        }
                                                    }).addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            sd.dismiss();
                                                            Toast.makeText(getContext(), e.toString(), Toast.LENGTH_SHORT).show();
                                                            FragmentTransaction ftt = getFragmentManager().beginTransaction();
                                                            ftt.replace(R.id.fragment_container, new UploadFragment()).commit();
                                                        }
                                                    });
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    sd.dismiss();
                                                    Toast.makeText(getContext(), "Something error occured", Toast.LENGTH_SHORT).show();
                                                    FragmentTransaction ftt = getFragmentManager().beginTransaction();
                                                    ftt.replace(R.id.fragment_container, new UploadFragment()).commit();
                                                }
                                            });
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(getContext(), e.toString(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getContext(), e.toString(), Toast.LENGTH_SHORT).show();
                                }
                            });

                        }

                    }
                };

                adapter6.startListening();
                adapter6.notifyDataSetChanged();
                holder.RecViewInner.setAdapter(adapter6);

            }
        };

        adapter7.startListening();
        adapter7.notifyDataSetChanged();
        UploadRec.setAdapter(adapter7);


    }

    // For deletion of a course folder that exists in our local device..
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

    @Override
    public void onStart() {
        super.onStart();
        adapter7.startListening();
        if (!(searchView.isIconified())) {
            searchView.setQuery("",false);
            searchView.setIconified(true);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter7.stopListening();
    }
}