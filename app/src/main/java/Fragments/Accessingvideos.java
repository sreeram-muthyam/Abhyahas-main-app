package Fragments;

import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.media.PlaybackParams;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.util.SparseArray;
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
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
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
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.Transaction;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;
import com.squareup.picasso.Picasso;
import com.sreerammuthyam.abhyahas.R;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import Interface.SubOnClickInterface;
import Model.ContentModel;
import at.huber.youtubeExtractor.VideoMeta;
import at.huber.youtubeExtractor.YouTubeExtractor;
import at.huber.youtubeExtractor.YtFile;

import static java.lang.String.valueOf;

public class Accessingvideos extends Fragment {

    ImageView back, resize, btFullscreen,profile;
    Button info,content,pdf, remove;
    View pdfview;
    View infoview,leftside,rightside, heading, contentview;
    String CreName,creator,Videoname,DESC,Price,VideoUrl,Pdf,CoId, transaction,UserID,enrol, intronametext, downloadUrl, nameofvideo;
    TextView CN,VN,Descp,PRICE,ListVideoAc,trans,introname;
    PDFView pdfView;
    PlayerView playerView2;
    SimpleExoPlayer exoPlayer2;
    RecyclerView RecAccess;
    FirebaseFirestore dbs2;
    FirebaseAuth mAuth;
    StorageReference mstorage;
    private FirestoreRecyclerAdapter adapter10;
    boolean flag = false, flagflag = false;
    ProgressDialog progressDialog;
    SecretKey skey;
    int size = 0 ;
    private boolean playWhenReady = true;
    ProgressDialog pd, ppd;
    View view;
    private long downloadId;
    RadioGroup speedgroup;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if(savedInstanceState==null) {
            view = inflater.inflate(R.layout.fragment_accessingvideos, container, false);
        }
        //View view = inflater.inflate(R.layout.fragment_accessingvideos, container, false);
        dbs2 = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mstorage = FirebaseStorage.getInstance().getReference();

        UserID = mAuth.getCurrentUser().getUid();

        progressDialog = new ProgressDialog(getContext());
        ppd = new ProgressDialog(getContext());

        back = view.findViewById(R.id.rev);
        info = view.findViewById(R.id.infobtn1);
        content = view.findViewById(R.id.contentbtn1);
        pdf = view.findViewById(R.id.pdfbtn1);
        pdfview = view.findViewById(R.id.pdf1);
        infoview = view.findViewById(R.id.info1);
        contentview = view.findViewById(R.id.content1);
        playerView2 = view.findViewById(R.id.player_view3);
        RecAccess = view.findViewById(R.id.Access_Rec_View);
        leftside = view.findViewById(R.id.leftside3);
        rightside = view.findViewById(R.id.rightside3);
        heading = view.findViewById(R.id.headingg);
        profile = view.findViewById(R.id.profile_imageav);
        trans = view.findViewById(R.id.trans);
        CN = view.findViewById(R.id.Access_CreaterName);
        VN = view.findViewById(R.id.Access_CourseName);
        Descp = view.findViewById(R.id.Access_CourseDesc);
        PRICE = view.findViewById(R.id.Access_CoursePrice);
        ListVideoAc = view.findViewById(R.id.List_VideoName_Access);
        remove = view.findViewById(R.id.remove);
        introname = view.findViewById(R.id.Intro_Name_access);
        resize = playerView2.findViewById(R.id.resize);
        btFullscreen = playerView2.findViewById(R.id.bt_fullscreen);
        speedgroup = playerView2.findViewById(R.id.RadioSpeed);

        byte[] encodedKey     = Base64.decode( "fRIxFDSHKrBDUjseir8TSg==\n"+"    ", Base64.DEFAULT);
        skey = new SecretKeySpec(encodedKey, 0, encodedKey.length, "AES");

        SearchView searchView = (SearchView) getActivity().findViewById(R.id.task);
        searchView.setVisibility(View.GONE);

        pd = new ProgressDialog(getContext());

