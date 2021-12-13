package com.example.gcp.testgcpservice.cntrlr;

import java.io.BufferedReader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.spec.MGF1ParameterSpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import javax.crypto.Cipher;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.cloud.kms.v1.CryptoKeyVersionName;
import com.google.cloud.kms.v1.KeyManagementServiceClient;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/")
public class MainController {

	@Value("${gcp.project-id}")
	String projectId;

	@Value("${gcp.key-zone}")
	String projectZone;

	@Value("${gcp.key-ring}")
	String keyRingName;

	@Value("${gcp.key-name-rsa}")
	String keyName;

	@Value("${gcp.key-version-rsa}")
	String keyVersion;

	@GetMapping()
	public Map<String, String> getHomeMain() {

		Map<String, String> returnMap = new HashMap<>();
		returnMap.put("status", "Success");
		returnMap.put("method", "getHomeMain");

		try {

			String plainText = "message";
			returnMap.put("plainText", plainText);

			// ENCRYPTION ##############################
			KeyManagementServiceClient client = KeyManagementServiceClient.create();

			CryptoKeyVersionName keyVersionName = CryptoKeyVersionName.of(projectId, projectZone, keyRingName, keyName,
					keyVersion);

			// Get the public key.
			com.google.cloud.kms.v1.PublicKey publicKey = client.getPublicKey(keyVersionName);

			BufferedReader bufferedReader = new BufferedReader(new StringReader(publicKey.getPem()));
			String encoded = bufferedReader.lines()
					.filter(line -> !line.startsWith("-----BEGIN") && !line.startsWith("-----END"))
					.collect(Collectors.joining());
			byte[] derKey = Base64.getDecoder().decode(encoded);

			X509EncodedKeySpec keySpec = new X509EncodedKeySpec(derKey);
			java.security.PublicKey rsaKey = KeyFactory.getInstance("RSA").generatePublic(keySpec);

			Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
			OAEPParameterSpec oaepParams = new OAEPParameterSpec("SHA-256", "MGF1", MGF1ParameterSpec.SHA256,
					PSource.PSpecified.DEFAULT);
			cipher.init(Cipher.ENCRYPT_MODE, rsaKey, oaepParams);
			byte[] ciphertext = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

			String encryptedString = Base64.getEncoder().encodeToString(ciphertext);
			returnMap.put("encryptedString", encryptedString);

		} catch (Exception e) {
			returnMap.put("exception", e.getMessage());
			log.error("exception", e);
		}

		return returnMap;
	}

}
