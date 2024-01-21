package it.uniba.dib.sms232417.asilapp.entity;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Patient implements Parcelable, Serializable {
    private String UUID;
    private String nome;
    private String cognome;
    private String email;
    private String dataNascita;
    private String regione;
    private List<Treatment> treatments;
    private Doctor doctor;

    public Patient() {
    }

    public Patient(String UUID, String nome, String cognome, String email, String dataNascita, String regione){
        this.nome = nome;
        this.cognome = cognome;
        this.email = email;
        this.dataNascita = dataNascita;
        this.regione = regione;

        this.UUID = UUID;

    }

    protected Patient(Parcel in) {
        UUID = in.readString();
        nome = in.readString();
        cognome = in.readString();
        email = in.readString();
        dataNascita = in.readString();
        regione = in.readString();
    }

    public static final Creator<Patient> CREATOR = new Creator<Patient>() {
        @Override
        public Patient createFromParcel(Parcel in) {
            return new Patient(in);
        }

        @Override
        public Patient[] newArray(int size) {
            return new Patient[size];
        }
    };

    public void setUUID(String UUID) {
        this.UUID = UUID;
    }

    public String getUUID() {
        return UUID;
    }

    public String getCognome() {
        return cognome;
    }

    public String getNome() {
        return nome;
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


    public void setTreatments(List<Treatment> treatments) {
        this.treatments = treatments;
    }

    public List<Treatment> getTreatments() {
        return treatments;
    }

    @Override
    public String toString() {
        return "Patient{" +
                "UUID='" + UUID + '\'' +
                "nome='" + nome + '\'' +
                ", cognome='" + cognome + '\'' +
                ", email='" + email + '\'' +
                ", dataNascita='" + dataNascita + '\'' +
                ", regione='" + regione + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(UUID);
        dest.writeString(nome);
        dest.writeString(cognome);
        dest.writeString(email);
        dest.writeString(dataNascita);
        dest.writeString(regione);
    }
}
