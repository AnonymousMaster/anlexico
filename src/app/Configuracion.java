/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package app;

import org.simpleframework.xml.*;

@Root
public class Configuracion {

   @Element
   private String dotpath = "/usr/bin/dot";
   
   @Element
   private String emptySymbol="(vacio)";

   @Element
   private String imgdir = "/tmp";

   @Attribute
   private int index;
   
   
   public Configuracion() {
      super();
   }  

   public Configuracion(String text, int index) {
      this.index = index;
      this.dotpath = text;
   }

   public String getDotPath() {
      return dotpath;
   }

   public int getGraphViz() {
      return index;
   }

    public String getEmptySymbol() {
        return emptySymbol;
    }

    public void setEmptySymbol(String emptySymbol) {
        this.emptySymbol = emptySymbol;
    }

    public void setImgdir(String text) {
        this.imgdir = text;
    }
    
    public String getImgdir() {
        return this.imgdir;
    }

    void setDotPath(String absolutePath) {
        this.dotpath = absolutePath;
    }
}
