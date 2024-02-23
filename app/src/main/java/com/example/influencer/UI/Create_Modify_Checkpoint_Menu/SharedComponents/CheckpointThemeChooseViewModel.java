package com.example.influencer.UI.Create_Modify_Checkpoint_Menu.SharedComponents;


import android.content.res.Resources;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.LiveDataReactiveStreams;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.influencer.Core.SingleLiveEvent;
import com.example.influencer.Data.Repositories.PostRepository.FirestorePostRepository;
import com.example.influencer.Domain.UserCheckpointThemeChooseUseCase;
import com.example.influencer.Domain.Validations.NewCheckpointThemeValidation;
import com.example.influencer.R;
import com.example.influencer.UI.Create_Modify_Checkpoint_Menu.SharedComponents.Model.CheckpointThemeItem;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class CheckpointThemeChooseViewModel extends ViewModel {

    private final MediatorLiveData<List<CheckpointThemeItem>> checkpointThemesMediator = new MediatorLiveData<>();
    private UserCheckpointThemeChooseUseCase userCheckpointThemeChooseUseCase;
    private final Resources resources;
    private  final NewCheckpointThemeValidation newCheckpointThemeValidation;
    private final FirestorePostRepository firestorePostRepository;
    private MutableLiveData<List<String>> categoriesLiveData = new MutableLiveData<>();

    @Inject
    CheckpointThemeChooseViewModel(UserCheckpointThemeChooseUseCase userCheckpointThemeChooseUseCase,
                                   Resources resources,
                                   NewCheckpointThemeValidation newCheckpointThemeValidation,
                                   FirestorePostRepository firestorePostRepository){
        this.userCheckpointThemeChooseUseCase = userCheckpointThemeChooseUseCase;
        this.resources = resources;
        this.newCheckpointThemeValidation = newCheckpointThemeValidation;
        this.firestorePostRepository = firestorePostRepository;
    }

    //https://www.notion.so/Activity-seleccionar-categoria-nuevo-checkpoint-update-checkpoint-2fe38f46f27f4e6f93752aa178796773?pvs=4#4fdf8783463a4983af9d292ab5ebf3ee
    private SingleLiveEvent<String> toastMessage = new SingleLiveEvent<>();

    public LiveData<List<CheckpointThemeItem>> getUserCheckpointsThemes() {
        //aca estaria bueno controlar si devuelve null (que el id del usuario no existe) pero bue xd
        LiveData<List<CheckpointThemeItem>> checkpointThemes = LiveDataReactiveStreams.fromPublisher(userCheckpointThemeChooseUseCase.getUserCheckpointsThemes());
        //usamos esto del Mediator porque LiveDataReactiveStreams es una operacion asincrona, por ende no podemos llamar a isLoading, porq sino el isLoading no esperaria a q se traiga la info 1ero para ponerse en false
        //por ende el mediator observa ctemente a lo que se ya se observa ctemente con LiveDataReactiveStreams, pero espera a q 1ero se obtenga esa info (espera info de LiveDataReactiveStreams y recien ahi se setea con esa info el mismo) para mostrar el false, y luego devolvemos el mediator
        //que seria lo mismo que devolver checkpointThemes, x que una observa al otro constantemente
        checkpointThemesMediator.addSource(checkpointThemes, items -> {
            checkpointThemesMediator.setValue(items);
        });
        return checkpointThemesMediator;
    }

    public boolean validatingNewThemeCheckpoint(TextInputEditText editText){
        return newCheckpointThemeValidation.invokeNewCheckpointThemeValidation(editText);
    }

    //https://www.notion.so/Life-Checkpoint-37dccb9dbb464169b4c9a42460f50f40?pvs=4#c843682676ae4d298b72adbd31b745ac
    public void addCheckpointThemeName(String checkpointThemeName) {
        Task<Void> task = userCheckpointThemeChooseUseCase.addCheckpointTheme(checkpointThemeName);
        if (task != null) {
            task.addOnFailureListener(e -> toastMessage.setValue(resources.getString(R.string.FireStore_Error)));
        }
    }

    public SingleLiveEvent<String> getToastMessage() {
        return toastMessage;
    }

    //https://www.notion.so/Upload-Checkpoint-1c875423235f4180a588c8453a7140e3?pvs=4#77ae0184119544a59e2374b179122ade
   public LiveData<List<String>> fetchUserPostCategories() {
       firestorePostRepository.getUserPostCategoriesFuture().thenAccept(categoriesLiveData::postValue);
       return categoriesLiveData;
   }
}