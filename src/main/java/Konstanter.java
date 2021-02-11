import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

class Konstanter {
    final static String path = "src\\main\\resources\\";

    //Nyckelordet "final" hindrar variabelns värde från
    //att ändras efter att den har initialiserats.
    //Variabeln blir alltså en konstant
    final static int
            FLAGGTYP_KORS = 0,
            FLAGGTYP_TRIKOLOR = 1,
            FLAGGTYP_BIKOLOR = 2,
            FLAGGTYP_ANNAN = 3,
            FLAGGTYP_SOLIDTRIVAGRAT = 4,
            FLAGGTYP_SOLIDTRILODRAT = 5;
    static ArrayList<Land>
            nordArray = new ArrayList<>(),
            ostArray = new ArrayList<>(),
            vastArray = new ArrayList<>(),
            sydArray = new ArrayList<>(),
            europaArray = new ArrayList<>(),
            blandadArray = new ArrayList<>();

    static ArrayList<String> fargerArray = new ArrayList<String>() {{
        add("röd");
        add("grön");
        add("brun");
        add("blå");
        add("gul");
        add("svart");
        add("vit");
        add("orange");
        add("grå");
    }},
            stadArray = new ArrayList<>(),
            landArray = new ArrayList<>();

    Konstanter() {
        try {
            //Guld -> gul, silver -> grå, beige -> brun


            //Norra Europa (norden + baltikum + irland/storbritannien)
            nordArray.add(new Land("Sverige", "Stockholm", FLAGGTYP_KORS, "blå gul", "5:8"));
            nordArray.add(new Land("Norge", "Oslo", FLAGGTYP_KORS, "röd vit blå", "8:11"));
            nordArray.add(new Land("Finland", "Helsingfors", FLAGGTYP_KORS, "vit blå", "11:18"));
            nordArray.add(new Land("Island", "Reykjavik", FLAGGTYP_KORS, "blå vit röd", "18:25"));
            nordArray.add(new Land("Danmark", "Köpenhamn", FLAGGTYP_KORS, "röd vit", "28:37"));
            nordArray.add(new Land("Estland", "Tallinn", FLAGGTYP_SOLIDTRIVAGRAT, "blå svart vit", "7:11"));
            nordArray.add(new Land("Irland", "Dublin", FLAGGTYP_SOLIDTRILODRAT, "grön vit orange", "1:2"));
            nordArray.add(new Land("Lettland", "Riga", FLAGGTYP_ANNAN, "röd vit", "1:2"));
            nordArray.add(new Land("Litauen", "Vilnius", FLAGGTYP_SOLIDTRIVAGRAT, "gul grön röd", "3:5"));
            nordArray.add(new Land("Storbritannien", "London", FLAGGTYP_KORS, "blå vit röd", "1:2"));
            Collections.shuffle(nordArray);
            europaArray.addAll(nordArray);

            //Östeuropa
            ostArray.add(new Land("Vitryssland", "Minsk", FLAGGTYP_ANNAN, "röd grön vit", "1:2"));
            ostArray.add(new Land("Bulgarien", "Sofia", FLAGGTYP_TRIKOLOR, "vit grön röd", "3:5"));
            ostArray.add(new Land("Tjeckien", "Prag", FLAGGTYP_ANNAN, "blå vit röd", "2:3"));
            ostArray.add(new Land("Ungern", "Budapest", FLAGGTYP_SOLIDTRIVAGRAT, "röd vit grön", "1:2"));
            ostArray.add(new Land("Moldavien", "Chisinau", FLAGGTYP_TRIKOLOR, "blå gul röd brun grön", "1:2"));
            ostArray.add(new Land("Polen", "Warszawa", FLAGGTYP_BIKOLOR, "vit röd", "5:8"));
            ostArray.add(new Land("Ryssland", "Moskva", FLAGGTYP_SOLIDTRIVAGRAT, "vit blå röd", "2:3"));
            ostArray.add(new Land("Slovakien", "Bratislava", FLAGGTYP_TRIKOLOR, "vit blå röd", "2:3"));
            ostArray.add(new Land("Ukraina", "Kiev", FLAGGTYP_BIKOLOR, "blå gul", "2:3"));
            ostArray.add(new Land("Rumänien", "Bukarest", FLAGGTYP_SOLIDTRILODRAT, "blå gul röd", "2:3"));
            Collections.shuffle(ostArray);
            europaArray.addAll(ostArray);

            //Västeuropa
            vastArray.add(new Land("Österrike", "Wien", FLAGGTYP_ANNAN, "röd vit", "2:3"));
            vastArray.add(new Land("Belgien", "Bryssel", FLAGGTYP_SOLIDTRILODRAT, "svart gul röd", "13:15"));
            vastArray.add(new Land("Frankrike", "Paris", FLAGGTYP_SOLIDTRILODRAT, "blå vit röd", "2:3"));
            vastArray.add(new Land("Tyskland", "Berlin", FLAGGTYP_SOLIDTRIVAGRAT, "svart röd gul", "3:5"));
            vastArray.add(new Land("Liechtenstein", "Vaduz", FLAGGTYP_BIKOLOR, "blå röd gul", "3:5"));
            vastArray.add(new Land("Luxemburg", "Luxemburg", FLAGGTYP_SOLIDTRIVAGRAT, "röd vit blå", "3:5"));
            vastArray.add(new Land("Monaco", "Monaco", FLAGGTYP_BIKOLOR, "röd vit", "4:5"));
            vastArray.add(new Land("Schweiz", "Bern", FLAGGTYP_KORS, "röd vit", "1:1"));
            vastArray.add(new Land("Nederländerna", "Amsterdam", FLAGGTYP_SOLIDTRIVAGRAT, "röd vit blå", "2:3"));
            Collections.shuffle(vastArray);
            europaArray.addAll(vastArray);

            //Södra Europa
            sydArray.add(new Land("Albanien", "Tirana", FLAGGTYP_ANNAN, "röd svart", "5:7"));
            sydArray.add(new Land("Nordmakedonien", "Skopje", FLAGGTYP_ANNAN, "röd gul", "1:2"));
            sydArray.add(new Land("Malta", "Valletta", FLAGGTYP_BIKOLOR, "röd vit grå", "2:3"));
            sydArray.add(new Land("Serbien", "Belgrad", FLAGGTYP_TRIKOLOR, "röd blå vit gul", "2:3"));
            sydArray.add(new Land("Andorra", "Andorra la Vella", FLAGGTYP_TRIKOLOR, "blå gul röd brun vit", "7:10"));
            sydArray.add(new Land("Bosnien och Hercegovina", "Sarajevo", FLAGGTYP_ANNAN, "blå gul vit", "1:2"));
            sydArray.add(new Land("Kroatien", "Zagreb", FLAGGTYP_TRIKOLOR, "röd vit blå gul", "1:2"));
            sydArray.add(new Land("Grekland", "Aten", FLAGGTYP_KORS, "blå vit", "2:3"));
            sydArray.add(new Land("Italien", "Rom", FLAGGTYP_SOLIDTRILODRAT, "grön vit röd", "2:3"));
            sydArray.add(new Land("Montenegro", "Podgorica", FLAGGTYP_ANNAN, "röd gul blå grön", "1:2"));
            sydArray.add(new Land("Portugal", "Lissabon", FLAGGTYP_ANNAN, "grön röd gul vit blå", "2:3"));
            sydArray.add(new Land("San Marino", "Dogana", FLAGGTYP_BIKOLOR, "vit blå gul grön", "3:4"));
            sydArray.add(new Land("Slovenien", "Ljubljana", FLAGGTYP_TRIKOLOR, "vit blå röd gul", "1:2"));
            sydArray.add(new Land("Spanien", "Madrid", FLAGGTYP_ANNAN, "röd gul", "2:3"));
            sydArray.add(new Land("Vatikanstaten", "Vatikanstaden", FLAGGTYP_BIKOLOR, "gul vit grå röd", "1:1"));
            Collections.shuffle(sydArray);
            europaArray.addAll(sydArray);

            Collections.shuffle(europaArray);

            for (int i = 0; i < 10; i++) blandadArray.add(europaArray.get(i));

        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            BufferedReader reader = new BufferedReader(new FileReader(path + "stad.txt"));
            String line = reader.readLine();

            while (line != null) // när en rad finns i textdokumentet.
            {
                try {
                    System.out.println("line = " + line);
                    stadArray.add(line);
                } catch (NumberFormatException e1) {
                    System.err.println("ignorerar ogiltig stad: " + line);
                }
                line = reader.readLine();
            }
            reader.close();
            reader = new BufferedReader(new FileReader(path + "land.txt"));
            line = reader.readLine().trim();
            while (line != null) // när en rad finns i textdokumentet.
            {
                try {
                    landArray.add(line);
                } catch (NumberFormatException e1) {
                    System.err.println("ignorerar ogiltigt land: " + line);
                }
                line = reader.readLine();
            }

        } catch (IOException ex) {
            System.err.println("Det gick inte att läsa filen (huvudstad/land) ");
        }


    }

}

