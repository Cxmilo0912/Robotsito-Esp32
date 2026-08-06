/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package co.sena.cimm.robot.servlet;

import co.sena.cimm.robot.model.EstadoGrabacion;
import co.sena.cimm.robot.model.PasoGrabado;
import co.sena.cimm.robot.model.RobotConfig;
import co.sena.cimm.robot.util.RobotHttpClient;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author julil
 */
@WebServlet("/grabar")
@javax.servlet.annotation.MultipartConfig
public class GrabacionServlet extends HttpServlet {

    private static final String SESSION_ESTADO = "estadoGrabacion";

    private static final AtomicBoolean reproduciendo = new AtomicBoolean(false);
    private static volatile int pasoActual = 0;
    private static volatile int totalPasos = 0;
    private static volatile Future<?> tareaActual = null;

    private static final ExecutorService executor = Executors.newSingleThreadExecutor(r -> {
        Thread t = new Thread(r, "GrabacionReproductor");
        t.setDaemon(true);
        return t;
    });

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String accion = request.getParameter("accion");
        if (accion == null) {
            accion = "";
        }
        PrintWriter out = response.getWriter();
        EstadoGrabacion estado = getEstadoFromSession(request);
        switch (accion) {
            case "iniciar":
                estado.iniciar();
                out.print("{\"exito\":true,\"mensaje\":\"Grabación iniciada\"}");
                break;
            case "detener":
                estado.cerrarUltimoPaso();
                estado.setGrabando(false);
                out.print("{\"exito\":true,\"mensaje\":\"Grabación detenida\",\"totalPasos\":"
                        + estado.getPasos().size() + "}");
                break;
            case "reproducir":
                iniciarReproduccion(request, estado, out);
                break;
            case "limpiar":
                estado.limpiar();
                out.print("{\"exito\":true,\"mensaje\":\"Grabación descartada\"}");
                break;
            case "cargarManual":
                estado.iniciar();
                String secuencia = request.getParameter("secuencia");
                if (secuencia == null) {
                    secuencia = "";
                }
                List<PasoGrabado> listaPasos = new ArrayList<>();
                for (String parte : secuencia.split(",")) {
                    String[] campos = parte.split("\\|");
                    if (campos.length == 2) {
                        try {
                            listaPasos.add(new PasoGrabado(campos[0], Long.parseLong(campos[1])));
                        } catch (NumberFormatException e) {
                        }
                    }

                }
                if (listaPasos.isEmpty()) {
                    out.print("{\"exito\":false,\"mensaje\":\"Secuencia vacía o inválida\"}");
                    break;
                }
                estado.limpiar();
                estado.setPasos(listaPasos);
                out.print("{\"exito\":true,\"mensaje\":\"" + listaPasos.size() + " pasos cargados\"}");

                break;

            default:
                out.print("{\"exito\":false,\"mensaje\":\"Acción desconocida\"}");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        if ("estado".equals(request.getParameter("accion"))) {
            responderEstado(request, response);
        } else {
            response.sendError(400, "Usa accion=estado");
        }
    }

    private void iniciarReproduccion(HttpServletRequest req, EstadoGrabacion estado, PrintWriter out) {
        if (reproduciendo.get()) {
            out.print("{\"exito\":false,\"mensaje\":\"Ya se está reproduciendo\"}");
            return;
        }
        List<PasoGrabado> pasos = estado.getPasos();
        if (pasos.isEmpty()) {
            out.print("{\"exito\":false,\"mensaje\":\"No hay nada grabado\"}");
            return;
        }

        RobotConfig config = RobotConfigServlet.getConfigFromSession(req);
        reproduciendo.set(true);
        pasoActual = 0;
        totalPasos = pasos.size();

        tareaActual = executor.submit(() -> ejecutarReproduccion(config, pasos));
        out.print("{\"exito\":true,\"mensaje\":\"Reproduciendo\",\"totalPasos\":" + totalPasos + "}");
    }

    private void ejecutarReproduccion(RobotConfig config, List<PasoGrabado> pasos) {

        try {

            for (int i = 0; i < pasos.size() && reproduciendo.get(); i++) {
                pasoActual = i + 1;
                PasoGrabado paso = pasos.get(i);
                RobotHttpClient.enviarComando(config, paso.getComando());
                Thread.sleep(paso.getDuracionMs());
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            RobotHttpClient.enviarComando(config, "/stop");
            reproduciendo.set(false);
        }

    }

    private void responderEstado(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Cache-Control", "no-cache");

        EstadoGrabacion estado = getEstadoFromSession(request);
        double prog = totalPasos > 0 ? (double) pasoActual / totalPasos * 100 : 0;

        response.getWriter().print("{\"grabando\":" + estado.isGrabando()
                + ",\"totalGrabado\":" + estado.getPasos().size()
                + ",\"reproduciendo\":" + reproduciendo.get()
                + ",\"pasoActual\":" + pasoActual
                + ",\"totalPasos\":" + totalPasos
                + ",\"progreso\":" + String.format("%.0f", prog) + "}");

    }

    public static EstadoGrabacion getEstadoFromSession(HttpServletRequest request) {
        HttpSession session = request.getSession(true);
        EstadoGrabacion estado = (EstadoGrabacion) session.getAttribute(SESSION_ESTADO);
        if (estado == null) {
            estado = new EstadoGrabacion();
            session.setAttribute(SESSION_ESTADO, estado);
        }
        return estado;
    }
}
