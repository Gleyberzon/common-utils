package miscellaneousUtils;

import java.util.List;
import java.util.Random;

/**
 * Class holds random functions
 * @author Yael Rozenfeld
 * @since 28.12.2022
 */
public class RandomUtils {
    /**
     * Get random Integer with exclude values
     * @param rnd - instance of Random
     * @param start - int to start random range
     * @param end - int to end random range
     * @param exclude - List<Integer> contains values to exclude
     * @return random number
     * @author Yael Rozenfeld
     * @since 28.12.2022
     */
    public static int getRandomIntWithExclusion(Random rnd, int start, int end, List<Integer> exclude ) {
        int random = start + rnd.nextInt(end - start + 1 - exclude.size());
        for (int ex : exclude) {
            if (random < ex) {
                break;
            }
            random++;
        }
        return random;
    }

    /**
     * Get a random number
     * @param upperbound the upper bound for the random number
     * @return a random number
     * @author genosar.dafna
     * @since 18.07.2022
     */
    public static int getRandomNumber(int upperbound)
    {
        Random rand = new Random();
        return rand.nextInt(upperbound);
    }

    /**
     * create a random number by the given length
     * For example: if length is 2 the returned random number will be between 10 and 99
     * @param length length of number
     * @return a random number by the given length
     * @author ghawi.rami
     * @since 19.07.2023
     */
    public static int getRandomNumberByLength(int length) {
        int min = (int) Math.pow(10, length - 1);
        int max = (int) Math.pow(10, length) - 1;

        Random random = new Random();
        return random.nextInt(max - min + 1) + min;
    }

    /**
     * Get a random number between 2 given numbers
     * @param upperBound the upper bound for the random number
     * @param lowerBound the lower bound for the random number
     * @return a random number
     * @author genosar.dafna
     * @since 18.07.2022
     */
    public static int getRandomNumber(int lowerBound, int upperBound)
    {
        return (int) (Math.random() * (upperBound - lowerBound)) + lowerBound;
    }

    /**
     * create a random string composed of letters
     * @param length length of wanted string
     * @param alphabet alphabet to generate from
     * @return return generated string
     * @author ghawi.rami
     * @since 19.07.2023
     */
    public static String getRandomAlphabetString(int length, String alphabet) {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < length; i++) {
            int index = random.nextInt(alphabet.length());
            char randomChar = alphabet.charAt(index);
            sb.append(randomChar);
        }

        return sb.toString();
    }
}
