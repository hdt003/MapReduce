import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) throws IOException, ClassNotFoundException {

        // Read input from file
        Scanner scanner = new Scanner(new File("input.txt"));
        String inputString = scanner.nextLine();

        // Split input string into words array
        String[] words = inputString.split("\\s+");

        // Split words array into three parts
        int partSize = words.length / 3;
        String[] words1 = new String[partSize];
        String[] words2 = new String[partSize];
        String[] words3 = new String[words.length - (2 * partSize)];
        System.arraycopy(words, 0, words1, 0, partSize);
        System.arraycopy(words, partSize, words2, 0, partSize);
        System.arraycopy(words, partSize * 2, words3, 0, words.length - (2 * partSize));

        // Create sockets and connect to servers
        Socket socket1 = new Socket("192.168.0.106", Integer.parseInt(args[0]));
        Socket socket2 = new Socket("192.168.0.106", Integer.parseInt(args[1]));
        Socket socket3 = new Socket("192.168.0.106", Integer.parseInt(args[2]));

        // Create ObjectOutputStream and ObjectInputStream for socket1
        ObjectOutputStream oos1 = new ObjectOutputStream(socket1.getOutputStream());
        ObjectInputStream ois1 = new ObjectInputStream(socket1.getInputStream());

        // Create ObjectOutputStream and ObjectInputStream for socket2
        ObjectOutputStream oos2 = new ObjectOutputStream(socket2.getOutputStream());
        ObjectInputStream ois2 = new ObjectInputStream(socket2.getInputStream());

        // Create ObjectOutputStream and ObjectInputStream for socket3
        ObjectOutputStream oos3 = new ObjectOutputStream(socket3.getOutputStream());
        ObjectInputStream ois3 = new ObjectInputStream(socket3.getInputStream());

        // Send words1 to server 1
        System.out.println("\033[33m"+"\n\t - Sending words to server 1...");
        oos1.writeObject(words1);
        oos1.flush();

        // Send words2 to server 2
        System.out.println("\t - Sending words to server 2...");
        oos2.writeObject(words2);
        oos2.flush();

        // Send words3 to server 3
        System.out.println("\t - Sending words to server 3...");
        oos3.writeObject(words3);
        oos3.flush();

        // Receive word counts from server 1
        Map<String, Integer> wordCount1 = (Map<String, Integer>) ois1.readObject();
        System.out.println("\t - Received the output from server 1");

        // Receive word counts from server 2
        Map<String, Integer> wordCount2 = (Map<String, Integer>) ois2.readObject();
        System.out.println("\t - Received the output from server 2");

        // Receive word counts from server 3
        Map<String, Integer> wordCount3 = (Map<String, Integer>) ois3.readObject();
        System.out.println("\t - Received the output from server 3"+"\033[0m");
        
        // Merge word counts from all three servers
        //shuffle and reduce
        Map<String, Integer> wordCount = new HashMap<>();
        for (String word : wordCount1.keySet()) {
            wordCount.put(word, wordCount.getOrDefault(word, 0) + wordCount1.get(word));
        }
        for (String word : wordCount2.keySet()) {
            wordCount.put(word, wordCount.getOrDefault(word, 0) + wordCount2.get(word));
        }
        for (String word : wordCount3.keySet()) {
            wordCount.put(word, wordCount.getOrDefault(word, 0) + wordCount3.get(word));
        }
        // Print word counts
        System.out.println("\033[31m"+"\tOutput:"+"\033[0m");
        System.out.println("\t\033[34m"+String.format("%-12s", "Word")+ " -->  Count"+"\033[0m");
        for (String word : wordCount.keySet()) {
            System.out.println("\t\033[32m"+String.format("%-12s", word) + " -->  " + wordCount.get(word)+"\033[0m");
        }

        // Close sockets and streams
        oos1.close();
        ois1.close();
        oos2.close();
        ois2.close();
        oos3.close();
        ois3.close();
        socket1.close();
        socket2.close();
        socket3.close();
    }
}