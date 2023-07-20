package compiladores;

public class ManejaErrores {
    
    private static ManejaErrores inst = null; //singleton
    private boolean hayError = false;

    public static ManejaErrores getInstancia() {

        if (inst == null) {
            inst = new ManejaErrores();
        }
        return inst;
    } 

    public void imprimeError(int l, String e) {
        this.hayError = true;
        System.out.println("ERROR en la linea " + l + " " + e);
    }

    public Boolean getHayError() {
        return this.hayError;
    }

    public void setHayError(){
        this.hayError = true;
    }
}
