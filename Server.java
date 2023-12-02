import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Server extends JFrame {

    private ServerSocket server;
    private Socket socket;

    private BufferedReader br;
    private PrintWriter out;

    private JTextArea messageArea = new JTextArea();
    private JTextField messageInput = new JTextField();
    private Font font = new Font("Roboto", Font.PLAIN, 20);

    private JLabel heading = new JLabel("Server Area");

    public Server() {
        try {
            server = new ServerSocket(7778);
            System.out.println("Server is ready to accept connection");
            System.out.println("Waiting...");
            socket = server.accept();

            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream());

            createGUI();
            startReading();
            startWriting();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createGUI() {
        this.setTitle("Server Chat Console");
        this.setSize(500, 500);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        heading.setFont(new Font("Roboto", Font.PLAIN, 16));
    ImageIcon originalIcon = new ImageIcon("chat_icon.jpeg");

    Image originalImage = originalIcon.getImage();
    Image resizedImage = originalImage.getScaledInstance(30, 30, Image.SCALE_SMOOTH);
    ImageIcon smallIcon = new ImageIcon(resizedImage);

    heading.setIcon(smallIcon);
    JScrollPane scrollPane = new JScrollPane(messageArea);
    scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

    this.setLayout(new BorderLayout());

    this.add(heading, BorderLayout.NORTH);
    this.add(scrollPane, BorderLayout.CENTER); 
    this.add(messageInput, BorderLayout.SOUTH);

	this.setVisible(true);


        messageInput.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String content = messageInput.getText();
                messageArea.append("Server: " + content + "\n");
                out.println(content);
                out.flush();
                messageInput.setText("");
            }
        });

        this.setVisible(true);
    }

    private void startReading() {
        Runnable r1 = () -> {
            try {
                while (true) {
                    String msg = br.readLine();
                    if (msg.equals("exit")) {
                        System.out.println("Client terminated the chat");
                        break;
                    }
                    messageArea.append("Client: " + msg + "\n");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
        new Thread(r1).start();
    }

    private void startWriting() {
        Runnable r2 = () -> {
            try {
                while (!socket.isClosed()) {
                    BufferedReader br1 = new BufferedReader(new InputStreamReader(System.in));
                    String content = br1.readLine();
                    out.println(content);
                    out.flush();
                    if (content.equals("exit")) {
                        socket.close();
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
        new Thread(r2).start();
    }

    public static void main(String[] args) {
        System.out.println("Server going to start......");
        new Server();
    }
}