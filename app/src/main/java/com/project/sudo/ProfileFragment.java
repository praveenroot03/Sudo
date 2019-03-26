package com.project.sudo;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

    private static boolean isValidChain(ArrayList<Transaction> trans) {

        if (trans.size() != 0) {
            Transaction currentBlock;
            Transaction PreviousBlock;

            for (int i = 1; i < trans.size(); i++) {
                currentBlock = trans.get(i);
                PreviousBlock = trans.get(i - 1);
                if (!currentBlock.PrevHash.equals(PreviousBlock.Hash)) {
                    return false;
                }
            }
        }
        return true;
    }

    private static ArrayList<Transaction> convert(ArrayList<String> trans) {
        ArrayList<Transaction> check = new ArrayList<Transaction>();
        for (int i = 0; i < trans.size(); i++) {
            String t[] = trans.get(i).split("#");
            check.add(new Transaction(t[0], t[1], t[2], t[3], t[4], t[5], Long.parseLong(t[6])));
        }
        return check;
    }

    private TextView tvName;
    private TextView tvEmail;
    private ListView listview;
    private Button rstbtn, addbtn;

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
        rstbtn = view.findViewById(R.id.rstTrans);
        addbtn = view.findViewById(R.id.addOrg);

        addbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(view.getContext(), AddOrganisation.class);
                startActivity(intent);
            }
        });

        final ArrayList<String> moneyarray = new ArrayList<>();
        final ArrayList<String> namearray = new ArrayList<>();
        final ArrayList<String> timearray = new ArrayList<>();

        rstbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usercol.document(mFirebaseUser.getUid()).update("transList", null).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        rstbtn.setVisibility(View.GONE);
                    }
                });

            }
        });

        usercol.document(mFirebaseUser.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                UserDetails userDetails = task.getResult().toObject(UserDetails.class);
                tvEmail.setText(userDetails.getEmail());
                String s = "Hello " + userDetails.getUsername() + "!";
                tvName.setText(s);
                if (userDetails.getTransList() != null) {
                    ArrayList<String> arrayList = userDetails.getTransList();
                    ArrayList<Transaction> check = convert(arrayList);
                    if (isValidChain(check)) {
                        if (arrayList.size() != 0) {
                            for (int i = 1; i < arrayList.size(); i++) {
                                String arr[] = arrayList.get(i).split("#");
                                moneyarray.add(arr[5]);
                                namearray.add(arr[4]);
                                timearray.add(arr[6]);
                            }

                            if (getActivity() != null) {
                                CustomListView adapter = new CustomListView(getActivity(), moneyarray, namearray, timearray);
                                listview.setAdapter(adapter);
                            }
                        } else {
                            Toast.makeText(view.getContext(), "No Transactions Yet!", Toast.LENGTH_SHORT).show();


                        }
                    } else {
                        Toast.makeText(view.getContext(), "Something went wrong!", Toast.LENGTH_SHORT).show();
                        rstbtn.setVisibility(View.VISIBLE);
                    }
                }
            }
        });


        return view;
    }

}
