import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

class Spel {

    static class BildMedProp {
        final BufferedImage bild;
        final String prop;

        BildMedProp(BufferedImage bild, String bildproportioner) {
            this.bild = bild;
            this.prop = bildproportioner;
        }
    }

    static Land land;
    static boolean
            flaggrev,
            namnrev,
            flaggtyprev,
            huvudstadrev,
            formrev,
            saAvsluta = false;

    private static int antalLanderVisade = 0;
    private static double andelKorrekt, tid;
    private static BufferedImage unknownFlagBild, avslutaBild, villAvslutaBild;
    private static boolean spelRedanKlart = false, uppdaterad = false;
    static ArrayList<BildMedProp> bmpList;

    static {
        try {
            unknownFlagBild = ImageIO.read(new File(Konstanter.path + "bilder\\unknownflag.png"));
            avslutaBild = ImageIO.read(new File(Konstanter.path + "bilder\\closewindow.jpg"));
            villAvslutaBild = ImageIO.read(new File(Konstanter.path + "bilder\\villavsluta" + ".jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    final static int
            SG_ENKEL = 0,
            SG_MEDEL = 1,
            SG_SVAR = 2,
            SG_OVERDRIVET_SVAR = 3;

    static int svarighetsgrad, antalFargerVisade = 0;
    static double antalFel = 0, antalKorrekt = 0;
    static private BildMedProp
            bmp1,
            bmp2,
            bmp3;
    static ArrayList<Land> landArray;
    private static double silhuettRot;
    static String senasteFelsvar = "", varldsdel = "";


    static void visaNyttLand() {
        spelRedanKlart = false;
        Logik.redanKorrekta = new ArrayList<>();

        formrev = false;
        flaggtyprev = false;
        flaggrev = false;
        namnrev = false;
        huvudstadrev = false;
        antalFargerVisade = 0;


        land = landArray.get(antalLanderVisade);

        if (svarighetsgrad == SG_ENKEL) {

            huvudstadrev = true;
            formrev = true;
            flaggtyprev = true;
            flaggrev = true;

        } else if (svarighetsgrad == SG_MEDEL || svarighetsgrad == SG_SVAR) {
            flaggtyprev = true;
        }


        if (svarighetsgrad != SG_ENKEL) {
            if (svarighetsgrad == SG_OVERDRIVET_SVAR) {
                //Vinkeln slumpas här för att den inte ska uppdateras varje gång spelet uppdaterar det som visas.
                // 2 pi radianer = 360 grader.
                silhuettRot = Math.random() * 2 * Math.PI;
            }

            ArrayList<BufferedImage> liknandeFlaggor;
            ArrayList<String> flaggProportioner;
            ArrayList<Land> allaLand = Konstanter.europaArray;
            liknandeFlaggor = new ArrayList<>();
            Collections.shuffle(allaLand);
            flaggProportioner = new ArrayList<>();

            String tidigareNamn = "";
            String tidigareProp = "";
            for (int i = 0; true; i++) {
//                System.out.println("Jämför " + allaLand.get(i).namn + " med " + land.namn);

                boolean approved = false;

                //För "SG_MEDEL" behöver inte flaggorna vara av samma typ.
                if (!tidigareNamn.equals(allaLand.get(i).namn)) {
                    if (svarighetsgrad == SG_MEDEL && allaLand.get(i) != land) {
                        approved = true;
                        tidigareNamn = allaLand.get(i).namn;
                        tidigareProp = allaLand.get(i).flaggProp;
                    } else if (svarighetsgrad != SG_MEDEL && allaLand.get(i) != land) {

                        if ((land.flaggtyp == Konstanter.FLAGGTYP_SOLIDTRIVAGRAT || land.flaggtyp == Konstanter.FLAGGTYP_SOLIDTRILODRAT || land.flaggtyp == Konstanter.FLAGGTYP_TRIKOLOR)
                                && (allaLand.get(i).flaggtyp == Konstanter.FLAGGTYP_SOLIDTRIVAGRAT || allaLand.get(i).flaggtyp == Konstanter.FLAGGTYP_SOLIDTRILODRAT || allaLand.get(i).flaggtyp == Konstanter.FLAGGTYP_TRIKOLOR)) {
                            if (!land.flaggProp.equals(allaLand.get(i).flaggProp)) {
                                if (liknandeFlaggor.size() == 1) {
                                    if (!tidigareProp.equals(allaLand.get(i).flaggProp)) {
                                        approved = true;
                                    }
                                } else {
                                    approved = true;
                                    tidigareNamn = allaLand.get(i).namn;
                                    tidigareProp = allaLand.get(i).flaggProp;
                                }

                            }
                        } else if (allaLand.get(i).flaggtyp == land.flaggtyp) {
                            approved = true;
                            tidigareNamn = allaLand.get(i).namn;
                            tidigareProp = allaLand.get(i).flaggProp;
                        }
                    }
                }


                if (approved && svarighetsgrad != SG_MEDEL) {
                    liknandeFlaggor.add(allaLand.get(i).flaggBilderArray.get(0));
                    flaggProportioner.add(allaLand.get(i).flaggProp);
                    System.out.println("Tillagd!!");
                    if (liknandeFlaggor.size() == 2) {
                        System.out.println("BREAK");
                        break;
                    }
                } else if (approved) {
                    liknandeFlaggor.add(allaLand.get(i).flaggBilderArray.get(allaLand.get(i).flaggBilderArray.size() - 1));
                    flaggProportioner.add(allaLand.get(i).flaggProp);
                    System.out.println("Tillagd!!");
                    if (liknandeFlaggor.size() == 2) {
                        System.out.println("BREAK");
                        break;
                    }
                }


            }
            if (svarighetsgrad != SG_MEDEL) {
                liknandeFlaggor.add(land.flaggBilderArray.get(0));
            } else {
                liknandeFlaggor.add(land.flaggBilderArray.get(land.flaggBilderArray.size() - 1));
            }
            flaggProportioner.add(land.flaggProp);

            bmp1 = new BildMedProp(liknandeFlaggor.get(0), flaggProportioner.get(0));
            bmp2 = new BildMedProp(liknandeFlaggor.get(1), flaggProportioner.get(1));
            bmp3 = new BildMedProp(liknandeFlaggor.get(2), flaggProportioner.get(2));

            bmpList = new ArrayList<BildMedProp>() {{
                add(bmp1);
                add(bmp2);
                add(bmp3);
            }};
            Collections.shuffle(bmpList);
        }
        antalLanderVisade += 1;

        System.out.println("Nytt land förbereds...");
        Main.rita.repaint();
        System.out.println("Nytt land visas.");


    }

    public static class Rita extends JComponent {
        @Override
        protected void paintComponent(Graphics g2) {
            Graphics2D g = (Graphics2D) g2;
            if (saAvsluta) {
                g.drawImage(villAvslutaBild, 1640, 0, this);
            } else {
                g.drawImage(avslutaBild, 1830, 0, this);
            }
            if (antalFel == 0) {
                andelKorrekt = 100.00;
            } else {
                andelKorrekt = (antalKorrekt * 100) / (antalFel + antalKorrekt);
            }

            if (!Frame.iHuvudmeny && !spelRedanKlart) {


                DecimalFormat nF = new DecimalFormat("#.00");

                g.setFont(new Font(Font.SANS_SERIF, Font.ITALIC, 32));
                if (antalKorrekt == 0) {
                    g.drawString("Andel korrekta svar: 0,00%", 15, 35);
                } else if (antalFel == 0 && antalKorrekt >= 1) {
                    g.drawString("Andel korrekta svar: 100,00%", 15, 35);
                } else {
                    g.drawString("Andel korrekta svar: " + nF.format(andelKorrekt) + "%", 15, 35);
                }
                g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 26));
                g.drawString("Land " + antalLanderVisade + " av " + landArray.size(), 15, 65);
                g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 26));
                g.setColor(Color.red);
                g.drawString(senasteFelsvar, 15, 500);
                g.setColor(Color.black);

                g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 50));
                if (namnrev) {
                    g.drawString("Namn: " + land.namn, 800, 700);
                } else {
                    g.drawString("Namn: ?", 800, 700);
                }

                g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 30));
                if (huvudstadrev) {
                    g.drawString("Huvudstad: " + land.huvudstad, 800, 750);
                } else {
                    g.drawString("Huvudstad: ?", 800, 750);
                }

                if (svarighetsgrad == SG_ENKEL) {
                    g.drawImage(land.silhuett, 800, 250, this);
                    g.drawImage(land.flaggBilderArray.get(land.flaggFargerArray.size()), 1500, 40, this);

                } else if (svarighetsgrad == SG_MEDEL) {
                    g.drawImage(land.silhuett, 800, 250, this);

                    if (!formrev) {
                        System.out.println("visar tre olika flaggor");
                        g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 24));
                        g.drawString("Vilken flagga är rätt?", 1545, 35);
                        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
                        g.drawString("1 (" + bmpList.get(0).prop + ")", 1400, 150);
                        g.drawImage(bmpList.get(0).bild, 1500, 50, this);
                        g.drawString("2 (" + bmpList.get(1).prop + ")", 1400, 380);
                        g.drawImage(bmpList.get(1).bild, 1500, 280, this);
                        g.drawString("3 (" + bmpList.get(2).prop + ")", 1400, 610);
                        g.drawImage(bmpList.get(2).bild, 1500, 510, this);

                    } else {
                        g.drawImage(land.flaggBilderArray.get(land.flaggFargerArray.size()), 1500, 40, this);
                        g.drawString("Flaggan färdig.", 1500, 270);
                    }


                } else if (svarighetsgrad == SG_SVAR) {
                    g.drawImage(land.silhuett, 800, 250, this);

                    if (!formrev) {
                        System.out.println("visar tre olika flaggor");
                        g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 24));
                        g.drawString("Vilken flagga är rätt?", 1545, 35);
                        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
                        g.drawString("1 (" + bmpList.get(0).prop + ")", 1400, 150);
                        g.drawImage(bmpList.get(0).bild, 1500, 50, this);
                        g.drawString("2 (" + bmpList.get(1).prop + ")", 1400, 380);
                        g.drawImage(bmpList.get(1).bild, 1500, 280, this);
                        g.drawString("3 (" + bmpList.get(2).prop + ")", 1400, 610);
                        g.drawImage(bmpList.get(2).bild, 1500, 510, this);

                    } else if (!flaggrev) {
                        g.drawImage(land.flaggBilderArray.get(antalFargerVisade), 1500, 40, this);
                        g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 25));
                        g.drawString("Säg färgen som hör till fält " + (antalFargerVisade + 1), 1500, 270);
                    } else {
                        g.drawImage(land.flaggBilderArray.get(land.flaggFargerArray.size()), 1500, 40, this);
                        g.drawString("Flaggan färdig.", 1500, 270);
                    }

                } else if (svarighetsgrad == SG_OVERDRIVET_SVAR) {

                    //Får ut absolutbeloppet för sinus och cosinus för den tidigare slumpade vinkeln.
                    // Ger alltså alltid ett positivt värde.
                    double sin = Math.abs(Math.sin(silhuettRot)), cos = Math.abs(Math.cos(silhuettRot));

                    //Skapar variabler med värden för bildens höjd/bredd före och efter rotationen
                    double bredd = land.silhuett.getWidth(), hojd = land.silhuett.getHeight();
                    int nyBredd = (int) Math.floor(bredd * cos + hojd * sin), nyHojd = (int) Math.floor(hojd * cos + bredd * sin);

                    //Skapar bilden som ska roteras (bi) och en Graphics2D (utöver "g" som redan finns).
                    //Allt som denna Graphics2D kommer att rita (bilden "bi") kommer att roteras.
                    //Det behövs en separat Graphics2D (utöver "g") för att inte rotera ALLT som ska visas.
                    BufferedImage bi = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice()
                            .getDefaultConfiguration().createCompatibleImage(nyBredd, nyHojd, Transparency.TRANSLUCENT);
                    Graphics2D g3 = bi.createGraphics();

                    //Ser till så att bildens "bounds", d.v.s. upptagningsområde flyttas till rätt ställe efter rotation.
                    g3.translate((nyBredd - bredd) / 2, (nyHojd - hojd) / 2);

                    //Roterar bilden
                    g3.rotate(silhuettRot, bredd / 2, hojd / 2);
                    g3.drawRenderedImage(land.silhuett, null);
                    g3.dispose();

                    //Ritar den roterate bilden
                    g.drawImage(bi, 800, 250, this);
                    if (flaggtyprev) {
                        if (!formrev) {
                            System.out.println("visar tre olika flaggor");
                            g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 24));
                            g.drawString("Vilken flagga är rätt?", 1545, 35);
                            g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
                            g.drawString("1 (" + bmpList.get(0).prop + ")", 1400, 150);
                            g.drawImage(bmpList.get(0).bild, 1500, 50, this);
                            g.drawString("2 (" + bmpList.get(1).prop + ")", 1400, 380);
                            g.drawImage(bmpList.get(1).bild, 1500, 280, this);
                            g.drawString("3 (" + bmpList.get(2).prop + ")", 1400, 610);
                            g.drawImage(bmpList.get(2).bild, 1500, 510, this);

                        } else if (!flaggrev) {
                            g.drawImage(land.flaggBilderArray.get(antalFargerVisade), 1500, 40, this);
                            g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 25));
                            g.drawString("Säg färgen som hör till fält " + (antalFargerVisade + 1), 1500, 270);
                        } else {
                            g.drawImage(land.flaggBilderArray.get(land.flaggFargerArray.size()), 1500, 40, this);
                            g.drawString("Flaggan färdig.", 1500, 270);
                        }

                    } else {

                        g.drawImage(unknownFlagBild, 1500, 40, this);
                    }

                }


                //Om allt är gissat.
                if ((svarighetsgrad == SG_ENKEL && namnrev) ||
                        ((svarighetsgrad == SG_MEDEL || svarighetsgrad == SG_SVAR) && namnrev && huvudstadrev && flaggrev) ||
                        (svarighetsgrad == SG_OVERDRIVET_SVAR && namnrev && huvudstadrev && flaggrev)) {


                    if (antalLanderVisade == landArray.size() && !uppdaterad) {
                        uppdaterad = true;
                        Main.rita.repaint();
                    } else if (uppdaterad) {
                        spelRedanKlart = true;
                        tid = Tid.tidSomPasserat();

                        if (landArray == Konstanter.blandadArray) {
                            sparaHighscore(0);
                        } else if (landArray == Konstanter.nordArray) {
                            sparaHighscore(1);
                        } else if (landArray == Konstanter.sydArray) {
                            sparaHighscore(2);
                        } else if (landArray == Konstanter.vastArray) {
                            sparaHighscore(3);
                        } else if (landArray == Konstanter.ostArray) {
                            sparaHighscore(4);
                        }


                        Main.f.dispose();

                        System.exit(0);
                    } else {
                        System.out.println("Landet gissades korrekt");
                        Logik.tidigare.add(land);
                        Taligenkanning.nyssKorrekt = true;
                        visaNyttLand();

                    }
                }


            }
        }

    }

    private static void sparaHighscore(int varldsdel) {
        File rekordFil;
        String vd;
        String sgrad;
        switch (varldsdel) {
            case 0:
                vd = "Blandad";
                break;
            case 1:
                vd = "Nordeuropa";
                break;
            case 2:
                vd = "Sydeuropa";
                break;
            case 3:
                vd = "Västeuropa";
                break;
            case 4:
                vd = "Östeuropa";
                break;
            default:
                vd = "[error]";
                break;
        }
        switch (svarighetsgrad) {
            case 0:
                sgrad = "enkelt";
                break;
            case 1:
                sgrad = "medel";
                break;
            case 2:
                sgrad = "svårt";
                break;
            case 3:
                sgrad = "överdrivet svårt";
                break;
            default:
                sgrad = "[error]";
                break;
        }

        rekordFil = (new File(Konstanter.path + "highscores\\" + vd + svarighetsgrad + ".txt"));


        NumberFormat nf = NumberFormat.getNumberInstance(Locale.GERMAN);
        DecimalFormat nF = (DecimalFormat) nf;
        nF.setMaximumFractionDigits(2);
        ArrayList<String> highscore;
        String rektid;
        double highScoreDouble;

        highscore = hittaHighscore(rekordFil);

        if (highscore.size() == 0) {
            highScoreDouble = 0;
        } else {
            highScoreDouble = Double.parseDouble(highscore.get(0));
        }


        int min;
        double s;


        String namn;

        min = (int) Math.floor(tid / 60);
        s = tid % 60;
        String tiden = "";
        if (min == 0) {
            tiden += nF.format(s) + " s";
        } else {
            tiden += min + " min " + nF.format(s) + " s";
        }


        if (andelKorrekt > highScoreDouble) {
            System.out.println("Nytt highscore " + nF.format(andelKorrekt));
            namn = JOptionPane.showInputDialog(Main.f, nF.format(andelKorrekt) + "% på '" + vd + " " + sgrad + "' med tiden " + tiden + " är nytt rekord!\nVänligen ange ditt namn:");
        } else if (andelKorrekt == highScoreDouble) {
            if (tid < Double.parseDouble(highscore.get(2))) {
                System.out.println("Nytt highscore " + nF.format(andelKorrekt));
                namn = JOptionPane.showInputDialog(Main.f, nF.format(andelKorrekt) + "% på '" + vd + " " + sgrad + "' med tiden " + tiden + " är nytt rekord!\nVänligen ange ditt namn:");
            } else {
                min = (int) Math.floor(Double.parseDouble(highscore.get(2)) / 60);
                s = Double.parseDouble(highscore.get(2)) % 60;
                rektid = "" + min + " min " + nF.format(s) + " s";
                namn = JOptionPane.showInputDialog(Main.f, "Spelet färdigt. Ditt resultat: " + nF.format(andelKorrekt) + "% på '" + vd + " " + sgrad + "' med tiden " + tiden + ".\nHighscore: " + highscore.get(0) + "% av " + highscore.get(1) + " med tiden " + rektid + "\nVänligen ange ditt namn:");
            }

        } else {
            min = (int) Math.floor(Double.parseDouble(highscore.get(2)) / 60);
            s = Double.parseDouble(highscore.get(2)) % 60;
            rektid = "" + min + " min " + nF.format(s) + " s";

            namn = JOptionPane.showInputDialog(Main.f, "Spelet färdigt. Ditt resultat: " + nF.format(andelKorrekt) + "% på '" + vd + " " + sgrad + "' med tiden " + tiden + ".\nHighscore: " + highscore.get(0) + "% av " + highscore.get(1) + " med tiden " + rektid + "\nVänligen ange ditt namn:");

        }


        if (namn.equals("")) {
            namn = "anonym";
        }

        if (namn.contains("-")) {
            namn = namn.replaceAll("-", "_");
        }

        try {
            String andelKorrektString = nF.format(andelKorrekt).replaceAll(",", ".");
            BufferedWriter output = new BufferedWriter(new FileWriter(rekordFil, true));
            output.newLine();
            output.append(andelKorrektString).append("-").append(namn).append("-").append(String.valueOf(tid));
            output.close();

        } catch (IOException ex1) {
            System.out.print("ERROR ");
        }
        System.out.println("Spel.sparaHighscore 3");

    }

    static ArrayList<String> hittaHighscore(File rekordFil) {
        if (!rekordFil.exists()) {
            try (FileOutputStream skriv = new FileOutputStream(rekordFil)) {
                String s = "";
                skriv.write(s.getBytes());
                skriv.flush();


            } catch (IOException e) {
                System.out.println("Fel vid rekordlagring.");
            }
        }

        double highScore = 0;
        ArrayList<String> c;
        ArrayList<String> hs = new ArrayList<>();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(rekordFil), StandardCharsets.ISO_8859_1));
            String line = reader.readLine();

            while (line != null) // när en rad finns i textdokumentet.
            {
                try {
                    c = new ArrayList<>();
                    Collections.addAll(c, line.trim().split("-"));
                    double score = Double.parseDouble(c.get(0));   // en rad i taget i form av double
                    double tid = Double.parseDouble(c.get(2));
                    if (score > highScore || (score == highScore && tid < Double.parseDouble(hs.get(2)))) {
                        hs = c;
                        hs.set(0, hs.get(0).replace(".", ","));
                        highScore = score;
                    }
                } catch (NumberFormatException e1) {
                    System.err.println("ignorerar ogiltigt resultat: '" + line + "'");
                }
                line = reader.readLine();
            }
            reader.close();


        } catch (IOException ex) {
            System.err.println("Det gick inte att läsa filen: " + rekordFil.getPath());
        }


        return hs;

    }

}
