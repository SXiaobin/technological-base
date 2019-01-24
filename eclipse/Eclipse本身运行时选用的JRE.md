Eclipse本身运行时选用的JRE：

1. 如果eclipse.ini中配置了-vm参数，那么则使用这个参数指定的JRE;
2. 否则就去查看eclipse安装目录下是否有JRE文件夹，如果有的话就使用这个JRE;
3. 否则的话就去系统中查找安装的JRE，如果还找不到的话就报错。

Preferences - Java - Compiler - JDK Compliance 设置 Java文件编译为 class文件时的编译 level，此处为全局设置。

具体针对不同项目可在此处点击 Configure Project Specific Settings..中选择项目为不同项目设置自己的编译级别；

或者选择项目选择其Java Compiler - Enable project specific settings 中设置。

原因：

compiler compliance level的含义说明：设置编译级别既 Eclipse compiler compliance level为较低版本，只是让编译器相信你的代码是兼容较低版本的，在编译时生成的bytecode(class)兼容较低版本。这样设置与你写代码时引用的JDK是没关系的，也就是说你在写代码时仍可以引用较高版本的API.（这样就可能导致错误）设置compiler compliance level为较低版本，这样的好处是当别人使用了较低版本的Jdk时也可以引用你写的编译后的代码。它可以保证编译后的class文件的版本一致性。但是，如果你的代码里面(java source)里面调用了较高版本jdk的API.那么即使设置了compiler compliance level为较低版本，在较低版本的JDK上运行你的代码也会报错。所以建议在写代码时引用的JDK，要跟你compiler compliance level设置的版本，是一致。

[java打jar包的异常一：could not find the main class，java.lang.UnsupportedClassVersionError: Bad version numb](http://blog.csdn.net/chenallen1025/article/details/7595478)

因为我们使用高版本的JDK编译的Java class 文件试图在较低版本的JVM上运行，所报的错误。因为，高版本的JDK生成的class文件使用的格式，可能与低版本的JDK的.class文件格式不同。这样，低版本的JVM无法解释执行这个.class文件，会抛出Couldnot find the main class.program will exit不支持的Class版本错误。 这个错误尤其在JDK5与JDK5之前的版本上表现明显。因为，JDK5在Java语法上作了不少增强，引入了一 些新的.class文件的元素，导致旧版本的JVM无法解释、执行.class文件。即使这个类并没有使用任何JDK5的新元素，但是JDK5生成 的.class文件，JDK1.4及其以前的JVM都无法辨认。