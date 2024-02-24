package it.uniba.dib.sms232417.asilapp.dialogs.review;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import it.uniba.dib.sms232417.asilapp.R;
import it.uniba.dib.sms232417.asilapp.interfaces.OnRatingSentListener;


public class MaterialRating extends DialogFragment implements RatingBar.OnRatingBarChangeListener {

    public static final String KEY = "fragment_rate";
    private View theDialogView;
    private float userRating; // Variabile per memorizzare il rating dell'utente


    private OnRatingSentListener listener;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireActivity());
        theDialogView = onCreateView(LayoutInflater.from(requireActivity()), null, savedInstanceState);
        builder.setCancelable(false);
        builder.setView(theDialogView);
        return builder.create();
    }

    @Override
    public View getView() {
        return theDialogView;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.lib_material_fragment_rating, container);

        Button bt_maybeLater = view.findViewById(R.id.bt_maybeLater);
        bt_maybeLater.setOnClickListener(cancelButton -> dismiss());

        Button bt_ratingSend = view.findViewById(R.id.bt_ratingSend);
        bt_ratingSend.setOnClickListener(send ->{
           // Toast.makeText( getActivity(), "Please select 5 star rating!", Toast.LENGTH_SHORT).show()

            if (userRating == 0) {
                Toast.makeText(getActivity(), "Seleziona una valutazione!", Toast.LENGTH_SHORT).show();
                return;
            }else {
                Log.d("MaterialRating", "Valutazione inviata dall'utente: " + userRating);

                if (listener != null) {
                    listener.onRatingSent(userRating);
                }

                dismiss();
            }
        });

        RatingBar bt_ratingBar = view.findViewById(R.id.bt_ratingBar);
        bt_ratingBar.setOnRatingBarChangeListener(this);

        return view;
    }

    @Override
    public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {

        Log.d("MaterialRating", "Valutazione inserita dall'utente: " + v);
        userRating = v; // Salva il rating dell'utente




        }

    public void setOnRatingSentListener(OnRatingSentListener listener) {
        this.listener = listener;
    }


}
