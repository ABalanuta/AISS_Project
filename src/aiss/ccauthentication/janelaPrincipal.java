package aiss.ccauthentication;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * janelaPrincipal.java
 *
 * Created on 1/Mar/2010, 22:11:35
 */

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.PixelGrabber;
import java.io.*;
import pteidlib.*;
import javax.swing.ImageIcon;
/**
 *
 * @author carlos
 */
public class janelaPrincipal extends javax.swing.JFrame {

    /** Creates new form janelaPrincipal */
    public janelaPrincipal() {
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

        jPanel1 = new javax.swing.JPanel();
        jTextField1 = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jTextField2 = new javax.swing.JTextField();
        jTextField3 = new javax.swing.JTextField();
        jTextField4 = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jTextField1.setEditable(false);

        jButton1.setText("Carregar dados...");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jTextField2.setEditable(false);

        jTextField3.setEditable(false);

        jTextField4.setEditable(false);

        jLabel1.setText("Nome");

        jLabel2.setText("Apelido");

        jLabel3.setText("Sexo");

        jLabel4.setText("Data Nasc.");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jLabel1))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jLabel2))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jLabel3))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jLabel4)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jTextField1, javax.swing.GroupLayout.DEFAULT_SIZE, 140, Short.MAX_VALUE)
                            .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextField2)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(54, 54, 54)
                        .addComponent(jButton1)))
                .addContainerGap(22, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(31, 31, 31)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 18, Short.MAX_VALUE)
                .addComponent(jButton1)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(20, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(22, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
       
            // TODO add your handling code here:
            mostraNome();
        
    }//GEN-LAST:event_jButton1ActionPerformed

      static
  {

  //    System.out.println(System.getProperty("java.library.path"));

    try
    {
        System.loadLibrary("pteidlibj");
    }
    catch (UnsatisfiedLinkError e)
    {
      System.err.println("Native code library failed to load.\n" + e);
      System.exit(1);
    }
  }

  public void PrintIDData(PTEID_ID idData)
  {
    System.out.println("DeliveryEntity : " + idData.deliveryEntity);
    System.out.println("PAN : " + idData.cardNumberPAN);
    System.out.println("...");
  }

  public void PrintAddressData(PTEID_ADDR adData)
  {
	if("N".equals(adData.addrType))
    {
        System.out.println("Type : National");
	System.out.println("Street : " + adData.street);
        System.out.println("Municipality : " + adData.municipality);
        System.out.println("...");
    }
    else
    {
        System.out.println("Type : International");
	System.out.println("Address : " + adData.addressF);
        System.out.println("City : " + adData.cityF);
        System.out.println("...");
    }
  }


    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {


        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                                    
                   janelaPrincipal janela = new janelaPrincipal();
    //                System.out.print(id.firstname);
                   // janela.jTextField1.setText(pteid.GetID().firstname);
                   janela.setVisible(true);

            }
        });

        
       


    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextField jTextField4;
    // End of variables declaration//GEN-END:variables


public void mostraNome(){

      try
    {
      // test.TestCVC();

      pteid.Init("");

	  //test.TestChangeAddress();

		// Don't check the integrity of the ID, address and photo (!)
      pteid.SetSODChecking(false);

	  int cardtype = pteid.GetCardType();
	  switch (cardtype)
	  {
		  case pteid.CARD_TYPE_IAS07:
			  System.out.println("IAS 0.7 card\n");
			  break;
		  case pteid.CARD_TYPE_IAS101:
			  System.out.println("IAS 1.0.1 card\n");
			  break;
		  case pteid.CARD_TYPE_ERR:
			  System.out.println("Unable to get the card type\n");
			  break;
		  default:
			  System.out.println("Unknown card type\n");
	  }  

      // Read ID Data
      PTEID_ID idData = pteid.GetID();
 //     if (null != idData)
   //   {
     //   PrintIDData(idData);
     // }

	  // Read Address
  //    PTEID_ADDR adData = pteid.GetAddr();
   //   if (null != adData)
    //  {
     //   PrintAddressData(adData);
     // }

	  // Read Picture Data
  
  
   // this.setVisible(true);

       // PIN operations
 //      int triesLeft = pteid.VerifyPIN((byte)0x83, null);
   //    triesLeft = pteid.ChangePIN((byte)0x83, null, null);

       // Read Certificates
       PTEID_Certif[] certs = pteid.GetCertificates();
       System.out.println("Number of certs found: " + certs.length);
       for(int i=0; i<certs.length; i++)
        System.out.println(certs[i].certifLabel);


       // Read Pins
   //    PTEID_Pin[] pins = pteid.GetPINs();

       // Read TokenInfo
     //  PTEID_TokenInfo token = pteid.GetTokenInfo();

       // Read personal Data
      // byte[] filein = {0x3F, 0x00, 0x5F, 0x00, (byte)0xEF, 0x07};
      // byte[] file = pteid.ReadFile(filein, (byte)0x81);

       // Write personal data
    //   String data = "Hallo JNI";
    //   pteid.WriteFile(filein, data.getBytes(), (byte)0x81);

           jTextField1.setText(idData.firstname);
           jTextField2.setText(idData.name);
           jTextField3.setText(idData.sex);
           jTextField4.setText(idData.birthDate);

       pteid.Exit(pteid.PTEID_EXIT_LEAVE_CARD);
    }
	catch (PteidException ex)
	{
		ex.printStackTrace();
		//System.out.println(ex.getMessage());
	}
	catch (Exception ex)
	{
		ex.printStackTrace();
		//System.out.println(ex.getMessage());
	}

}

}