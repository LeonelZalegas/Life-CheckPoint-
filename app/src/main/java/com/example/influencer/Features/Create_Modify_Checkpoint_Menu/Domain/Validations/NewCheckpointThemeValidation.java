package com.example.influencer.Features.Create_Modify_Checkpoint_Menu.Domain.Validations;


import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.example.influencer.R;
import com.google.android.material.textfield.TextInputEditText;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class NewCheckpointThemeValidation {
//bueno, tal parece que se podia trabajar sin el contexto ? jajaj dios mio, el parametro del error tenia q ser 1 string nomas
    private AwesomeValidation validation;

    @Inject
    public NewCheckpointThemeValidation(){
        this.validation = new AwesomeValidation(ValidationStyle.BASIC);
    }

    public boolean invokeNewCheckpointThemeValidation(TextInputEditText editText) {
        validation.addValidation(editText,"[a-zA-Z]{4,17}", editText.getContext().getString(R.string.Error_new_category));// para q solo se ponga letras (may y min) y minimo 3 letras y maximo 17
        return validation.validate();
    }
}
