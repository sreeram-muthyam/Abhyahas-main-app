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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

import java.util.HashMap;

import Model.UserModel;

public class FollowersFragment extends Fragment {

    private FirebaseAuth mAuth;
    String userid;
    private RecyclerView firstrec2;
    private FirebaseFirestore mfirestore;
    private FirestoreRecyclerAdapter madapter2;
    ImageView back;
    View view;
    SearchView searchView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(savedInstanceState==null) {
            view = inflater.inflate(R.layout.fragment_followers, container, false);
        }
        //View view = inflater.inflate(R.layout.fragment_followers, container, false);

        back = view.findViewById(R.id.backfollowers);
        mfirestore = FirebaseFirestore.getInstance();
        firstrec2 = view.findViewById(R.id.firstrec2);
        firstrec2.setHasFixedSize(true);
        firstrec2.setLayoutManager(new LinearLayoutManager(view.getContext()));
        mAuth = FirebaseAuth.getInstance();

        userid = mAuth.getCurrentUser().getUid();


        searchView = (SearchView) getActivity().findViewById(R.id.task);
        searchView.setVisibility(View.VISIBLE);

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                FollowersFragment frag = new FollowersFragment();
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

        Query query2 = mfirestore.collection("USERS").whereEqualTo(userid+"+follower","true");

        FirestoreRecyclerOptions<UserModel> options1 = new FirestoreRecyclerOptions.Builder<UserModel>()
                .setQuery(query2,UserModel.class)
                .build();

        madapter2 = new FirestoreRecyclerAdapter<UserModel, FollowersFragment.ViewHolder>(options1) {

            @NonNull
            @Override
            public FollowersFragment.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view2 = LayoutInflater.from(parent.getContext()).inflate(R.layout.usersinfo,parent,false);
                return new FollowersFragment.ViewHolder(view2);
            }

            @Override
            protected void onBindViewHolder(@NonNull FollowersFragment.ViewHolder holder, int position, @NonNull UserModel model) {

                String uid = model.getUser_ID();
                holder.name.setText(model.getFull_Name());
                holder.collegeyear.setText(model.getCollege_Name());
                holder.year.setText("Year of Study : "+model.getYear_of_Study());
                int r = Integer.parseInt(model.getFollowing());
                holder.following.setText(model.getFollowers()+" Followers || "+model.getFollowing()+" Following");
                holder.follow.setText("REMOVE");
                Picasso.get().load(model.getImage()).into(holder.pic);

                holder.follow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (isOnline()) {
                            if (holder.follow.getText().toString().equals("REMOVED")) {
                                Toast.makeText(getContext(), " Already removed ", Toast.LENGTH_SHORT).show();
                            } else {
                                ProgressDialog pd = new ProgressDialog(getContext());
                                pd.setTitle("Loading...");
                                pd.setMessage("Wait for a while. Until "+model.getFull_Name()+" is removed from your Followers list");
                                pd.setCancelable(false);
                                pd.show();

                                mfirestore.runTransaction(new Transaction.Function<Void>() {
                                    @Nullable
                                    @Override
                                    public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {

                                        DocumentReference userref = mfirestore.collection("USERS").document(userid);
                                        DocumentReference anotherref = mfirestore.collection("USERS").document(uid);
                                        DocumentSnapshot userdoc = transaction.get(userref);
                                        DocumentSnapshot anotherdoc = transaction.get(anotherref);
                                        String userfollowers = userdoc.getString("Followers");
                                        String anotherfollowing = anotherdoc.getString("Following");
                                        HashMap<String,String> userhash = new HashMap<>();
                                        userhash.put("Followers",String.valueOf(Integer.parseInt(userfollowers)-1));
                                        userhash.put(uid+"+following","false");
                                        transaction.set(userref,userhash, SetOptions.merge());
                                        HashMap<String,String> anotherhash = new HashMap<>();
                                        anotherhash.put("Following",String.valueOf(Integer.parseInt(anotherfollowing)-1));
                                        anotherhash.put(userid+"+follower","false");
                                        transaction.set(anotherref,anotherhash,SetOptions.merge());

                                        return null;
                                    }
                                }).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        holder.follow.setText("REMOVED");
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
        madapter2.startListening();
        madapter2.notifyDataSetChanged();
        firstrec2.setAdapter(madapter2);
        setHasOptionsMenu(true);

        return view;
    }

    private class ViewHolder extends RecyclerView.ViewHolder{

        private TextView name, collegeyear, following, year;
        Button follow;
        ImageView pic, profile;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.username);
            collegeyear = itemView.findViewById(R.id.collegeyear);
            following = itemView.findViewById(R.id.follow);
            pic = itemView.findViewById(R.id.userpic);
            year = itemView.findViewById(R.id.collegeyear2);
            follow = itemView.findViewById(R.id.followbutton);
            profile = itemView.findViewById(R.id.userpic);

        }
    }


