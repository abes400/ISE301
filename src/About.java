/*
 * ISE301 - An experimental application.
 * This file is part of ISE301.
 *
 * ISE301 is free software: you can redistribute it and/or modify it under the terms of the GNU General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 *
 * ISE301 is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with ISE301. If not, see
 * <https://www.gnu.org/licenses/>.
 * */
import javax.swing.JDialog;
import javax.swing.JLabel;
import java.awt.Font;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.ResourceBundle;

// This is a singleton class, which means you can create only one instance of it.

public class About {
    private static About obj; // The variable to hold our one and only object.
    private final JDialog dialog;
    private final static ResourceBundle bundle = ResourceBundle.getBundle("AboutStrings");
    private About(){ // Constructor implemented privately only to be called in the class definition.

        // Initializing the dialog box for the GUI
        dialog = new JDialog();
        //Fixed size
        dialog.setResizable(false);
        dialog.setSize(new Dimension(270, 230));
        dialog.setLayout(new FlowLayout());

        // Blending the title bar with window (looks cool)
        dialog.getRootPane().putClientProperty( "apple.awt.transparentTitleBar", true );

        // From now on, it's just initializing labels and puutting them on the dialog.
        JLabel name = new JLabel(bundle.getString("ABT_TITLE"));
        name.setFont(name.getFont().deriveFont(Font.BOLD));
        dialog.add(name);

        name = new JLabel(bundle.getString("ABT_DSC"));
        name.setFont(name.getFont().deriveFont(Font.PLAIN, 10));
        dialog.add(name);

        dialog.add(new JLabel("                                                     "));
        name = new JLabel(bundle.getString("ABT_DSC0"));
        name.setFont(name.getFont().deriveFont(Font.PLAIN, 10));
        dialog.add(name);
        dialog.add(new JLabel("                                                     "));

        for(int i = 1; i <= 4; i++){ // Iterated in order not to repeating the same code.
            name = new JLabel(bundle.getString("ABT_DSC" + i));
            name.setFont(name.getFont().deriveFont(Font.PLAIN, 10));
            dialog.add(name);
        }
    }

    public static About getInstance() { // This is how you can access the instance of our singleton. By passing the reference to it.
        if(obj == null){ // If not initiated, initiate one
            obj = new About();
        }
        WindowActions.centerWindow(obj.dialog);
        obj.dialog.setVisible(true);
        return obj;
    }
}
