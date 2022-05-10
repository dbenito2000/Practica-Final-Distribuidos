package services;

import java.io.BufferedReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.io.InputStreamReader;
import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import threads.Proceso;
import util.Maquina;

@Singleton
@Path("controller")
public class Controller {

	// Singleton Structure

	public static Controller instance = null;
	
	
	ArrayList<Thread> threads;
	ArrayList<Proceso> procesos;

	int numLocal = 2;
	int idLocal = 0;
	int numProcesos = 6;

	ArrayList<Maquina> maquinas;
	
	
	public Controller() {
		Controller.instance = this;
	}

	public static Controller getController() {
		return Controller.instance;
	}

	public int getNumProc() {
		return this.numProcesos;
	}

	public String getUrlForID(int id) {
		
		for(int i = 0; i < maquinas.size();i++) {
			if(maquinas.get(i).idInMachine(id)) {
				return "http://" + maquinas.get(i).getAddress() + "/PracticaFinal/rest/controller/";
			}
		}
		
		return "none";
	
	}

	// Endpoints

	@GET
	@Produces(MediaType.TEXT_PLAIN)
	@Path("start")
	public String start() {
		
		System.out.println("[CONTROLLER] Started with id: " + this.idLocal);
		procesos = new ArrayList<>();
		threads = new ArrayList<>();
		for (int i = 0; i < numLocal; i++) {
			Proceso tmp = new Proceso(i + idLocal);
			procesos.add(tmp);
			Thread tmpThread = new Thread(tmp);
			threads.add(tmpThread);
		}
		for(int i = 0; i < threads.size();i++) {
			threads.get(i).start();
		}
		
		return "Operacion Realizada";
	}

	@GET
	@Produces(MediaType.TEXT_PLAIN)
	@Path("setId")
	public String setId(@QueryParam(value = "id") int id, @QueryParam(value = "cantidad") int cantidad, @QueryParam(value = "total") int total) {
		
		this.idLocal = id;
		this.numLocal = cantidad;
		this.numProcesos = total;
		this.maquinas = new ArrayList<>();
		return "Operacion Realizada";
	}
	
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	@Path("addMachine")
	public String addMachine(@QueryParam(value="address") String address, @QueryParam(value = "id") int id, @QueryParam(value = "cantidad") int cantidad) {
		
		this.maquinas.add(new Maquina(address,id,cantidad));
		
		return "Operacion Realizada";
	}
	
	
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	@Path("eleccion")
	public String eleccion(@QueryParam(value = "id") int id) {
		if (id - idLocal >= numLocal || id - idLocal < 0)
			return "Error";
		int workingId = id - idLocal;
		Proceso tmp = procesos.get(workingId);
		int res = tmp.eleccion();
		return Integer.toString(res);
	}

	@GET
	@Produces(MediaType.TEXT_PLAIN)
	@Path("computar")
	public String computar(@QueryParam(value = "id") int id) {
		if (id - idLocal >= numLocal || id - idLocal < 0)
			return "Error";
		int workingId = id - idLocal;
		Proceso tmp = procesos.get(workingId);
		return Integer.toString(tmp.computar());
	}
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	@Path("notificarControlador")
	public String notificarControlador(@QueryParam(value = "id") int id, @QueryParam(value = "controlador") int controlador) {
		if (id - idLocal >= numLocal || id - idLocal < 0)
			return "Error";
		int workingId = id - idLocal;
		Proceso tmp = procesos.get(workingId);
		tmp.notifyResponse(controlador);
		return "Operacion Realizada";
	}
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	@Path("pararProceso")
	public String pararProceso(@QueryParam(value = "id") int id) {
		if (id - idLocal >= numLocal || id - idLocal < 0)
			return "Error";
		int workingId = id - idLocal;
		Proceso tmp = procesos.get(workingId);
		tmp.pararProcesso();
		return "Operacion Realizada";
	}
	
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	@Path("iniciarProceso")
	public String iniciarProceso(@QueryParam(value = "id") int id) {
		if (id - idLocal >= numLocal || id - idLocal < 0)
			return "Error";
		int workingId = id - idLocal;
		Proceso tmp = procesos.get(workingId);
		System.out.println("Intentando iniciar xD");
		tmp.iniciarProceso();
		return "Operacion Realizada";
	}
	
}
