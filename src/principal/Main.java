/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package principal;

import traductor.AlgMinimizacion;
import traductor.Subconjuntos;
import Automata.Automata;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import traductor.Traductor;

/**
 *
 * @author cparra
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) throws Exception{
        System.out.println("************************************************");
        System.out.println("             Analizador lexico                  ");
        System.out.println("************************************************");
        System.out.print("Introduzca el alfabeto: ");
        BufferedReader ini = new BufferedReader(new InputStreamReader(System.in));
        String alpha = ini.readLine();

        System.out.print("Introduzca la expresion regular a ser analizada: ");
        String regex = ini.readLine();

        //String regex = "(a|b)*abb";
        //String alpha = "ab";
        
        
        Traductor t = new Traductor(regex, alpha);
        Automata A = t.traducir();
        A.setAlpha(t.getAlfabeto());
        A.setRegex(t.getRegex());
        
        String salida_simple = A.imprimir();
        System.out.println(salida_simple);
        
        //Alg de Subconjuntos 
        Subconjuntos algSub = new Subconjuntos(A);
        Automata AFD = algSub.ejecutar().convertAutomata();
        System.out.println("\nAFD\n____\n");
        System.out.println(AFD.imprimir());
        
        //Eliminar estados inalacanzables
        AFD = Subconjuntos.eliminar_estados_inalcanzables(AFD);  
        //Alg de Minimizacion
        AlgMinimizacion algMin = new AlgMinimizacion(AFD);
        Automata AFDM = algMin.minimizar();
        System.out.println("\nAFDM\n_____\n");
        System.out.println(AFDM.imprimir());

        System.out.print("Desea validar una cadena de entrada? : S/N ");
        String sn = ini.readLine();
        if (sn.equalsIgnoreCase("S")){
             System.out.print("Introduzca la cadena que desea validar: ");
             String cadena = ini.readLine();
             Simulacion sim = new Simulacion(cadena, A);
             if (sim.validar())
                   System.out.println("La cadena es valida.");
             else
                 System.out.println("La cadena no es valida.");
        }


    }

}
