package com.example.influencer.Core.UI;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.os.Bundle;
import android.widget.PopupMenu;

import com.example.influencer.R;
import com.example.influencer.databinding.ActivityHomeBinding;
import com.example.influencer.databinding.ActivityOnboardingBinding;

import dagger.hilt.android.AndroidEntryPoint;
import me.ibrahimsn.lib.OnItemSelectedListener;
import me.ibrahimsn.lib.SmoothBottomBar;

@AndroidEntryPoint
public class Home extends AppCompatActivity {

    private ActivityHomeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        NavController nav = Navigation.findNavController(this,  R.id.main_tab_container);
        //nos pedian hacer esto en la docu de la liberia de Smoothnavbar, osea inflar el menu en vez del Bar (como sucede en bottom navigation view)
        PopupMenu popupMenu = new PopupMenu(this, null);
        popupMenu.inflate(R.menu.bottom_nav_bar_menu);

        binding.bottomBar.setupWithNavController(popupMenu.getMenu(),nav);

    }
}