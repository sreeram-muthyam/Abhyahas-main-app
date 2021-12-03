package Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.Transaction;
import com.squareup.picasso.Picasso;
import com.sreerammuthyam.abhyahas.R;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import Model.UserModel;

import static java.lang.String.valueOf;

public class ConnectmoreFragment extends Fragment {

    ImageView back;
    private RecyclerView firstrec;
    private FirebaseFirestore mfirestore;
    private FirestoreRecyclerAdapter madapter;
    private FirebaseAuth mAuth;
    String userid;
    View view;
    SearchView searchView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if(savedInstanceState==null) {
            view = inflater.inflate(R.layout.fragment_connectmore, container, false);
        }
       // View view = inflater.inflate(R.layout.fragment_connectmore, container, false);

        back = view.findViewById(R.id.backprofile1);
        mfirestore = FirebaseFirestore.getInstance();
        firstrec = view.findViewById(R.id.firstrec);
        firstrec.setHasFixedSize(true);
        firstrec.setLayoutManager(new LinearLayoutManager(view.getContext()));

        mAuth = FirebaseAuth.getInstance();

        userid = mAuth.getCurrentUser().getUid();

        searchView = (SearchView) getActivity().findViewById(R.id.task);
        searchView.setVisibility(View.VISIBLE);

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                ConnectmoreFragment frag = new ConnectmoreFragment();
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

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction ftt = getFragmentManager().beginTransaction();
                ftt.replace(R.id.fragment_container, new Fragments.MyprofileFragment()).commit();
            }
        });

        Query query2 = mfirestore.collection("USERS").whereNotEqualTo("User_ID","deleted");

        FirestoreRecyclerOptions<UserModel> options = new FirestoreRecyclerOptions.Builder<UserModel>()
                .setQuery(query2,UserModel.class)
                .build();

        madapter = new FirestoreRecyclerAdapter<UserModel,ModelViewHolder>(options) {


            @NonNull
            @Override
            public ModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view2 = LayoutInflater.from(parent.getContext()).inflate(R.layout.usersinfo,parent,false);
                return new ModelViewHolder(view2);
            }

            @Override
            protected void onBindViewHolder(@NonNull ModelViewHolder holder, int position, @NonNull UserModel model) {

                String uid = model.getUser_ID();

                DocumentReference btnref = mfirestore.collection("USERS").document(userid);
                btnref.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String r = documentSnapshot.getString(uid+"+follower");
                        if(r!=null) {
                            if(r.equals("true")) {
                                holder.follow.setText("UNFOLLOW");
                            } else {
                                holder.follow.setText("FOLLOW");
                            }
                        } else {
                                holder.follow.setText("FOLLOW");
                        }
                    }
                });

                    holder.name.setText(model.getFull_Name());
                    holder.collegeyear.setText(model.getCollege_Name());
                    holder.year.setText("Year of Study : "+model.getYear_of_Study());
                    holder.following.setText(model.getFollowers()+" Followers || "+model.getFollowing()+" Following");
                    Picasso.get().load(model.getImage()).into(holder.pic);


                    holder.follow.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (isOnline()) {
                                ProgressDialog pd = new ProgressDialog(getContext());
                                pd.setTitle("Loading....");
                                pd.setCancelable(false);

                                if (holder.follow.getText().toString().equals("FOLLOW")) {
                                    // write code to follow

                                    pd.setMessage("Wait for a while until "+model.getFull_Name()+" is added to your Following List");
                                    pd.show();

                                    mfirestore.runTransaction(new Transaction.Function<Void>() {
                                        @Nullable
                                        @Override
                                        public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {

                                            DocumentReference userref = mfirestore.collection("USERS").document(userid);
                                            DocumentReference anotherref = mfirestore.collection("USERS").document(uid);
                                            DocumentSnapshot userdoc = transaction.get(userref);
                                            DocumentSnapshot anotherdoc = transaction.get(anotherref);
                                            String userfollowing = userdoc.getString("Following");
                                            String anotherfollower = anotherdoc.getString("Followers");
                                            HashMap<String,String> userhash = new HashMap<>();
                                            userhash.put("Following",String.valueOf(Integer.parseInt(userfollowing)+1));
                                            userhash.put(uid+"+follower","true");
                                            transaction.set(userref,userhash,SetOptions.merge());
                                            HashMap<String,String> anotherhash = new HashMap<>();
                                            anotherhash.put("Followers",String.valueOf(Integer.parseInt(anotherfollower)+1));
                                            anotherhash.put(userid+"+following","true");
                                            transaction.set(anotherref,anotherhash,SetOptions.merge());

                                            return null;
                                        }
                                    }).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            pd.dismiss();
                                            Toast.makeText(getContext(), "Now you are following "+model.getFull_Name(), Toast.LENGTH_SHORT).show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            pd.dismiss();
                                            Toast.makeText(getContext(), "Error occured. Try after sometime", Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                } else {
                                    // write code to unfollow


                                    pd.setMessage("Wait for a while until "+model.getFull_Name()+" is removed from your Following List");
                                    pd.show();

                                    mfirestore.runTransaction(new Transaction.Function<Void>() {
                                        @Nullable
                                        @Override
                                        public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {

                                            DocumentReference userref = mfirestore.collection("USERS").document(userid);
                                            DocumentReference anotherref = mfirestore.collection("USERS").document(uid);
                                            DocumentSnapshot userdoc = transaction.get(userref);
                                            DocumentSnapshot anotherdoc = transaction.get(anotherref);
                                            String userfollowing = userdoc.getString("Following");
                                            String anotherfollower = anotherdoc.getString("Followers");
                                            HashMap<String,String> userhash = new HashMap<>();
                                            userhash.put("Following",String.valueOf(Integer.parseInt(userfollowing)-1));
                                            userhash.put(uid+"+follower","false");
                                            transaction.set(userref,userhash,SetOptions.merge());
                                            HashMap<String,String> anotherhash = new HashMap<>();
                                            anotherhash.put("Followers",String.valueOf(Integer.parseInt(anotherfollower)-1));
                                            anotherhash.put(userid+"+following","false");
                                            transaction.set(anotherref,anotherhash,SetOptions.merge());

                                            return null;
                                        }
                                    }).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            pd.dismiss();
                                            Toast.makeText(getContext(), model.getFull_Name()+" is removed successfully", Toast.LENGTH_SHORT).show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            pd.dismiss();
                                            Toast.makeText(getContext(), "Error occurred. Try after sometime", Toast.LENGTH_SHORT).show();
                                        }
                                    });


                                }
                            } else {
                                Toast.makeText(getContext(), " Check your INTERNET connection ", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }

        };

        madapter.startListening();
        madapter.notifyDataSetChanged();
        firstrec.setAdapter(madapter);
        setHasOptionsMenu(true);

        return view;
    }

    private class ModelViewHolder extends RecyclerView.ViewHolder{

        private TextView name, collegeyear, following, year;
        Button follow;
        ImageView pic;
        View user;

        public ModelViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.username);
            collegeyear = itemView.findViewById(R.id.collegeyear);
            following = itemView.findViewById(R.id.follow);
            pic = itemView.findViewById(R.id.userpic);
            year = itemView.findViewById(R.id.collegeyear2);
            follow = itemView.findViewById(R.id.followbutton);
            user = itemView.findViewById(R.id.user);

        }
    }
    private void proccessSearch(String s) {
        String SR = s.toLowerCase();
        FirestoreRecyclerOptions<UserModel> options = new FirestoreRecyclerOptions.Builder<UserModel>()
                .setQuery(FirebaseFirestore.getInstance().collection("USERS").orderBy("Name_search").startAt(SR).endAt(SR+"\uf8ff"),UserModel.class)
                .build();

        madapter = new FirestoreRecyclerAdapter<UserModel,ModelViewHolder>(options) {


            @NonNull
            @Override
            public ModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view2 = LayoutInflater.from(parent.getContext()).inflate(R.layout.usersinfo,parent,false);
                return new ModelViewHolder(view2);
            }

            @Override
            protected void onBindViewHolder(@NonNull ModelViewHolder holder, int position, @NonNull UserModel model) {

                String uid = model.getUser_ID();

                DocumentReference btnref = mfirestore.collection("USERS").document(userid);
                btnref.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String r = documentSnapshot.getString(uid+"+follower");
                        if(r!=null) {
                            if(r.equals("true")) {
                                holder.follow.setText("UNFOLLOW");
                            } else {
                                holder.follow.setText("FOLLOW");
                            }
                        } else {
                            holder.follow.setText("FOLLOW");
                        }
                    }
                });

                holder.name.setText(model.getFull_Name());
                holder.collegeyear.setText(model.getCollege_Name());
                holder.year.setText("Year of Study : "+model.getYear_of_Study());
                holder.following.setText(model.getFollowers()+" Followers || "+model.getFollowing()+" Following");
                Picasso.get().load(model.getImage()).into(holder.pic);


                holder.follow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (isOnline()) {
                            ProgressDialog pd = new ProgressDialog(getContext());
                            pd.setTitle("Loading....");
                            pd.setCancelable(false);

                            if (holder.follow.getText().toString().equals("FOLLOW")) {
                                // write code to follow

                                pd.setMessage("Wait for a while until "+model.getFull_Name()+" is added to your Following List");
                                pd.show();

                                mfirestore.runTransaction(new Transaction.Function<Void>() {
                                    @Nullable
                                    @Override
                                    public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {

                                        DocumentReference userref = mfirestore.collection("USERS").document(userid);
                                        DocumentReference anotherref = mfirestore.collection("USERS").document(uid);
                                        DocumentSnapshot userdoc = transaction.get(userref);
                                        DocumentSnapshot anotherdoc = transaction.get(anotherref);
                                        String userfollowing = userdoc.getString("Following");
                                        String anotherfollower = anotherdoc.getString("Followers");
                                        HashMap<String,String> userhash = new HashMap<>();
                                        userhash.put("Following",String.valueOf(Integer.parseInt(userfollowing)+1));
                                        userhash.put(uid+"+follower","true");
                                        transaction.set(userref,userhash,SetOptions.merge());
                                        HashMap<String,String> anotherhash = new HashMap<>();
                                        anotherhash.put("Followers",String.valueOf(Integer.parseInt(anotherfollower)+1));
                                        anotherhash.put(userid+"+following","true");
                                        transaction.set(anotherref,anotherhash,SetOptions.merge());

                                        return null;
                                    }
                                }).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        pd.dismiss();
                                        Toast.makeText(getContext(), "Now you are following "+model.getFull_Name(), Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        pd.dismiss();
                                        Toast.makeText(getContext(), "Error occured. Try after sometime", Toast.LENGTH_SHORT).show();
                                    }
                                });

                            } else {
                                // write code to unfollow


                                pd.setMessage("Wait for a while until "+model.getFull_Name()+" is removed from your Following List");
                                pd.show();

                                mfirestore.runTransaction(new Transaction.Function<Void>() {
                                    @Nullable
                                    @Override
                                    public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {

                                        DocumentReference userref = mfirestore.collection("USERS").document(userid);
                                        DocumentReference anotherref = mfirestore.collection("USERS").document(uid);
                                        DocumentSnapshot userdoc = transaction.get(userref);
                                        DocumentSnapshot anotherdoc = transaction.get(anotherref);
                                        String userfollowing = userdoc.getString("Following");
                                        String anotherfollower = anotherdoc.getString("Followers");
                                        HashMap<String,String> userhash = new HashMap<>();
                                        userhash.put("Following",String.valueOf(Integer.parseInt(userfollowing)-1));
                                        userhash.put(uid+"+follower","false");
                                        transaction.set(userref,userhash,SetOptions.merge());
                                        HashMap<String,String> anotherhash = new HashMap<>();
                                        anotherhash.put("Followers",String.valueOf(Integer.parseInt(anotherfollower)-1));
                                        anotherhash.put(userid+"+following","false");
                                        transaction.set(anotherref,anotherhash,SetOptions.merge());

                                        return null;
                                    }
                                }).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        pd.dismiss();
                                        Toast.makeText(getContext(), model.getFull_Name()+" is removed successfully", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        pd.dismiss();
                                        Toast.makeText(getContext(), "Error occurred. Try after sometime", Toast.LENGTH_SHORT).show();
                                    }
                                });


                            }
                        } else {
                            Toast.makeText(getContext(), " Check your INTERNET connection ", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }

        };

        madapter.startListening();
        madapter.notifyDataSetChanged();
        firstrec.setAdapter(madapter);

    }

    private boolean isOnline() {
        ConnectivityManager connMgr = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    @Override
    public void onStop() {
        super.onStop();
        madapter.stopListening();
    }

    @Override
    public void onStart() {
        super.onStart();
        madapter.startListening();
        if (!(searchView.isIconified())) {
            searchView.setQuery("",false);
            searchView.setIconified(true);
        }
    }
}