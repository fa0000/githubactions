package Client;

import java.io.*;
import java.util.stream.Collectors;
import java.util.*;

public class Utilites {
    static boolean checkAddressValidity(String txt){
        List<Character> chars = txt.chars().mapToObj(e -> (char)e).collect(Collectors.toList());
        for(int i = 0; i < chars.size(); i++){
            if( !(Character.isDigit(chars.get(i)) || chars.get(i).equals('.')) ){
                return false;
            }
        }
        return true;
    }

    static String askIP() throws IOException {
        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Veuillez entrer une adresse IP ");
        String txt = input.readLine();
        while(!Utilites.checkAddressValidity(txt)){
            System.out.println("Warning: invalide ");
            System.out.println("Veuillez entrer une adresse IP ");
            txt = input.readLine();
        }
        return txt;
    }

    static int askPort() throws IOException {
        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Veuillez entrer un port ");
        int port = Integer.parseInt(input.readLine());
        while((port > 5050 || port < 5000)){
            System.out.println("Warning: invalide ");
            System.out.println("Veuillez entrer un port entre 5000 et 5050 ");
            port = Integer.parseInt(input.readLine());
        }
        return port;
    }

    public static void sendData(DataOutputStream out, File upload){
        try{
            System.out.println("file length" +upload.length());
            FileInputStream fileInputStream = new FileInputStream(upload);
            out.writeLong(upload.length());
            byte[] bytes = new byte[(int)upload.length()];
            fileInputStream.read(bytes);
            out.write(bytes);
            fileInputStream.close();
            // out.close();
            System.out.println("File sent");
        }
        catch(Exception e){}
    }
    public static void recieveData(DataInputStream in, String fileName, String path){
        try{
            System.out.println("Recieving to " + path);
            String fileLocation = path; // file location in client
            int bytesRead = 0;
            FileOutputStream output = new FileOutputStream(fileLocation + File.separator + fileName);
            long size = in.readLong();
            byte[] buffer = new byte[4*1024];
            while (size > 0 && (bytesRead = in.read(buffer, 0, (int)Math.min(buffer.length, size))) != -1) {
                output.write(buffer,0,bytesRead);
                size -= bytesRead;      // read upto file size
            }
            output.close();
        }
        catch(Exception e ){

        }
    }


    static void createFolder(String folderName, String newFolder, DataOutputStream out) throws IOException {
        File folder = new File(folderName + "\\" + newFolder);
        if (folder.mkdir()==true)
        {
            out.writeUTF("Le dossier " + newFolder + " a été créé.");
        }
        else if (folder.exists())
        {
            out.writeUTF("Le dossier " + newFolder + " existe déjà.");
        }
        else {
            File fileFound = findParent(folder);
            if (fileFound == null) {
                out.writeUTF("La racine du dossier " + folder.getName()  + " n'existe pas.");
            }
            else
            {
                out.writeUTF("Le dossier " + fileFound.getName() + " n'existe pas.");
            }
        }
        out.writeUTF("end");
    }

    static File findParent(File file) //mkdir complement
    {
        if (file.getParentFile()==null)
        {
            return null;
        }
        else if(file.getParentFile().exists()) {
            return file;
        }
        else
        {
            return findParent(file.getParentFile());
        }
    }

    public static void listFilesForFolder(final File folder, DataOutputStream out) throws IOException {
        for (final File fileEntry : folder.listFiles()) {
            if (fileEntry.isDirectory()) {
                out.writeUTF("[Folder] " + fileEntry.getName());
            } else {
                out.writeUTF("[File] " + fileEntry.getName());
            }
        }
        out.writeUTF("end");
    }

    // Retourne un nouveau File du dossier
    public static File handleCD(File actualFile, String folderName) {
        if(folderName == ".."){
            return actualFile.getParentFile();
        }
        String newPath = actualFile.getPath() + File.separator + folderName;
        return new File(newPath);
    }





}

