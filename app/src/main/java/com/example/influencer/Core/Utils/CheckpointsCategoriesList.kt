package com.example.influencer.Core.Utils

import android.content.res.Resources
import com.example.influencer.Features.Create_Modify_Checkpoint_Menu.Domain.Model.CheckpointThemeItem
import com.example.influencer.R
import javax.inject.Inject
import javax.inject.Singleton

//para usarlo en el recyclerview de mostrar categorias para seleccionar una y que se muetren los posts de ella, tod0 en el profile tab
//TODO:cambiar en las clases relacioandas al filtrado de categorias el listado manual de los nombres por el de este objeto

@Singleton
class CheckpointsCategoriesList @Inject constructor(resources: Resources) {
    private val names = resources.getStringArray(R.array.Checkpoint_categories_list)

    val categories = listOf(
        CheckpointThemeItem(R.color.rojo_normal, R.drawable.checkpoint_choose__love, names[0]),
        CheckpointThemeItem(R.color.yellow, R.drawable.checkpoint_choose__family, names[1]),
        CheckpointThemeItem(cn.pedant.SweetAlert.R.color.main_green_color, R.drawable.checkpoint_choose__friends, names[2]),
        CheckpointThemeItem(R.color.blue, R.drawable.checkpoint_choose__mentalhealth, names[3]),
        CheckpointThemeItem(R.color.purple_500, R.drawable.checkpoint_choose__work, names[4]),
        CheckpointThemeItem(R.color.teal_700, R.drawable.checkpoint_choose__creativity, names[5]),
        CheckpointThemeItem(R.color.pink, R.drawable.checkpoint_choose__education_learning, names[6]),
        CheckpointThemeItem(R.color.off_yellow, R.drawable.checkpoint_choose__health_fitness, names[7]),
        CheckpointThemeItem(R.color.brown, R.drawable.checkpoint_choose__hobbies_interest, names[8]),
        CheckpointThemeItem(R.color.gris, R.drawable.vector_asset_add, names[9])
    )
}

