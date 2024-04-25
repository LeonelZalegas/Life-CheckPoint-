package com.example.influencer.Features.Create_Modify_Checkpoint_Menu.Data.LocalDB;

import android.content.res.Resources;

import com.example.influencer.R;
import com.example.influencer.Features.Create_Modify_Checkpoint_Menu.Domain.Model.CheckpointThemeItem;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

//esta clase se encarga de generar los items/categorias estaticos del reyclerview que van a estar siempre presentes en cualquier usuario
//para eso se necesitaba Context (para obtener el array con los strings de los nombres de cada categoria desde strings.xml) pero como
//no puede haber Context aca, se utiliza la clase MyApp (en Core) para obtener estos sin la necesidad del context
public class CheckpointChooseStaticRows {

    String[] CheckpointChooseNames;
    private final List<CheckpointThemeItem> STATIC_ROWS;
    private final Resources resources;

    @Inject
    public CheckpointChooseStaticRows (Resources resources){
        this.resources = resources;
        CheckpointChooseNames = resources.getStringArray(R.array.names_checkpoint_choose);
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

    public List<CheckpointThemeItem> fetchStaticRows(){
        return STATIC_ROWS;
    }

}
