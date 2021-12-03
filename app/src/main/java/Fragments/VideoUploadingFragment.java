package Fragments;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.PlaybackParams;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
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
import java.util.List;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import Interface.SubOnClickInterface;
import Model.ContentModel;
import static android.app.Activity.RESULT_OK;
import static java.lang.String.valueOf;

public class VideoUploadingFragment extends Fragment {

    private static final int PICK_VIDEO = 21;
    Uri videoUri;
    String coursename, Videoname;
    String college, UserId,path;
    StorageReference mstorage;
    FirebaseFirestore mfirestore;
    FirebaseAuth mAuth;
    CollectionReference videosdbref;
    DocumentReference introdbref;
    ImageView back3,btFullscreen,resize;
    Button videoselect,videoupload,finalsubmit, intro;
    EditText videoname;
    TextView notice, Intro_Content_Name;
    ProgressDialog pd,sd;
    RecyclerView RecUplaoding;
    SecretKey skey;
    private FirestoreRecyclerAdapter adapter7;
    boolean flag = false;
    View view;
    PlayerView playerViewUploading;
    SimpleExoPlayer exoPlayerUploading;
    int size = 0 ;
    RadioGroup speedgroup;
    NetworkChangeListener networkChangeListener;


    int num = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(savedInstanceState==null) {
            view = inflater.inflate(R.layout.fragment_videouploading, container, false);
        }

        back3 = view.findViewById(R.id.back3);
        videoselect = view.findViewById(R.id.select);
        videoupload = view.findViewById(R.id.upload);
        finalsubmit = view.findViewById(R.id.finalsubmit);
        videoname = view.findViewById(R.id.videoname);
        playerViewUploading = view.findViewById(R.id.player_view_Upload);
        notice = view.findViewById(R.id.notice);
        intro = view.findViewById(R.id.intro);
        finalsubmit.setVisibility(View.GONE);
        RecUplaoding = view.findViewById(R.id.Uploaded_Rec_View);
        Intro_Content_Name = view.findViewById(R.id.Intro_Content_Name);
        btFullscreen = playerViewUploading.findViewById(R.id.bt_fullscreen);
        resize = playerViewUploading.findViewById(R.id.resize);
        btFullscreen.setVisibility(View.GONE);
        speedgroup = playerViewUploading.findViewById(R.id.RadioSpeed);

        networkChangeListener = new NetworkChangeListener();

        byte[] encodedKey     = Base64.decode( "fRIxFDSHKrBDUjseir8TSg==\n"+"    ", Base64.DEFAULT);
        skey = new SecretKeySpec(encodedKey, 0, encodedKey.length, "AES");

        mAuth = FirebaseAuth.getInstance();
        UserId = mAuth.getCurrentUser().getUid();

        ChipNavigationBar chipNavigationBar = getActivity().findViewById(R.id.bottom_nav_menu_id);
        chipNavigationBar.setEnabled(false);

        SearchView searchView = (SearchView) getActivity().findViewById(R.id.task);
        searchView.setVisibility(View.GONE);

        pd = new ProgressDialog(getContext());
        sd = new ProgressDialog(getContext());

        Bundle bundle1 = this.getArguments();
        if(bundle1!=null){
            coursename = (String) bundle1.get("name");
            college = (String) bundle1.get("text");
        }

        speedgroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i){

                    case R.id.Speed0_5x:
                        PlaybackParams param1 = new PlaybackParams();
                        param1.setSpeed(0.5f);// 1f is 1x, 2f is 2x
                        exoPlayerUploading.setPlaybackParams(param1);
                        break;
                    case R.id.Speed1x:
                        PlaybackParams param2 = new PlaybackParams();
                        param2.setSpeed(1f);// 1f is 1x, 2f is 2x
                        exoPlayerUploading.setPlaybackParams(param2);
                        break;
                    case R.id.Speed1_5x:
                        PlaybackParams param3 = new PlaybackParams();
                        param3.setSpeed(1.5f);// 1f is 1x, 2f is 2x
                        exoPlayerUploading.setPlaybackParams(param3);
                        break;
                    case R.id.Speed2x:
                        PlaybackParams param4 = new PlaybackParams();
                        param4.setSpeed(2f);// 1f is 1x, 2f is 2x
                        exoPlayerUploading.setPlaybackParams(param4);
                        break;

                }
            }
        });

        mfirestore = FirebaseFirestore.getInstance();

        mstorage = FirebaseStorage.getInstance().getReference();

        introdbref = mfirestore.collection("HOME").document(college).collection("LISTITEM").document(coursename);

        videosdbref = mfirestore.collection("HOME").document(college).collection("LISTITEM").document(coursename).collection("ALL VIDEOS");

        RecUplaoding.setHasFixedSize(true);
        RecUplaoding.setLayoutManager(new LinearLayoutManager(view.getContext()));

        Query query6 = videosdbref.orderBy("Order");
        FirestoreRecyclerOptions<ContentModel> options7 = new FirestoreRecyclerOptions.Builder<ContentModel>()
                .setQuery(query6,ContentModel.class)
                .build();

        adapter7 = new FirestoreRecyclerAdapter<ContentModel,UpContentViewHolder2>(options7){

            @NonNull
            @Override
            public UpContentViewHolder2 onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View Vew = LayoutInflater.from(parent.getContext()).inflate(R.layout.content_item,parent,false);
                return new UpContentViewHolder2(Vew);
            }

            @Override
            protected void onBindViewHolder(@NonNull UpContentViewHolder2 holder, int position, @NonNull ContentModel model) {
                holder.Text.setText(model.getVideo_Name());
                holder.SubInterfaceClick(new SubOnClickInterface() {
                    @Override
                    public void OnClick(View view, boolean isLongPressed) {

                        try {
                            decryptFile(String.valueOf(getActivity().getExternalFilesDir("Uploaded Courses/"+coursename+"/"+model.getVideo_Name())),skey, "1");
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
        RecUplaoding.setAdapter(adapter7);

        Intro_Content_Name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    decryptFile(String.valueOf(getActivity().getExternalFilesDir("Uploaded Courses/"+coursename+"/"+Intro_Content_Name.getText().toString())),skey, "1");
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

        intro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Videoname = videoname.getText().toString();

                if (!(Videoname.isEmpty())) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setMessage("Do you want to upload the above video as Introductory video");
                    builder.setTitle("INTRODUCTORY VIDEO CONFIRMATION");
                    builder.setCancelable(false);
                    builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (videoUri != null ) {

                                try {
                                    encryptFile(path,Videoname);
                                } catch (IOException e) {
                                    Toast.makeText(getContext(), "Error"+e.toString(), Toast.LENGTH_SHORT).show();
                                } catch (NoSuchPaddingException e) {
                                    Toast.makeText(getContext(), "Error"+e.toString(), Toast.LENGTH_SHORT).show();
                                } catch (NoSuchAlgorithmException e) {
                                    Toast.makeText(getContext(), "Error"+e.toString(), Toast.LENGTH_SHORT).show();
                                } catch (InvalidKeyException e) {
                                    Toast.makeText(getContext(), "Error"+e.toString(), Toast.LENGTH_SHORT).show();
                                }

                                pd.setCancelable(false);
                                pd.setTitle("Saving data");
                                pd.show();

                                videoselect.setVisibility(View.GONE);
                                intro.setVisibility(View.GONE);
                                videoname.setEnabled(false);

                                Intro_Content_Name.setText(Videoname);
                                Intro_Content_Name.setVisibility(View.VISIBLE);

                                HashMap<String,String> introh = new HashMap<>();

                                final StorageReference introref = mstorage.child("Courses").child(coursename).child(Videoname);

                                File file = new File(valueOf(getActivity().getExternalFilesDir("/Uploaded Courses/"+coursename)),Videoname);

                                introref.putFile(videoUri).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                                        double progressPercent = (100.00 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                                        pd.setMessage("Progress: " + (int) progressPercent + "%");
                                    }
                                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        introref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                introh.put("Intro_Name",Videoname);
                                                introh.put("Intro_Url", String.valueOf(uri));
                                                introdbref.set(introh, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        pd.dismiss();
                                                        videoselect.setVisibility(View.VISIBLE);
                                                        videoupload.setVisibility(View.VISIBLE);
                                                        videoname.setEnabled(true);
                                                        videoupload.setVisibility(View.VISIBLE);
                                                        notice.setVisibility(View.GONE);
                                                        videoname.setText("");
                                                        notice.setVisibility(View.GONE);
                                                        videoselect.setText("Select Next");
                                                        flag = true;
                                                        Toast.makeText(getContext(), "Introductory Video Uploaded successfully", Toast.LENGTH_SHORT).show();
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        pd.dismiss();
                                                        Toast.makeText(getContext(), "Error ! "+e.toString(), Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                pd.dismiss();
                                                Toast.makeText(getContext(), "Error ! "+e.toString(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        pd.dismiss();
                                        Toast.makeText(getContext(), "Error ! "+e.toString(), Toast.LENGTH_SHORT).show();
                                    }
                                });

                            } else {
                                Toast.makeText(getContext(), "Video loading failed", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });

                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            videoname.setText("");
                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                } else {
                    Toast.makeText(getContext(), "Video name and Video file are mandatory", Toast.LENGTH_SHORT).show();
                }

            }
        });

        back3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage("Do you want to stop creating new course ?\n\nAll the data saved before will be erased out from the servers and your chance of uploading will be reduced by 1");
                builder.setTitle("ALERT !!!");
                builder.setCancelable(false);
                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        sd.setTitle("Deleting all your data");
                        sd.setMessage("Wait for few seconds");
                        sd.setCancelable(false);
                        sd.show();

                        File delfile1 = new File(valueOf(getActivity().getExternalFilesDir("Uploaded Courses/" + coursename)));

                        mfirestore.collection("HOME").document(college).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                if(delfile1.exists()) {
                                    deleteDirectory(delfile1);
                                }
                                mfirestore.collection("HOME").document(college).collection("LISTITEM").document(coursename).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        String intr = documentSnapshot.getString("Intro_Name");
                                        if(intr!=null) {
                                            final StorageReference inv = mstorage.child("Courses").child(coursename).child(intr);
                                            inv.delete();
                                        }
                                    }
                                });
                                HashMap<String,Double> hmap = new HashMap<>();
                                hmap.put(UserId,documentSnapshot.getDouble(UserId)-1.00);
                                hmap.put("pending",documentSnapshot.getDouble("pending")-1.00);

                                mfirestore.collection("HOME").document(college).set(hmap, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        CollectionReference collectionReference = introdbref.collection("ALL VIDEOS");
                                        collectionReference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                            @Override
                                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                if (!(queryDocumentSnapshots.isEmpty())) {
                                                    List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                                                    if (!(list.isEmpty())) {
                                                        for (DocumentSnapshot d : list){
                                                            String id = d.getId();
                                                            mfirestore.collection("HOME").document(college).collection("LISTITEM").document(coursename).collection("ALL VIDEOS").document(d.getId())
                                                                    .delete();
                                                            final StorageReference vidref = mstorage.child("Courses").child(coursename).child(id);
                                                            vidref.delete();
                                                        }
                                                    }
                                                }
                                            }
                                        });
                                        introdbref.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {

                                                final StorageReference imageref = mstorage.child("Courses").child(coursename).child("Course Image");
                                                final StorageReference pdfref = mstorage.child("Courses").child(coursename).child("Course PDF");


                                                imageref.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        try
                                                        {
                                                            pdfref.delete();
                                                        } catch (Exception e) {

                                                        }
                                                        sd.dismiss();
                                                        FragmentTransaction ftt = getFragmentManager().beginTransaction();
                                                        ftt.replace(R.id.fragment_container, new UploadFragment()).commit();

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
                                                ftt.replace(R.id.fragment_container, new Fragments.UploadFragment()).commit();
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

        videoselect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(num<10) {
                    Intent intent = new Intent();
                    intent.setType("video/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(intent, PICK_VIDEO);
                } else {
                    videoselect.setEnabled(false);
                    videoupload.setEnabled(false);
                    videoname.setText("Limit of 10 Videos completed. FINAL SUBMIT the course !\n\nWant to upload more videos create new course named part 2 for this course");
                    Toast.makeText(getContext(), "Limit of 10 Videos completed. FINAL SUBMIT the course !", Toast.LENGTH_SHORT).show();
                    Toast.makeText(getContext(), "Want to upload more videos create new course named part 2 for this course", Toast.LENGTH_SHORT).show();
                }
            }
        });

        videoupload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Videoname = videoname.getText().toString();

                if (!Videoname.isEmpty()) {


                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setMessage("Do you want to upload the above video");
                    builder.setTitle("VIDEO CONFIRMATION");
                    builder.setCancelable(false);
                    builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (videoUri != null ) {
                                videoselect.setVisibility(View.GONE);
                                videoupload.setVisibility(View.GONE);
                                videoname.setEnabled(false);
                                if (num<10) {
                                    pd.setCancelable(false);
                                    pd.setTitle("Saving data");
                                    pd.show();
                                    uploadvideo();
                                } else {
                                    videoname.setText("Limit of 10 Videos completed. FINAL SUBMIT the course !\n\nWant to upload more videos create new course named part 2 for this course");
                                }

                            } else {
                                Toast.makeText(getContext(), "Video loading failed. Select the Video again nad make sure it is not same as the last video", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });

                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            videoname.setText("");
                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();


                } else {
                    Toast.makeText(getContext(), "Video name and Video file is mandatory", Toast.LENGTH_SHORT).show();
                }

            }
        });

        finalsubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage("Do you want to final submit the course");
                builder.setTitle("FINAL SUBMIT !!!");
                builder.setCancelable(false);
                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        mfirestore.collection("USERS").document(UserId).collection("TRANSACTIONS").document("ontheway").update("upload_status",0.00).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                mfirestore.collection("USERS").document(UserId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        String courses_uploaded = documentSnapshot.getString("courses_uploaded");
                                        mfirestore.collection("USERS").document(UserId).update("courses_uploaded",String.valueOf(Integer.parseInt(courses_uploaded)+1)).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                mfirestore.collection("HOME").document(college).collection("LISTITEM").document(coursename).update("final_submit","DONE").addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        FragmentTransaction ftt = getFragmentManager().beginTransaction();
                                                        ftt.replace(R.id.fragment_container, new Fragments.UploadFragment()).commit();
                                                    }
                                                });
                                            }
                                        });
                                    }
                                });
                            }
                        });
                    }
                });

                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        videoname.setText("");
                        videoUri = null;
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();

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

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_VIDEO || resultCode == RESULT_OK ||
                data != null || data.getData() != null){

            path = RealPathUtil.getRealPath(getContext(),data.getData());

            if(path==null) {
                Toast.makeText(getContext(), "Video is not supported from the folder you selected. Try from another folder", Toast.LENGTH_SHORT).show();
            } else {

                File file = new File(path);
                long length = file.length();

                if(flag) {
                    if (length < 40000000) {
                        videoselect.setText("CHANGE");
                        videoUri =data.getData();
                        initializePlayer(String.valueOf(videoUri));

                    } else {
                        Toast.makeText(getContext(), "Select the video less than 40 MB", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent();
                        intent.setType("video/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(intent, PICK_VIDEO);
                    }
                } else {
                    if (length < 5000000) {
                        videoselect.setText("CHANGE");
                        videoUri =data.getData();
                        initializePlayer(String.valueOf(videoUri));
                    } else {
                        Toast.makeText(getContext(), "Select the video less than 5 MB", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent();
                        intent.setType("video/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(intent, PICK_VIDEO);
                    }
                }
            }
        } else {
            Toast.makeText(getContext(), "Video not Selected", Toast.LENGTH_SHORT).show();
        }
    }

    private class UpContentViewHolder2 extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView Text;
        private SubOnClickInterface subOnClickInterface;
        public UpContentViewHolder2(@NonNull View itemView) {
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

    private void uploadvideo() {

        try {
            encryptFile(path,Videoname);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), e.toString(), Toast.LENGTH_SHORT).show();
        } catch (NoSuchPaddingException e) {
            Toast.makeText(getContext(), e.toString(), Toast.LENGTH_SHORT).show();
        } catch (NoSuchAlgorithmException e) {
            Toast.makeText(getContext(), e.toString(), Toast.LENGTH_SHORT).show();
        } catch (InvalidKeyException e) {
            Toast.makeText(getContext(), e.toString(), Toast.LENGTH_SHORT).show();
        }

        final StorageReference videoref = mstorage.child("Courses").child(coursename).child(Videoname);

        videoref.putFile(videoUri).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                double progressPercent = (100.00 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                pd.setMessage("Progress: " + (int) progressPercent + "%");
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                videoref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        num = num + 1;
                        HashMap<String,String> hashMap = new HashMap<>();
                        hashMap.put("Video_Name",Videoname);
                        hashMap.put("Video_Url", String.valueOf(uri));

                        videosdbref.document(Videoname).set(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                videosdbref.document(Videoname).update("Order",num).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        pd.dismiss();
                                        videoselect.setVisibility(View.VISIBLE);
                                        videoupload.setVisibility(View.VISIBLE);
                                        videoname.setEnabled(true);
                                        finalsubmit.setVisibility(View.VISIBLE);
                                        videoname.setText("");
                                        videoselect.setText("SELECT NEXT");
                                        Toast.makeText(getContext(), Videoname+" Uploaded successfully.", Toast.LENGTH_SHORT).show();
                                        videoUri = null;
                                    }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                pd.dismiss();
                                Toast.makeText(getContext(), "Error ! "+e.toString(), Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                        Toast.makeText(getContext(), "Error ! "+e.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(getContext(), "Error ! "+e.toString(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void encryptFile(String filePath,String encnamevideo) throws IOException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        int read;
        FileInputStream fis = new FileInputStream(new File(filePath));
        File enc = new File(valueOf(getActivity().getExternalFilesDir("/Uploaded Courses/"+coursename)));
        File outfile = new File(enc,encnamevideo);
        if(!outfile.exists())
            outfile.createNewFile();

        FileOutputStream fos = new FileOutputStream(outfile);

        Cipher encipher = Cipher.getInstance("AES");
        encipher.init(Cipher.ENCRYPT_MODE, skey);
        CipherInputStream cis = new CipherInputStream(fis, encipher);

        byte[] buffer = new byte[1024];
        while((read = cis.read(buffer)) >= 0)
        {
            fos.write(buffer, 0, read);
            fos.flush();
        }
        fos.close();
    }

    private void decryptFile(String encryptFilePath, SecretKey secretKey, String decnamevideo) throws IOException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        int read;
        File outfile = new File(encryptFilePath);
        File dec = new File(valueOf(getActivity().getExternalFilesDir("/data/ob/is/sgisfjf/sfygf/faigiyg/afuyg/fguy\"")));
        File decfile = new File(dec,decnamevideo);
        if(!decfile.exists())
            decfile.createNewFile();

        FileOutputStream decfos = new FileOutputStream(decfile);
        FileInputStream encfis = new FileInputStream(outfile);

        Cipher decipher = Cipher.getInstance("AES");

        decipher.init(Cipher.DECRYPT_MODE, secretKey);
        CipherOutputStream cos = new CipherOutputStream(decfos,decipher);

        byte[] buffer = new byte[1024];
        while((read=encfis.read(buffer)) >= 0)
        {
            cos.write(buffer, 0, read);
            cos.flush();
        }
        cos.close();
        String video = String.valueOf(Uri.fromFile(decfile));
        releasePlayer();
        initializePlayer(video);
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

        exoPlayerUploading = ExoPlayerFactory.newSimpleInstance(
                new DefaultRenderersFactory(getActivity()),
                new DefaultTrackSelector(), new DefaultLoadControl());

        playerViewUploading.setPlayer(exoPlayerUploading);

        exoPlayerUploading.setPlayWhenReady(false);

        Uri uri = Uri.parse(video);
        MediaSource mediaSource = buildMediaSource(uri);
        exoPlayerUploading.prepare(mediaSource,false, false);

        exoPlayerUploading.addListener(new Player.DefaultEventListener() {
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
        if (exoPlayerUploading != null) {
            //    playWhenReady = exoPlayerUploading.getPlayWhenReady();
            //      playbackPosition = exoPlayer2.getCurrentPosition();
            //     currentWindow = exoPlayer2.getCurrentWindowIndex();
            exoPlayerUploading.release();
            exoPlayerUploading = null;
        }
    }

    public void resizefun() {

        switch (size) {

            case 0 :
                playerViewUploading.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_ZOOM);
                Toast.makeText(getView().getContext(), "ZOOM mode", Toast.LENGTH_SHORT).show();
                size = 1;
                break;

            case 1 :
                playerViewUploading.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
                Toast.makeText(getView().getContext(), "FIT mode", Toast.LENGTH_SHORT).show();
                size = 2;
                break;

            case 2 :
                playerViewUploading.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FILL);
                Toast.makeText(getView().getContext(), "FILL mode", Toast.LENGTH_SHORT).show();
                size = 3;
                break;

            case 3 :
                playerViewUploading.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIXED_HEIGHT);
                Toast.makeText(getView().getContext(), "FIXED HEIGHT mode", Toast.LENGTH_SHORT).show();
                size = 4;
                break;

            case 4 :
                playerViewUploading.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIXED_WIDTH);
                Toast.makeText(getView().getContext(), "FIXED WIDTH mode", Toast.LENGTH_SHORT).show();
                size = 0;
                break;

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
        getActivity().unregisterReceiver(networkChangeListener);
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

    }

    @Override
    public void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        getActivity().registerReceiver(networkChangeListener,filter);
    }

}