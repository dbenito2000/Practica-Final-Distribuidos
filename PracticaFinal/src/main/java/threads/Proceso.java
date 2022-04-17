package threads;

import java.util.Random;

import util.Acuerdo;
import util.Estado;

public class Proceso implements Runnable {

	// Variables
	
	int id;
	int coordinador;
	
	// Estados
	
	Acuerdo estadoAcuerdo;
	Estado estadoProceso = Estado.INACTIVO;
	
	// Generación de numeros aleatorios
	
	Random r = new Random();
	
	public Proceso(int idIn) {
		this.id = idIn;
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
	
	
	public double randomNumber(double lowerLimit, double upperLimit) {
		return lowerLimit + r.nextDouble(upperLimit-lowerLimit);
	}

}
