package agh.cs.evolution_generator.controller;

import agh.cs.evolution_generator.MapElements.Animal;
import agh.cs.evolution_generator.MapElements.Grass;
import agh.cs.evolution_generator.Maps.GamingMap;
import agh.cs.evolution_generator.Maps.Vector2d;
import agh.cs.evolution_generator.Reports.ReportGenerator;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class MainViewController {
    GamingMap map1;
    GamingMap map2;
    public boolean isStoppedLeft = false;
    public boolean isStoppedRight = false;
    private boolean deadModeIsOn = false;
    public int speed = 500;
    int reportID = 0;
    private double CELL_SIZE;

    //potrzebne do sledzenia statystyk wybranego zwierzaka po wznowieniu symulacji:
    private Animal selectedAnimal = null;
    private GamingMap animalOnMap = null;
    private TextArea selectedAnimalStats = null;
    private int numberOfEras = 0;

    public void setMap(GamingMap map1, GamingMap map2) {
        this.map1 = map1;
        this.map2 = map2;
        this.CELL_SIZE = Math.min(canvas1.getWidth() / map1.getWidth(), canvas1.getHeight() / map1.getHeight());
    }

    @FXML
    public Canvas canvas1;

    @FXML
    public Canvas canvas2;

    @FXML
    public Button playBtnLeft;
    
    @FXML
    public Button playBtnRight;

    @FXML
    public Button dnaBtn;

    @FXML
    public Button reportBtn;

    @FXML
    public Button deadAnimals;

    @FXML
    public Button fasterBtn;

    @FXML
    public Button slowerBtn;

    @FXML
    public TextArea leftTextArea;

    @FXML
    public TextArea rightTextArea;

    @FXML
    public TextArea leftAnimalStats;

    @FXML
    public TextArea rightAnimalStats;

    @FXML
    public void initialize(){
        setBtnBackground(dnaBtn, "/png/dna.png");
        setBtnBackground(deadAnimals, "/png/skull.png");
        setBtnBackground(playBtnLeft, "/png/pause.png");
        setBtnBackground(playBtnRight, "/png/pause.png");
        setBtnBackground(fasterBtn, "/png/faster.png");
        setBtnBackground(slowerBtn, "/png/slower.png");

        leftTextArea.setEditable(false);
        rightTextArea.setEditable(false);
        leftAnimalStats.setEditable(false);
        rightAnimalStats.setEditable(false);

        canvas1.setOnMouseClicked(new EventHandler<MouseEvent>() {
            double[] pos = new double[2];
            @Override
            public void handle(MouseEvent event) {
                pos[0] = event.getX();
                pos[1] = event.getY();
                if(!deadModeIsOn) chooseAnimal(pos, map1, leftAnimalStats, map1.animalsList, isStoppedLeft);
                else chooseAnimal(pos, map1, leftAnimalStats, map1.deadAnimals, isStoppedLeft);
            }
        });

        canvas2.setOnMouseClicked(new EventHandler<MouseEvent>() {
            double[] pos = new double[2];
            @Override
            public void handle(MouseEvent event) {
                pos[0] = event.getX();
                pos[1] = event.getY();
                if(!deadModeIsOn) chooseAnimal(pos, map2, rightAnimalStats, map2.animalsList, isStoppedRight);
                else chooseAnimal(pos, map2, rightAnimalStats, map2.deadAnimals, isStoppedRight);
            }
        });
    }

    @FXML
    public void clickLeft(){
        this.isStoppedLeft = !this.isStoppedLeft;
        this.deadModeIsOn = false;
        if(!this.isStoppedLeft) setBtnBackground(playBtnLeft, "/png/pause.png");
        else setBtnBackground(playBtnLeft, "/png/play-button.png");
    }
    @FXML
    public void clickRight(){
        this.isStoppedRight = !this.isStoppedRight;
        this.deadModeIsOn = false;
        if(!this.isStoppedRight) setBtnBackground(playBtnRight, "/png/pause.png");
        else setBtnBackground(playBtnRight, "/png/play-button.png");
    }
    @FXML
    public void dnaClicked(){
        this.deadModeIsOn = false;
        if(this.isStoppedLeft){
            printDominantGenOnly(map1, canvas1);
        }
        if(this.isStoppedRight){
            printDominantGenOnly(map2, canvas2);
        }
    }
    @FXML
    public void showDeadAnimals(){
        this.deadModeIsOn = true;
        if(this.isStoppedLeft){
            printDeadAnimal(map1, canvas1);
        }
        if(this.isStoppedRight){
            printDeadAnimal(map2, canvas2);
        }
    }
    @FXML
    public void speedUp(){
        if(this.speed - 200 >=0) this.speed -= 200;
        else this.speed = 0;
    }
    @FXML
    public void slowDown(){
        this.speed += 200;
    }
    @FXML
    public void generateReport(){
        if(isStoppedRight && isStoppedLeft){
            ReportGenerator reportGenerator = new ReportGenerator(reportID, map1.days, map2.days, animalAmountToReport(), grassesAmountToReport(), map1.dominantGenotype(), map2.dominantGenotype(), avgEnergyToReport(), avgDeadLifeToReport(), avgKidsAmountToReport());
            reportGenerator.main();
            reportID++;
        }
    }

    public void chooseAnimal(double[] clickedPos, GamingMap map, TextArea txtAr, List<Animal> list, boolean thisMapIsStopped){ // on mouse clicked
        if(thisMapIsStopped){
            for(Animal a: list){
                double x = a.getPosition().x * CELL_SIZE - CELL_SIZE;
                double y = a.getPosition().y * CELL_SIZE - CELL_SIZE;

                if(x <= clickedPos[0] && clickedPos[0] <= x+CELL_SIZE && y<=clickedPos[1] && clickedPos[1] <= y+CELL_SIZE){
                    int n;
                    do{
                        n = textInputDialogWindow();
                    } while (n <= 0);

                    selectedAnimal = a;
                    animalOnMap = map;
                    selectedAnimalStats = txtAr;
                    numberOfEras = n;

                    printAnimalStats(a, txtAr, map, n);
                    break;
                }
            }
        }
    }
    private int textInputDialogWindow(){
        TextInputDialog dialog = new TextInputDialog();
        TextField textField = dialog.getEditor();
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("[1-9](\\d)*")) {
                textField.setText(oldValue);
            }
        });
        textField.setText("1");
        dialog.setTitle("Text Input Dialog");
        dialog.setHeaderText("Określenie epoki");
        dialog.setContentText("Podaj nr epoki (liczba):");
        Optional<String> result = dialog.showAndWait();
        try{
            return result.map(Integer::parseInt).orElse(0);
        } catch (NumberFormatException nfe) {
            System.out.println("Podana wartość to nie liczba");
            return -1;
        }
    }
    private void setBtnBackground(Button btn, String src){
        Image btnImg;
        btnImg = new Image(src, btn.getWidth(), btn.getHeight(), false, true, true);
        BackgroundImage bImage = new BackgroundImage(btnImg, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, new BackgroundSize(btn.getWidth(), btn.getHeight(), true, true, true, false));
        Background background = new Background(bImage);
        btn.setBackground(background);
    }

    public void updateCanvas(){
        if(!isStoppedLeft){
            leftTextArea.clear();
            drawMap(map1, canvas1);
            updateDescription(map1, leftTextArea);
        }
        if(!isStoppedRight){
            rightTextArea.clear();
            drawMap(map2, canvas2);
            updateDescription(map2, rightTextArea);
        }
        if(!isStoppedRight || !isStoppedLeft){
            if(selectedAnimal != null){
                printAnimalStats(selectedAnimal, selectedAnimalStats, animalOnMap, numberOfEras);
            }
        }
    }

    private void drawMap(GamingMap map, Canvas canvas){
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        gc.setFill(Color.CHARTREUSE);

        map.grasses.values().forEach(grass -> {
            int x = grass.getPosition().x;
            int y = grass.getPosition().y;
            gc.fillRect(x*CELL_SIZE - CELL_SIZE, y*CELL_SIZE - CELL_SIZE, CELL_SIZE, CELL_SIZE);
        });

        map.animals.values().forEach((animals -> {
            gc.setFill(animalEnergy(animals.get(0)));
            int aX = animals.get(0).getPosition().x;
            int aY = animals.get(0).getPosition().y;
            gc.fillOval(aX*CELL_SIZE - CELL_SIZE, aY*CELL_SIZE - CELL_SIZE, CELL_SIZE, CELL_SIZE);
        }));
    }

    private Color animalEnergy(Animal animal){ // system kolorowania zwierzaka w zaleznosci od poziomu energii
        if(animal.getEnergy() >= 0.9 * animal.startingEnergy) return Color.GREEN;
        else if(animal.getEnergy() >= 0.8 * animal.startingEnergy) return Color.LIMEGREEN;
        else if(animal.getEnergy() >= 0.7 * animal.startingEnergy) return Color.LIGHTGREEN;
        else if(animal.getEnergy() >= 0.6 * animal.startingEnergy) return Color.YELLOWGREEN;
        else if(animal.getEnergy() >= 0.5 * animal.startingEnergy) return Color.YELLOW;
        else if (animal.getEnergy() >= 0.4 * animal.startingEnergy) return Color.GOLD;
        else if(animal.getEnergy() >= 0.3 * animal.startingEnergy) return Color.ORANGE;
        else if(animal.getEnergy() >= 0.2 * animal.startingEnergy) return Color.ORANGERED;
        else if(animal.getEnergy() >= 0.1 * animal.startingEnergy) return Color.RED;
        else if(animal.getEnergy() > 0) return Color.DARKRED;
        else return Color.BLACK;
    }

    private void updateDescription(GamingMap map, TextArea txtAr){
        txtAr.appendText("Nr dnia: " + map.days + "\n");
        txtAr.appendText("Wszystkich zwierząt: " + map.animalsList.size() + "\n");
        txtAr.appendText("Wszystkich roślin: " + map.grasses.size() + "\n");
        txtAr.appendText("Gen dominujący: \n" + Arrays.toString(map.dominantGenotype()) + "\n");
        txtAr.appendText("Średnia energia: " + map.avgEnergy() + "\n");
        txtAr.appendText("Średni wiek martwych: " + map.avgDeadLife() + "\n");
        txtAr.appendText("Średni wiek żyjących: " + map.avgAliveLife() + "\n");
        txtAr.appendText("Średnia liczba dzieci żyjących: " + map.avgKidsAmount() + "\n");
    }

    private void printAnimalStats(Animal animal, TextArea txtAr, GamingMap map, int n){
        txtAr.setText("Statystyki wybranego zwierzaka: " + "\n");
        txtAr.appendText("Genom zwierzaka: \n" + Arrays.toString(animal.gen.printGen()) + "\n");
        txtAr.appendText("Dominujący gen: " + animal.dominantGen() + "\n");
        txtAr.appendText("Liczba dzieci łącznie: " + animal.kids.size() + "\n");
        txtAr.appendText("Liczba potomków łącznie: " + animal.howManyDescendants(map.days) + "\n");
        txtAr.appendText("Liczba dzieci po " + n + " dniach: " + animal.howManyKidsAfterNEra(n) + "\n");
        txtAr.appendText("Liczba potomków po " + n + " dniach: " + animal.howManyDescendants(n) + "\n");
        txtAr.appendText("Wiek: " + animal.age + "\n");
        if(animal.getEnergy() <= 0) txtAr.appendText("Ten zwierzak jest już martwy!\n");
        if(this.deadModeIsOn || animal.getEnergy() <= 0){
            txtAr.appendText("Epoka w której zmarł: " + animal.whenDead + "\n");
        }
    }

    private void printDominantGenOnly(GamingMap map, Canvas canvas){
        ArrayList<Animal> animalsWithDominantGenotype = new ArrayList<>();
        int[] dominantGenotypeInMap = map.dominantGenotype();
        for(Animal a: map.animalsList){
            if(Arrays.equals(a.gen.orientations, dominantGenotypeInMap)) animalsWithDominantGenotype.add(a);
        }
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        for(Animal a: animalsWithDominantGenotype){
            gc.setFill(animalEnergy(a));
            int aX = a.getPosition().x;
            int aY = a.getPosition().y;
            gc.fillOval(aX*CELL_SIZE - CELL_SIZE, aY*CELL_SIZE - CELL_SIZE, CELL_SIZE, CELL_SIZE);
        }
    }

    private void printDeadAnimal(GamingMap map, Canvas canvas){
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        for(Animal a: map.deadAnimals){
            gc.setFill(animalEnergy(a));
            int aX = a.getPosition().x;
            int aY = a.getPosition().y;
            gc.fillOval(aX*CELL_SIZE - CELL_SIZE, aY*CELL_SIZE - CELL_SIZE, CELL_SIZE, CELL_SIZE);
        }
    }

    private int animalAmountToReport(){
        return map1.animalsList.size() + map1.deadAnimals.size() + map2.animalsList.size() + map2.deadAnimals.size();
    }

    private int grassesAmountToReport(){
        return map1.grasses.size() + map2.grasses.size();
    }

    private double avgEnergyToReport(){
        return Math.round(((map1.avgEnergy() + map2.avgEnergy()) / 2) * 100.0) / 100.0;
    }

    private double avgDeadLifeToReport(){
        return Math.round(((map1.avgDeadLife() + map2.avgDeadLife()) / 2) * 100.0) / 100.0;
    }

    private int avgKidsAmountToReport(){
        return (map1.avgKidsAmount() + map2.avgKidsAmount()) / 2;
    }
}
