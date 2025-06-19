package com.example.appmapscamera.model;

import java.util.List;

public class Local {
    private String nome;
    private double latitude;
    private double longitude;
    private int raio;
    private List<String> enderecoFotos;

    public Local() {
    }


    public Local(String nome, int raio, double latitude, double longitude) {
        this.nome = nome;
        this.raio = raio;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getRaio() {
        return raio;
    }

    public void setRaio(int raio) {
        this.raio = raio;
    }

    public List<String> getEnderecoFotos() {
        return enderecoFotos;
    }

    public void setEnderecoFotos(List<String> enderecoFotos) {
        this.enderecoFotos = enderecoFotos;
    }


}
