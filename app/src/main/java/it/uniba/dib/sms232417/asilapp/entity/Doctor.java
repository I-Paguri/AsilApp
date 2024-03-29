package it.uniba.dib.sms232417.asilapp.entity;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Doctor implements Parcelable, Serializable {
    private String uuid;
    private String nome;
    private String cognome;
    private String email;
    private String dataNascita;
    private String regione;
    private String specializzazione;
    private String numeroDiRegistrazioneMedica;
    private List<String> myPatients;
    private String profileImageUrl;

    public Doctor() {
    }

    public Doctor(String uuid, String nome, String cognome, String email, String dataNascita, String regione, String specializzazione, String numeroDiRegistrazioneMedica) {
        this.uuid = uuid;
        this.nome = nome;
        this.cognome = cognome;
        this.email = email;
        this.dataNascita = dataNascita;
        this.regione = regione;
        this.specializzazione = specializzazione;
        this.numeroDiRegistrazioneMedica = numeroDiRegistrazioneMedica;
        this.myPatients = new ArrayList<>();
    }

    protected Doctor(Parcel in) {
        uuid = in.readString();
        nome = in.readString();
        cognome = in.readString();
        email = in.readString();
        dataNascita = in.readString();
        regione = in.readString();
        specializzazione = in.readString();
        numeroDiRegistrazioneMedica = in.readString();
        myPatients = in.createStringArrayList();
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

    public List<String> getMyPatients() {
        return myPatients;
    }

    public void setMyPatients(List<String> myPatients) {
        this.myPatients = myPatients;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
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

    public int getAge() {
        String birthdayString = this.getDataNascita();
        try {
            // Parse the birthday string into a Date object
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy", Locale.ENGLISH);

            Date birthDate = sdf.parse(birthdayString);

            // Get the current date
            Calendar today = Calendar.getInstance();

            // Convert the birth date into a Calendar object
            Calendar birthDay = Calendar.getInstance();
            birthDay.setTimeInMillis(birthDate.getTime());

            // Calculate the age
            int age = today.get(Calendar.YEAR) - birthDay.get(Calendar.YEAR);

            // If the birthday has not occurred this year, subtract one from the age
            if (today.get(Calendar.DAY_OF_YEAR) < birthDay.get(Calendar.DAY_OF_YEAR)) {
                age--;
            }
            return age;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
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


    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(uuid);
        dest.writeString(nome);
        dest.writeString(cognome);
        dest.writeString(email);
        dest.writeString(dataNascita);
        dest.writeString(regione);
        dest.writeString(specializzazione);
        dest.writeString(numeroDiRegistrazioneMedica);
        dest.writeStringList(myPatients);
    }

    @Override
    public String toString() {
        return "Doctor{" +
                "uuid='" + uuid + '\'' +
                ", nome='" + nome + '\'' +
                ", cognome='" + cognome + '\'' +
                ", email='" + email + '\'' +
                ", dataNascita='" + dataNascita + '\'' +
                ", regione='" + regione + '\'' +
                ", specializzazione='" + specializzazione + '\'' +
                ", numeroDiRegistrazioneMedica='" + numeroDiRegistrazioneMedica + '\'' +
                ", myPatientsUUID=" + myPatients +
                ", profileImageUrl='" + profileImageUrl + '\'' +
                '}';
    }
}
