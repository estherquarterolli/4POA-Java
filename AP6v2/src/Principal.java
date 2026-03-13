
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

public class Principal {

    public static void main(String[] args) {
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
            server.createContext("/cadastro", new FormularioHandler());
            server.setExecutor(null); 
            server.start();
            
            System.out.println("Servidor rodando! Acesse: http://localhost:8080/cadastro");
        } catch (IOException e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

    static class FormularioHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            String metodo = t.getRequestMethod();

            if ("GET".equalsIgnoreCase(metodo)) {
                // Mostra o formulário na tela
                byte[] html = Files.readAllBytes(Paths.get("formulario.html"));
                t.getResponseHeaders().add("Content-Type", "text/html; charset=UTF-8");
                t.sendResponseHeaders(200, html.length);
                OutputStream os = t.getResponseBody();
                os.write(html);
                os.close();
                
            } else if ("POST".equalsIgnoreCase(metodo)) {
         
                Scanner scanner = new Scanner(t.getRequestBody(), "UTF-8").useDelimiter("\\A");
                String dados = scanner.hasNext() ? scanner.next() : "";
                scanner.close();

         
                String dadosDecodificados = URLDecoder.decode(dados, "UTF-8");

                String resposta = "<h2>Dados recebidos com sucesso no Java!</h2>" +
                                  "<p>O que chegou do formulário: <b>" + dadosDecodificados + "</b></p>" +
                                  "<a href='/cadastro'>Voltar</a>";
                
                byte[] bytes = resposta.getBytes(StandardCharsets.UTF_8);
                t.getResponseHeaders().add("Content-Type", "text/html; charset=UTF-8");
                t.sendResponseHeaders(200, bytes.length);
                OutputStream os = t.getResponseBody();
                os.write(bytes);
                os.close();
            }
        }
    }
}