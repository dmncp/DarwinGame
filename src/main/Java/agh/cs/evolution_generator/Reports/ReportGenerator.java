package agh.cs.evolution_generator.Reports;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

public class ReportGenerator {
    int days1, days2, animalAmount, grassesAmount, avgKidsAmount;
    int[] dominantGenotype1, dominantGenotype2;
    double avgEnergy, avgLife;
    int reportID;

    public ReportGenerator(int id, int days1, int days2, int animalAmount, int grassesAmount, int[] dominantGenotype1, int[] dominantGenotype2, double avgEnergy, double avgLife, int avgKidsAmount) {
        this.days1 = days1;
        this.days2 = days2;
        this.animalAmount = animalAmount;
        this.grassesAmount = grassesAmount;
        this.avgKidsAmount = avgKidsAmount;
        this.dominantGenotype1 = dominantGenotype1;
        this.dominantGenotype2 = dominantGenotype2;
        this.avgEnergy = avgEnergy;
        this.avgLife = avgLife;
        this.reportID = id;
    }

    public void main(){
        File report = createNewReport();
        reportID++;
        String dataForReport = createReportInformation();
        writeToFile(report, dataForReport);
    }

    public File createNewReport(){
        File report = null;
        try{
            report = new File("src/main/Java/agh/cs/evolution_generator/Reports/report" + reportID + ".txt");
            boolean res = report.createNewFile();
        } catch (IOException e){
            e.printStackTrace();
        }
        return report;
    }

    public void writeToFile(File fileName, String txt){
        try{
            FileOutputStream outputStream = new FileOutputStream(fileName);
            byte[] strToBytes = txt.getBytes();
            outputStream.write(strToBytes);

            outputStream.close();
        } catch(IOException e){
            e.printStackTrace();
        }
    }
    public String createReportInformation(){
        String info1 = "Raport wygenerowany po " + days1 + " dniach na mapie 1 i po " + days2 + " na mapie 2.\n";
        String info2 = "Liczba wszystkich zwierząt jakie pojawiły się w tym czasie na obu mapach: " + animalAmount + "\n";
        String info3 = "Liczba wszystkich roślin jakie pojawiły się w tym czasie na obu mapach: " + grassesAmount + "\n";
        String info4 = "Dominujący genotyp na mapie 1: " + Arrays.toString(dominantGenotype1) + "\n";
        String info5 = "Dominujący genotyp na mapie 2: " + Arrays.toString(dominantGenotype2) + "\n";
        String info6 = "Średnia ilość energi żyjących zwierząt: " + avgEnergy + "\n";
        String info7 = "Średnia długość życia martwych zwierząt: " + avgLife + "\n";
        String info8 = "Średnia ilość dzieci żyjących zwierząt: " + avgKidsAmount + "\n";

        return info1 + info2 + info3 + info4 + info5 + info6 + info7 + info8;
    }

}
