package packWork;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.DataOutputStream;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

public class ImageConsumer extends Thread{
	private ImageBuffer image;
	private BufferedImage[] imagePieces;
	private DataOutputStream out;
	private int value;
	
	private LocalDateTime start;
    private LocalDateTime end;
	
	public ImageConsumer(ImageBuffer image, DataOutputStream out){
		this.image = image;
		this.out = out;
		this.imagePieces = new BufferedImage[4];
	}
	
	public void run(){
		try{
			for(int i=0; i<4; i++){
				this.start = LocalDateTime.now();
				
				this.imagePieces[i] = this.image.getImage();//Bucatile din imagine le punem in intr-un array.

				this.end = LocalDateTime.now();
				
				sleep(1000);
				
				System.out.println("Consumatorul a primit bucata " + (i+1) +" in " + Duration.between(this.start, this.end).toMillis() + " ms");
			}
			
			LevelAdjustment.etapa1End = LocalDateTime.now();
			
			System.out.println("\n\n--------------------------------");
			
			//Procesam fiecare bucata in parte
			LevelAdjustment.etapa2Start = LocalDateTime.now();
			
			for(int i=0; i<4; i++){
				this.processImg(this.imagePieces[i], this.value);
				sleep(1000);
			}
				
			LevelAdjustment.etapa2End = LocalDateTime.now();
			
		}
		catch(NullPointerException | InterruptedException e){
			System.out.println("Producatorul nu a pus imaginea!");
		}
		
		System.out.println("\n\n--------------------------------");
		
		try {
			LevelAdjustment.etapa3Start = LocalDateTime.now();
			
			for(int i=0; i<4; i++){// Iteram prin fiecare bucata din imagine
				this.start = LocalDateTime.now();
				
				// Trimitem pixel cu pixel prin pipe.
				for(int j=0; j<this.imagePieces[i].getWidth(); j++)
					for(int k=0; k<this.imagePieces[i].getHeight(); k++)
						out.writeInt(this.imagePieces[i].getRGB(j, k));
				
				this.end = LocalDateTime.now();
				System.out.println("\nAm trimis segmentul " + i + " in " + Duration.between(this.start, this.end).toMillis() + " ms");
				
				sleep(1000);
			}
			out.close();
		}
		catch (IOException | InterruptedException e1) {
			e1.printStackTrace();
		}
	}
	
	// Metoda processImg primeste o imagine si o valoare, aplica greyscale pe ea si apoi aplica contrast la valoarea primita ca parametru
	private void processImg(BufferedImage image, Integer value){
		LocalDateTime start = LocalDateTime.now();
        for (int i = 0; i < image.getWidth(); i++) {
            for (int j = 0; j < image.getHeight(); j++) {
                //Grayscale
                image.setRGB(i, j, calculateValue(image.getRGB(i, j)));
                // Contrast
                image.setRGB(i, j, calculateContrastFactor(image.getRGB(i, j), value));
            }
        }
        LocalDateTime end = LocalDateTime.now();
        System.out.println("Procesarea imaginii a durat " + Duration.between(start, end).toMillis() + " ms");
        //return image;
	}

	// Setam variabila value cu valoarea primita de la utilizator
	public void setValue(int value){
		this.value = value;
	}
	
	// Calculeaza valoarea pentru greyscale ca medie a celor 3 componente. Fiecare valoare este inlocuita cu media
	private int calculateValue(int p) {
        int a = (p >> 24) & 0xff;
        int r = (p >> 16) & 0xff;
        int g = (p >> 8) & 0xff;
        int b = p & 0xff;

        //calculate average
        int avg = (r + g + b) / 3;

        //replace RGB value with avg
        return (a << 24) | (avg << 16) | (avg << 8) | avg;
    }
	
	// Valorile unei componente trebuie sa fie intre 0 si 255
	private int truncate(int i) {
		if (i < 0) {
            return 0;
        }
        if (i > 255) {
            return 255;
        }
        return i;
    }
	
	// Se calculeaza pe baza valorii un factor care se adauga apoi la valoarea fiecarui pixel
	private int calculateContrastFactor(int rgb, Integer value) {
		Color color = new Color(rgb);
		
		int factor = (259 * (value + 255)) / (255 * (259 - value));
	    
	    int red = truncate(factor * (color.getRed() - 128) + 128);
	    int green = truncate(factor * (color.getGreen() - 128) + 128);
	    int blue = truncate(factor * (color.getBlue() - 128) + 128);
	    
	    return new Color(red, green, blue).getRGB();
    }
}
