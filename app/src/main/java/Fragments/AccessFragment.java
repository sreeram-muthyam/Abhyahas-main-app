package Fragments;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.Transaction;
import com.squareup.picasso.Picasso;
import com.sreerammuthyam.abhyahas.R;
import com.sreerammuthyam.abhyahas.payment_Activity;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import Interface.SubOnClickInterface;
import Model.CollegeItemModel;
import Model.ItemModel;

import static java.lang.String.valueOf;

public class AccessFragment extends Fragment {

    String creatorname,UserId,creator, mon;
    private RecyclerView AccessRec;
    private FirebaseFirestore firebaseFirestore3;
    private FirebaseAuth mAuth;
    private FirestoreRecyclerAdapter adapter8;
    private FirestoreRecyclerAdapter adapter9;
    ProgressDialog pd;
    View view;
    SearchView searchView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            view  = inflater.inflate(R.layout.fragment_access, container, false);
        }
        //View view = inflater.inflate(R.layout.fragment_access, container, false);
        AccessRec = view.findViewById(R.id.Access_Rec);
        firebaseFirestore3 = FirebaseFirestore.getInstance();
        AccessRec.setHasFixedSize(true);
        AccessRec.setLayoutManager(new LinearLayoutManager(view.getContext()));

        mAuth = FirebaseAuth.getInstance();

        UserId = mAuth.getCurrentUser().getUid();

        pd = new ProgressDialog(getContext());

        FloatingActionButton fab = view.findViewById(R.id.fab_btn_access);

        searchView = (SearchView) getActivity().findViewById(R.id.task);
        searchView.setVisibility(View.VISIBLE);

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                AccessFragment frag = new AccessFragment();
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

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(view.getContext(), "Complaint a course", Toast.LENGTH_SHORT).show();
                Intent BroeseIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://docs.google.com/forms/d/e/1FAIpQLSeQgzLMiNh8JO8cvMVnuMMrF_d2_FJYyj8x7HoJ7VKE8F2Fxw/viewform"));
                startActivity(BroeseIntent);
            }
        });

        Query query9 = firebaseFirestore3.collection("HOME").whereGreaterThan(UserId+"access",0);

        FirestoreRecyclerOptions<CollegeItemModel> options9 = new  FirestoreRecyclerOptions.Builder<CollegeItemModel>()
                .setQuery(query9,CollegeItemModel.class)
                .build();
        adapter9 = new FirestoreRecyclerAdapter<CollegeItemModel,AccessColViewHolder>(options9){
            @NonNull
            @Override
            public AccessColViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view3 = LayoutInflater.from(parent.getContext()).inflate(R.layout.college_item,parent,false);
                return new AccessColViewHolder(view3);
            }

            @Override
            protected void onBindViewHolder(@NonNull AccessColViewHolder holder, int position, @NonNull CollegeItemModel model) {

                Query query8 = firebaseFirestore3.collection("HOME").document(model.getCollege_Name()).collection("LISTITEM").whereEqualTo(UserId,"true");

                FirestoreRecyclerOptions<ItemModel> options8 = new FirestoreRecyclerOptions.Builder<ItemModel>()
                        .setQuery(query8,ItemModel.class)
                        .build();

                adapter8 = new FirestoreRecyclerAdapter<ItemModel,AccessViewHolder>(options8){
                    @NonNull
                    @Override
                    public AccessViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View v4 = LayoutInflater.from(parent.getContext()).inflate(R.layout.item,parent,false);
                        return new AccessViewHolder(v4);
                    }

                    @Override
                    protected void onBindViewHolder(@NonNull AccessViewHolder holder6, int position, @NonNull ItemModel model6) {

                        holder.but1.setVisibility(View.GONE);
                        holder.list_Colname.setVisibility(View.VISIBLE);
                        holder.list_Colname.setText(model.getCollege_Name());

                        Bundle bundle8 = new Bundle();

                        DocumentReference dref = firebaseFirestore3.collection("USERS").document(model6.getCreater_ID());

                        dref.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                creatorname = documentSnapshot.getString("Full_Name");
                                holder6.txt_item__title.setText("by "+creatorname);
                                creator = documentSnapshot.getString("Image");
                                bundle8.putString("CreName",creatorname);
                                bundle8.putString("creator",creator);
                            }
                        });

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

                                bundle8.putString("ViName",model6.getCourse_Name());
                                bundle8.putString("Pri",model6.getCourse_Price());
                                bundle8.putString("PDF",model6.getCourse_PDF());
                                bundle8.putString("Vurl",model6.getIntro_Url());
                                bundle8.putString("Desc",model6.getCourse_Description());
                                bundle8.putString("CoID",model.getCollege_Name());
                                bundle8.putString("enrol",model6.getEnrolments());
                                bundle8.putString("Introname",model6.getIntro_Name());

                                File AccFile = new File(valueOf(getActivity().getExternalFilesDir("/Purchased Courses")),UserId+model6.getCourse_Name()+"Purchased");
                                if(AccFile.exists()){

                                    if (creatorname!=null&&creator!=null&&model6.getCourse_Name()!=null&&model6.getCourse_Price()!=null&&model6.getCourse_PDF()!=null&&
                                            model6.getIntro_Url()!=null&&model6.getCourse_Description()!=null&&model6.getEnrolments()!=null&&model.getCollege_Name()!=null&&model6.getIntro_Name()!=null) {

                                        Accessingvideos frag8 = new Accessingvideos();
                                        frag8.setArguments(bundle8);
                                        getFragmentManager()
                                                .beginTransaction()
                                                .replace(R.id.fragment_container,frag8)
                                                .commit();

                                    } else {
                                        Toast.makeText(getContext(), "   Loading...   ", Toast.LENGTH_SHORT).show();
                                    }

                                }else{
                                    new AlertDialog.Builder(getContext())
                                            .setCancelable(false)
                                            .setTitle("Course not available")
                                            .setMessage("You purchased this course on different device. If you think there is a mistake, Contact Team Abhyahas ")
                                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    dialogInterface.dismiss();
                                                }
                                            }).setNegativeButton("Repurchase", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            if(model6.getCourse_Price().equals("FREE")){
                                                pd.setTitle("RE-PURCHASING");
                                                pd.setMessage("Wait for a while until course is added to your access list");
                                                pd.setCancelable(false);
                                                pd.show();
                                                File AccFile = new File(valueOf(getContext().getExternalFilesDir("/Purchased Courses/"+UserId+model6.getCourse_Name()+"Purchased")));
                                                try {
                                                    AccFile.createNewFile();
                                                    Toast.makeText(getContext(), "You have purchased this course again please click on course to open", Toast.LENGTH_SHORT).show();
                                                } catch (IOException e) {
                                                    Toast.makeText(getContext(), "Error while adding course into your access list", Toast.LENGTH_SHORT).show();
                                                    e.printStackTrace();
                                                }
                                                pd.dismiss();

                                            } else {
                                                mon = "true";
                                                HashMap<String, String> hashMap = new HashMap<>();
                                                hashMap.put("price",model6.getCourse_Price());
                                                hashMap.put("coursename",model6.getCourse_Name());
                                                hashMap.put("collid",model.getCollege_Name());
                                                hashMap.put("mon",mon);
                                                hashMap.put("from","access");
                                                firebaseFirestore3.collection("USERS").document(UserId).collection("TRANSACTIONS").document("ontheway").set(hashMap,SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Intent intent = new Intent(getActivity(), payment_Activity.class);
                                                        startActivity(intent);
                                                        getActivity().finish();
                                                    }
                                                });
                                            }
                                        }
                                    }).setNeutralButton("Remove", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            pd.setTitle("Removing...");
                                            pd.setMessage("Wait for a while until "+model6.getCourse_Name()+" is removed from here");
                                            pd.setCancelable(false);
                                            pd.show();

                                            // 1) Usersccess reduce by 1
                                            // 2) Usercourse access false
                                            // 3) Reduce enrolments by 1
                                            // 4) Delete local files
                                            // 5) Check whether it is deleting to admin

                                            firebaseFirestore3.runTransaction(new Transaction.Function<Void>() {
                                                @Nullable
                                                @Override
                                                public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {

                                                    DocumentReference userref = firebaseFirestore3.collection("USERS").document(UserId);
                                                    DocumentSnapshot usersnap = transaction.get(userref);
                                                    DocumentReference clgref = firebaseFirestore3.collection("HOME").document(model.getCollege_Name());
                                                    DocumentReference crsref = firebaseFirestore3.collection("HOME").document(model.getCollege_Name()).collection("LISTITEM").document(model6.getCourse_Name());
                                                    DocumentSnapshot clgsnap = transaction.get(clgref);
                                                    DocumentSnapshot crssnap = transaction.get(crsref);

                                                    Double useraccess = clgsnap.getDouble(UserId+"access");
                                                    Double deleted = clgsnap.getDouble("deleted");
                                                    String enrolments = crssnap.getString("Enrolments");
                                                    String status = crssnap.getString("Status");
                                                    String courses_accessed = usersnap.getString("courses_accessed");

                                                    if(deleted==null)
                                                    {
                                                        deleted=0.00;
                                                    }

                                                    transaction.update(clgref,UserId+"access",useraccess-1.00);
                                                    HashMap<String, String> crshash = new HashMap<>();
                                                    crshash.put("Enrolments",String.valueOf(Integer.parseInt(enrolments)-1));
                                                    crshash.put(UserId,"false");
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
                                                    File delfile2 = new File(valueOf(getActivity().getExternalFilesDir("Purchased Courses/"+UserId+model6.getCourse_Name()+"Purchased/")));
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
                                                    Toast.makeText(getContext(), "Error while removing. Contact Team Abhyahas if you still want to remove the course", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }
                                    }).show();
                               }
                            }
                        });
                    }
                };

                adapter8.startListening();
                adapter8.notifyDataSetChanged();
                holder.RecViewInner.setAdapter(adapter8);

            }
        };

        adapter9.startListening();
        adapter9.notifyDataSetChanged();
        AccessRec.setAdapter(adapter9);

        setHasOptionsMenu(true);

        return view;
    }

    private class AccessViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView txt_item__title;
        TextView txt_Videoname,PriceId;
        ImageView image_item;
        private SubOnClickInterface subOnClickInterface;

        public AccessViewHolder(@NonNull View itemView) {

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

    private class AccessColViewHolder extends RecyclerView.ViewHolder {
        RecyclerView RecViewInner;
        public RecyclerView.LayoutManager manager;
        private TextView list_Colname;
        private TextView but1;
        View v;

        public AccessColViewHolder(@NonNull View itemView) {
            super(itemView);

            RecViewInner = itemView.findViewById(R.id.recycler_view_list);
            manager = new LinearLayoutManager(itemView.getContext(),LinearLayoutManager.HORIZONTAL,false);
            RecViewInner.setLayoutManager(manager);
            v = itemView;
            list_Colname = itemView.findViewById(R.id.list_Col_name);
            but1 = itemView.findViewById(R.id.SHOW_ALL);
            list_Colname.setVisibility(View.INVISIBLE);
            but1.setVisibility(View.INVISIBLE);

        }
    }

    private class AccessColViewHolder2 extends RecyclerView.ViewHolder {
        RecyclerView RecViewInner;
        public RecyclerView.LayoutManager manager;

        public AccessColViewHolder2(@NonNull View itemView) {
            super(itemView);
            RecViewInner = itemView.findViewById(R.id.recycler_view_list);
            manager = new LinearLayoutManager(itemView.getContext(),LinearLayoutManager.HORIZONTAL,false);
            RecViewInner.setLayoutManager(manager);

        }
    }

    private void proccessSearch(String s) {
        String SR = s.toLowerCase();

        Query query9 = firebaseFirestore3.collection("HOME").whereGreaterThan(UserId+"access",0);

        FirestoreRecyclerOptions<CollegeItemModel> options9 = new  FirestoreRecyclerOptions.Builder<CollegeItemModel>()
                .setQuery(query9,CollegeItemModel.class)
                .build();
        adapter9 = new FirestoreRecyclerAdapter<CollegeItemModel,AccessColViewHolder2>(options9){
            @NonNull
            @Override
            public AccessColViewHolder2 onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view3 = LayoutInflater.from(parent.getContext()).inflate(R.layout.searchcollegeitem,parent,false);
                return new AccessColViewHolder2(view3);
            }

            @Override
            protected void onBindViewHolder(@NonNull AccessColViewHolder2 holder, int position, @NonNull CollegeItemModel model) {

                Query query8 = firebaseFirestore3.collection("HOME").document(model.getCollege_Name()).collection("LISTITEM").whereEqualTo(UserId,"true");

                FirestoreRecyclerOptions<ItemModel> options8 = new FirestoreRecyclerOptions.Builder<ItemModel>()
                        .setQuery(query8.orderBy("Course_search").startAt(SR).endAt(SR+"\uf8ff"),ItemModel.class)
                        .build();

                adapter8 = new FirestoreRecyclerAdapter<ItemModel,AccessViewHolder>(options8){
                    @NonNull
                    @Override
                    public AccessViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View v4 = LayoutInflater.from(parent.getContext()).inflate(R.layout.item,parent,false);
                        return new AccessViewHolder(v4);
                    }

                    @Override
                    protected void onBindViewHolder(@NonNull AccessViewHolder holder6, int position, @NonNull ItemModel model6) {

                        Bundle bundle8 = new Bundle();

                        DocumentReference dref = firebaseFirestore3.collection("USERS").document(model6.getCreater_ID());

                        dref.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                creatorname = documentSnapshot.getString("Full_Name");

                                holder6.txt_item__title.setText("by "+creatorname);
                                creator = documentSnapshot.getString("Image");

                                bundle8.putString("CreName",creatorname);
                                bundle8.putString("creator",creator);

                            }
                        });

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

                                bundle8.putString("ViName",model6.getCourse_Name());
                                bundle8.putString("Pri",model6.getCourse_Price());
                                bundle8.putString("PDF",model6.getCourse_PDF());
                                bundle8.putString("Vurl",model6.getIntro_Url());
                                bundle8.putString("Desc",model6.getCourse_Description());
                                bundle8.putString("CoID",model.getCollege_Name());
                                bundle8.putString("enrol",model6.getEnrolments());
                                bundle8.putString("Introname",model6.getIntro_Name());

                                File AccFile = new File(valueOf(getActivity().getExternalFilesDir("/Purchased Courses")),UserId+model6.getCourse_Name()+"Purchased");
                                if(AccFile.exists()){

                                    if (creatorname!=null&&creator!=null&&model6.getCourse_Name()!=null&&model6.getCourse_Price()!=null&&model6.getCourse_PDF()!=null&&
                                            model6.getIntro_Url()!=null&&model6.getCourse_Description()!=null&&model6.getEnrolments()!=null&&model.getCollege_Name()!=null&&model6.getIntro_Name()!=null) {

                                        Accessingvideos frag8 = new Accessingvideos();
                                        frag8.setArguments(bundle8);
                                        getFragmentManager()
                                                .beginTransaction()
                                                .replace(R.id.fragment_container,frag8)
                                                .commit();

                                    } else {
                                        Toast.makeText(getContext(), "   Loading...   ", Toast.LENGTH_SHORT).show();
                                    }

                                } else{
                                    new AlertDialog.Builder(getContext())
                                            .setCancelable(false)
                                            .setTitle("Course not available")
                                            .setMessage("You purchased this course on different device. If you think there is a mistake, Contact Team Abhyahas ")
                                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    dialogInterface.dismiss();
                                                }
                                            }).setNegativeButton("Repurchase", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            if(model6.getCourse_Price().equals("FREE")){
                                                pd.setTitle("RE-PURCHASING");
                                                pd.setMessage("Wait for a while until course is added to your access list");
                                                pd.setCancelable(false);
                                                pd.show();
                                                File AccFile = new File(valueOf(getContext().getExternalFilesDir("/Purchased Courses/"+UserId+model6.getCourse_Name()+"Purchased")));
                                                try {
                                                    AccFile.createNewFile();
                                                    Toast.makeText(getContext(), "You have purchased this course again please click on course to open", Toast.LENGTH_SHORT).show();
                                                } catch (IOException e) {
                                                    Toast.makeText(getContext(), "Error while adding course into your access list", Toast.LENGTH_SHORT).show();
                                                    e.printStackTrace();
                                                }
                                                pd.dismiss();

                                            } else {
                                                mon = "true";
                                                HashMap<String, String> hashMap = new HashMap<>();
                                                hashMap.put("price",model6.getCourse_Price());
                                                hashMap.put("coursename",model6.getCourse_Name());
                                                hashMap.put("collid",model.getCollege_Name());
                                                hashMap.put("mon",mon);
                                                hashMap.put("from","access");
                                                firebaseFirestore3.collection("USERS").document(UserId).collection("TRANSACTIONS").document("ontheway").set(hashMap,SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Intent intent = new Intent(getActivity(), payment_Activity.class);
                                                        startActivity(intent);
                                                        getActivity().finish();
                                                    }
                                                });
                                            }
                                        }
                                    }).setNeutralButton("Remove", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            pd.setTitle("Removing...");
                                            pd.setMessage("Wait for a while until "+model6.getCourse_Name()+" is removed from here");
                                            pd.setCancelable(false);
                                            pd.show();

                                            // 1) Usersccess reduce by 1
                                            // 2) Usercourse access false
                                            // 3) Reduce enrolments by 1
                                            // 4) Delete local files
                                            // 5) Check whether it is deleting to admin

                                            firebaseFirestore3.runTransaction(new Transaction.Function<Void>() {
                                                @Nullable
                                                @Override
                                                public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {

                                                    DocumentReference userref = firebaseFirestore3.collection("USERS").document(UserId);
                                                    DocumentSnapshot usersnap = transaction.get(userref);
                                                    DocumentReference clgref = firebaseFirestore3.collection("HOME").document(model.getCollege_Name());
                                                    DocumentReference crsref = firebaseFirestore3.collection("HOME").document(model.getCollege_Name()).collection("LISTITEM").document(model6.getCourse_Name());
                                                    DocumentSnapshot clgsnap = transaction.get(clgref);
                                                    DocumentSnapshot crssnap = transaction.get(crsref);

                                                    Double useraccess = clgsnap.getDouble(UserId+"access");
                                                    Double deleted = clgsnap.getDouble("deleted");
                                                    String enrolments = crssnap.getString("Enrolments");
                                                    String status = crssnap.getString("Status");
                                                    String courses_accessed = usersnap.getString("courses_accessed");

                                                    if(deleted==null)
                                                    {
                                                        deleted=0.00;
                                                    }

                                                    transaction.update(clgref,UserId+"access",useraccess-1.00);
                                                    HashMap<String, String> crshash = new HashMap<>();
                                                    crshash.put("Enrolments",String.valueOf(Integer.parseInt(enrolments)-1));
                                                    crshash.put(UserId,"false");
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
                                                    File delfile2 = new File(valueOf(getActivity().getExternalFilesDir("Purchased Courses/"+UserId+model6.getCourse_Name()+"Purchased/")));
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
                                                    Toast.makeText(getContext(), "Error while removing. Contact Team Abhyahas if you still want to remove the course", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }
                                    }).show();
                                }
                            }
                        });
                    }
                };

                adapter8.startListening();
                adapter8.notifyDataSetChanged();
                holder.RecViewInner.setAdapter(adapter8);

            }
        };

        adapter9.startListening();
        adapter9.notifyDataSetChanged();
        AccessRec.setAdapter(adapter9);
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


    @Override
    public void onStart() {
        super.onStart();
        adapter9.startListening();
        if (!(searchView.isIconified())) {
            searchView.setQuery("",false);
            searchView.setIconified(true);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter9.stopListening();
    }
}