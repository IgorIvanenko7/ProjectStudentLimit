package cases.tests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
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
    //@SneakyThrows
    public void encryption_Decryption() throws Exception {
        String password = "secret";
        String encrypt = "Jz728zw/0DYJGGEZQASoF96lJmnwXZVjsiuln/VJnL0=";

//        var encryptString = aesEncryptionUtil.decrypt(encrypt, encrypt);
        var encryptString = decryptAES1(password, encrypt);

        log.info("# Encript : {}#", encryptString);
    }


    private static final int AES_BLOCK_SIZE = 16; // Размер блока AES в байтах

    public String decryptAES(String password, String crypt64) throws Exception {


        // Создание 32-байтового ключа на основе пароля
        byte[] key = new byte[32];
        byte[] passwordBytes = password.getBytes(StandardCharsets.UTF_8);
        System.arraycopy(passwordBytes, 0, key, 0, Math.min(passwordBytes.length, key.length));

        // Декодирование зашифрованного текста из Base64
        byte[] crypt = Base64.getDecoder().decode(crypt64);

        // Получаем IV (первые 16 байт зашифрованного текста)
        byte[] iv = new byte[AES_BLOCK_SIZE];
        System.arraycopy(crypt, 0, iv, 0, AES_BLOCK_SIZE);

        // Получаем шифрованные данные (первый блок уже содержит IV)
        byte[] encryptedBytes = new byte[crypt.length - AES_BLOCK_SIZE];
        System.arraycopy(crypt, AES_BLOCK_SIZE, encryptedBytes, 0, encryptedBytes.length);

        // Создание объекта Cipher для расшифровки
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
        IvParameterSpec ivParams = new IvParameterSpec(iv);

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParams);


        byte[] original = cipher.doFinal(Base64.getDecoder().decode(crypt64));
        return new String(original);




        // Удаление паддинга
        // Проверяем, что последний байт не превышает размер расшифрованного текста
//        int padding = decrypted[decrypted.length - 1];
//        if (padding < 1 || padding > AES_BLOCK_SIZE) {
//            throw new IllegalArgumentException("Invalid padding");
//        }
//
//        // Удаляем паддинг и создаем строку
//        return new String(decrypted, 0, decrypted.length - padding, StandardCharsets.UTF_8);
    }



    public String decryptAES1(String password, String crypt64) throws Exception {
        if (crypt64 == null || crypt64.isEmpty()) {
            return null;
        }

        // Создание 32-байтового ключа на основе пароля
        byte[] key = new byte[32];
        byte[] passwordBytes = password.getBytes(StandardCharsets.UTF_8);
        System.arraycopy(passwordBytes, 0, key, 0, Math.min(passwordBytes.length, key.length));

        // Декодирование зашифрованного текста из Base64
        byte[] crypt = Base64.getDecoder().decode(crypt64);

        // Создание объекта Cipher для шифрования
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
        IvParameterSpec ivParams = new IvParameterSpec(crypt, 0, AES_BLOCK_SIZE);

        // Извлечение зашифрованных данных
        byte[] encryptedBytes = new byte[crypt.length - AES_BLOCK_SIZE];
        System.arraycopy(crypt, AES_BLOCK_SIZE, encryptedBytes, 0, encryptedBytes.length);

        // Инициализация шифра
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParams);

        // Расшифровка
        byte[] decrypted = cipher.doFinal(encryptedBytes);

        // Удаление паддинга
        int lastByte = decrypted[decrypted.length - 1];
        if (lastByte < 1 || lastByte > AES_BLOCK_SIZE) {
            throw new IllegalArgumentException("Invalid padding");
        }

        return new String(decrypted, 0, decrypted.length - lastByte, StandardCharsets.UTF_8);
    }




}
