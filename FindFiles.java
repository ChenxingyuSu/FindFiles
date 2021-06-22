import java.io.*;
import java.util.*;
import java.util.Vector;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;



public class FindFiles {
    public static boolean matched = false;
    public static String filename;
    public static String direct;
    public static String ext;
    public static Vector<String> errorArr = new Vector<>();
    public static boolean has_r = false;
    public static boolean has_reg = false;
    public static boolean has_dir = false;
    public static boolean has_ext = false;
    public static ArrayList<String> extensions = new ArrayList<>();
    public static ArrayList<String> result = new ArrayList<>();


    public static boolean is_valid_regex(String regex) {
        try {
            Pattern.compile(regex);
        } catch (PatternSyntaxException ex) {
            return false;
        }
        return true;
    }

    public static void help() {
        System.out.println("Usage: java FindFiles filetofind [-option arg]\n" +
                "-help                     :: print out a help page and exit the program.\n" +
                "-r                        :: execute the command recursively in subfiles.\n" +
                "-reg                      :: treat `filetofind` as a regular expression when searching.\n" +
                "-dir [directory]          :: find files starting in the specified directory.\n" +
                "-ext [ext1,ext2,...]      :: find files matching [filetofind] with extensions [ext1, ext2,...].");
        System.exit(0);
    }

    public static void defaultFindFile(String name, File file) {
        System.out.println("Searching for file '" + name +"' in directory " + file);
        File[] list = file.listFiles();
        if (list != null) {
            for (File fil : list) {
                if (fil.isDirectory()) {
                    if (FindFiles.has_r) {
                        defaultFindFile(name, fil);
                    } else {
                        continue;
                    }
                } else if (name.equals(fil.getName())) {
                    FindFiles.matched = true;
                    FindFiles.result.add(fil.getAbsolutePath());
                    //System.out.println(fil.getAbsolutePath());
                } else {
                    continue;
                }
            }
        } else {
            if (FindFiles.has_dir) {
                String emptyDirc = "ERROR: The specified directory " + FindFiles.direct + " is empty. " +
                        "Please provide a non-empty directory.";
                System.out.println(emptyDirc);
                System.exit(0);
            } else {
                String emptyDirc = "ERROR: The current directory " + FindFiles.direct + " is empty. " +
                        "Please specify a non-empty directory using \'-dir\' option.";
                System.out.println(emptyDirc);
                System.exit(0);
            }
        }
    }

    public static void regexFindFile(String name, File file) {
        System.out.println("Searching for file that matches the given regular expression '" + name +"' in directory " + file);
        File[] list = file.listFiles();
        if (list != null) {
            for (File fil : list) {
                if (fil.isDirectory()) {
                    if (FindFiles.has_r) {
                        regexFindFile(name, fil);
                    } else {
                        continue;
                    }
                } else if (Pattern.matches(name, fil.getName())) {
                    if (!has_ext) {
                        FindFiles.matched = true;
                        FindFiles.result.add(fil.getAbsolutePath());
                        //System.out.println(fil.getAbsolutePath());
                    } else {
                        for (int i = 0; i < FindFiles.extensions.size(); ++i) {
                            if (Pattern.matches(FindFiles.extensions.get(i), fil.getName())){
                                FindFiles.matched = true;
                                FindFiles.result.add(fil.getAbsolutePath());
                                //System.out.println(fil.getAbsolutePath());
                            }
                        }
                    }
                } else {
                    continue;
                }
            }
        } else {
            if (FindFiles.has_dir) {
                String emptyDirc = "ERROR: The specified directory " + FindFiles.direct + " is empty. " +
                        "Please provide a non-empty directory.";
                System.out.println(emptyDirc);
                System.exit(0);
            } else {
                String emptyDirc = "ERROR: The current directory " + FindFiles.direct + " is empty. " +
                        "Please specify a non-empty directory using \'-dir\' option.";
                System.out.println(emptyDirc);
                System.exit(0);
            }
        }
    }

    public static void isMatchGivenDirec() {
        if (!FindFiles.matched) {
            System.out.println("Unable to find the file specified in the given directory: "
                    + FindFiles.direct +". Please try another file name or try another directory.");
            System.exit(0);
        } else {
            System.out.println("We found the following matching files:");
            for (int i = 0; i < FindFiles.result.size(); ++i) {
                System.out.println(FindFiles.result.get(i));
            }
        }
    }

