import java.io.*;
import java.net.*;
import java.util.*;

public class Cliente {
    @SuppressWarnings("unchecked")
    public static void main(String[] args) {

        String host = "localhost";
        int PORTATCP = 8080;
        int PORTAUDP = 5000;
        Scanner sc = new Scanner(System.in);
        String nomeCliente;

        // enviar uma requisicao ao servidor usando UDP para receber um nome
        try (DatagramSocket clientSocket = new DatagramSocket()) {
            InetAddress serverAddress = InetAddress.getByName(host); // Pegar o endereco que o servidor esta rodando

            // Enviar a requisicao para o servidor
            byte[] sendData = "REQUEST".getBytes();
            // preparar a requisicao para endereco e porta do servidor UDP
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverAddress, PORTAUDP);
            // enviar a requisicao
            clientSocket.send(sendPacket);
            System.out.println("Requisicao UDP enviada com sucesso !");

            // vetor para receber o nome do servidor
            byte[] receiveData = new byte[1024];
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

            // receber o nome do servidor
            clientSocket.receive(receivePacket);

            // atribuir o nome recebido ao cliente
            nomeCliente = new String(receivePacket.getData(), 0, receivePacket.getLength()).trim();
            System.out.println("Seu nome atribuido e: Usuario " + nomeCliente);

        } catch (IOException e) {
            System.out.println("Erro ao se conectar com o servidor UDP");
            e.printStackTrace();
        }

        // inicializar conexao TCP
        try (Socket socket = new Socket(host, PORTATCP); // estabelecer uma conexao TCP no localhost porta 8080
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                //BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                //input stream de objetos para receber a lista resposta do servidor
                ObjectInputStream input = new ObjectInputStream(socket.getInputStream())) {
            String message;

            System.out.println("Conexão TCP estabelecida com sucesso");

            while (true) {
                message = sc.nextLine();
                
                // enviar a mensagem para o servidor
                out.println(message);
                System.out.println("Estas sao todas as possiveis palavras originadas desta cifra");

                //lista response recebe as respostas do servidor via input stream
                ArrayList<String> response = (ArrayList<String>) input.readObject(); // lista
                
                //imprimir a lista de respostas
                for (String s : response){
                    System.out.println(s);
                }

            }
        }

        catch (IOException e) {
            System.out.println("erro ao se conectar com o servidor TCP");
        } 
        
        catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        sc.close();
    }

}
