# xml-binding

本项目旨于研究、比较、记录当前较为流行的几种xml的解析技术方案

1. DOM
2. 1. JDOM
   2. DOM4J
3. SAX
4. Digester
5. JAXB
6. jackson

JAXB is not directly comparable to DOM and SAX. The Java DOM and SAX parsing APIs are lower-level APIs to parse XML documents, while JAXB (Java API for XML Binding) is a higher-level API for converting XML elements and attributes to a Java object hierarchy (and vice versa). Implementations of JAXB will most likely use a DOM or SAX parser behind the scenes to do the actual parsing of the XML input data.

Often you'll want to convert the content of an XML document into objects in your Java program. If this is what you want to do, then JAXB will probably be easier to use and you'll need to write less code than when you would be using the DOM or SAX parsing API.

Whether it's the right approach for your case depends on exactly what the functional and technical requirements are for your project.

XSD转换成功Java Bean 命令： xjc