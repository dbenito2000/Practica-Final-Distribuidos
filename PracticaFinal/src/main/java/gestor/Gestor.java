package gestor;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

public class Gestor {
	
	
	static boolean salida = false;
	
	public static void main(String[] args) {
		
		ArrayList<String> maquinas = new ArrayList<String>();
		ArrayList<Integer> cantidades = new ArrayList<Integer>();
		
		Scanner sc = new Scanner(System.in);
		
		System.out.println("[*] Sistema gestor del algoritmo Bully\n[*] Para configurar el despliegue, introduzca las direcciones de las maquinas,\n[*] Introduzca q para terminar");
		
		while(!salida) {
			System.out.println("[*] Introduzca la dirección de la máquina (ip:puerto): ");
			String tmp = sc.nextLine();
			
			if(tmp.equals("q")) {
				salida = true;
				break;
			} else {
				maquinas.add(tmp);
			}
			
			System.out.println("[*] Introduzca la cantidad de procesos de la máquina: ");
			
			int cantidad = sc.nextInt();
			sc.nextLine();
			if(cantidad > 0) {
				cantidades.add(cantidad);
			}
		}
		
		System.out.println("[*] Iniciando el despliegue.");
		int total = 0;
		for(int i = 0; i < cantidades.size(); i++) {
			total+=cantidades.get(i);
		}
		
		
		
		int contadorID = 0;
		for(int i = 0; i < maquinas.size(); i++) {
			String direccionMaquina = maquinas.get(i);
			int cantidadMaquina = cantidades.get(i);
			
			request("http://" + direccionMaquina + "/PracticaFinal/rest/controller/setId?id=" + contadorID + "&cantidad=" + cantidadMaquina + "&total=" + total);
			int contadorTemporal = 0;
			for(int n = 0; n < maquinas.size(); n++) {
				
				request("http://" + direccionMaquina + "/PracticaFinal/rest/controller/addMachine?address=" + maquinas.get(n) + "&id=" + contadorTemporal + "&cantidad=" + cantidades.get(i));
				contadorTemporal += cantidades.get(i);
			}
		}
		
		System.out.println("[*] Maquinas configuradas con el despliegue actual, iniciando...");
		
		for(int i = 0; i < maquinas.size(); i++) {
			String direccionMaquina = maquinas.get(i);
			request("http://" + direccionMaquina + "/PracticaFinal/rest/controller/start");
		}
		
		System.out.println("[*] Algoritmo Iniciado, seleccione una opcion para realizar una operación:");
		salida = false;
		while(!salida) {
			
			System.out.println("======\n[*] Menu Principal\n\"======");
			System.out.println("[*] p - Parar proceso n dado como argumento (EJ: p 3)");
			System.out.println("[*] i - Iniciar proceso n dado como argumento (EJ: i 3)");
			System.out.println("[*] q - Salir");
			
			String opcion = sc.nextLine();
			
			
			switch(opcion.charAt(0)) {
				case 'q':
					salida = true;
					break;
				case 'p':
					int id = Integer.parseInt(opcion.trim().split(" ")[1]);
					System.out.println("[*] Parando proceso " + id);
					int contador = 0;
					for(int i = 0; i < cantidades.size(); i++) {
						contador += cantidades.get(i);
						if(id < contador) {
							pararProceso(maquinas.get(i), id);
							break;
						}
					}
					
					break;
				case 'i':
					int idInit = Integer.parseInt(opcion.trim().split(" ")[1]);
					System.out.println("[*] Iniciando proceso " + idInit);
					int contadorInit = 0;
					for(int i = 0; i < cantidades.size(); i++) {
						contadorInit += cantidades.get(i);
						if(idInit < contadorInit) {
							iniciarProceso(maquinas.get(i), idInit);
							break;
						}
					}
					
					break;
			}
		}
		
		
		
		
		sc.close();
	}

	
	public static void pararProceso(String direccion, int id) {
		request("http://" + direccion + "/PracticaFinal/rest/controller/pararProceso?id=" + id);
	}
	
	public static void iniciarProceso(String direccion, int id) {
		request("http://" + direccion + "/PracticaFinal/rest/controller/iniciarProceso?id=" + id);
	}
	
	public static String request(String urlIn) {
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
				System.out.println(output);
				return output;
			}
			
		} catch(Exception ex) {
			return "";
		}
	}
}
