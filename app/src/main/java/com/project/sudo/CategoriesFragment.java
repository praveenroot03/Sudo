package com.project.sudo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.SearchView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class CategoriesFragment extends Fragment {

    public CategoriesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private CardView card_all, card_animals, card_enivironment, card_humanitarian, card_education;
    private List<Organisation> orgList;
    private OrgAdapter orgAdapter;
    private RecyclerView recyclerView;
    private View gap;
    private ScrollView scrollView;
    private SearchView searchView;


    //Firebase Instances
    private FirebaseUser mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference usercol = db.collection("users");
    private CollectionReference orgcol = db.collection("Organisation");


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_categories, container, false);

        card_all = view.findViewById(R.id.card_all);
        card_animals = view.findViewById(R.id.card_animals);
        card_education = view.findViewById(R.id.card_education);
        card_enivironment = view.findViewById(R.id.card_environment);
        card_humanitarian = view.findViewById(R.id.card_humanitarian);
        scrollView = view.findViewById(R.id.scrollview);

        orgList = new ArrayList<>();

        recyclerView = view.findViewById(R.id.recycler_view);
//        gap = view.findViewById(R.id.gap);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        searchView = view.findViewById(R.id.searchview);


        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                orgAdapter = new OrgAdapter(view.getContext(), orgList);
                recyclerView.setAdapter(orgAdapter);
                return false;
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                List<Organisation> tempList = new ArrayList<>();
                for (Organisation org : orgList) {
                    tempList.add(org);
                }

                for (int i = 0; i < tempList.size(); i++) {
                    if (!tempList.get(i).toString().toLowerCase().contains(query.toLowerCase())) {
                        tempList.remove(i);
                        i--;
                    }
                }
                orgAdapter = new OrgAdapter(view.getContext(), tempList);
                recyclerView.setAdapter(orgAdapter);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                return false;

            }
        });

        card_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                initView();
                orgcol.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        for(DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()){
                            Organisation org = doc.toObject(Organisation.class);
                            orgList.add(org);
                        }
                        integrateBM(view);

                    }
                });
            }

        });

        card_animals.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initView();
                orgcol.whereEqualTo("type","Animal").addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                        for(DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()){
                            Organisation org = doc.toObject(Organisation.class);
                            orgList.add(org);
                        }
                        integrateBM(view);

                    }
                });

            }
        });

        card_education.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initView();
                orgcol.whereEqualTo("type","Education").addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                        for(DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()){
                            Organisation org = doc.toObject(Organisation.class);
                            orgList.add(org);
                        }
                        integrateBM(view);

                    }
                });
            }
        });

        card_enivironment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initView();
                orgcol.whereEqualTo("type","Environment").addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                        for(DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()){
                            Organisation org = doc.toObject(Organisation.class);
                            orgList.add(org);
                        }
                        integrateBM(view);

                    }
                });
            }
        });

        card_humanitarian.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                initView();

//                gap.setVisibility(View.VISIBLE);
                orgcol.whereEqualTo("type","Humanitarian").addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                        for(DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()){
                            Organisation org = doc.toObject(Organisation.class);
                            orgList.add(org);
                        }

                        integrateBM(view);

                    }
                });
            }
        });


        return view;
    }

    private void integrateBM(final View view) {
        usercol.document(mFirebaseUser.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                UserDetails userDetails = task.getResult().toObject(UserDetails.class);

                for (int i = 0; i < orgList.size(); i++) {
                    if (userDetails.getBookmarkIds().contains(orgList.get(i).getTagline())) {
                        orgList.get(i).setBookmarked(true);
                    }
                }
                orgAdapter = new OrgAdapter(view.getContext(), orgList);
                recyclerView.setAdapter(orgAdapter);
            }
        });
    }

    private void initView() {
        searchView.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.VISIBLE);
        scrollView.setVisibility(View.GONE);
        recyclerView.setItemViewCacheSize(10);
        orgList.clear();
    }
}
