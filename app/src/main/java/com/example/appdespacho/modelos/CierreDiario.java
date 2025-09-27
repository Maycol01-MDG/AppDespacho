package com.example.appdespacho.modelos;

public class CierreDiario {
    private int id;
    private String bomba;
    private double lecturaInicial;
    private double lecturaFinal;
    private double salidaVales;
    private double salidaContometro;
    private double diferencia;
    private String fecha;
    private String operador; // 🔹 Nuevo campo

    // Constructor vacío
    public CierreDiario() {
    }

    // Constructor con parámetros
    public CierreDiario(int id, String bomba, double lecturaInicial, double lecturaFinal,
                        double salidaVales, double salidaContometro, double diferencia,
                        String fecha, String operador) {
        this.id = id;
        this.bomba = bomba;
        this.lecturaInicial = lecturaInicial;
        this.lecturaFinal = lecturaFinal;
        this.salidaVales = salidaVales;
        this.salidaContometro = salidaContometro;
        this.diferencia = diferencia;
        this.fecha = fecha;
        this.operador = operador;
    }

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBomba() {
        return bomba;
    }

    public void setBomba(String bomba) {
        this.bomba = bomba;
    }

    public double getLecturaInicial() {
        return lecturaInicial;
    }

    public void setLecturaInicial(double lecturaInicial) {
        this.lecturaInicial = lecturaInicial;
    }

    public double getLecturaFinal() {
        return lecturaFinal;
    }

    public void setLecturaFinal(double lecturaFinal) {
        this.lecturaFinal = lecturaFinal;
    }

    public double getSalidaVales() {
        return salidaVales;
    }

    public void setSalidaVales(double salidaVales) {
        this.salidaVales = salidaVales;
    }

    public double getSalidaContometro() {
        return salidaContometro;
    }

    public void setSalidaContometro(double salidaContometro) {
        this.salidaContometro = salidaContometro;
    }

    public double getDiferencia() {
        return diferencia;
    }

    public void setDiferencia(double diferencia) {
        this.diferencia = diferencia;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getOperador() {
        return operador;
    }

    public void setOperador(String operador) {
        this.operador = operador;
    }
}
