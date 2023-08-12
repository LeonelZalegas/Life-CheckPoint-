package com.example.influencer.Data.Repositories;

import com.example.influencer.Data.LocalDB.CheckpointChooseStaticRows;
import com.example.influencer.Data.Network.UserService;
import com.example.influencer.R;
import com.example.influencer.UI.Create_Modify_Checkpoint_Menu.SharedComponents.Model.CheckpointThemeItem;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.rxjava3.core.BackpressureStrategy;
import io.reactivex.rxjava3.core.Flowable;

//la funcion en general de las clases repositorios es la de decidir que Data Source utilizar (si una BD local, firebase, api,etc) y confluir estas
//y simplemente obtener los datos para que esta manera los UseCase puedan utilizarla (estos ultimos no tienen que saber q Data Source se utiliza)
//Aca entonces utilizamos firestore para los rows variables (varian por usuario) y LocalDB para los rows staticos presentes siempre, y luego los combinamos a ambos
//https://www.notion.so/Activity-seleccionar-categoria-nuevo-checkpoint-update-checkpoint-2fe38f46f27f4e6f93752aa178796773?pvs=4#277511b4d9ae4163914db3666b4df9d3
public class CheckpointChooseRowsRepo {
    private final UserService userService;
    private final CheckpointChooseStaticRows checkpointChooseStaticRows;

    @Inject
    public CheckpointChooseRowsRepo(UserService userService,CheckpointChooseStaticRows checkpointChooseStaticRows) {
        this.userService = userService;
        this.checkpointChooseStaticRows = checkpointChooseStaticRows;
    }

    //https://www.notion.so/Activity-seleccionar-categoria-nuevo-checkpoint-update-checkpoint-2fe38f46f27f4e6f93752aa178796773?pvs=4#e0a982c6358c457a8d118f16034be32a
    private Flowable<List<CheckpointThemeItem>> getCheckpointThemesRealTime(String id) {
        return Flowable.create(emitter -> {
            ListenerRegistration registration = userService.getusuarioRealTime(id).addSnapshotListener((snapshot, error) -> {
                if (error != null || snapshot == null) {
                    emitter.onError(new Exception(error));
                    return;
                }

                List<CheckpointThemeItem> firestoreRows = new ArrayList<>();
                if (snapshot.contains("CheckpointThemesNames")) {
                    List<String> checkpointThemes = (List<String>) snapshot.get("CheckpointThemesNames");
                    for (String theme : checkpointThemes) {
                        firestoreRows.add(new CheckpointThemeItem(R.color.gris, R.drawable.vector_asset_better_flag, theme));
                    }
                }
                emitter.onNext(firestoreRows);
            });

            emitter.setCancellable(registration::remove);
        }, BackpressureStrategy.LATEST);
    }

    public Flowable<List<CheckpointThemeItem>> getAllCheckpointThemes(String id) {
        return getCheckpointThemesRealTime(id)
                .map(firestoreRows -> {
                    List<CheckpointThemeItem> combinedRows = new ArrayList<>(checkpointChooseStaticRows.fetchStaticRows());
                    combinedRows.addAll(firestoreRows);
                    return combinedRows;
                });
    }

}
