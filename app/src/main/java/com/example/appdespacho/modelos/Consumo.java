package com.example.appdespacho.modelos;

public class Consumo {
    private int id;
    private String fecha;
    private String tipoConsumo;
    private String placa;
    private String numeroVale;
    private String bomba;
    private double galones;
    private String motivo;

    public Consumo() {
    }

    public Consumo(String fecha, String tipoConsumo, String placa, String numeroVale,
                   String bomba, double galones, String motivo) {
        this.fecha = fecha;
        this.tipoConsumo = tipoConsumo;
        this.placa = placa;
        this.numeroVale = numeroVale;
        this.bomba = bomba;
        this.galones = galones;
        this.motivo = motivo;
    }


    public Consumo(int id, String fecha, String tipoConsumo, String placa, String numeroVale,
                   String bomba, double galones, String motivo) {
        this.id = id;
        this.fecha = fecha;
        this.tipoConsumo = tipoConsumo;
        this.placa = placa;
        this.numeroVale = numeroVale;
        this.bomba = bomba;
        this.galones = galones;
        this.motivo = motivo;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getTipoConsumo() {
        return tipoConsumo;
    }

    public void setTipoConsumo(String tipoConsumo) {
        this.tipoConsumo = tipoConsumo;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public String getNumeroVale() {
        return numeroVale;
    }

    public void setNumeroVale(String numeroVale) {
        this.numeroVale = numeroVale;
    }

    public String getBomba() {
        return bomba;
    }

    public void setBomba(String bomba) {
        this.bomba = bomba;
    }

    public double getGalones() {
        return galones;
    }

    public void setGalones(double galones) {
        this.galones = galones;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }
}
