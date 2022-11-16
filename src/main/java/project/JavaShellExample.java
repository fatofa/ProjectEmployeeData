package project;


import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
public class JavaShellExample {
    private static final String WINDOWS_OS_PREFIX = "windows";
    private static final String OS_NAME_KEY = "os.name";
    private static String projectPath = System.getProperty("user.dir");
    private static String pathChromeDriverZip = projectPath + "\\src\\main\\resources\\windows";
    private static String version;
    public static void main(String[] args) {
        System.out.println("Chrome in local is: " + getVersionChromeDriverLocal());

        if (!checkChromeProjectWithChromeDriverLocal()) {
            downloadChromeDriver();
            System.out.println("Update Chrome Driver Done");
        } else {
            System.out.println("Chrome Driver is new version");
        }
    }
    private static boolean isWindows() {
        return System.getProperty(OS_NAME_KEY).toLowerCase().startsWith(WINDOWS_OS_PREFIX);
    }
    private static String getVersionChromeDriverLocal() {
        ProcessBuilder processBuilder = new ProcessBuilder();
        if (isWindows()) {
            // Run this on Windows, cmd, /c = terminate after this run
            processBuilder.command("cmd.exe", "/c", "curl https://chromedriver.storage.googleapis.com/LATEST_RELEASE --ssl-no-revoke");
        }
        try {
            Process process = processBuilder.start();
            // blocked :(
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((version = reader.readLine().toString()) != null) {
                break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return version;
    }
    public static void downloadChromeDriver() {
        if (isWindows()) {
            // Install ChromeDriver
            try {
                FileWriter myWriter = new FileWriter("src/main/resources/windows/BatFolder/install.bat");
                myWriter.write("curl https://chromedriver.storage.googleapis.com/" + getVersionChromeDriverLocal() + "/chromedriver_win32.zip --output " + pathChromeDriverZip + "\\DriverChrome.zip --ssl-no-revoke");
                myWriter.close();
                System.out.println("Successfully wrote to the file.");
            } catch (IOException e) {
                System.out.println("An error occurred.");
                e.printStackTrace();
            }
            try{
                Process p = Runtime.getRuntime().exec("src/main/resources/windows/BatFolder/install.bat");
                p.waitFor();
                System.out.println("Install ChromeDriver success");
            }catch( IOException ex ){
                //Validate the case the file can't be accesed (not enought permissions)
            }catch( InterruptedException ex ){
                //Validate the case the process is being stopped by some external situation
            }
        }
        if (isWindows()) {
            // Unzip chromedriver
            try {
                FileWriter myWriter = new FileWriter("src/main/resources/windows/BatFolder/unzip.bat");
                myWriter.write("powershell expand-archive " + pathChromeDriverZip + "\\DriverChrome.zip " + pathChromeDriverZip +"\\DriverChrome -Force");
                myWriter.close();
                System.out.println("Successfully wrote to the file.");
            } catch (IOException e) {
                System.out.println("An error occurred.");
                e.printStackTrace();
            }
            try{
                Process p = Runtime.getRuntime().exec("src/main/resources/windows/BatFolder/unzip.bat");
                p.waitFor();
                System.out.println("Unzip ChromeDriver success");
            }catch( IOException ex ){
                //Validate the case the file can't be accesed (not enought permissions)
            }catch( InterruptedException ex ){
                //Validate the case the process is being stopped by some external situation
            }
        }
    }
    public static boolean checkChromeProjectWithChromeDriverLocal() {
        String line = null;
        ProcessBuilder processBuilder = new ProcessBuilder();
        if (isWindows()) {
            // Run this on Windows, cmd, /c = terminate after this run
            processBuilder.command("cmd.exe", "/c", "cd /d " + pathChromeDriverZip + "\\DriverChrome && chromeDriver -v");
        }
        try {
            Process process = processBuilder.start();
            // blocked :(
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            while ((line = reader.readLine().toString()) != null) {
                break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Chrome in Project is: " + line.substring(13,27));
        
        return getVersionChromeDriverLocal().contains(line.substring(13,16));

    }
}