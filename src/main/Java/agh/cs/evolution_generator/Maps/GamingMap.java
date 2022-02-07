package agh.cs.evolution_generator.Maps;

import agh.cs.evolution_generator.MapElements.Genom;
import agh.cs.evolution_generator.MapElements.Grass;
import agh.cs.evolution_generator.MapElements.Animal;

import java.util.*;

public class GamingMap{
    private int x;
    private int y;
    private Vector2d jungleLowerLeft;
    private Vector2d jungleUpperRight;
    private int grassEnergy; // energia dodawana po zjedzeniu rośliny - podawana przez użytkownika
    private int moveEnergy; // energia którą traci zwierzak za wykonanie ruchu;
    public int days; // ile dni działa już generator
    private int animalsAmount; // ile zwierząt będzie na początku
    private double jungleRatio;
    private double occupiedArea = calculatePercentage(); // opis linijka 34

    public Map<Vector2d, ArrayList<Animal>> animals = new LinkedHashMap<>(); // hash mapa zwierząt - lista zwierząt na danej pozycji
    public List<Animal> animalsList = new ArrayList<>(); // zwykła lista zwierząt
    public Map<Vector2d, Grass> grasses = new LinkedHashMap<>(); // hash mapa roślin
    public List<Animal> deadAnimals = new ArrayList<>();

    public GamingMap(int width, int height, double jungleRatio, int grassEnergy, int dayEnergy, int animalsAmount){
        this.x = width;
        this.y = height;
        this.jungleRatio = jungleRatio;
        this.grassEnergy = grassEnergy;
        this.jungleLowerLeft = new Vector2d((int)(width/2 - width*jungleRatio/2), (int)(height/2 - height*jungleRatio/2));
        this.jungleUpperRight = new Vector2d((int)(width/2 + width*jungleRatio/2), (int)(height/2 + height*jungleRatio/2));
        this.moveEnergy = dayEnergy;
        this.days = 0;
        this.animalsAmount = animalsAmount;
    }

    public int getWidth() {
        return x;
    }

    public int getHeight() {
        return y;
    }

    // poniższa funkcja oblicza (w zależności od rozmiaru mapy) przy jakim zapełnieniu mapy roślinami zatrzymać wypełnianie
    // chodzi o to, że gdy mapa będzie bardzo duża np 1000x1000 a wolnych będzie np 20 (stosunkowo mało) pozycji
    // to ciężko wylosować te pozycje dla roślin (możemy zapętlić w nieskończoność)
    // więc ta funkcja oblicza jaki procent wypełnienia jest dopuszczalny żeby program działał "znośnie"
    public double calculatePercentage(){ // zwracane wartości zapełnienia są losowymi propozycjami które wydawały się sensowne
        if(this.x * this.y > 1000000) return 0.7; // 70%
        if(this.x * this.y > 640000) return 0.75;
        if(this.x * this.y > 250000) return 0.8;
        if(this.x * this.y > 100000) return 0.85;
        if(this.x * this.y > 80000) return 0.9;
        if(this.x * this.y > 30000) return 0.95;
        return 1;
    }

    public String toString(){
        MapVisualizer visualizer = new MapVisualizer(this);
        return visualizer.draw(new Vector2d(0,0), new Vector2d(this.x, this.y));
    }

    public void mainRunGenerator(){ // główna funkcja odpowiedzialna za całą symulacje
        this.days++;
        run(); // przemieszczamy wszystkie zwierzęta
        eat(); // zwierzaki zjadają rośliny
        animalReproduction(); // rozmnażanie
        removeDeceased(); // usuwamy zmarłe zwierzęta
        if(grasses.size() <= occupiedArea * this.x * this.y){
            addNewGrass(randomPosInJungle()); // jedna nowa roślina w dżungli
            addNewGrass(randomPosOutsideJungle()); // jedna poza dżunglą
        }
    }

