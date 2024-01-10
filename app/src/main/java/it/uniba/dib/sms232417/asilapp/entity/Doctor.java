package it.uniba.dib.sms232417.asilapp.entity;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class Doctor implements Parcelable, Serializable {
    private String nome;
    private String cognome;
    private String email;
    private String dataNascita;
    private String regione;
    private String specializzazione;
    private String numeroDiRegistrazioneMedica;

    public Doctor(String nome, String cognome, String email, String dataNascita, String regione, String specializzazione, String numeroDiRegistrazioneMedica) {
        this.nome = nome;
        this.cognome = cognome;
        this.email = email;
        this.dataNascita = dataNascita;
        this.regione = regione;
        this.specializzazione = specializzazione;

        this.numeroDiRegistrazioneMedica = numeroDiRegistrazioneMedica;
    }

    public String getNome() {
        return nome;
    }

    public String getCognome() {
        return cognome;
    }

    public String getEmail() {
        return email;
    }

    public String getDataNascita() {
        return dataNascita;
    }

    public String getRegione() {
        return regione;
    }

    public String getSpecializzazione() {
        return specializzazione;
    }


    public String getNumeroDiRegistrazioneMedica() {
        return numeroDiRegistrazioneMedica;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(nome);
        dest.writeString(cognome);
        dest.writeString(email);
        dest.writeString(dataNascita);
        dest.writeString(regione);
        dest.writeString(specializzazione);
        dest.writeString(numeroDiRegistrazioneMedica);

    }
}
