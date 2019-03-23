package com.project.sudo;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.Serializable;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


public class OrganisationActivity extends AppCompatActivity {

    private ImageView imageView;
    private Toolbar toolbar;
    private TextView desc_tv, tvWeb, tvEmail, tvPhone;
    private FloatingActionButton btnWeb, btnEmail, btnPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organisation);
        toolbar = findViewById(R.id.toolbar);

        imageView = findViewById(R.id.iv_header);
        desc_tv = findViewById(R.id.desc_tv);
        tvWeb = findViewById(R.id.tv_website);
        tvEmail = findViewById(R.id.tv_email);
        tvPhone = findViewById(R.id.tv_phone);
        btnEmail = findViewById(R.id.btnEmail);
        btnPhone = findViewById(R.id.btnPhone);
        btnWeb = findViewById(R.id.btnWeb);
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
        Glide.with(getApplicationContext()).load(org.getPhotourl()).into(imageView);

        String desc = org.getTagline() + "\n\n" + org.getDesc() + "\n\n";
        desc_tv.setText(desc);
        tvWeb.setText(org.getWebsite());
        tvEmail.setText(org.getEmail());
        tvPhone.setText(org.getPhnum());

        btnPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + tvPhone.getText().toString()));
                if (intent.resolveActivity(getPackageManager()) != null) startActivity(intent);
            }
        });

        btnEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailIds[] = new String[1];
                emailIds[0] = tvEmail.getText().toString();
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:"));
                intent.putExtra(Intent.EXTRA_EMAIL, emailIds);
                if (intent.resolveActivity(getPackageManager()) != null) startActivity(intent);
            }
        });

        btnWeb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri webpage = Uri.parse(tvWeb.getText().toString());
                Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
                if (intent.resolveActivity(getPackageManager()) != null) startActivity(intent);
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
