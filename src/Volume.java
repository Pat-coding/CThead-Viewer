import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import java.io.*;
import java.util.Arrays;

public class Volume {
    public static final int CT_x_axis = 256;
    public static final int CT_y_axis = 256;
    public static final int CT_z_axis = 113;
    int b1;
    int b2;
    private short[][][] ctHead;
    private short min;
    private short max;

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

    /**
     * @param image
     * @param a
     * @param newVal
     */
    public void slices(WritableImage image, Axis a, int newVal) {
        int w = (int) image.getWidth();
        int h = (int) image.getHeight();
        PixelWriter image_writer = image.getPixelWriter();
        double col;
        short hu; //hounsfield unit
        for (int j = 0; j < h; j++) {
            for (int i = 0; i < w; i++) {
                if (i > 256) {
                    i = 256;
                } else if (j > 256) {
                    j = 256;
                }
                switch (a) {
                    case Z:
                        hu = ctHead[newVal][j][i];
                        col = (((float) hu - (float) min) / ((float) (max - min)));
                        image_writer.setColor(i, j, Color.color(col, col, col, 1.0));
                        break;
                    case Y:
                        hu = ctHead[j][i][newVal];
                        col = (((float) hu - (float) min) / ((float) (max - min)));
                        image_writer.setColor(i, j, Color.color(col, col, col, 1.0));
                        break;
                    case X:
                        hu = ctHead[j][newVal][i];
                        col = (((float) hu - (float) min) / ((float) (max - min)));
                        image_writer.setColor(i, j, Color.color(col, col, col, 1.0));
                        break;
                }
            }
        }
    }

    public void volRend(WritableImage image, Axis a, double newVal) {
        int w = (int) image.getWidth();
        int h = (int) image.getHeight();
        PixelWriter image_writer = image.getPixelWriter();
        short hu;

        //outer loop
        for (int j = 0; j < h; j++) {
            for (int i = 0; i < w; i++) {
                double[] colAccum = {0,0,0};
                double transAccum = 1;
                //ray cast loop
                for (int k = 0; k < a.value; k++) {
                    switch (a) {
                        case Z:
                            hu = ctHead[k][j][i];
                            TF z_val = TF.lookup(hu);
                            colAccum = volRendCalc(z_val, colAccum, transAccum, newVal);
                            transAccum = transAccum * (1 - z_val.opVal);
                            break;
                        case Y:
                            hu = ctHead[j][i][k];
                            TF y_val = TF.lookup(hu);
                            colAccum = volRendCalc(y_val, colAccum, transAccum, newVal);
                            transAccum = transAccum * (1 - y_val.opVal);
                            break;
                        case X:
                            hu = ctHead[j][k][i];
                            TF x_val = TF.lookup(hu);
                            colAccum = volRendCalc(x_val, colAccum, transAccum, newVal);
                            transAccum = transAccum * (1 - x_val.opVal);
                            break;
                    }

                }
                image_writer.setColor(i, j, Color.color(colAccum[0], colAccum[1], colAccum[2], 1.0));
            }
        }


    }

    public double[] volRendCalc(TF tf, double[] colAccum, double transAccum, double newVal) {
        if (newVal != 0.12 && tf.equals(TF.R2)) {
            colAccum[0] += transAccum * newVal * tf.rVal;
            if (colAccum[0] > 1) {
                colAccum[0] = 1;
            }
            colAccum[1] += transAccum * newVal * tf.gVal;
            if (colAccum[1] > 1) {
                colAccum[1] = 1;
            }
            colAccum[2] += transAccum * newVal * tf.bVal;
            if (colAccum[2] > 1) {
                colAccum[2] = 1;
            }

        } else {
            colAccum[0] += transAccum * tf.opVal * tf.rVal;
            if (colAccum[0] > 1) {
                colAccum[0] = 1;
            }
            colAccum[1] += transAccum * tf.opVal * tf.gVal;
            if (colAccum[1] > 1) {
                colAccum[1] = 1;
            }
            colAccum[2] += transAccum * tf.opVal * tf.bVal;
            if (colAccum[2] > 1) {
                colAccum[2] = 1;
            }
        }

        return new double[]{colAccum[0], colAccum[1], colAccum[2]};
    }

    public enum Axis {
        Z(CT_z_axis),
        Y(CT_y_axis),
        X(CT_x_axis);

        public final int value;

        Axis(int value) {
            this.value = value;
        }
    }

    public enum TF {
        R1((short) - 1117, (short) -299, 0.0, 0.0, 0.0, 0.0),
        R2((short) - 300, (short) 49, 1.0, 0.79, 0.6, 0.12),//skin hu
        R3((short) 50, (short) 299, 0.0, 0.0, 0.0, 0.0),
        R4((short) 300, (short) 4096, 1.0, 1.0, 1.0, 0.8);//bone hu

        public final short min;
        public final short max;
        public final double rVal;
        public final double gVal;
        public final double bVal;
        public final double opVal;

        TF(short min, short max, double rVal, double gVal, double bVal, double opVal){
            this.min = min;
            this.max = max;
            this.rVal = rVal;
            this.gVal = gVal;
            this.bVal = bVal;
            this.opVal = opVal;
        }

        public static TF lookup(final short v) {
            return Arrays.stream(values())
                    .filter(r -> v >= r.min && v <= r.max)
                    .findFirst()
                    .orElse(null);
        }
    }
}
