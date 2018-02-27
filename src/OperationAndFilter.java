package diva_assign1;
import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import java.awt.Color;
import java.awt.Graphics;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.awt.image.WritableRaster;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Arrays;

/**
 *
 * @author amrusha
 */
public class DIVA_assign1 {
private static BufferedImage rotateImage(BufferedImage pic1) throws IOException {
    int width = pic1.getWidth(null);
    int height = pic1.getHeight(null);

    double angle = Math.toRadians(180);
    double sin = Math.sin(angle);
    double cos = Math.cos(angle);
    double x0 = 0.5 * (width - 1);     // point to rotate about
    double y0 = 0.5 * (height - 1);     // center of image

    WritableRaster inRaster = pic1.getRaster();
    BufferedImage pic2 = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    WritableRaster outRaster = pic2.getRaster();
    int[] pixel = new int[3];

    // rotation
    for (int x = 0; x < width; x++) {
        for (int y = 0; y < height; y++) {
            double a = x - x0;
            double b = y - y0;
            int xx = (int) (+a * cos - b * sin + x0);
            int yy = (int) (+a * sin + b * cos + y0);

            if (xx >= 0 && xx < width && yy >= 0 && yy < height) {
                outRaster.setPixel(x, y, inRaster.getPixel(xx, yy, pixel));
            }
        }
    }
    return pic2;
}
public static BufferedImage getNegativeImage(BufferedImage img) {
        int w1 = img.getWidth();
        int h1 = img.getHeight();
        BufferedImage gray = new BufferedImage(w1, h1, 1);
        int value, alpha, r, g, b;
        for (int i = 0; i < w1; i++) {
            for (int j = 0; j < h1; j++) {
                value = img.getRGB(i, j); // store value
                alpha = getAlpha(value);
                r = 255 - getRed(value);
                g = 255 - getGreen(value);
                b = 255 - getBlue(value);

                value = createRGB(alpha, r, g, b);
                gray.setRGB(i, j, value);
            }
        }
        return gray;
    }

    public static int createRGB(int alpha, int r, int g, int b) {
        int rgb = (alpha << 24) + (r << 16) + (g << 8) + b;
        return rgb;
    }

    public static int getAlpha(int rgb) {
        return (rgb >> 24) & 0xFF;
    }

    public static int getRed(int rgb) {
        return (rgb >> 16) & 0xFF;
    }

    public static int getGreen(int rgb) {
        return (rgb >> 8) & 0xFF;
    }

