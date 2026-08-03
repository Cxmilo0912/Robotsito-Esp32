/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package co.sena.cimm.robot.servlet;

import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author julil
 */
@WebServlet("/grabar")
public class GrabacionServlet extends HttpServlet {
    
    @Override
    protected  void doGet(HttpServletRequest request,HttpServletResponse response)
    throws IOException{
        String accion = request.getParameter("accion");
        if(accion == "estado"){}
    
    }
    @Override
    protected void doPost(HttpServletRequest request,HttpServletResponse response)
    throws IOException{
    String accion = request.getParameter("accion");
   
    switch(accion){
        case "iniciar":
            break;
        case "detener":
            break;
        case "reproducir":
            break;
        case "limpiar":
            break;
    }
    }
}
