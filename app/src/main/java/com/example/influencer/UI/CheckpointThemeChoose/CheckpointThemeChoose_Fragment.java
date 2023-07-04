package com.example.influencer.UI.CheckpointThemeChoose;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import com.example.influencer.R;
import com.example.influencer.databinding.FragmentCheckpointThemeChooseBinding;

import java.util.List;

import kotlin.collections.CollectionsKt;

public class CheckpointThemeChoose_Fragment extends Fragment {

    private FragmentCheckpointThemeChooseBinding binding;
    private CheckpointThemeChooseViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentCheckpointThemeChooseBinding.inflate(inflater,container,false);

        // Retrieve arguments
        String mainTitle = getArguments().getString("MainTitle", "something somehow isn't working");
        String secondaryTitle = getArguments().getString("SecondaryTitle", "something somehow isn't working");

        // Update the titles
        binding.MainTitle.setText(mainTitle);
        binding.SecondaryTitle.setText(secondaryTitle);


        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(CheckpointThemeChooseViewModel.class);

        GridLayoutManager layoutManager = new GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false);
        binding.checkpointThemeRV.setLayoutManager(layoutManager);

        // Retrieve arguments
        boolean showAddNewRow = getArguments().getBoolean("showAddNewRow", true);

        //observer para que se vea reflejado cada vez que se actualize la lista de las categorias de checkpooint
        viewModel.getUserCheckpointsThemes().observe(getViewLifecycleOwner(),notFilteredRowItems -> {

            //lo que hacemos arriva es obtener notFilteredRowItems a traves del metodo del viewmodel y esa seria tipo la lista actualizada en cuestion de todos los items de los checkpoint (Creadas x usuario y las que no)
            //luego abajo con el if, a esa notFilteredRowItems le vamos a qutiar el primer item/row que es el de agregar un nuevo item dependiendo del valor booleano del bundle que pasamos desde las 2 activities y la colocamos en el adapter a esa nueva lista filtrada

            List<CheckpointThemeItem> rowItems;
            if (showAddNewRow){
                rowItems = notFilteredRowItems;
            }else{
                rowItems = CollectionsKt.filter(notFilteredRowItems, checkpointThemeItem -> !checkpointThemeItem.getText().equals("Create Custom")); //"Create Custom" es el nombre del string del row de "agregar nuevo item/row" q sale en la seccion de Strings del proyecto
            }
            CheckpointThemeChooseAdapter adapter = new CheckpointThemeChooseAdapter(rowItems);
            binding.checkpointThemeRV.setAdapter(adapter);

            //esto es para el filtrado por busqueda del usuario
            binding.CategoryCheckpointSearch.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    // Not needed
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String userFilter) {
                    List<CheckpointThemeItem> RowItemsFiltered = CollectionsKt.filter(rowItems, checkpointThemeItem -> checkpointThemeItem.getText().toLowerCase().contains(userFilter.toLowerCase()));
                    adapter.FilterItems(RowItemsFiltered);
                    return true;
                }
            });
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}