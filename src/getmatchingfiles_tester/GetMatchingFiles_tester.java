
package getmatchingfiles_tester;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.application.Application;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.stage.Window;

/**
 *
 * @author gutich01
 */
public class GetMatchingFiles_tester extends Application {

    //
    private final String IMAGE_DIRECTORY_PREF_KEY = "imageDirectory";
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
    StackPane root = new StackPane();
    static File[] fileList; // = new File[];
    @Override
    public void start(Stage primaryStage) throws IOException {
        primaryStage.setTitle("Get Some Matching Files!");
        
        
        Button btn = new Button();
        btn.setText("Open some dang files already!");
        btn.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                try {
                    System.out.println("Hello World!");
                    openApp(root);
                } catch (IOException ex) {
                    Logger.getLogger(GetMatchingFiles_tester.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        
        root.getChildren().add(btn);
        primaryStage.setScene(new Scene(root, 300, 250));
        primaryStage.show();
        //openApp(root);
    }
    
    private void openApp(StackPane root) throws IOException {
        
        ReadOnlyObjectProperty<Scene> scprop = root.sceneProperty();
        Scene sc = scprop.getValue();
        
        Window w = sc.getWindow();

        final DirectoryChooser chs = new DirectoryChooser();

        final Preferences prefs = Preferences.systemNodeForPackage(GetMatchingFiles_tester.class);
        final String imageDirectory = prefs.get(IMAGE_DIRECTORY_PREF_KEY, System.getProperty("user.home"));

        final File startDir = new File(imageDirectory);
        chs.setInitialDirectory(startDir);

        final File selectedDir = chs.showDialog(w);

        try {
            prefs.put(IMAGE_DIRECTORY_PREF_KEY, selectedDir.getCanonicalPath());
        } catch (IOException ex) {
            Logger.getLogger(GetMatchingFiles_tester.class.getName()).log(Level.SEVERE, null, ex);
        }

        FilenameFilter filter = new FilenameFilter() {

            @Override
            public boolean accept(File dir, String name) {
                // COMMENTED OUT FOR TESTING; UN-COMMENT FOR FINAL V.
                //   if (name.startsWith("0-") || name.startsWith("1-")) {
                //       return true;
                //   }
                if ( name.endsWith(".png") ) { //return false; }
                    if (name.startsWith("0-")) {
                        return true;
                    }
                    if (name.startsWith("d-")) {
                        return true;
                    }
                    if (name.startsWith("r-")) {
                        return true;
                    }
                }
                return false;
            }
        };
        
        //final File[] fileList = selectedDir.listFiles(filter);
        fileList = selectedDir.listFiles(filter);
        
        if (0 == fileList.length) {
            return;
        }
        
        File[] imageSet = getMatchingFiles(fileList[1]);
        
        for ( File ff : imageSet ) {
            if ( ff.exists() ) {
                System.out.println(" ** file in set : " + ff.getName() ); // DEBUG line
            }
        }
        
    }
    
    public static File[] getMatchingFiles(File file) throws IOException {
        
        //File dir = file.getParentFile(); File[] fls = dir.listFiles();
        File[] fls = fileList;
        String filename = file.getName();
        File[] imageSet = new File[4]; // = new File[4]; // TODO: change once have set with d[epth] images
        String baseImageType = "";
        //baseImageType += filename.charAt(0);
        System.out.println(" ** base image type : " + baseImageType ); // DEBUG line
        double unixTime;
        double baseUnixTime = getUnixDatetimeFromFilename(filename);
        double oldDiff = 100000000.00; //Just a really large number...

        if ( imageSet[0] == null || imageSet[1] == null || imageSet[2] == null || imageSet[3] == null ) {
            int i = 0;
            for ( File ff : fls ) {
                String fname = ff.getName();
                //System.out.println(" ** i = " + i ); // DEBUG line
                //System.out.println(" ** base image type index : " + baseImageType.indexOf(fname.charAt(0)) ); // DEBUG line
                if ( baseImageType.indexOf(fname.charAt(0)) == -1 ) {

                    System.out.println(" ** file hit : " + fname );     // DEBUG line
                    System.out.println(" ** base image type : " + baseImageType); // DEBUG line
                    System.out.println(" ** file prefix : " + fname.charAt(0));      // DEBUG line
                    unixTime = getUnixDatetimeFromFilename(fname);
                    double diff = Math.abs((unixTime - baseUnixTime));
                    //System.out.print(" *** unixTime : " + unixTime ); // DEBUG line
                    //System.out.println(" * baseUnixTime : " + baseUnixTime ); // DEBUG line
                    System.out.println(" *** DIFFERENCE (base-current) : " + diff ); // DEBUG line

                    if ( diff > oldDiff ) {
                        switch( fls[i-1].getName().charAt(0) ) { //was: fname.charAt(0) //First fill in the result array...
                            case '0':
                                imageSet[0] = fls[i-1];
                                System.out.println("  > set [0] : " + fls[i-1].getName()); // DEBUG line
                                //imageSet[1] = getFile(hhid, type, "1".concat(imageSet[0].getName().substring(1))); // TODO: implement this when transfer method to URIData.java in backend
                                imageSet[1] = new File(fls[i-1].getParent(), "1".concat(imageSet[0].getName().substring(1)));
                                break;
                            case 'r':
                                imageSet[2] = fls[i-1];
                                System.out.println("  > set [2] : " + fls[i-1].getName()); // DEBUG line
                                break;
                            case 'd':
                                imageSet[3] = fls[i-1];
                                System.out.println("  > set [3] : " + fls[i-1].getName()); // DEBUG line
                                break;
                        }
                        //was : baseImageType += fname.charAt(0);
                        baseImageType += fls[i-1].getName().charAt(0);
                        System.out.println(" ** base image type NOW: " + baseImageType ); // DEBUG line
                        oldDiff = 100000000.00;
                    }
                    else { oldDiff = diff; 
                        System.out.println(" ** oldDiff NOW = " + oldDiff ); // DEBUG line
                    } 
                }
                i = i+1;
                System.out.print(" i : " + i); // DEBUG line
                System.out.println(" ... Leaving (ff : fls) loop " ); // DEBUG line
            }
        }

        //DEBUG lines + 5
        /*
        if ( imageSet[0] != null && imageSet[1] != null && imageSet[2] != null ) {
            for ( File ff : imageSet ) {
                System.out.println(" ** file in set : " + ff.getName() ); // DEBUG line
            }
        } 
        */ // final String f1Name = "1".concat(f0Name.substring(1));
        //imageSet[1] = getFile(hhid, type, "1".concat(imageSet[0].getName().substring(1))); // TODO: implement this when transfer method to URIData.java in backend
        //imageSet[1] = new File(file.getParent(), "1".concat(imageSet[0].getName().substring(1)));
        return imageSet;
    }
    
     /**
     * @return the UNIX timestamp from the image file name
     */
    private static double getUnixDatetimeFromFilename(String filename) {  

        String utime = "0";
        // e.g. 0-1331705389.007177-123_0314_020949-160x120.png
        Pattern INPUT_PATTERN = Pattern.compile("-[0-9]+\\.[0-9]+-");
        Matcher matcher = INPUT_PATTERN.matcher(filename);
        while ( matcher.find() ) {
            utime = matcher.group();
        }
        //System.out.print(" >> timestamp string : " + utime ); // DEBUG line
        //System.out.println(" >> length = " + utime.length() ); // DEBUG line
         utime = utime.substring(1,utime.length()-1); // remove hyphens from ends of INPUT_PATTERN 
        double unixTime = Double.parseDouble(utime);
        return unixTime;
        
    }
    
}