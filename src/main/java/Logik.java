import java.util.ArrayList;

class Tid {
    private static long start;

    static void startaTiden() {
        start = System.currentTimeMillis();
    }

    static double tidSomPasserat() {
        long nu = System.currentTimeMillis();
        return (nu - start) / 1000.0;
    }
}

class Logik {
    static ArrayList<Land> tidigare = new ArrayList<>();

    static ArrayList<String> redanKorrekta = new ArrayList<>();

    private static void felsvar(String s) {
        if (!Taligenkanning.nyssFelsvar) {
            Spel.senasteFelsvar = s;
        }
        Spel.antalFel += 1;
        Taligenkanning.nyssFelsvar = true;
        Main.rita.repaint();
    }

    private static void korrektSvar(String typ) {
        if (typ.equals("flaggrev")) {
            if ((((Spel.svarighetsgrad == Spel.SG_SVAR || Spel.svarighetsgrad == Spel.SG_OVERDRIVET_SVAR) && Spel.antalFargerVisade == Spel.land.flaggFargerArray.size())) || Spel.svarighetsgrad == Spel.SG_MEDEL) {
                redanKorrekta.add(typ);
            }
            Spel.antalKorrekt += 1;
        } else if (!redanKorrekta.contains(typ)) {
            redanKorrekta.add(typ);
            Spel.antalKorrekt += 1;
        }
        Spel.senasteFelsvar = "";
        Main.rita.repaint();
    }

    private static void villAvsluta() {
        Spel.saAvsluta = true;
        Main.rita.repaint();
    }

