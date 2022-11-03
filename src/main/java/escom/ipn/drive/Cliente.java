package escom.ipn.drive;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;

public class Cliente {
    
  public static void main(String[] args) throws IOException {
      Scanner sc = new Scanner(System.in);
      while(true){
        System.out.println("Cliente en espera, selecciona una opcion: \n");
        System.out.println("1. Subir archivos");
        System.out.println("2. Salir");
        System.out.println("Su opcion: ");
        switch(sc.nextInt()) {
            case 1:
                subir();
                break;
            case 2:
                return;
        }
      }
      
  }

  public static void subir() throws IOException {
    JFileChooser jf = new JFileChooser();
    jf.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

    if (jf.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
      File f = jf.getSelectedFile();

      if (f.isFile()) {
        subirArchivo(f, "");
      } else {
        subirDirectorio(f, "");
      }
    }
  }

  public static void subirArchivo(File f, String parent) {
    try {
        int pto = 1234;
        String dir = "127.0.0.1";
        Socket cl = new Socket(dir, pto);
        System.out.println("\nConexion con servidor establecida\n");
        DataOutputStream dos = new DataOutputStream(cl.getOutputStream());

        String nombre = f.getName();
        String path = f.getAbsolutePath();
        long tam = f.length();
        System.out.println("Preparándose pare enviar archivo " + path + " de " + tam + " bytes\n\n");

        DataInputStream dis = new DataInputStream(new FileInputStream(path));
        dos.writeUTF(nombre);
        dos.flush();
        dos.writeLong(tam);
        dos.writeUTF(parent);
        dos.flush();
        
        long enviados = 0;
        int l = 0, porcentaje = 0;
        
        while (enviados < tam) {
          byte[] b = new byte[1500];
          l = dis.read(b);

          System.out.println("Enviados: " + l);

          dos.write(b, 0, l);
          dos.flush();
          enviados = enviados + l;
          porcentaje = (int)((enviados * 100) / tam);

          System.out.print("\rEnviado el " + porcentaje + " % del archivo");
        }
        System.out.println("\n\nArchivo enviado...\n");
        
        dis.close();
        dos.close();
        cl.close();

    } catch (IOException ex) {
      Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

  public static void subirDirectorio(File f, String parent) {
    File[] ficheros = f.listFiles();
    String nombre = f.getName();
    for (int x = 0; x < ficheros.length; x++) {
      if (ficheros[x].isFile()) {
        subirArchivo(ficheros[x], parent+"/"+nombre);
      } else {
        subirDirectorio(ficheros[x], parent+"/"+ nombre);
      }
    }
  }
}