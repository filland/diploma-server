import bntu.diploma.utils.AdvancedEncryptionStandard;

import java.nio.charset.StandardCharsets;

public class Test {

    public static void main(String[] args) {


        String message = "1";

        String key = "MZygpewJsCpRrfOr";

        try {
            byte [] result = AdvancedEncryptionStandard.encrypt(message.getBytes(), key.getBytes(StandardCharsets.UTF_8));

            System.out.println("result - "+new String(result));


            String decryptedMessage = AdvancedEncryptionStandard.decrypt(result, key.getBytes(StandardCharsets.UTF_8));


            System.out.println("decrypted message - "+decryptedMessage);


        } catch (Exception e) {
            e.printStackTrace();
        }


    }

}
