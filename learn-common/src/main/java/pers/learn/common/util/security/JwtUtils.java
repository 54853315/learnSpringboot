package pers.learn.common.util.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;

@Slf4j
@Component
public class JwtUtils {
    public static String ISSUER;

    public static long EXPIRE_TIME;

    public static String SECRET_KEY;

    public static Algorithm ALGORITHM;

    @Value("${jwt.issuer}")
    public void setISSUER(String issuer) {
        ISSUER = issuer;
    }

    @Value("${jwt.expireTime}")
    public void setExpireTime(long expireTime) {
        EXPIRE_TIME = expireTime;
    }

    @Value("${jwt.secretKey}")
    public void setSecretKey(String secretKey) {
        SECRET_KEY = secretKey;
        ALGORITHM = Algorithm.HMAC256(SECRET_KEY);
    }

    public static long getExpireTimeForReal() {
        return EXPIRE_TIME * 1000 * 60;
    }

    /**
     * 从数据声明生成令牌
     *
     * @param username
     * @return
     */
    public static String generateToken(String username) {
        long currentTime = System.currentTimeMillis();
        log.info("=====generateToken===now    is " + new Date(currentTime));
        log.info("=====generateToken===expire is " + new Date(currentTime + getExpireTimeForReal()));

        String token = JWT.create()
                .withIssuer(ISSUER)
                .withIssuedAt(new Date(currentTime))// 签发时间
                .withExpiresAt(new Date(currentTime +getExpireTimeForReal()))// 过期时间戳
                .withClaim("username", username)//自定义参数
                .sign(ALGORITHM);

        return token;
    }

    /**
     * 验证令牌
     *
     * @param token
     * @return
     */
//    private Boolean validateToken(String token) {
//        // Reusable verifier instance
//        JWTVerifier verifier = JWT.require(algorithm).withIssuer(issuer).build();
//        DecodedJWT decodedJWT = verifier.verify(token);
//        // verify issuer
//        String issuer = decodedJWT.getIssuer();
//        // verity 自定义参数
//        String username = decodedJWT.getClaim("username").asString();
//        if (("").equals(username)) {
//            return false;
//        }
//        return true;
//    }
    public static Map<String, Object> validateToken(String token) {
        Map<String, Object> principal = null;
        JWTVerifier verifier = JWT.require(ALGORITHM)
                .acceptExpiresAt(EXPIRE_TIME)
                .build();
        try {
            DecodedJWT jwt = verifier.verify(token);
            Claim claim = jwt.getClaim("username");
            principal = claim.asMap();
        } catch (TokenExpiredException exception) {
            log.trace("token过期了: {}", exception.getClass().getName());
        } catch (SignatureVerificationException exception) {
            log.trace("token错误: {}", exception.getClass().getName());
        } catch (JWTDecodeException exception) {
            log.trace("token解码错误: {}", exception.getClass().getName());
        }
        return principal;
    }

    private Boolean isTokenExpired(String token) {
        return false;
    }

}
