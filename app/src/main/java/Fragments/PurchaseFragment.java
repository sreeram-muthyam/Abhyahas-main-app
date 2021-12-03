package Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.PlaybackParams;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.Transaction;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;
import com.squareup.picasso.Picasso;
import com.sreerammuthyam.abhyahas.R;
import com.sreerammuthyam.abhyahas.payment_Activity;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import static java.lang.String.valueOf;

public class PurchaseFragment extends Fragment {

    ImageView rev2;
    TextView here, CourseName, CreaterName, Descreption, Price, PDFCheck, INName, enrolments;
    String courseName, VIurl, CollId, CreName, desc1, Pri, PdfC, IntroName, CreIma, userID, verify, enrol, back, creator, Total_Enrolments;
    PlayerView playerView2;
    Button purchase,repurchase;
    ImageView btFullscreen, resize, profile;
    SimpleExoPlayer exoPlayer2;
    View heading, leftside, rightside;
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth mAuth;
    boolean flag = false;
    int size = 0;
    StorageReference mstorage;
    private boolean playWhenReady = true;
    ChipNavigationBar navBar;
    ProgressDialog pd;
    View view;
    Double num;
    String mon = "false";
    RadioGroup speedgroup;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if(savedInstanceState==null) {
            view = inflater.inflate(R.layout.fragment_purchase, container, false);
        }
        //View view = inflater.inflate(R.layout.fragment_purchase, container, false);

        rev2 = view.findViewById(R.id.rev2);
        here = view.findViewById(R.id.here);
        profile = view.findViewById(R.id.profile_purchase);
        CourseName = view.findViewById(R.id.Course_Name);
        CreaterName = view.findViewById(R.id.CreaterName);
        Descreption = view.findViewById(R.id.desc);
        Price = view.findViewById(R.id.Price_id);
        heading = view.findViewById(R.id.heading);
        leftside = view.findViewById(R.id.leftside3);
        rightside = view.findViewById(R.id.rightside3);
        PDFCheck = view.findViewById(R.id.CheckPDf);
        INName = view.findViewById(R.id.Intro_Name);
        purchase = view.findViewById(R.id.purchase);
        enrolments = view.findViewById(R.id.enrolments_purchase);
        repurchase = view.findViewById(R.id.repurchase);
        playerView2 = view.findViewById(R.id.player_view);
        speedgroup = playerView2.findViewById(R.id.RadioSpeed);
        btFullscreen = playerView2.findViewById(R.id.bt_fullscreen);
        resize = playerView2.findViewById(R.id.resize);


        SearchView searchView = (SearchView) getActivity().findViewById(R.id.task);
        searchView.setVisibility(View.GONE);

        speedgroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i){

                    case R.id.Speed0_5x:
                        PlaybackParams param1 = new PlaybackParams();
                        param1.setSpeed(0.5f);// 1f is 1x, 2f is 2x
                        exoPlayer2.setPlaybackParams(param1);
                        break;
                    case R.id.Speed1x:
                        PlaybackParams param2 = new PlaybackParams();
                        param2.setSpeed(1f);// 1f is 1x, 2f is 2x
                        exoPlayer2.setPlaybackParams(param2);
                        break;
                    case R.id.Speed1_5x:
                        PlaybackParams param3 = new PlaybackParams();
                        param3.setSpeed(1.5f);// 1f is 1x, 2f is 2x
                        exoPlayer2.setPlaybackParams(param3);
                        break;
                    case R.id.Speed2x:
                        PlaybackParams param4 = new PlaybackParams();
                        param4.setSpeed(2f);// 1f is 1x, 2f is 2x
                        exoPlayer2.setPlaybackParams(param4);
                        break;

                }
            }
        });

        firebaseFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getCurrentUser().getUid();
        mstorage = FirebaseStorage.getInstance().getReference();

        navBar = getActivity().findViewById(R.id.bottom_nav_menu_id);

        pd = new ProgressDialog(getContext());

        //Checkout.preload(getContext());

        Bundle bundle1 = this.getArguments();
        if (bundle1 != null) {
            courseName = (String) bundle1.get("ViName");
            VIurl = (String) bundle1.get("ViUrl");
            CollId = (String) bundle1.get("CoId");
            CreName = (String) bundle1.get("CreNa");
            CreIma = (String) bundle1.get("CreIm");
            desc1 = (String) bundle1.get("Desc");
            Pri = (String) bundle1.get("Pr");
            PdfC = (String) bundle1.get("PDF");
            IntroName = (String) bundle1.get("IName");
            verify = (String) bundle1.get("verify");
            enrol = (String) bundle1.get("enrol");
            back = (String) bundle1.get("back");
            creator = (String) bundle1.get("creator");
            Total_Enrolments = (String) bundle1.get("Total_Enrolments");
        }

        CourseName.setText(courseName);
        CreaterName.setText(CreName);
        Descreption.setText(desc1);
        enrolments.setText("Number of students enrolled : " + Total_Enrolments);
        Picasso.get().load(Uri.parse(CreIma)).into(profile);
        INName.setText(IntroName);

        if (PdfC.equals("No PDF")) {
            PDFCheck.setText("PDF is not available to this course");
        } else {
            PDFCheck.setText("PDF is available to this course");
        }

        if (Pri.equals("FREE")) {
            purchase.setText("Enrol for free");
            Price.setText("Coures Price: " + Pri);
        } else {
            Price.setText("Coures Price: Rs. " + Pri);
        }

        if (creator.equals(userID)) {
            purchase.setVisibility(View.INVISIBLE);
        }
        File AccFile = new File(valueOf(getActivity().getExternalFilesDir("/Purchased Courses")),userID+courseName+"Purchased");
        if (verify != null) {
            if (verify.equals("true")&& AccFile.exists()) {
                purchase.setEnabled(false);
                purchase.setText("Already enrolled");
            }else if(verify.equals("true")&& !(AccFile.exists())){
                purchase.setVisibility(View.GONE);
                repurchase.setVisibility(View.VISIBLE);
            }
        }

        repurchase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isOnline()) {
                    if (Pri.equals("FREE")) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setMessage("Do you want to re-enroll for this course for free ?");
                        builder.setTitle("Confirmation !!!");
                        builder.setCancelable(false);
                        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                pd.setTitle("RE-PURCHASING");
                                pd.setMessage("Wait for a while until course is added to your access list");
                                pd.setCancelable(false);
                                pd.show();
                                File AccFile = new File(valueOf(getActivity().getExternalFilesDir("/Purchased Courses/"+userID+courseName+"Purchased")));
                                try {
                                    AccFile.createNewFile();
                                } catch (IOException e) {
                                    Toast.makeText(getContext(), "Error while adding course into your access list", Toast.LENGTH_SHORT).show();
                                    e.printStackTrace();
                                }

                                navBar.setItemSelected(R.id.a, true);
                                Toast.makeText(getContext(), "Now " + courseName + " will open in your access section. Happy Learning :) ", Toast.LENGTH_SHORT).show();
                                pd.dismiss();
                                FragmentTransaction ftt = getFragmentManager().beginTransaction();
                                ftt.replace(R.id.fragment_container, new AccessFragment()).commit();
                            }
                        });
                        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setMessage("Do you want to re-enroll for this paid course ?");
                        builder.setTitle("Confirmation !!!");
                        builder.setCancelable(false);
                        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mon = "true";
                                HashMap<String, String> hashMap = new HashMap<>();
                                hashMap.put("price",Pri);
                                hashMap.put("coursename",courseName);
                                hashMap.put("collid",CollId);
                                hashMap.put("mon",mon);
                                hashMap.put("from","access");
                                firebaseFirestore.collection("USERS").document(userID).collection("TRANSACTIONS").document("ontheway").set(hashMap,SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Intent intent = new Intent(getActivity(), payment_Activity.class);
                                        startActivity(intent);
                                        getActivity().finish();
                                    }
                                });
                            }
                        });
                        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                    }
                } else {
                    Toast.makeText(getContext(), " check your INTERNET connection ", Toast.LENGTH_SHORT).show();
                }
            }
        });

        purchase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isOnline()) {
                    if (Pri.equals("FREE")) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setMessage("Do you want to enrol for this course for free ?");
                        builder.setTitle("Confirmation !!!");
                        builder.setCancelable(false);
                        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                enrolcourse();
                            }
                        });
                        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                    } else {
                        //Toast.makeText(getContext(), "Please update the app to purchase the course", Toast.LENGTH_SHORT).show();
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setMessage("Do you want to enrol for the paid course ?");
                        builder.setTitle("Confirmation !!!");
                        builder.setCancelable(false);
                        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                HashMap<String, String> hashMap = new HashMap<>();
                                hashMap.put("price",Pri);
                                hashMap.put("coursename",courseName);
                                hashMap.put("collid",CollId);
                                hashMap.put("mon",mon);
                                hashMap.put("from","access");
                                firebaseFirestore.collection("USERS").document(userID).collection("TRANSACTIONS").document("ontheway").set(hashMap,SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Intent intent = new Intent(getActivity(), payment_Activity.class);
                                        startActivity(intent);
                                        getActivity().finish();
                                    }
                                });
                            }
                        }); builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                    }
                } else {
                    Toast.makeText(getContext(), " Check your INTERNET connection ", Toast.LENGTH_SHORT).show();
                }
            }
        });

        rev2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (back.equals("home")) {
                    FragmentTransaction ftt = getFragmentManager().beginTransaction();
                    ftt.replace(R.id.fragment_container, new Home()).commit();
                } else {
                    Bundle bundle = new Bundle();
                    bundle.putString("CN", CollId);
                    SeeallFragment frag2 = new SeeallFragment();
                    frag2.setArguments(bundle);
                    getFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragment_container, frag2)
                            .commit();
                }
            }
        });

        here.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

           if (isOnline()) {
               Bundle bundle2 = new Bundle();
               bundle2.putString("Doc", courseName);
               bundle2.putString("CoId", CollId);
               bundle2.putString("url", VIurl);
               bundle2.putString("CR", CreName);
               bundle2.putString("PRI", Pri);
               bundle2.putString("CreIm", CreIma);
               bundle2.putString("DE", desc1);
               bundle2.putString("PD", PdfC);
               bundle2.putString("IN", IntroName);
               bundle2.putString("verify", verify);
               bundle2.putString("enrol", enrol);
               bundle2.putString("back", back);
               bundle2.putString("creator", creator);
               bundle2.putString("Total_Enrolments", Total_Enrolments);

               CoursecontentFragment frag2 = new CoursecontentFragment();
               frag2.setArguments(bundle2);
               getFragmentManager()
                       .beginTransaction()
                       .replace(R.id.fragment_container, frag2)
                       .commit();
           } else {
               Toast.makeText(getContext(), " Check your INTERNET connection ", Toast.LENGTH_SHORT).show();
           }

            }
        });

        btFullscreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view1) {
                if (flag) {
                    btFullscreen.setImageDrawable(getResources().getDrawable(R.drawable.ic_fullscreen));

                    if (((AppCompatActivity) getActivity()).getSupportActionBar() != null) {
                        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
                    }
                    ((AppCompatActivity) getActivity()).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) playerView2.getLayoutParams();
                    params.width = params.MATCH_PARENT;
                    params.height = (int) (200 * view.getContext().getResources().getDisplayMetrics().density);
                    playerView2.setLayoutParams(params);

                    CourseName.setVisibility(View.VISIBLE);
                    rev2.setVisibility(View.VISIBLE);
                    navBar.setVisibility(View.VISIBLE);
                    heading.setVisibility(View.VISIBLE);
                    leftside.setVisibility(View.VISIBLE);
                    rightside.setVisibility(View.VISIBLE);
                    flag = false;
                } else {
                    btFullscreen.setImageDrawable(getResources().getDrawable(R.drawable.ic_fullscreen_exit));

                    if (((AppCompatActivity) getActivity()).getSupportActionBar() != null) {
                        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
                    }

                    ((AppCompatActivity) getActivity()).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) playerView2.getLayoutParams();
                    params.width = params.MATCH_PARENT;
                    params.height = params.MATCH_PARENT;
                    playerView2.setLayoutParams(params);
                    CourseName.setVisibility(View.GONE);
                    rev2.setVisibility(View.GONE);
                    navBar.setVisibility(View.GONE);
                    heading.setVisibility(View.GONE);
                    leftside.setVisibility(View.GONE);
                    rightside.setVisibility(View.GONE);

                    flag = true;
                }
            }
        });

        resize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resizefun();
            }
        });

        return view;
    }

    private boolean isOnline() {
        ConnectivityManager connMgr = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }


    public void enrolcourse() {
        pd.setTitle("PURCHASING");
        pd.setMessage("Wait for a while until course is added to your access list");
        pd.setCancelable(false);
        pd.show();
        // 1) Getting no. of accessed courses in that college
        // 2) Get enrolments and Total Enrolments from the course details
        // 3) Set user id true in course details
        // 4) Increase accessed courses number
        // 5) Increase enrolments and total enrolments
        firebaseFirestore.runTransaction(new Transaction.Function<Void>() {
            @Nullable
            @Override
            public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                DocumentReference collegeref = firebaseFirestore.collection("HOME").document(CollId);
                DocumentSnapshot noofaccess = transaction.get(collegeref);
                DocumentReference userref = firebaseFirestore.collection("USERS").document(userID);
                DocumentSnapshot usersnap = transaction.get(userref);
                num = noofaccess.getDouble(userID + "access");
                DocumentReference courseref = firebaseFirestore.collection("HOME").document(CollId).collection("LISTITEM").document(courseName);
                DocumentSnapshot coursedet = transaction.get(courseref);
                String enrolments = coursedet.getString("Enrolments");
                String totalenrolments = coursedet.getString("Total_Enrolments");
                String courses_accessed = usersnap.getString("courses_accessed");
                if (num==null) {
                    num=0.00;
                }
                if (enrolments==null) {
                    enrolments="0";
                }
                if (totalenrolments==null) {
                    totalenrolments="0";
                }
                int en = Integer.parseInt(enrolments);
                int To_en = Integer.parseInt(totalenrolments);
                num = num + 1.00;
                enrolments = String.valueOf(en+1);
                totalenrolments = String.valueOf(To_en+1);
                transaction.update(collegeref,userID+"access",num);
                HashMap<String, String> cr = new HashMap<>();
                cr.put("Enrolments",enrolments);
                cr.put("Total_Enrolments",totalenrolments);
                cr.put(userID,"true");
                transaction.set(courseref,cr,SetOptions.merge());
                transaction.update(userref,"courses_accessed",String.valueOf(Integer.parseInt(courses_accessed)+1));

                return null;
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                File AccFile = new File(valueOf(getActivity().getExternalFilesDir("/Purchased Courses/"+userID+courseName+"Purchased")));
                try {
                    AccFile.createNewFile();
                } catch (IOException e) {
                    Toast.makeText(getContext(), "Error while adding course into your access list", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }

                navBar.setItemSelected(R.id.a, true);
                Toast.makeText(getContext(), "Now " + courseName + " will be in your access section. Happy Learning :) ", Toast.LENGTH_SHORT).show();
                pd.dismiss();
                FragmentTransaction ftt = getFragmentManager().beginTransaction();
                ftt.replace(R.id.fragment_container, new AccessFragment()).commit();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "Error while purchasing the course.", Toast.LENGTH_SHORT).show();
                pd.dismiss();
            }
        });
    }


    public void resizefun() {

        switch (size) {

            case 0:
                playerView2.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_ZOOM);
                Toast.makeText(getView().getContext(), "ZOOM mode", Toast.LENGTH_SHORT).show();
                size = 1;
                break;

            case 1:
                playerView2.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
                Toast.makeText(getView().getContext(), "FIT mode", Toast.LENGTH_SHORT).show();
                size = 2;
                break;

            case 2:
                playerView2.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FILL);
                Toast.makeText(getView().getContext(), "FILL mode", Toast.LENGTH_SHORT).show();
                size = 3;
                break;

            case 3:
                playerView2.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIXED_HEIGHT);
                Toast.makeText(getView().getContext(), "FIXED HEIGHT mode", Toast.LENGTH_SHORT).show();
                size = 4;
                break;

            case 4:
                playerView2.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIXED_WIDTH);
                Toast.makeText(getView().getContext(), "FIXED WIDTH mode", Toast.LENGTH_SHORT).show();
                size = 0;
                break;

        }

    }

    private void initializePlayer(String video) {
        exoPlayer2 = ExoPlayerFactory.newSimpleInstance(
                new DefaultRenderersFactory(getActivity()),
                new DefaultTrackSelector(), new DefaultLoadControl());

        playerView2.setPlayer(exoPlayer2);

        //   exoPlayer2.setPlayWhenReady(true);
        exoPlayer2.setPlayWhenReady(false);
        //      exoPlayer2.seekTo(currentWindow, playbackPosition);

        Uri uri = Uri.parse(video);
        MediaSource mediaSource = buildMediaSource(uri);
        exoPlayer2.prepare(mediaSource,false, false);



    }


    private MediaSource buildMediaSource(Uri uri) {
        return new ExtractorMediaSource.Factory(
                new DefaultDataSourceFactory(getActivity(),"Exoplayer-local")).
                createMediaSource(uri);
    }

    private void releasePlayer() {
        if (exoPlayer2 != null) {
            playWhenReady = exoPlayer2.getPlayWhenReady();
            //      playbackPosition = exoPlayer2.getCurrentPosition();
            //     currentWindow = exoPlayer2.getCurrentWindowIndex();
            exoPlayer2.release();
            exoPlayer2 = null;
        }
    }
    @Override
    public void onPause() {
        super.onPause();
        if (Util.SDK_INT < 24) {
            releasePlayer();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (Util.SDK_INT >= 24) {
            releasePlayer();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (Util.SDK_INT >= 24) {
            releasePlayer();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if ((Util.SDK_INT < 24 || exoPlayer2 == null)) {
            initializePlayer(VIurl);
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        if (Util.SDK_INT >= 24) {
            initializePlayer(VIurl);
        }
    }
}
