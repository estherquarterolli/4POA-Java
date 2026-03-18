package cep;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Principal {

    public static void main(String[] args) {
        try {
          
            HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
            
            // Define a rota "/cadastro"
            server.createContext("/cadastro", new FormularioHandler());
            server.setExecutor(null); 
            server.start();
            
            System.out.println("Servidor web da Zabeth's Gourmet rodando!");
            System.out.println("Acesse no seu navegador: http://localhost:8080/cadastro");
            
        } catch (IOException e) {
            System.out.println("Erro ao iniciar o servidor: " + e.getMessage());
        }
    }

    // Classe que lida com as requisições (mostrar a tela e salvar os dados)
    static class FormularioHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            String metodo = t.getRequestMethod();

            if ("GET".equalsIgnoreCase(metodo)) {
                // Quando o usuário acessa pelo navegador, o Java lê o arquivo HTML e envia pra ele
                try {
                    byte[] paginaHtml = Files.readAllBytes(Paths.get("formulario.html"));
                    t.getResponseHeaders().add("Content-Type", "text/html; charset=UTF-8");
                    t.sendResponseHeaders(200, paginaHtml.length);
                    
                    OutputStream os = t.getResponseBody();
                    os.write(paginaHtml);
                    os.close();
                } catch (IOException e) {
                    String erro = "Erro: Arquivo formulario.html não encontrado na raiz do projeto (pasta AP6).";
                    t.sendResponseHeaders(404, erro.length());
                    OutputStream os = t.getResponseBody();
                    os.write(erro.getBytes());
                    os.close();
                }
                
            } else if ("POST".equalsIgnoreCase(metodo)) {
                // Quando o usuário clica em "Salvar", o Java recebe os dados aqui
                Scanner scanner = new Scanner(t.getRequestBody(), "UTF-8").useDelimiter("\\A");
                String dadosBrutos = scanner.hasNext() ? scanner.next() : "";
                scanner.close();

                // Corta o texto que chegou e separa cada campo (nome, rua, cep, etc)
                Map<String, String> dados = parseFormData(dadosBrutos);

                // Monta a tela de sucesso confirmando que os dados chegaram separados
                String resposta = "<html><body style='font-family: Arial; margin: 40px; background-color: #fff0f5;'>" +
                        "<div style='background: white; padding: 30px; border-radius: 10px; max-width: 500px; margin: auto; box-shadow: 0 4px 15px rgba(0,0,0,0.1);'>" +
                        "<h2 style='color: #d63384; text-align: center;'>Cadastro Salvo com Sucesso!</h2>" +
                        "<p style='text-align: center;'>O servidor Java da AP6 recebeu os seguintes dados:</p>" +
                        "<ul style='line-height: 1.8; color: #333;'>" +
                        "<li><b>Nome:</b> " + dados.getOrDefault("nome", "") + "</li>" +
                        "<li><b>Telefone:</b> " + dados.getOrDefault("telefone", "") + "</li>" +
                        "<li><b>E-mail:</b> " + dados.getOrDefault("email", "") + "</li>" +
                        "<li><b>Endereço de Entrega:</b> " + dados.getOrDefault("rua", "") + ", " + 
                              dados.getOrDefault("numero", "") + " - " + 
                              dados.getOrDefault("complemento", "") + "</li>" +
                        "<li><b>Bairro:</b> " + dados.getOrDefault("bairro", "") + "</li>" +
                        "<li><b>Cidade/UF:</b> " + dados.getOrDefault("cidade", "") + " / " + 
                              dados.getOrDefault("estado", "") + "</li>" +
                        "<li><b>CEP:</b> " + dados.getOrDefault("cep", "") + "</li>" +
                        "</ul>" +
                        "<div style='text-align: center; margin-top: 20px;'>" +
                        "<a href='/cadastro' style='padding: 10px 20px; background: #d63384; text-decoration: none; color: white; border-radius: 5px; font-weight: bold;'>Voltar</a>" +
                        "</div></div></body></html>";
                
                byte[] bytes = resposta.getBytes(StandardCharsets.UTF_8);
                t.getResponseHeaders().add("Content-Type", "text/html; charset=UTF-8");
                t.sendResponseHeaders(200, bytes.length);
                
                OutputStream os = t.getResponseBody();
                os.write(bytes);
                os.close();
            }}

        // Tradutor que pega a linha inteira do formulário e separa as chaves e valores
        private Map<String, String> parseFormData(String formData) throws UnsupportedEncodingException {
            Map<String, String> map = new HashMap<>();
            if (formData == null || formData.trim().isEmpty()) {
                return map;
            }
            String[] pares = formData.split("&");
            for (String par : pares) {
                String[] chaveValor = par.split("=");
                if (chaveValor.length == 2) {
                    String chave = URLDecoder.decode(chaveValor[0], "UTF-8");
                    String valor = URLDecoder.decode(chaveValor[1], "UTF-8"); 
                    map.put(chave, valor);
                } else if (chaveValor.length == 1) {
                    String chave = URLDecoder.decode(chaveValor[0], "UTF-8");
                    map.put(chave, "");
                }}
            return map;
        }}}