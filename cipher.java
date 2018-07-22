// This code converts a colored jpg image into a black-and-white jpg image. 
// Usage: java Code <inputfile> <outputfile>


import java.io.*;
import java.lang.*;

import javax.imageio.ImageIO;

import sun.security.util.Length;

import java.io.File;
import java.io.*;
import java.awt.image.BufferedImage;

class ImageOperations{
	private int height;
	private int width;
	private String imageName;
	private byte I[][];
	private byte E[][];
	private byte chunk[][];
	private int len;
	
	//Constructor fixes the height and width of the image object
	public ImageOperations(String fileName){		
		imageName = fileName;
		
		BufferedImage BI = null;
		try{
			File sourceimage = new File(imageName);
			BI = ImageIO.read(sourceimage);
		}catch(IOException e){
			System.out.println("Error Reading the Image File:" + imageName);
		}
		height = BI.getHeight();
		width = BI.getWidth();
		
		//Now we initialize the grayscale image I.
		I = new byte[height][width];
		E = new byte[height][width];
		len=0;
		chunk=new byte[256][8];
		//First read the RGB pixels from the image.
		int pixels[][] = new int[height][width];
		for(int y = 0;y < height;y++){
			for(int x = 0;x < width;x++){
				int tempRGB = BI.getRGB(x, y);
				int R = (tempRGB >> 16) & 0xff;
				int G = (tempRGB >> 8) & 0xff;
				int B = (tempRGB) & 0xff;
				int gray = (int) ((0.11 * B) + (0.59 * G) + (0.3 * R));
				I[y][x] = (byte) gray;				
			}
		}	
	}

	public boolean searchForChunk(byte B[]){
        boolean is_new = true;
        for(int i=0;i<len;i++ ){
            int j=0;
            for(;j<8;j++){
                if(B[j] != chunk[i][j] ){
                    break;
                }
            }
            if(j == 8){
                is_new = false;
                break;
            }
        }
        return is_new;
    }

	public void chunk(byte N[])
	{
		for(int i=0;i<N.length;i+=8)
		{
			byte temp[]=new byte [8];
			for(int j=i;j<i+8;j++)
			{
				temp[j-i]=N[j];
			}
			if(searchForChunk(temp))
			{
				for(int k=0;k<8;k++)
				{
					// System.out.println("Pushed");
                    chunk[len][k] = temp[k];                     
                }
                len++;
			}
			
			if(len == 256)
				break;
        }
    }
	

	public void answer(byte A[])
	{
		byte B[]=new byte[(width*height)];
		for(int i=0; i<A.length; i+=8)
		{
			byte temp[]=new byte [8];
			for(int j=i;j<i+8;j++)
			{
				temp[j-i]=A[j];
			}
			boolean bool=searchForChunk(temp);
			if( bool == false)
			{
				for(int j=i;j<i+8;j++)
				{
					B[j]=127;
					// System.out.println("Matched");
				}				
			}
			else
			{
				for(int j=i;j<i+8;j++)
				{
					B[j]=0;
				}
			}
		}
		for(int i=0;i<height;i++)
		{
			for(int j=0;j<width;j++)
			{
				E[i][j]=B[width*i+j];
			}
		}
	}
	

	public void display()
	{
		for(int i=0;i<1024;i++)
		{
			System.out.print(E[0][i]+" ");
		}
	}

	public void OnetoTwo(byte A[]){
		for(int i=0;i<1024;i++){
			for(int j=0;j<768;j++){
				I[i][j] = A[1024*i + j];
			}	
		}
	}

	public byte[] TwoDtoOneD(){
		byte A[] = new byte[1024*768];

		for(int i=0;i<1024*768;i++){
			A[i] = I[i/1024][i%1024];
		}
		return A;
	}
	
	//This method outputs a grayscale image file in the jpg format.
	public void writeImage(String outputFileName){
		
		File F = new File(imageName);
		
		BufferedImage BI = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
				
		for(int y = 0;y < height;y++){
			for(int x = 0;x < width;x++){
				int gray = (int) E[y][x] & 0xFF;
				int newRGB = (gray << 16) + (gray << 8) + gray;
				BI.setRGB(x, y, newRGB);
			}
		}
		try{
			ImageIO.write(BI, "JPG", new File(outputFileName));
		} catch(IOException e){
			System.err.println("Unable to output results");
		}
	}
}

public class cipher{

	public static void display(byte temp[])
	{
		for(int i=0;i<temp.length;i++)
		{
			System.out.print(temp[i]+" ");
		}
	}

	public static void main(String args[]){	
		
		//Initialize the image operations object that reads the image.
		ImageOperations IOP = new ImageOperations(args[0]);
		byte B[]=IOP.TwoDtoOneD();
		IOP.chunk(B);
		IOP.answer(B);
		// IOP.display();

		//Write the file.
		IOP.writeImage(args[1]);
	}	
}