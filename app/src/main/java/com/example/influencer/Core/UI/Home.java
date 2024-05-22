package com.example.influencer.Core.UI;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.PopupMenu;

import com.example.influencer.R;
import com.example.influencer.databinding.ActivityHomeBinding;
import com.example.influencer.databinding.ActivityOnboardingBinding;

import dagger.hilt.android.AndroidEntryPoint;
import me.ibrahimsn.lib.OnItemSelectedListener;
import me.ibrahimsn.lib.SmoothBottomBar;

//@AndroidEntryPoint
//public class Home extends AppCompatActivity {
//
//    private ActivityHomeBinding binding;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        binding = ActivityHomeBinding.inflate(getLayoutInflater());
//        setContentView(binding.getRoot());
//
//        NavController nav = Navigation.findNavController(this,  R.id.main_tab_container);
//        //nos pedian hacer esto en la docu de la liberia de Smoothnavbar, osea inflar el menu en vez del Bar (como sucede en bottom navigation view)
//        PopupMenu popupMenu = new PopupMenu(this, null);
//        popupMenu.inflate(R.menu.bottom_nav_bar_menu);
//
//        binding.bottomBar.setupWithNavController(popupMenu.getMenu(),nav);
//
//        // navigating to ProfileTabFragment with a userId
////        String userId = null; // This is null for the current user
////        Bundle bundle = new Bundle();
////        bundle.putString("userId", userId);
////        nav.navigate(R.id.profileTabFragment, bundle);
//
////        binding.bottomBar.setOnItemSelectedListener((OnItemSelectedListener) item -> {
////            if (item.getItemId() == R.id.profileTabFragment) {
////                String userId = null; // This is null for the current user
////                Bundle bundle = new Bundle();
////                bundle.putString("userId", userId);
////                nav.navigate(R.id.profileTabFragment, bundle);
////                return true;
////            }
////            return false;
////        });
//
//
//    }
//}

@AndroidEntryPoint
public class Home extends AppCompatActivity {

    private ActivityHomeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        NavController navController = Navigation.findNavController(this, R.id.main_tab_container);
        setupSmoothBottomMenu(navController);
    }

    private void setupSmoothBottomMenu(NavController navController) {
        PopupMenu popupMenu = new PopupMenu(this, null);
        popupMenu.inflate(R.menu.bottom_nav_bar_menu);

        binding.bottomBar.setupWithNavController(popupMenu.getMenu(), navController);

        binding.bottomBar.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public boolean onItemSelect(int position) {
                MenuItem item = popupMenu.getMenu().getItem(position);
                if (item.getItemId() == R.id.profileTabFragment) {
                    String userId = null; // This is null for the current user
                    Bundle bundle = new Bundle();
                    bundle.putString("userId", userId);
                    navController.navigate(R.id.profileTabFragment, bundle);
                    return true;
                } else {
                    // Let the SmoothBottomBar handle other navigations
                    NavigationUI.onNavDestinationSelected(item, navController);
                    return true;
                }
            }
        });
    }
}
