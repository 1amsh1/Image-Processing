/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package histogrammatching;
import java.awt.EventQueue;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
  import java.io.IOException;
  import java.util.logging.Level;
  import java.util.logging.Logger;
  import javax.imageio.ImageIO;
/**
 *
 * @author amrusha
 */
public class HistogramMatching {
      
      private BufferedImage input;
      
      public HistogramMatching()
      {
          final int LEVEL = 256;
          int [] lred = new int[LEVEL];
          int [] lblue = new int[LEVEL];
          int [] lgreen = new int[LEVEL];
          double [] pr = new double[LEVEL];
          double [] pg = new double[LEVEL];
          double [] pb = new double[LEVEL];
          double [] sr = new double[LEVEL];
          double [] sg = new double[LEVEL];
          double [] sb = new double[LEVEL];
          try {
              //read and load the image
              input = ImageIO.read(new File("/home/amrusha/DIVA/lena_gray.bmp"));
          } catch (IOException ex) {
              Logger.getLogger(HistogramMatching.class.getName()).log(Level.SEVERE, null, ex);
          }
          //build an image with the same dimension of the file read
          //also the same spectrum of colours
          BufferedImage im = new BufferedImage(input.getWidth(), input.getHeight(),
                  BufferedImage.TYPE_INT_ARGB);
          //object created to draw into the bufferedImage
         Graphics2D g2d = im.createGraphics();
          //draw input into im
          g2d.drawImage(input, 0, 0, null);
          //the first step to do the matching histogram is equilized the image
          //by histogram equalization , so now we are getting the s mapping
          //based on the theory, we are obtaining the nk samples
          for(int h=0; h<input.getHeight(); h++)
          {
              for(int w=0; w<input.getWidth(); w++)
              {
                  int pix=0;
                  int alpha = 0xff &(im.getRGB(w, h)>>24);
                  int red = 0xff &(im.getRGB(w, h)>>16);
                  int green = 0xff&(im.getRGB(w, h)>>8);
                 int blue = 0xff&(im.getRGB(w, h));
                  //collecting the sample of each colour
                  lred[red]++;
                  lgreen[green]++;
                  lblue[blue]++;
              }//w
          }//h
          //getting the PDF of the normilized histogram
          //pr(rk)
          for(int h=0; h<LEVEL; h++)
          {
              pr[h] = (double)lred[h]/((double)input.getHeight()*(double)input.getWidth());
              pg[h] = (double)lgreen[h]/((double)input.getHeight()*(double)input.getWidth());
              pb[h] = (double)lblue[h]/((double)input.getHeight()*(double)input.getWidth());
          }//h
          //now we need to map to s domain
          for(int h=0; h<LEVEL; h++)
          {
              //mapping to the red color
              sr[h] = 0;
              //accumulating
              for(int j=0; j<h; j++)
              {
                  sr[h] = sr[h] + pr[j];
              }//j
              sr[h] = (LEVEL-1)*(sr[h]);
              //green
              sg[h]=0;
              for(int j=0; j<h; j++)
              {
                  sg[h]=sg[h]+pg[j];
              }
              sg[h] = (LEVEL-1)*(sg[h]);
              //blue
              sb[h]=0;
              for(int j=0; j<h; j++)
              {
                  sb[h] = sb[h]+pb[j];
              }
              sb[h] = (LEVEL-1)*(sb[h]);     
         }//h
         
         //digital levels round the values
         for(int h=0; h<LEVEL; h++)
         {
             sr[h] = Math.round(sr[h]);
             sg[h] = Math.round(sg[h]);
             sb[h] = Math.round(sb[h]);
         }         //now starting the matching logic to match the histogram
         //first we need to specify pz(zq)
         double [] pz = new double[LEVEL];
         double [] G = new double[LEVEL];
         
         for(int h=0; h<LEVEL; h++)
         {
          pz[h] = ramp(h);
         }
         //accumulating
         
         for(int z=0; z<LEVEL; z++)
         {
             //mapping to the red color
             G[z] = 0;
             //accumulating
             for(int j=0; j<z; j++)
             {
                 G[z] = G[z] + pz[j];
             }//j
             G[z] = Math.round((LEVEL-1)*(G[z]));
         }//h
        
         for(int k=0; k<LEVEL; k++)
         {
             //scanning Gz mapping to re-map
             double temp = 0.0;
             double [] small = new double[2];
             //small[0] = index and small[1] = value
             small[0] = 0; //index or z
             small[1] = 10; //value
             for(int z=0; z<LEVEL; z++)
             {

                 temp = Math.abs(sg[k] - G[z]);
                 if(temp<small[1])
                 {
                     small[1] = temp;
                     small[0] = z;
                 }//if 
             }//z
            sg[k] = small[0]; 
         }
         for(int h=0; h<input.getHeight(); h++)
                 {
                     for(int w=0; w<input.getWidth(); w++)
                     {
                         int pix = 0;
                         int alpha = 0xff & (im.getRGB(w, h)>>24);
                         int red =   0xff & (im.getRGB(w, h)>>16);
                         int green = 0xff & (im.getRGB(w, h)>>8);
                         int blue =  0xff & im.getRGB(w, h);

                         {
                             red = (int) sr[(int)red];
                         }
                         {
                             green = (int) sg[(int)green];
                         }
                         {
                             blue = (int) sb[(int)blue];
                         }
                         
                         pix = pix | blue;
                         pix = pix | (green <<8);
                         pix = pix | (red <<16);
                         pix = pix | (alpha <<24);
                         
                         im.setRGB( w, h, pix);
                         pix = 0;
                     } 
                 }
         try {
             ImageIO.write(im, "png", new File("/home/amrusha/DIVA/HistogramMatching.png"));
             //System.out.println("Matched");
         } catch (IOException ex) {
             Logger.getLogger(HistogramMatching.class.getName()).log(Level.SEVERE, null, ex);
         }
           
        
     }//constructor
     
     public double ramp(int x)
     {
         double a=-2.0/((255.0)*(255.0));
         double b=2.0/(255.0);       
        return ((double)x*a +b); 
    }
         public static void main(String[] args) {
            new HistogramMatching();
        }
    
    
}