    public static int getBlue(int rgb) {
        return rgb & 0xFF;
    }
 
 
public static BufferedImage zoomImage(BufferedImage img) {
     
    BufferedImage outImg = null;
     
    int[] rgbOutput = null;
    Graphics g = null;
    int width = 0,height = 0;
     
    try {
         
        width  =  img.getWidth();
        height = img.getHeight();
         
        outImg = new BufferedImage(width<<1, height<<1, 1);
         
        int rgbInput[]=new int[width*height];
        rgbOutput = new int[(width<<1)*(height<<1)];
        
        int i,j,k,l,value;
        k=0;
        for (i = 0; i < width; i++) {
            for (j = 0; j < height; j++) {
                value = img.getRGB(i, j); 
                rgbInput[k++] = value;
            }
        }
        
        k=0;
        for(i=0;i<(height<<1);i+=2) {
            for(j=0;j<(width<<1);j+=2) {
                rgbOutput[i*(width<<1) + j] = rgbInput[k] ;
                rgbOutput[(i+1)*(width<<1) + j]  = rgbInput[k];
                rgbOutput[i*(width<<1) + j+1]  = rgbInput[k];
                rgbOutput[(i+1)*(width<<1) + j+1]  = rgbInput[k];
                k++;
            }
        }
         
        g = outImg.getGraphics();
    } catch(Exception e){}
         int k=0;
    int value,alpha, r, g1, b;
     for (int i = 0; i < (width<<1); i++) {
            for (int j = 0; j < (height<<1); j++) {
                value = rgbOutput[k++];
                alpha = getAlpha(value);
                r = getRed(value);
                g1 = getGreen(value);
                b = getBlue(value);

                value = createRGB(alpha, r, g1, b);
                outImg.setRGB(i, j, value);
            }
        }
        return outImg;
}

public static BufferedImage shrinkImage(BufferedImage img) {
        BufferedImage gray = new BufferedImage(img.getWidth(),img.getHeight(), 1);
         for (int x = 0; x < img.getWidth(); x++) {
            for (int y = 0; y < img.getHeight(); y++) {
                gray.setRGB( x/2, y/2, img.getRGB(x,y) );
            }
        }
         return gray;
}
public static BufferedImage translateImage(BufferedImage img) {
        int x1 = 50;
        int y1 = 50;
        BufferedImage gray = new BufferedImage(img.getWidth()+60,img.getHeight()+60, 1);
         for (int x = 0; x < img.getWidth(); x++) {
            for (int y = 0; y < img.getHeight(); y++) {
                gray.setRGB( x + x1, y + y1, img.getRGB(x,y) );
            }
        }
         return gray;
}

public static BufferedImage MedianFilter(BufferedImage img) throws IOException {                               //Input Photo File
        Color[] pixel=new Color[9];
        int[] R=new int[9];
        int[] B=new int[9];
        int[] G=new int[9];
        for(int i=1;i<img.getWidth()-1;i++)
            for(int j=1;j<img.getHeight()-1;j++)
            {
               pixel[0]=new Color(img.getRGB(i-1,j-1));
               pixel[1]=new Color(img.getRGB(i-1,j));
               pixel[2]=new Color(img.getRGB(i-1,j+1));
               pixel[3]=new Color(img.getRGB(i,j+1));
               pixel[4]=new Color(img.getRGB(i+1,j+1));
               pixel[5]=new Color(img.getRGB(i+1,j));
               pixel[6]=new Color(img.getRGB(i+1,j-1));
               pixel[7]=new Color(img.getRGB(i,j-1));
               pixel[8]=new Color(img.getRGB(i,j));
               for(int k=0;k<9;k++){
                   R[k]=pixel[k].getRed();
                   B[k]=pixel[k].getBlue();
                   G[k]=pixel[k].getGreen();
               }
               Arrays.sort(R);
               Arrays.sort(G);
               Arrays.sort(B);
               img.setRGB(i,j,new Color(R[4],B[4],G[4]).getRGB());
            }
        return img;
}
public static BufferedImage highboostFilter(BufferedImage img) throws IOException {   

    float[]data={ 0,-1/4,0,-1/4,2,-1/4,0,-1/4,0};
    Kernel kernel = new Kernel(3, 3,data);
    BufferedImageOp ConOp = new ConvolveOp(kernel);
    img = ConOp.filter(img, null);
    return img;
}

public static BufferedImage lowpassFilter(BufferedImage img) throws IOException { 
    float val = (float)1/9;
    float[]data={val,val,val,val,val,val,val,val,val};
    Kernel kernel = new Kernel(3, 3,data);
    BufferedImageOp ConOp = new ConvolveOp(kernel);
    img = ConOp.filter(img, null);
    return img;
}
public static BufferedImage highpassFilter(BufferedImage img) throws IOException {   
        float val = (float)(-1);
        float val2 = (float)2;
    float[]data={-1,-1,-1,-1,8,-1,-1,-1,-1};
    Kernel kernel = new Kernel(3, 3,data);
    BufferedImageOp ConOp = new ConvolveOp(kernel);
    img = ConOp.filter(img, null);
    return img;
}
public static String readTif() throws FileNotFoundException, IOException
{
            FileInputStream in = new FileInputStream("/home/amrusha/DIVA/lena_gray_256.tif");
            FileChannel channel = in.getChannel();
            ByteBuffer buffer = ByteBuffer.allocate((int)channel.size());
            channel.read(buffer);
            String tiffEncodedImg = Base64.encode(buffer.array()); 
            return tiffEncodedImg;
}
public static BufferedImage readImage() throws FileNotFoundException, IOException
{
    BufferedImage image = ImageIO.read(new File("/home/amrusha/DIVA/lena_gray.bmp"));
    return image;
}
public static void writeImage(BufferedImage op_image,String filename) throws FileNotFoundException, IOException
{
    ImageIO.write(op_image, "bmp", new File(filename));
}
public static void main(String[] args) {
        

       BufferedImage op_image = null,op_image2 = null;
        try {
            
            BufferedImage image = readImage();
            
            //ROTATION
            op_image = rotateImage(image);
            writeImage(op_image,"/home/amrusha/DIVA/Rotated.bmp");
            
            //NEGATIVE
            op_image = getNegativeImage(image);
            ImageIO.write(op_image, "bmp", new File("/home/amrusha/DIVA/negative.bmp"));
            
            //ZOOMING
            op_image = zoomImage(image);
            ImageIO.write(op_image, "bmp", new File("/home/amrusha/DIVA/zoom.bmp"));
            
            //SHRINKNG
            op_image = shrinkImage(image);
            ImageIO.write(op_image, "bmp", new File("/home/amrusha/DIVA/shrink.bmp"));
            
            //TRANSLATE
            op_image = translateImage(image);
            ImageIO.write(op_image, "bmp", new File("/home/amrusha/DIVA/translate.bmp"));
            
            //MEDIAN FILTER
            op_image = MedianFilter(image);
            ImageIO.write(op_image, "bmp", new File("/home/amrusha/DIVA/medianFilter.bmp"));
            
            //HIGHBOOST FILTER
            op_image = highboostFilter(image);
            ImageIO.write(op_image, "bmp", new File("/home/amrusha/DIVA/highboostFilter.bmp"));
            
            //LOWPASS FILTER
            op_image = lowpassFilter(image);
            ImageIO.write(op_image, "bmp", new File("/home/amrusha/DIVA/lowpassFilter.bmp"));
            
            //HIGHPASS FILTER
            op_image = highpassFilter(image);
            ImageIO.write(op_image, "bmp", new File("/home/amrusha/DIVA/highpassFilter.bmp"));
            
        } catch (IOException e) {
              e.printStackTrace();
        }
    }
}

