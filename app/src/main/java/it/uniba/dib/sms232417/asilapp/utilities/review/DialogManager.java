package it.uniba.dib.sms232417.asilapp.utilities.review;

import android.content.Context;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import it.uniba.dib.sms232417.asilapp.utilities.review.MaterialFeedback;
import it.uniba.dib.sms232417.asilapp.utilities.review.MaterialRating;


public class DialogManager {

    public static void showMaterialFeedback(Context context, float rating, String email) {
        FragmentManager fragmentManager = getFragManager(context);
        MaterialFeedback materialFeedback = new MaterialFeedback(email);
        materialFeedback.setRating(rating);
        materialFeedback.show(fragmentManager, MaterialRating.KEY);
    }

    private static FragmentManager getFragManager(Context context){
        AppCompatActivity activity = (AppCompatActivity) context;
        return activity.getSupportFragmentManager();
    }
}