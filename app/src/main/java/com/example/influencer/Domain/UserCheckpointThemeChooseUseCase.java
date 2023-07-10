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

    public List<CheckpointThemeItem> fetchStaticRows() {
        return STATIC_ROWS;
    } //habria que hacer privado esto despues porque luego cuando incluyamos el tema de firestore, se llamara a la funcion "execute" directamente q es la que combina este metodo con el metodo q obtiene de firestore

//bueno...aca faltaria agregar lo que falta de la parte de firestore (de cada usuario) para luego hacer otro metodo para combinar
    //tanto la parte fija de las categorias de checkpoint como las variables de cada usuario guardadas en firestore (fijarse en phind lo q nos dio)

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