        Bundle bundle8 = this.getArguments();
        if(bundle8!=null){

            CreName =(String) bundle8.get("CreName");
            creator =(String) bundle8.get("creator");
            Videoname =(String) bundle8.get("ViName");
            DESC =(String)bundle8.get("Desc");
            Price = (String) bundle8.get("Pri");
            VideoUrl=(String) bundle8.get("Vurl");
            Pdf = (String)bundle8.get("PDF");
            CoId = (String) bundle8.get("CoID");
            enrol = (String) bundle8.get("enrol");
            intronametext = (String) bundle8.getString("Introname");

            Picasso.get().load(Uri.parse(creator)).into(profile);
            CN.setText(CreName);
            VN.setText(Videoname);
            Descp.setText(DESC);
            if (Price.equals("FREE")) {
                PRICE.setText("Free Course");
            } else {
                PRICE.setText("Course Price : Rs. " + Price + "/-");
            }

        }

        introname.setText(intronametext);

        if(Pdf.equals("No PDF")){
            pdf.setVisibility(View.INVISIBLE);
        }

        if(Price.equals("FREE")){
            trans.setText("This is the free course offered by "+CreName+". So, there will be no transaction details for this course");
        } else {

            dbs2.collection("HOME").document(CoId).collection("LISTITEM").document(Videoname).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {

                    transaction = documentSnapshot.getString(UserID+"+transaction");
                    trans.setText(transaction);
                }
            });
        }

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

        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage("Do you want to remove this course from your access section ?");
                builder.setTitle("Confirmation !!!");
                builder.setCancelable(false);
                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        pd.setTitle("Removing...");
                        pd.setMessage("Wait for a while until "+Videoname+" is removed from here");
                        pd.setCancelable(false);
                        pd.show();

                        // 1) Usersccess reduce by 1
                        // 2) Usercourse access false
                        // 3) Reduce enrolments by 1
                        // 4) Delete local files
                        // 5) Check whether it is deleting to admin

                        dbs2.runTransaction(new Transaction.Function<Void>() {
                            @Nullable
                            @Override
                            public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {

                                DocumentReference userref = dbs2.collection("USERS").document(UserID);
                                DocumentSnapshot usersnap = transaction.get(userref);
                                DocumentReference clgref = dbs2.collection("HOME").document(CoId);
                                DocumentReference crsref = dbs2.collection("HOME").document(CoId).collection("LISTITEM").document(Videoname);
                                DocumentSnapshot clgsnap = transaction.get(clgref);
                                DocumentSnapshot crssnap = transaction.get(crsref);

                                Double useraccess = clgsnap.getDouble(UserID+"access");
                                Double deleted = clgsnap.getDouble("deleted");
                                String enrolments = crssnap.getString("Enrolments");
                                String status = crssnap.getString("Status");
                                String courses_accessed = usersnap.getString("courses_accessed");

                                if(deleted==null)
                                {
                                    deleted=0.00;
                                }

                                transaction.update(clgref,UserID+"access",useraccess-1.00);
                                HashMap<String, String> crshash = new HashMap<>();
                                crshash.put("Enrolments",String.valueOf(Integer.parseInt(enrolments)-1));
                                crshash.put(UserID,"false");
                                transaction.set(crsref,crshash,SetOptions.merge());
                                transaction.update(userref,"courses_accessed",String.valueOf(Integer.parseInt(courses_accessed)-1));

                                if(enrolments.equals("1")&&status.equals("delete")) {
                                    transaction.update(crsref,"Status","deleted");
                                    transaction.update(clgref,"deleted",deleted+1.00);
                                }

                                return null;
                            }
                        }).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                File delfile2 = new File(valueOf(getActivity().getExternalFilesDir("Purchased Courses/"+UserID+Videoname+"Purchased/")));
                                deleteDirectory(delfile2);
                                pd.dismiss();
                                Toast.makeText(getContext(), "Course cleared successfully.", Toast.LENGTH_SHORT).show();
                                FragmentTransaction ftt = getFragmentManager().beginTransaction();
                                ftt.replace(R.id.fragment_container, new AccessFragment()).commit();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                pd.dismiss();
                                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                //Toast.makeText(getContext(), "Error while removing. Contact Team Abhyahas if you still want to remove the course", Toast.LENGTH_SHORT).show();
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
        });

        ChipNavigationBar navBar = getActivity().findViewById(R.id.bottom_nav_menu_id);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File del = new File(valueOf(getActivity().getExternalFilesDir("/data/ob/is/sgisfjf/sfygf/faigiyg/afuyg/fguy")),"1");
                if(del.exists()){
                    del.delete();
                }
                File pdf = new File(valueOf(getActivity().getExternalFilesDir("/data/ob/is/sgisfjf/sfygf/faigiyg/afuyg/fguy")),"pdf");
                if(pdf.exists()){
                    pdf.delete();
                }
                FragmentTransaction ftt = getFragmentManager().beginTransaction();
                ftt.replace(R.id.fragment_container, new Fragments.AccessFragment()).commit();
            }
        });

        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                infoview.setVisibility(View.VISIBLE);
                pdfview.setVisibility(View.GONE);
                contentview.setVisibility(View.GONE);
            }
        });

        pdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                infoview.setVisibility(View.GONE);
                pdfview.setVisibility(View.VISIBLE);
                contentview.setVisibility(View.GONE);

                pdfView=(PDFView) view.findViewById(R.id.pdf_Access);

                File pdffile = new File(valueOf(getActivity().getExternalFilesDir("Purchased Courses/"+UserID+Videoname+"Purchased")),"Course Pdf");

                if (pdffile.exists()){
                    progressDialog.setCancelable(false);
                    progressDialog.setTitle("Opening PDF");
                    progressDialog.setMessage("Wait for a while until PDF is loaded...");
                    progressDialog.show();

                    try {
                        decryptPdfFile(String.valueOf(getActivity().getExternalFilesDir("Purchased Courses/"+UserID+Videoname+"Purchased/Course pdf")),skey,"pdf");
                    } catch (IOException e) {
                        progressDialog.dismiss();
                        e.printStackTrace();
                    } catch (NoSuchPaddingException e) {
                        e.printStackTrace();
                        progressDialog.dismiss();
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                        progressDialog.dismiss();
                    } catch (InvalidKeyException e) {
                        e.printStackTrace();
                        progressDialog.dismiss();
                    }
                }else{
                    if (isOnline()) {
                        progressDialog.setCancelable(false);
                        progressDialog.setTitle("Downloading");
                        progressDialog.setMessage("Enabling offline mode visibility for Course Pdf. Wait for a while...\nThis will take a few seconds depending on your Internet speed\nDon't close the app and don't press back button ");
                        progressDialog.show();
                        //     download(model10.getVideo_Name());
                        downladPdf();
                    } else {
                        Toast.makeText(getContext(), " Check your INTERNET connection ", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });

        IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        getActivity().registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                long broadcastedDownloadID = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID,-1);
                if (broadcastedDownloadID==downloadId) {
                    if (getDownloadStatus() == DownloadManager.STATUS_SUCCESSFUL) {

                        if (flagflag) {
                            flagflag = false;
                            if (UserID != null && Videoname != null) {
                                try {
                                    decryptPdfFile(String.valueOf(getActivity().getExternalFilesDir("Purchased Courses/" + UserID + Videoname + "Purchased/Course pdf")), skey, "pdf");
                                } catch (IOException e) {
                                    Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_SHORT).show();
                                } catch (NoSuchPaddingException e) {
                                    Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_SHORT).show();
                                } catch (NoSuchAlgorithmException e) {
                                    Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_SHORT).show();
                                } catch (InvalidKeyException e) {
                                    Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(context, "Loading", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            try {
                                encryptFile(String.valueOf(getActivity().getExternalFilesDir("/Downloaded file/Youtube/videos/1/new/data/ob/metadata/metadata")), nameofvideo);
                            } catch (IOException e) {
                                Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_SHORT).show();
                            } catch (NoSuchPaddingException e) {
                                Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_SHORT).show();
                            } catch (NoSuchAlgorithmException e) {
                                Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_SHORT).show();
                            } catch (InvalidKeyException e) {
                                Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
            }
        },filter);


        content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                introname.setVisibility(View.GONE);
                infoview.setVisibility(View.GONE);
                pdfview.setVisibility(View.GONE);
                contentview.setVisibility(View.VISIBLE);
                RecAccess.setHasFixedSize(true);
                RecAccess.setLayoutManager(new LinearLayoutManager(view.getContext()));

                Query query10 = dbs2.collection("HOME").document(CoId).collection("LISTITEM").document(Videoname).collection("ALL VIDEOS").orderBy("Order");
                FirestoreRecyclerOptions<ContentModel> options10 = new FirestoreRecyclerOptions.Builder<ContentModel>()
                        .setQuery(query10,ContentModel.class)
                        .build();
                adapter10 = new FirestoreRecyclerAdapter<ContentModel,AcContentViewHolder>(options10) {
                    @NonNull
                    @Override
                    public AcContentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View Vew1 = LayoutInflater.from(parent.getContext()).inflate(R.layout.content_item,parent,false);
                        return new AcContentViewHolder(Vew1);
                    }

                    @Override
                    protected void onBindViewHolder(@NonNull AcContentViewHolder holder10, int position, @NonNull ContentModel model10) {

                        holder10.Text.setText(model10.getVideo_Name());
                        holder10.SubInterfaceClick(new SubOnClickInterface() {
                            @Override
                            public void OnClick(View view, boolean isLongPressed) {
                                nameofvideo = model10.getVideo_Name();
                                ListVideoAc.setText("  Playing : "+model10.getVideo_Name());
                                Toast.makeText(getContext(), model10.getVideo_Name(), Toast.LENGTH_SHORT).show();
                                File myfile = new File(valueOf(getActivity().getExternalFilesDir("Purchased Courses/"+UserID+Videoname+"Purchased/")),model10.getVideo_Name());

                                if (myfile.exists()){
                                    // Encrypted file is present
                                    try {
                                        progressDialog.setCancelable(false);
                                        progressDialog.setTitle("Opening Video");
                                        progressDialog.setMessage("Wait for a while until "+model10.getVideo_Name()+" is loaded...");
                                        progressDialog.show();
                                        decryptFile(String.valueOf(getActivity().getExternalFilesDir("Purchased Courses/"+UserID+Videoname+"Purchased/"+model10.getVideo_Name())),skey, "1");
                                    } catch (IOException e) {
                                        progressDialog.dismiss();
                                        Toast.makeText(getContext(), "Error"+e.toString(), Toast.LENGTH_SHORT).show();
                                    } catch (NoSuchPaddingException e) {
                                        progressDialog.dismiss();
                                        Toast.makeText(getContext(), "Error"+e.toString(), Toast.LENGTH_SHORT).show();
                                    } catch (NoSuchAlgorithmException e) {
                                        progressDialog.dismiss();
                                        Toast.makeText(getContext(), "Error"+e.toString(), Toast.LENGTH_SHORT).show();
                                    } catch (InvalidKeyException e) {
                                        progressDialog.dismiss();
                                        Toast.makeText(getContext(), "Error"+e.toString(), Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    // encrypt and save the file
                                    File downloadfile = new File(valueOf(getActivity().getExternalFilesDir("/Downloaded file/Youtube/videos/1/new/data/ob/metadata")),"metadata");
                                    if(downloadfile.exists()) {
                                        try {
                                            progressDialog.setCancelable(false);
                                            progressDialog.setTitle("Opening Video");
                                            progressDialog.setMessage("Wait for a while until "+model10.getVideo_Name()+" is loaded...");
                                            progressDialog.show();
                                            encryptFile(String.valueOf(getActivity().getExternalFilesDir("/Downloaded file/Youtube/videos/1/new/data/ob/metadata/metadata")),model10.getVideo_Name());
                                        } catch (IOException e) {
                                            progressDialog.dismiss();
                                            Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_SHORT).show();
                                        } catch (NoSuchPaddingException e) {
                                            progressDialog.dismiss();
                                            Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_SHORT).show();
                                        } catch (NoSuchAlgorithmException e) {
                                            progressDialog.dismiss();
                                            Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_SHORT).show();
                                        } catch (InvalidKeyException e) {
                                            progressDialog.dismiss();
                                            Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        // Need to download the video
                                       if (isOnline()) {
                                           progressDialog.setCancelable(false);
                                           progressDialog.setTitle("Offline Mode Enabling...");
                                           progressDialog.setMessage("Enabling offline mode visibility for "+model10.getVideo_Name()+". Wait for a while...\nThis will take a few seconds depending on your Internet speed\nDon't close the app and don't press back button ");
                                           progressDialog.show();
                                           String youtubeLink = model10.getVideo_Url();
                                           new YouTubeExtractor(getContext()) {
                                               @Override
                                               public void onExtractionComplete(SparseArray<YtFile> ytFiles, VideoMeta vMeta) {
                                                   if (ytFiles != null) {
                                                       List<Integer> iTags = Arrays.asList(22, 17, 18, 137);
                                                       for (Integer itag : iTags) {
                                                           YtFile yt = ytFiles.get(itag);
                                                           if (yt!=null) {
                                                               downloadUrl = yt.getUrl();
                                                               if(downloadUrl!=null) {
                                                                   Uri uri = Uri.parse(downloadUrl);
                                                                   String s = String.valueOf(uri);
                                                                   downloadFile(getActivity(),"metadata","/Downloaded file/Youtube/videos/1/new/data/ob/metadata",uri);
                                                                   break;
                                                               } else {
                                                                   Toast.makeText(getContext(), "Error while enabling offline mode. Contact Team Abhyahas", Toast.LENGTH_SHORT).show();
                                                               }
                                                           } else {
                                                               continue;
                                                           }
                                                       }
                                                   }
                                               }
                                           }.extract(youtubeLink, true, true);
                                       } else {
                                           Toast.makeText(getContext(), " Check your INTERNET connection ", Toast.LENGTH_SHORT).show();
                                       }
                                    }
                                }
                            }
                        });
                    }
                };
                adapter10.startListening();
                adapter10.notifyDataSetChanged();
                RecAccess.setAdapter(adapter10);
            }
        });

        btFullscreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view1) {
                if(flag){
                    btFullscreen.setImageDrawable(getResources().getDrawable(R.drawable.ic_fullscreen));

                    if (((AppCompatActivity)getActivity()).getSupportActionBar() != null){
                        ((AppCompatActivity)getActivity()).getSupportActionBar().show();
                    }
                    ((AppCompatActivity)getActivity()).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)playerView2.getLayoutParams();
                    params.width = params.MATCH_PARENT;
                    params.height = (int) (200 * view.getContext().getResources().getDisplayMetrics().density);
                    playerView2.setLayoutParams(params);

                    CN.setVisibility(View.VISIBLE);
                    back.setVisibility(View.VISIBLE);
                    navBar.setVisibility(View.VISIBLE);
                    heading.setVisibility(View.VISIBLE);
                    leftside.setVisibility(View.VISIBLE);
                    rightside.setVisibility(View.VISIBLE);
                    flag = false;
                }else {
                    btFullscreen.setImageDrawable(getResources().getDrawable(R.drawable.ic_fullscreen_exit));

                    if ( ((AppCompatActivity)getActivity()).getSupportActionBar() != null){
                        ((AppCompatActivity)getActivity()).getSupportActionBar().hide();
                    }

                    ((AppCompatActivity)getActivity()).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)playerView2.getLayoutParams();
                    params.width = params.MATCH_PARENT;
                    params.height = params.MATCH_PARENT;
                    playerView2.setLayoutParams(params);
                    CN.setVisibility(View.GONE);
                    back.setVisibility(View.GONE);
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

    private int getDownloadStatus() {
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(downloadId);

        DownloadManager downloadManager = (DownloadManager) getActivity().getSystemService(Context.DOWNLOAD_SERVICE);
        Cursor cursor = downloadManager.query(query);

        if(cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
            int status = cursor.getInt(columnIndex);

            return status;
        }

        return DownloadManager.ERROR_UNKNOWN;
    }


    private void downladPdf(){
        StorageReference pdfref = mstorage.child("Courses").child(Videoname).child("Course PDF");

        pdfref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                downloadFile(getContext(),"Course Pdf","Purchased Courses/"+UserID+Videoname+"Purchased",uri);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(getContext(), "Error occurred in Downloading Pdf"+e, Toast.LENGTH_SHORT).show();
            }
        });
    }



    private void downloadFile(Context context, String fileName, String destinationDirectory, Uri uri) {
        if (fileName.equals("Course Pdf")) {
            flagflag = true;
        }
            DownloadManager.Request request = new DownloadManager.Request(uri);
            //request.setTitle("Enabling offline capability");
            //request.setDescription("Wait for a while until offline capability for "+fileName+" is turned ON");
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);
            request.setDestinationInExternalFilesDir(context,destinationDirectory,fileName);
            DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            downloadId = downloadManager.enqueue(request);
    }


    class RetrievePDFStream extends AsyncTask<String,Void, InputStream> {
        @Override
        protected InputStream doInBackground(String... strings) {
            InputStream inputStream = null;

            try {

                URL urlx = new URL(strings[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) urlx.openConnection();
                if (urlConnection.getResponseCode() == 200) {
                    inputStream = new BufferedInputStream(urlConnection.getInputStream());

                }
            } catch (IOException e) {
                return null;
            }
            return inputStream;
        }

        @Override
        protected void onPostExecute(InputStream inputStream) {
            pdfView.fromStream(inputStream).load();
        }
    }

    private class AcContentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView Text;
        private SubOnClickInterface subOnClickInterface;
        public AcContentViewHolder(@NonNull View itemView) {
            super(itemView);
            Text = itemView.findViewById(R.id.Content_Video);
            itemView.setOnClickListener(this);
        }
        public void SubInterfaceClick(SubOnClickInterface subOnClickInterface){
            this.subOnClickInterface = subOnClickInterface;
        }

        @Override
        public void onClick(View view) {
            subOnClickInterface.OnClick(view,false);
        }
    }

    public void resizefun() {

        switch (size) {

            case 0 :
                playerView2.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_ZOOM);
                Toast.makeText(getView().getContext(), "ZOOM mode", Toast.LENGTH_SHORT).show();
                size = 1;
                break;

            case 1 :
                playerView2.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
                Toast.makeText(getView().getContext(), "FIT mode", Toast.LENGTH_SHORT).show();
                size = 2;
                break;

            case 2 :
                playerView2.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FILL);
                Toast.makeText(getView().getContext(), "FILL mode", Toast.LENGTH_SHORT).show();
                size = 3;
                break;

            case 3 :
                playerView2.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIXED_HEIGHT);
                Toast.makeText(getView().getContext(), "FIXED HEIGHT mode", Toast.LENGTH_SHORT).show();
                size = 4;
                break;

            case 4 :
                playerView2.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIXED_WIDTH);
                Toast.makeText(getView().getContext(), "FIXED WIDTH mode", Toast.LENGTH_SHORT).show();
                size = 0;
                break;

        }

    }

    private void decryptFile(String encryptFilePath, SecretKey secretKey, String decnamevideo) throws IOException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        int read;
        File outfile = new File(encryptFilePath);
        File dec = new File(valueOf(getActivity().getExternalFilesDir("/data/ob/is/sgisfjf/sfygf/faigiyg/afuyg/fguy")));
        File decfile = new File(dec,decnamevideo);
        if(!decfile.exists())
            decfile.createNewFile();

        FileOutputStream decfos = new FileOutputStream(decfile);
        FileInputStream encfis = new FileInputStream(outfile);

        Cipher decipher = Cipher.getInstance("AES");

        decipher.init(Cipher.DECRYPT_MODE, secretKey);
        CipherOutputStream cos = new CipherOutputStream(decfos,decipher);

        byte[] buffer = new byte[1024]; // buffer can read file line by line to increase speed
        while((read=encfis.read(buffer)) >= 0)
        {
            cos.write(buffer, 0, read);
            cos.flush();
        }
        cos.close();

        String video = String.valueOf(Uri.fromFile(decfile));
        releasePlayer();
        initializePlayer(video);

       new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                progressDialog.dismiss();
            }
        },1500);

    }

    private void decryptPdfFile(String encryptFilePath, SecretKey secretKey, String decnamevideo) throws IOException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        int read;
        File outfile = new File(encryptFilePath);
        File dec = new File(valueOf(getActivity().getExternalFilesDir("/data/ob/is/sgisfjf/sfygf/faigiyg/afuyg/fguy")));
        File decfile = new File(dec,decnamevideo);
        if(!decfile.exists())
            decfile.createNewFile();

        FileOutputStream decfos = new FileOutputStream(decfile);
        FileInputStream encfis = new FileInputStream(outfile);

        Cipher decipher = Cipher.getInstance("AES");

        decipher.init(Cipher.DECRYPT_MODE, secretKey);
        CipherOutputStream cos = new CipherOutputStream(decfos,decipher);

        byte[] buffer = new byte[1024]; // buffer can read file line by line to increase speed
        while((read=encfis.read(buffer)) >= 0)
        {
            cos.write(buffer, 0, read);
            cos.flush();
        }
        cos.close();
        pdfView.fromFile(decfile).defaultPage(0)
                .enableAnnotationRendering(true)
                .scrollHandle(new DefaultScrollHandle(getContext()))
                .load();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                progressDialog.dismiss();
                decfile.delete();
            }
        },1500);


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

    private void initializePlayer(String video) {
        exoPlayer2 = ExoPlayerFactory.newSimpleInstance(
                new DefaultRenderersFactory(getActivity()),
                new DefaultTrackSelector(), new DefaultLoadControl());

        playerView2.setPlayer(exoPlayer2);

        exoPlayer2.setPlayWhenReady(false);

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

    private void encryptFile(String filePath,String encnamevideo) throws IOException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        int read;
        FileInputStream fis = new FileInputStream(new File(filePath));
        File enc = new File(String.valueOf(getActivity().getExternalFilesDir("Purchased Courses/"+UserID+Videoname+"Purchased/")));
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
        File deletefile = new File(valueOf(getActivity().getExternalFilesDir("/Downloaded file/Youtube/videos/1/new/data/ob/metadata")),"metadata");
        if (deletefile.exists()){
            deletefile.delete();
        }
        try {
            decryptFile(String.valueOf(getActivity().getExternalFilesDir("Purchased Courses/"+UserID+Videoname+"Purchased/"+encnamevideo)),skey, "1");
        } catch (IOException e) {
            Toast.makeText(getContext(), "Error"+e.toString(), Toast.LENGTH_SHORT).show();
        } catch (NoSuchPaddingException e) {
            Toast.makeText(getContext(), "Error"+e.toString(), Toast.LENGTH_SHORT).show();
        } catch (NoSuchAlgorithmException e) {
            Toast.makeText(getContext(), "Error"+e.toString(), Toast.LENGTH_SHORT).show();
        } catch (InvalidKeyException e) {
            Toast.makeText(getContext(), "Error"+e.toString(), Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onPause() {
        super.onPause();
        if (Util.SDK_INT < 24) {
            releasePlayer();
        }
        File del = new File(valueOf(getActivity().getExternalFilesDir("/data")));
        if(del.exists()){
            deleteDirectory(del);
        }
        File deletefile = new File(valueOf(getActivity().getExternalFilesDir("/Downloaded file/")));
        if (deletefile.exists()){
            deleteDirectory(deletefile);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (Util.SDK_INT >= 24) {
            releasePlayer();
        }
        File del = new File(valueOf(getActivity().getExternalFilesDir("/data")));
        if(del.exists()){
            deleteDirectory(del);
        }
        File deletefile = new File(valueOf(getActivity().getExternalFilesDir("/Downloaded file/")));
        if (deletefile.exists()){
            deleteDirectory(deletefile);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (Util.SDK_INT >= 24) {
            releasePlayer();
        }
        File del = new File(valueOf(getActivity().getExternalFilesDir("d")));
        if(del.exists()){
            deleteDirectory(del);
        }
        File deletefile = new File(valueOf(getActivity().getExternalFilesDir("/Downloaded file")));
        if (deletefile.exists()){
            deleteDirectory(deletefile);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if ((Util.SDK_INT < 24 || exoPlayer2 == null)) {
            //exoPlayer2.setPlayWhenReady(false);
            initializePlayer(VideoUrl);
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        if (Util.SDK_INT >= 24) {
            //exoPlayer2.setPlayWhenReady(false);
            initializePlayer(VideoUrl);

        }
    }

}
