package application;

import java.util.ArrayList;

import application.control.Configuration;

public class Main {

    public static void main(String[] args) {
        Configuration.runApp(args);
        // showLine(args);
    }

    public static void showLine(String[] args) {
        ArrayList<Integer> totalCode = new ArrayList<>();
        System.out.println("Calcul du nombre de lignes de codes total...");

        System.out.println("Package control :");
        totalCode.add(53);
        System.out.println("Configuration.java : " + totalCode.get(totalCode.size() - 1));
        totalCode.add(49);
        System.out.println("SendMails.java : " + totalCode.get(totalCode.size() - 1));

        System.out.println("Package tools :");
        totalCode.add(83);
        System.out.println("AlertUtilities.java : " + totalCode.get(totalCode.size() - 1));
        totalCode.add(22);
        System.out.println("StageManagement.java : " + totalCode.get(totalCode.size() - 1));

        System.out.println("Package view :");
        totalCode.add(823);
        System.out.println("ConfigurationController.java : " + totalCode.get(totalCode.size() - 1));
        totalCode.add(394);
        System.out.println("SendMailsController.java : " + totalCode.get(totalCode.size() - 1));

        System.out.println("Package visualEffects :");
        totalCode.add(43);
        System.out.println("Animations.java : " + totalCode.get(totalCode.size() - 1));
        totalCode.add(39);
        System.out.println("Style.java : " + totalCode.get(totalCode.size() - 1));

        System.out.println("Package model :");
        totalCode.add(60);
        System.out.println("ConfigurationSave.java : " + totalCode.get(totalCode.size() - 1));
        totalCode.add(49);
        System.out.println("FileReader.java : " + totalCode.get(totalCode.size() - 1));
        totalCode.add(122);
        System.out.println("MailSender.java : " + totalCode.get(totalCode.size() - 1));
        totalCode.add(41);
        System.out.println("SaveManagement.java : " + totalCode.get(totalCode.size() - 1));
        totalCode.add(77);
        System.out.println("ServerBaseConfiguration.java : " + totalCode.get(totalCode.size() - 1));

        int total = 0;
        for (Integer i : totalCode) {
            total += i;
        }
        ;
        System.out.println("Nombre total de lignes : " + total);
    }
}
