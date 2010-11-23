/*
 * Dtrans.java
 *
 * Created on 11 de noviembre de 2008, 01:03 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package afgenjava;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;
import traductor.Token;

/**
 *
 * @author fmancia
 */
public class Dtrans {
    Hashtable dtrans;
    
    /** Creates a new instance of Dtrans */
    public Dtrans() {
        dtrans = new Hashtable();
    }

    
    public ListaEstados obtenerValor(DtransClave clave){
        return obtenerValor(clave.getIndiceEstados(), clave.getIndiceToken());
    }
    
    public ListaEstados obtenerValor(ListaEstados lista, Token token){
        DtransClave comparar = new DtransClave(lista, token);
        Enumeration en = dtrans.keys();
        DtransClave clave;
        while(en.hasMoreElements()){
            clave = (DtransClave)en.nextElement();
            if(clave.compareTo(comparar) == 0){
                return (ListaEstados) dtrans.get(clave);
            }
        }
        return null;
    }
    
    
    public void setValor(DtransClave clave, ListaEstados valor){
        dtrans.put(clave, valor);
    }
    
    public Automata convertAutomata(){
        Automata a = new Automata(); 
        
        Enumeration en = dtrans.keys();
        while(en.hasMoreElements()){
            DtransClave clave = (DtransClave) en.nextElement();
            ListaEstados valor = obtenerValor(clave);
            
            int id_new_origen = clave.getIndiceEstados().getId();
            int id_new_dest = valor.getId();
            Estado st_new_origen, st_new_dest;
            
            try{
                 st_new_origen = a.getEstadoById(id_new_origen);
            }catch(Exception ex){
                //No existe el estado entonces creamos 
                st_new_origen = new Estado(id_new_origen, 
                                            clave.getIndiceEstados().contieneInicial(), 
                                            clave.getIndiceEstados().contieneFinal(), 
                                            false);
                a.addEstado(st_new_origen);
                if(clave.getIndiceEstados().contieneInicial()){
                    a.setInicial(st_new_origen);
                }
                if(clave.getIndiceEstados().contieneFinal()){
                    a.getFinales().insertar(st_new_origen);
                }
                
            }
            
            
            try{
                 st_new_dest = a.getEstadoById(id_new_dest);
            }catch(Exception ex){
                //No existe el estado entonces creamos 
                st_new_dest = new Estado(id_new_dest, 
                                        valor.contieneInicial(), 
                                        valor.contieneFinal(), 
                                        false);
                a.addEstado(st_new_dest);
                if(valor.contieneInicial()){
                    a.setInicial(st_new_dest);
                }
                if(valor.contieneFinal()){
                    a.getFinales().insertar(st_new_dest);
                }
            }

            //Agregamos los enlaces.
            Enlace enlace_new = new Enlace( st_new_origen, st_new_dest, 
                                            clave.getIndiceToken().getValor());
            
            st_new_origen.addEnlace(enlace_new);
        }
               
        return a;
    }
    
    
    public String imprimir(){
        String print = "";
        Enumeration en = dtrans.keys();
        while(en.hasMoreElements()){
            DtransClave clave = (DtransClave) en.nextElement();
            ListaEstados lista = obtenerValor(clave);
            
            print += "\n" + clave.getIndiceEstados().imprimir() +
                    " -#- " + clave.getIndiceToken().getValor() +
                    " = " + lista.imprimir();
            
        }
        return print;
    }

    
    
    

   
}

