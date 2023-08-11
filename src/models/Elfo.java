package models;

public class Elfo extends Personaje{
    public Elfo() {
    }

    public Elfo(String nombre, String apodo, Raza raza, String fechaDeNacimiento) {
        super(nombre, apodo, raza, fechaDeNacimiento);
    }

    @Override
    public double ataque(Personaje personaje) {
        double va, ed, pdef;
        // (((VA*ED)-PDEF)/500)*100 ) * 1.05
        va = this.calcularValorDeAtaque();
        ed = this.calcularEfectividadDeDisparo();
        pdef = personaje.calcularPoderDeDefensa();
        return ((((va*ed)-pdef)/500)/ 4) / 1.05;
    }
}
