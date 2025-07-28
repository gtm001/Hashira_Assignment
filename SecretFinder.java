import java.io.*;
import java.math.BigInteger;
import java.util.*;
import org.json.simple.*;
import org.json.simple.parser.*;

public class SecretFinder {

    public static BigInteger lagrangeInterpolation(List<BigInteger> x, List<BigInteger> y) {
        int k = x.size();
        BigInteger result = BigInteger.ZERO;

        for (int i = 0; i < k; i++) {
            BigInteger term = y.get(i);
            for (int j = 0; j < k; j++) {
                if (j != i) {
                    BigInteger numerator = x.get(j).negate(); // -xj
                    BigInteger denominator = x.get(i).subtract(x.get(j)); // xi - xj
                    term = term.multiply(numerator).divide(denominator);
                }
            }
            result = result.add(term);
        }

        return result;
    }

    public static BigInteger findSecretFromJson(String filename) throws Exception {
        // Read JSON
        JSONParser parser = new JSONParser();
        JSONObject data = (JSONObject) parser.parse(new FileReader(filename));

        JSONObject keys = (JSONObject) data.get("keys");
        int k = Integer.parseInt(keys.get("k").toString());

        List<BigInteger> xList = new ArrayList<>();
        List<BigInteger> yList = new ArrayList<>();

        // Collect first k entries
        int count = 0;
        for (Object key : data.keySet()) {
            if (key.equals("keys")) continue;
            count++;
            if (count > k) break;

            int x = Integer.parseInt(key.toString());
            JSONObject entry = (JSONObject) data.get(key.toString());
            int base = Integer.parseInt(entry.get("base").toString());
            String valStr = entry.get("value").toString();

            BigInteger y = new BigInteger(valStr, base);
            xList.add(BigInteger.valueOf(x));
            yList.add(y);
        }

        // Use Lagrange interpolation at x=0
        return lagrangeInterpolation(xList, yList);
    }

    public static void main(String[] args) throws Exception {
        BigInteger secret1 = findSecretFromJson("secret1.json");
        BigInteger secret2 = findSecretFromJson("secret2.json");

        System.out.println("Secret 1: " + secret1);
        System.out.println("Secret 2: " + secret2);
    }
}
