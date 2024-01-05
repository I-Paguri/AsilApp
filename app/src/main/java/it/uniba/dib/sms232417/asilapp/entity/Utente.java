package it.uniba.dib.sms232417.asilapp.entity;

import java.util.Date;

public class Utente {
    private String nome;
    private String cognome;
    private String email;
    private String dataNascita;
    private String regione;

    public Utente(String nome, String cognome, String email, String dataNascita, String regione){
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
}
