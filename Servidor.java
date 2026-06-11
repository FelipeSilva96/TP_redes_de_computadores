import java.io.*;
import java.net.*;
import java.util.*;

public class Servidor {
    private static final int PORTATCP = 8080;
    private static final int PORTAUDP = 5000;

    public static void main(String[] args) {
        System.out.println("Iniciando o servidor");

        // 1. Thread para escutar requisições UDP
        new Thread(Servidor::ServidorUDP).start();

        // 2. Thread para escutar conexões TCP
        new Thread(Servidor::ServidorTCP).start();
    }

    // SERVIDOR UDP
    private static void ServidorUDP() {
        try (DatagramSocket udpSocket = new DatagramSocket(PORTAUDP)) {
            System.out.println("[UDP] Pronto na porta " + PORTAUDP);
            byte[] buffer = new byte[1024];

            while (true) {
                DatagramPacket pacoteRecebido = new DatagramPacket(buffer, buffer.length);
                udpSocket.receive(pacoteRecebido); // Aguarda requisição

                // Criar um nome único usando UUID
                String idUnico = UUID.randomUUID().toString().substring(0, 6);
                String nomeGerado = "Usuario - " + idUnico;
                System.out.println("nome gerado: " + nomeGerado);
                System.out.println("[UDP] Cliente solicitou nome. Gerado: " + nomeGerado);

                // Envia o nome de volta para o IP e Porta do cliente UDP
                byte[] dadosResposta = nomeGerado.getBytes();
                DatagramPacket pacoteResposta = new DatagramPacket(
                        dadosResposta,
                        dadosResposta.length,
                        pacoteRecebido.getAddress(),
                        pacoteRecebido.getPort());
                udpSocket.send(pacoteResposta);
            }
        } catch (IOException e) {
            System.err.println("[UDP] Erro: " + e.getMessage());
        }
    }

    // SERVIDOR TCP
    private static void ServidorTCP() {
        try (ServerSocket tcpSocket = new ServerSocket(PORTATCP)) {
            System.out.println("[TCP] Pronto na porta " + PORTATCP);

            while (true) {
                // Aguarda um cliente se conectar via TCP
                Socket clienteSocket = tcpSocket.accept();
                System.out.println("[TCP] Novo cliente conectado: " + clienteSocket.getRemoteSocketAddress());

                // Cria uma nova Thread exclusiva para cuidar deste cliente
                new Thread(new TratadorClienteTCP(clienteSocket)).start();
            }
        } catch (IOException e) {
            System.err.println("[TCP] Erro: " + e.getMessage());
        }
    }

    // --- PARTE 3: TRATADOR DE CLIENTE TCP (Executado em Threads separadas) ---
    private static class TratadorClienteTCP implements Runnable {

        private final Socket socket;

        public TratadorClienteTCP(Socket socket) {
            this.socket = socket;
        }

        @SuppressWarnings("deprecation")
        @Override
        public void run() {
            try (
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                    //output stream para retornar a lista de resposta
                    ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream())) {
                String linha;
                

                // while ((linha = in.readLine()) != null) {
                while (true) {
                    linha = in.readLine();
                    if (linha.equalsIgnoreCase("sair")) {
                        out.println("Conexão encerrada pelo servidor.");
                        break;
                    }

                    System.out.println("[TCP Thread-" + Thread.currentThread().getId() + "] Recebido: " + linha);

                    //lista para armazenar as cifras possiveis
                    List<String> cifras = new ArrayList<>();

                    //iterar sobre a string testando todas as cifras possiveis 
                    for (int i = 0; i < 26; i++) {
                        cifras.add(decode(i, linha));
                    }

                    //eviar a lista via output stream
                    output.writeObject(cifras);
                    output.flush();

                }
            } catch (IOException e) {
                System.err.println("[TCP Thread] Conexão perdida com o cliente: " + e.getMessage());
            } finally {
                try {
                    socket.close();
                    System.out.println("[TCP] Socket do cliente fechado.");
                } catch (IOException e) {
                    System.err.println("[TCP] Erro ao fechar socket: " + e.getMessage());
                }
            }
        }
    }

    // Decodificador da cifra de cesar
    static String decode(int chave, String cifra) {
        StringBuilder sBuilder = new StringBuilder();
        chave = chave % 26;

        for (char c : cifra.toCharArray()) {
            if ('A' <= c && c <= 'Z') { // caractere maiusculo
                char nc = (char) ('A' + (c - 'A' - chave + 26) % 26);
                sBuilder.append(nc);
            }

            else if ('a' <= c && c <= 'z') { // caractere minusculo
                char nc = (char) ('a' + (c - 'a' - chave + 26) % 26);
                sBuilder.append(nc);
            }

            else {
                sBuilder.append(c); // caracteres que nao sejam letras
            }

        }

        return sBuilder.toString();
    }
}
