package traductor;

import java.util.ArrayList;


/**
 *
 * Analizador Léxico del traductor dirigido por sintaxis de expresiones regulares
 * a AFNs

 */
public class Analizador {
    
    /**
     * Buffer de String que contiene la expresión regular a analizar
     */
    private StringBuffer regex;
    
    /**
     * Lista de caracteres que conforman el alfabeto 
     */
    private ArrayList<String> Alpha;
    
    /**
     * Símbolos especiales del lenguaje
     */
    private String specials;     
    
    /**
     * Constructor de la clase del analizador léxico
     */
    public Analizador(String regex, String alfabeto) {
        this.regex = new StringBuffer(regex);
        this.Alpha = new ArrayList<String>() ;

        char a[] = new char[alfabeto.length()];
        a = alfabeto.toCharArray();
        java.util.Arrays.sort(a);

       for (int i = 0; i < alfabeto.length(); i++) {
            if (! this.Alpha.contains(a[i]+ "")) {
                Alpha.add(a[i]+ "");
            }
        }

        this.specials = "*+?|()";
    }
    
    
    /**
     * Constructor de la clase del analizador léxico que recibe un alfabeto ya creado
     */
    public Analizador(String regex, ArrayList<String> alfabeto) {
        this.regex = new StringBuffer(regex);
        this.Alpha = alfabeto;
        this.specials = "*+?|()";
    }
    
    /**
     *consume un elemento de la entrada, lo procesa si y solo si, es un caracter especial o un simbolo valido del alfabeto
     * y crea un token a partir de el y lo devuelve
     */
    public Token next() throws Exception {
       String s = "";

        if (this.regex.length() > 0) {
            s = Character.toString( this.regex.charAt(0) );
            this.regex.deleteCharAt(0);
        }
        ;
        Token siguiente;
        
        if (s.equalsIgnoreCase(" ") || s.equalsIgnoreCase("\t")) {
            siguiente = next();         // Los espacios y tabuladores se ignoran            

        } else if (this.specials.indexOf(s) >= 0 || this.Alpha.contains(s) || s.length() == 0) {
            siguiente = new Token(s);   // se procesan los simbolos del alfabeto o especiales

        } else {
            String except = "El símbolo "+s+" no es válido";
            throw new Exception(except);
        }

        return siguiente;
    }
    

    
    /**
     * Obtener el Alfabeto utilizado
     * @return Alpha El Alfabeto completo utilizado
     */
    public ArrayList<String> getAlpha() {
        return Alpha;
    }

    /**
     * Obtener la expresión regular
     * @return regex Expresión regular
     */
    public StringBuffer getRegex() {
        return regex;
    }

    
    /**
     * Obtener la expresión regular (en String)
     * @return regex Expresión regular, como un String
     */
    public String getRegexString() {
        return regex.toString();
    }
    
    /**
     * Obtener caracteres especiales
     * @return specials Los operadores y simbolos especiales del lenguaje
     */
    public String getSpecials() {
        return specials;
    }
}
