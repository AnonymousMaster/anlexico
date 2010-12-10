package traductor;

import Automata.Automata;
import java.util.ArrayList;


/**
 * El traductor es el encargado de implementar los procedimientos necesarios
 * para llevar a cabo el proceso de traducción 
 */
public class Traductor {
 
    /**
     * Analizador Lexico
     */
    private Analizador lexico;
    
    /**
     * Expresión regular a traducir
     */
    private String regex;
    
    /**
     * Token que contiene el simbolo que se está procesando actualmente
     */
    private Token preanalisis;
    
    /**
     * Alfabeto sobre el cual está definida la expresión regular.
     */
    private  ArrayList<String> alfabeto;
    
    /**
     * Automata en el cual se guardará el resultado final de la traducción. 
     * Se trata de un Automata del tipo AFN. 
     */
    private Automata automata;
    
    /**
     * Simbolo especial utilizado para guardar recordar el símbolo operador
     * consumido por una producción, cuando se deba aplicar la misma en una 
     * producción superior
     */
    private String Special;
    
    /**
     * Contador de caracteres procesados
     */
    private int posicion;
    
    /**
     * Flag que indica la existencia o no de errores al final de la traducción
     */
    private boolean hayErrores = false;

    /**
     * Flag que indica la existencia o no de errores al final de la traducción
     */
    private String errMsg = ""; 
    /**
     * Constructor vacío de la clase <code>Analizador</code>
     */
    public Traductor() {
    }

    /**
     * Constructor del <code>Analizador</code> Sintáctico a partir de la 
     * expresión regular y el alfabeto de entrada. 
     * 
     * @param regex Expresión regular cuyo AFN queremos generar
     * @param alfabeto Alfabeto sobre el cual está definida la expresión regular
     */
    public Traductor(String regex, String alfabeto) {
        this.setPosicion(0);
        this.regex = regex;
        this.alfabeto = new ArrayList<String>();
        this.setAlfabetoString(alfabeto);
        this.lexico = new Analizador(regex, alfabeto); // creamos el analizador léxico
        this.preanalisis = null;
        try {
            // creamos el analizador léxico
           this.preanalisis = this.lexico.next(); // obtenemos el primer símbolo desde el analizador léxico
        } catch (Exception ex) {
            this.hayErrores = true;            
            this.errMsg = ex.getMessage();
            System.out.println(this.getErrMsg());
            System.exit(0);
        }
        automata = new Automata();
        automata.setTipo("AFN");
    }

    /**
     * Implementación del procedimiento que se encarga de parear el símbolo de
     * preanálisis actual con la entrada esperada según la sintaxis del lenguaje
     * 
     * @param tok Símbolo esperado
     * @throws exceptions.SyntaxError Error de Sintaxis
     */
    private void Match(String simbolo) throws Exception {
        
        Token tok = new Token(simbolo); // se crea un Token temporal para 
                                        // compararlo con preanalisis
        
        if ( getPreanalisis().compareTo(tok) == 0 ) {
            this.setPreanalisis(this.lexico.next());
            this.Special = tok.getValor();
            this.incPosicion();
        } else {
            throw new Exception(tok.getValor() + this.getPosicion());
        }
    }


   
    
    public Automata traducir() {
        this.automata = this.RE();
        
        if (!this.isHayErrores()) {
            if (!(preanalisis.getTipo().equals("FIN"))) {
                this.hayErrores = true; 
                this.errMsg = "Quedaron caracteres sin analizar debido al siguiente Token no esperado["+
                        this.getPosicion()+"]: "+preanalisis.getValor();
            }
        }
        
        return this.automata;
    }
    
    /**
     * Método correspondiente al símbolo inicial de la gramática de expresiones 
     * regulares. 
     * 
     * Las producciones que pueden ser vacío, retornan un valor null en ese caso. 
     * Las demás producciones lanzan excepciones que se trasladan a los ámbitos 
     * de llamada superiores
     * 
 
     */
    private Automata RE() {
        
        // automatas auxiliares de producciones llamadas
        Automata Aux1 = null;
        Automata Aux2;
        
        try {

            Aux1 = this.resimple();
            Aux2 = this.A();

            if (Aux2 != null) {
                Aux1.thompson_or(Aux2);
            }
        } catch (Exception ex) {
            
            this.hayErrores = true;  
            this.errMsg =  "Se produjo un error FATAL en el traductor. La generación del AFN no puede continuar\n";
            System.out.println(this.getErrMsg());
            System.exit(0);
        }
      
        if (!(this.hayErrores) ){
            this.setAutomata(Aux1); // Actualizar el Automata Global
            Aux1.setAlpha(this.alfabeto);
            Aux1.setRegex(this.regex);
        }
        return Aux1;
    }

    /**
     * Producción A, que permite la recursión necesaria para producir cadenas 
     * de expresiones regulares separadas por el operador "|" (disyunción) <br><br>
     * 
     * @return null si derivó en vacío, en caso contrario, el automata generado
     * @throws exceptions.SyntaxError
     */
    private Automata A() throws Exception {
        try {            
            Token or = new Token("|");            
            
            if (preanalisis.compareTo(or) == 0) {    
                this.Match("|"); // si preanalisis es el esperado, consumimos, 
                return RE();            
            } else {                 
                return null;    // si es vacío se analiza en otra producción
            }         
        } catch (Exception ex) {
            this.hayErrores = true;  
            throw new Exception("Error de sintaxis en el símbolo ["+this.getPosicion()+"]: se esperaba '|' en lugar de -> " + this.preanalisis.getValor());
        }
    }
    
