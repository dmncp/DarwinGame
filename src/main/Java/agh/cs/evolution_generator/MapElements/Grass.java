package agh.cs.evolution_generator.MapElements;

import agh.cs.evolution_generator.Maps.GamingMap;
import agh.cs.evolution_generator.Maps.Vector2d;

public class Grass {
    public Vector2d position;
    private GamingMap map;

    public Grass(Vector2d position, GamingMap map){
        this.position = position;
        this.map = map;
    }

    public Vector2d getPosition(){
        return this.position;
    }

    public String toString(){
        return "*";
    }




}
