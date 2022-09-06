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
import java.util.logging.Level;
import java.util.logging.Logger;

public class Servidor {

  private static Socket socket;

  public static void main(String[] args) {

    DataOutputStream output;
    BufferedInputStream bis;
    BufferedOutputStream bos;

    byte[] receivedData;
    int in ;
    String file;

    downloadFile(getServerSocket(1234), createFile());
    
  }
  
  private static ServerSocket getServerSocket(int port) {
      
      ServerSocket s = null;
      
      try {
          s = new ServerSocket(1234);
          s.setOption(StandardSocketOptions.SO_REUSEADDR, true);
      } catch (IOException ex) {
          Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
      }
      
      return s;
  }
  
  private static String createFile(){
    File f = new File("");
    String ruta = f.getAbsolutePath();
    String carpeta="archivos";
    String ruta_archivos = ruta+"\\"+carpeta+"\\";
    System.out.println("ruta:"+ruta_archivos);
    File f2 = new File(ruta_archivos);
    f2.mkdirs();
    f2.setWritable(true);

    return ruta_archivos;
  }
  
  private static void listFiles(DataOutputStream dos, String ruta_archivos) {
      try {
          String listadoFicheros = "";
          
          File f = new File(ruta_archivos);
          File[] ficheros = f.listFiles();
          
          for (int x=0;x<ficheros.length;x++){
              if(listadoFicheros.equals("")) {
                  listadoFicheros = ficheros[x].getName();
              } else {
                  listadoFicheros = listadoFicheros + ";" + ficheros[x].getName();
              }
              
          }
          dos.writeUTF(listadoFicheros);
      } catch (IOException ex) {
          Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
      }
  }
  
  private static void downloadFile(ServerSocket s, String ruta_archivos){
      for(;;){
          try {
              Socket cl = s.accept();
              System.out.println("Cliente conectado desde "+cl.getInetAddress()+":"+cl.getPort());
              DataInputStream dis = new DataInputStream(cl.getInputStream());
              String nombre = dis.readUTF();
              long tam = dis.readLong();
              System.out.println("Comienza descarga del archivo: "+nombre+" de "+tam+" bytes\n\n");
              DataOutputStream dos = new DataOutputStream(new FileOutputStream(ruta_archivos+nombre));
              long recibidos=0;
              int l=0, porcentaje=0;
              while(recibidos<tam){
                  byte[] b = new byte[1500];
                  l = dis.read(b);
                  System.out.println("leidos: "+l);
                  dos.write(b,0,l);
                  dos.flush();
                  recibidos = recibidos + l;
                  porcentaje = (int)((recibidos*100)/tam);
                  System.out.print("\rRecibido el "+ porcentaje +" % del archivo");
              }//while
              System.out.println("Archivo recibido...");
              System.out.println("\nListado de archivos: ");
              listFiles(dos, ruta_archivos);
              dos.close();
              dis.close();
              cl.close();
          } //for
          catch (IOException ex) {
              Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
          }
          }
  }
}