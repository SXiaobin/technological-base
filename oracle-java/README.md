# How Tos

## How to remote debug a java application

In Java 8 the JDK supports a JAVA_TOOL_OPTIONS environment variable so to enable the debugger for any Java application. 

```cmd
-agentlib:jdwp=transport=dt_socket,address=8000,server=y,suspend=n
```

Below is one example how we need to set when we run a jar

```cmd
 java -Xmx256m -agentlib:jdwp=transport=dt_socket,address=8000,server=y,suspend=n -jar /service-workflowhelper2.jar 
```

Then start a remote debug session connecting to localhost:8000

# Trouble Shooting

## Problem - [Why is my Java Charset.defaultCharset() GBK and not Unicode?](https://stackoverflow.com/questions/16602900/why-is-my-java-charset-defaultcharset-gbk-and-not-unicode)

### Solution

> What is JAVA default charset?

It's picked up from the default set in your OS. This could be Windows-1252-???

> Is it Unicode?

This is not a charset. A charset defines how to encode characters as bytes.

> How JAVA default charset interact with programmers?

It's the default used when you don't specify a charset.

> For example, if JAVA use Unicode, then a string "abc" cannot be encoded into other charset since they are different from Unicode like charset for Russia, Frence etc, since they are totally different encoding method.

Internally Java uses UTF-16 but you don't need to know that. This has no issues with most languages except some Chinese dialects require the use of code points.

> What does Charset.defaultCharset() returns?

It does what it appears to do. You can confirm this by reading the javadoc for this method.

> Does it return my WIN8's default charset?

Because that is what it is supposed to do. You only have a problem if your OS's character set cannot be mapped into Java or is not correctly mapped into Java. If it is the same, everything is fine.

> How Charset.defaultCharset() return GBK. I didn't set anything in my WIN8 related default charset except the one for "language for non-Unicode Programs" in control panel.

It is this because Java thinks you set this for Windows. To correct this, you must have the correct character set in Windows.

> If I declare a String in java like: String str = "abc";, I don't know the process of charset/encoding.

For the purposes of this question, there isn't any encoding involved. There is only characters they don't need to be encoded to make characters because they are already characters.

> How the keyboard translates my key button into Java Unicode charset?

The keyboard doesn't. It only knows which keys you pressed. The OS turns these keys into characters.

> The String str is stored in my .java source code file. What is the charset to store java source code?

That is determined by the editor which does the storing. Most likely it will be the OS default again, or if you change it you might make it UTF-8.

# 第三方API汇总信息

## [Lombok Project]( https://projectlombok.org/)

通过注解的形式实现 Getter and setter等相关方法

## JOOQ

数据库API。予以解决方言问题

## [javatuples](https://www.javatuples.org/index.html)

javatuples 1.2 is an evolutive release. Now tuples are comparable --and sortable-- and new static methods have been added for obtaining tuples from iterables, even if these iterables hold more elements than needed by the new tuple.