package com.example.artmarket;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.example.artmarket.R;
import com.example.artmarket.databinding.ActivityMainBinding;
import com.example.artmarket.Home;
import com.example.artmarket.profile;
import com.example.artmarket.regist;
import com.example.artmarket.plusPicture;
import com.example.artmarket.model.Image;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

    public class MainActivity extends AppCompatActivity {
        Home homeFragment = new Home();
        profile profileFragment = new profile();
        regist registFragment = new regist();
        Search searchFragment = new Search();

        private BottomNavigationView bottomNav;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            SharedPreferences sp = getSharedPreferences("PC", Context.MODE_PRIVATE);
            bottomNav = findViewById(R.id.bottomNav);
            boolean hasAccount = sp.getString("TY", "-9").equals("-9");

            if (!hasAccount) {
                bottomNav.setVisibility(View.GONE);
                replaceFragment(registFragment);
            } else {
                replaceFragment(homeFragment);
                showMainMenu();
            }

            //TextView signuptext = findViewById(R.id.signuptext);

            bottomNav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(MenuItem item) {
                    if (item.getItemId() == R.id.home) {
                        replaceFragment(homeFragment);
                    } else if (item.getItemId() == R.id.profile) {
                        replaceFragment(profileFragment);
                    } else if (item.getItemId() == R.id.search) {
                        replaceFragment(searchFragment);
                    } else {
                        replaceFragment(new plusPicture());
                    }
                    return true;
                }
            });
        }

        public void replaceFragment(Fragment fragment){
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.frame_layout, fragment);
            fragmentTransaction.commit();
        }

        public void showMainMenu() {
            bottomNav.setVisibility(View.VISIBLE);
        }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_activity_main, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        if (item.getItemId() == R.id.search) {
//            replaceFragment(new Search());
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }
}