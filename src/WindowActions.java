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

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.Window;

public class WindowActions {
    /**
     This function will put ANY WINDOW INSTANCE, which is passed, to the center of the screen.
     @param candidateWindow the window instance you would like to center
     */
    public static void centerWindow(Window candidateWindow){
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension winSize = candidateWindow.getSize();
        Point center = new Point((screenSize.width - winSize.width) / 2, (screenSize.height - winSize.height) / 2);
        candidateWindow.setLocation(center);
    }

}
