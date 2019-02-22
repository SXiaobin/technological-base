# XML Binding Solutions

本项目旨于研究、比较、记录当前较为流行的几种xml的解析技术方案。

XSD转换成功Java Bean 命令： xjc

## lower-level APIs

### DOM

### JDOM

### DOM4J

## higher-level API

### JAXB

JAXB is not directly comparable to DOM and SAX. The Java DOM and SAX parsing APIs are lower-level APIs to parse XML documents, while JAXB (Java API for XML Binding) is a higher-level API for converting XML elements and attributes to a Java object hierarchy (and vice versa). Implementations of JAXB will most likely use a DOM or SAX parser behind the scenes to do the actual parsing of the XML input data.

Often you'll want to convert the content of an XML document into objects in your Java program. If this is what you want to do, then JAXB will probably be easier to use and you'll need to write less code than when you would be using the DOM or SAX parsing API.

Whether it's the right approach for your case depends on exactly what the functional and technical requirements are for your project.

### jackson

[Github project home page](https://github.com/FasterXML/jackson-dataformat-xml)

The related examples is underpackge: org.oracle.java.xml.jackson

# Troubleshooting

## Unnecessary Namespace

I'm trying to parse my object to XML using jackson-dataformat-xml and, when i set the root namespace and parse the file, all properties of my object in the XML gives a empty namespace xmlns="". On jackson's github docs, is advise to use woodstox instead stax XML implementation to solve this but, the behavior still exists.

This is my pojo:

```java
@JacksonXmlRootElement(namespace = "https://www.google.com.br")
public class Cliente implements Serializable {
 // Omitted
}
```

And my parse code:

```java
Cliente cliente = new Cliente();
cliente.setId(new Long(1));
cliente.setNome("Tiago Cassio".toUpperCase());
cliente.setSobrenome("da Conceicao".toUpperCase());
Endereco endereco = new Endereco();
endereco.setId(new Long(1));
endereco.setLogradouro("blablabla");
endereco.setNumero("999");
endereco.setCep("99999");
endereco.setBairro("blablabla");
cliente.setEndereco(endereco);
ObjectMapper mapper = new XmlMapper();
System.out.println(mapper.writeValueAsString(cliente));
```

This is the XML generated:

```xml
<Cliente xmlns="https://www.google.com.br">
    <id xmlns="">1</id>
    <nome xmlns="">TIAGO CASSIO</nome>
    <sobrenome xmlns="">DA CONCEICAO</sobrenome>
    <endereco xmlns="">
        <id>1</id>
        <logradouro>blablabla</logradouro>
        <numero>999</numero>
        <cep>99999</cep>
        <bairro>blablabla</bairro>
    </endereco>
</Cliente>
```

Any idea where is the problem? My project is under a Spring boot version 1.3.0.M5. Thanks for all.

### Case

Github project jackson-dataformat-xml explicitly uses Woodstox as XML library. But maybe normally we are using locally is Stax.

> Also: you usually also want to make sure that XML library in use is [Woodstox](https://github.com/FasterXML/woodstox) since it is not only faster than Stax implementation JDK provides, but also works better and avoids some known issues like adding unnecessary namespace prefixes.

 *-- from Github project README*

### Resolution

#### Use Woodstox as XML library

<!--Actually this resolution doesn't work properly now. Keeping it here for documnet.-->

1. Add dependency to the pom.

   ```xml
   <dependency>
       <groupId>org.codehaus.woodstox</groupId>
       <artifactId>woodstox-core-asl</artifactId>
       <version>4.1.2</version>
   </dependency>
   ```

2. Use the following lines will update your XmlMapper to use WoodStox and use this mapper to deserialize.

   ```java
   XmlFactory factory = new XmlFactory(new WstxInputFactory(), new WstxOutputFactory());
   XmlMapper xmlMapper = new XmlMapper(factory);
   ```

#### Treat namespace attribute as a normal attribute

1. remove nameSpace from @JacksonXmlRootElement

2. add necessary field:

  ```java
  @JacksonXmlProperty(isAttribute=true, localName = "xmlns")
  private String xmlns;
  ```

This resolution, you could find the related code in org.oracle.java.xml.jackson.OperationCallDto

### Reference

[Unnecessary Namespace in Jackson XML 2.6.1 + Woodstox 4.4.1](https://stackoverflow.com/questions/33103274/unnecessary-namespace-in-jackson-xml-2-6-1-woodstox-4-4-1) *- from stackoverflow*

[Support for providing default namespaces for a class / package](https://github.com/FasterXML/jackson-dataformat-xml/issues/18) - *from  Github support*

[unnecessary xmlns="" in the root element](https://github.com/FasterXML/jackson-dataformat-xml/issues/32)  *- from Github support*