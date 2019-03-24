package com.project.sudo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class FavouritesFragment extends Fragment {

    public FavouritesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    private RecyclerView recyclerView;
    private SearchView searchView;
    private List<Organisation> orgList;
    private OrgAdapter orgAdapter;


    //Firebase Instances
    private FirebaseUser mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference usercol = db.collection("users");
    private CollectionReference orgcol = db.collection("Organisation");

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        final View view = inflater.inflate(R.layout.fragment_favourites, container, false);

        recyclerView = view.findViewById(R.id.recyclerview);
        searchView = view.findViewById(R.id.searchview);
        orgList = new ArrayList<>();


        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

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

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                orgAdapter = new OrgAdapter(view.getContext(), orgList);
                recyclerView.setAdapter(orgAdapter);
                return false;
            }
        });

        usercol.document(mFirebaseUser.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                final UserDetails userDetails = documentSnapshot.toObject(UserDetails.class);

                if (userDetails.getBookmarkIds() != null) {
                    for (int i = 0; i < userDetails.getBookmarkIds().size(); i++) {
                        orgcol.document(userDetails.getBookmarkIds().get(i)).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                Organisation organisation = task.getResult().toObject(Organisation.class);
                                organisation.setBookmarked(true);
                                orgList.add(organisation);

                                while (orgList.size() == userDetails.getBookmarkIds().size()) {
                                    integrateBM(view);
                                    break;
                                }

                            }
                        });
                    }
                }
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

}
