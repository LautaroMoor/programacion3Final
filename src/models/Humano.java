package models;

public class Humano extends Personaje{
    public Humano() {
    }

    public Humano(String nombre, String apodo, Raza raza, String fechaDeNacimiento) {
        super(nombre, apodo, raza, fechaDeNacimiento);
    }

    @Override
    public double ataque(Personaje personaje) {
        double va, ed, pdef;
        // ((VA*ED)-PDEF)/500)*100
        va = this.calcularValorDeAtaque();
        ed = this.calcularEfectividadDeDisparo();
        pdef = personaje.calcularPoderDeDefensa();
        return (((va*ed)-pdef)/500) * 2;
    }
}
