import javafx.fxml.Initializable;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller class containing the UI
 */

public class Controller implements Initializable {

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void setWindow(Stage stage) throws IOException {
        readData();
    }

    public void readData() throws IOException {
        //File name is hardcoded here - much nicer to have a dialog to select it and capture the size from the user
        File file = new File(".\\src\\CThead");
        //Read the data quickly via a buffer (in C++ you can just do a single fread - I couldn't find if there is an equivalent in Java)
        DataInputStream in = new DataInputStream(new BufferedInputStream(new FileInputStream(file)));

        int i, j, k; //loop through the 3D data set

        short min = Short.MAX_VALUE;
        short max = Short.MIN_VALUE; //set to extreme values
        short read; //value read in
        int b1;
        int b2;
        int CT_z_axis = 256;
        int CT_y_axis = 256;
        int CT_x_axis = 113;
        short[][][] cthead;

        cthead = new short[CT_z_axis][CT_y_axis][CT_x_axis]; //allocate the memory - note this is fixed for this data set
        //loop through the data reading it in
        for (k = 0; k < CT_z_axis; k++) {
            for (j = 0; j < CT_y_axis; j++) {
                for (i = 0; i < CT_x_axis; i++) {
                    //because the Endianess is wrong, it needs to be read byte at a time and swapped
                    b1 = ((int) in.readByte()) & 0xff; //the 0xff is because Java does not have unsigned types
                    b2 = ((int) in.readByte()) & 0xff; //the 0xff is because Java does not have unsigned types
                    read = (short) ((b2 << 8) | b1); //and swizzle the bytes around
                    if (read < min) min = read; //update the minimum
                    if (read > max) max = read; //update the maximum
                    cthead[k][j][i] = read; //put the short into memory (in C++ you can replace all this code with one fread)
                }
            }
        }
        //pass on data to volume class
    }

    public void displaySlices() {

    }

    public void displaySliders() {

    }

}
