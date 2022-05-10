package util;

public class Maquina {
	
	public String direccion;
	public int id;
	public int procesos;
	
	public Maquina(String direccionIn, int idIn, int procesosIn) {
		this.direccion = direccionIn;
		this.id = idIn;
		this.procesos = procesosIn;
	}
	
	public boolean idInMachine(int idIn) {
		if (idIn - id >= procesos || idIn - id < 0) {
			return false;
		}
		return true;
	}
	
	public String getAddress() {
		return this.direccion;
	}
}
