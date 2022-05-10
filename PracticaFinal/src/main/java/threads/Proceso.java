package threads;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;
import java.util.concurrent.Semaphore;

import services.Controller;
import util.Acuerdo;
import util.Estado;

public class Proceso implements Runnable {

	// Flags
	
	boolean flagCoordinador;
	Object espera = new Object();
	Semaphore sleepSemaphore = new Semaphore(0);
	
	// Variables
	
	int id;
	int coordinador;
	
	// Estados
	
	Acuerdo estadoAcuerdo = Acuerdo.ACUERDO;
	Estado estadoProceso = Estado.ACTIVO;
	
	// Generación de numeros aleatorios
	
	Random r = new Random();
	
	public Proceso(int idIn) {
		this.id = idIn;
	}
	
	public void escribirAConsola(String msg) {
		System.out.println("["+this.id+"] " + msg);
	}
	
	@Override
	public void run() {
		
		this.eleccion();
		System.out.println("["+id+"] Started.");
		
		
		while (true) {
			if(estadoProceso == Estado.ACTIVO) {
				// Bucle Principal
				// Espera
				escribirAConsola("Thread en Espera");
				try {
					Thread.sleep((long) randomNumber(0.5,1.0) * 1000);
				} catch(Exception ex) {
					ex.printStackTrace();
					return;
				}
				
				// Computar Valor
				escribirAConsola("Computando valor.");
				int valor = computarValor();
				
				if(valor < 0) {
					escribirAConsola("Valora Computado Incorrecto");
					eleccion();
					
				}
			} else {
				escribirAConsola("Parado por controlador.");
				try {
					sleepSemaphore.acquire();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
				escribirAConsola("Reanundando ejecucion.");
				this.estadoProceso = Estado.ACTIVO;
				this.eleccion();
			}
			
			
		}
		
	}
	
	public void pararProcesso() {
		this.estadoProceso = Estado.INACTIVO;
	}
	
	public void iniciarProceso() {
		System.out.println("Intentandolo xD");
		sleepSemaphore.release();
		System.out.println("[*] Notificado!");
	}
	
	public int computarValor() {
		
		if(this.coordinador == this.id) return 1;
		try {
			String res = request(Controller.getController().getUrlForID(coordinador) + "computar?id=" + coordinador);
			
			return Integer.parseInt(res);
		} catch(Exception ex) {
			return -1;
		}
		
		//return 1;
	}
	
	public int eleccion() {
		if(estadoProceso == Estado.INACTIVO) return -1;
		if(estadoAcuerdo == Acuerdo.ACUERDO) {
			
			if(this.id == Controller.getController().getNumProc()-1) {
				this.coordinador = id;
				notificarCoordinador();
				estadoAcuerdo = Acuerdo.ACUERDO;
				return 0;
			}
			
			estadoAcuerdo= Acuerdo.ELECCION_ACTIVA;
			boolean flagFinEleccion = false;
			while(!flagFinEleccion) {
				boolean flagSuccess = false;
				for(int i = id+1; i < Controller.getController().getNumProc(); i++) {
					if(coordinadorActivo(Controller.getController().getUrlForID(i)+"eleccion?id="+i)) {
						flagSuccess = true;
						break;
					}
				}
				if(flagSuccess) {
					
					escribirAConsola("Entrando a eleccion Pasiva");
					
					estadoAcuerdo = Acuerdo.ELECCION_PASIVA;
					//flagCoordinador = false;
					try {
						synchronized(espera) {
							espera.wait(1000);
						}
						
						if(flagCoordinador) {
							flagFinEleccion = true;
							flagCoordinador = false;
							estadoAcuerdo = Acuerdo.ACUERDO;
						}
					} catch(Exception ex) {
						
					}
				} else {
					this.coordinador = id;
					notificarCoordinador();
					flagFinEleccion = true;
					estadoAcuerdo = Acuerdo.ACUERDO;
				}
			}
		}
		return 0;
	} 
	
	public void notifyResponse(int idIn) {
		escribirAConsola("Se me ha notificado " + idIn + " como nuevo controlador.");
		flagCoordinador = true;
		coordinador = idIn;
		synchronized(espera) {
			espera.notifyAll();
		}
	}
	
	public void notificarCoordinador() {
		
		escribirAConsola("Notificandome como coordinador");
		for(int i = 0; i < Controller.instance.getNumProc(); i++) {
			// Request a la url de notificación
			
			if(i == this.id) continue;
			
			request(Controller.getController().getUrlForID(i) + "notificarControlador?id=" + i + "&controlador=" + this.id);
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
				BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
				String output = "";
				String tmp;
				while((tmp = br.readLine()) != null) {
					output += tmp;
				}
				
				if(Integer.parseInt(output) == -1) return false;
				return true;
			}
			
		} catch(Exception ex) {
			return false;
		}
	}

	public int computar() {
		escribirAConsola("Recibida peticion de computar.");
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
		return lowerLimit + r.nextDouble(upperLimit-lowerLimit) +5;
	}

}
