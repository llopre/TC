package compiladores;

public class Id {

    private String nombre;
    private String tipo;
    private Boolean usado;

    public Id() {}

    public Id(String nombre, String tipo) {
        this.nombre = nombre;
        this.tipo = tipo;
        this.usado = false;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Boolean getUsado() {
        return usado;
    }

    public void setUsado(Boolean usado) {
        this.usado = usado;
    }

    
}
