package agh.cs.evolution_generator.MapElements;

import java.util.ArrayList;
import java.util.Arrays;

public class Genom {
    public int[] orientations = new int[32]; // przechowuje geny zwierzaka

    public Genom(){
        fillGenomsArray();
    }

    public Genom(Genom firstParent, Genom secondParent){ // konstruktor przeznaczony dla dziecka (połączenie genów rodziców)
        // najpierw wybieramy punkty podziału genów rodziców
        int a = (int) (Math.random() * 16) + 1; // pierwszy punkt podziału będzie z przedziału [1, 16] żeby nie musieć później sprawdzać czy a > b
        int b = (int) (Math.random() * (30 - a - 2)) + (a + 2); // drugi punkt podziału będzie z przedziału [a+2, 30]

        // teraz zaczynamy
        this.orientations = new int[32]; // wynik mieszania genów
        for(int i = 0; i < a; i++){
            this.orientations[i] = firstParent.orientations[i];
        }
        for(int i = a; i < b; i++){
            this.orientations[i] = secondParent.orientations[i];
        }
        for(int i = b; i < 32; i++){
            this.orientations[i] = firstParent.orientations[i];
        }
        // na koniec sprawdzamy czy wszysktie kierunki znajdują się w genie
        fillGenomsArray();
    }
    public int[] printGen(){
        return orientations;
    }

    private void fillGenomsArray(){
        int[] options = new int[8]; // sluzy do sprawdzenia czy wszystkie kierunki zostały wykorzystane

        for(int i =  0; i < 8; i++){
            options[i] = 0; // na początku same zera bo nie wykorzystano jeszcze żadnego kierunku
        }
        for(int i = 0; i<32; i++){
            int dir = (int) (Math.random() * 8); // wybieramy liczbę z zakresu 0-7 i wpisujemy do genomu
            orientations[i] = dir;
            options[dir]++;
        }
        //teraz trzeba sprawdzic czy wszystkie kierunki zostaly wykorzystane w genie
        for(int i = 0; i < 8; i++){
            if(options[i] == 0){ // jeżeli ten kierunek nie został wykorzystany to musimy go wstawić kosztem jednego (losowego) kierunku w genomie
                int tmp;
                do{
                    tmp = (int) (Math.random() * 32); // losujemy pozycję do podmiany
                } while(options[orientations[tmp]] <= 1); // sprawdzamy czy wylosowany kierunek do podmiany występuje więcej niż raz, bo jezeli nie to nie możemy wstawić zamiast niego nowego kierunku - to spowodowałoby usunięcie teho kierunku z genomu

                options[orientations[tmp]]--; // skoro usunięto ten kierunek to zmniejszamy ilość o 1
                orientations[tmp] = i; // wpisujemy kierunek którego nie było
            }
        }
        Arrays.sort(orientations); // na koniec sortujemy genomy
    }

    public int chooseDirection(){ //
        int dir = (int) (Math.random() * 32);
        return this.orientations[dir];
    }
}