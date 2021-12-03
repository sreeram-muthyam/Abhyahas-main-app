package Fragments;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.media.PlaybackParams;
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
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.Transaction;
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
import java.util.HashMap;
import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import Interface.SubOnClickInterface;
import Model.ContentModel;
import static java.lang.String.valueOf;

public class UploadedcoursesFragment extends Fragment {

    ImageView back, btFullscreen,resize,profile;
    Button info,content,pdf,remove;
    View pdfview,transview;
    View contentview;
    View infoview,leftside,rightside, heading;
    String CreName,Videoname,DESC,Price,VideoUrl,Pdf,CoId,creatorImg,enrol,coursename,userId,amount_generated,Total_Enrolments;
    TextView CN,VN,Descp,PRICE,ListVideo,enrolments,amount,transactions,introname;
    PDFView pdfView;
    PlayerView playerView2;
    SimpleExoPlayer exoPlayer2;
    RecyclerView RecUplaod;
    FirebaseFirestore dbs1;
    FirebaseAuth mAuth;
    private FirestoreRecyclerAdapter adapter7;
    boolean flag = false;
    SecretKey skey;
    int size = 0 ;
    ProgressDialog pd;
    private boolean playWhenReady = true;
    View view;
    RadioGroup speedgroup;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(savedInstanceState==null) {
            view= inflater.inflate(R.layout.fragment_uploadedcourses, container, false);
        }
        //View view= inflater.inflate(R.layout.fragment_uploadedcourses, container, false);

        mAuth = FirebaseAuth.getInstance();
        dbs1 = FirebaseFirestore.getInstance();

        userId = mAuth.getCurrentUser().getUid();

        pd = new ProgressDialog(getContext());

        Bundle bundle6 = this.getArguments();
        if(bundle6!=null){
            CreName =(String) bundle6.get("CreName");
            creatorImg =(String) bundle6.get("CreImg");
            Videoname =(String) bundle6.get("ViName");
            coursename =(String) bundle6.get("coursename");
            DESC =(String)bundle6.get("Desc");
            Price = (String) bundle6.get("Pri");
            VideoUrl=(String) bundle6.get("Vurl");
            Pdf = (String)bundle6.get("PDF");
            CoId = (String) bundle6.get("CoID");
            enrol = (String) bundle6.get("enrol");
            Total_Enrolments = (String) bundle6.get("Total_Enrolments");
            amount_generated = (String) bundle6.get("amount_generated");
        }

        byte[] encodedKey     = Base64.decode( "fRIxFDSHKrBDUjseir8TSg==\n"+"    ", Base64.DEFAULT);
        skey = new SecretKeySpec(encodedKey, 0, encodedKey.length, "AES");

        back = view.findViewById(R.id.backimage);
        info = view.findViewById(R.id.infobtn);
        content = view.findViewById(R.id.contentbtn);
        pdf = view.findViewById(R.id.pdfbtn);
        pdfview = view.findViewById(R.id.pdfview);
        infoview = view.findViewById(R.id.infoview);
        contentview = view.findViewById(R.id.contentview);
        ListVideo = view.findViewById(R.id.List_VideoName);
        playerView2 = view.findViewById(R.id.player_view2);
        RecUplaod = view.findViewById(R.id.Upload_Rec_View);
        leftside = view.findViewById(R.id.leftside3);
        rightside = view.findViewById(R.id.rightside3);
        heading = view.findViewById(R.id.head);
        profile = view.findViewById(R.id.profile_imageuc);
        enrolments = view.findViewById(R.id.enrolments_uploaded);
        amount = view.findViewById(R.id.amount);
        transactions = view.findViewById(R.id.transactions);
        transview = view.findViewById(R.id.transview);
        introname = view.findViewById(R.id.Intro_Name_uploaded);
        remove = view.findViewById(R.id.remove);
        btFullscreen = playerView2.findViewById(R.id.bt_fullscreen);
        resize = playerView2.findViewById(R.id.resize);
        speedgroup = playerView2.findViewById(R.id.RadioSpeed);

        introname.setText(Videoname);

        amount.setText("Amount generated : "+amount_generated);

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

