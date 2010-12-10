package traductor;

/**
 * almacena cada simbolo encontrado en la expresion regular
 */
public class Token  {
    private String tipo; //letra, numero u operador
    private String valor; //simbolo
    /**
     * crea un token a partir de un simbolo
     */
    public Token(String simbolo) {
        this.valor = simbolo;
        this.setTipoS(simbolo);
    }

    public String getTipo() {
        return tipo;
    }
    public String getValor() {
        return valor;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
    public void setValor(String valor) {
        this.valor = valor;
        this.setTipo(valor);
    }
    
    /**
     * compara el token actual con otro recibido por parametro
     */
    public int compareTo(Token t) {
        if (this.getTipo() == t.getTipo() 
                && this.getValor().compareTo(t.getValor()) == 0 ) {
            return 0;
        } else {
            return -1;
        }
    }
    /*
     * tipos de token
     */
    private void setTipoS(String simbolo) {
        
        if (simbolo.isEmpty()) {
            this.tipo = "FIN";
        } else {

            switch (simbolo.charAt(0)) {
                case '*':
                    this.tipo = "KLEENE";
                    break;
                case '+':
                    this.tipo = "PLUS";
                    break;
                case '?':
                    this.tipo = "CEROUNO";
                    break;
                case '|':
                    this.tipo = "OR";
                    break;
                case '(':
                    this.tipo = "PARI";
                    break;
                case ')':
                    this.tipo = "PARD";
                    break;
                default:
                    this.tipo = "ALFA";
                    this.valor = simbolo;
                    break;
            }
        }
    }
}
