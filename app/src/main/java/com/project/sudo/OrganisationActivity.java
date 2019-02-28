package com.project.sudo;

import android.os.Bundle;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.content.Intent;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;


public class OrganisationActivity extends AppCompatActivity {

    private ImageView imageView;
    private Toolbar toolbar;
    private TextView desc_tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organisation);
        toolbar = findViewById(R.id.toolbar);

        imageView = findViewById(R.id.iv_header);
        desc_tv = findViewById(R.id.desc_tv);
        final FirebaseAuth mauth = FirebaseAuth.getInstance();

        Serializable serializable = getIntent().getSerializableExtra("orgInfo");
        final Organisation org = (Organisation)serializable;

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FirebaseUser mcurrentUser = mauth.getCurrentUser();
                if(mcurrentUser!=null) {
                    Intent intent = new Intent(getApplicationContext(), PaymentAmount.class);
                    intent.putExtra("orgName", org.getName());
                    startActivity(intent);
                }
                else{
                    Toast.makeText(getApplicationContext(),"Create Account to Donate!",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                }
            }
        });

        toolbar.setTitle(org.getName());
        setSupportActionBar(toolbar);

        String desc = org.getTagline() + "\n\n"
                + org.getDesc() + "\n\n"
                + org.getWebsite() + "\n\n"
                + "Contact Info," + "\n\n"
                + org.getEmail() + "\n\n"
                + org.getPhnum();
        desc_tv.setText(desc);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
