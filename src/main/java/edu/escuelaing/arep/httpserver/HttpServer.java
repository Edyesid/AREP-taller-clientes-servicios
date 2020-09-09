package edu.escuelaing.arep.httpserver;
import org.apache.commons.io.FilenameUtils;

import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.*;
/**
 * Servicio http
 */
public class HttpServer {

	static Socket clientSocket = null;
	private boolean running;
	static PrintWriter printwriter;
	static BufferedReader in;

	public HttpServer() {
		running = false;
	}
	/**
	 * Ejecuta el servicio
	 */
	public void start() {
		try {
			ServerSocket serverSocket = null;
			try {
				serverSocket = new ServerSocket(getPort());
			} catch (IOException e) {
				System.err.println("Could not listen on port:" + getPort());
				System.exit(1);
			}
			
			running = true;

			while (running) {

				try {
					System.out.println("Listo para recibir en el puerto " + getPort() + ".....");
					clientSocket = serverSocket.accept();
				} catch (IOException e) {
					System.err.println("Accept failed.");
					System.exit(1);
				}
				
				processRequest(clientSocket);
				
				clientSocket.close();
			}
			
		} catch (IOException ex) {
            Logger.getLogger(HttpServer.class.getName()).log(Level.SEVERE, null, ex);
        }
	}
	/**
	 * Hace el proceso de respuesta de las diferentes peticiones
	 * @param clientSocket2 socket cliente de la peticion
	 * @throws IOException si hay error en la entrada o salida
	 */
	private void processRequest(Socket clientSocket2) throws IOException {
		
		printwriter = new PrintWriter(clientSocket2.getOutputStream(), true);
		in = new BufferedReader(new InputStreamReader(clientSocket2.getInputStream()));
		String inputLine, outputLine;

		StringBuilder stringBuilder = new StringBuilder();

		Pattern pattern = Pattern.compile("GET /([^\\s]+)");
		Matcher matcher = null;

		while ((inputLine = in.readLine()) != null) {
			System.out.println("Recib�: " + inputLine);
			stringBuilder.append(inputLine);
			if (!in.ready()) {
				matcher = pattern.matcher(stringBuilder.toString());
				if (matcher.find()) {
					String req = matcher.group().substring(5);
					System.out.println("VALUE: " + req);
					returnRequest(req);
				}

				break;
			}
		}

		printwriter.close();
		in.close();
		
	}
	/**
	 * Responde a las peticiones propuestas
	 * @param req solicitud
	 * @throws IOException si hay error en la entrada o salida
	 */
	public static void returnRequest(String req) throws IOException {

		String path = "src/main/resources/";
		String img = FilenameUtils.getExtension(req);
		if (img.equals("js")) {
			path = path + "js/";

		} else if (img.equals("png") || img.equals("jpg") || img.equals("gif"))  {
			path = path + "imagenes/";
		}

		System.out.println(path + req);
		File file = new File(path + req);

		if (file.exists() && !file.isDirectory()) {
			if (img.equals("png") || img.equals("jpg") || img.equals("gif")) {

				FileInputStream fis = new FileInputStream(file);
				byte[] data = new byte[(int) file.length()];
				fis.read(data);
				fis.close();

				DataOutputStream binaryOut = new DataOutputStream(clientSocket.getOutputStream());
				binaryOut.writeBytes("HTTP/1.0 200 OK\r\n");
				binaryOut.writeBytes("Content-Type: image/" + img + "\r\n");
				binaryOut.writeBytes("Content-Length: " + data.length);
				binaryOut.writeBytes("\r\n\r\n");
				binaryOut.write(data);

				binaryOut.close();

			} else {
				printwriter.println("HTTP/1.1 200 \r\nContent-Type: text/html\r\n\r\n");
				BufferedReader br = new BufferedReader(new FileReader(file));

				StringBuilder stringBuilder = new StringBuilder();
				String str;
				while ((str = br.readLine()) != null) {
					stringBuilder.append(str);
				}
				printwriter.println(stringBuilder.toString());
				br.close();
			}
		} else {
			printwriter.println("HTTP/1.1 404 \r\n\r\n<html><body><h1>ERROR 404: NOT FOUND</h1></body></html>");

		}

	}
	/**
	 * Retorna el puerto por el que corre el servicio
	 * @return port (36000)
	 */
	static int getPort() {
		if (System.getenv("PORT") != null) {
			return Integer.parseInt(System.getenv("PORT"));
		}

		return 36000;
	}

}