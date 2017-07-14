package randomizer.common.utils;

import randomizer.common.utils.dsdecmp.HexInputStream;
import randomizer.common.utils.dsdecmp.JavaDSDecmp;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class CompressionUtils 
{
	public static byte[] decompress(File file)
	{	
		try 
		{
			int[] output = new int[0];
			HexInputStream hexStream;
			byte[] bytes = Files.readAllBytes(Paths.get(file.getAbsolutePath()));
			if (bytes[0] == 0x13) 
			{
				byte[] tmp = new byte[bytes.length - 4];
				System.arraycopy(bytes, 4, tmp, 0, tmp.length);
				bytes = tmp;
			}
			hexStream = new HexInputStream(new ByteArrayInputStream(bytes));
			output = JavaDSDecmp.decompress(hexStream);
			bytes = new byte[output.length];
			for (int i = 0; i < output.length; i++) 
				bytes[i] = (byte) output[i];
			return bytes;
		} catch (IOException e) {
			e.printStackTrace();
			return new byte[0];
		}
	}
	
	public static byte[] compress(File file)
	{
		try {
			byte[] cmp = JavaDSDecmp.compressLZ11(Files.readAllBytes(Paths.get(file.getCanonicalPath())));
			byte[] cmp2 = new byte[cmp.length + 4];
			cmp2[0] = 0x13;
			System.arraycopy(cmp, 0, cmp2, 4, cmp.length);
			System.arraycopy(cmp, 1, cmp2, 1, 3);
			return cmp2;
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
			return new byte[0];
		}
	}
	
	public static byte[] compress(byte[] bytes)
	{
		byte[] cmp = JavaDSDecmp.compressLZ11(bytes);
		byte[] cmp2 = new byte[cmp.length + 4];
		cmp2[0] = 0x13;
		System.arraycopy(cmp, 0, cmp2, 4, cmp.length);
		System.arraycopy(cmp, 1, cmp2, 1, 3);
		return cmp2;
	}
}
