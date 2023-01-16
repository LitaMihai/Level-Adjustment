package packWork;

import java.awt.image.BufferedImage;

public class ImageBuffer{
	private BufferedImage imageBuffered; 
	private boolean available = false; 
	
	public synchronized BufferedImage getImage(){
		while(!this.available){
			try{
				wait(); //Asteapta producatorul sa puna o bucata din imagine
			}
			catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		this.available = false;
		notifyAll();
		return this.imageBuffered;
	}
	
	public synchronized void saveImage(BufferedImage img){
		while(this.available){
			try{
				wait(); //Asteapta consumatorul sa preia bucata de imagine
			}
			catch(InterruptedException e){
				e.printStackTrace();
			}
		}
		this.imageBuffered = img;
		available = true;
		notifyAll();
	}
}
