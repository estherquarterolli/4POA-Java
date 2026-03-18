package cep;

import com.sun.net.httpserver.*;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

public class ServidorCadastro {

    public static void main(String[] args) {
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
            server.createContext("/cadastro", new FormularioHandler());
            server.setExecutor(null); 
            server.start();
            System.out.println("Servidor rodando: http://localhost:8080/cadastro");
        } catch (IOException e) {
            System.out.println("Erro ao iniciar: " + e.getMessage());
        }
    }

    static class FormularioHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            String metodo = t.getRequestMethod();

            if ("GET".equalsIgnoreCase(metodo)) {
                // Lê e envia o arquivo HTML
                byte[] paginaHtml = Files.readAllBytes(Paths.get("formulario.html"));
                responder(t, 200, paginaHtml);
                
            } else if ("POST".equalsIgnoreCase(metodo)) {
                // Lê os dados recebidos
                Scanner scanner = new Scanner(t.getRequestBody(), "UTF-8").useDelimiter("\\A");
                String dadosBrutos = scanner.hasNext() ? scanner.next() : "";
                
                // Separa os dados
                Map<String, String> dados = parseFormData(dadosBrutos);

                // Monta a resposta simplificada
                String resposta = "<h2>Cadastro Salvo!</h2>" +
                                  "<p>Nome: " + dados.getOrDefault("nome", "") + "</p>" +
                                  "<p>CEP: " + dados.getOrDefault("cep", "") + "</p>" +
                                  "<a href='/cadastro'>Voltar</a>";
                
                responder(t, 200, resposta.getBytes(StandardCharsets.UTF_8));
            }
        }


        // Função para evitar repetição de código na hora de enviar a resposta
        private void responder(HttpExchange t, int status, byte[] conteudo) throws IOException {
            t.getResponseHeaders().add("Content-Type", "text/html; charset=UTF-8");
            t.sendResponseHeaders(status, conteudo.length);
            try (OutputStream os = t.getResponseBody()) {
                os.write(conteudo);
            }
        }

        // Função simplificada para separar chaves e valores
        private Map<String, String> parseFormData(String formData) throws UnsupportedEncodingException {
            Map<String, String> map = new HashMap<>();
            if (formData == null || formData.isEmpty()) return map;
            
            for (String par : formData.split("&")) {
                String[] cv = par.split("=");
                if (cv.length > 0) {
                    String chave = URLDecoder.decode(cv[0], "UTF-8");
                    String valor = cv.length > 1 ? URLDecoder.decode(cv[1], "UTF-8") : "";
                    map.put(chave, valor);
                }
            }
            return map;
        }
    }
}