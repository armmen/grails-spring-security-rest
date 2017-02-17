package grails.plugin.springsecurity.rest.token.generation.jwt;

import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.InitializingBean

import java.nio.charset.StandardCharsets
import java.security.KeyFactory
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import org.bouncycastle.util.io.pem.PemReader

/**
 * Loads RSA public/private key's from configuration strings
 */
@Slf4j
@CompileStatic
class StringRSAKeyProvider implements RSAKeyProvider, InitializingBean {

    String publicKeyStr
    String privateKeyStr

    RSAPublicKey publicKey
    RSAPrivateKey privateKey

    private byte[] decodeKey(String key) {
        InputStream input = new ByteArrayInputStream(key.getBytes(StandardCharsets.UTF_8))
        InputStreamReader inputStreamReader = new InputStreamReader(input)
        PemReader pemReader = new PemReader(inputStreamReader)
        try {
            return pemReader.readPemObject().getContent()
        } finally {
            pemReader.close()
            inputStreamReader.close()
            input.close()
        }
    }

    @Override
    void afterPropertiesSet() throws Exception {
        log.debug "Loading public/private key from configuration"
        KeyFactory kf = KeyFactory.getInstance("RSA")
        log.debug "Public key: ${publicKeyStr}"

        if (publicKeyStr) {
            def spec = new X509EncodedKeySpec(decodeKey(publicKeyStr))
            publicKey = kf.generatePublic(spec) as RSAPublicKey
        }

        log.debug "Private key: ${privateKeyStr}"
        if (privateKeyStr) {
            def spec = new PKCS8EncodedKeySpec(decodeKey(privateKeyStr))
            privateKey = kf.generatePrivate(spec) as RSAPrivateKey
        }
    }

}
