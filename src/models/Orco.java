package models;

public class Orco extends Personaje{
    public Orco() {
    }

    public Orco(String nombre, String apodo, Raza raza, String fechaDeNacimiento) {
        super(nombre, apodo, raza, fechaDeNacimiento);
    }

    @Override
    public double ataque(Personaje personaje) {
        double va, ed, pdef;
        // ( ((VA*ED)-PDEF)/500)*100 ) * 1.1
        va = this.calcularValorDeAtaque();
        ed = this.calcularEfectividadDeDisparo();
        pdef = personaje.calcularPoderDeDefensa();
        return ((((va * ed) - pdef)/500) / 4) * 1.1;
    }
}
