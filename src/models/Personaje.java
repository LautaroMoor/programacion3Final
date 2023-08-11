package models;

import interfaces.Acciones;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Random;

@Getter
@Setter
@NoArgsConstructor
public abstract class Personaje implements Acciones {
    //Atributos de clase
    protected double salud = 100;//100

    //Atributos de instancia
    protected String nombre;
    protected String apodo;
    protected Raza raza;
    protected String fechaDeNacimiento;
    protected int edad; //entre 0 a 300
    protected int velocidad;// 1 a 10
    protected int destreza; //1 a 5
    protected int fuerza;//1 a 10
    protected int nivel; //1 a 10
    protected int armadura; //1 a 10

    public Personaje(String nombre, String apodo, Raza raza, String fechaDeNacimiento) {
        this.nombre = nombre;
        this.apodo = apodo;
        this.raza = raza;
        this.fechaDeNacimiento = fechaDeNacimiento;

        //Calculando edad
        LocalDate fechaNacimiento = LocalDate.parse(fechaDeNacimiento);
        LocalDate fechaActual = LocalDate.now();
        Period periodo = Period.between(fechaNacimiento, fechaActual);
        this.edad = periodo.getYears();

        //Aleatorios
        Random rand = new Random();
        this.edad = rand.nextInt(301);
        this.velocidad = rand.nextInt(10) + 1;
        this.destreza = rand.nextInt(5) + 1;
        this.fuerza = rand.nextInt(10) + 1;
        this.nivel = rand.nextInt(10) + 1;
        this.armadura = rand.nextInt(10) + 1;
    }

    public void actualizarSalud(double daniorecibido){
        setSalud(this.salud - daniorecibido);
    }

    // (PD) ---> Poder de Disparo: Haga el producto de Destreza * Fuerza * Nivel del personaje que ataca
    // (ED) ---> Efectividad de Disparo:Genere un valor aleatorio de 1 a 100. Considerarlo como valor porcentual
    // (VA) ---> Valor de Ataque:Al Poder de Disparo lo multiplico por la Efectividad de Disparo.
    // (PDEF) ---> Poder de Defensa: Haga el producto de Armadura * Velocidad del personaje que defiende

    protected double calcularPoderDeDisparo (){
        return this.destreza * this.fuerza * this.nivel;
    }

    protected double calcularEfectividadDeDisparo (){
        Random rand = new Random();
        return rand.nextInt(100) + 1;
    }

    protected double calcularValorDeAtaque (){
        double poderDeDisparo = this.calcularPoderDeDisparo();
        double efectividadDeDisparo = this.calcularEfectividadDeDisparo();
        return poderDeDisparo * efectividadDeDisparo;
    }

    protected double calcularPoderDeDefensa (){
        return this.armadura * this.velocidad;
    }

    @Override
    public String toString() {
        Random rand = new Random();
        String razaStr;

        List<String> adjetivosCalificativos = List.of(
                "EL INCREÍBLE", "EL PODEROSO", "EL LEGENDARIO", "EL VALIENTE", "EL MÍSTICO",
                "EL IMPARABLE", "EL INVENCIBLE", "EL MAESTRO", "EL FANTÁSTICO", "EL MAGNÍFICO",
                "EL HEROICO", "EL ÉPICO", "EL ASOMBROSO", "EL SUPREMO", "EL MAESTRO DE LA BATALLA"
        );

        switch (raza) {
            case ORCO:
                razaStr = "un temible Orco";
                break;
            case HUMANO:
                razaStr = "un valiente Humano";
                break;
            case ELFO:
                razaStr = "un mágico Elfo";
                break;
            default:
                razaStr = "un misterioso ser";
        }

        String adjetivo = adjetivosCalificativos.get(rand.nextInt(adjetivosCalificativos.size()));

        String presentacion = adjetivo + " " + nombre.toUpperCase() + " AKA " + apodo.toUpperCase() +
                " ES " + razaStr.toUpperCase() + " Y TIENE " + salud + " PUNTOS DE VIDA. " +
                "POSEE UNA EDAD DE " + edad + " AÑOS, UNA VELOCIDAD DE " + velocidad +
                ", UNA DESTREZA DE " + destreza + ", UNA FUERZA DE " + fuerza +
                ", UN NIVEL DE " + nivel + " Y UNA ARMADURA DE " + armadura + ".";

        return presentacion;
    }
}