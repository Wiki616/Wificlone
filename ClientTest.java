import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.util.*;

public class ClientTest {
    private ClientSocket cs = null;

    private static String ip = "192.168.1.110";

    private int port = 8888;

    private String sendMessage = "Windwos";

    public ClientTest() {
        try {
            if (createConnection()) {
                sendMessage();
                getMessage();
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private boolean createConnection() {
        cs = new ClientSocket(ip, port);
        try {
            cs.CreateConnection();
            System.out.print("Connect success!" + "\n");
            return true;
        } catch (Exception e) {
            System.out.print("Connect failure!" + "\n");
            return false;
        }

    }

    private void sendMessage() {
        if (cs == null)
            return;
        try {
            cs.sendMessage(sendMessage);
        } catch (Exception e) {
            System.out.print("send message failure!" + "\n");
        }
    }

    private void getMessage() {
        if (cs == null)
            return;
        DataInputStream inputStream = null;
        try {
            inputStream = cs.getMessageStream();
        } catch (Exception e) {
            System.out.print("\n");
            return;
        }

        try {
            String savePath = "D:\\Wificlone\\";
            int bufferSize = 8192;
            byte[] buf = new byte[bufferSize];
            int passedlen = 0;
            long len=0;
            
            savePath += inputStream.readUTF();
            DataOutputStream fileOut = new DataOutputStream(new BufferedOutputStream(new BufferedOutputStream(new FileOutputStream(savePath))));
            len = inputStream.readLong();
            
            System.out.println("file length:" + len + "\n");
            System.out.println("recive ok!" + "\n");
                    
            while (true) {
                int read = 0;
                if (inputStream != null) {
                    read = inputStream.read(buf);
                }
                passedlen += read;
                if (read == -1) {
                    break;
                }
                System.out.println("receive" +  (passedlen * 100/ len) + "%\n");
                fileOut.write(buf, 0, read);
            }
            System.out.println("receive complete£¬save as" + savePath + "\n");

            fileOut.close();
        } catch (Exception e) {
            System.out.println("error" + "\n");
            return;
        }
    }

    public static void main(String arg[]) {
        Scanner cin = new Scanner(System.in);
	System.out.println("Please input the ip :");
	ip = cin.next();
        for (int i = 0;i < 4;i ++) 
		new ClientTest();
    }
}
