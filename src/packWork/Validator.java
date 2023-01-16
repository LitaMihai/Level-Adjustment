package packWork;

public abstract class Validator<T> {

    public abstract void validate(T input) throws Exception;
}
