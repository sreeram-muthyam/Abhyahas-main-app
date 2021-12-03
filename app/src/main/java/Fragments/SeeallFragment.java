package Fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.squareup.picasso.Picasso;
import com.sreerammuthyam.abhyahas.R;

import Interface.SubOnClickInterface;
import Model.ItemModel;

public class SeeallFragment extends Fragment {

    String ColName,creatorname,creatorimage,UserID,verify;
    ImageView rev;
    TextView Tv;
    View view;
    SearchView searchView;

    private RecyclerView RecViewList;
    private FirebaseAuth mAuth;
    private FirebaseFirestore FFb;
    private FirestoreRecyclerAdapter adapter4;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(savedInstanceState==null) {
            view = inflater.inflate(R.layout.fragment_seeall, container, false);
        }
        //View view = inflater.inflate(R.layout.fragment_seeall, container, false);

        Bundle bundle = this.getArguments();
        if(bundle!=null){
            ColName = (String) bundle.get("CN");
        }

        mAuth = FirebaseAuth.getInstance();
        UserID = mAuth.getCurrentUser().getUid();

        rev = view.findViewById(R.id.rev7);
        Tv = view.findViewById(R.id.See_Col);

        searchView = (SearchView) getActivity().findViewById(R.id.task);
        searchView.setVisibility(View.VISIBLE);

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                Bundle bundle = new Bundle();
                bundle.putString("CN",ColName);
                SeeallFragment frag2 = new SeeallFragment();
                frag2.setArguments(bundle);
                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container,frag2)
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

        Tv.setText(ColName);

        rev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction ftt = getFragmentManager().beginTransaction();
                ftt.replace(R.id.fragment_container, new Fragments.Home()).commit();
            }
        });

        RecViewList = view.findViewById(R.id.See_RecView);
        FFb = FirebaseFirestore.getInstance();
        RecViewList.setHasFixedSize(true);
        RecViewList.setLayoutManager(new LinearLayoutManager(view.getContext()));

        Query query4 = FFb.collection("HOME").document(ColName).collection("LISTITEM").whereEqualTo("Status","accepted");

        FirestoreRecyclerOptions<ItemModel> options4 = new FirestoreRecyclerOptions.Builder<ItemModel>()
                .setQuery(query4, ItemModel.class)
                .build();

        adapter4 = new FirestoreRecyclerAdapter<ItemModel,ShowViewHolder>(options4){
            @NonNull
            @Override
            public ShowViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view1 = LayoutInflater.from(parent.getContext()).inflate(R.layout.seeall_item,parent,false);
                return new ShowViewHolder(view1);
            }

            @Override
            protected void onBindViewHolder(@NonNull ShowViewHolder holder4, int position, @NonNull ItemModel model4) {

                Bundle bundle1 = new Bundle();

                holder4.seeall.setVisibility(View.VISIBLE);

                DocumentReference dref = FFb.collection("USERS").document(model4.getCreater_ID());
                dref.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        creatorname = documentSnapshot.getString("Full_Name");
                        creatorimage = documentSnapshot.getString("Image");
                        holder4.txt_Show__title.setText("by "+creatorname);
                        bundle1.putString("CreNa",creatorname);
                        bundle1.putString("CreIm",creatorimage);
                    }
                });

                holder4.txt_Videoname_show.setText(model4.getCourse_Name());
                Picasso.get().load(model4.getCourse_image()).into(holder4.image_item_show);
                if (model4.getCourse_Price().equals("FREE")) {
                    holder4.Show_price.setText("Free");
                } else {
                    holder4.Show_price.setText("Rs. "+model4.getCourse_Price()+"/-");
                }

                FFb.collection("HOME").document(ColName).collection("LISTITEM").document(model4.getCourse_Name()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        verify = documentSnapshot.getString(UserID);
                        if (verify == null) {
                            verify = "Not purchased";
                        }
                        bundle1.putString("verify",verify);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), e.toString(), Toast.LENGTH_SHORT).show();
                    }
                });

                holder4.SubInterfaceClick(new SubOnClickInterface() {
                    @Override
                    public void OnClick(View view, boolean isLongPressed) {

                        if(creatorname!=null&&creatorimage!=null&&verify!=null&&model4.getCourse_Name()!=null&&model4.getIntro_Url()!=null&&ColName!=null&&model4.getCourse_Description()!=null
                                &&model4.getCourse_Price()!=null&&model4.getCourse_PDF()!=null&&model4.getIntro_Name()!=null&&model4.getTotal_Enrolments()!=null&&model4.getEnrolments()!=null&&model4.getCreater_ID()!=null)
                        {
                            bundle1.putString("ViName",model4.getCourse_Name());
                            bundle1.putString("ViUrl",model4.getIntro_Url());
                            bundle1.putString("CoId",ColName);
                            bundle1.putString("Desc",model4.getCourse_Description());
                            bundle1.putString("Pr",model4.getCourse_Price());
                            bundle1.putString("PDF",model4.getCourse_PDF());
                            bundle1.putString("IName",model4.getIntro_Name());
                            bundle1.putString("enrol",model4.getEnrolments());
                            bundle1.putString("creator", model4.getCreater_ID());
                            bundle1.putString("Total_Enrolments", model4.getTotal_Enrolments());
                            bundle1.putString("back","seeall");

                            PurchaseFragment frag1 = new PurchaseFragment();
                            frag1.setArguments(bundle1);
                            getFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.fragment_container,frag1)
                                    .commit();
                        }
                    }
                });

            }
        };
        adapter4.startListening();
        adapter4.notifyDataSetChanged();
        RecViewList.setAdapter(adapter4);
        setHasOptionsMenu(true);

        return view;
    }

    private class ShowViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView txt_Show__title;
        TextView txt_Videoname_show,Show_price;
        ImageView image_item_show;
        View seeall;
        private SubOnClickInterface subOnClickInterface;

        public ShowViewHolder(@NonNull View itemView) {
            super(itemView);

            txt_Videoname_show = (TextView) itemView.findViewById(R.id.tv_show_Title);
            txt_Show__title = (TextView) itemView.findViewById(R.id.showTitle);
            image_item_show = (ImageView) itemView.findViewById(R.id.showImage);
            Show_price = (TextView) itemView.findViewById(R.id.item_price_show);
            seeall = (View) itemView.findViewById(R.id.seeitem);

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

    private void proccessSearch(String s) {
        String SR = s.toLowerCase();

        Query query5 = FFb.collection("HOME").document(ColName).collection("LISTITEM").whereEqualTo("Status","accepted");

        FirestoreRecyclerOptions<ItemModel> options = new FirestoreRecyclerOptions.Builder<ItemModel>()
                .setQuery(query5.orderBy("Course_search").startAt(s).endAt(s + "\uf8ff"), ItemModel.class)
                .build();
        adapter4 = new FirestoreRecyclerAdapter<ItemModel,ShowViewHolder>(options){
            @NonNull
            @Override
            public ShowViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view1 = LayoutInflater.from(parent.getContext()).inflate(R.layout.seeall_item,parent,false);
                return new ShowViewHolder(view1);
            }

            @Override
            protected void onBindViewHolder(@NonNull ShowViewHolder holder4, int position, @NonNull ItemModel model4) {

                Bundle bundle1 = new Bundle();

                holder4.seeall.setVisibility(View.VISIBLE);

                DocumentReference dref = FFb.collection("USERS").document(model4.getCreater_ID());
                dref.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        creatorname = documentSnapshot.getString("Full_Name");
                        creatorimage = documentSnapshot.getString("Image");
                        holder4.txt_Show__title.setText("by "+creatorname);
                        bundle1.putString("CreNa",creatorname);
                        bundle1.putString("CreIm",creatorimage);
                    }
                });

                holder4.txt_Videoname_show.setText(model4.getCourse_Name());
                Picasso.get().load(model4.getCourse_image()).into(holder4.image_item_show);
                if (model4.getCourse_Price().equals("FREE")) {
                    holder4.Show_price.setText("Free");
                } else {
                    holder4.Show_price.setText("Rs. "+model4.getCourse_Price()+"/-");
                }

                FFb.collection("HOME").document(ColName).collection("LISTITEM").document(model4.getCourse_Name()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String verify = documentSnapshot.getString(UserID);
                        if (verify == null) {
                            verify = "Not purchased";
                        }
                        bundle1.putString("verify",verify);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), e.toString(), Toast.LENGTH_SHORT).show();
                    }
                });

                holder4.SubInterfaceClick(new SubOnClickInterface() {
                    @Override
                    public void OnClick(View view, boolean isLongPressed) {
                        if (creatorname != null && creatorimage != null && verify != null && model4.getCourse_Name() != null && model4.getIntro_Url() != null && ColName != null && model4.getCourse_Description() != null
                                && model4.getCourse_Price() != null && model4.getCourse_PDF() != null && model4.getIntro_Name() != null && model4.getTotal_Enrolments() != null && model4.getEnrolments() != null && model4.getCreater_ID() != null) {
                            bundle1.putString("ViName", model4.getCourse_Name());
                            bundle1.putString("ViUrl", model4.getIntro_Url());
                            bundle1.putString("CoId", ColName);
                            bundle1.putString("Desc", model4.getCourse_Description());
                            bundle1.putString("Pr", model4.getCourse_Price());
                            bundle1.putString("PDF", model4.getCourse_PDF());
                            bundle1.putString("IName", model4.getIntro_Name());
                            bundle1.putString("enrol", model4.getEnrolments());
                            bundle1.putString("creator", model4.getCreater_ID());
                            bundle1.putString("Total_Enrolments", model4.getTotal_Enrolments());
                            bundle1.putString("back", "seeall");

                            PurchaseFragment frag1 = new PurchaseFragment();
                            frag1.setArguments(bundle1);
                            getFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.fragment_container, frag1)
                                    .commit();
                        }
                    }
                });

            }
        };
        adapter4.startListening();
        adapter4.notifyDataSetChanged();
        RecViewList.setAdapter(adapter4);


    }

    @Override
    public void onStart() {
        super.onStart();
        adapter4.startListening();
        if (!(searchView.isIconified())) {
            searchView.setQuery("",false);
            searchView.setIconified(true);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter4.stopListening();
    }
}