    /**
     * Producción resimple
     * 
     * @return Automata producido por la producción
     * @throws exceptions.SyntaxError
     * @throws exceptions.LexicalError
     */
    private Automata resimple() throws Exception {
        Automata Aux1 = this.rebasico(); 
        Automata Aux2 = this.B();       
        
        if (Aux2 != null) {
            Aux1.thompson_concat(Aux2);
        }
        
        return Aux1;
    }

    /**
     * Producción rebasico. 
     * @return Automata generado luego de derivar la producción
     */
    private Automata rebasico() throws Exception {
        
        Automata Aux1 = list();

        if (Aux1 != null) {
            char operator = op();

            switch (operator) {
                case '*':
                    Aux1.thompson_kleene();
                    break;
                case '+':
                    Aux1.thompson_plus();
                    break;
                case '?':
                    Aux1.thompson_cerouno();
                    break;
                case 'E':
                    break;
            }
        } /*else if (preanalisis.) {
            throw new SyntaxError("se esperaba un símbolo del lenguaje y se encontró: "
                            +this.preanalisis.getValor(),this.getPosicion());            
        }*/

        return Aux1;
    }
    
    /**
     * La producción B debe verificar si preanalisis está en el conjunto primero
     * de resimple, y si está, volver a ejecutar resimple. En caso contrario debe
     * retornar null. <br> <br>
     * 
     * El conjunto Primero de resimple es {"(",[alpha]}. 
     * 
     * @return Automata el automata producido por la producción, o null si la 
     *                  producción deriva en vacío. 
     * @throws exceptions.SyntaxError
     * @throws exceptions.LexicalError
     */
    private Automata B() throws Exception {
        
        String current = preanalisis.getValor();
        Automata result = null;
       
        if ( !(preanalisis.getTipo().equals("FIN")) &&
             (this.alfabeto.contains(current) || current.compareTo("(")==0)
           ) {
            result = this.resimple();
        }
        
        return result;
    }
    
    private Automata list() throws Exception {
        
        Token grupofirst = new Token("(");
        
        if(preanalisis.compareTo(grupofirst) == 0) {
            return this.grupo();            
        } else {
            return this.leng();
        }
    }
    
    private char op() throws Exception {
        char operador = 'E';        
        
        if (preanalisis.getValor().compareTo("") != 0) {
            operador = preanalisis.getValor().charAt(0);

            switch (operador) {
                case '*':
                    this.Match("*");
                    break;
                case '+':
                    this.Match("+");
                    break;
                case '?':
                    this.Match("?");
                    break;
                default:
                    return 'E';
            }
        }
        return operador;
    }
    
    private Automata grupo() throws Exception {
        try {
            this.Match("(");
        } catch (Exception ex) {
            this.hayErrores = true;  
            throw new Exception("se esperaba el símbolo -> '('" );

        }
        
        Automata Aux1 = this.RE();
        
        try {
            this.Match(")");
        } catch (Exception ex) {
            this.hayErrores = true;  
            throw new Exception("se esperaba el símbolo -> ')'");
        }
        
        return Aux1;
    }
    
    /**
     * 
     * @return
     */
    private Automata leng() throws Exception {
        Automata nuevo = null;
        try {
            if (!(preanalisis.getTipo().equals("FIN")) ){
                nuevo = new Automata(preanalisis.getValor(),"AFN");
                this.Match(preanalisis.getValor());
            }
        } catch (Exception ex) {
            this.hayErrores = true;  
            throw new Exception("Error Léxico en [" + this.getPosicion() + "]: el símbolo no pertenece al alfabeto");
        
        }
        
        return nuevo;
    }
    
    
    /* ----------------------- GETTERS Y SETTERS ------------------------ */
    public String getRegex() {
        return regex;
    }

    public void setRegex(String regex) {
        this.setPosicion(0);
        this.regex = regex;        
        this.lexico = new Analizador(regex, alfabeto); // creamos el analizador léxico

        try {
            // creamos el analizador léxico
            this.preanalisis = this.lexico.next(); // obtenemos el primer símbolo desde el analizador léxico
        } catch (Exception ex) {
            this.hayErrores = true;
            this.errMsg = 
                    "Se produjo un error FATAL en el traductor. La generación del AFN no puede continuar\n"+
                    "--> "+ex.getMessage();
            
            System.out.println(this.getErrMsg());
            System.exit(0);
        }
        automata = new Automata();
    }

    public Token getPreanalisis() {
        return preanalisis;
    }

    public void setPreanalisis(Token preanalisis) {
        this.preanalisis = preanalisis;
    }

    public ArrayList<String> getAlfabeto() {
        return alfabeto;
    }

    public void setAlfabeto(ArrayList<String> alfabeto) {
        this.alfabeto = alfabeto;
    }

    public void setAlfabetoString(String alpha) {
        char a[] = new char[alpha.length()];
        a = alpha.toCharArray();
        java.util.Arrays.sort(a);
       this.alfabeto.removeAll(alfabeto);
       for (int i = 0; i < alpha.length(); i++) {
            if (! this.alfabeto.contains(a[i]+ "")) {
                this.alfabeto.add(a[i]+ "");
            }
        }
    }
    public Automata getAutomata() {
        return automata;
    }

    public void setAutomata(Automata Aut) {
        this.automata = Aut;
    }

    public int getPosicion() {
        return posicion;
    }

    public void setPosicion(int posicion) {
        this.posicion = posicion;
    }
    
    public void incPosicion() {
        this.setPosicion(this.posicion+1);
    }

    public boolean isHayErrores() {
        return hayErrores;
    }

    public String getErrMsg() {
        return errMsg;
    }
}
