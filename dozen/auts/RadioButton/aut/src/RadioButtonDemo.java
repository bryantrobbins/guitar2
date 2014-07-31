/*
 * Copyright (c) 2009-@year@. The GUITAR group at the University of Maryland.
 * Names of owners of this group may be obtained by sending an e-mail to
 * atif@cs.umd.edu. Permission is hereby granted, free of charge, to any
 * person obtaining a copy of this software and associated  documentation files
 * (the "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to permit
 * persons to whom the Software is furnished to do so, subject to the
 * following conditions:
 * 
 *   The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 * IN THE SOFTWARE.
 */
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

/**
 * RadioButtonDemo a simple application used for teaching student how to write
 * a Java Swing GUI application.
 * 
 * <p>
 * 
 * @author <a href="mailto:baonn@cs.umd.edu"> Bao N. Nguyen </a>
 * 
 */
public class RadioButtonDemo extends JFrame {
   private static final long serialVersionUID = 1L;

   // -------------------------
   // GUI widgets
   // -------------------------
   JButton      w0;    // Exit
   JRadioButton w1;    // Circle
   JRadioButton w2;    // Square

   JButton      w3;    // Create
   JPanel       w4;    // Rendered shape
   JButton      w5;    // Reset

   JCheckBox    w6;    // Log exit time

   // -------------------------
   // Container panel
   // -------------------------
   JPanel contentPane;

   // -------------------------
   // Program state
   // -------------------------
   int          exitCircle = 0, exitSquare = 0;
   Shape        currentShape;
   Boolean      created = false;

   enum Shape {
      CIRCLE, SQUARE
   }

   /**
    * Constructor to setup the initial state
    */
   public RadioButtonDemo() {
      super("Radio Button Demo");
      contentPane = new JPanel(new BorderLayout());

      w1 = new JRadioButton("Circle");
      w1.setSelected(false);
      w1.addActionListener(new W1Listener());

      w2 = new JRadioButton("Square");
      w2.setSelected(false);
      w2.addActionListener(new W2Listener());

      ButtonGroup selectShapeGroup = new ButtonGroup();
      selectShapeGroup.add(w1);
      selectShapeGroup.add(w2);

      Box selectShapePanel = new Box(BoxLayout.Y_AXIS);
      selectShapePanel.add(w1);
      selectShapePanel.add(w2);

      selectShapePanel.setBorder(
            BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.GRAY),
                                           "Select"));

      draw(new EmptyPanel());

      w3 = new JButton("Create");
      w3.addActionListener(new W3Listener());

      w5 = new JButton("Reset");
      w5.setEnabled(false);
      w5.addActionListener(new W5Listener());

      w0 = new JButton("Exit");
      w0.addActionListener(new W0Listener());

      JPanel buttonPanel = new JPanel();
      buttonPanel.add(w3);
      buttonPanel.add(w5);
      buttonPanel.add(w0);

      contentPane.add(selectShapePanel,
                      BorderLayout.WEST);
      contentPane.add(w4, BorderLayout.CENTER);
      contentPane.add(buttonPanel, BorderLayout.SOUTH);

      w6 = new JCheckBox("Log exit time.");

      setContentPane(contentPane);
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
   }

   /**
    * Draw a shape in the `Rendered Shape` area
    * 
    * @param shape The shape to draw
    */
   private void draw(JPanel shape) {
      if (w4 != null) {
         contentPane.remove(w4);
      }
      w4 = shape;
      w4.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.GRAY),
            "Rendered Shape"));
      contentPane.add(w4);
      repaint();
      pack();
   }

   // -----------------------
   // Listeners
   // -----------------------
   /**
    * Circle listener
    */
   class W1Listener implements ActionListener {
      @Override
      public void actionPerformed(ActionEvent arg0) {
         currentShape = Shape.CIRCLE;
         if (created) {
            draw(new CirclePanel());
         }
      }
   }

   /**
    * Square radio button listener
    */
   class W2Listener implements ActionListener {

      @Override
      public void actionPerformed(ActionEvent arg0) {
         currentShape = Shape.SQUARE;
         if (created) {
            draw(new SquarePanel());
         }
      }
   }

   /**
    * Create button listener
    */
   class W3Listener implements ActionListener {
      @Override
      public void actionPerformed(ActionEvent e) {
         JPanel shape;
         if (currentShape == Shape.CIRCLE ||
             currentShape == Shape.SQUARE) {
            w5.setEnabled(true);
         }
         created = true;
         if (currentShape == Shape.CIRCLE)
            shape = new CirclePanel();
         else if (currentShape == Shape.SQUARE)
            shape = new SquarePanel();
         else
            shape = new EmptyPanel();
         draw(shape);
      }
   }

   /**
    * Reset button listener
    */
   class W5Listener implements ActionListener {
      @Override
      public void actionPerformed(ActionEvent e) {
         w5.setEnabled(false);
         created = false;
         draw(new EmptyPanel());
      }
   }

   /**
    * Exit button listener
    */
   class W0Listener implements ActionListener {
      @Override
      public void actionPerformed(ActionEvent e) {

         String message = "Are you sure?";

         if (created == true &&
             currentShape == Shape.CIRCLE) {
            exitCircle++;
         }
         if (created == true &&
             currentShape == Shape.SQUARE) {
            exitSquare++;
         }

         Object[] params = { message, w6 };
 
         // Exit w/o confirmation if rendered 10 times
         if (exitCircle == 10 || exitSquare == 10) {
            System.exit(0);
         }
         // Otherwise ask for confirmation
         int exit =
            JOptionPane.showConfirmDialog(null, params,
            "Exit Confirmation",
            JOptionPane.YES_NO_OPTION);
         if (exit == 0) {
            if (w6.isSelected()) {
               writeTimeStamp();
            }
            System.exit(0);
         }
      }
   }

   /**
    * Dummy method mimic writing a log file
    */
   private void writeTimeStamp() {
      // Nothing in the body
   }

   // -----------------------
   // Create and draw panels
   // -----------------------
   /**
    * Circle panel
    */
   class CirclePanel extends JPanel {
      private static final long serialVersionUID = 1L;

      public CirclePanel() {
         super();
      }

      @Override
      public void paintComponent(Graphics g) {
         g.drawOval(60, 30, 45, 45);
      }
   }

   /**
    * Square panel
    */
   class SquarePanel extends JPanel {
      private static final long serialVersionUID = 1L;

      public SquarePanel() {
         super();
      }

      @Override
      public void paintComponent(Graphics g) {
         g.drawRect(60, 30, 45, 45);
      }
   }

   /**
    * Empty panel
    */
   class EmptyPanel extends JPanel {
      private static final long serialVersionUID = 1L;

      public EmptyPanel() {
         super();
      }
   }

   /**
    * Create the GUI and show it. For thread safety, this method should be
    * invoked from the event-dispatching thread.
    */
   private static void createAndShowGUI() {
      RadioButtonDemo frame = new RadioButtonDemo();
      frame.setVisible(true);
      frame.pack();
   }

   public static void main(String[] args) {
      javax.swing.SwingUtilities.invokeLater(
         new Runnable() {
         public void run() {
            createAndShowGUI();
         }
      });
   }
}
