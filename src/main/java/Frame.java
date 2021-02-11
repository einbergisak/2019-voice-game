import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.io.File;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

class Frame extends JFrame {
    static private JPanel p;
    static JLabel startaLabel;
    static JRadioButton
            knappBlandad,
            knappNorr,
            knappSyd,
            knappVast,
            knappOst,
            knappEnkel,
            knappMedel,
            knappSvar,
            knappOverdrivet;
    static boolean iHuvudmeny;

    private static JScrollPane sp;

    private static ArrayList<JRadioButton> b, s;

    //    static JLabel output;
    Frame() {
        setSize(1920, 1080);
        setUndecorated(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);
        getContentPane().add(Main.rita);
        setTitle("Isaks Geografispel");
        Main.rita.setSize(1920, 1080);
        Main.rita.setLayout(null);
        p = new JPanel();
        JLabel region = new JLabel("Region:"),
                sg = new JLabel("Svårighetsgrad:");
        region.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 30));
        region.setBorder(BorderFactory.createEmptyBorder(20, 0, 15, 0));
        sg.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 30));
        sg.setBorder(BorderFactory.createEmptyBorder(20, 0, 15, 0));


        knappBlandad = new JRadioButton("Blandad");
        knappNorr = new JRadioButton(("Nordeuropa"));
        knappSyd = new JRadioButton(("Sydeuropa"));
        knappVast = new JRadioButton(("Västeuropa"));
        knappOst = new JRadioButton(("Östeuropa"));
