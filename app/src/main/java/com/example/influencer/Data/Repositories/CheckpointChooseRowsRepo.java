package com.example.influencer.Data.Repositories;

import com.example.influencer.Data.LocalDB.CheckpointChooseStaticRows;
import com.example.influencer.Data.Network.UserService;
import com.example.influencer.R;
import com.example.influencer.UI.CheckpointThemeChoose.CheckpointThemeItem;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.core.BackpressureStrategy;
import io.reactivex.rxjava3.core.Flowable;

public class CheckpointChooseRowsRepo {
    private final UserService userService;
    private final CheckpointChooseStaticRows checkpointChooseStaticRows = new CheckpointChooseStaticRows();

    public CheckpointChooseRowsRepo(UserService userService) {
        this.userService = userService;
    }

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
