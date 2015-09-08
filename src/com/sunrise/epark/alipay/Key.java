package com.sunrise.epark.alipay;

public class Key {
	//商户PID
		public static final String PARTNER = "2088021394138713";//"2088411331507115";
		//商户收款账号
		public static final String SELLER = "sunrise.itech@hotmail.com";//"xiuhu.f@longerkj.com";
		//商户私钥，pkcs8格式
		public static final String RSA_PRIVATE = "MIICeAIBADANBgkqhkiG9w0BAQEFAASCAmIwggJeAgEAAoGBANZQ7kkQir513oqjUYtWPP59h3/xdrO7y/SwR17JpukvPA6DGPxbL11agJxvuTxwXiFugzDHqZcKuLYxfyIL1LePB1CTES4yyDGL3BG3H47V9Cyz+OnoK70sd1KLxdtjQjHqfg/W29UPpAmrOWKtFncEqKH4/rFZAGSEvM7jS4ppAgMBAAECgYEAnZDcz/0IdKUGG5mdXzrlFxnFYExvwA7HEbv0jPzm6TPgDK9X1FlaBmF+gwUlBAl9O0kbzOZOigzI5rKXTLm0BnL3B61DumlwFdPkw1+Y0WrPp3hsy+ihCA5u9D5GA8OnaVj38RpwwDFKKT5y4LcHGZo8ebuOlJhNT9l92WyrgzECQQD9WX/uFx+DlSSphIONWpST6twv3fJr/KRQJDA291XkHc/BW3asvB66W1aQVVr4M1lmygYPkP/8/ONNGo+yhpT1AkEA2I7lElgl+zi6hVwqaIFXjrxPgu4C0ikW9h6jJqPYvQclQ/bzc/SGMO4Wa02zdoM7iGeBhYfEwhK1wQrMSt4XJQJAQz6lMRncGH3WrFPq4vL+6r/0z5O2i6kUJ91jGsPNfW3YPfc76Z9I/KfPsyGksqpWeWz37NvMxmaNImMBbx4/nQJBANR9kgQn/ky9YzfMMciNcY6a9CHyzU3OB7vEYC5gAyGny0cfH88dljaYx+LOQVP+fQHhHlQZ3lTX6wPaRo8H0QkCQQDXtlDz7k57+GcQpscHgWfpdBpLhhthaDaFDhqdYREe/nsNW9XDKNs6ONiNE1rvp8DjuQqu3wRAzFBzZezVbsUd";
		//支付宝公钥
		public static final String RSA_PUBLIC ="MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDDI6d306Q8fIfCOaTXyiUeJHkrIvYISRcc73s3vF1ZT7XN8RNPwJxo8pWaJMmvyTn9N4HQ632qJBVHf8sxHi/fEsraprwCtzvzQETrNRwVxLO5jVmRGi60j8Ue1efIlzPXV9je9mkjzOmdssymZkh2QhUrCmZYI/FCEa3/cNMW0QIDAQAB";

		//商户私钥，pkcs8格式
/*		public static final String RSA_PRIVATE = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAL9" +
				"LDVC694KUO6O5pXi5TUAdd1oFCkRsuXsB0pq58FK0xs05b6j3C35P+Ht3oWy4cVSAeKty7eZNDIYYkuG" +
				"jk0lj3vTkVNsByVaR+z/W5usP8794HgqhKAQyw6Ymh+F0tj4n2hO8m0XJXLBrsFszEZOytJkUom5J0+n" +
				"O31pYiLhlAgMBAAECgYAESyv2Vuv9S1R3XVBggFBCkya9p4VOed5D64uNm4TQZmxb6OEfusPQRv090Da" +
				"YWR96FRQgTQkX9TzFucF6PRCuOAn2yCuYO+dR+ybYURQo6yrRUX36DHD4/Bz4unD8x6SBntS9yLX/2Lb" +
				"nqQCWmfbnb4dDYzwzX9Fz8PHEe/2a1QJBAPVUp62errFdjLwlfwDofTSwvisOLu20IxQLmnW1R9E092Z" +
				"W7KQSBBsf3P9e8fn4sKoQz69NIFM4MLNJFhsL2ccCQQDHnMe1V0F1kTmnvzjBuSpGgspA1BJGKEaQt40" +
				"ZHerNOQH0cMW70K+32kJMFDiSdhkVDGTknylp2NmVHItJevxzAkBIKFoZSu7+5BEc1bqBPeB1uvZ0G3v" +
				"aFn2qy67mqCczdWy/ARohN9tVTw3lXru1Vlw/6Sns2baEQ6avVPPXiKjJAkARoppKPSE9X802MsCy7Mb" +
				"9X8S6oYHTzO8fDfhbRbde1jCEBgqSI0fC+Hdu/UJaPjDNGUE4qY8hGNVwRQtRPJpdAkEAilm9nXW1Bth" +
				"UWa2bQWsYXq/YjX8tk6UPIYZP6lAAyLWkeZwNqqfj/YqQhOmdS/W9lUswiUQv1gyxItKfOo7+Sw==";
*/
		//支付宝公钥
	/*	public static final String RSA_PUBLIC = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCnxj/9qwVfgoU" + 
				"h/y2W89L6BkRAFljhNhgPdyPuBV64bfQNN1PjbCzkIM6qRdKBoLPXmKKMiFYnkd6rAoprih3/PrQEB/"
				+ "VsW8OoM8fxn67UDYuyBTqA23MML9q1+ilIZwBC2AQ2UBVOrFXfFl75p6/B5KsiNG9zpgmLCUYuLkxpL"
				+ "QIDAQAB";
		*/
		public static final int SDK_PAY_FLAG = 1;

		public static final int SDK_CHECK_FLAG = 2;
}