    public Vector2d checkPosAfterMove(Vector2d pos){
        if(pos.x >= this.x) pos.x = pos.x - this.x;
        else if(pos.x < 0) pos.x = this.x + pos.x;
        if(pos.y >= this.y) pos.y = pos.y - this.y;
        else if(pos.y < 0) pos.y = this.y + pos.y;
        return pos;
    }

    public Object objectAt(Vector2d position) {
        if(animals.get(position) != null) return animals.get(position); // zwróci listę zwierząt z danej pozycji
        return grasses.get(position);
    }

    public boolean objectAtJungle(Vector2d position){
        return position.x >= this.jungleLowerLeft.x &&
                position.x <= jungleUpperRight.x && position.y >= this.jungleLowerLeft.y &&
                position.y <= jungleUpperRight.y;
    }

    public void addAnimal(Animal animal){
        Vector2d pos = new Vector2d(animal.getPosition().x, animal.getPosition().y);

        if(animals.get(pos) == null){
            ArrayList<Animal> animalsAtPos = new ArrayList<>();
            animals.put(pos, animalsAtPos);
        }
        animals.get(pos).add(animal);
    }

    public void addNewGrass(Vector2d pos){ // dodaje roślinę - dziala dla dzungli i dla stepu
        // najpierw trzeba stworzyć nową roślinę
        if(pos != null){
            Grass grass = new Grass(pos, this);
            if(grasses.get(grass.getPosition()) == null && animals.get(grass.getPosition()) == null){
                Vector2d grassPos = new Vector2d(grass.getPosition().x, grass.getPosition().y);
                grasses.put(grassPos, grass);
            }
        }
    }
    public Vector2d randomPosInJungle(){ // losuje pozycję rośliny w dzungli
        int i;
        ArrayList<Vector2d> pos = possibleJunglePositions(jungleLowerLeft, jungleUpperRight);
        if(pos.size() > 0){
            i = (int)(Math.random() * pos.size());
            return pos.get(i);
        }
        return null;
    }

    public Vector2d randomPosOutsideJungle(){ // losuje pozycję rośliny w stepie
        int i;
        ArrayList<Vector2d> pos = possibleSteppePositions(new Vector2d(0,0), new Vector2d(this.x, this.y));
        if(pos.size()>0){
            i = (int)(Math.random() * pos.size());
            return pos.get(i);
        }
        return null;
    }

    //Wypisuje mozliwe do wyboru pozycje w dzungli (spośród nich losowana pozycja)
    public ArrayList<Vector2d> possibleJunglePositions(Vector2d lowerLeft, Vector2d upperRight){
        ArrayList<Vector2d> possiblePos = new ArrayList<>();
        for(int i = lowerLeft.x; i < upperRight.x;i++){
            for (int j = lowerLeft.y; j < upperRight.y;j++){
                if(objectAt(new Vector2d(i, j)) == null){
                    possiblePos.add(new Vector2d(i, j));
                }
            }
        }
        return possiblePos;
    }

    //Wypisuje mozliwe do wyboru pozycje w stepie (spośród nich losowana pozycja)
    public ArrayList<Vector2d> possibleSteppePositions(Vector2d lowerLeft, Vector2d upperRight){
        ArrayList<Vector2d> possiblePos = new ArrayList<>();
        for(int i = lowerLeft.x; i < upperRight.x;i++){
            for (int j = lowerLeft.y; j < upperRight.y;j++){
                if(objectAt(new Vector2d(i, j)) == null && !objectAtJungle(new Vector2d(x, y))){
                    possiblePos.add(new Vector2d(i, j));
                }
            }
        }
        return possiblePos;
    }

