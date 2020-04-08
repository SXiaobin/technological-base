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

## [Why is my Java Charset.defaultCharset() GBK and not Unicode?](https://stackoverflow.com/questions/16602900/why-is-my-java-charset-defaultcharset-gbk-and-not-unicode)

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

## Java: cannot find symbol

The project could able be complied in pipeline but in Intellij, it reports this error.

### Cause

Unknow

### Solution A

1. Intellij - Maven - right click on your project - Unlink Maven Projects.
2. Intellij - Project Structure - Modules,  import the module again.
3. Intellij - Build - Rebuild your project

## [Maven can't find my local artifacts](https://stackoverflow.com/questions/16866978/maven-cant-find-my-local-artifacts)

本地中央仓库的类库存在，但项目无法找到该类库。错误信息为：

`Cannot resolve jta:jta:1.0.1`

### Cause

主要是因为Maven考虑到同名类库的冲突，使用`_maven.repositories`记录类库来源起到类似于命名空间的作用。而Maven 3.x版本存在bug，导致离线模式该功能无法正常使用。

> Prior to Maven 3.0.x, Maven did not track the origin of files in the local repository.
>
> This could result in build issues, especially if you were building something that listed the (now dead) very borked java.net2 repository... Not only did that repository change released artifacts (extremely bad and evil practice) but it also published artifacts at the same coordinates as artifacts on central but with different content (unbelievably evil)
>
> So you could have the build work (because you had commons-io:commons-io:2.0 from central) wipe your local repo and the build fails (because you now get commons-io:commons-io:2.0 from java.net2 which was a completely different artifact with different dependencies in the pom) *or* vice versa.
>
> The above situation is one of the drivers for using a maven repository manager, because that allows you to control the subset of a repository that you expose downstream and the order in which artifacts are resolved from multiple repositories (usually referred to as routing rules)
>
> In any case, when maven switched to Aether as the repository access layer, the decision was made to start tracking where artifacts come from.
>
> So with Maven 3.0.x, when an artifact is downloaded from a repository, maven leaves a `_maven.repositories` file to record where the file was resolved from. If you are building a project and the effective list of repositories does not include the location that the artifact was resolved from, then Maven decides that it is as if the artifact was not in the cache, and will seek to re-resolve the artifact...
>
> There are a number of bugs in 3.0.x though... The most critical being how `offline` is handled... Namely: when offline, maven 3.0.x thinks there are no repositories, so will always find a mismatch against the `_maven.repositories` file!!!
>
> The workaround for Maven 3.0.x is to delete these files from your local cache, eg
>
> ```
> $ find ~/.m2/repository -name _maven.repositories -exec rm -v {} \;
> ```
>
> The side effect is that you loose the protections that Maven 3.0.x is trying to provide.
>
> The good news is that Maven 3.1 will have the required fix (if we can ever get our act together and get a release out the door)
>
> With Maven 3.1 when in offline mode the `_maven.repositories` file is (semi-)ignored, and there is also an option to ignore that file for online builds (referred to as legacy mode)
>
> At this point in time (June 1st 2013) the 4th attempt to cut a release that meets the legal and testing requirements is in progress... So, assuming that the 4th time is lucky, I would *hope* to see 3.1.0-alpha-1 released in 3-4 days time... But it could be longer given that we want to give the changes in 3.1 enough time to soak to ensure uses builds don't break (there was a change in an API exposed (by accident-ish - the API is needed by the site and dependency plugin) that plugin authors have depended on (even though they shouldn't have) so there is potential, though we think we have all the bases covered)
>
> Hope that answers your question (and maybe a few more you didn't know you had ;-) )

### Solution A

移除本地中央仓库某类库下的`_maven.repositories`，但这样就无法再使用maven类库保护功能。

# 第三方API汇总信息

## [Lombok Project]( https://projectlombok.org/)

通过注解的形式实现 Getter and setter等相关方法

## JOOQ

数据库API。予以解决方言问题

## [javatuples](https://www.javatuples.org/index.html)

javatuples 1.2 is an evolutive release. Now tuples are comparable --and sortable-- and new static methods have been added for obtaining tuples from iterables, even if these iterables hold more elements than needed by the new tuple.