package com.example.appdespacho.modelos;

public class TransferenciaCrc {
    private int id;
    private String fecha;
    private String numeroVale;
    private double galones;
    private String camionCrc;

    public TransferenciaCrc() {}

    public TransferenciaCrc(String fecha, String numeroVale, double galones, String camionCrc) {
        this.fecha = fecha;
        this.numeroVale = numeroVale;
        this.galones = galones;
        this.camionCrc = camionCrc;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }

    public String getNumeroVale() { return numeroVale; }
    public void setNumeroVale(String numeroVale) { this.numeroVale = numeroVale; }

    public double getGalones() { return galones; }
    public void setGalones(double galones) { this.galones = galones; }

    public String getCamionCrc() { return camionCrc; }
    public void setCamionCrc(String camionCrc) { this.camionCrc = camionCrc; }
}
