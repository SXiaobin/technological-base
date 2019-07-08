# Libraries

## [Sardine](https://github.com/lookfirst/sardine)

### Maven

```xml
	<dependency>
      <groupId>com.github.lookfirst</groupId>
      <artifactId>sardine</artifactId>
      <version>5.8</version>
    </dependency>
```

# How-Tos

## How to use SSL Poke to verify connectivity

Try the Java class `SSLPoke` to see if your truststore contains the right certificates. This will let you connect to a SSL service, send a byte of input, and watch the output.

1. Download [SSLPoke.class](https://confluence.atlassian.com/kb/files/779355358/779355357/1/1441897666313/SSLPoke.class)

2. Execute the class as per the below, changing the URL and port appropriately.

   ```cmd
   $JAVA_HOME/bin/java SSLPoke cloud.e-go-mobile.com 443
   ```

   A failed connection would produce the below:
   
   ```cmd
   $JAVA_HOME/bin/java SSLPoke jira.example.com 443
   sun.security.validator.ValidatorException: PKIX path building failed: sun.security.provider.certpath.SunCertPathBuilderException: unable to find valid certification path to requested target
   	at sun.security.validator.PKIXValidator.doBuild(PKIXValidator.java:387)
   	at sun.security.validator.PKIXValidator.engineValidate(PKIXValidator.java:292)
   	at sun.security.validator.Validator.validate(Validator.java:260)
   	at sun.security.ssl.X509TrustManagerImpl.validate(X509TrustManagerImpl.java:324)
   	at sun.security.ssl.X509TrustManagerImpl.checkTrusted(X509TrustManagerImpl.java:229)
   	at sun.security.ssl.X509TrustManagerImpl.checkServerTrusted(X509TrustManagerImpl.java:124)
   	at sun.security.ssl.ClientHandshaker.serverCertificate(ClientHandshaker.java:1351)
   	at sun.security.ssl.ClientHandshaker.processMessage(ClientHandshaker.java:156)
   	at sun.security.ssl.Handshaker.processLoop(Handshaker.java:925)
   	at sun.security.ssl.Handshaker.process_record(Handshaker.java:860)
   	at sun.security.ssl.SSLSocketImpl.readRecord(SSLSocketImpl.java:1043)
   	at sun.security.ssl.SSLSocketImpl.performInitialHandshake(SSLSocketImpl.java:1343)
   	at sun.security.ssl.SSLSocketImpl.writeRecord(SSLSocketImpl.java:728)
   	at sun.security.ssl.AppOutputStream.write(AppOutputStream.java:123)
   	at sun.security.ssl.AppOutputStream.write(AppOutputStream.java:138)
   	at SSLPoke.main(SSLPoke.java:31)
   Caused by: sun.security.provider.certpath.SunCertPathBuilderException: unable to find valid certification path to requested target
   	at sun.security.provider.certpath.SunCertPathBuilder.build(SunCertPathBuilder.java:145)
   	at sun.security.provider.certpath.SunCertPathBuilder.engineBuild(SunCertPathBuilder.java:131)
   	at java.security.cert.CertPathBuilder.build(CertPathBuilder.java:280)
   	at sun.security.validator.PKIXValidator.doBuild(PKIXValidator.java:382)
   	... 15 more
   ```
   
   A successful connection would look like this:
   
   ```cmd
   $JAVA_HOME/bin/java SSLPoke jira.example.com 443
   Successfully connected
   ```
   
   If `-Djavax.net.ssl.trustStore` is present in your JVM arguments, Java will use the keystore specified with that argument. You can verify whether the `-Djavax.net.ssl.trustStore`parameter is causing problems by running the `SSLPoke `test and specifying the same JVM argument to use that keystore. For example:
   
   ```cmd
   $JAVA_HOME/bin/java -Djavax.net.ssl.trustStore=/my/custom/truststore SSLPoke jira.example.com 443
   ```
   
   If this fails (confirming the problem that the truststore doesn't contain the appropriate certificates), then the certificate will need to be imported into that truststore as per the instructions in [Connecting to SSL Services](https://confluence.atlassian.com/kb/connecting-to-ssl-services-802171215.html).

## How to fetch the certificate

Fetch the certificate, replacing [google.com](http://google.com/) with the FQDN of the server JIRA is attempting to connect to:
**Unix:**

```none
openssl s_client -connect google.com:443 -servername google.com:443 < /dev/null | sed -ne '/-BEGIN CERTIFICATE-/,/-END CERTIFICATE-/p' > public.crt
```

**Windows:**

```none
openssl s_client -connect cloud.e-go-mobile.com:443 -servername cloud.e-go-mobile.com:443 < NUL | sed -ne '/-BEGIN CERTIFICATE-/,/-END CERTIFICATE-/p' > public.crt
```

If you are under a redirection domain page, you must specify always -servername <your_domain_name> in order to ensure we are loading the correct domain, otherwise, openssl takes the first SSL cert he receives, when it should be the second cert that belongs to your domain.

The command above will only be executed if you have [Sed for Windows](http://gnuwin32.sourceforge.net/packages/sed.htm) as well as [OpenSSL](https://www.openssl.org/) installed on your environment. If you don't have Sed or OpenSSL or you don't want to install it, use the instructions below as an alternative. Issue the following command:

```none
openssl s_client -connect google.com:443 -servername google.com:443
```

Save the output to a file called `public.crt.` Edit the the `public.crt` file so it contains only what is between the `BEGIN CERTIFCATE` and `END CERTIFICATE` lines. This is how your file should look like after you edited it:

```none
-----BEGIN CERTIFICATE-----
< Certificate content as fetched by the command line. 
Don't change this content, only remove what is before 
and after the BEGIN CERTIFICATE and END CERTIFICATE. 
That's what your Sed command is doing for you :-) >
-----END CERTIFICATE-----
```

## How to add a CERTIFICATE to a truststore using keytool

1. Run the `keytool -import -alias *ALIAS* -file *public.cert* -storetype *TYPE* -keystore *server.truststore*` command:

   ```cmd
   keytool -import -alias e.Go -file public.crt -storetype JKS -keystore server.truststore
   ```

2. If the specified truststore already exists, enter the existing password for that truststore, otherwise enter a new password:

   ```cmd
   Enter keystore password:  <password> 
   ```

3. Enter `yes` when prompted to trust the certificate:

   ```cmd
   Owner: CN=<user's name>, OU=<dept name>, O=<company name>, L=<city>, ST=<state>, C=<country>
   Issuer: CN=<user's name>, OU=<dept name>, O=<company name>, L=<city>, ST=<state>, C=<country>
   Serial number: 416d8636
   Valid from: Fri Jul 31 14:47:02 CDT 2009 until: Sat Jul 31 14:47:02 CDT 2010
   Certificate fingerprints: 
            MD5:  22:4C:A4:9D:2E:C8:CA:E8:81:5D:81:35:A1:84:78:2F
            SHA1: 05:FE:43:CC:EA:39:DC:1C:1E:40:26:45:B7:12:1C:B9:22:1E:64:63
   Trust this certificate? [no]:  yes
   ```

   Result

   The certificate in `public.cert` has been added to the new truststore named `server.truststore`.

# Troubleshooting

## Unable to find valid certification path to requested target

Exception in thread "main" javax.net.ssl.SSLHandshakeException: sun.security.validator.ValidatorException: PKIX path building failed: sun.security.provider.certpath.SunCertPathBuilderException: unable to find valid certification path to requested target

### Cause

Whenever Java attempts to connect to another application over SSL (e.g.: HTTPS, IMAPS, LDAPS), it will *only* be able to connect to that application if it can trust it. The way trust is handled in the Java world is that you have a keystore (typically `$JAVA_HOME/lib/security/cacerts`), also known as the truststore. This contains a list of all known Certificate Authority (CA) certificates, and Java will only trust certificates that are signed by one of those CAs or public certificates that exist within that keystore. For example, if we look at the certificate for Atlassian, we can see that the ***.atlassian.com** certificate has been signed by the intermediate certificates, **DigiCert High Assurance EV Root CA** and **DigiCert High Assurance CA-3**. These intermediate certificates have been signed by the root  **Entrust.net Secure Server CA** :

![](..\.image\2019-07-08-001.png)

These three certificates combined are referred to as the certificate chain, and, as they are all within the Java keystore (`cacerts`), Java will trust any certificates signed by them (in this case, ***.atlassian.com**). Alternatively, if the ***. atlassian.com**  certificate had been in the keystore, Java would also trust that site.

This problem is therefore caused by a certificate that is self-signed (a CA did not sign it) or a certificate chain that does not exist within the Java truststore. Java does not trust the certificate and fails to connect to the application.

### Solution

1. Make sure you have imported the public certificate of the target instance into the truststore according to the [Connecting to SSL Services](https://confluence.atlassian.com/kb/connecting-to-ssl-services-802171215.html) instructions.
2. Make sure any certificates have been imported into the correct truststore; you may have multiple JRE/JDKs. See [Installing Java](https://confluence.atlassian.com/jira/installing-java-185729673.html) for this.
3. Check to see that the correct truststore is in use. If `-Djavax.net.ssl.trustStore` has been configured, it will override the location of the default truststore, which will need to be checked.
4. Check if your Anti Virus tool has "SSL Scanning" blocking SSL/TLS. If it does, disable this feature or set exceptions for the target addresses (check the product documentation to see if this is possible.)
5. If connecting to a mail server, such as Exchange, ensure authentication allows plain text.
6. Verify that the target server is configured to serve SSL correctly. This can be done with the [SSL Server Test](https://www.ssllabs.com/ssltest/) tool.
7. If all else fails, your truststore might be out of date. Upgrade Java to the latest version supported by your application.

### References

- [Atlassian Knowledge Base / SSL/TLS Troubleshooting / Unable to connect to SSL services due to "PKIX Path Building Failed" error](https://confluence.atlassian.com/kb/unable-to-connect-to-ssl-services-due-to-pkix-path-building-failed-779355358.html)
- [Atlassian Knowledge Base / SSL/TLS Troubleshooting / How to import a public SSL certificate into a JVM](https://confluence.atlassian.com/kb/how-to-import-a-public-ssl-certificate-into-a-jvm-867025849.html)
- [Red Hat / Security Guide9.5 / Add a Certificate to a Truststore Using Keytool](https://access.redhat.com/documentation/en-us/red_hat_jboss_data_virtualization/6.2/html/security_guide/add_a_certificate_to_a_truststore_using_keytool)