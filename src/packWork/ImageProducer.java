package packWork;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

public class ImageProducer extends Thread{
	private File file; // imaginea noastra
    private ImageBuffer image;
    
    private LocalDateTime start;
    private LocalDateTime end;
    
    private int halfWidth;
    private int halfHeight;
    
	public ImageProducer(ImageBuffer image){
		this.image = image;
	}
	
	// Setter pentru path-ul unde se afla imagine pe care vrem sa o modificam
	// si aflarea halfWidth si halfHeight
	public void setPathToImage(String path){
		this.file = new File(path);
		
		try {// Salvam in halfWidth si halfHeight jumatate din lungimea imaginii si jumatate din inaltimea ei.
    		Iterator<?> readers = ImageIO.getImageReadersByFormatName("bmp");
        	ImageReader reader = (ImageReader)readers.next();
        	ImageInputStream iis = ImageIO.createImageInputStream(file);
            reader.setInput(iis, true);
    		
			this.halfWidth = reader.getWidth(0)/2;
			this.halfHeight = reader.getHeight(0)/2;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void run() {
		try{
			LevelAdjustment.etapa1Start = LocalDateTime.now();
			
            for(int i = 0; i < 4; i++){
            	this.start = LocalDateTime.now();
            	
            	// Citim bucata din imagine
            	BufferedImage bufferedImage = readFragment(this.file, i, halfWidth, halfHeight);
            	
            	// Salvam bucata din imagine
	            this.image.saveImage(bufferedImage);
	            
	            this.end = LocalDateTime.now();
	            System.out.println("\nProducatorul a trimis bucata " + (i+1) +" in " + Duration.between(this.start, this.end).toMillis() + " ms");
	            
	            sleep(1000);
			}
		}
		catch (IOException | InterruptedException e) {
			e.printStackTrace();
            System.exit(1);
		}
	}

	// Metoda cu care citim 1/4 din imagine
	private BufferedImage readFragment(File file, int piece, int halfWidth, int halfHeight) throws IOException {
		ImageInputStream imageStream = ImageIO.createImageInputStream(file);
	    ImageReader reader = ImageIO.getImageReaders(imageStream).next();
	    ImageReadParam param = reader.getDefaultReadParam();

    	/* Array-uri de pozitii
    	 * 
 		 * (0, 0) -> stanga sus
 		 * (halfWidth, 0) -> dreapta sus
 		 * (0, halfHeight) -> stanga jos
 		 * (halfWidth, halfHeight) -> dreapta jos
 		*/ 
	    int x[] = {0 , halfWidth, 0, halfWidth};
        int y[] = {0 , 0, halfHeight, halfHeight};
        
        // Declaram un dreptunghi cu pozitia x,y si dimensiunea halfWidth si halfHeight
	    Rectangle rect = new Rectangle(x[piece], y[piece], halfWidth, halfHeight);
	    
	    // Citim bucata din imagine
	    param.setSourceRegion(rect);
	    reader.setInput(imageStream, true, true);
	    BufferedImage image = reader.read(0, param);

	    reader.dispose();
	    imageStream.close();

	    // Returnam bucata din imagine
	    return image;
	}
	
	// Getter pentru HalfWidth
	public int getHalfWidth(){
		return this.halfWidth;
	}
	
	// Getter pentru HalfHeight
	public int getHalfHeight(){
		return this.halfHeight;
	}
}
