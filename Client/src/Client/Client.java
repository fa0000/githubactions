package Client;

import java.io.*;
import java.net.Socket;

//application client
public class Client {
    private static Socket socket;


    public static void main(String[] args) throws Exception {
        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
        String serverAddress = Utilites.askIP();
        int port = Utilites.askPort();

        //connection au server
        socket = new Socket(serverAddress, port);
        System.out.format("Serveur lancé sur [%s:%d]\n", serverAddress, port);

        //creation canal input
        DataInputStream in = new DataInputStream(socket.getInputStream());

        //creation canal output
        DataOutputStream out = new DataOutputStream(socket.getOutputStream());

        // Attente de la réception d'un message envoyé par le, server sur le canal
        String connectionMsg = in.readUTF();
        System.out.println(connectionMsg);

        File path = new File(".");

        //read input et envoi au serveur
        String line = "";
        String reponse = "";
        while(true){
            try{
                line = input.readLine();
                out.writeUTF(line);
                if(line.equals("exit")){
                    break;
                }
                while(!reponse.equals("end")){
                    reponse = in.readUTF();
                    if(reponse.equals("download")){
                        System.out.println("recieving data to " + path.getPath());
                        Utilites.recieveData(in, line.split(" ")[1], path.getPath());
                    }
                    else if(reponse.equals("upload")){
                        Utilites.sendData(out, new File(line.split(" ")[1]));
                    }
                    else if(!reponse.equals("end")){
                        System.out.println(reponse);
                    }
                }
                reponse = "";
            }
            catch(IOException e){
                System.out.println(e);
            }
        }
        // fermeture de La connexion avec le serveur
        System.out.println("Connection to server closed");
        socket.close();
    }
}