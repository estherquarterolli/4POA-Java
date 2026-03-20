package controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/DoceServlet")
public class DoceServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
 
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        
        request.setCharacterEncoding("UTF-8");

        // Captura os dados enviados pelo formulário JSP
        String nome = request.getParameter("nome");
        double preco = Double.parseDouble(request.getParameter("preco")); 
        String tipo = request.getParameter("tipo");
        String validade = request.getParameter("validade");

        
        String jdbcURL = "jdbc:postgresql://localhost:5432/zabeths_db"; 
        String dbUser = "postgres"; 
        String dbPassword = "1234"; 

        String mensagem;

        try {
            
            Class.forName("org.postgresql.Driver");
            Connection conexao = DriverManager.getConnection(jdbcURL, dbUser, dbPassword);

           
            String sql = "INSERT INTO doces (nome, preco, tipo, validade) VALUES (?, ?, ?, ?)";
            PreparedStatement statement = conexao.prepareStatement(sql);
            statement.setString(1, nome);
            statement.setDouble(2, preco);
            statement.setString(3, tipo);
            statement.setDate(4, java.sql.Date.valueOf(validade)); // Converte a String de data do HTML para Date do SQL

          
            statement.executeUpdate();
            conexao.close();
            
            mensagem = "Sucesso! O doce '" + nome + "' foi cadastrado no banco de dados.";
            
        } catch (Exception e) {
            mensagem = "Erro no banco de dados: " + e.getMessage();
            e.printStackTrace();
        }

        // Envia o resultado para a tela de resposta JSP
        request.setAttribute("resultado", mensagem);
        request.getRequestDispatcher("resultado.jsp").forward(request, response);
    }
}