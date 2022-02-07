package agh.cs.evolution_generator.MapElements;

import agh.cs.evolution_generator.Maps.GamingMap;
import agh.cs.evolution_generator.Maps.MapDirection;
import agh.cs.evolution_generator.Maps.Vector2d;

import java.util.ArrayList;
import java.util.List;

public class Animal{

    private MapDirection orientation;
    private GamingMap map;
    private Vector2d initialPosition; // pozycja zwierzaka
    public int actualEnergy; // energia określona na starcie
    public int startingEnergy; // potrzebne do rozmnazania - min energia do rozmnozenia to 0.5 energi startowej
    public Genom gen; // geny zwierzaka
    public int age; // wiek zwierzaka - ile dni już przeżył
    public int whenBorn; // kiedy zwierzak został dodany do mapy - w ktorej epoce; potrzebne do rozwiązania problemu z wypisaniem liczby potomków po danej liczbie epok
    public int whenDead;
    public List<Animal> kids = new ArrayList<>(); // lista dzieci danego zwierzaka

    public Animal(GamingMap map, Vector2d pos, int startingEnergy){ // dla początkowych zwierzaków
        this.map = map;
        this.initialPosition = pos;
        this.actualEnergy = startingEnergy;
        this.startingEnergy = startingEnergy;
        this.gen = new Genom();
        this.age = 0;
        this.whenBorn = 0; // konstruktor jest przeznaczony tylko dla pierwszych zwierzaków dodanych na starcie; dlatego 0 - rodzą się na początku
        this.orientation = MapDirection.randomStartingDir(); // startowa pozycja może być dowolna
    }


    public Animal(GamingMap map, Vector2d initialPosition, int startingEnergy, Genom gen){ //dla dzieci
        this.map = map;
        this.initialPosition = initialPosition;
        this.actualEnergy = startingEnergy;
        this.startingEnergy = startingEnergy;
        this.gen = gen;
        this.age = 0;
        this.orientation = MapDirection.randomStartingDir();
    }

    public String toString(){
        switch (orientation){
            case NORTH: return  "N";
            case EAST: return "E";
            case WEST: return "W";
            case SOUTH: return "S";
            case NORTH_EAST: return "NE";
            case NORTH_WEST: return "NW";
            case SOUTH_EAST: return "SE";
            case SOUTH_WEST: return "SW";
            default: return "ERROR";
        }
    }

    public Vector2d getPosition(){
        return this.initialPosition;
    }
    public int getEnergy(){
        return this.actualEnergy;
    }

    public void move(){
        int turnDirection = gen.chooseDirection(); //musimy wiedzieć gdzie się obrócić
        for(int i = 0; i < turnDirection; i++){
            orientation = orientation.next();
        }
        Vector2d afterMove = this.initialPosition.add(orientation.toUnitVector());
        this.initialPosition = this.map.checkPosAfterMove(afterMove);
    }

    public void eat(int energy){
        this.actualEnergy += energy;
    }

    public int dominantGen(){ //dominujący gen w genotypie danego zwierzaka
        int[] gensCounter = new int[8];
        int maxInd = 0;
        int maxVal = 0;
        for(int i = 0; i < 8; i++){
            gensCounter[i] = 0;
        }
        for(int i = 0; i < 32; i++){
            gensCounter[this.gen.orientations[i]]++;
        }
        for(int i = 0; i < 8; i++){
            if(maxVal <= gensCounter[i]){
                maxVal = gensCounter[i];
                maxInd = i;
            }
        }
        return maxInd;
    }

    public void nextDay(int energy){
        this.age ++;
        this.actualEnergy -= energy;
    }
    public void moveTo(Vector2d pos){ // wykorzystywana do umieszczenia nowego potomka na odpowienie miejsce spełniające warunki
        this.initialPosition = pos;
    }

    public int howManyKidsAfterNEra(int n){ // funkcja sprawdzająca ile dzieci miał zwierzak po n dniach
        int result = 0;
        for(Animal a: this.kids){
            if(a.whenBorn <= n) result++;
        }
        return result;
    }

    public int howManyDescendants(int n){ // sprawdza ile potomków miał zwierzak po n dniach
        ArrayList<Animal> descendants = new ArrayList<>();
        descendantsCounter(n, descendants);
        return descendants.size();
    }
    private void descendantsCounter(int n, ArrayList<Animal> desc){
        for(Animal a:kids){
            if(a.whenBorn <= n && !desc.contains(a)){
                desc.add(a);
                a.descendantsCounter(n, desc);
            }
        }
    }
}
