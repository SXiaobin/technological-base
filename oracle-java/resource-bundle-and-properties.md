# ResourceBundle和properties 读取配置文件区别

java.util.ResourceBundle 和java.util.properties 读取配置文件区别

这两个类都是读取properties格式的文件的，而Properties同时还能用来写文件。   

Properties的处理方式是将其作为一个映射表,而且这个类表示了一个持久的属性集,他是继承HashTable这个类。ResourceBundle本质上也是一个映射，但是它提供了国际化的功能。假设电脑设置的地区是中国大陆，语言是中文，那么你向ResourceBundle（资源约束名称为base）获取abc变量的值的时候，ResourceBundle会先后搜索  

  base_zh_CN_abc.properties  

  base_zh_CN.properties  

  base_zh.properties  

  base.properties  

文件，直到找到abc为止。相应的，在英国就会去找base_en_GB_abc.properties等。 因此，你只需要提供不同语言的资源文件，而无需改变代码，就达到了国际化的目的。 另外，在.properties里面，不能直接使用中文之类文字，而是要通过native2ascii转乘\uxxxx这种形式 

​    附: 

   1.编码问题:

无论系统的默认编码是什么，ResourceBundle在读取properties文件时统一使用iso8859-1编码。因此，如果在默认编码为 GBK的系统中编写了包含中文的properties文件，经由ResourceBundle读入时，必须转换为GBK格式的编码，否则不能正确识别。

   2.用法:

ResourceBundle:

ResourceBundle conf= ResourceBundle.getBundle("config/fnconfig/fnlogin");

String value= conf.getString("key");

 

Properties:

Properties prop = new Properties();

try { InputStream is = getClass().getResourceAsStream("xmlPath.properties");

prop.load(is);

//或者直接prop.load(new FileInputStream("c:/xmlPath.properties"));

if (is != null) { is.close();

} } catch (Exception e) { System.out.println( "file " + "catalogPath.properties" + " not found!\n" + e); } String value= prop.getProperty("key").toString();