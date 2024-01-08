package it.uniba.dib.sms232417.asilapp.entity;

import java.io.Serializable;

public class Patient implements Serializable {
    private String nome;
    private String cognome;
    private String email;
    private String dataNascita;
    private String regione;

    public Patient(Patient patient){
        this.nome = patient.getNome();
        this.cognome = patient.getCognome();
        this.email = patient.getEmail();
        this.dataNascita = patient.getDataNascita();
        this.regione = patient.getRegione();


    }
    public Patient(String nome, String cognome, String email, String dataNascita, String regione){
        this.nome = nome;
        this.cognome = cognome;
        this.email = email;
        this.dataNascita = dataNascita;
        this.regione = regione;

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



    @Override
    public String toString() {
        return "Patient{" +
                "nome='" + nome + '\'' +
                ", cognome='" + cognome + '\'' +
                ", email='" + email + '\'' +
                ", dataNascita='" + dataNascita + '\'' +
                ", regione='" + regione + '\'' +
                '}';
    }
}
