package com.project.sudo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

public class ProfileFragment extends Fragment {

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private TextView tvName;
    private TextView tvEmail;
    private ListView listview;

    //Firebase Instances
    private FirebaseUser mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference usercol = db.collection("users");
    private CollectionReference orgcol = db.collection("Organisation");

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_profile, container, false);

        tvEmail = view.findViewById(R.id.tvEmail);
        tvName = view.findViewById(R.id.tvName);
        listview = view.findViewById(R.id.listView);

        final ArrayList<String> moneyarray = new ArrayList<>();
        final ArrayList<String> namearray = new ArrayList<>();
        final ArrayList<String> timearray = new ArrayList<>();

        usercol.document(mFirebaseUser.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                UserDetails userDetails = task.getResult().toObject(UserDetails.class);
                tvEmail.setText(userDetails.getEmail());
                tvName.setText(userDetails.getUsername());
                if (userDetails.getTransList() != null) {
                    ArrayList<String> arrayList = userDetails.getTransList();

                    for (int i = 1; i < arrayList.size(); i++) {
                        String arr[] = arrayList.get(i).split("#");
                        moneyarray.add(arr[5]);
                        namearray.add(arr[4]);
                        timearray.add(arr[6]);
                    }

                    CustomListView adapter = new CustomListView(getActivity(), moneyarray, namearray, timearray);
                    listview.setAdapter(adapter);
                }
            }
        });


        return view;
    }

}
