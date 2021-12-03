package Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.sreerammuthyam.abhyahas.R;

import java.util.ArrayList;
import java.util.List;


public class CoursecontentFragment extends Fragment {

    ImageView rev;
    String DocI,CollI,Vurl,CRENAME,PRIC,DESC,PDFCh,INN,CreIma,VR,enrol,back,creator,Total_Enrolments;
    private FirebaseFirestore db;
    private List<String> namelist = new ArrayList<>();
    ListView listView;
    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (savedInstanceState==null) {
            view = inflater.inflate(R.layout.fragment_coursecontent, container, false);
        }
        //View view = inflater.inflate(R.layout.fragment_coursecontent, container, false);

        db = FirebaseFirestore.getInstance();
        rev = view.findViewById(R.id.rev3);
        listView = view.findViewById(R.id.List1);

        SearchView searchView = (SearchView) getActivity().findViewById(R.id.task);
        searchView.setVisibility(View.GONE);

        Bundle bundle2 = this.getArguments();
        if(bundle2!=null){
            DocI = (String) bundle2.get("Doc");
            CollI = (String) bundle2.get("CoId");
            Vurl = (String) bundle2.get("url");
            CRENAME = (String) bundle2.get("CR");
            CreIma= (String) bundle2.get("CreIm");
            PRIC = (String) bundle2.get("PRI");
            DESC = (String) bundle2.get("DE");
            PDFCh = (String) bundle2.get("PD");
            INN = (String) bundle2.get("IN");
            VR = (String) bundle2.get("verify");
            enrol = (String) bundle2.get("enrol");
            back = (String) bundle2.get("back");
            creator = (String) bundle2.get("creator");
            Total_Enrolments = (String) bundle2.get("Total_Enrolments");
        }

        rev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bundle bundle1 = new Bundle();
                bundle1.putString("ViName",DocI);
                bundle1.putString("ViUrl",Vurl);
                bundle1.putString("CoId",CollI);
                bundle1.putString("CreNa",CRENAME);
                bundle1.putString("Desc",DESC);
                bundle1.putString("Pr",PRIC);
                bundle1.putString("PDF",PDFCh);
                bundle1.putString("IName",INN);
                bundle1.putString("CreIm",CreIma);
                bundle1.putString("verify",VR);
                bundle1.putString("enrol",enrol);
                bundle1.putString("back",back);
                bundle1.putString("creator",creator);
                bundle1.putString("Total_Enrolments",Total_Enrolments);

                PurchaseFragment frag1 = new PurchaseFragment();
                frag1.setArguments(bundle1);
                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container,frag1)
                        .commit();

            }
        });

        db.collection("HOME").document(CollI).collection("LISTITEM").document(DocI).collection("ALL VIDEOS").orderBy("Order").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                namelist.clear();

                for (DocumentSnapshot snapshot : queryDocumentSnapshots){
                    namelist.add(snapshot.getString("Video_Name"));
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(view.getContext(),android.R.layout.simple_list_item_1,namelist);
                adapter.notifyDataSetChanged();
                listView.setAdapter(adapter);
            }
        });


        return view;
    }

}