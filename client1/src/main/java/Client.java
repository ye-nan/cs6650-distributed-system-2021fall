import org.apache.commons.cli.ParseException;

public class Client {
    public static void main(String[] args) throws ParseException {
        CommandLineParser parser = new CommandLineParser();
        InputParams params = parser.parse(args);
        System.out.println(params);
    }
}
