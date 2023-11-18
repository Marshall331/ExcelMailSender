package application;

import java.util.ArrayList;

import application.control.Configuration;

public class Main {

    // public static void main(String[] args) {
    // Configuration.runApp(args);
    // }

    public static void main(String[] args) {
        ArrayList<Integer> totalCode = new ArrayList<>();
        System.out.println("Calcul du nombre de lignes de codes");
        System.out.println("Package Control");
        totalCode.add(53);
        System.out.println("Configuration.java : " + totalCode.get(totalCode.size() - 1));
        totalCode.add(49);
        System.out.println("SendMails.java : " + totalCode.get(totalCode.size() - 1));

        int total = 0;
        for (Integer i : totalCode) {
            total += i;
        }
        ;
        System.out.println("Nombre total de lignes : " + total);
    }
}
