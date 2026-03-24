package org.hiram;

import javax.swing.*;

import com.formdev.flatlaf.FlatDarculaLaf;
import org.hiram.forms.Borrower;
import org.hiram.forms.Example;
import org.hiram.forms.Management;


/**
 * Hello world!
 *
 */
public class Main
{
    public static void main( String[] args ) {
        FlatDarculaLaf.setup();
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                Management manager = new Management();
                manager.setVisible(true);
            }
        });
    }
}
