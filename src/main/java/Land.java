import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

class Land {
    String namn, huvudstad, flaggProp;
    int flaggtyp;
    BufferedImage silhuett;
    ArrayList<BufferedImage> flaggBilderArray = new ArrayList<>();
    ArrayList<String> flaggFargerArray = new ArrayList<>();


    Land(String namn, String huvudstad, int flaggtyp, String farger, String flaggProp) throws IOException {
        //Klassens fält
        this.namn = namn;
        this.huvudstad = huvudstad;
        this.flaggtyp = flaggtyp;
        this.flaggProp = flaggProp;

        this.flaggFargerArray.addAll(Arrays.asList(farger.split(" ")));


        String p = Konstanter.path + "bilder\\" + namn + "\\";
        this.silhuett = ImageIO.read(new File(p + "silhuett.png"));
        for (int i = 0; i <= this.flaggFargerArray.size(); i++) {

            this.flaggBilderArray.add(ImageIO.read(new File(p + "flagga" + i + ".png")));
        }
    }


}
