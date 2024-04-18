import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;

public class OS1Assignment {

    public static void main(String[] args) throws FileNotFoundException, IOException {

        String filename = args[0];
        File file = new File(filename);
        int[] pagemaps = {2, 4, 1, 7, 3, 5, 6}; // page table

        FileWriter fw = new FileWriter("Output-OS1");

        try (FileInputStream stream = new FileInputStream(file)) {
            byte[] buffer = new byte[8]; // Buffer to hold each 8-byte address
            int bytesRead = 0;

            while (true) { // Continue until end of file
                int read = stream.read(buffer, bytesRead, 8 - bytesRead);
                if (read == -1) {
                    break; // End of file
                }
                bytesRead += read;

                if (bytesRead == 8) { // Process each 8-byte chunk
                    reverse(buffer);

                    // Convert buffer to hexadecimal representation
                    StringBuilder hexString = new StringBuilder();
                    for (int i = 0; i < buffer.length; i++) {
                        hexString.append(String.format("%02x", buffer[i]));
                    }

                    // System.out.println(hexString.toString());

                    // Convert buffer to long value
                    long virtual = new BigInteger(1, buffer).longValue();
                    String vbinary = Long.toBinaryString(virtual);

                    // Add leading zeros to create last 7 bits as offset
                    String paddedBinary;
                    if (vbinary.length() < 7){
                        paddedBinary = String.format("%7s", vbinary).replace(' ', '0');
                    }
                    // otherwise use last 7 bits
                    else{
                        paddedBinary = vbinary.substring(vbinary.length() - 7);
                    }

                    //System.out.println(paddedBinary);

                    // Convert offset from binary string to integer
                    int offset = Integer.parseInt(paddedBinary, 2);

                    //System.out.println(offset);

                    // Determine page number based on virtual address
                    int page;
                    if (virtual < 128) {
                        page = 0;
                    } else if (virtual > 127 && virtual < 256) {
                        page = 1;
                    } else if (virtual > 255 && virtual < 385) {
                        page = 2;
                    } else if (virtual > 384 && virtual < 514) {
                        page = 3;
                    } else if (virtual > 513 && virtual < 643) {
                        page = 4;
                    } else if (virtual > 642 && virtual < 772) {
                        page = 5;
                    } else if (virtual > 772 && virtual < 901) {
                        page = 6;
                    } else {
                        page = 0;
                    }

                    // Map page number to frame number
                    int pageIndex = pagemaps[page];

                    // Calculate base index to perform translation
                    int baseIndex = pageIndex * 128;

                    // Generate physical address (as an int)
                    int physical = baseIndex + offset;

                    //System.out.println(physical);

                    // Convert int back to hexadecimal to form physical address
                    String hexaPhysical = Integer.toHexString(physical) + "\n";

                    // write each physical address to output file
                    fw.write(hexaPhysical);

                    //System.out.println(hexaPhysical);

                    // Reset bytesRead and hexString for the next 8-byte chunk
                    bytesRead = 0;
                    hexString.setLength(0); // Clear the StringBuilder
                }
            }
            fw.close();
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
    }

    // Method to reverse the order of bytes in the array
    public static void reverse(byte[] array) {
        if (array == null) {
            return;
        }
        int i = 0;
        int j = array.length - 1;
        byte tmp;
        while (j > i) {
            tmp = array[j];
            array[j] = array[i];
            array[i] = tmp;
            j--;
            i++;
        }
    }
}
