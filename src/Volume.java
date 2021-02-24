import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

import java.io.*;

public class Volume {
    private short[][][] ctHead;
    private short min;
    private short max;
    int b1;
    int b2;
    public static final int CT_x_axis = 256;
    public static final int CT_y_axis = 256;
    public static final int CT_z_axis = 113;

    public Volume(String filename) throws IOException {
        File file = new File(".\\src\\CThead");
        //Read the data quickly via a buffer (in C++ you can just do a single fread - I couldn't find if there is an equivalent in Java)
        DataInputStream in = new DataInputStream(new BufferedInputStream(new FileInputStream(file)));
        int i;
        int j;
        int k;
        min = Short.MAX_VALUE;
        max = Short.MIN_VALUE; //set to extreme values
        short read; //value read in

        ctHead = new short[CT_z_axis][CT_y_axis][CT_x_axis]; //allocate the memory - note this is fixed for this data set
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
                    ctHead[k][j][i] = read; //put the short into memory (in C++ you can replace all this code with one fread)

                }
            }
        }
    }

    public enum Axis {
        Z,
        Y,
        X
    }

    public void slices(WritableImage image, Axis a, int newVal) {
        int w = (int) image.getWidth();
        int h = (int) image.getHeight();
        PixelWriter image_writer = image.getPixelWriter();
        double col;
        short datum;
        for (int j = 0; j < h; j++) {
            for (int i = 0; i < w; i++) {
                if (i > 256) {
                    i = 256;
                } else if (j > 256) {
                    j = 256;
                }
                switch (a) {
                    case Z:
                        datum = ctHead[newVal][j][i];
                        col = (((float) datum - (float) min) / ((float) (max - min)));
                        image_writer.setColor(i, j, Color.color(col, col, col, 1.0));
                        break;
                    case Y:
                        datum = ctHead[j][i][newVal];
                        col = (((float) datum - (float) min) / ((float) (max - min)));
                        image_writer.setColor(i, j, Color.color(col, col, col, 1.0));
                        break;
                    case X:
                        datum = ctHead[j][newVal][i];
                        col = (((float) datum - (float) min) / ((float) (max - min)));
                        image_writer.setColor(i, j, Color.color(col, col, col, 1.0));
                        break;
                }
            }
        }
    }
}


