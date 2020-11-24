package net.dreamlu.iot.mqtt.aliyun;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

/**
 * 阿里云 mqtt 签名方式
 *
 * @author L.cm
 */
public class MqttSign {
	/**
	 * 用户名
	 */
	private final String username;
	/**
	 * 密码
	 */
	private final String password;
	/**
	 * 客户端id
	 */
	private final String clientId;

	public MqttSign(String productKey, String deviceName, String deviceSecret) {
		Objects.requireNonNull(productKey, "productKey is null");
		Objects.requireNonNull(deviceName, "deviceName is null");
		Objects.requireNonNull(deviceSecret, "deviceSecret is null");
		this.username = deviceName + '&' + productKey;
		String timestamp = Long.toString(System.currentTimeMillis());
		this.password = getPassword(productKey, deviceName, deviceSecret, timestamp);
		this.clientId = getClientId(productKey, deviceName, timestamp);
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public String getClientId() {
		return clientId;
	}

	private static String getPassword(String productKey, String deviceName, String deviceSecret, String timestamp) {
		String plainPasswd = "clientId" + productKey + '.' + deviceName + "deviceName" +
			deviceName + "productKey" + productKey + "timestamp" + timestamp;
		return hmacSha256(plainPasswd, deviceSecret);
	}

	private static String getClientId(String productKey, String deviceName, String timestamp) {
		return productKey + '.' + deviceName + '|' + "timestamp=" + timestamp + ",_v=paho-java-1.0.0,securemode=2,signmethod=hmacsha256|";
	}

	private static String hmacSha256(String plainText, String key) {
		if (plainText == null || key == null) {
			return null;
		}
		try {
			Mac mac = Mac.getInstance("HmacSHA256");
			SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(), "HmacSHA256");
			mac.init(secretKeySpec);
			byte[] hmacResult = mac.doFinal(plainText.getBytes());
			return String.format("%064x", new BigInteger(1, hmacResult));
		} catch (NoSuchAlgorithmException | InvalidKeyException e) {
			throw new RuntimeException(e);
		}
	}

}
