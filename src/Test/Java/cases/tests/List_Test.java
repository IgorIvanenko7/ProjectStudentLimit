package cases.tests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Log4j2
public class List_Test /*extends Root*/ {

    @Test()
    public void addItemList() {
        String key = "key";
        List<String> listStr = List.of("a", "b", "c");
        Map<String, List<String>> storeUser = new HashMap<>();

        listStr.forEach (item -> {
                    var lll = storeUser.getOrDefault(key, new ArrayList<>());
                    lll.add(item);
                    storeUser.put(key, lll);
                }
        );
        log.info(" Map: {}", storeUser);
    }

    @Test()
    public void convert() {

        ObjectMapper vm = new ObjectMapper();

        Map<String, Object> entities = new LinkedHashMap<>();
        entities.put("folds", vm.createObjectNode());
        entities.put("base", "Data");
        entities.put("base1", "Data1");
        var jNode = vm.valueToTree(entities);

        log.info("debug_1 ->: {}", jNode);

        var resJ = Optional.ofNullable(jNode.findValue("folds"))
                .orElseGet(() -> {
                    var objectNode = (ObjectNode) jNode;
                    objectNode.put("folds", vm.createObjectNode());
                    return  objectNode;
                });

        System.out.println(resJ);
        log.info(" Res: {}", resJ.toString());
    }

    @Test()
    public void encryption_Decryption() throws Exception {
        String password = "secret";
//      String encrypt = "AES256:Jz728zw/0DYJGGEZQASoF96lJmnwXZVjsiuln/VJnL0=";
        String encrypt = "AES256:/4uDmSdMpB80DvceXhrrnqO++1qEDsx04Q48PqEyuvU/Qyd+G/DeUwduXM3Wu7oXD5igntDPnmMAW9Vrkomi5RSFFv01Ml9dSiWhEVAL1tm2ntYq4VOeRp5lo0A7UdUtiC3kDzhuhpaefVvxoxd3YILawrKTLNEB1TbiY7cHV9vLyiTgJgKc6ofdN7mKXzEW";
        var encryptString = decryptAES(password, encrypt);
        log.info("# Encript : {} #", encryptString);
    }

    private static final int AES_BLOCK_SIZE = 16; // Размер блока AES в байтах
    private static final String ENCRYPTION_ALGORITHM = "AES/CBC/PKCS5PADDING",
                                AES = "AES256:";

    @SneakyThrows
    public String decryptAES(String password, String cryptMessage) {
        // Validate is null Delete prefix substring
        var cryptM =  Optional.ofNullable(cryptMessage)
                .map(cryptMess ->
                    cryptMess.contains(AES) ?
                        cryptMess.substring(AES.length()) : cryptMess)
                .orElseThrow(() -> new RuntimeException("# Exception: cryptMessage is Null #"));
        // Создание 32-байтового ключа на основе пароля
        byte[] key = new byte[32];
        byte[] passwordBytes = password.getBytes(StandardCharsets.UTF_8);
        System.arraycopy(passwordBytes, 0, key, 0, Math.min(passwordBytes.length, key.length));
        // Декодирование зашифрованного текста из Base64
        byte[] crypt = Base64.getDecoder().decode(cryptM);
        // Получаем IV (первые 16 байт зашифрованного текста)
        byte[] iv = new byte[AES_BLOCK_SIZE];
        System.arraycopy(crypt, 0, iv, 0, AES_BLOCK_SIZE);
        // Получаем шифрованные данные (первый блок уже содержит IV)
        byte[] encryptedBytes = new byte[crypt.length - AES_BLOCK_SIZE];
        System.arraycopy(crypt, AES_BLOCK_SIZE, encryptedBytes, 0, encryptedBytes.length);
        // Создание объекта Cipher для расшифровки
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
        IvParameterSpec ivParams = new IvParameterSpec(iv);
        Cipher cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParams);
        byte[] original = cipher.doFinal(encryptedBytes);
        return new String(original);
    }
}





