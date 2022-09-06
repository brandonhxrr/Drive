package ipn.escom.drive;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;
import javax.swing.JFileChooser;

public class Cliente {
    public static void main (String[] args){
        try{
            establecerConexion();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public static void establecerConexion() throws IOException{
        int pto = 1234;
        String dir = "127.0.0.1";
        Socket cl = new Socket(dir,pto);
        System.out.println("Conexion con servidor establecida.. lanzando FileChooser..");
        subir(cl);
    }
    public static void subir(Socket cl) throws IOException{
        JFileChooser jf = new JFileChooser();
        int r = jf.showOpenDialog(null);
            if(r==JFileChooser.APPROVE_OPTION){
                File f = jf.getSelectedFile();
                String nombre = f.getName();
                String path = f.getAbsolutePath();
                long tam = f.length();
                System.out.println("Preparandose pare enviar archivo "+path+" de "+tam+" bytes\n\n");
                DataOutputStream dos = new DataOutputStream(cl.getOutputStream());
                DataInputStream dis = new DataInputStream(new FileInputStream(path));
                dos.writeUTF(nombre);
                dos.flush();
                dos.writeLong(tam);
                dos.flush();
                long enviados = 0;
                int l=0,porcentaje=0;
                while(enviados<tam){
                    byte[] b = new byte[1500];
                    l=dis.read(b);
                    System.out.println("enviados: "+l);
                    dos.write(b,0,l);
                    dos.flush();
                    enviados = enviados + l;
                    porcentaje = (int)((enviados*100)/tam);
                    System.out.print("\rEnviado el "+porcentaje+" % del archivo");
                }//while
                System.out.println("\nArchivo enviado..");
                dis.close();
                dos.close();
                cl.close();
            }
    }
}
