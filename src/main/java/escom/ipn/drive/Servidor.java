package escom.ipn.drive;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.StandardSocketOptions;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Servidor {

  private static Socket socket;
  private static Scanner sc = new Scanner(System.in);

  public static void main(String[] args) {

    downloadFile(getServerSocket(1234), createFile());

  }

  private static ServerSocket getServerSocket(int port) {

    ServerSocket s = null;

    try {
      s = new ServerSocket(port);
      s.setOption(StandardSocketOptions.SO_REUSEADDR, true);
    } catch (IOException ex) {
      Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
    }

    return s;
  }

  private static String createFile() {
    File f = new File("");
    String ruta = f.getAbsolutePath();
    String carpeta = "archivos";
    String ruta_archivos = "";
    
    if(System.getProperty("os.name").equals("Windows")) {
        ruta_archivos = ruta+"\\"+carpeta+"\\";
    }else {
        ruta_archivos = ruta + "/" + carpeta + "/";
    }
    System.out.println("ruta:" + ruta_archivos);
    
    File f2 = new File(ruta_archivos);
    f2.mkdirs();
    f2.setWritable(true);

    return ruta_archivos;
  }

  public static void listFiles(String ruta_archivos, int level) {

    File f = new File(ruta_archivos);
    File[] ficheros = f.listFiles();

    for (File fichero : ficheros) {
        for (int i = 0; i < level; i++) {
            System.out.print("\t");
        }
        System.out.println(fichero.getName());
        if(fichero.isDirectory()) {
            listFiles(fichero.getPath(), level+1);
        }
    } 
  }

  private static void downloadFile(ServerSocket s, String ruta_archivos) {
    for (;;) {
        System.out.println("Servidor en espera, selecciona una opcion: \n");
        System.out.println("1. Listar archivos");
        System.out.println("2. Esperar");
        System.out.println("3. Salir");
        System.out.print("Su opcion: ");
        switch(sc.nextInt()) {
            case 1:
                listFiles(ruta_archivos, 0);
                break;
            case 2:
                break;
            case 3:
                return;
        }
      try {
        Socket cl = s.accept();

        DataInputStream dis = new DataInputStream(cl.getInputStream());

        System.out.println("\nCliente conectado desde " + cl.getInetAddress() + ":" + cl.getPort());
        
        String nombre = dis.readUTF();
        long tam = dis.readLong();
        String parent = dis.readUTF();

        DataOutputStream dos = null;

        if (parent.equals("")) {
          System.out.println("Comienza descarga del archivo: " + nombre + " de " + tam + " bytes\n\n");
          dos = new DataOutputStream(new FileOutputStream(ruta_archivos + nombre));
        } else {
          File f2 = new File(ruta_archivos + "/" + parent + "/");
          f2.mkdirs();
          f2.setWritable(true);

          System.out.println("Comienza descarga del archivo: " + parent + "/" + nombre + " de " + tam + " bytes\n\n");
          dos = new DataOutputStream(new FileOutputStream(ruta_archivos + "/" + parent + "/" + nombre));
        }

        long recibidos = 0;
        int l = 0, porcentaje = 0;
        
        while (recibidos < tam) {
          byte[] b = new byte[1500];
          l = dis.read(b);
          
          System.out.println("Leidos: " + l);
          
          dos.write(b, 0, l);
          dos.flush();
          recibidos = recibidos + l;
          porcentaje = (int)((recibidos * 100) / tam);
          
          System.out.println("\rRecibido el " + porcentaje + " % del archivo");
        } 
        System.out.println("\nArchivo recibido...");
        System.out.println("\nListado de archivos: ");
        listFiles(ruta_archivos, 0);
        
        dos.close();
        dis.close();
        cl.close();

      }
      catch (IOException ex) {
        Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
      }
    }
  }
}