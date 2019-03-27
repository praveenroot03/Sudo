package com.project.sudo;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseUserMetadata;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

public class MainActivity extends AppCompatActivity {

    private TextView mTextMessage;
    private static final int RC_SIGN_IN = 123;
    private FirebaseAuth auth;
    //Firebase Instances

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    private CollectionReference userscol = db.collection("users");

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            switch (item.getItemId()) {
                case R.id.navigation_profile:
                    auth = FirebaseAuth.getInstance();
                    if (auth.getCurrentUser() != null) {
                        fragmentTransaction.replace(R.id.content_frame, new ProfileFragment());
                        fragmentTransaction.commit();
                    } else {
                        Toast.makeText(getApplicationContext(), "Please Sign In", Toast.LENGTH_SHORT).show();
                        startActivityForResult(
                                AuthUI.getInstance()
                                        .createSignInIntentBuilder()
                                        .setAvailableProviders(Arrays.asList(
                                                new AuthUI.IdpConfig.EmailBuilder().build()))
                                        .build(),
                                RC_SIGN_IN);

                    }
                    return true;
                case R.id.navigation_categories:
                    fragmentTransaction.replace(R.id.content_frame, new CategoriesFragment());
                    fragmentTransaction.commit();
                    return true;
                case R.id.navigation_favourites:
                    if (mFirebaseUser != null) {
                        fragmentTransaction.replace(R.id.content_frame, new FavouritesFragment());
                        fragmentTransaction.commit();
                    } else {
                        Toast.makeText(getApplicationContext(), "Signin to Bookamark!", Toast.LENGTH_SHORT).show();
                    }
                    return true;
            }
            return false;
        }


    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.setSelectedItemId(R.id.navigation_categories);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // RC_SIGN_IN is the request code you passed into startActivityForResult(...) when starting the sign in flow.
        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            // Successfully signed in
            if (resultCode == RESULT_OK) {
                FirebaseUser mcurrentUser = auth.getCurrentUser();
                FirebaseUserMetadata metadata = auth.getCurrentUser().getMetadata();
                if (metadata.getCreationTimestamp() == metadata.getLastSignInTimestamp()) {
                    UserDetails userDetails = new UserDetails(mcurrentUser.getDisplayName(), mcurrentUser.getEmail(), mcurrentUser.getUid());
                    userscol.document(mcurrentUser.getUid()).set(userDetails).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(getApplicationContext(), "User Created", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                Toast.makeText(getApplicationContext(), "Signed in" +
                        "", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            } else {
                // Sign in failed
                if (response == null) {
                    // User pressed back button
                    return;
                }

            }
        }
    }

}
