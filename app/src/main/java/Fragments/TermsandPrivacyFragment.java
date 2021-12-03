package Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
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
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;
import com.sreerammuthyam.abhyahas.R;

import Model.TransactionModel;

public class TermsandPrivacyFragment extends Fragment {

    ImageView back;
    String CreName,Videoname,DESC,Price,VideoUrl,Pdf,CoId,creatorImg,enrol,coursename, UserID, to, amount, Total_Enrolments;
    TextView amount_generated, amount_pending;
    View view;

    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;
    private FirestoreRecyclerAdapter madapter2;
    private RecyclerView firstrec2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(savedInstanceState==null) {
            view = inflater.inflate(R.layout.fragment_termsand_privacy, container, false);
        }
        //View view = inflater.inflate(R.layout.fragment_termsand_privacy, container, false);

        amount_generated = view.findViewById(R.id.amount_generated);
        amount_pending = view.findViewById(R.id.amount_pending);

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        firstrec2 = view.findViewById(R.id.transaction_rec);
        firstrec2.setHasFixedSize(true);
        firstrec2.setLayoutManager(new LinearLayoutManager(view.getContext()));

        UserID = firebaseAuth.getCurrentUser().getUid();

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
            amount = (String) bundle6.get("amount_generated");
            Total_Enrolments = (String) bundle6.get("Total_Enrolments");
            to = (String) bundle6.getString("to");
        }

        SearchView searchView = (SearchView) getActivity().findViewById(R.id.task);
        searchView.setVisibility(View.GONE);

        back = view.findViewById(R.id.backpolicy);
        ChipNavigationBar chipNavigationBar = getActivity().findViewById(R.id.bottom_nav_menu_id);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chipNavigationBar.setItemSelected(R.id.c,true);
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
                bundle6.putString("amount_generated",amount);
                bundle6.putString("coursename",coursename);

                if(to.equals("upload")){
                    FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                    ft.add(R.id.fragment_container,new Fragments.UploadFragment());
                } else {
                    UploadedcoursesFragment frag = new UploadedcoursesFragment();
                    frag.setArguments(bundle6);
                    getFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragment_container, frag)
                            .commit();
                }
            }
        });

        firebaseFirestore.collection("HOME").document(CoId).collection("LISTITEM").document(coursename).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Double generated, pending;
                if(documentSnapshot.getDouble("amount_generated")==null) {
                    generated = 0.00;
                } else {
                    generated = documentSnapshot.getDouble("amount_generated");
                }
                if(documentSnapshot.getDouble("amount_pending")==null) {
                    pending = 0.00;
                } else {
                    pending = documentSnapshot.getDouble("amount_pending");
                }
                amount_pending.setText("Amount pending to be settled : "+String.valueOf(pending));
                amount_generated.setText("Total amount generated : "+String.valueOf(generated));
            }
        });

        Query query2 = firebaseFirestore.collection("HOME").document(CoId).collection("LISTITEM").document(coursename).collection("TRANSACTIONS").orderBy("transaction_order");

        FirestoreRecyclerOptions<TransactionModel> options1 = new FirestoreRecyclerOptions.Builder<TransactionModel>()
                .setQuery(query2,TransactionModel.class)
                .build();

        madapter2 = new FirestoreRecyclerAdapter<TransactionModel, ViewHolder>(options1) {

            @NonNull
            @Override
            public TermsandPrivacyFragment.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view2 = LayoutInflater.from(parent.getContext()).inflate(R.layout.transaction_item,parent,false);
                return new TermsandPrivacyFragment.ViewHolder(view2);
            }

            @Override
            protected void onBindViewHolder(@NonNull TermsandPrivacyFragment.ViewHolder holder, int position, @NonNull TransactionModel model) {


                    holder.amount_settled.setText("Amount settled : "+String.valueOf(model.getAmount_settled()));
                    holder.transaction_time.setText("Transaction Time : "+model.getTransaction_time());
                    holder.transaction_date.setText("Transaction Date : "+model.getTransaction_date());
                    holder.transaction_order.setText("Transaction Number : "+String.valueOf(model.getTransaction_order()));
                    holder.transaction_id.setText("Transaction ID : "+model.getTransaction_id());
                    holder.transaction_method.setText("Transaction Method : "+model.getTransaction_method());


            }

        };


        madapter2.startListening();
        madapter2.notifyDataSetChanged();
        firstrec2.setAdapter(madapter2);
        setHasOptionsMenu(true);

        return view;
    }

    private class ViewHolder extends RecyclerView.ViewHolder{

        TextView transaction_date, transaction_time, transaction_id, transaction_method, transaction_order,  amount_settled;
        CardView trasaction_card;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            transaction_date = itemView.findViewById(R.id.transaction_date);
            transaction_time = itemView.findViewById(R.id.transaction_time);
            transaction_id = itemView.findViewById(R.id.transaction_id);
            transaction_method = itemView.findViewById(R.id.transaction_method);
            transaction_order = itemView.findViewById(R.id.transaction_order);
            amount_settled = itemView.findViewById(R.id.transaction_amount);
            trasaction_card = itemView.findViewById(R.id.transaction_card);

        }
    }


}