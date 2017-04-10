package com.sap.hana.cloud.samples.weather.web;

import java.io.IOException;

import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.sap.security.auth.login.LoginContextFactory;


/**
 * Servlet implementation class HelloWorldServlet
 */
public class HelloWorldServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public HelloWorldServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		//response.getWriter().append("Served at: ").append(request.getContextPath());
		//response.getWriter().println("Hola desde una aplicacion Java en HCP");
		String user = request.getRemoteUser();
		String addr = request.getRemoteAddr();
		String host = request.getRemoteHost();
		String port = String.valueOf(request.getRemotePort());
		String names = request.getHeaderNames().toString();
		String name =  request.getUserPrincipal().getName();
		if (user != null){	
			response.getWriter().println("names:" + names + " name: " + name);
			response.getWriter().println("Hola " + user + " addr: "+addr + " Host: " + host + " Port: " + port); //Obtener nombre usuario logueado
		}else{
			LoginContext loginContext;//Instancia de la clase Login.
			try{
				loginContext = LoginContextFactory.createLoginContext("FORM");
				loginContext.login();
				response.getWriter().println("Usuario: " + request.getRemoteUser() + "Addr: "+ request.getRemoteAddr());
			}catch ( LoginException ex){
				ex.printStackTrace();
			}
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
