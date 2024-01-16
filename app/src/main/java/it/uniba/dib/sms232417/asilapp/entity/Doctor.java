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

    protected Doctor(Parcel in) {
        nome = in.readString();
        cognome = in.readString();
        email = in.readString();
        dataNascita = in.readString();
        regione = in.readString();
        specializzazione = in.readString();
        numeroDiRegistrazioneMedica = in.readString();
    }

    public static final Creator<Doctor> CREATOR = new Creator<Doctor>() {
        @Override
        public Doctor createFromParcel(Parcel in) {
            return new Doctor(in);
        }

        @Override
        public Doctor[] newArray(int size) {
            return new Doctor[size];
        }
    };

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
