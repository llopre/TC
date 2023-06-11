package compiladores;

public class Variable extends Id {
    
    public Variable() {}

    public Variable(String nombre, String tipo) {
        super(nombre, tipo);
    }

    @Override
    public String toString() {
        String variable = this.getTipo() + " " + this.getNombre();

        if (this.getUsado())
            variable += " (USADA)";
        else
            variable += " (NO USADA)";
            
        return variable;
    }
}