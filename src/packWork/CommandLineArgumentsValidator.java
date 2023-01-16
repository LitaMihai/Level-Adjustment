package packWork;

/**
 * Validatorul pentru argumentele date de utilizator
 */
public class CommandLineArgumentsValidator extends ArgumentsValidator<String[]> {

    /**
     * Functia care valideaza parametrii
     *
     * @param input - un array de string
     * @throws Exception - se arunca InvalidArgumentException daca un argument este invalid
     */
    @Override
    public void validate(String[] input) throws Exception {
        if (input.length != 3) {
            throw new InvalidArgumentsException("Nu au fost introdusi toti parametrii.");
        }
        if (Integer.valueOf(input[1]).compareTo(0) < 0 || Integer.valueOf(input[1]).compareTo(255) > 0) {
            throw new InvalidArgumentsException("Al doilea argument este invalid. Valoarea trebuie sa fie intre 0 si 255!");
        }
    }
}
