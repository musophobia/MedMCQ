package com.example.sakib.myapplication;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.sakib.myapplication.models.ExamHistory;
import com.example.sakib.myapplication.models.QuestionClient;
import com.example.sakib.myapplication.models.Users;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private CardView cardView_medi;
    private CardView cardView_varsity;
    private CardView cardView_notice;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setBackgroundDrawable(
                new ColorDrawable(Color.parseColor("#5e9c00")));

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        UserDataSendToServer();


        cardView_medi = (CardView) findViewById(R.id.card_view1);
        cardView_varsity = (CardView) findViewById(R.id.card_view2);
        cardView_notice = (CardView) findViewById(R.id.card_view3);

        cardView_medi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                launchActivity_medi();
            }
        });

        cardView_varsity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                launchActivity_varsity();
            }
        });

        cardView_notice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                launchActivity_notice();
            }
        });


    }
    private void UserDataSendToServer(){
        FirebaseAuth mAuth;
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        Toast.makeText(MainActivity.this, user.getDisplayName(), Toast.LENGTH_SHORT).show();
        System.out.println("FireBase theke ja pai"+user.getIdToken(true)+user.getPhoneNumber()+user.getUid()+user.getEmail());

        Users usertoSend= new Users();
        usertoSend.setEmail(user.getEmail());
        usertoSend.setProviderID(user.getProviderId());
        usertoSend.setName(user.getDisplayName());
        usertoSend.setUserID(user.getUid());


        Retrofit.Builder builder2 = new Retrofit.Builder()
                .baseUrl("http://missiondmc.ml/")
                //    .baseUrl("https://api.github.com/")
                .addConverterFactory(GsonConverterFactory.create());

        Retrofit retrofit2 = builder2.build();
        QuestionClient eClient = retrofit2.create(QuestionClient.class);
        Call<Users> call= eClient.post_users(usertoSend);

        call.enqueue(new Callback<Users>() {
            @Override
            public void onResponse(Call<Users> call, Response<Users> response) {
                Toast.makeText(MainActivity.this, "yes! :)", Toast.LENGTH_SHORT).show();
                System.out.print("FUFU"+ response.body());
            }

            @Override
            public void onFailure(Call<Users> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Already Exists", Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void launchActivity_medi() {
        Intent intent = new Intent(this, MedicalMainActivity.class);
        startActivity(intent);
    }

    private void launchActivity_varsity() {
        Intent intent = new Intent(this, VarsityActivity.class);
        startActivity(intent);
    }

    private void launchActivity_notice() {
        Intent intent = new Intent(this, NoticeActivity.class);
        startActivity(intent);
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.fook_notice) {
            // Handle the camera action
        } else if (id == R.id.goga) {

        } else if (id == R.id.recharge) {

            Intent intent = new Intent(MainActivity.this, PaymentActivity.class);
            startActivity(intent);

        } else if (id == R.id.home_page) {

        } else if (id == R.id.hog_profile) {

            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
            startActivity(intent);

        } else if (id == R.id.logout_shit) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
