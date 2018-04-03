package tf;

import java.io.*;
import java.util.Arrays;

public class Application {
    private static final int MAX = 25;

    private static final int STOPWORDS = 0;         // data[0] is stopwords list
    private static final int LINE = 1;              // data[1] is line list
    private static final int IDX_START_CHAR = 2;    // data[2] is the star char of a word
    private static final int IDX = 3;               // data[3] is the index
    private static final int FLAG = 4;              // data[4] is a flag indicating if a word has been found
    private static final int WORD = 5;              // data[5] is the word candidate
    private static final int WORD_NNNN = 6;         // data[6] is the word NNNN
    private static final int FREQUENCY = 7;         // data[7] is frequency
    private static final int POS_FD = 8;            // data[8] is current pointer
    private static final int TF_COUNT = 9;          // data[9] is the size of the list
    private static final int TF_WORD = 10;          // data[10..34] is the list of words
    private static final int TF_FREQUENCY = 35;      // data[35..69] is the list of frequencies

    private static final int MEM_DATA_BANK = 71;

    public static void main(String args[]) throws IOException {
        // PART 1:
        // - read stopwords
        // - read the input file one line at a time
        // - filter the characters, normalize to lower case
        // - identify words, increment corresponding counts in file

        // Stop words

        BufferedReader f = new BufferedReader(new InputStreamReader(Application.class.getResourceAsStream(args[0])));
        Object[] data = new Object[MEM_DATA_BANK];
        data[STOPWORDS] = f.readLine().split(",");
        f.close();

        // Open the input file
        f = new BufferedReader(new InputStreamReader(Application.class.getResourceAsStream(args[1])));

        // Secondary memory
        File out =  File.createTempFile("word_freqs", "tmp");
        RandomAccessFile word_freqs = new RandomAccessFile(out, "rw");

        // Loop over input file's lines
        ;
        while((data[LINE] = f.readLine()) != null) {
            data[IDX_START_CHAR] = null;
            data[IDX] = 0;
            for(char c: (((String) data[LINE]) + '\n').toCharArray()) {
                if (data[IDX_START_CHAR] == null) {
                    if (Character.isLetterOrDigit(c)) {
                        // Found the start of a word
                        data[IDX_START_CHAR] = data[IDX];
                    }
                } else {
                    if (!Character.isLetterOrDigit(c)) {
                        // Found the end of a word
                        data[WORD] = ((String) data[LINE]).substring((int)data[IDX_START_CHAR], (int)data[IDX]).toLowerCase();
                        if (((String) data[WORD]).length() > 1 && !
                                Arrays.asList((Object[])data[STOPWORDS]).contains(data[WORD])) {
                            word_freqs.seek(0);
                            data[POS_FD] = 0L;
                            data[FLAG] = null;
                            data[FREQUENCY] =  0;
                            // Let's find it
                            while(data[FLAG] == null && word_freqs.read() != -1) {
                                word_freqs.seek((long)data[POS_FD]);
                                data[WORD_NNNN] = word_freqs.readUTF();
                                data[FREQUENCY] = word_freqs.readInt();
                                if (data[WORD].equals(data[WORD_NNNN])) {
                                    data[FLAG] = true;
                                    word_freqs.seek((long) data[POS_FD]);
                                } else {
                                    data[POS_FD] = word_freqs.getFilePointer();
                                    data[FREQUENCY] =  0;
                                }
                            }
                            // Update
                            word_freqs.writeUTF((String)data[WORD]);
                            word_freqs.writeInt((int) data[FREQUENCY] + 1);
                            word_freqs.seek(0);
                        }
                        data[IDX_START_CHAR] = null;
                    }
                }
                data[IDX] = (int)data[IDX] + 1;
            }
        }

        // PART 2
        // Now we need to find the 25 most frequently occurring words.
        data[POS_FD] = word_freqs.getFilePointer();
        data[TF_COUNT] = 0;
        while(word_freqs.read() != -1) {
            word_freqs.seek((long)data[POS_FD]);
            data[WORD_NNNN] = word_freqs.readUTF();
            data[FREQUENCY] = word_freqs.readInt();
            data[FLAG] = null;
            for(int i = 0; i < MAX && data[FLAG] == null; i++) {
               if (data[TF_WORD + i] == null || (int)data[TF_FREQUENCY + i]<(int)data[FREQUENCY]) {
                   if ((int)data[TF_COUNT] < MAX) {
                       data[TF_COUNT] = (int) data[TF_COUNT] + 1;
                   }
                   for(int j = (int)data[TF_COUNT] - 1; i < j ; j--) {
                       data[TF_WORD + j] = data[TF_WORD + j - 1];
                       data[TF_FREQUENCY + j] = data[TF_FREQUENCY + j -1];
                   }
                   data[TF_WORD + i] = data[WORD_NNNN];
                   data[TF_FREQUENCY + i] = data[FREQUENCY];
                   data[FLAG] = true;
               }
            }
            data[POS_FD] = word_freqs.getFilePointer();
        }

        // Open the output file
        BufferedWriter o = new BufferedWriter(new FileWriter(new File(args[2])));
        for(int i = 0; i < (int)data[TF_COUNT]; i++) {
            o.write(data[TF_WORD + i] + " - " + data[TF_FREQUENCY + i] + "\n");
        }
        o.close();

        word_freqs.close();
        f.close();
    }
}
