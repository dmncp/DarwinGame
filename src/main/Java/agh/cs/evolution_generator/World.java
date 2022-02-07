package agh.cs.evolution_generator;

import agh.cs.evolution_generator.MapElements.Animal;
import agh.cs.evolution_generator.Maps.GamingMap;
import agh.cs.evolution_generator.Maps.Vector2d;
import agh.cs.evolution_generator.controller.MainViewController;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import org.json.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class World extends Application{
    private static GamingMap map1;
    private static GamingMap map2;
    private static MainViewController controller;

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"));

        Parent root = loader.load();
        controller = loader.getController();
        initMaps();

        Scene scene = new Scene(root);
        stage.setScene(scene);

        stage.setTitle("Evolution Generator");
        stage.show();

        runGenerator();

    }
    private void initMaps(){
        try{
            String path="src/main/resources/parameters.json";
            String parameters = new String(Files.readAllBytes(Paths.get(path)));
            JSONObject arguments = new JSONObject(parameters);
            int width = arguments.getInt("width");
            int height= arguments.getInt("height");
            int startingEnergy = arguments.getInt("startEnergy");
            int moveEnergy = arguments.getInt("moveEnergy");
            int plantEnergy = arguments.getInt("plantEnergy");
            double jungleRatio = arguments.getDouble("jungleRatio");
            int animalAmount = arguments.getInt("animalAmount");

            if(width == 0 || height == 0 || startingEnergy == 0 || moveEnergy == 0 || plantEnergy == 0 || jungleRatio == 0.0 || animalAmount == 0){
                throw new IllegalArgumentException("Nie mozesz podac zerowych wartosci");
            }
            if(width < 0){
                throw new IllegalArgumentException("Szerokosc nie moze byc ujemna");
            }
            if(height < 0){
                throw new IllegalArgumentException("Wysokosc nie moze byc ujemna");
            }
            if(startingEnergy < 0){
                throw new IllegalArgumentException("Energia poczatkowa nie moze byc ujemna");
            }
            if(moveEnergy < 0){
                throw new IllegalArgumentException("Strata energii po ruchu nie powinna dodawac energii.");
            }
            if(plantEnergy < 0){
                throw new IllegalArgumentException("Zjedzenie rosliny nie powinno odbierac energie");
            }
            if(jungleRatio < 0.0 || jungleRatio > 1.0){
                throw new IllegalArgumentException("Dzungla musi zajmować miedzy 0 a 100% całej mapy");
            }
            if(animalAmount < 0){
                throw new IllegalArgumentException("Liczba zwierzakow nie moze być ujemna");
            }

            // 1. tworzymy mape
            map1 = new GamingMap(width, height, jungleRatio, plantEnergy, moveEnergy, animalAmount);
            map2 = new GamingMap(width, height, jungleRatio, plantEnergy, moveEnergy, animalAmount);
            controller.setMap(map1, map2);
            // 2. generujemy zwierzaki i dodajemy do mapy
            for(int i = 0; i < animalAmount; i++){
                int x = (int) (Math.random() * width);
                int y = (int) (Math.random() * height);
                Vector2d pos = new Vector2d(x, y);
                Animal animal1 = new Animal(map1, pos, startingEnergy);
                Animal animal2 = new Animal(map2, pos, startingEnergy);

                map1.addAnimal(animal1);
                map1.animalsList.add(animal1);

                map2.addAnimal(animal2);
                map2.animalsList.add(animal2);
            }
        } catch(IllegalArgumentException | IOException ex){
            System.out.println(ex);
            System.out.println("Zakonczenie dzialania programu!");
            System.exit(1);
        }
    }
    public void runGenerator(){ //3. uruchamiamy generator
        AnimationTimer timer = new AnimationTimer() {
            private long lastUpdate = 0;
            private int DELAY = 50_000_000;
            @Override
            public void handle(long now) {
                if(now - lastUpdate >= DELAY) {
                    // Stop the timer when there are no living animals
                    if(map1.animalsList.size() == 0 && map2.animalsList.size() == 0) {
                        stop();
                    }
                    // One day cycle
                    if(map1.animalsList.size() > 0 && !controller.isStoppedLeft) { map1.mainRunGenerator(); }
                    if(map2.animalsList.size() > 0 && !controller.isStoppedRight) { map2.mainRunGenerator(); }
                    // Update the canvases
                    controller.updateCanvas();
                    lastUpdate = now;
                    try {
                        Thread.sleep(controller.speed);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        timer.start();
    }
}
