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

		try {
			URL url = new URL("http://localhost:3000/");
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("GET");

			BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String output = "";
			String tmp;
			while ((tmp = br.readLine()) != null) {
				output += tmp;
			}

			this.idLocal = Integer.parseInt(output);

		} catch (Exception ex) {

		}
		System.out.println("[CONTROLLER] Started with id: " + this.idLocal);
		procesos = new ArrayList<>();
		for (int i = 0; i < numLocal; i++) {
			Proceso tmp = new Proceso(i + idLocal);
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

	public String getUrlForID(int id) {
		
		if(id > 3) {
			return "http://localhost:8082/PracticaFinal/rest/controller/";
		}
		if(id > 1) {
			return "http://localhost:8081/PracticaFinal/rest/controller/";
		}
		return "http://localhost:8080/PracticaFinal/rest/controller/";
	}

	// Endpoints

	@GET
	@Produces(MediaType.TEXT_PLAIN)
	@Path("start")
	public String start() {
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
		tmp.eleccion();
		return "";
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
}
