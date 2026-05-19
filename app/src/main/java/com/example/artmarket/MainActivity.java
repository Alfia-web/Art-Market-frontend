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
import android.widget.Toast;

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
            String savedTY = sp.getString("TY", "-9");
            Toast.makeText(this, "TY = " + savedTY, Toast.LENGTH_LONG).show(); // ← сюда

            bottomNav = findViewById(R.id.bottomNav);
            boolean isLoggedIn = !savedTY.equals("-9");
            Toolbar toolbar = findViewById(R.id.toolbar_actionbar);
            if (!isLoggedIn) {
                bottomNav.setVisibility(View.GONE);
                replaceFragment(registFragment);
            } else {
                replaceFragment(homeFragment);
                showMainMenu();
            }

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

            toolbar.setOnMenuItemClickListener(item->{
                if (item.getItemId()==R.id.out){
                    sp.edit().clear().apply();
                    replaceFragment(new regist());
                    bottomNav.setVisibility(View.GONE);
                    toolbar.setVisibility(View.GONE);
                    return true;
                }
                return false;
            });
        }

        public void replaceFragment(Fragment fragment){
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.frame_layout, fragment);
            fragmentTransaction.commit();
        }

        public void showMainMenu() {
            Toolbar toolbar = findViewById(R.id.toolbar_actionbar);
            bottomNav.setVisibility(View.VISIBLE);
            toolbar.setVisibility(View.VISIBLE);
        }

}