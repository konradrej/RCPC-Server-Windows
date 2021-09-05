import java.io.File;

/**
 * Entry point for application.
 *
 * @author Konrad Rej
 * @author www.konradrej.com
 * @version 1.0
 */
public class App {
    public static void main(String[] args) {
        // Gets logger config file from resources and sets system property.
        String logConfigPath = new File(TempApp.class.getClassLoader().getResource("log4j2.configurationFile.xml").getFile()).getAbsolutePath();
        System.setProperty("log4j2.configurationFile", logConfigPath);
    }
}