    public void run(){
        for(Animal animal: animalsList){
            Vector2d before = animal.getPosition();
            animals.get(before).remove(animal);
            if(animals.get(before).size() == 0) animals.remove(before);
            animal.move();
            animal.nextDay(this.moveEnergy); // aktualizacja wieku i energii
            addAnimal(animal);
        }
    }
    public void eat(){
        for(Animal animal: animalsList){
            if(grasses.containsKey(animal.getPosition())){
                // najpierw musimy sprawdzić jakie jeszcze zwierzaki poza rozpatrywanym stoją na tej samej pozycji
                ArrayList<Animal> animalsInThisPlace = animals.get(animal.getPosition());
                // następnie z tej listy wybieramy te najsilniejsze - one podzielą sie rosliną
                ArrayList<Animal> strongest = new ArrayList<>();
                int maxEnergy = 0;
                for(Animal a:animalsInThisPlace){ // wyszukujemy najsilniejszego zwierzaka
                    if(maxEnergy < a.getEnergy()){
                        maxEnergy = a.getEnergy();
                    }
                }

                for(Animal a:animalsInThisPlace){ // następnie najsilniejsze zwierzaki dodajemy do listy najsilniejszych
                    if(maxEnergy == a.getEnergy()){
                        strongest.add(a);
                    }
                }

                for(Animal a:strongest){ // każdemu zwierzakowi dodajemy odpowiednią energię
                    //System.out.println("zwierzak zjadl trawe. energia przed: " + a.getEnergy());
                    a.eat(grassEnergy / strongest.size());
                    //System.out.println(" energia po: "+ a.getEnergy());
                }

                grasses.remove(animal.getPosition()); // na koniec usuwamy trawę - została zjedzona
            }
        }
    }

    public void removeDeceased(){ // usuń wszystkie zmarłe zwierzęta
        ArrayList<Animal> animalsToRemove = new ArrayList<>();
        for(Animal a: animalsList){ // najpierw odnajdujemy zmarłe zwierzęta i dodajemy je do listy zwierząt do usunięcia
            if(a.getEnergy() <= 0) {
                animalsToRemove.add(a);
            }
        }
        for(Animal a:animalsToRemove){ // idąc przez listę zmarłych zwierząt usuwam je...
            Vector2d pos = a.getPosition();
            animals.get(pos).remove(a); // ... z listy zwierząt na danej pozycji w hashmapie oraz ...
            if(animals.get(pos).size() == 0) animals.remove(pos);
            animalsList.remove(a); // ... z listy wszystkich zwierząt
            deadAnimals.add(a); // dodanie do listy zmarłych zwierząt
            a.whenDead = this.days;
        }
    }

    public void animalReproduction(){
        ArrayList<Vector2d> positions = new ArrayList<>(animals.keySet()); //lista wszystkich pozycji na których są zwierzęta
        for(Vector2d pos:positions){
            if(animals.get(pos).size() >= 2){
                int[] strongest = findStrongest(animals.get(pos));
                Animal parent1 = animals.get(pos).get(strongest[0]);
                Animal parent2 = animals.get(pos).get(strongest[1]);
                if(parent1.getEnergy() >= 0.5*parent1.startingEnergy && parent2.getEnergy() >= 0.5*parent2.startingEnergy)
                    reproduce(parent1, parent2);
            }
        }
    }

    public int[] findStrongest(ArrayList<Animal> animals){ //funkcja pomocnicza wyszukująca dwa najsilniejsze zwierzaki do rozmnażania (zakładamy że rozmnażają się zawsze dwa najsilniejsze)
        int strongestID1 = -1;
        int strongestEnergy1 = -1;
        int strongestID2 = -1;
        int strongestEnergy2 = -1;
        int[] result = new int[2];

        for(int i = 0; i< animals.size(); i++){
            if(strongestEnergy1 < animals.get(i).getEnergy()){
                strongestEnergy2 = strongestEnergy1;
                strongestID2 = strongestID1;
                strongestEnergy1 = animals.get(i).getEnergy();
                strongestID1 = i;
            }
            else if(strongestEnergy2 < animals.get(i).getEnergy()){
                strongestEnergy2 = animals.get(i).getEnergy();
                strongestID2 = i;
            }
        }
        result[0] = strongestID1;
        result[1] = strongestID2;
        return result;
    }

