package com.example.appmapscamera.model;

import java.util.List;

/**
 * Classe que representa um local geográfico com nome, coordenadas, raio de alcance e fotos associadas.
 */
public class Local {
    private String nome;
    private double latitude;
    private double longitude;
    private int raio;
    private List<String> enderecoFotos;

    /**
     * Construtor padrão da classe Local.
     * Inicializa o objeto sem definir valores para os atributos.
     */
    public Local() {
    }

    /**
     * Construtor que inicializa o local com nome, raio, latitude e longitude.
     *
     * @param nome      Nome do local.
     * @param raio      Raio de alcance ao redor do local, em metros.
     * @param latitude  Latitude do local.
     * @param longitude Longitude do local.
     */
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
