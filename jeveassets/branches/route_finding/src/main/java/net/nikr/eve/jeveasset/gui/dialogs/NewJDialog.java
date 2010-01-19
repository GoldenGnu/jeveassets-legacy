/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * NewJDialog.java
 *
 * Created on 05-Dec-2009, 20:37:03
 */

package net.nikr.eve.jeveasset.gui.dialogs;

/**
 *
 * @author Andrew
 */
public class NewJDialog extends javax.swing.JDialog {

    /** Creates new form NewJDialog */
    public NewJDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    algorithm = new javax.swing.JComboBox();
    jScrollPane1 = new javax.swing.JScrollPane();
    descriSP = new javax.swing.JTextArea();
    jScrollPane2 = new javax.swing.JScrollPane();
    availSP = new javax.swing.JList();
    add = new javax.swing.JButton();
    remove = new javax.swing.JButton();
    jScrollPane3 = new javax.swing.JScrollPane();
    waypoSP = new javax.swing.JList();
    addRandom = new javax.swing.JButton();
    waypointsRemaining = new javax.swing.JLabel();
    calculate = new javax.swing.JButton();
    progress = new javax.swing.JLabel();
    availableRemaining = new javax.swing.JLabel();

    setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

    algorithm.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

    descriSP.setColumns(20);
    descriSP.setRows(5);
    jScrollPane1.setViewportView(descriSP);

    availSP.setModel(new javax.swing.AbstractListModel() {
      String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
      public int getSize() { return strings.length; }
      public Object getElementAt(int i) { return strings[i]; }
    });
    jScrollPane2.setViewportView(availSP);

    add.setText("jButton1");

    remove.setText("jButton2");

    waypoSP.setModel(new javax.swing.AbstractListModel() {
      String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
      public int getSize() { return strings.length; }
      public Object getElementAt(int i) { return strings[i]; }
    });
    jScrollPane3.setViewportView(waypoSP);

    addRandom.setText("jButton3");

    waypointsRemaining.setText("jLabel1");

    calculate.setText("jButton4");

    progress.setText("jLabel2");

    availableRemaining.setText("avail");

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
    getContentPane().setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addGroup(layout.createSequentialGroup()
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
              .addComponent(availableRemaining, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
              .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 144, Short.MAX_VALUE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
              .addComponent(remove)
              .addComponent(add)
              .addComponent(addRandom))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
              .addComponent(calculate, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 151, Short.MAX_VALUE)
              .addComponent(waypointsRemaining, javax.swing.GroupLayout.DEFAULT_SIZE, 151, Short.MAX_VALUE)
              .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 151, Short.MAX_VALUE)))
          .addComponent(progress, javax.swing.GroupLayout.DEFAULT_SIZE, 380, Short.MAX_VALUE)
          .addComponent(algorithm, javax.swing.GroupLayout.Alignment.TRAILING, 0, 380, Short.MAX_VALUE)
          .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 380, Short.MAX_VALUE))
        .addContainerGap())
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addGap(4, 4, 4)
        .addComponent(progress)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(algorithm, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
          .addComponent(jScrollPane3, 0, 0, Short.MAX_VALUE)
          .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 94, Short.MAX_VALUE)
          .addGroup(layout.createSequentialGroup()
            .addComponent(add)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(remove)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(addRandom)))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(waypointsRemaining)
          .addComponent(availableRemaining))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(calculate)
        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );

    pack();
  }// </editor-fold>//GEN-END:initComponents

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                NewJDialog dialog = new NewJDialog(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JButton add;
  private javax.swing.JButton addRandom;
  private javax.swing.JComboBox algorithm;
  private javax.swing.JList availSP;
  private javax.swing.JLabel availableRemaining;
  private javax.swing.JButton calculate;
  private javax.swing.JTextArea descriSP;
  private javax.swing.JScrollPane jScrollPane1;
  private javax.swing.JScrollPane jScrollPane2;
  private javax.swing.JScrollPane jScrollPane3;
  private javax.swing.JLabel progress;
  private javax.swing.JButton remove;
  private javax.swing.JList waypoSP;
  private javax.swing.JLabel waypointsRemaining;
  // End of variables declaration//GEN-END:variables

}