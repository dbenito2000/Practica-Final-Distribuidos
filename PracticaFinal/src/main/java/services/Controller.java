package services;

import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;


@Singleton
@Path("controller")
public class Controller {
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	@Path("saludo")
	public String saludo() {
		return "Hola Gente!";
	}
}
