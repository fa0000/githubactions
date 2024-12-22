package Serveur;
import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import java.util.Map;

import static java.util.Map.entry;


public class ClientHandler extends Thread { // pour traiter la demande de chaque client sur un socket particulier
    private Socket socket;
    private int clientNumber;
    static File path = new File(".");

    public ClientHandler(Socket socket, int clientNumber) {
        this.socket = socket;
        this.clientNumber = clientNumber;
        System.out.println("New connection with client#" + clientNumber + " at " + socket);
    }




    public void run() { // Création de thread qui envoi un message à un client
        try {
            DataOutputStream out = new DataOutputStream(socket.getOutputStream()); // création de canal d’envoi
            out.writeUTF("Hello from server - you are client#" + clientNumber); // envoi de message
            //recevoir les messages du client
            String message = "";
            String argument;
            DataInputStream in = new DataInputStream(socket.getInputStream());
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd@HH:mm:ss");
            while(!message.equals("exit")){
                try{
                    message = in.readUTF();

                    String[] splitCommande = message.split(" ");
                    if (splitCommande.length == 2 && splitCommande[0].equals("cd"))
                    {
                        argument = splitCommande[1];
                        path = Utilites.handleCD(path, argument);
                        out.writeUTF("end");
                    }
                    else if (splitCommande.length == 2 && splitCommande[0].equals("upload"))
                    {
                        out.writeUTF("upload");
                        argument = splitCommande[1];
                        System.out.println(argument + " to server path: " + path.getPath());
                        Utilites.recieveData(in, argument, path.getPath());
                        out.writeUTF("end");
                    }
                    else if (splitCommande.length == 2 && splitCommande[0].equals("download"))
                    {
                        out.writeUTF("download");
                        argument = splitCommande[1];
                        Utilites.sendData(out, new File(path.getPath() + File.separator + argument));
                        out.writeUTF("end");
                    }
                    else if (splitCommande.length == 2 && splitCommande[0].equals("mkdir"))
                    {
                        argument = splitCommande[1];
                        Utilites.createFolder(path.getPath(), argument, out);
                    }
                    else if (splitCommande.length == 1 && splitCommande[0].equals("ls")){
                        Utilites.listFilesForFolder(path, out);
                    }
                    else if (message.equals("exit")){}
                    else{
                        out.writeUTF("Commande inconnu.");
                        out.writeUTF("end");
                    }


                    LocalDateTime now = LocalDateTime.now();
                    System.out.format("[%s - %s] : %s\n", socket.getRemoteSocketAddress().toString().substring(1), dtf.format(now), message);
                }
                catch (IOException i){
                    System.out.println(i);
                }
            }


        } catch (IOException e) {
            System.out.println("Error handling client# " + clientNumber + ": " + e);
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                System.out.println("Couldn't close a socket");
            }
            System.out.println("Connection with client# " + clientNumber + " closed");
        }
    }
}