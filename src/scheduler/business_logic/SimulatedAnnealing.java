package scheduler.business_logic;

import scheduler.dbModels.Job;

import java.util.List;
import java.util.Random;

public class SimulatedAnnealing {

    /*private int suggested; //kísérletek száma a futás során
    private double accepted_ratio; //állapotváltozások aránya

    private double alpha; //konstans amivel a hőmérsékletet szorozzuk

    private int minStep;
    private int maxStep;

    private int steps;

    public void constants() {
    }

    *//**
     * Elfogadjuk-e a megadott lépéslehetőséget?
     *
     * @param nextIndex     szomszéd állapot
     * @param actualIndex aktuális állapot
     * @param c     aktuális hőmérséklet
     * @return igen, ha a lépés elfogadható.
     *//*
    private boolean accept(List<Job> jobList, int actualIndex, int nextIndex, double c) {
        Random r = new Random();
        int diff =
                jobList.get(actualIndex).getRunTime() - jobList.get(nextIndex).getRunTime();//célfüggvény hívás amivel összehasonlítjuk a jelengeli és a szomszéd állapotot
        if (diff < 0) {
            return true;
        } else {
            return (Math.exp((double) (-diff) / c) > r.nextDouble());
        }
    }

    *//**
     * Felfűtés fázisa
     * @param x aktuális állapot
     * @return legmagasabb hőmérséklet
     *//*
    protected double heating(Job x) {
        Random r = new Random();
        int index;
        int accepted;
        double c = 1.0;

        do {
            accepted = 0;
            c *= 2;
            for (int j = 0; j < suggested; j++) {
                index = r.nextInt(x.numberOfNeighbours());// kell egy függvény, hogy hány szomszédja avn az adott jobnak/operációnak
                if (accept(x, index, c)) {// ez az előző függvény meghívása
                    ++accepted;
                }
            }
        } while (accepted_ratio > ((double) accepted / suggested));
        return c;
    }

    protected Job randomWalk(Job x, double c) {
        Random rand = new Random();
        for (int j = 0; j < steps; j++) {
            int i = rand.nextInt(x.numberOfNeighbours());
            if (accept(x, i, c)) { //az első függvény hívása
                x.chooseNeighbour(i);
            }
        }
        return x;
    }

    *//**
     * Végrehajtja a szimulált hűtést.
     * @param x aktuális állapot
     *//*
    protected void annealing(State x) {

        double c;
        c = heating(x);
        steps = minStep;
        do {
            x=randomWalk(x, c);
            c = c * alpha;
            steps++;
        } while (steps < maxStep); }


    public Job solve(Job x) {
        x.fillRandom();
        annealing(x);
        return x;
    }}*/
    }
