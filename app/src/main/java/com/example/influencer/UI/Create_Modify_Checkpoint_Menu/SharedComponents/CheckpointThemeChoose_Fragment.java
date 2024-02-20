package com.example.influencer.UI.Create_Modify_Checkpoint_Menu.SharedComponents;

import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModelProvider;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.influencer.R;
import com.example.influencer.UI.Create_Modify_Checkpoint_Menu.SharedComponents.Model.CheckpointThemeItem;
import com.example.influencer.UI.Upload_New_Checkpoint.UploadNewCheckpoint;
import com.example.influencer.UI.Upload_New_Update_Checkpoint.Upload_New_Update_CheckpointActivity;
import com.example.influencer.databinding.AlertDialogAddCategoryBinding;
import com.example.influencer.databinding.FragmentCheckpointThemeChooseBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import kotlin.collections.CollectionsKt;

@AndroidEntryPoint
public class CheckpointThemeChoose_Fragment extends Fragment {

    private FragmentCheckpointThemeChooseBinding binding;
    private CheckpointThemeChooseViewModel viewModel;
    @Inject
    CheckpointThemeChooseAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentCheckpointThemeChooseBinding.inflate(inflater,container,false);

        // Retrieve arguments
        String mainTitle = getArguments().getString("MainTitle", getContext().getString(R.string.Generic_error));
        String secondaryTitle = getArguments().getString("SecondaryTitle",  getContext().getString(R.string.Generic_error));

        // Update the titles
        binding.MainTitle.setText(mainTitle);
        binding.SecondaryTitle.setText(secondaryTitle);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(CheckpointThemeChooseViewModel.class);

        setupRecyclerView();
        setupToastMessageObserver();
        setupUserCheckpointsThemesObserver();
        setupLoadingObserver();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void setupRecyclerView() {
        GridLayoutManager layoutManager = new GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false);
        binding.checkpointThemeRV.setLayoutManager(layoutManager);
        binding.checkpointThemeRV.setAdapter(adapter);
    }

    private void setupToastMessageObserver() {
        //para mostrar toast de error de agregado de nuevo checkpoint del usuario
        viewModel.getToastMessage().observe(getViewLifecycleOwner(), message -> {
            if (message != null) {
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupLoadingObserver() {
        viewModel.isLoading.observe(getViewLifecycleOwner(),it ->{
            if (it) {
                binding.progress.setVisibility(View.VISIBLE);
            } else {
                binding.progress.setVisibility(View.GONE);
            }
        });
    }


    private void setupUserCheckpointsThemesObserver() {
        // Retrieve arguments del valor booleano de si se incluye o no el "Custom Checkpoint"
        boolean showAddNewRow = getArguments().getBoolean("showAddNewRow", true);

        //observer para que se vea reflejado cada vez que se actualize la lista de las categorias de checkpooint
        viewModel.getUserCheckpointsThemes().observe(getViewLifecycleOwner(),notFilteredRowItems -> {
            viewModel.fetchUserPostCategories().observe(getViewLifecycleOwner(), userCategories -> {

                   //lo que hacemos arriva es obtener notFilteredRowItems a traves del metodo del viewmodel y esa seria tipo la lista actualizada en cuestion de todos los items de los checkpoint (Creadas x usuario y las que no)
                   //luego abajo con el if, a esa notFilteredRowItems le vamos a qutiar el primer item/row que es el de agregar un nuevo item dependiendo del valor booleano del bundle que pasamos desde las 2 activities y la colocamos en el adapter a esa nueva lista filtrada
                   //el 2do obserber (el anidado) basicmente observa las categorias de los post del usuario q tiene actualmente

                    List<CheckpointThemeItem> rowItems;
                    if (showAddNewRow){
                      rowItems = notFilteredRowItems;
                    }else{
                         //"Create Custom" es el nombre del string del row de "agregar nuevo item/row" q sale en la seccion de Strings del proyecto
                       rowItems = CollectionsKt.filter(notFilteredRowItems, item -> userCategories.contains(item.getText()));
                    }

                   //https://www.notion.so/Activity-seleccionar-categoria-nuevo-checkpoint-update-checkpoint-2fe38f46f27f4e6f93752aa178796773?pvs=4#8a685d43c4114530aaa5b551ff209690
                   //es para la ventana de agregar el nuevo nombre de categoria
                adapter.UpdateRows(rowItems,item -> {

                    if (showAddNewRow) {
                        if (item.getText().equals("Create Custom")) {
                          showDialogAndSaveToFireStore();
                       } else {
                          Intent intent = new Intent(getActivity(), UploadNewCheckpoint.class);
                          intent.putExtra("SELECTED_CATEGORY", item);
                          startActivity(intent);
                       }
                    }else{
                        Intent intent = new Intent(getActivity(), Upload_New_Update_CheckpointActivity.class);
                        intent.putExtra("SELECTED_CATEGORY", item);
                        startActivity(intent);
                    }
                });
               setupSearchView(rowItems);
            });
        });
    }

    private void setupSearchView(List<CheckpointThemeItem> rowItems) {
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
                adapter.UpdateRows(RowItemsFiltered);
                return true;
            }
        });
    }

    private void showDialogAndSaveToFireStore() {
        AlertDialogAddCategoryBinding binding = AlertDialogAddCategoryBinding.inflate(LayoutInflater.from(getActivity()));

        AlertDialog alertDialog = new MaterialAlertDialogBuilder(getActivity(),R.style.CustomAlertDialog)
                .setTitle(getString(R.string.Main_title_new_category))
                .setView(binding.getRoot())
                .setPositiveButton(getString(R.string.OK), null)         //se agrega un null como 2do parametro para denotar que vamos a customizar q ahcer cuando se clickea el OK
                .setNegativeButton(getString(R.string.Close), new DialogInterface.OnClickListener() {   //aca como no se customiza nada, se toma todo x default, en donde independientemente de si se indica que se cierre la alert dialog o no al clickear, se va a cerrar igual si clickemaos en CLOSE
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).create();

//https://www.notion.so/Activity-seleccionar-categoria-nuevo-checkpoint-update-checkpoint-2fe38f46f27f4e6f93752aa178796773?pvs=4#afa893ed79914f338b59e13778a51d8c
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button button = ((AlertDialog) dialogInterface).getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (viewModel.validatingNewThemeCheckpoint(binding.editTextNewTheme)) {
                            viewModel.addCheckpointThemeName(binding.editTextNewTheme.getText().toString());
                            dialogInterface.dismiss(); //dismiss dialog when condition is met
                        }
                    }
                });
            }
        });
        alertDialog.show();
    }
}