        //When remove button is clicked
        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage("Do you want to delete this Course ? It will be no longer visible to students across the platform then after");
                builder.setTitle("Confirmation !!!");
                builder.setCancelable(false);
                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        // 1) Clgref userupload reduce by 1
                        // 2) Courseupload change CREATORID
                        // 3) Change status to delete
                        // 4) Delete local files
                        // 5) If pending decrease pending by 1
                        // 6) If accepted decrease accepted by 1
                        // 7) If enrolments are 0 then increase deleted by 1 and change status to deleted

                        pd.setTitle("Removing...");
                        pd.setMessage("Wait for a while until "+coursename+" is removed from here");
                        pd.setCancelable(false);
                        pd.show();

                        dbs1.runTransaction(new Transaction.Function<Void>() {
                            @Nullable
                            @Override
                            public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                                DocumentReference userref = dbs1.collection("USERS").document(userId);
                                DocumentSnapshot usersnap = transaction.get(userref);
                                DocumentReference clgref = dbs1.collection("HOME").document(CoId);
                                DocumentReference crsref = dbs1.collection("HOME").document(CoId).collection("LISTITEM").document(coursename);
                                DocumentSnapshot clgsnap = transaction.get(clgref);
                                DocumentSnapshot crssnap = transaction.get(crsref);

                                Double userupload = clgsnap.getDouble(userId);
                                String status = crssnap.getString("Status");
                                String enrolments = crssnap.getString("Enrolments");
                                String courses_uploaded = usersnap.getString("courses_uploaded");

                                transaction.update(userref,"courses_uploaded",String.valueOf(Integer.parseInt(courses_uploaded)-1));

                                if (status.equals("accepted")) {
                                    Double accepted = clgsnap.getDouble("accepted");
                                    HashMap<String, Double> accepthash = new HashMap<>();
                                    accepthash.put("accepted",accepted-1.00);
                                    accepthash.put(userId,userupload-1.00);
                                    transaction.set(clgref,accepthash,SetOptions.merge());
                                    HashMap<String, String> crshash = new HashMap<>();
                                    crshash.put("Status","delete");
                                    crshash.put("Creator_ID",userId+" deleted");
                                    transaction.set(crsref,crshash,SetOptions.merge());
                                }

                                if (status.equals("pending")) {
                                    Double pending = clgsnap.getDouble("pending");
                                    HashMap<String, Double> accepthash = new HashMap<>();
                                    accepthash.put("pending",pending-1.00);
                                    accepthash.put(userId,userupload-1.00);
                                    transaction.set(clgref,accepthash,SetOptions.merge());
                                    HashMap<String, String> crshash = new HashMap<>();
                                    crshash.put("Status","delete");
                                    crshash.put("Creator_ID",userId+" deleted");
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
                                File delfile = new File(valueOf(getActivity().getExternalFilesDir("Uploaded Courses/" + coursename)));
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

        SearchView searchView = (SearchView) getActivity().findViewById(R.id.task);
        searchView.setVisibility(View.GONE);

        if(Pdf.equals("No PDF")){
            pdf.setVisibility(View.INVISIBLE);
        }

        enrolments.setText("Number of students enrolled : "+Total_Enrolments);

        Picasso.get().load(Uri.parse(creatorImg)).into(profile);

        ChipNavigationBar navBar = getActivity().findViewById(R.id.bottom_nav_menu_id);

        CN = view.findViewById(R.id.Upload_CreaterName);
        VN = view.findViewById(R.id.Upload_CourseName);
        Descp = view.findViewById(R.id.Upload_CourseDesc);
        PRICE = view.findViewById(R.id.Upload_CoursePrice);

        CN.setText(CreName);
        VN.setText(coursename);
        Descp.setText(DESC);

        if(Price.equals("FREE")) {
            amount.setVisibility(View.GONE);
            PRICE.setText("FREE Course");
            transview.setVisibility(View.GONE);
        } else {
            PRICE.setText("Course Price : Rs." + Price+"/-");
        }

        transactions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle6 = new Bundle();
                bundle6.putString("CreName",CreName);
                bundle6.putString("CreImg",creatorImg);
                bundle6.putString("ViName",Videoname);
                bundle6.putString("Pri",Price);
                bundle6.putString("PDF",Pdf);
                bundle6.putString("Vurl",VideoUrl);
                bundle6.putString("Desc",DESC);
                bundle6.putString("CoID",CoId);
                bundle6.putString("enrol",enrol);
                bundle6.putString("Total_Enrolments",Total_Enrolments);
                bundle6.putString("coursename",coursename);
                bundle6.putString("amount_generated",amount_generated);
                bundle6.putString("to","uploaded");

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
        });

        // When back image is selected.
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
                ftt.replace(R.id.fragment_container, new Fragments.UploadFragment()).commit();
            }
        });

        // When info button is clicked
        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                infoview.setVisibility(View.VISIBLE);
                pdfview.setVisibility(View.GONE);
                contentview.setVisibility(View.GONE);
            }
        });

        // When Pdf button is clicked.
        pdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                infoview.setVisibility(View.GONE);
                pdfview.setVisibility(View.VISIBLE);
                contentview.setVisibility(View.GONE);
                pdfView=(PDFView) view.findViewById(R.id.pdf_Upload);

                try {
                    decryptPdfFile(String.valueOf(getActivity().getExternalFilesDir("Uploaded Courses/"+coursename+"/Course Pdf")),skey,"pdf");
                } catch (IOException e) {
                    Toast.makeText(getContext(), "Error"+e.toString(), Toast.LENGTH_SHORT).show();
                } catch (NoSuchPaddingException e) {
                    Toast.makeText(getContext(), "Error"+e.toString(), Toast.LENGTH_SHORT).show();
                } catch (NoSuchAlgorithmException e) {
                    Toast.makeText(getContext(), "Error"+e.toString(), Toast.LENGTH_SHORT).show();
                } catch (InvalidKeyException e) {
                    Toast.makeText(getContext(), "Error"+e.toString(), Toast.LENGTH_SHORT).show();
                }

                //   new RetrievePDFStream().execute(Pdf);

            }
        });

        // When content button is clicked.
        content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                introname.setVisibility(View.GONE);
                infoview.setVisibility(View.GONE);
                pdfview.setVisibility(View.GONE);
                contentview.setVisibility(View.VISIBLE);
                dbs1 = FirebaseFirestore.getInstance();
                RecUplaod.setHasFixedSize(true);
                RecUplaod.setLayoutManager(new LinearLayoutManager(view.getContext()));

                Query query6 = dbs1.collection("HOME").document(CoId).collection("LISTITEM").document(coursename).collection("ALL VIDEOS").orderBy("Order");
                FirestoreRecyclerOptions<ContentModel> options7 = new FirestoreRecyclerOptions.Builder<ContentModel>()
                        .setQuery(query6,ContentModel.class)
                        .build();

                adapter7 = new FirestoreRecyclerAdapter<ContentModel,UpContentViewHolder>(options7){
                    @NonNull
                    @Override
                    public UpContentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View Vew = LayoutInflater.from(parent.getContext()).inflate(R.layout.content_item,parent,false);
                        return new UpContentViewHolder(Vew);
                    }

                    @Override
                    protected void onBindViewHolder(@NonNull UpContentViewHolder holder7, int position, @NonNull ContentModel model7) {
                        holder7.Text.setText(model7.getVideo_Name());
                        holder7.SubInterfaceClick(new SubOnClickInterface() {
                            @Override
                            public void OnClick(View view, boolean isLongPressed) {
                                try {
                                    ListVideo.setText("Playing : "+model7.getVideo_Name());
                                    decryptFile(String.valueOf(getActivity().getExternalFilesDir("Uploaded Courses/"+coursename+"/"+model7.getVideo_Name())),skey,"1");
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
                        });
                    }
                };
                adapter7.startListening();
                adapter7.notifyDataSetChanged();
                RecUplaod.setAdapter(adapter7);
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

    private class UpContentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView Text,enrolments;
        private SubOnClickInterface subOnClickInterface;
        public UpContentViewHolder(@NonNull View itemView) {
            super(itemView);
            Text = itemView.findViewById(R.id.Content_Video);
            enrolments = itemView.findViewById(R.id.enrolments_uploaded);
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

    // For opening of a PDF file.
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

    // Selecting different video size mode in exoplayer.
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

    // File is decrypted..

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

        //    stopPlayer();
        releasePlayer();
        initializePlayer(video);

  /*      new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                decfile.delete();
            }
        },1500);     */
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

    // Method for initilizing exoplayer.
    private void initializePlayer(String video) {
        exoPlayer2 = ExoPlayerFactory.newSimpleInstance(
                new DefaultRenderersFactory(getActivity()),
                new DefaultTrackSelector(), new DefaultLoadControl());


        playerView2.setPlayer(exoPlayer2);


        exoPlayer2.setPlayWhenReady(false);

        Uri uri = Uri.parse(video);
        MediaSource mediaSource = buildMediaSource(uri);
        exoPlayer2.prepare(mediaSource,false, false);

        exoPlayer2.addListener(new Player.DefaultEventListener() {
            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                if (playWhenReady && playbackState == Player.STATE_READY) {
                    // media actually playing
                } else if (playWhenReady) {
                    // might be idle (plays after prepare()),
                    // buffering (plays when data available)
                    // or ended (plays when seek away from end)
                } else {
                    // player paused in any state
                }
                //      super.onPlayerStateChanged(playWhenReady, playbackState);
            }
        });



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
    public void onDestroy() {
        super.onDestroy();
        if (Util.SDK_INT >= 24) {
            releasePlayer();
        }
        File del = new File(valueOf(getActivity().getExternalFilesDir("/data/ob/is/sgisfjf/sfygf/faigiyg/afuyg/fguy")),"1");
        if(del.exists()){
            del.delete();
        }
        File pdf = new File(valueOf(getActivity().getExternalFilesDir("/data/ob/is/sgisfjf/sfygf/faigiyg/afuyg/fguy")),"pdf");
        if(pdf.exists()){
            pdf.delete();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (Util.SDK_INT < 24) {
            releasePlayer();
        }
        File del = new File(valueOf(getActivity().getExternalFilesDir("/data/ob/is/sgisfjf/sfygf/faigiyg/afuyg/fguy")),"1");
        if(del.exists()){
            del.delete();
        }
        File pdf = new File(valueOf(getActivity().getExternalFilesDir("/data/ob/is/sgisfjf/sfygf/faigiyg/afuyg/fguy")),"pdf");
        if(pdf.exists()){
            pdf.delete();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (Util.SDK_INT >= 24) {
            releasePlayer();
        }
        File del = new File(valueOf(getActivity().getExternalFilesDir("/data/ob/is/sgisfjf/sfygf/faigiyg/afuyg/fguy")),"1");
        if(del.exists()){
            del.delete();
        }
        File pdf = new File(valueOf(getActivity().getExternalFilesDir("/data/ob/is/sgisfjf/sfygf/faigiyg/afuyg/fguy")),"pdf");
        if(pdf.exists()){
            pdf.delete();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if ((Util.SDK_INT < 24 || exoPlayer2 == null)) {
            //exoPlayer2.setPlayWhenReady(false);
            // Playing Introductory video from local device when fragment is begun
            try {
                decryptFile(String.valueOf(getActivity().getExternalFilesDir("Uploaded Courses/"+coursename+"/"+Videoname)),skey, "1");
            } catch (IOException e) {
                e.printStackTrace();
            } catch (NoSuchPaddingException e) {
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (InvalidKeyException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        if (Util.SDK_INT >= 24) {
            //exoPlayer2.setPlayWhenReady(false);
            // Playing Introductory video from local device when fragment is begun
            try {
                decryptFile(String.valueOf(getActivity().getExternalFilesDir("Uploaded Courses/"+coursename+"/"+Videoname)),skey, "1");
            } catch (IOException e) {
                e.printStackTrace();
            } catch (NoSuchPaddingException e) {
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (InvalidKeyException e) {
                e.printStackTrace();
            }
        }
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
                decfile.delete();
            }
        },1500);
    }
}