    static void check(String in) {

//        if (Spel.saAvsluta) {
//            if (in.equals(" Ja") || in.equals(" ja") || in.equals(" avsluta ja")) {
//                Main.avsluta();
//            } else if (in.equals(" Nej") || in.equals(" nej") || in.equals(" avsluta nej")) {
//                Spel.saAvsluta = false;
//                Main.rita.repaint();
//            }
//        }

        if (in.contains("stäng av") || in.contains("avsluta")) {
            Main.avsluta();
            return;
        }

        if (Frame.iHuvudmeny) {


            if (in.contains("starta spelet") || in.contains("kör spelet")) {
                Main.f.startaSpelet();
                return;
            }

            if (in.contains("Nordeuropa") || in.contains("norra Europa")) {
                Frame.knappNorr.setSelected(true);
            } else if (in.contains("Sydeuropa") || in.contains("södra Europa")) {
                Frame.knappSyd.setSelected(true);
            } else if (in.contains("Östeuropa") || in.contains("östra Europa")) {
                Frame.knappOst.setSelected(true);
            } else if (in.contains("Västeuropa") || in.contains("västra Europa")) {
                Frame.knappVast.setSelected(true);
//            } else if (in.contains("hela Europa")) {
//                Frame.knappEuropa.setSelected(true);
            } else if (in.contains("blandad") || in.contains("blandat")) {
                Frame.knappBlandad.setSelected(true);
            }

            if (in.contains("lätt") || in.contains("enkel")) {
                Frame.knappEnkel.setSelected(true);

            } else if (in.contains("svår") || in.contains("svårt") || in.contains("sår") || in.contains("tvål") || in.contains("får") || in.contains("överdriv")) {
                if (in.contains("medel")) {
                    Frame.knappMedel.setSelected(true);
                } else if (in.contains("mycket") || in.contains("extra") || in.contains("väldig") || in.contains("överdriv") || in.contains("Avbryt svårt")) {
                    Frame.knappOverdrivet.setSelected(true);
                } else {
                    Frame.knappSvar.setSelected(true);
                }
            }
        } else {


            if (!Spel.namnrev) {

                if (in.contains("namn") || in.contains("heter") || in.contains("Land") || in.contains("land") || Spel.svarighetsgrad == Spel.SG_ENKEL) {

                    boolean saEttLand = false;
                    String denSomSades = "";

//                    //Kollar om spelaren sa namnet på något land.
//                    for (int i = 0; i < Konstanter.europaArray.size(); i++) {
//                        if (in.contains(Konstanter.europaArray.get(i).namn)) {
//                            saEttLand = true;
//                            denSomSades = Konstanter.europaArray.get(i).namn;
//                            break;
//                        }
//                    }

                    //Kollar om spelaren sa namnet på något land.
                    for (int i = 0; i < Konstanter.landArray.size(); i++) {
                        if (in.contains(Konstanter.landArray.get(i))) {
                            saEttLand = true;
                            denSomSades = Konstanter.landArray.get(i);
                            break;
                        }
                    }

                    if (in.contains(Spel.land.namn)) {
                        Spel.namnrev = true;
                        korrektSvar("namnrev");
                    }

                    //Om användaren sa fel land
                    else if (saEttLand) {

                        boolean sagd = false;
                        for (Land l : tidigare) {
                            if (l.namn.equals(denSomSades)) {
                                sagd = true;
                                break;
                            }
                        }
                        if (!sagd) {
                            Spel.namnrev = true;
                            felsvar("Fel namn på landet. Du sa '" + denSomSades + "' Rätt svar: " + Spel.land.namn);
                        }
                    }
                }
            }

            //Kollar om spelaren redan har gissat rätt på huvudstaden.
            //rev = revealed = avslöjad, d.v.s att spelaren har listat ut vad det är.
            if (!Spel.huvudstadrev) {

                //“in” är en String (text) som innehåller de ord som taligenkänningen
                // tror att spelaren sade.
                //"in.contains(x)" kollar om Stringen "in" innehåller texten "x".
                // "huvudstaden" innehåller t.ex. "staden".
                if (in.contains("stad")) {

                    boolean saEnHuvudstad = false;
                    String denSomSades = "";

                    //Kollar om spelaren sa namnet på någon huvudstad genom att jämföra
                    //med ALLA länders huvudstäder.
                    for (int i = 0; i < Konstanter.stadArray.size(); i++) {
                        if (in.contains(Konstanter.stadArray.get(i))) {
                            saEnHuvudstad = true;
                            denSomSades = Konstanter.stadArray.get(i);
                            break;
                        }
                    }

                    //Om användaren sa rätt huvudstad
                    if (in.contains(Spel.land.huvudstad)) {
                        Spel.huvudstadrev = true;
                        korrektSvar("huvudstadrev");
                    }

                    //Om användaren sa en huvudstad, fast inte rätt huvudstad
                    else if (saEnHuvudstad) {
                        boolean sagd = false;
                        for (Land l : tidigare) {
                            if (l.huvudstad.equals(denSomSades)) {
                                sagd = true;
                                break;
                            }
                        }
                        if (!sagd) {
                            Spel.huvudstadrev = true;
                            felsvar("Fel huvudstad. Du sa '" + denSomSades +
                                    "' Rätt svar: " + Spel.land.huvudstad);
                        }


                    }

                }
            }

            if (!Spel.flaggrev) {
                if (!Spel.flaggtyprev) {

                    if (in.contains("kors")) {
                        if (Spel.land.flaggtyp == Konstanter.FLAGGTYP_KORS) {
                            Spel.flaggtyprev = true;
                            korrektSvar("flaggtyprev");
                        } else {
                            Spel.flaggtyprev = true;
                            felsvar("Fel flaggtyp. Du sa 'kors'");
                        }
                    } else if (in.contains("trikolor") || in.contains("tricolor") || in.contains("trefärgad") || in.contains("tre färger") || in.contains("tre färjan") || in.contains("tri-color") || in.contains("tri color") || in.contains("Trikoloren")) {
                        if (Spel.land.flaggtyp == Konstanter.FLAGGTYP_TRIKOLOR || Spel.land.flaggtyp == Konstanter.FLAGGTYP_SOLIDTRILODRAT || Spel.land.flaggtyp == Konstanter.FLAGGTYP_SOLIDTRIVAGRAT) {
                            Spel.flaggtyprev = true;
                            korrektSvar("flaggtyprev");
                        } else {
                            Spel.flaggtyprev = true;
                            felsvar("Fel flaggtyp. Du sa 'trefärgad/trikolor'");
                        }
                    } else if (in.contains("bikolor") || in.contains("bicolor") || in.contains("tvåfärgad") || in.contains("bi-color") || in.contains("bipolar") || in.contains("2-färgad") || in.contains("två färger")) {
                        if (Spel.land.flaggtyp == Konstanter.FLAGGTYP_BIKOLOR) {
                            Spel.flaggtyprev = true;
                            korrektSvar("flaggtyprev");
                        } else {
                            Spel.flaggtyprev = true;
                            felsvar("Fel flaggtyp. Du sa 'tvåfärgad/bikolor''");
                        }
                    } else if (in.contains("annan") || in.contains("ospecificerad") || in.contains("unik")) {
                        if (Spel.land.flaggtyp == Konstanter.FLAGGTYP_ANNAN) {
                            Spel.flaggtyprev = true;
                            korrektSvar("flaggtyprev");
                        } else {
                            Spel.flaggtyprev = true;
                            felsvar("Fel flaggtyp. Du sa 'annan/ospecificerad'");
                        }
                    }

                } else if (!Spel.formrev) {
                    if (in.contains("nummer") || in.contains("siffra") || in.contains("först") || in.contains("andr") || in.contains("tredje") || in.contains("nr ") || in.contains("Minette")) {
                        int c;
                        if (Spel.svarighetsgrad == Spel.SG_MEDEL) {
                            c = Spel.land.flaggBilderArray.size() - 1;
                        } else {
                            c = 0;
                        }
                        int korrekt = 0;
                        if (Spel.bmpList.get(0).bild == Spel.land.flaggBilderArray.get(c)) {
                            korrekt = 1;
                        } else if (Spel.bmpList.get(1).bild == Spel.land.flaggBilderArray.get(c)) {
                            korrekt = 2;
                        } else if (Spel.bmpList.get(2).bild == Spel.land.flaggBilderArray.get(c)) {
                            korrekt = 3;
                        }

                        if (in.contains("nummer ett") || in.contains("nr 1") || in.contains("först") || in.contains("Minette") || in.contains("nummer 1")) {
                            if (korrekt == 1) {
                                Spel.formrev = true;
                                korrektSvar("formrev");
                            } else {
                                Spel.formrev = true;
                                felsvar("Fel flagga. Du sa nr 1 / den första. Rätt svar: nr " + korrekt);
                            }
                        } else if (in.contains("nummer två") || in.contains("nr 2") || in.contains("andr") || in.contains("nummer 2")) {
                            if (korrekt == 2) {
                                Spel.formrev = true;
                                korrektSvar("formrev");
                            } else {
                                Spel.formrev = true;
                                felsvar("Fel flagga. Du sa nr 2 / den andra. Rätt svar: nr " + korrekt);
                            }
                        } else if (in.contains("nummer tre") || (in.contains("nr 3") || in.contains("tredje") || in.contains("nummer 3"))) {
                            if (korrekt == 3) {
                                Spel.formrev = true;
                                korrektSvar("formrev");
                            } else {
                                Spel.formrev = true;
                                felsvar("Fel flagga. Du sa nr 3 / den tredje. Rätt svar: nr " + korrekt);
                            }
                        }
                    }

                    if (Spel.svarighetsgrad == Spel.SG_MEDEL && Spel.formrev) {
                        Spel.flaggrev = true;
                        Main.rita.repaint();
                    }

                } else if (Spel.svarighetsgrad != Spel.SG_MEDEL) {
                    //Loopen körs en gång för varje färg i arrayen fargerArray.
                    //Kör resterande kod om användaren nämnde en färg
                    for (String fargSomKollas : Konstanter.fargerArray) {
                        if (in.contains(fargSomKollas)) {

                            //Om användaren sa korrekt färg
                            if (fargSomKollas.equals(Spel.land.flaggFargerArray.get(Spel.antalFargerVisade))) {
                                Spel.antalFargerVisade += 1;
                                System.out.println("Antal färger visade = " + Spel.antalFargerVisade);
                                if (Spel.antalFargerVisade == Spel.land.flaggFargerArray.size()) {
                                    Spel.flaggrev = true;
                                }
                                korrektSvar("flaggrev");
                                break;
                            }
                            //Om användaren sa fel färg
                            else {

                                //Om användaren nämnde en tidigare färg så ges inget felsvar.
                                boolean saTidigareFarg = false;
                                for (int i = Spel.antalFargerVisade; i > 0; i--) {
                                    if (fargSomKollas.equals(Spel.land.flaggFargerArray.get(i - 1))) {
                                        saTidigareFarg = true;
                                        break;
                                    }
                                }
                                //Om nämnde en felaktig färg som inte redan har visats
                                if (!saTidigareFarg) {
                                    Spel.antalFargerVisade += 1;
                                    System.out.println("Antal färger visade = " + Spel.antalFargerVisade);
                                    if (Spel.antalFargerVisade == Spel.land.flaggFargerArray.size()) {
                                        Spel.flaggrev = true;
                                    }
                                    //Ger felsvar
                                    switch (fargSomKollas) {
                                        default:
                                            felsvar("Felaktig färg. Du sa " + fargSomKollas + ". Korrekt svar: "
                                                    + Spel.land.flaggFargerArray.get(Spel.antalFargerVisade - 1));
                                        case "grå":
                                            felsvar("Felaktig färg. Du sa grå/silver. Korrekt svar: "
                                                    + Spel.land.flaggFargerArray.get(Spel.antalFargerVisade - 1));
                                            break;
                                        case "gul":
                                            felsvar("Felaktig färg. Du sa gul/guld. Korrekt svar: "
                                                    + Spel.land.flaggFargerArray.get(Spel.antalFargerVisade - 1));
                                            break;

                                    }
                                    break;

                                }

                            }
                        }
                    }

                }

            }

        }


    }
}
