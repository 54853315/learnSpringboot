package pers.learn.common.util.security;

import org.apache.shiro.crypto.RandomNumberGenerator;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.Sha256Hash;

import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.KeyGenerator;


/**
 * 对称密钥密码算法工具类
 */
public class CipherUtils {
    /**
     * 生成随机秘钥
     *
     * @param keyBitSize    字节大小
     * @param algorithmName 算法名称
     * @return 创建密匙
     */
    public static Key generateNewKey(int keyBitSize, String algorithmName) {
        KeyGenerator kg;
        try {
            kg = KeyGenerator.getInstance(algorithmName);
        } catch (NoSuchAlgorithmException e) {
            String msg = "Unable to acquire " + algorithmName + " algorithm.  This is required to function.";
            throw new IllegalStateException(msg, e);
        }
        kg.init(keyBitSize);
        return kg.generateKey();
    }

    /**
     * 生成随机salt
     *
     * @param plainTextPassword
     * @return
     */
    public static Map<Object, String> generateSlat(String plainTextPassword) {
        //使用Apache的commons-lang包中的方法，生产一个随机的数字，数字长度为20
//        String salt = RandomStringUtils.randomAlphabetic(20)
        // 使用shiro的安全随机数生成
        RandomNumberGenerator rng = new SecureRandomNumberGenerator();
        // 这里必须toString或者toHex，不能直接将SimpleByteSource对象传参给Sha256Hash()
        // 因为在Sha256Hash类的convertSourceToBytes()中，执行 this.convertSaltToBytes(salt.toString()) 和 this.convertSaltToBytes(salt) 是2个结果
        // 存入数据库的salt可是字符串啊，在doGetAuthenticationInfo 里进行到HashedCredentialsMatcher 类的中的 doCredentialsMatch() 时，
        // 会将用户输入的明文密码根据配置里的加密规则和数据库里的盐值进行加密 怎么样都不会相等的（不会抛出异常）
        String salt = rng.nextBytes().toHex();
        //明文密码进行散列算法1024次，并加盐
        String password = new Sha256Hash(plainTextPassword, salt, 1024).toBase64();
        Map<Object, String> map = new HashMap<>();
        map.put("salt", salt);
        map.put("password", password);
        return map;
    }
}
