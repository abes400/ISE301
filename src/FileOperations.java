/*
 * Lunchpad - A Launchpad/Soundboard application.
 * This file is part of Lunchpad.
 *
 * Lunchpad is free software: you can redistribute it and/or modify it under the terms of the GNU General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 *
 * Lunchpad is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Lunchpad. If not, see
 * <https://www.gnu.org/licenses/>.
 * */

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.JFileChooser;
import org.apache.commons.io.FileUtils;

/**
 * The class that reduces complex code snippets for selecting a file or directory using JFileChooser to a few
 * lines of code. It is created in order to reduce the complexity that may be caused by the long
 * and repeating code snippet that could be used for selecting a file or a directory using JFileChooser.
 * Simply call <pre>{@code loadFileTo(String targetDir, int mode)}</pre> and the selected file or directory is
 * copied to the desired path. <pre></pre>
 * In order to make sure that the program doesn't try to process a null file or directory
 * as the user clicks Cancel, don't forget to check if the attribute <strong>filename</strong> is null.
 * Use the copied file or directory in your code only if <strong>filename is not null</strong>.
 * <pre>
 *     <strong>Ex:</strong>
 *     {@code if(FileOperations.filename != null){
 *         //Process the file here.
 *     }}
 * </pre>
 * @author İ. K. Bilir (Abes400)
 * @since 1.0
 */
public class FileOperations {
    public static String filename; // Holds the filename selected by user
    public static int file = JFileChooser.FILES_ONLY, dir = JFileChooser.DIRECTORIES_ONLY;
//file : 0, dir : 1

    /**
     * Initiates a JFileChooser object window, copies the selected file or directory to the path passed as parameter and
     * stores its name in the attribute <strong>String filename</strong>
     * <pre></pre>
     * In order to make sure that the program doesn't try to process a null file or directory after the user clicks
     * Cancel, don't forget to check if the attribute <strong>filename</strong> is null. Use the copied file or directory
     * in your code only if <strong>filename is not null</strong>.
     * <pre>
     *     <strong>Ex:</strong>
     *     {@code if(FileOperations.filename != null){
     *         //Process the file here.
     *     }}
     * </pre>
     * @param targetDir The directory where the selected file or directory will be copied.
     * @param mode Decides whether only directories or only files can be selected.
     * @throws java.io.IOException
     * @since 1.0
     */
    public static void loadFileTo(String targetDir, int mode) throws java.io.IOException {
        filename = null; // first set filename to null. Since this attribute is static, the value will not be deleted
                         // after the method returns. This can cause some issues since if this method is called again
                         // and the cancel button is clicked.
            JFileChooser fc = new JFileChooser(); //Initiate JFileChooser
            if(mode == JFileChooser.FILES_ONLY){
                fc.setDialogTitle("Choose a file");
                fc.setFileSelectionMode(JFileChooser.FILES_ONLY); // Only files are allowed since the mode is FILES_ONLY
                // Restricting selected files to .wav
                fc.setFileFilter(new FileNameExtensionFilter("Wave files", "wav"));
                if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                    Path sourcePath = Paths.get(fc.getSelectedFile().getPath()), // Get the path to the item selected
                         // Concatenates selected filename to target dir. Gets the path where the item will be copied to.
                         targetPath = Paths.get(targetDir + fc.getSelectedFile().getName());
                        Files.copy(sourcePath, targetPath);
                    filename = fc.getSelectedFile().getName(); // Now filename is set to the name of the selected item
                }
            } else if (mode == JFileChooser.DIRECTORIES_ONLY) {
                fc.setDialogTitle("Choose a folder");
                // Only directories are allowed since the mode is FILES_ONLY
                fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                
                if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                    // Creates directory for targetDir parameter.
                    Files.createDirectory(Paths.get(targetDir + fc.getSelectedFile().getName()));
                    File sourcePath = new File(fc.getSelectedFile().getPath()),
                            targetPath = new File(targetDir + fc.getSelectedFile().getName());
                    FileUtils.copyDirectory(sourcePath, targetPath); // Directory is coppied
                    filename = fc.getSelectedFile().getName(); // Now filename is set to the name of the selected item
                }
            }
            fc = null;
    }

    /**
     * Searches for the item that has the given filename in the given path. If there is any file with the same name,
     * it concatenates a number after the filename.
     * The suffix (usually file extension) should be passed as a separate parameter so that the method can know where
     * to add the unique number. For example, when the extension of the file is passed separately, the method will
     * understand that the number should be added between filename and the extension.
     * <pre>{@code FileOperations.createUniquePathName(my_path, "hello", "")} =>  a file called "hello" exists => "hello_2"
     *  {@code FileOperations.createUniquePathName(my_path, "hello", ".java") => a file called "hello.java" exists => "hello_2.java"}
     * </pre>
     * @param path Where the method should check
     * @param filename What the searched item name will be
     * @param optionalExtension What the extension or suffix of the searched and generated file (or folder) is
     * @return String that contains the combination of the filename and a number that makes the given file name unique.
     */
    public static String createUniquePathName(String path, String filename, String optionalExtension){
        String newName = filename;
        int i = 2;
        while(Files.exists(Paths.get(path + "/" + newName + optionalExtension))) {
            newName = filename + '_' + i++;
        }
        return newName;
    }
}
