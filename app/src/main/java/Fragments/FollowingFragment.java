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
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
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

public class FollowingFragment extends Fragment {

    private FirebaseAuth mAuth;
    String userid;
    private RecyclerView firstrec1;
    private FirebaseFirestore mfirestore;
    private FirestoreRecyclerAdapter madapter1;
    ImageView back;
    View view;
    SearchView searchView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (savedInstanceState==null) {
            view = inflater.inflate(R.layout.fragment_following, container, false);
        }

        //View view = inflater.inflate(R.layout.fragment_following, container, false);

        mfirestore = FirebaseFirestore.getInstance();
        back = view.findViewById(R.id.backfollowing);
        firstrec1 = view.findViewById(R.id.firstrec1);
        firstrec1.setHasFixedSize(true);
        firstrec1.setLayoutManager(new LinearLayoutManager(view.getContext()));
        mAuth = FirebaseAuth.getInstance();
        userid = mAuth.getCurrentUser().getUid();

        searchView = (SearchView) getActivity().findViewById(R.id.task);
        searchView.setVisibility(View.VISIBLE);

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                FollowingFragment frag = new FollowingFragment();
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

        Query query2 = mfirestore.collection("USERS").whereEqualTo(userid+"+following","true");

        FirestoreRecyclerOptions<UserModel> options1 = new FirestoreRecyclerOptions.Builder<UserModel>()
                .setQuery(query2,UserModel.class)
                .build();

        madapter1 = new FirestoreRecyclerAdapter<UserModel, FollowingFragment.ViewHolder>(options1) {

            @NonNull
            @Override
            public FollowingFragment.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view2 = LayoutInflater.from(parent.getContext()).inflate(R.layout.usersinfo,parent,false);
                return new FollowingFragment.ViewHolder(view2);
            }

            @Override
            protected void onBindViewHolder(@NonNull FollowingFragment.ViewHolder holder, int position, @NonNull UserModel model) {

                String uid = model.getUser_ID();
                holder.name.setText(model.getFull_Name());
                holder.collegeyear.setText(model.getCollege_Name());
                holder.year.setText("Year of Study : "+model.getYear_of_Study());
                int s = Integer.parseInt(model.getFollowers());
                holder.following.setText(model.getFollowers()+" Followers || "+model.getFollowing()+" Following");
                holder.follow.setText("UNFOLLOW");
                Picasso.get().load(model.getImage()).into(holder.pic);

                holder.follow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (isOnline()) {
                            if(holder.follow.getText().toString().equals("UN FOLLOWED")) {
                                Toast.makeText(getContext(), " Already Un followed ", Toast.LENGTH_SHORT).show();
                            } else {
                                ProgressDialog pd = new ProgressDialog(getContext());
                                pd.setTitle("Loading...");
                                pd.setMessage("Wait for a while. Until "+model.getFull_Name()+" is removed from your Following list");
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
                                        String userfollowing = userdoc.getString("Following");
                                        String anotherfollower = anotherdoc.getString("Followers");
                                        HashMap<String,String> userhash = new HashMap<>();
                                        userhash.put("Following",String.valueOf(Integer.parseInt(userfollowing)-1));
                                        userhash.put(uid+"+follower","false");
                                        transaction.set(userref,userhash, SetOptions.merge());
                                        HashMap<String,String> anotherhash = new HashMap<>();
                                        anotherhash.put("Followers",String.valueOf(Integer.parseInt(anotherfollower)-1));
                                        anotherhash.put(userid+"+following","false");
                                        transaction.set(anotherref,anotherhash,SetOptions.merge());

                                        return null;
                                    }
                                }).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        holder.follow.setText("UN FOLLOWED");
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

        madapter1.startListening();
        madapter1.notifyDataSetChanged();
        firstrec1.setAdapter(madapter1);
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
                .setQuery(FirebaseFirestore.getInstance().collection("USERS").whereEqualTo(userid+"+following","true").orderBy("Name_search").startAt(SR).endAt(SR+"\uf8ff"),UserModel.class)
                .build();

        madapter1 = new FirestoreRecyclerAdapter<UserModel, FollowingFragment.ViewHolder>(options1) {

            @NonNull
            @Override
            public FollowingFragment.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view2 = LayoutInflater.from(parent.getContext()).inflate(R.layout.usersinfo,parent,false);
                return new FollowingFragment.ViewHolder(view2);
            }

            @Override
            protected void onBindViewHolder(@NonNull FollowingFragment.ViewHolder holder, int position, @NonNull UserModel model) {

                String uid = model.getUser_ID();
                holder.name.setText(model.getFull_Name());
                holder.collegeyear.setText(model.getCollege_Name());
                holder.year.setText("Year of Study : "+model.getYear_of_Study());
                int s = Integer.parseInt(model.getFollowers());
                holder.following.setText(model.getFollowers()+" Followers || "+model.getFollowing()+" Following");
                holder.follow.setText("UNFOLLOW");
                Picasso.get().load(model.getImage()).into(holder.pic);

                holder.follow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (isOnline()) {
                            if(holder.follow.getText().toString().equals("UN FOLLOWED")) {
                                Toast.makeText(getContext(), " Already Un followed ", Toast.LENGTH_SHORT).show();
                            } else {
                                ProgressDialog pd = new ProgressDialog(getContext());
                                pd.setTitle("Loading...");
                                pd.setMessage("Wait for a while. Until "+model.getFull_Name()+" is removed from your Following list");
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
                                        String userfollowing = userdoc.getString("Following");
                                        String anotherfollower = anotherdoc.getString("Followers");
                                        HashMap<String,String> userhash = new HashMap<>();
                                        userhash.put("Following",String.valueOf(Integer.parseInt(userfollowing)-1));
                                        userhash.put(uid+"+follower","false");
                                        transaction.set(userref,userhash, SetOptions.merge());
                                        HashMap<String,String> anotherhash = new HashMap<>();
                                        anotherhash.put("Followers",String.valueOf(Integer.parseInt(anotherfollower)-1));
                                        anotherhash.put(userid+"+following","false");
                                        transaction.set(anotherref,anotherhash,SetOptions.merge());

                                        return null;
                                    }
                                }).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        holder.follow.setText("UN FOLLOWED");
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


        madapter1.startListening();
        madapter1.notifyDataSetChanged();
        firstrec1.setAdapter(madapter1);
        setHasOptionsMenu(true);

    }


    private boolean isOnline() {
        ConnectivityManager connMgr = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    @Override
    public void onStart() {
        super.onStart();
        madapter1.startListening();
        if (!(searchView.isIconified())) {
            searchView.setQuery("",false);
            searchView.setIconified(true);
        }
    }


    @Override
    public void onStop() {
        super.onStop();
        madapter1.stopListening();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}