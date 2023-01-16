package packWork;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

import javax.imageio.ImageIO;

public class WriterResult extends Thread{

	private DataInputStream in;
	private String outPath;
	private BufferedImage newSegment;
	private int halfWidth;
	private int halfHeight;
	private BufferedImage originalImage;
	private Graphics2D g2d;
	
	private LocalDateTime start;
	private LocalDateTime end;
	
	public WriterResult(DataInputStream in){
		this.in = in;
	}
	
	public void run(){
		try {
			// Variabila in care se va stoca fiecare bucata din imagine primita
			this.newSegment = new BufferedImage(this.halfWidth, this.halfHeight, BufferedImage.TYPE_INT_RGB);
			
			// Variabila in care se va stoca imaginea finala
			this.originalImage = new BufferedImage(2 * this.halfWidth, 2 * this.halfHeight, BufferedImage.TYPE_INT_RGB);
			
			// Variabila ce ne ajuta sa combinam bucatile din imagine
			this.g2d = this.originalImage.createGraphics();
			
			int pixel;
			
			for(int i=0; i<4; i++){
				this.start = LocalDateTime.now();
				
				//Citim fiecare pixel din pipe si cream bucata din imagine
				for(int j=0; j<this.halfWidth; j++)
					for(int k=0; k<this.halfHeight; k++){
						pixel = in.readInt(); // Citesc pixel-ul in variabila pixel
						this.newSegment.setRGB(j, k, pixel); // setez pixelul in variabila auxiliara newSegment
					}
				
				this.end = LocalDateTime.now();
				
				System.out.println("Am primit segmentul " + i + " in " + Duration.between(this.start, this.end).toMillis() + " ms");
				
				// Unesc bucata primita din imagine la restul pentru a o reconstrui
				this.joinImage(this.newSegment, i, this.g2d);
				
				sleep(1000);
			}
			
			LevelAdjustment.etapa3End = LocalDateTime.now();
			
			//ImageIO.write(this.imagePieces[i], "bmp", new File("D:\\Facultate\\AWJ\\Workspace\\Level Adjustment\\test" + i + ".bmp"));
			this.g2d.dispose();
			in.close();
			
			// Scriem imaginea editata in folder.
			ImageIO.write(this.originalImage, "bmp", new File(outPath));
			System.out.println("\nImaginea editata a fost creata!");
			
		}catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	// Metoda cu care setam path-ul unde sa punem imaginea editata
	public void setOutPath(String outPath){
		this.outPath = outPath;
	}
	
	// Setter pentru halfWidth
	public void setHalfWidth(int halfWidth){
		this.halfWidth = halfWidth;
	}
	
	// Setter pentru halfHeight
	public void setHalfHeight(int halfHeight){
		this.halfHeight = halfHeight;
	}

	// Metoda ce deseneaza in imaginea finala bucata de imagine primita in variabila img
	private void joinImage(BufferedImage img, int piece, Graphics2D g2d){
		// Array-uri de pozitii
		// (0, 0) -> stanga sus
		// (halfWidth, 0) -> dreapta sus
		// (0, halfHeight) -> stanga jos
		// (halfWidth, halfHeight) -> dreapta jos
		int x[] = {0 , halfWidth, 0, halfWidth};
	    int y[] = {0 , 0, halfHeight, halfHeight};

	    // Desenam bucata de imagine in imaginea finala
	    g2d.drawImage(img, x[piece], y[piece], null);
	}
}
