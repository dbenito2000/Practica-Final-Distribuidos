package threads;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.InvocationCallback;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import services.Controller;
import util.Acuerdo;
import util.Estado;

public class Proceso implements Runnable {

	// Flags
	
	boolean flagCoordinador;
	Object espera = new Object();
	
	// Variables
	
	int id;
	int coordinador;
	
	// Estados
	
	Acuerdo estadoAcuerdo;
	Estado estadoProceso = Estado.ACTIVO;
	
	// Generación de numeros aleatorios
	
	Random r = new Random();
	
	public Proceso(int idIn) {
		this.id = idIn;
		this.eleccion();
	}
	
	@Override
	public void run() {
		
		while (estadoProceso != Estado.INACTIVO) {
			
			// Bucle Principal
			// Espera
			
			try {
				Thread.sleep((long) randomNumber(0.5,1.0));
			} catch(Exception ex) {
				ex.printStackTrace();
				return;
			}
			
			// Computar Valor
			
			int valor = computarValor();
			if(valor < 0) {
				eleccion();
			}
			
		}
		
	}
	
	public int computarValor() {
		return 1;
	}
	
	public void eleccion() {
		boolean flagFinEleccion = false;
		while(!flagFinEleccion) {
			boolean flagSuccess = false;
			for(int i = id+1; i <= 6; i++) {
				if(coordinadorActivo("http://localhost:8080/loquequieras"+i)) {
					flagSuccess = true;
					break;
				}
			}
			if(flagSuccess) {
				flagCoordinador = false;
				try {
					synchronized(espera) {
						espera.wait(1000);
					}
					
					if(flagCoordinador) {
						flagFinEleccion = true;
					}
				} catch(Exception ex) {
					
				}
			} else {
				this.coordinador = id;
				notificarCoordinador();
				flagFinEleccion = true;
			}
		}
	} 
	
	public void notifyResponse(int idIn) {
		flagCoordinador = true;
		coordinador = idIn;
		synchronized(espera) {
			espera.notifyAll();
		}
	}
	
	public void notificarCoordinador() {
		for(int i = 0; i < Controller.instance.getNumProc(); i++) {
			// Request a la url de notificación
			request("");
		}
	}
	
	public String request(String urlIn) {
		try {
			URL url = new URL(urlIn);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("GET");
			if(con.getResponseCode() != 200) {
				// Error in connection!
				return "";
			} else {
				BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
				String output = "";
				String tmp;
				while((tmp = br.readLine()) != null) {
					output += tmp;
				}
				return output;
			}
			
		} catch(Exception ex) {
			return "";
		}
	}
	
	
	public boolean coordinadorActivo(String urlIn) {
		try {
			URL url = new URL(urlIn);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setConnectTimeout(1000);
			con.setRequestMethod("GET");
			if(con.getResponseCode() != 200) {
				// Error in connection!
				return false;
			} else {
				return true;
			}
			
		} catch(Exception ex) {
			return false;
		}
	}

	public int computar() {
		if(estadoProceso == Estado.INACTIVO) {
			return -1;
		} else {
			try {
				Thread.sleep((long) randomNumber(0.1,0.3));
			} catch(Exception ex) {
				ex.printStackTrace();
				return -1;
			}
			return 1;
		}
	}
	
	
	public int llamadaACoordinador() {
		
		// Llamada a API Rest del coordinador
		
		return 1;
	}
	
	public double randomNumber(double lowerLimit, double upperLimit) {
		return lowerLimit + r.nextDouble(upperLimit-lowerLimit);
	}

}
