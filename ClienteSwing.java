import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class ClienteSwing extends JFrame {
    private final String HOST = "localhost";
    private final int PORTATCP = 8080;
    private final int PORTAUDP = 5000;

    // Componentes da Interface
    private JLabel lblNomeUdp;
    private JTextField txtEntradaTcp;
    private JTextArea txtResultadoTcp;
    private JButton btnEnviarTcp;

    // Recursos de Rede TCP
    private Socket socketTCP;
    private PrintWriter outTCP;
    private ObjectInputStream inTCP;

    public ClienteSwing() {
        configurarInterface();
        iniciarConexoesDeRede();
    }

    private void configurarInterface() {
        setTitle("Cliente - Redes de Computadores I");
        setSize(550, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // --- PAINEL SUPERIOR (UDP) ---
        JPanel panelTopo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelTopo.setBorder(BorderFactory.createTitledBorder("Identificação (UDP)"));
        lblNomeUdp = new JLabel("Buscando nome no servidor...");
        lblNomeUdp.setFont(new Font("SansSerif", Font.BOLD, 14));
        lblNomeUdp.setForeground(new Color(46, 164, 79)); // Verde GitHub
        panelTopo.add(lblNomeUdp);
        add(panelTopo, BorderLayout.NORTH);

        // --- PAINEL CENTRAL (TCP) ---
        JPanel panelCentro = new JPanel(new BorderLayout(5, 5));
        panelCentro.setBorder(BorderFactory.createTitledBorder("Decodificador de Cifra de César (TCP)"));

        // Área de Input
        JPanel panelInput = new JPanel(new BorderLayout(5, 5));
        panelInput.add(new JLabel("Digite a string cifrada:"), BorderLayout.NORTH);
        
        txtEntradaTcp = new JTextField();
        // Permite enviar apertando 'Enter'
        txtEntradaTcp.addActionListener(e -> enviarMensagemTCP()); 
        panelInput.add(txtEntradaTcp, BorderLayout.CENTER);

        btnEnviarTcp = new JButton("Enviar requisição");
        btnEnviarTcp.setEnabled(false); // Só habilita quando o TCP conectar
        btnEnviarTcp.addActionListener(e -> enviarMensagemTCP());
        panelInput.add(btnEnviarTcp, BorderLayout.EAST);

        panelCentro.add(panelInput, BorderLayout.NORTH);

        // Área de Output (Resultados)
        txtResultadoTcp = new JTextArea();
        txtResultadoTcp.setEditable(false);
        txtResultadoTcp.setFont(new Font("Monospaced", Font.PLAIN, 13));
        JScrollPane scrollPane = new JScrollPane(txtResultadoTcp);
        panelCentro.add(scrollPane, BorderLayout.CENTER);

        add(panelCentro, BorderLayout.CENTER);

        // --- EVENTO DE FECHAMENTO ---
        // Garante que enviamos "sair" para o servidor fechar a thread dele graciosamente
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                encerrarConexaoTCP();
            }
        });
    }

    private void iniciarConexoesDeRede() {
        // 1. Thread dedicada para a requisição UDP
        new Thread(() -> {
            try (DatagramSocket udpSocket = new DatagramSocket()) {
                InetAddress serverAddress = InetAddress.getByName(HOST);
                
                // Envia requisição
                byte[] sendData = "REQUEST".getBytes();
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverAddress, PORTAUDP);
                udpSocket.send(sendPacket);

                // Aguarda resposta
                byte[] receiveData = new byte[1024];
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                udpSocket.receive(receivePacket);

                String nomeRecebido = new String(receivePacket.getData(), 0, receivePacket.getLength()).trim();
                
                // Atualiza a interface usando a EDT
                SwingUtilities.invokeLater(() -> {
                    lblNomeUdp.setText("Conectado via UDP: " + nomeRecebido);
                });

            } catch (IOException ex) {
                SwingUtilities.invokeLater(() -> lblNomeUdp.setText("Erro no UDP: " + ex.getMessage()));
            }
        }).start();

        // 2. Thread dedicada para estabelecer a conexão TCP inicial
        new Thread(() -> {
            try {
                socketTCP = new Socket(HOST, PORTATCP);
                outTCP = new PrintWriter(socketTCP.getOutputStream(), true);
                
                // O ObjectInputStream bloqueia até receber o cabeçalho do ObjectOutputStream do servidor
                inTCP = new ObjectInputStream(socketTCP.getInputStream());

                SwingUtilities.invokeLater(() -> {
                    txtResultadoTcp.append("[SISTEMA] Conexão TCP estabelecida na porta " + PORTATCP + ".\n");
                    btnEnviarTcp.setEnabled(true); // Libera a interface para o usuário
                });

            } catch (IOException ex) {
                SwingUtilities.invokeLater(() -> {
                    txtResultadoTcp.append("[ERRO] Falha ao conectar no servidor TCP: " + ex.getMessage() + "\n");
                });
            }
        }).start();
    }

    private void enviarMensagemTCP() {
        String mensagem = txtEntradaTcp.getText().trim();
        if (mensagem.isEmpty()) return;

        txtEntradaTcp.setText("");
        txtResultadoTcp.append("\n>> Enviando: " + mensagem + "\n");
        btnEnviarTcp.setEnabled(false); // Desabilita botão enquanto processa

        // Thread para aguardar a resposta TCP sem travar a interface
        new Thread(() -> {
            try {
                // Envia para o servidor
                outTCP.println(mensagem);

                // Fica aguardando a lista de volta via ObjectInputStream
                @SuppressWarnings("unchecked")
                ArrayList<String> resposta = (ArrayList<String>) inTCP.readObject();

                // Atualiza a interface com a resposta
                SwingUtilities.invokeLater(() -> {
                    txtResultadoTcp.append("<< Respostas recebidas:\n");
                    for (int i = 0; i < resposta.size(); i++) {
                        txtResultadoTcp.append(String.format("   Chave %02d: %s\n", i, resposta.get(i)));
                    }
                    txtResultadoTcp.setCaretPosition(txtResultadoTcp.getDocument().getLength()); // Rola o scroll pro final
                });

            } catch (IOException | ClassNotFoundException ex) {
                SwingUtilities.invokeLater(() -> txtResultadoTcp.append("[ERRO] Falha na comunicação: " + ex.getMessage() + "\n"));
            } finally {
                // Reabilita o botão independente do resultado
                SwingUtilities.invokeLater(() -> btnEnviarTcp.setEnabled(true));
            }
        }).start();
    }

    private void encerrarConexaoTCP() {
        try {
            if (outTCP != null) {
                outTCP.println("sair"); // Avisa ao servidor para encerrar o loop e a thread dele
            }
            if (socketTCP != null && !socketTCP.isClosed()) {
                socketTCP.close();
            }
        } catch (IOException ex) {
            System.err.println("Erro ao fechar os recursos de rede: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        // Inicia a interface gráfica de forma thread-safe
        SwingUtilities.invokeLater(() -> {
            ClienteSwing cliente = new ClienteSwing();
            // Centraliza a janela na tela
            cliente.setLocationRelativeTo(null); 
            cliente.setVisible(true);
        });
    }
}
