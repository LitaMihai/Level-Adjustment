package packWork;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Scanner;

public class LevelAdjustment {
	
	// Obiectele folosite in aplicatie
    private ImageBuffer imageBuffer;
    private ImageProducer imageProducer;
    private ImageConsumer imageConsumer;
   
    private WriterResult imageWriterResult;
    
    private PipedOutputStream pipeOut;
    private PipedInputStream pipeIn;
    private DataOutputStream out;
    private DataInputStream in;
    
    private final CommandLineArgumentsValidator commandLineArgumentsValidator;
    
    public static LocalDateTime etapa1Start;
    public static LocalDateTime etapa1End;
    
    public static LocalDateTime etapa2Start;
    public static LocalDateTime etapa2End;
    
    public static LocalDateTime etapa3Start;
    public static LocalDateTime etapa3End;
	
	public LevelAdjustment(String[] args) {
		this.imageBuffer = new ImageBuffer();
		
		try {

			this.pipeOut = new PipedOutputStream();
			this.pipeIn = new PipedInputStream();
			this.pipeIn.connect(this.pipeOut);
			
			this.out = new DataOutputStream(pipeOut);
			this.in = new DataInputStream(pipeIn);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		this.imageProducer = new ImageProducer(this.imageBuffer);
		this.imageConsumer = new ImageConsumer(this.imageBuffer, this.out);
		this.imageWriterResult = new WriterResult(this.in);
		
		this.commandLineArgumentsValidator = new CommandLineArgumentsValidator();
		
		this.run(args);
	}

	private void run(String[] args){
		System.out.println("Level Adjustment - Welcome");
		
		// In cazul in care au fost trimise argumente din linia de comanda, preluam parametrii de acolo, altfel ii cerem utilizatorului date
        if (args.length != 0) {
            commandLineFlow(args);
        } else {
            interactiveFlow();
        }
        
        // Punem acest while ca programul sa nu afiseze rezultatele inainte ca ultimul thread sa se inchida
        while(this.imageWriterResult.isAlive() == true);
        
        // Afisam timpul din fiecare etapa
    	System.out.println("\n\n--------------------------------");
        System.out.println("\nEtapa 1 --> Cititre si primire intre producator si consumator");
        System.out.println("\nAceasta etapa a durat " + Duration.between(LevelAdjustment.etapa1Start, LevelAdjustment.etapa1End).toMillis() + " ms");
        System.out.println("\n\nEtapa 2 --> Procesarea bucatilor din imagine");
        System.out.println("\nAceasta etapa a durat " + Duration.between(LevelAdjustment.etapa2Start, LevelAdjustment.etapa2End).toMillis() + " ms");
        System.out.println("\n\nEtapa 3 --> Trimiterea bucatilor catre WriterResult si creearea imaginei finale");
        System.out.println("\nAceasta etapa a durat " + Duration.between(LevelAdjustment.etapa3Start, LevelAdjustment.etapa3End).toMillis() + " ms"); 
	}
	
	private void interactiveFlow() {
        // Se cer cele 3 input-uri de la utilizator
        System.out.println("Introduceti locatia imaginii.... ");
        Scanner scanner = new Scanner(System.in);
        String path = scanner.nextLine();
        
        System.out.println("Introduceti valoarea level adjustment, intre 0 si 255....");
        String value = scanner.nextLine();
        
        // Validarea valorii de level adjustment
        ValueValidator validator = new ValueValidator();
        while (!validator.isValid(value)) {
            System.out.println("Introduceti valoarea level adjustment, intre 0 si 255....");
            value = scanner.nextLine();
        }
        
        System.out.println("Introduceti locatia unde sa fie salvata imaginea modificata");
        String outPath = scanner.nextLine();
        
        scanner.close();
        
        // Se valideaza toti 3 parametrii trimiti din linia de comanda
        String[] args = new String[3];
        args[0] = path;
        args[1] = value;
        args[2] = outPath;
        try {
			this.commandLineArgumentsValidator.validate(args);
		} catch (Exception e) {
			e.printStackTrace();
		}
        
        this.setVariablesAndStartThreads(args);
    }
	
    private void commandLineFlow(String[] args) {
        System.out.println("Software-ul a fost apelat cu argumentele in linia de comanda, se verifica argumentele");
        
        try {
            // Se valideaza toti 3 parametrii trimiti din linia de comanda
            this.commandLineArgumentsValidator.validate(args);
            
        } catch (Exception e) {
            if (e instanceof InvalidArgumentsException) {
                System.out.println(e.getMessage());
                System.out.println("==========================");
                System.out.println("Primul parametru trebuie sa fie calea catre fisier + extensia fisierului");
                System.out.println("Cel de-al doilea parametru un numar intre 0 la 255 ce reprezinta nivelul de level adjustment");
                System.out.println("Al treilea parametru trebuie sa fie locatia unde sa fie salvata imaginea modificata");
            }
        }
        
        this.setVariablesAndStartThreads(args);
    }
    private void setVariablesAndStartThreads(String[] args){
		// Setam path-ul catre imaginea pe care vrem sa o modificam
        this.imageProducer.setPathToImage(args[0]);
        
        // Setam path-ul catre locatia unde vrem sa fie imaginea modificata
        this.imageWriterResult.setOutPath(args[2]);
        
        // Initializam variabilele cu dimensiunea necesara
        int halfWidth = this.imageProducer.getHalfWidth();
		int halfHeight = this.imageProducer.getHalfHeight();
		
		// Setam valoarea introdusa de utilizator
		this.imageConsumer.setValue(Integer.parseInt(args[1]));
		
		// Setam dimensiunile necesare procesarii si transmiterii de imagini
        this.imageWriterResult.setHalfWidth(halfWidth);
        this.imageWriterResult.setHalfHeight(halfHeight);
        
        // Pornim thread-urile
        this.imageProducer.start();
        this.imageConsumer.start();
        this.imageWriterResult.start();
	}
}
