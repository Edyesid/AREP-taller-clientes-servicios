package edu.escuelaing.arep.spark;

import java.io.IOException;

import edu.escuelaing.arep.httpserver.HttpServer;
/**
 * Iniciar el servicio similar al framework spark
 * @author Edwin
 *
 */
public class Spark {
	/**
	 * Crea e inicia el servicio httpServer
	 * @param args argumentos
	 * @throws IOException si hay error en la entrada o salida
	 */
	public static void main(String[] args) throws IOException {
		HttpServer HttpServer = new HttpServer();
		HttpServer.start();
	}
}
