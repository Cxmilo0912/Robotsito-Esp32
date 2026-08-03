/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package co.sena.cimm.robot.model;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Admin
 */
public class EstadoGrabacion {

    private List<PasoGrabado> pasos;
    private LocalDateTime inicioPaso;
    private boolean grabando;

    public EstadoGrabacion() {
        this.pasos = new ArrayList<>();
        this.grabando = false;
        this.inicioPaso = null;
    }

    public void registrarPaso(String comando, String descripcion) {
        LocalDateTime ahora = LocalDateTime.now();

        // Si ya había un paso abierto, cerrarlo con la duración transcurrida
        if (inicioPaso != null && !pasos.isEmpty()) {
            long ms = Duration.between(inicioPaso, ahora).toMillis();
            pasos.get(pasos.size() - 1).setDuracionMs(ms);
        }

        pasos.add(new PasoGrabado(comando, 0, descripcion));
        inicioPaso = ahora;
    }

    /**
     * Se llama al pulsar "Detener": cierra el último paso pendiente.
     */
    public void cerrarUltimoPaso() {
        if (inicioPaso != null && !pasos.isEmpty()) {
            long ms = Duration.between(inicioPaso, LocalDateTime.now()).toMillis();
            pasos.get(pasos.size() - 1).setDuracionMs(ms);
        }
        inicioPaso = null;
    }

    public void iniciar() {
        pasos = new ArrayList<>();
        grabando = true;
        inicioPaso = null;
    }

    public void limpiar() {
        pasos = new ArrayList<>();
        grabando = false;
        inicioPaso = null;
    }

    public List<PasoGrabado> getPasos() {
        return pasos;
    }

    public void setPasos(List<PasoGrabado> pasos) {
        this.pasos = pasos;
    }

    public LocalDateTime getInicioPaso() {
        return inicioPaso;
    }

    public void setInicioPaso(LocalDateTime inicioPaso) {
        this.inicioPaso = inicioPaso;
    }

    public boolean isGrabando() {
        return grabando;
    }

    public void setGrabando(boolean grabando) {
        this.grabando = grabando;
    }
}
