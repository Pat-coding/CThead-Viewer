import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

public class Volume {
    short[][][] cthead; //store the 3D volume data set
    short min; //min
    short max; //max value in the 3D volume data set
    int CT_x_axis = 256;
    int CT_y_axis = 256;
    int CT_z_axis = 113;

    public enum Slice {
        TOP,
        DOWN,
        SIDE
    }

    public Volume(short[][][] cthead, short min, short max) {
        this.cthead = cthead;
        this.min = min;
        this.max = max;
    }

    public void slices(WritableImage image) {
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
                datum = cthead[CT_z_axis][j][i];
                col = (((float) datum - (float) min) / ((float) (max - min)));
                image_writer.setColor(i, j, Color.color(col, col, col, 1.0));
            }
        }

    }

    public void sliders(Slice s) {
        switch (s) {
            case TOP:
                break;
            case DOWN:
                break;
            case SIDE:
                break;
        }
    }
}


