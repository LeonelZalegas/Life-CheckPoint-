package com.example.influencer.Domain.Validations;

import android.app.Activity;
import android.content.Context;
import android.renderscript.ScriptGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.example.influencer.R;
import com.example.influencer.databinding.AlertDialogAddCategoryBinding;
import com.google.android.material.textfield.TextInputEditText;

public class NewCheckpointThemeValidation {
//bueno, tal parece que se podia trabajar sin el contexto ? jajaj dios mio, el parametro del error tenia q ser 1 string nomas

    static public boolean invoke(TextInputEditText editText) {
        AwesomeValidation Validacion_piola;
        Validacion_piola = new AwesomeValidation(ValidationStyle.BASIC);
        Validacion_piola.addValidation(editText,"[a-zA-Z]{4,17}", editText.getContext().getString(R.string.Error_new_category));// para q solo se ponga letras (may y min) y minimo 3 letras y maximo 17

        if (Validacion_piola.validate()) {
            return true;
        } else{
            return  false;
        }
    }
}