    public static void isMatchCurrDirec() {
        if (!FindFiles.matched) {
            System.out.println("Unable to find the file specified in the current directory. " +
                    "Please try another file name or specify a directory using -dir option.");
            System.exit(0);
        }else {
            System.out.println("We found the following matching files:");
            for (int i = 0; i < FindFiles.result.size(); ++i) {
                System.out.println(FindFiles.result.get(i));
            }
        }
    }


    public static void main(String[] args) {
        //Vector<String> errorArr = new Vector<>();
        // user provided less than expected minimum number of arguments
        // ie. user provided no argument
        if (args.length < 1) {
            help();
        }
        for (int i = 0; i < args.length; ++i) {
            if (args[i].equals("-help")) {
                help();
            }
        }

        //FindFiles.filename = args[0];
        // no options are specified
        if (args.length == 1) {
            char firstChar = args[0].charAt(0);
            if (firstChar == '-') {
                if ((args[0].equals("-r")) || (args[0].equals("-ext")) ||
                        (args[0].equals("-dir")) || (args[0].equals("-reg"))) {
                    String missing_filename = "ERROR: missing file name, please enter the file name.";
                    errorArr.add(missing_filename);
                } else{
                    String invalid_option = "ERROR: \'" + args[0] + "\' is an invalid option. Please supply valid options from the following list: " +
                            "\'-help\', \'-r\', \'-reg\', \'-dir\', \'-ext\'.";
                    FindFiles.errorArr.add(invalid_option);
                }
            } else {
                FindFiles.filename = args[0];
            }
            // no error reported before, default behaviour: search for the file in the currently
            // directory and to NOT recurse into subdirectories.
            if (FindFiles.errorArr.size() == 0){
                String currDirectory = System.getProperty("user.dir");
                defaultFindFile(args[0], new File(currDirectory));
                isMatchCurrDirec();
            }
            // error reported, print out the first error message
            else {
                System.out.println(FindFiles.errorArr.firstElement());
                if (FindFiles.errorArr.size() == 1) {
                    System.exit(0); // there is only one error, report the error message and exit
                } else {
                    help(); // more than one error, report error and print help then exit
                }
            }
        } else {
            for (int i = 0; i < args.length; ++i) { // Assuming multiple occurrences of same option will not be tested

                if (args[i].equals("-r")) {
                    FindFiles.has_r = true;
                } else if (args[i].equals("-reg")) {
                    FindFiles.has_reg = true;
                } else if (args[i].equals("-dir")) {
                    FindFiles.has_dir = true;
                    if (i + 1 == args.length) {
                        String missing_param = "ERROR: specific directory missing. \'-dir\' requires arguments to be specified.";
                        FindFiles.errorArr.add(missing_param);
                        continue;
                    }
                    char firstChar = args[i+1].charAt(0);
                    if (firstChar == '-') {
                        String invalid_option =
                                "ERROR: \'" + args[i + 1] + "\' is an invalid option. Please supply valid options from the following list: " +
                                        "\'-help\', \'-r\', \'-reg\', \'-dir\', \'-ext\'\n";
                        String missing_parm = "ERROR: specific directory missing. \'-dir\' requires arguments to be specified.";
                        FindFiles.errorArr.add(invalid_option);
                        FindFiles.errorArr.add(missing_parm);
                    } else {
                        FindFiles.direct = args[i + 1];
                        if (!new File(FindFiles.direct).isDirectory()) {
                            String invalid_direct = "ERROR: " + args[i+1] + " is an invalid directory. Please enter a valid directory.";
                            FindFiles.errorArr.add(invalid_direct);
                        }
                    }
                    i++;
                } else if (args[i].equals("-ext")) {
                    if (i + 1 == args.length) {
                        String missing_param = "ERROR: specific file extension missing. \'-ext\' requires arguments to be specified.";
                        FindFiles.errorArr.add(missing_param);
                        continue;
                    }
                    FindFiles.has_ext = true;
                    char firstChar = args[i+1].charAt(0);
                    if (firstChar == '-') {
                        String invalid_option =
                                "ERROR: \'" + args[i+1] + "\' is an invalid option. Please supply valid options from the following list: " +
                                        "\'-help\', \'-r\', \'-reg\', \'-dir\', \'-ext\'.";
                        String missing_param = "ERROR: specific file extension missing. \'-ext\' requires arguments to be specified.";
                        FindFiles.errorArr.add(invalid_option);
                        FindFiles.errorArr.add(missing_param);
                    } else {
                        FindFiles.ext = args[i + 1];
                    }
                    i++;
                } else {
                    char firstChar = args[i].charAt(0);
                    if (firstChar == '-') {
                        String invalid_option =
                                "ERROR: \'" + args[i] + "\' is an invalid option. Please supply valid options from the following list: " +
                                        "\'-help\', \'-r\', \'-reg\', \'-dir\', \'-ext\'.";
                        FindFiles.errorArr.add(invalid_option);
                    } else {
                        FindFiles.filename = args[i];
                    }
                }
            }
            int count = 0;
            if (has_dir) count += 2;
            if (has_ext) count += 2;
            if (has_r) count += 1;
            if (has_reg) {
                if (!is_valid_regex(FindFiles.filename)) {
                    String invalid_regex =
                            "ERROR: " + filename + " is an invalid regular expression. Please enter a valid regular expression.";
                    FindFiles.errorArr.add(invalid_regex);
                }
                count += 1;
            }
            if (args.length - count != 1) {
                String exceeds_expected_number_of_args =
                        "ERROR: the number of arguments entered exceeds the maximum expected number of arguments.";
                FindFiles.errorArr.add(exceeds_expected_number_of_args);

            }
            if (FindFiles.errorArr.size() != 0) {
                System.out.println(FindFiles.errorArr.firstElement());
                if (FindFiles.errorArr.size() > 1) {
                    help();
                } else {
                    System.exit(0);
                }
            }
            
            if (has_ext){
                String ext_str = FindFiles.ext;
                //System.out.println(ext_str);
                while (ext_str.indexOf(",") != -1) {
                    int index = ext_str.indexOf(",");
                    String e = ext_str.substring(0, index);
                    FindFiles.extensions.add(e);
                    ext_str = ext_str.substring(index + 1);
                }
                FindFiles.extensions.add(ext_str);
            }
            // Since two more error cases are checked above, check the number of elements in errorArr,
            // if error occur, print the first error, if error > 1 then print help()
            if (FindFiles.errorArr.size() == 1) {
                System.out.println(FindFiles.errorArr.firstElement());
                System.exit(0);
            } else if (FindFiles.errorArr.size() > 1) {
                System.out.println(FindFiles.errorArr.firstElement());
                help();
            }
            if (!FindFiles.has_reg) {
                if (!FindFiles.has_ext) {
                    if (FindFiles.has_dir) {
                        defaultFindFile(FindFiles.filename, new File(FindFiles.direct));
                        isMatchGivenDirec();
                    } else {
                        String currDirectory = System.getProperty("user.dir");
                        defaultFindFile(FindFiles.filename, new File(currDirectory));
                        isMatchCurrDirec();
                    }
                } else {
                    FindFiles.extensions.replaceAll(s -> FindFiles.filename + "." + s);
                    for(int i = 0; i < FindFiles.extensions.size(); i++) {
                        if (FindFiles.has_dir) {
                            defaultFindFile(FindFiles.extensions.get(i), new File(FindFiles.direct));
                            isMatchGivenDirec();
                        } else {
                            String currDirectory = System.getProperty("user.dir");
                            defaultFindFile(FindFiles.extensions.get(i), new File(currDirectory));
                            isMatchCurrDirec();
                        }
                    }
                }
            } else {
                if (has_ext) {
                    FindFiles.extensions.replaceAll(s -> ".*\\." + s);
                }
                if (FindFiles.has_dir) {
                    regexFindFile(FindFiles.filename, new File(FindFiles.direct));
                    isMatchGivenDirec();
                } else {
                    String currDirectory = System.getProperty("user.dir");
                    regexFindFile(FindFiles.filename, new File(currDirectory));
                    isMatchCurrDirec();
                }
            }
            System.exit(0);
        }
    }
}
