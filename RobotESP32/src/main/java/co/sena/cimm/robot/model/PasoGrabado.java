/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package co.sena.cimm.robot.model;

import java.io.Serializable;

/**
 *
 * @author julil
 */
public class PasoGrabado implements Serializable {

    private String comando;
    private long duracionMs;
    private String descripcion;

    public PasoGrabado() {
    }

    public PasoGrabado(String comando, long duracionMs, String descripcion) {
        this.comando = comando;
        this.duracionMs = duracionMs;
        this.descripcion = descripcion;
    }

    public PasoGrabado(String comando, long duracionMs) {
         this.comando = comando;
        this.duracionMs = duracionMs;
    }

    public String getComando() {
        return comando;
    }

    public void setComando(String comando) {
        this.comando = comando;
    }

    public long getDuracionMs() {
        return duracionMs;
    }

    public void setDuracionMs(long duracionMs) {
        this.duracionMs = duracionMs;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

}
