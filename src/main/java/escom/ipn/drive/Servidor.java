package escom.ipn.drive;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Servidor {
  public static void main(String[] args) {
    int pto = 1234;
    
    System.out.println("Servidor iniciado esperando por archivos..");
    
    ServerSocket s = getServerSocket(pto);

    Socket cl;

    try {
      for (;;) {
        cl = s.accept();

        System.out.println("Cliente conectado desde " + cl.getInetAddress() + ":" + cl.getPort());

        downloadFile(cl, getFile());
        cl.close();
      }
    } catch (IOException ex) {
      Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

  private static ServerSocket getServerSocket(int port) {

    ServerSocket s = null;
    try {
      s = new ServerSocket(port);
      s.setReuseAddress(true);
    } catch (IOException ex) {
      Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
    }

    return s;
  }

  private static String getFile() {
    File f = new File("");
    String ruta = f.getAbsolutePath();
    String carpeta = "archivos";
    String ruta_archivos = ruta + "\\" + carpeta + "\\";
    System.out.println("ruta:" + ruta_archivos);

    return ruta_archivos;
  }

  private static void downloadFile(Socket cl, String ruta_archivos) {
    try {
      DataInputStream dis = new DataInputStream(cl.getInputStream());
      String nombre = dis.readUTF();
      long tam = dis.readLong();
      System.out.println("Comienza descarga del archivo " + nombre + " de " + tam + " bytes\n\n");
      DataOutputStream dos = new DataOutputStream(new FileOutputStream(ruta_archivos + nombre));
      long recibidos = 0;
      int l = 0, porcentaje = 0;
      while (recibidos < tam) {
        byte[] b = new byte[1500];
        l = dis.read(b);
        System.out.println("leidos: " + l);
        dos.write(b, 0, l);
        dos.flush();
        recibidos = recibidos + l;
        porcentaje = (int)((recibidos * 100) / tam);
        System.out.print("\rRecibido el " + porcentaje + " % del archivo");
      }
      System.out.println("Archivo recibido..");
      dos.close();
      dis.close();
    } catch (IOException ex) {
      Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
    }

  }
}