    private void proccessSearch(String s) {

        String SR = s.toLowerCase();

        FirestoreRecyclerOptions<UserModel> options1 = new FirestoreRecyclerOptions.Builder<UserModel>()
                .setQuery(FirebaseFirestore.getInstance().collection("USERS").whereEqualTo(userid+"+follower","true").orderBy("Name_search").startAt(SR).endAt(SR+"\uf8ff"),UserModel.class)
                .build();

        madapter2 = new FirestoreRecyclerAdapter<UserModel, FollowersFragment.ViewHolder>(options1) {

            @NonNull
            @Override
            public FollowersFragment.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view2 = LayoutInflater.from(parent.getContext()).inflate(R.layout.usersinfo,parent,false);
                return new FollowersFragment.ViewHolder(view2);
            }

            @Override
            protected void onBindViewHolder(@NonNull FollowersFragment.ViewHolder holder, int position, @NonNull UserModel model) {

                String uid = model.getUser_ID();
                holder.name.setText(model.getFull_Name());
                holder.collegeyear.setText(model.getCollege_Name());
                holder.year.setText("Year of Study : "+model.getYear_of_Study());
                int r = Integer.parseInt(model.getFollowing());
                holder.following.setText(model.getFollowers()+" Followers || "+model.getFollowing()+" Following");
                holder.follow.setText("REMOVE");
                Picasso.get().load(model.getImage()).into(holder.pic);

                holder.follow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (isOnline()) {
                            if (holder.follow.getText().toString().equals("REMOVED")) {
                                Toast.makeText(getContext(), " Already removed ", Toast.LENGTH_SHORT).show();
                            } else {
                                ProgressDialog pd = new ProgressDialog(getContext());
                                pd.setTitle("Loading...");
                                pd.setMessage("Wait for a while. Until "+model.getFull_Name()+" is removed from your Followers list");
                                pd.setCancelable(false);
                                pd.show();

                                mfirestore.runTransaction(new Transaction.Function<Void>() {
                                    @Nullable
                                    @Override
                                    public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {

                                        DocumentReference userref = mfirestore.collection("USERS").document(userid);
                                        DocumentReference anotherref = mfirestore.collection("USERS").document(uid);
                                        DocumentSnapshot userdoc = transaction.get(userref);
                                        DocumentSnapshot anotherdoc = transaction.get(anotherref);
                                        String userfollowers = userdoc.getString("Followers");
                                        String anotherfollowing = anotherdoc.getString("Following");
                                        HashMap<String,String> userhash = new HashMap<>();
                                        userhash.put("Followers",String.valueOf(Integer.parseInt(userfollowers)-1));
                                        userhash.put(uid+"+following","false");
                                        transaction.set(userref,userhash, SetOptions.merge());
                                        HashMap<String,String> anotherhash = new HashMap<>();
                                        anotherhash.put("Following",String.valueOf(Integer.parseInt(anotherfollowing)-1));
                                        anotherhash.put(userid+"+follower","false");
                                        transaction.set(anotherref,anotherhash,SetOptions.merge());

                                        return null;
                                    }
                                }).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        holder.follow.setText("REMOVED");
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
        madapter2.startListening();
        madapter2.notifyDataSetChanged();
        firstrec2.setAdapter(madapter2);

    }

    private boolean isOnline() {
        ConnectivityManager connMgr = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    @Override
    public void onStart() {
        super.onStart();
        madapter2.startListening();
        if (!(searchView.isIconified())) {
            searchView.setQuery("",false);
            searchView.setIconified(true);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        madapter2.stopListening();
    }
}