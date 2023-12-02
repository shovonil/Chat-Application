import java.io.*;
import java.net.*;

import javax.swing.*;
import java.awt.*;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;


public class Client extends JFrame{

Socket socket;

BufferedReader br;
PrintWriter out;

private JLabel heading=new JLabel("Client Area");
private JTextArea messageArea=new JTextArea();
private JTextField messageInput=new JTextField();
private Font font=new Font("Roboto",Font.PLAIN,20);

public Client(){

	try{
		System.out.println("sending request to server");
		socket = new Socket("127.0.0.1",7778);
		System.out.println("connection done...");


		br=new BufferedReader(new InputStreamReader(socket.getInputStream()));
		out=new PrintWriter(socket.getOutputStream());

		createGUI();
		handleEvents();

		startReading();
		// startWriting();
	}catch(Exception e){
		e.printStackTrace();
	}
}

private void createGUI(){

	this.setTitle("Client Messager[END]");
	this.setSize(500,500);
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

}

private void handleEvents(){

	messageInput.addKeyListener(new KeyListener() {
    @Override
    public void keyTyped(KeyEvent e) {
        // Not used in this case, but needs to be implemented
    }

    @Override
    public void keyPressed(KeyEvent e) {
        // Handle key pressed event if needed
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            String contentToSend = messageInput.getText();
            messageArea.append("Me : " + contentToSend+"\n");
            out.println(contentToSend);
            out.flush();
            messageInput.setText("");
            messageInput.requestFocus();
        }
    }
});
}

public void startReading(){

	Runnable r1=()->{

		System.out.println("reader started...");

		try{
		while(true){
		
			String msg=br.readLine();
			if(msg.equals("exit")){
				System.out.println("Server terminate the chat");
				JOptionPane.showMessageDialog(this,"Server Terminated the chat");
				messageInput.setEnabled(false);
				socket.close();
				break;
			}
			// System.out.println("Server : "+msg);
			messageArea.append("Server : "+msg+"\n");
		
		}
	}catch(Exception e){
		e.printStackTrace();
	}

	};
	new Thread(r1).start();
}

public void startWriting(){

	Runnable r2=()->{

		System.out.println("writer started...");

		try{
		while(!socket.isClosed()){
			
				BufferedReader br1=new BufferedReader(new InputStreamReader(System.in));
				String content=br1.readLine();
				out.println(content);
				out.flush();

				if(content.equals("exit")){

					socket.close();
					break;
				}

			
		}
	}catch(Exception e){
		e.printStackTrace();
	}
	};
	new Thread(r2).start();
}


	public static void main(String[] args) {
		
		System.out.println("Client side...");
		new Client();
	
}}