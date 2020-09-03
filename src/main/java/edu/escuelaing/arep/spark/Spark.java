package edu.escuelaing.arep.spark;

import java.io.IOException;

import edu.escuelaing.arep.httpserver.HttpServer;

public class Spark {
	
	public static void main(String[] args) throws IOException {
		HttpServer HttpServer = new HttpServer();
		HttpServer.start();
	}
}
