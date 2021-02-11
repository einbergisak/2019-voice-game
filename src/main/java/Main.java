import java.util.Scanner;

public class Main {

    //stationär G:\My Drive\.GA\keys\serviceaccadmingcp\einbergga-cc4aacfa16d8.json
    //laptop G:\Min enhet\.GA\keys\serviceaccadmingcp\einbergga-cc4aacfa16d8.json

    static Frame f;
    static Spel.Rita rita = new Spel.Rita();

    public static void main(String... args) {
        System.setProperty("file.encoding", "UTF-8");
        f = new Frame();
        if (args.length != 0) {
            if (args[0].equals("keyboardmode")) {
                Scanner sc = new Scanner(System.in);
                String k;
                while (true) {
                    k = sc.nextLine();
                    Logik.check(k);
                }
            }
        } else {
            try {
                Taligenkanning.starta("sv-SE");
            } catch (Exception e) {
                System.out.println("Exception caught: " + e);
            }
        }


    }

    static void avsluta() {
        Taligenkanning.inspelningKlar = true;
        f.dispose();
        System.exit(0);
    }

}


