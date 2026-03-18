package controller; 

import com.sun.net.httpserver.*;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import model.Doce;

public class Principal {
    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        
        server.createContext("/doceria", (HttpExchange t) -> {
            if ("GET".equals(t.getRequestMethod())) {
                
                byte[] html = Files.readAllBytes(Paths.get("webapp/index.html"));
                enviar(t, html);
            } else {
                InputStreamReader isr = new InputStreamReader(t.getRequestBody(), "utf-8");
                BufferedReader br = new BufferedReader(isr);
                String query = br.readLine();
                
                String resposta = "<html><body><h2>Zabeth's Gourmet: Doce Cadastrado!</h2><p>" + 
                                  URLDecoder.decode(query, "UTF-8").replace("&", "<br>") + 
                                  "</p><a href='/doceria'>Voltar</a></body></html>";
                enviar(t, resposta.getBytes());
            }
        });

        server.start();
        System.out.println("Servidor da Zabeth's Gourmet rodando em: http://localhost:8080/doceria");
    }

    private static void enviar(HttpExchange t, byte[] resposta) throws IOException {
        t.sendResponseHeaders(200, resposta.length);
        try (OutputStream os = t.getResponseBody()) {
            os.write(resposta);
        }
    }
}