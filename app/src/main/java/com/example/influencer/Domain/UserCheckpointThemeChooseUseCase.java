package com.example.influencer.Domain;

import com.example.influencer.Data.Network.AuthenticationService;
import com.example.influencer.Data.Network.FirebaseClient;
import com.example.influencer.Data.Network.UserService;
import com.example.influencer.R;
import com.example.influencer.UI.CheckpointThemeChoose.CheckpointThemeItem;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//esta clase sirve para obtener tanto los rows que son fijos(estaticos) como los rows personales que ha guardado cada usuario (en firestore)

public class UserCheckpointThemeChooseUseCase {

   Context context;
   String[] CheckpointChooseNames;
   private final List<CheckpointThemeItem> STATIC_ROWS;
   private UserService userService;

    public UserCheckpointThemeChooseUseCase(Context context) {
        this.context = context;
        FirebaseClient firebaseClient = FirebaseClient.getInstance();
        this.userService = new UserService(firebaseClient);

        CheckpointChooseNames = context.getResources().getStringArray(R.array.names_checkpoint_choose);
        STATIC_ROWS = Arrays.asList(
                new CheckpointThemeItem(R.color.gris,R.drawable.vector_asset_add,CheckpointChooseNames[0]),
                new CheckpointThemeItem(R.color.rojo_normal,R.drawable.checkpoint_choose__love,CheckpointChooseNames[1]),
                new CheckpointThemeItem(R.color.yellow,R.drawable.checkpoint_choose__family,CheckpointChooseNames[2]),
                new CheckpointThemeItem(cn.pedant.SweetAlert.R.color.main_green_color,R.drawable.checkpoint_choose__friends,CheckpointChooseNames[3]),
                new CheckpointThemeItem(R.color.blue,R.drawable.checkpoint_choose__mentalhealth,CheckpointChooseNames[4]),
                new CheckpointThemeItem(R.color.purple_500,R.drawable.checkpoint_choose__work,CheckpointChooseNames[5]),
                new CheckpointThemeItem(R.color.teal_700,R.drawable.checkpoint_choose__creativity,CheckpointChooseNames[6]),
                new CheckpointThemeItem(R.color.pink,R.drawable.checkpoint_choose__education_learning,CheckpointChooseNames[7]),
                new CheckpointThemeItem(R.color.off_yellow,R.drawable.checkpoint_choose__health_fitness,CheckpointChooseNames[8]),
                new CheckpointThemeItem(R.color.brown,R.drawable.checkpoint_choose__hobbies_interest,CheckpointChooseNames[9])
        );
    }

    //obtienes los rows o checkpoints themes estaticos
    public List<CheckpointThemeItem> fetchStaticRows() {
        return STATIC_ROWS;
    }

    //obtienes los rows o checkpoints themes dinamicos guardados en firestore
    public LiveData<List<CheckpointThemeItem>> fetchFirestoreRows() {
        MutableLiveData<List<CheckpointThemeItem>> firestoreRows = new MutableLiveData<>();
        String userId = AuthenticationService.getInstance().getuid();
        if (userId != null) {
            userService.getusuarioRealTime(userId).addSnapshotListener((snapshot, error) -> {
                if (error != null) {
                    Log.d("error_listening", "Listen failed.", error);
                    return;
                }

                //aca es donde se soluciona el bug de que cuando el usuario aun no crea por primera vez una categoria (el field CheckpointThemesNames no existe aun) la lista de categorias no se muestra (osea las estaticas)
                //parece q xq la creacion de "items" esta afuera del condicional inicial y x ende si el docum existe pero no el field el setValue se ejecuta igual (aunque le pasemos una lista vacia) antes el setValue estaba adentro del 2do condicional
                //y es que Transformations.map unicamente se activa si es que sucede un cambio/modificacion en el LivaData de los paramteros que recibe(es decir a fetchFirestoreRows) es decir aplica una funcion al liveData que recibe solo si este cambia xd
                // x ende como que al setearle una lista vacia al LiveData de CheckpointThemesNames (al liveData q devuelve) como que detecta que algo cambio y x ende Transformations.map se activa y muestra los Statics rows nomas
                List<CheckpointThemeItem> items = new ArrayList<>();
                if (snapshot != null && snapshot.exists()) {
                    if (snapshot.contains("CheckpointThemesNames")) {
                        List<String> checkpointThemes = (List<String>) snapshot.get("CheckpointThemesNames");
                        for (String theme : checkpointThemes) {
                            items.add(new CheckpointThemeItem(R.color.gris, R.drawable.vector_asset_better_flag, theme));
                        }
                    }
                    firestoreRows.setValue(items);
                    Log.d("HOLAP", "fetchFirestoreRows: snapshot si existe ");
                } else {
                    // Document does not exist, return static rows
                    Log.d("HOLAP", "fetchFirestoreRows: snapshot no existe ");
                    //firestoreRows.setValue(fetchStaticRows());
                }
            });
        }
        return firestoreRows;
    }

   //combina tanto los themes checkpoints estaticos como los dinamicos guardados en firestore
    public LiveData<List<CheckpointThemeItem>> execute() {
        List<CheckpointThemeItem> staticRows = fetchStaticRows();

        //https://www.notion.so/Activity-seleccionar-categoria-nuevo-checkpoint-update-checkpoint-2fe38f46f27f4e6f93752aa178796773?pvs=4#fc890489b38742f099119e3d715125f2
        return Transformations.map(fetchFirestoreRows(), firestoreRows -> {
            List<CheckpointThemeItem> updatedRows = new ArrayList<>(staticRows);
            updatedRows.addAll(firestoreRows);
            return updatedRows;
        });
    }


    public void addCheckpointTheme(String checkpointThemeName){
        String userId = AuthenticationService.getInstance().getuid();
        if (userId != null){
            userService.addCheckpointThemeToUser(userId,checkpointThemeName).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Log.d("ADD NEW CATEGORY", "Category successfully added!");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(context, R.string.FireStore_Error, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
