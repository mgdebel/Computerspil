import java.io.*;
import java.net.*;
import java.nio.charset.*;
import java.nio.file.*;
import java.nio.channels.*;
import java.util.*;
import java.awt.Desktop;
import javax.swing.JOptionPane;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * This class is used to communicate with the test server.
 *
 * Errors should be reported via the webboard.
 * Describe the problem as detailed and precise as possible.
 * If we cannot reproduce it, we cannot fix it.
 *
 * @author Nikolaj I. Schwartzbach & Asger Phillip Andersen
 * @version 2023-11-22
 */
public class TestServer {
    private static String srcDir = "";
    private static final String testServerOnlinePath = "https://DeadViolets.github.io/IntProg-undervisningsmateriale/web/e23/opgaver/";

    private TestServer() {
    }

    /**
     * Downloads any available updates to the testserver file.
     * 
     * @throws IOException
     */
    public static void updateTestServer() {
        String errMsg = "";
        String messageDialogHeader = "TestServer-Klient Opdatering";
        if (updateAvailable()) {
            try (ReadableByteChannel rbc = Channels.newChannel(new URL(
                    testServerOnlinePath + "TestServer.java")
                    .openStream());
                    FileOutputStream fos = new FileOutputStream(srcDir + "TestServer.java")) {
                fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
                JOptionPane.showMessageDialog(null,
                        "Din lokale testserver fil er blevet opdateret. Genstart venligst BlueJ",
                        messageDialogHeader, JOptionPane.INFORMATION_MESSAGE);
            } catch (FileNotFoundException e) {
                errMsg = "Det var ikke muligt at overskrive den lokale fil.\nKontakt venligst en instruktor.";
            } catch (MalformedURLException e) {
                errMsg = "Kan ikke oprette forbindelse til Remote URL.\nKontakt venligst en instruktor.";
            } catch (IOException e) {
                errMsg = "Der opstod en fejl med FileOutputStream. Prøv eventuelt at køre BlueJ med adminstrator-rettigheder.\n Ellers kontakt en instruktor.\n\nFejl:\n"
                        + e.getMessage();
            } finally {
                if (!errMsg.equals("")) {
                    JOptionPane.showMessageDialog(null,
                            errMsg,
                            messageDialogHeader, JOptionPane.ERROR_MESSAGE);
                    System.err.println(errMsg);
                }
            }
        } else {
            JOptionPane.showMessageDialog(null, "Du har allerede den nyeste version af TestServer filen",
                    "TestServer Opdatering", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * Checks if there is an update available, for the testserver.
     * NOT CURRENTLY IN USE
     */
    private static void checkForUpdates() {
        boolean updateAvailable = updateAvailable();
        if (updateAvailable) {
            System.out.println("Der er en ny version af TestServeren. Kald updateTestServer metoden, for at opdatere");
        } else {
            System.out.println("Du har den nyeste version af TestServeren :)");
        }
    }

    /**
     * Checks if there is an update available, used in both the public method, and
     * the download method
     * 
     * @return true when there is an update available, otherwise false.
     */
    private static boolean updateAvailable() {
        setSrcDir();
        String errMsg = "";
        String messageDialogHeader = "TestServer-Klient Tjek Opdatering";
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("uuuu-MM-dd");

        try (InputStream local = new FileInputStream(new File(srcDir + "TestServer.java"));
                InputStream online = (new URL(
                        testServerOnlinePath + "TestServer.java"))
                        .openStream()) {
            LocalDate localDate = LocalDate.parse(parseVersionDate(local), dtf);
            LocalDate onlineDate = LocalDate.parse(parseVersionDate(online), dtf);

            return localDate.isBefore(onlineDate);
        } catch (MalformedURLException e) {
            errMsg = "Kan ikke oprette forbindelse til Remote URL.\nKontakt venligst en instruktor.";
        } catch (FileNotFoundException e) {
            errMsg = "Kan ikke finde den lokale testserver-klient fil.\nKontakt venligst en instruktor.";
        } catch (IOException e) {
            errMsg = "Der skete en IO fejl ved opdateringstjekket.\nKontakt venligst en instruktor.\n\nFejl:\n"
                    + e.getMessage();
        } finally {
            if (!errMsg.equals("")) {
                JOptionPane.showMessageDialog(null,
                        errMsg,
                        messageDialogHeader, JOptionPane.ERROR_MESSAGE);
                System.err.println(errMsg);
            }
        }
        return false;
    }

    /**
     * Parses the version date of the testserver file specified in the inputstream
     * 
     * @param testServerInputStream - An input stream to the testserver file
     * @return The version date of the given file, as a string. uuuu-MM-dd
     *         formatted.
     */
    private static String parseVersionDate(InputStream testServerInputStream) {
        Scanner s = new Scanner(testServerInputStream);
        String date = "";
        while (s.hasNextLine()) {
            String line = s.nextLine();
            if (line.length() > 21 && line.substring(3, 11).equals("@version")) {
                date = line.substring(12, 22);
                break;
            }
        }

        s.close();

        return date;
    }

    /**
     * Downloads the latest files from the course webpage.
     * 
     * @param exercise one of: CG1, CG3, CG5
     */
    public static void download(String exercise) throws IOException {
        if (updateAvailable()) {
            int optionChosen = JOptionPane.showOptionDialog(null,
                    "Der er en ny version af TestServer filen, vil du opdatere nu?\n(Dette er påkrævet for at kunne downloade nye filer)",
                    "Opdater TestServer fil?",
                    JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, null);
            if (optionChosen == JOptionPane.YES_NO_OPTION) {
                updateTestServer();
            }

            return;
        }

        String ex = exercise.toLowerCase();

        switch (ex) {

            case "cg1":
                downloadCourseFiles("cg1", "Game.java", "GUI.java",
                        "Settings.java", "Player.java", "GUIPlayer.java", "RandomPlayer.java",
                        "GreedyPlayer.java", "SmartPlayer.java", "CGTest.java",
                        "network.dat", "map.png", "greedyplayer.png", "randomplayer.png",
                        "smartplayer.png", "guiplayer.png");
                break;
            case "cg3":
                downloadCourseFiles("cg3", "Game.java", "GUI.java",
                        "Player.java", "RandomPlayer.java",
                        "GreedyPlayer.java", "SmartPlayer.java");
                break;
            case "cg5":
                downloadCourseFiles("cg5", "LogPlayer.java", "Game.java", "Settings.java");
                break;
        }
    }

    /**
     * Tests exercise and open the resulting link in the default browser (if
     * uploaded successfully).
     * 
     * @param exercise Short name of exercise.
     */
    public static void testAndOpenInBrowser(String exercise) throws IOException, URISyntaxException {
        String str = test(exercise);
        if (str.contains("Link: ")) {
            str = str.substring(str.indexOf("Link: ") + 6);
            Desktop.getDesktop().browse(new URL(str).toURI());
        }
    }

    /**
     * Tests exercise in the classpath (the folder from which the program is
     * invoked).
     * 
     * @param exercise Short name of exercise.
     */
    public static String test(String exercise) throws IOException {
        if (!isAlive()) {
            JOptionPane.showMessageDialog(null,
                    "Testserveren er desværre nede. Prøv igen senere, og skriv eventuelt på diskussionsforummet, hvis der ikke allerede er gjort det.",
                    "TestServer nede", JOptionPane.ERROR_MESSAGE);

            return "";
        }

        if (updateAvailable()) {
            int optionChosen = JOptionPane.showOptionDialog(null,
                    "Der er en ny version af TestServer filen, vil du opdatere nu?\n(Dette er påkrævet for at kunne teste opgaver)",
                    "Opdater TestServer fil?",
                    JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, null);
            if (optionChosen == JOptionPane.YES_NO_OPTION) {
                updateTestServer();
            }

            return "";
        }

        String ex = exercise.toLowerCase();
        // Files to be uploaded
        String[] files = null;
        switch (ex) {

            // DieCup
            case "dc1":
                files = new String[] { "Die", "DieCup" };
                break;
            case "dc2":
            case "dc3-1":
            case "dc3-4":
                files = new String[] { "Die", "DieCup", "TestDriver" };
                break;
            case "dc4-1":
            case "dc4-2":
                files = new String[] { "Die", "DieCup", "DieTest", "DieCupTest" };
                break;
            case "dc4-3":
                files = new String[] { "Game" };
                break;

            // Turtle
            case "turtle1":
            case "turtle2":
                files = new String[] { "Actor", "Canvas", "Turtle" };
                break;

            // 2016
            case "basketplayer":
                files = new String[] { "BasketPlayer", "BasketTeam", "TestDriver" };
                break;
            case "cow":
                files = new String[] { "Cow", "DiaryFarm", "TestDriver" };
                break;
            case "field-1":
            case "field-2":
                files = new String[] { "Field", "Farm", "TestDriver" };
                break;
            case "goat":
                files = new String[] { "Goat", "GoatFarm", "TestDriver" };
                break;
            case "handballplayer":
                files = new String[] { "HandballPlayer", "HandballTeam", "TestDriver" };
                break;
            case "image":
                files = new String[] { "Image", "UsbStick", "TestDriver" };
                break;
            case "soccerplayer":
                files = new String[] { "SoccerPlayer", "SoccerTeam", "TestDriver" };
                break;
            case "volleyplayer":
                files = new String[] { "VolleyPlayer", "VolleyTeam", "TestDriver" };
                break;
            case "passenger":
                files = new String[] { "Passenger", "Ferry", "TestDriver" };
                break;
            case "picture":
                files = new String[] { "Picture", "UsbStick", "TestDriver" };
                break;
            case "song":
                files = new String[] { "Song", "DVD", "TestDriver" };
                break;

            // 2017
            case "animal":
                files = new String[] { "Animal", "Zoo", "TestDriver" };
                break;
            case "brick-1":
            case "brick-2":
                files = new String[] { "Brick", "LegoBox", "TestDriver" };
                break;
            case "carpet-1":
            case "carpet-2":
                files = new String[] { "Carpet", "Shop", "TestDriver" };
                break;
            case "tool-1":
            case "tool-2":
                files = new String[] { "Tool", "ToolBox", "TestDriver" };
                break;
            case "vegetable-1":
            case "vegetable-2":
                files = new String[] { "Vegetable", "Shop", "TestDriver" };
                break;
            case "nail":
                files = new String[] { "Nail", "Box", "TestDriver" };
                break;
            case "screw":
                files = new String[] { "Screw", "Box", "TestDriver" };
                break;
            case "chicken":
                files = new String[] { "Chicken", "ChickenYard", "TestDriver" };
                break;
            case "pigeon":
                files = new String[] { "Pigeon", "PigeonLoft", "TestDriver" };
                break;
            case "penguin":
                files = new String[] { "Penguin", "Group", "TestDriver" };
                break;

            // 2018
            case "ferry":
                files = new String[] { "Ferry", "Harbour", "TestDriver" };
                break;
            case "train-1":
            case "train-2":
                files = new String[] { "Train", "TrainStation", "TestDriver" };
                break;
            case "bus-1":
            case "bus-2":
                files = new String[] { "Bus", "BusStation", "TestDriver" };
                break;
            case "flight":
                files = new String[] { "Flight", "Airport", "TestDriver" };
                break;

            // 2019
            case "chapter":
                files = new String[] { "Chapter", "Book", "TestDriver" };
                break;
            case "cheese":
                files = new String[] { "Cheese", "Cooler", "TestDriver" };
                break;
            case "drink":
                files = new String[] { "Drink", "Refrigerator", "TestDriver" };
                break;
            case "flower":
                files = new String[] { "Flower", "Bouquet", "TestDriver" };
                break;
            case "food":
                files = new String[] { "Food", "DeepFreezer", "TestDriver" };
                break;
            case "fruit":
                files = new String[] { "Fruit", "Basket", "TestDriver" };
                break;
            case "pearl":
                files = new String[] { "Pearl", "Necklace", "TestDriver" };
                break;
            case "photo":
                files = new String[] { "Photo", "Album", "TestDriver" };
                break;

            // 2020
            case "cat":
                files = new String[] { "Cat", "Family", "TestDriver" };
                break;
            case "dog":
                files = new String[] { "Dog", "Kennel", "TestDriver" };
                break;
            case "elephant":
                files = new String[] { "Elephant", "Zoo", "TestDriver" };
                break;
            case "fox":
                files = new String[] { "Fox", "Island", "TestDriver" };
                break;
            case "horse":
                files = new String[] { "Horse", "Farm", "TestDriver" };
                break;
            case "lion":
                files = new String[] { "Lion", "Savannah", "TestDriver" };
                break;
            case "pet":
                files = new String[] { "Pet", "Child", "TestDriver" };
                break;
            case "rabbit":
                files = new String[] { "Rabbit", "Boy", "TestDriver" };
                break;
            case "mouse":
                files = new String[] { "Mouse", "Girl", "TestDriver" };
                break;
            case "pig":
                files = new String[] { "Pig", "Island", "TestDriver" };
                break;
            case "sheep":
                files = new String[] { "Sheep", "Farm", "TestDriver" };
                break;
            case "tiger":
                files = new String[] { "Tiger", "Forest", "TestDriver" };
                break;

            // Videos
            case "phone":
                files = new String[] { "Phone", "WebShop", "TestDriver" };
                break;
            case "pirate":
                files = new String[] { "Pirate", "PirateShip", "TestDriver" };
                break;
            case "car":
                files = new String[] { "Car", "Store", "TestDriver" };
                break;
            case "turtle":
                files = new String[] { "Turtle", "Zoo", "TestDriver" };
                break;

            // Handins
            case "musician":
                files = new String[] { "Musician", "Band", "TestDriver" };
                break;
            case "racer":
                files = new String[] { "Racer", "FormulaOne", "TestDriver" };
                break;
            case "boat":
                files = new String[] { "Boat", "Marina", "TestDriver" };
                break;
            case "biker":
                files = new String[] { "Biker", "MotorcycleClub", "TestDriver" };
                break;
            case "film":
                files = new String[] { "Film", "FilmCollection", "TestDriver" };
                break;

            case "cg1-1":
                files = new String[] { "City" };
                break;
            case "cg1-2":
                files = new String[] { "Road" };
                break;
            case "cg1-3":
                files = new String[] { "Position" };
                break;
            case "cg1-4":
                files = new String[] { "Country" };
                break;
            case "cg1-5":
                files = new String[] { "City", "Country" };
                break;
            case "cg1-6":
                files = new String[] { "Country" };
                break;
            case "cg1-7":
                files = new String[] { "Country" };
                break;
            case "cg1":
                files = new String[] { "City", "Road", "Position", "Country" };
                break;

            case "cg2-1":
                files = new String[] { "RoadTest" };
                break;
            case "cg2-2":
                files = new String[] { "PositionTest" };
                break;
            case "cg2-3":
                files = new String[] { "CityTest" };
                break;
            case "cg2-4":
                files = new String[] { "CountryTest" };
                break;
            case "cg2":
                files = new String[] { "RoadTest", "PositionTest", "CityTest", "CountryTest" };
                break;

            case "cg3-1":
                files = new String[] { "BorderCity", "BorderCityTest" };
                break;
            case "cg3-2":
                files = new String[] { "CapitalCity", "CapitalCityTest" };
                break;
            case "cg3-3":
                files = new String[] { "MafiaCountry", "MafiaCountryTest" };
                break;
            case "cg3-4":
                files = new String[] { "City", "CityTest" };
                break;
            case "cg3":
                files = new String[] { "City", "CityTest", "BorderCity", "BorderCityTest", "CapitalCity",
                        "CapitalCityTest",
                        "MafiaCountry", "MafiaCountryTest" };
                break;

            case "cg5":
                files = new String[] { "Log", "Settings" };
                break;

            default:
                JOptionPane.showMessageDialog(null,
                        "Invalid exercise: " + exercise + ".",
                        "Test af opgave", JOptionPane.ERROR_MESSAGE);
                System.err.println("Invalid exercise: " + exercise + ".");
                return "Invalid exercise: " + exercise + ".";
        }

        Map<String, String> arguments = new HashMap<>();

        // Check upload code
        String auID = "", code = "";

        // Check if auID/code are saved
        Path dataPath = Paths.get("upload-data.dat");
        if (Files.exists(dataPath)) {
            // Read first line
            String line = Files.readAllLines(dataPath).get(0);

            // Split into auID and code
            String[] data = line.split(" ");

            auID = data[0];
            code = data[1];
        }

        // Check if information is missing
        if (auID.isEmpty() || code.isEmpty()) {
            // Get user input
            Scanner s = new Scanner(System.in);

            System.out.print(
                    "Første gang du uploader et projekt til testserveren skal du indtaste dit auID og en adgangskode.\n"
                            +
                            "Begge dele huskes i din projektmappe, således at du ikke behøver at indtaste dem ved senere uploads af projektet.\n\n"
                            +
                            "Du kan skaffe dig en adgangskode via linket: https://dintprog.cs.au.dk/reset.php\n\n" +
                            "Det er samme adgangskode, der anvendes til quizserveren og til testserveren.\n\n" +
                            "Indtast auID: \n> ");

            auID = s.nextLine();
            System.out.print("\nIndtast adgangskode: \n> ");
            code = s.nextLine();

            s.close();

            // Save information for next time
            PrintWriter pw = new PrintWriter("upload-data.dat");
            pw.println(auID + " " + code);
            pw.close();
        }

        // Set meta information
        arguments.put("h", ex.replace("dc3-1", "dc3a").replace("dc3-4", "dc3b").replace("dc4-1", "dc4a")
                .replace("dc4-2", "dc4b").replace("dc4-3", "dc4c"));
        arguments.put("auID", auID.toLowerCase());
        arguments.put("code", code);

        // Check all files and accumulate contents
        for (String file : files) {
            if (file.equals("TestDriver") && !Files.exists(Paths.get(srcDir + "TestDriver.java"))) {
                file = "Driver";
            }

            Path p = Paths.get(srcDir + file + ".java");
            // handling for IntelliJ setup (aka file is stored in "<user.dir>/src")
            if (!Files.exists(p))
                p = Paths.get("src", file + ".java");

            // Check if file exists
            if (Files.exists(p)) {
                String str = new String(Files.readAllBytes(p), Charset.defaultCharset());
                arguments.put(file.equals("Driver") ? "testdriver" : file.toLowerCase(), str);
            } else {
                String errMsg = "No file with this name '" + p.toRealPath().toString() + "'.";
                JOptionPane.showMessageDialog(null,
                        errMsg,
                        "Test af opgave", JOptionPane.ERROR_MESSAGE);
                System.err.println(errMsg);
                return errMsg;
            }
        }

        // Send request and handle response
        String response = sendPost("https://dintprog.cs.au.dk/upload.php", arguments);
        response = response.replace("-server", "");
        System.out.println(response);
        if (response.startsWith("Invalid")) {
            try {
                Files.delete(dataPath);
                test(ex);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return response;
    }

    /**
     * This method downloads a variable number of files as specified by files.
     * 
     * @param version The version of the files to download. One of: cg1, cg2, cg3.
     * @param files   The files to download.
     */
    private static void downloadCourseFiles(String version, String... files) {
        for (String file : files)
            try {
                downloadFile(testServerOnlinePath
                        + version + "/" + file, file);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null,
                        "Unable to download file: " + file + ". Reason: " + e.getMessage(),
                        "Download filer", JOptionPane.ERROR_MESSAGE);
                System.err.println("Unable to download file: " + file + ". Reason: " + e.getMessage());
            }

        JOptionPane.showMessageDialog(null, "Download af filer for version '" + version
                + "' blev gennemført succesfuldt. Det kan være nødvendigt at genstarte BlueJ for at se de nye filer.",
                "Filer downloadet succesfuldt.", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * This method downloads a file from a url and saves it to the local machine.
     * 
     * @param url  The url from which to download.
     * @param dest The destination of the downloaded file.
     */
    private static void downloadFile(String url, String dest) throws IOException {
        dest = dest.endsWith(".java") ? srcDir + dest : dest;
        if (Files.exists(Paths.get(dest)))
            Files.delete(Paths.get(dest));
        URL website = new URL(url);
        ReadableByteChannel rbc = Channels.newChannel(website.openStream());
        FileOutputStream fos = new FileOutputStream(dest);
        fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        fos.close();
    }

    /**
     * This method sends an HTTP POST request to a specified location with some
     * arguments.
     * 
     * @param location  URL to POST to.
     * @param arguments HTTP header arguments.
     * @return Response from the URL (if any).
     * @throws IOException For various reasons.
     */
    private static String sendPost(String location, Map<String, String> arguments) throws IOException {

        URL url = new URL(location);
        URLConnection con = url.openConnection();
        HttpURLConnection http = (HttpURLConnection) con;
        con.setRequestProperty("User-Agent", "Java client");
        con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        http.setRequestMethod("POST"); // PUT is another valid option
        http.setDoOutput(true);

        StringJoiner sj = new StringJoiner("&");
        for (Map.Entry<String, String> entry : arguments.entrySet())
            sj.add(URLEncoder.encode(entry.getKey(), "UTF-8") + "="
                    + URLEncoder.encode(entry.getValue(), "UTF-8"));
        byte[] out = sj.toString().getBytes(StandardCharsets.UTF_8);
        int length = out.length;
        http.setFixedLengthStreamingMode(length);
        http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        http.connect();
        try (OutputStream os = http.getOutputStream()) {
            os.write(out);
        }

        StringBuilder sb;
        try (BufferedReader in = new BufferedReader(new InputStreamReader(http.getInputStream()))) {
            String line;
            sb = new StringBuilder();
            while ((line = in.readLine()) != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
            }
        }
        http.disconnect();
        return sb.toString();
    }

    private static void setSrcDir() {
        srcDir = Files.isDirectory(Paths.get("src")) ? "src/" : "";
    }

    private static boolean isAlive() throws IOException {
        URL obj = new URL("https://dintprog.cs.au.dk/alive.php");
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", "Java client");
        int responseCode = con.getResponseCode();
        // System.out.println("GET Response Code :: " + responseCode);
        if (responseCode == HttpURLConnection.HTTP_OK) { // success
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            String status = response.toString();
            return "oppe".equals(response.toString());
        } else {
            System.err.println("Serveren svarede ikke. Kontakt en instruktor");
            return false;
        }
    }
}