    public void reproduce(Animal animal1, Animal animal2){ // reprodukcja - powstanie nowego zwierzaka
        Vector2d childPos = animal1.getPosition(); //na początku ustawiamy dziecko na pozycji rodzicow, pozniej przesuniemy
        int childEnergy = (int) (0.25 * animal1.getEnergy() + 0.25 * animal2.getEnergy());
        Genom childGens = new Genom(animal1.gen, animal2.gen);
        Animal child = new Animal(this, childPos, childEnergy, childGens);
        moveChildToEmptyPos(child); // nowy zwierzak zgodnie z instrukcją musi zajmować jedno z sąsiednich pustych pól do pola na którym został stworzony
        this.animalsList.add(child); // dodajemy zwierzaka do listy zwierzat
        addAnimal(child); // i dodajemy do hash mapy
        child.whenBorn = this.days; // ustawiamy kiedy zwierzak się urodził
        animal1.actualEnergy -= (int)(animal1.actualEnergy * 0.25); // odejmujemy zwierzakom ich energię z produkcji potomka
        animal2.actualEnergy -= (int)(animal2.actualEnergy * 0.25);
        animal1.kids.add(child); // dodajemy dziecko do listy dzieci rodzica
        animal2.kids.add(child);
    }

    public void moveChildToEmptyPos(Animal child){
        Vector2d oldChildPos = child.getPosition();
        Vector2d newChildPos;
        for(int i = -1; i < 2; i++){ //iterujemy po wszystkich możliwych kombinacjach sąsiednich pozycji
            for(int j = -1; j< 2; j++) {
                newChildPos = checkPosAfterMove(oldChildPos.add(new Vector2d(i, j)));
                if (isEmpty(newChildPos)){
                    child.moveTo(newChildPos);
                    return;
                }
            }
        }
        // jeżeli wszystkie pozycje dookoła są zajęte to wybieramy dowolną pozycję
        int x = (int)(Math.random() * 3) - 1;
        int y = (int)(Math.random() * 3) - 1;
        newChildPos = checkPosAfterMove(oldChildPos.add(new Vector2d(x, y)));
        child.moveTo(newChildPos);
    }
    public boolean isEmpty(Vector2d positon){
        return objectAt(positon) == null || objectAt(positon) instanceof Grass;
    }

    public int[] dominantGenotype(){
        Map<int[], Integer> genotypeCounter = new LinkedHashMap<>();
        for(Animal a: animalsList){
            if(genotypeCounter.containsKey(a.gen.orientations)){
               int val = genotypeCounter.get(a.gen.orientations);
               genotypeCounter.remove(a.gen.orientations);
               genotypeCounter.put(a.gen.orientations, val+1);
            }
            else{
                genotypeCounter.put(a.gen.orientations, 1);
            }
        }
        int[] dominantGenotype = new int[32];
        int maxVal = 0;
        for(int[] i: genotypeCounter.keySet()){
            if(maxVal < genotypeCounter.get(i)){
                maxVal = genotypeCounter.get(i);
                dominantGenotype = i;
            }
        }
        return dominantGenotype;
    }

    public double avgEnergy(){
        double sum = 0;
        for(Animal a: animalsList){
            sum += a.getEnergy();
        }
        return Math.round(sum/animalsList.size() * 100.0) / 100.0;
    }

    public double avgDeadLife(){
        double sum = 0;
        for(Animal a: deadAnimals){
            sum += a.age;
        }
        return Math.round(sum/deadAnimals.size() * 100.0) / 100.0;
    }

    public double avgAliveLife(){
        double sum = 0;
        for(Animal a: animalsList){
            sum += a.age;
        }
        return Math.round(sum/animalsList.size() * 100.0) / 100.0;
    }
    public int avgKidsAmount(){
        int sum = 0;
        for(Animal a: animalsList){
            sum += a.kids.size();
        }
        return sum / animalsList.size();
    }
}
