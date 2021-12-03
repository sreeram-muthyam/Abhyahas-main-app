package Fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.squareup.picasso.Picasso;
import com.sreerammuthyam.abhyahas.R;

import Interface.SubOnClickInterface;
import Model.CollegeItemModel;
import Model.ItemModel;

public class Home extends Fragment {

    String creatorname, creatorimage, UserID, verify, title, message;

    private RecyclerView mFirestoreList;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth mAuth;
    private FirestoreRecyclerAdapter adapter;
    private FirestoreRecyclerAdapter adapter2;
    View view;
    SearchView searchView;

    DocumentReference mref;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if(savedInstanceState==null) {
            view = inflater.inflate(R.layout.fragment_home, container, false);
        }
        //View view = inflater.inflate(R.layout.fragment_home, container, false);

        FloatingActionButton fab = view.findViewById(R.id.fab_btn_home);

        mAuth = FirebaseAuth.getInstance();
        UserID = mAuth.getCurrentUser().getUid();

        searchView = (SearchView) getActivity().findViewById(R.id.task);
        searchView.setVisibility(View.VISIBLE);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(view.getContext(), "Request a Course", Toast.LENGTH_SHORT).show();
                Intent BroeseIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://docs.google.com/forms/d/e/1FAIpQLSehYDK3wCoI_8Sl-3U_-KvePSuvpyfifDTWMkAe0jF7T3hUEg/viewform"));
                startActivity(BroeseIntent);
            }
        });

        firebaseFirestore = FirebaseFirestore.getInstance();
        mFirestoreList = view.findViewById(R.id.OuterRec_View);

        mFirestoreList.setHasFixedSize(true);
        mFirestoreList.setLayoutManager(new LinearLayoutManager(view.getContext()));

        mref = firebaseFirestore.collection("ADMIN").document("message");
        mref.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                title = documentSnapshot.getString("title");
                message = documentSnapshot.getString("message");
                Double d = documentSnapshot.getDouble("d");
                if(d==1.00) {
                   return;
                } else {
                    new AlertDialog.Builder(getContext())
                            .setIcon(R.mipmap.ic_launcher)
                            .setTitle(title)
                            .setMessage(message)
                            .setCancelable(false)
                            .setPositiveButton("Close App", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    getActivity().finishAffinity();
                                    System.exit(0);
                                }
                            })
                            .show();
                }
            }
        });


        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                Home frag = new Home();
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

        Query query = firebaseFirestore.collection("HOME").whereGreaterThan("accepted", 0);

        FirestoreRecyclerOptions<CollegeItemModel> options = new FirestoreRecyclerOptions.Builder<CollegeItemModel>()
                .setQuery(query, CollegeItemModel.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<CollegeItemModel, CollegeViewHolder>(options) {
            @NonNull
            @Override
            public CollegeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view1 = LayoutInflater.from(parent.getContext()).inflate(R.layout.college_item, parent, false);
                return new CollegeViewHolder(view1);

            }

            @Override
            public void onError(@NonNull FirebaseFirestoreException e) {
                super.onError(e);

            }

            @Override
            protected void onBindViewHolder(@NonNull CollegeViewHolder holder, int position, @NonNull CollegeItemModel model) {

                if (model.getCollege_Name() != null) {
                    Query query1 = firebaseFirestore.collection("HOME").document(model.getCollege_Name()).collection("LISTITEM").limit(4).whereEqualTo("Status", "accepted");
                    FirestoreRecyclerOptions<ItemModel> options1 = new FirestoreRecyclerOptions.Builder<ItemModel>()
                            .setQuery(query1, ItemModel.class)
                            .build();


                    adapter2 = new FirestoreRecyclerAdapter<ItemModel, ItemViewHolder>(options1) {
                        @NonNull
                        @Override
                        public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
                            return new ItemViewHolder(v);
                        }

                        @Override
                        protected void onBindViewHolder(@NonNull ItemViewHolder holder1, int position, @NonNull ItemModel model1) {

                            holder.list_Colname.setVisibility(View.VISIBLE);
                            holder.but1.setVisibility(View.VISIBLE);
                            holder.list_Colname.setText(model.getCollege_Name());

                            holder1.txt_Videoname.setText(model1.getCourse_Name());
                            Picasso.get().load(model1.getCourse_image()).into(holder1.image_item);
                            if (model1.getCourse_Price().equals("FREE")) {
                                holder1.PriceId.setText("Free");
                            } else {
                                holder1.PriceId.setText("Rs. " + model1.getCourse_Price() + "/-");
                            }
                            holder.but1.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    if(model.getCollege_Name()!=null){
                                        Bundle bundle = new Bundle();
                                        bundle.putString("CN", model.getCollege_Name());


                                        SeeallFragment frag = new SeeallFragment();
                                        frag.setArguments(bundle);
                                        getFragmentManager()
                                                .beginTransaction()
                                                .replace(R.id.fragment_container, frag)
                                                .commit();
                                    } else {
                                        Toast.makeText(getContext(), "     Loading...     ", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                            Bundle bundle1 = new Bundle();

                            DocumentReference dref = firebaseFirestore.collection("USERS").document(model1.getCreater_ID());

                            dref.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    creatorname = documentSnapshot.getString("Full_Name");
                                    creatorimage = documentSnapshot.getString("Image");
                                    bundle1.putString("CreNa", creatorname);
                                    bundle1.putString("CreIm", creatorimage);
                                    holder1.txt_item__title.setText("by " + creatorname);
                                }
                            });

                            firebaseFirestore.collection("HOME").document(model.getCollege_Name()).collection("LISTITEM").document(model1.getCourse_Name()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    verify = documentSnapshot.getString(UserID);
                                    if (verify == null) {
                                        verify = "Not purchased";
                                    }
                                    bundle1.putString("verify", verify);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getContext(), e.toString(), Toast.LENGTH_SHORT).show();
                                }
                            });


                            holder1.SubInterfaceClick(new SubOnClickInterface() {
                                @Override
                                public void OnClick(View view, boolean isLongPressed) {

                                    if(creatorname!=null&&creatorimage!=null&&verify!=null&&model1.getCourse_Name()!=null&&model1.getIntro_Url()!=null&&model.getCollege_Name()!=null&&model1.getCourse_Description()!=null
                                    &&model1.getCourse_Price()!=null&&model1.getCourse_PDF()!=null&&model1.getIntro_Name()!=null&&model1.getTotal_Enrolments()!=null&&model1.getEnrolments()!=null&&model1.getCreater_ID()!=null) {
                                        bundle1.putString("ViName", model1.getCourse_Name());
                                        bundle1.putString("ViUrl", model1.getIntro_Url());
                                        bundle1.putString("CoId", model.getCollege_Name());
                                        bundle1.putString("Desc", model1.getCourse_Description());
                                        bundle1.putString("Pr", model1.getCourse_Price());
                                        bundle1.putString("PDF", model1.getCourse_PDF());
                                        bundle1.putString("IName", model1.getIntro_Name());
                                        bundle1.putString("enrol", model1.getEnrolments());
                                        bundle1.putString("creator", model1.getCreater_ID());
                                        bundle1.putString("Total_Enrolments", model1.getTotal_Enrolments());
                                        bundle1.putString("back", "home");
                                        PurchaseFragment frag1 = new PurchaseFragment();
                                        frag1.setArguments(bundle1);
                                        getFragmentManager()
                                                .beginTransaction()
                                                .replace(R.id.fragment_container, frag1)
                                                .commit();
                                    } else {
                                        Toast.makeText(getContext(), "   Loading...   ", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    };
                    adapter2.startListening();
                    adapter2.notifyDataSetChanged();
                    holder.RecViewInner.setAdapter(adapter2);
                } else {
                    return;
                }

            }
        };
        adapter.startListening();
        adapter.notifyDataSetChanged();
        mFirestoreList.setAdapter(adapter);

        setHasOptionsMenu(true);

        return view;
    }

    private class CollegeViewHolder extends RecyclerView.ViewHolder {
        private TextView list_Colname;
        private TextView but1;
        View vv2;
        RecyclerView RecViewInner;
        public RecyclerView.LayoutManager manager;

        public CollegeViewHolder(@NonNull View itemView) {
            super(itemView);
            vv2 = itemView.findViewById(R.id.vv2);
            list_Colname = itemView.findViewById(R.id.list_Col_name);
            RecViewInner = itemView.findViewById(R.id.recycler_view_list);
            but1 = itemView.findViewById(R.id.SHOW_ALL);
            but1.setVisibility(View.GONE);
            list_Colname.setVisibility(View.GONE);
            manager = new LinearLayoutManager(itemView.getContext(), LinearLayoutManager.HORIZONTAL, false);
            RecViewInner.setLayoutManager(manager);

        }
    }

    private class CollegeViewHolder2 extends RecyclerView.ViewHolder {

        RecyclerView RecViewInner;
        public RecyclerView.LayoutManager manager;

        public CollegeViewHolder2(@NonNull View itemView) {
            super(itemView);
            RecViewInner = itemView.findViewById(R.id.recycler_view_list);
            manager = new LinearLayoutManager(itemView.getContext(), LinearLayoutManager.HORIZONTAL, false);
            RecViewInner.setLayoutManager(manager);

        }
    }

    private class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView txt_item__title;
        TextView txt_Videoname, PriceId;
        ImageView image_item;
        private SubOnClickInterface subOnClickInterface;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            txt_item__title = itemView.findViewById(R.id.tv_sub_Title);
            txt_Videoname = itemView.findViewById(R.id.tvTitle);
            image_item = itemView.findViewById(R.id.itemImage);
            PriceId = itemView.findViewById(R.id.item_price);

            itemView.setOnClickListener(this);

        }

        public void SubInterfaceClick(SubOnClickInterface subOnClickInterface) {
            this.subOnClickInterface = subOnClickInterface;
        }

        @Override
        public void onClick(View vi) {
            subOnClickInterface.OnClick(vi, false);
        }
    }

    private void proccessSearch(String s) {

        String SR = s.toLowerCase();

        Query query = firebaseFirestore.collection("HOME").whereGreaterThan("accepted", 0);

        FirestoreRecyclerOptions<CollegeItemModel> options = new FirestoreRecyclerOptions.Builder<CollegeItemModel>()
                .setQuery(query, CollegeItemModel.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<CollegeItemModel, CollegeViewHolder2>(options) {
            @NonNull
            @Override
            public CollegeViewHolder2 onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view1 = LayoutInflater.from(parent.getContext()).inflate(R.layout.searchcollegeitem, parent, false);
                return new CollegeViewHolder2(view1);

            }

            @Override
            protected void onBindViewHolder(@NonNull CollegeViewHolder2 holder, int position, @NonNull CollegeItemModel model) {

                Query query1 = firebaseFirestore.collection("HOME").document(model.getCollege_Name()).collection("LISTITEM").whereEqualTo("Status", "accepted");
                FirestoreRecyclerOptions<ItemModel> options1 = new FirestoreRecyclerOptions.Builder<ItemModel>()
                        .setQuery(query1.orderBy("Course_search").startAt(SR).endAt(SR + "\uf8ff"), ItemModel.class)
                        .build();

                adapter2 = new FirestoreRecyclerAdapter<ItemModel, ItemViewHolder>(options1) {
                    @NonNull
                    @Override
                    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
                        return new ItemViewHolder(v);
                    }

                    @Override
                    protected void onBindViewHolder(@NonNull ItemViewHolder holder1, int position, @NonNull ItemModel model1) {

                        holder1.txt_Videoname.setText(model1.getCourse_Name());
                        Picasso.get().load(model1.getCourse_image()).into(holder1.image_item);
                        if (model1.getCourse_Price().equals("FREE")) {
                            holder1.PriceId.setText("Free");
                        } else {
                            holder1.PriceId.setText("Rs. " + model1.getCourse_Price() + "/-");
                        }


                        Bundle bundle1 = new Bundle();
                        DocumentReference dref = firebaseFirestore.collection("USERS").document(model1.getCreater_ID());

                        dref.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {

                                creatorname = documentSnapshot.getString("Full_Name");
                                creatorimage = documentSnapshot.getString("Image");
                                bundle1.putString("CreNa", creatorname);
                                bundle1.putString("CreIm", creatorimage);
                                holder1.txt_item__title.setText("by " + creatorname);

                            }
                        });

                        firebaseFirestore.collection("HOME").document(model.getCollege_Name()).collection("LISTITEM").document(model1.getCourse_Name()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                String verify = documentSnapshot.getString(UserID);
                                if (verify == null) {
                                    verify = "Not purchased";
                                }
                                bundle1.putString("verify", verify);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getContext(), e.toString(), Toast.LENGTH_SHORT).show();
                            }
                        });


                        holder1.SubInterfaceClick(new SubOnClickInterface() {
                            @Override
                            public void OnClick(View view, boolean isLongPressed) {
                                if(creatorname!=null&&creatorimage!=null&&verify!=null&&model1.getCourse_Name()!=null&&model1.getIntro_Url()!=null&&model.getCollege_Name()!=null&&model1.getCourse_Description()!=null
                                        &&model1.getCourse_Price()!=null&&model1.getCourse_PDF()!=null&&model1.getIntro_Name()!=null&&model1.getTotal_Enrolments()!=null&&model1.getEnrolments()!=null&&model1.getCreater_ID()!=null) {
                                    bundle1.putString("ViName", model1.getCourse_Name());
                                    bundle1.putString("ViUrl", model1.getIntro_Url());
                                    bundle1.putString("CoId", model.getCollege_Name());
                                    bundle1.putString("Desc", model1.getCourse_Description());
                                    bundle1.putString("Pr", model1.getCourse_Price());
                                    bundle1.putString("PDF", model1.getCourse_PDF());
                                    bundle1.putString("IName", model1.getIntro_Name());
                                    bundle1.putString("enrol", model1.getEnrolments());
                                    bundle1.putString("creator", model1.getCreater_ID());
                                    bundle1.putString("Total_Enrolments", model1.getTotal_Enrolments());
                                    bundle1.putString("back","home");
                                    PurchaseFragment frag1 = new PurchaseFragment();
                                    frag1.setArguments(bundle1);
                                    getFragmentManager()
                                            .beginTransaction()
                                            .replace(R.id.fragment_container, frag1)
                                            .commit();
                                } else {
                                    Toast.makeText(getContext(), "   Loading...   ", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                };
                adapter2.startListening();
                adapter2.notifyDataSetChanged();
                holder.RecViewInner.setAdapter(adapter2);
            }
        };
        adapter.startListening();
        adapter.notifyDataSetChanged();
        mFirestoreList.setAdapter(adapter);

    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
        if (!(searchView.isIconified())) {
            searchView.setQuery("",false);
            searchView.setIconified(true);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}