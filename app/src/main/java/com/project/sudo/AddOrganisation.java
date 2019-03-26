package com.project.sudo;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.net.URL;
import java.util.Random;
import java.util.regex.Pattern;

import androidx.appcompat.app.AppCompatActivity;

public class AddOrganisation extends AppCompatActivity {

    private EditText orgName, orgTagline, orgDesc, orgWebsite, orgPhnum, orgEmail, orgPhotourl;
    private Button addOrgbtn;
    private Spinner orgType;

    //Firebase Instances
    private FirebaseUser mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference usercol = db.collection("users");
    private CollectionReference orgcol = db.collection("Organisation");

    public static void displayToast(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    public static boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\." +
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";

        Pattern pat = Pattern.compile(emailRegex);
        if (email == null)
            return false;
        return pat.matcher(email).matches();
    }

    public static boolean isValidURL(String url) {
        /* Try creating a valid URL */
        try {
            new URL(url).toURI();
            return true;
        }

        // If there was an Exception
        // while creating URL object
        catch (Exception e) {
            return false;
        }
    }

    public static boolean isValidPhnum(String str) {

        if (str.matches("^[0-9]{10}$")) {
            return true;
        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_organisation);
        orgDesc = findViewById(R.id.orgDesc);
        orgName = findViewById(R.id.orgName);
        orgTagline = findViewById(R.id.orgTagline);
        orgWebsite = findViewById(R.id.orgWebsite);
        orgPhnum = findViewById(R.id.orgPhnum);
        orgPhotourl = findViewById(R.id.orgPhotourl);
        orgEmail = findViewById(R.id.orgEmailID);
        //Button
        addOrgbtn = findViewById(R.id.orgAdd);
        //Spinner
        orgType = findViewById(R.id.orgType);

        //create a list of items for the spinner.
        String[] items = new String[]{"Animal", "Environment", "Education", "Humanitarian"};
        //create an adapter to describe how the items are displayed, adapters are used in several places in android.
        //There are multiple variations of this, but this is the basic variant.
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        //set the spinners adapter to the previously created one.
        orgType.setAdapter(adapter);


        orgPhotourl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // initialize a Random object somewhere; you should only need one
                Random random = new Random();
                int x = random.nextInt(3);
                String animal[] = new String[]{"130", "937", "659"};
                String education[] = new String[]{"180", "367", "534"};
                String humanitarian[] = new String[]{"103", "129", "513"};
                String environment[] = new String[]{"112", "127", "480"};

                String index = "";
                if (orgType.getSelectedItem().toString().equalsIgnoreCase("animal")) {
                    index = animal[x];
                }
                if (orgType.getSelectedItem().toString().equalsIgnoreCase("education")) {
                    index = education[x];
                }
                if (orgType.getSelectedItem().toString().equalsIgnoreCase("environment")) {
                    index = environment[x];
                }
                if (orgType.getSelectedItem().toString().equalsIgnoreCase("humanitarian")) {
                    index = humanitarian[x];
                }

                // generate a random integer from 0 to 899, then add 100
                orgPhotourl.setText("https://picsum.photos/200/300?image=" + index);
            }
        });

        addOrgbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!isValidEmail(orgEmail.getText().toString())) {
                    displayToast(getApplicationContext(), "Invalid Email ID");
                } else if (!isValidPhnum(orgPhnum.getText().toString())) {
                    displayToast(getApplicationContext(), "Invalid Phnum");
                } else if (!isValidURL(orgWebsite.getText().toString())) {
                    displayToast(getApplicationContext(), "Invalid Web Address");
                } else {

                    Organisation organisation = new Organisation(orgName.getText().toString(), orgTagline.getText().toString(), orgDesc.getText().toString(), orgWebsite.getText().toString(), orgPhnum.getText().toString(), orgEmail.getText().toString(), orgPhotourl.getText().toString(), orgType.getSelectedItem().toString());

                    orgcol.document(organisation.getTagline()).set(organisation).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(getApplicationContext(), "Organisation Successfully Added!", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });

                }
            }
        });
    }

}
