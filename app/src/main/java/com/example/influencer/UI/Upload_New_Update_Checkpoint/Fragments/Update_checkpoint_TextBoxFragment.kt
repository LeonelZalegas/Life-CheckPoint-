package com.example.influencer.UI.Upload_New_Update_Checkpoint.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.influencer.R

const val MAX_LENGTH = 140

class Update_checkpoint_TextBox : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_update_checkpoint__text_box, container, false)
    }


}