package escom.ipn.drive;

import escom.ipn.drive.Servidor;

public class Main {
    public static void main(String[] args) {
        Servidor server = new Servidor();
        
        showMenu();
    }
    
    private static void showMenu(){
        System.out.println("1. Subir archivo\n" +
                            "2. Listar archivos\n" +
                            "3. Salir\n" +
                            "Su opción: ");
    }
}
