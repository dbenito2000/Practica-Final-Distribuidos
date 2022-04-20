package services;

import java.util.ArrayList;

import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import threads.Proceso;


@Singleton
@Path("controller")
public class Controller {
	
	// Singleton Structure
	
	public static Controller instance = null;
	ArrayList<Proceso> procesos;
	
	int numLocal = 2;
	int idLocal = 0;
	int numProcesos = 6;
	
	public Controller() {
		Controller.instance = this;
		procesos = new ArrayList<>();
		for(int i = 0; i < numLocal; i++) {
			Proceso tmp = new Proceso(i+idLocal);
			procesos.add(tmp);
			Thread tmpThread = new Thread(tmp);
			tmpThread.start();
		}
	}
	
	
	
	public static Controller getController() {
		return Controller.instance;
	}
	
	public int getNumProc() {
		return this.numProcesos;
	}
	
	
	
	// Endpoints
	
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	@Path("saludo")
	public String saludo() {
		return "Hola Gente!";
	}
	
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	@Path("eleccion")
	public String eleccion(@QueryParam(value="id") int id) {
		int workingId = id-idLocal;
		Proceso tmp = procesos.get(workingId);
		tmp.eleccion();
		
		
		return "";
	}
	
	
	
}