//        knappEuropa = new JRadioButton("Hela Europa");

        b = new ArrayList<JRadioButton>() {{
            add(knappBlandad);
            add(knappNorr);
            add(knappSyd);
            add(knappOst);
            add(knappVast);
//            add(knappEuropa);
        }};

        knappEnkel = new JRadioButton("Enkelt");
        knappMedel = new JRadioButton("Medelsvårt");
        knappSvar = new JRadioButton("Svårt");
        knappOverdrivet = new JRadioButton(("Överdrivet svårt"));


        s = new ArrayList<JRadioButton>() {{
            add(knappEnkel);
            add(knappMedel);
            add(knappSvar);
            add(knappOverdrivet);
        }};


        ButtonGroup bg1 = new ButtonGroup(), bg2 = new ButtonGroup();

        p.setBounds(150, 304, 400, 800);
        p.setLayout(new BoxLayout(p, BoxLayout.PAGE_AXIS));

        startaLabel = new JLabel("Startar taligenkänning...");
        startaLabel.setBounds(150, 200, 500, 100);
        startaLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 42));


        Font font = new Font(Font.SANS_SERIF, Font.PLAIN, 24);
        p.add(region);
        for (JRadioButton knapp : b) {
            knapp.setFont(font);
            knapp.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));
            bg1.add(knapp);
            p.add(knapp);
        }

        p.add(sg);
        for (JRadioButton knapp : s) {
            knapp.setFont(font);
            bg2.add(knapp);
            knapp.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));
            p.add(knapp);
        }

        knappBlandad.setSelected(true);
        knappEnkel.setSelected(true);


        add(startaLabel);
        add(p);


        //Världsdel, svårighetsgrad, resultat, tid
        Object[][] rekordArray = new Object[20][5];
        Object[] content;
        String vd, sgrad;
        File rekordFil;
        NumberFormat nf = NumberFormat.getNumberInstance(Locale.GERMAN);
        DecimalFormat nF = (DecimalFormat) nf;
        nF.setMaximumFractionDigits(2);

        int varv = 0;
        for (int i = 0; i < 5; i++) {
            switch (i) {
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
            for (int j = 0; j < 4; j++) {
                content = new Object[5];
                switch (j) {
                    case 0:
                        sgrad = "Enkelt";
                        break;
                    case 1:
                        sgrad = "Medelsvårt";
                        break;
                    case 2:
                        sgrad = "Svårt";
                        break;
                    case 3:
                        sgrad = "Överdrivet";
                        break;
                    default:
                        sgrad = "[error]";
                        break;
                }
                content[0] = (vd);
                content[1] = (sgrad);
                rekordFil = (new File(Konstanter.path + "highscores\\" + vd + j + ".txt"));
                ArrayList<String> d = Spel.hittaHighscore(rekordFil);
                if (d.size() == 0) {
                    d.add("-");
                    d.add("-");
                    d.add("-");
                }
                content[2] = (d.get(1));
                if (!d.get(0).equals("-")) {
                    content[3] = (d.get(0) + "%");
                } else {
                    content[3] = (d.get(0));
                }


                if (!d.get(2).equals("-")) {
                    int min;
                    double s;

                    min = (int) Math.floor(Double.parseDouble(d.get(2)) / 60);
                    s = Double.parseDouble(d.get(2)) % 60;
                    String tiden = "";
                    if (min == 0) {
                        tiden += nF.format(s) + " s";
                    } else {
                        tiden += min + " min " + nF.format(s) + " s";
                    }

                    content[4] = (tiden);
                } else {
                    content[4] = d.get(2);
                }

                rekordArray[varv] = content;
                varv++;
            }


        }

        String[] kolumner = {"Region", "Svårighetsgrad", "Namn", "Resultat", "Tid"};
        final JTable tabell = new JTable(rekordArray, kolumner) {
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return false;
            }
        };

        tabell.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 20));
        tabell.setBorder(BorderFactory.createLineBorder(new Color(122, 138, 153), 1, false));
        DefaultTableCellRenderer r = new DefaultTableCellRenderer() {
            Font font = new Font(Font.SANS_SERIF, Font.PLAIN, 22);

            @Override
            public Component getTableCellRendererComponent(JTable table,
                                                           Object value, boolean isSelected, boolean hasFocus,
                                                           int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus,
                        row, column);
                setFont(font);
                return this;
            }

        };


        class asd extends JLabel implements TableCellRenderer {
            asd() {
                JTableHeader header = tabell.getTableHeader();
                setHorizontalAlignment(CENTER);
                setBorder(tabell.getBorder());
                setForeground(header.getForeground());
                setBackground(header.getBackground());
                setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 20));
                setPreferredSize(new Dimension(0, 50));
            }

            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {


                setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 20));

                setText(value.toString());
                return this;
            }
        }

        for (int i = 0; i < 5; i++) {
            tabell.getColumn(tabell.getColumnName(i)).setHeaderRenderer(new asd());
        }

        r.setHorizontalAlignment(JLabel.CENTER);
        tabell.setDefaultRenderer(Object.class, r);
        tabell.setRowHeight(39);
        tabell.setFillsViewportHeight(true);


        sp = new JScrollPane(tabell);
        sp.setBounds(850, 85, 1000, 833);


        tabell.getColumn(tabell.getColumnName(0)).setMaxWidth(150);
        tabell.getColumn(tabell.getColumnName(1)).setMaxWidth(140);
        tabell.getColumn(tabell.getColumnName(2)).setMaxWidth(345);
        tabell.getColumn(tabell.getColumnName(3)).setMaxWidth(165);
        tabell.getColumn(tabell.getColumnName(4)).setMaxWidth(200);

        add(sp);
        sp.setVisible(true);

        iHuvudmeny = true;
        setVisible(true);
        Main.rita.repaint();
        System.out.println("konst");
        new Konstanter();
    }


    void startaSpelet() {
        startaLabel.setText("Startar...");
        Main.rita.repaint();

        System.out.println("das");

        if (knappBlandad.isSelected()) {
            Spel.landArray = Konstanter.blandadArray;
            Spel.varldsdel = "Blandad";
        } else if (knappNorr.isSelected()) {
            Spel.landArray = Konstanter.nordArray;
            Spel.varldsdel = "Nordeuropa";
        } else if (knappSyd.isSelected()) {
            Spel.landArray = Konstanter.sydArray;
            Spel.varldsdel = "Sydeuropa";
        } else if (knappVast.isSelected()) {
            Spel.landArray = Konstanter.vastArray;
            Spel.varldsdel = "Västeuropa";
        } else if (knappOst.isSelected()) {
            Spel.landArray = Konstanter.ostArray;
            Spel.varldsdel = "Östeuropa";
//        }else if (knappEuropa.isSelected()){
//            Spel.landArray = Konstanter.europaArray;
//            Spel.varldsdel = "Hela Europa";
        }

        for (int i = 0; i < s.size(); i++) {
            if (s.get(i).isSelected()) {
                Spel.svarighetsgrad = i;
            }
        }

        remove(sp);
        remove(p);
        remove(startaLabel);
        System.out.println("visar nytt land");
        iHuvudmeny = false;
        Spel.visaNyttLand();
        Tid.startaTiden();
    }


}