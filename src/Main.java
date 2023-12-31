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
import javax.sound.sampled.LineUnavailableException;
import javax.swing.UIManager;
import com.formdev.flatlaf.extras.FlatDesktop;


public class Main {
    public static void main(String[] args) throws LineUnavailableException{
        FlatDesktop.setAboutHandler( () -> About.getInstance());

        try{
            UIManager.setLookAndFeel("com.formdev.flatlaf.themes.FlatMacLightLaf");
        } catch (Exception e) {e.printStackTrace();}
        String path = System.getProperty("user.home");
        RecorderWindow ibo = new RecorderWindow(path);
    }
}