import java.io.*;
import java.net.*;
import java.util.*;

public class Server {
    public static void main(String[] args) throws Exception {
        int port = Integer.parseInt(args[0]);
        ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("\033[34m"+"\t- Server listening on port " + port+"\033[0m");
        while (true) {
            Socket socket = serverSocket.accept();
            System.out.println("\n\033[34m"+"\t-> Accepted connection from " + socket.getInetAddress()+"\033[0m");
            new Worker(socket).start();
        }
    }

    private static class Worker extends Thread {
        private final Socket socket;

        public Worker(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try {
                ObjectInputStream inFromClient = new ObjectInputStream(socket.getInputStream());
                ObjectOutputStream outToClient = new ObjectOutputStream(socket.getOutputStream());

                // Receive words array from client
                String[] words = (String[]) inFromClient.readObject();
                System.out.println("\033[34m"+"\t- Received " + words.length + " words from client " + socket.getInetAddress()+" :" +"\033[0m");
               for(int i = 0; i < words.length; i++)
               { System.out.println("\033[32m"+"\t\t"+words[i]+"\033[0m");
                 }

                // Count word frequencies
                Map<String, Integer> wordFreq = new HashMap<>();
                for (String word : words) {
                    wordFreq.put(word, wordFreq.getOrDefault(word, 0) + 1);
                }

                // Send word frequencies to client
                outToClient.writeObject(wordFreq);
                outToClient.flush();
                System.out.println("\033[34m"+"\t- Sent word frequencies to client " + socket.getInetAddress()+"\033[0m");

                // Close streams and socket
                inFromClient.close();
                outToClient.close();
                socket.close();
                System.out.println("\033[34m"+"\t- Closed connection with client " + socket.getInetAddress()+"\033[0m\n");
                System.out.println("--------------------------------------------------------------");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

