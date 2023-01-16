package packWork;

import java.awt.image.BufferedImage;

public interface Image {

    public BufferedImage getImage(boolean isOriginalImage);

    public void saveImage(BufferedImage image, boolean isOriginalImage);
}
