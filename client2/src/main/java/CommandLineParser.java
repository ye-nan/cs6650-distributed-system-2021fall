import org.apache.commons.cli.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandLineParser {
    private static final int MIN_NUMOPTIONS = 3;
    private static final int MAX_NUMTHREADS = 512;
    private static final int MAX_NUMSKIERS = 100000;
    private static final int DEFAULT_NUMLIFTS = 40;
    private static final int MIN_NUMLIFTS = 5;
    private static final int MAX_NUMLIFTS = 60;
    private static final int DEFAULT_NUMRUNS = 10;
    private static final int MAX_NUMRUNS = 20;

    private Options options;
    private DefaultParser parser;
    public CommandLineParser() {
        this.options = new Options();
        this.parser = new DefaultParser();

        // arg: number of threads
        Option threads = Option.builder()
                .longOpt("threads")
                .argName("numThreads")
                .hasArg()
                .required()
                .desc("number of threads to run (max 256)" )
                .build();
        options.addOption(threads);

        // arg: number of skiers
        Option skiers = Option.builder()
                .longOpt("skiers")
                .argName("numSkiers")
                .hasArg()
                .required()
                .desc("number of skier to generate lift rides for (max 100000)" )
                .build();
        options.addOption(skiers);

        // arg: number of lifts
        Option lifts = Option.builder()
                .longOpt("lifts")
                .argName("numLifts")
                .hasArg()
                .desc("number of ski lifts (range 5-60, default 40)" )
                .build();
        options.addOption(lifts);

        // arg: number of runs
        Option runs = Option.builder()
                .longOpt("runs")
                .argName("numRuns")
                .hasArg()
                .desc("mean numbers of ski lifts each skier rides each day (default 10, max 20)" )
                .build();
        options.addOption(runs);

        // arg: server ip/port
        Option server = Option.builder()
                .longOpt("server")
                .argName("server")
                .hasArg()
                .required()
                .desc("IP/port address of the serve" )
                .build();
        options.addOption(server);

        HelpFormatter formatter = new HelpFormatter();
        String header = "Multi-thread client to interact with a remote Tomcat server for load testing\n\n";
        String footer = "\nNan Ye, NEU Seattle, CS6650 2021Fall";
        formatter.printHelp("Client", header, options, footer, true);
    }

    public InputParams parse(String[] args) throws ParseException{
        if (this.options.getOptions().size() < MIN_NUMOPTIONS)
            throw new ParseException("Missing one or more input args.");

        // add default value to args
        List<String> argList = new ArrayList<>(Arrays.asList(args));

        if (this.options.getOption("lifts").getValue() == null) {
            argList.add("--lifts");
            argList.add(String.valueOf(DEFAULT_NUMLIFTS));
        }
        if (this.options.getOption("runs").getValue() == null) {
            argList.add("--runs");
            argList.add(String.valueOf(DEFAULT_NUMRUNS));
        }

        args = argList.toArray(new String[0]);
        CommandLine cmd = this.parser.parse(this.options, args);

        InputParams params;
        try {
            params = new InputParams(
                    Integer.parseInt(cmd.getOptionValue("threads")),
                    Integer.parseInt(cmd.getOptionValue("skiers")),
                    Integer.parseInt(cmd.getOptionValue("lifts")),
                    Integer.parseInt(cmd.getOptionValue("runs")),
                    cmd.getOptionValue("server")
            );

            if (params.getNumThreads() < 0 || params.getNumThreads() > MAX_NUMTHREADS)
                throw new ParseException("Number of threads out of bound.");
            if (params.getNumSkiers() < 0 || params.getNumSkiers() > MAX_NUMSKIERS)
                throw new ParseException("Number of skiers out of bound.");
            if (params.getNumLifts() < MIN_NUMLIFTS || params.getNumLifts() > MAX_NUMLIFTS)
                throw new ParseException("Number of lifts out of bound.");
            if (params.getNumRuns() < 0 || params.getNumRuns() > MAX_NUMRUNS)
                throw new ParseException("Number of runs out of bound.");

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return params;
    }
}
