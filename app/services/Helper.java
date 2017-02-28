package services;

import java.util.Random;

/**
 * Created by vnazarov on 28/02/17.
 */
public class Helper {
    static public String generateUniqueString(int size){
        final String alphabet ="0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        int n = alphabet.length();
        StringBuilder sb = new StringBuilder(size);
        Random r = new Random();
        for (int i=0; i<size; i++) {
            sb.append(alphabet.charAt(r.nextInt(n)));
        }
        return sb.toString();
    }
}
