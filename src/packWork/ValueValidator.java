package packWork;

/**
 * Clasa care valideaza valoarea introdusa de utilizator
 */
public class ValueValidator extends ArgumentsValidator<Integer> {

	// Metoda ce valideaza numarul introdus de utilizator
	// Numarul trebuie sa fie intre 0 si 255
    @Override
    public void validate(Integer input) throws Exception {
        if (input.compareTo(0) < 0 || input.compareTo(255) > 0) {
            throw new InvalidArgumentsException("Valoarea trebuie sa fie intre 0 si 255");
        }
    }

    // Aceasta functie ne permita sa ii cerem utilizatorului sa introduca o valoarea pana cand aceasta este valida
    public boolean isValid(String input) {
        try {
            validate(Integer.valueOf(input));
            return true;
        } catch (InvalidArgumentsException e) {
            System.out.println(e.getMessage());
            return false;
        } catch (NumberFormatException e) {
            System.out.println("Valoarea introdusa nu este numar !");
            return false;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }
}
