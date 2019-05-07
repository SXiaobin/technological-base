# RESTful API Authentication Basics

28 NOVEMBER 2016 on [REST API](https://blog.restcase.com/tag/rest-api/), [Architecture](https://blog.restcase.com/tag/architecture/), [Guidelines](https://blog.restcase.com/tag/guidelines/), [API](https://blog.restcase.com/tag/api/), [REST API Security](https://blog.restcase.com/tag/rest-api-security/)

Almost every REST API must have some sort of authentication. One of the most common headers is call Authorization. Wait a minute, we are talking about authentication but why the [Authorization header](https://en.wikipedia.org/wiki/Basic_access_authentication)?

##### Authentication vs. Authorization

The distinction between authentication and authorization is important in understanding how RESTful APIs are working and why connection attempts are either accepted or denied:

- **Authentication** is the verification of the credentials of the connection attempt. This process consists of sending the credentials from the remote access client to the remote access server in an either plaintext or encrypted form by using an authentication protocol.
- **Authorization** is the verification that the connection attempt is allowed. Authorization occurs after successful authentication.

**In other words:** Authentication is stating that you are who are you are and Authorization is asking if you have access to a certain resource.

I know that it is a bit confusing that in REST APIs we are using the Authorization header for doing Authentication (or both) but if we remember that when calling an API we are requesting an access to certain resource it means that the server should know whether it should give access to that resource or not, hence when [developing and designing RESTful API](http://www.restcase.com/)Authorization header sounds just fine.

##### Basic Authentication

The most simple way to deal with authentication is to use HTTP basic authentication. We use a special HTTP header where we add 'username:password' encoded in base64.

```
GET / HTTP/1.1
Host: example.org
Authorization: Basic Zm9vOmJhcg==
```

Note that even though your credentials are encoded, they are not encrypted! It is very easy to retrieve the username and password from a basic authentication. Do not use this authentication scheme on plain HTTP, but only through SSL/TLS.

![HTTP Basic Authentication](https://blog.restcase.com/content/images/2016/11/webapi_auth04.png)

###### HMAC

One of the downsides of basic authentication is that we need to send over the password on every request. Also, it does not safeguard against tampering of headers or body.

Another way is to use HMAC ([hash based message authentication](https://en.wikipedia.org/wiki/Hash-based_message_authentication_code)). Instead of having passwords that need to be sent over, we actually send a hashed version of the password, together with more information. Let's assume we have the following credentials: username "username", password "secret".

Suppose we try to access a protected resource:

```
/users/username/account
```

First, we need to fetch all the information we need, and concatenate this.

```
GET+/users/username/account
```

Here, we just concatenate the HTTP verb and the actual URL. We could add other information as well, like the current timestamp, a random number, or the md5 of the message body in order to prevent tampering of the body, or prevent replay attacks. Next, we generate a hmac:

```
digest = base64encode(hmac("sha256", "secret",     "GET+/users/username/account"))
```

This digest we can send over as a HTTP header:

```
GET /users/username/account HTTP/1.1
Host: example.org
Authentication: hmac username:[digest]
```

Right now, the server knows the user "username" tries to access the resource. The server can generate the digest as well, since it has all information.

Please note that the "password" is not encrypted on the server, as the server needs to know the actual value. This is why te name "secret" is preffered and not a "password".

Even if a hacker was listening in on the conversation, they could not use the authentication information to POST data to user's account details, or look at some other users accounts, or any other URL, as this would change the digest and the hacker does not have the secret that both the server and client has.

However, the hacker could access user's account whenever it wants since it doesn't change the digest. This is why many times more information is send over, like the current time, and a nonce:

```
digest = base64encode(hmac("sha256", "secret", "GET+/users/username/account+20apr201312:59:24+123456"))
```

We added two extra pieces of information. The current date and a number that we only use once (nonce)

```
GET /users/username/account HTTP/1.1
Host: example.org
Authentication: hmac username:123456:[digest]
Date: 20 apr 2013 12:59:24
```

The server can reconstruct the digest again, since the client sends over the nonce and date. When the date is not in a certain range of the current servers time (say, 10 minutes), the server can ignore the message, as it probably is a replay of an earlier send message (note: either that, or the server or clients time is wrong. This is a common issue when dealing with time-limited authentications!).

The nonce is a number we only use once. If we want to access the same resource again, we MUST change this number. This means that every time we access a resource, the nonce will be different, and thus the digest will be different, even if we access the resource in the same second. This way we are sure that no replay attacks can be done. Each request is only valid once, and only once.

##### OAuth 2.0 or OAuth 1.0

![OAuth 2.0 or OAuth 1.0](https://blog.restcase.com/content/images/2016/11/2-oauth1-vs-oauth2-v1.png)

###### A Little History

In December 2007, OAuth 1.0 addressed delegation with a framework based on digital signatures. It was secure and it was strong. Major players began to adopt it. Google began OAuth 1.0 support in 2008. By 2010, Twitter forced all third-party apps to use their OAuth 1.0 implementation.

However, OAuth 1.0 required crypto-implementation and crypto-interoperability. While secure, it was a challenge for many developers to implement.

Then came OAuth 2.0 in October 2012.

Building a secure OAuth solution is no easy challenge. Large enterprises joined the OAuth standard body and influenced it in many ways. While OAuth 2.0 is much easier to implement than OAuth 1.0 with its crypto underpinnings, the new version contains many compromises at the security level.

However, support for non-browser implementations and a clear separation of resource delivery and authorization helped make the new standard more usable for large enterprises and more.

In many cases, it is no longer feasible to use OAuth 1.0 as a client-side implementer. For example, Google moved away from OAuth 1.0 in April 2012, and no longer permits the use of OAuth 1.0. However, Twitter still fully supports OAuth 1.0. (for more information - <https://dev.twitter.com/oauth>)

It is very rare to see new authorization server implementations of OAuth 1.0. However, you can still consider OAuth 1.0 if your resource provider still supports it (and has committed to continue supporting it), you have developers with good experience in cryptography, and you have good key management capabilities.

These are a lot of “ifs,” and OAuth 2.0 is almost always the right choice today. If your desire is to use OAuth with proper cryptography, the trend is more and more to use OAuth 2.0 with cryptographic extensions. If you are [designing and developing a new API](http://www.restcase.com/), OAuth 2.0 is your choice!

Still wondering what to do? Compare the security properties of both versions and decide which is right for your implementation.

![OAuth Way Of Work](https://blog.restcase.com/content/images/2016/11/oauth-policy1.png)

##### OAuth 1.0

- **Transport-Independent**: Security is not delegated to HTTPS/TLS.
- **Founded in cryptography, especially digital signatures** : Digital signatures are used to prove the integrity and authenticity of a message. Digital signatures can ensure that a certain message was sent from a specific source and that the message and signature were not tampered with in any way. A signed message is tied to its origin. It cannot be tampered with or copied to another source, but client-side implementations can be especially complex.
- **Messages are each individually cryptographically signed** : If a single message within the communication is constructed or signed improperly, the entire transaction will be invalidated
- **Basic Signature Workflow**.

###### Example workflow:

1. Client application registers with provider, such as Twitter.
2. Twitter provides client with a “consumer secret” unique to that application.
3. Client app signs all OAuth requests to Twitter with its unique “consumer secret.”
4. If any of the OAuth request is malformed, missing data, or signed improperly, the request will be rejected.

**Note**: Some use the OAuth 1.0 scope parameter to carry authorization/entitlement in addition to the token; that can be a useful architecture consideration.

##### OAuth 2.0

- **Transport-Dependent** : Most security defenses are delegated to HTTPS/TLS. A typo, an improper TLS configuration, a failure to properly validate a certificate, or vulnerabilities in an underlying library can lead to a man-in-the-middle (MiTM) attack, compromising all OAuth communications.
- **Centered around bearer tokens** : These are easy for integration but not great for security. Bearer tokens do not provide internal security mechanisms. They can be copied or stolen but are easier to implement.
- **Easier** : OAuth 2.0 is much more usable, but much more difficult to build securely.
- **Flexible** : OAuth 1.0 only handled web workflows, but OAuth 2.0 considers non-web clients as well.
- **Better separation of duties** : Handling resource requests and handling user authorization can be decoupled in OAuth 2.0.
- **Basic Signature Workflow**.

###### Example workflow:

1. Client application registers with provider, such as Twitter.
2. Twitter provides client with a “client secret” unique to that application.
3. Client application includes “client secret” with every request.
4. If any of the OAuth request is malformed, missing data, or contains the wrong secret, the request will be rejected.

###### See also:

- [RFC 7235 - Access Authentication Framework](http://tools.ietf.org/html/rfc7235#section-2)
- [RFC 2617 - HTTP Authentication: Basic and Digest Access Authentication](http://tools.ietf.org/html/rfc2617)
- [RFC 6749 - OAuth2 standard](http://tools.ietf.org/html/rfc6749)

##### Summary

Please keep in mind that Basic authentication and OAuth versions MUST be protected through SSL/TLS. They should not be used over plain HTTP.

Authentication is stating that you are who are you are and Authorization is asking if you have access to a certain resource. When working with REST APIs you must remember to consider security from the start.

RESTful API often use GET (read), POST (create), PUT (replace/update) and DELETE (to delete a record). Not all of these are valid choices for every single resource collection, user, or action. Make sure the incoming HTTP method is valid for the session token/API key and associated resource collection, action, and record.

For example, if you have an RESTful API for a library, it's not okay to allow anonymous users to DELETE book catalog entries, but it's fine for them to GET a book catalog entry. On the other hand, for the librarian, both of these are valid uses.