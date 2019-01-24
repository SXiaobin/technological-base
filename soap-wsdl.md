# Web Service - WSDL

 WSDL (Web Services Description Language,Web服务描述语言)是一种XML Application，他将Web服务描述定义为一组服务访问点，客户端可以通过这些服务访问点对包含面向文档信息或面向过程调用的服务进行访问(类似远程过程调用)。WSDL首先对访问的操作和访问时使用的请求/响应消息进行抽象描述，然后将其绑定到具体的传输协议和消息格式上以最终定义具体部署的服务访问点。相关的具体部署的服务访问点通过组合就成为抽象的Web服务。 本文将详细讲解WSDL文档的结构，并分析每个元素的作用。

一：WSDL定义

​    WSDL是一个用于精确描述Web服务的文档，WSDL文档是一个遵循WSDL XML模式的XML文档。WSDL 文档将Web服务定义为服务访问点或端口的集合。在 WSDL 中，由于服务访问点和消息的抽象定义已从具体的服务部署或数据格式绑定中分离出来，因此可以对抽象定义进行再次使用：消息，指对交换数据的抽象描述；而端口类型，指操作的抽象集合。用于特定端口类型的具体协议和数据格式规范构成了可以再次使用的绑定。将Web访问地址与可再次使用的绑定相关联，可以定义一个端口，而端口的集合则定义为服务。

   一个WSDL文档通常包含7个重要的元素，即types、import、message、portType、operation、binding、service元素。这些元素嵌套在definitions元素中，definitions是WSDL文档的根元素。文章的下一部分将会详细介绍WSDL的基本结构。

二：WSDL的基本结构--概述

如第一部分最后描述的那样，一个基本的WSDL文档包含7个重要的元素。下面将分别介绍这几个元素以及他们的作用。

WSDL 文档在Web服务的定义中使用下列元素：

- **Types** - 数据类型定义的容器，它使用某种类型系统(一般地使用XML Schema中的类型系统)。
- **Message** - 通信消息的数据结构的抽象类型化定义。使用Types所定义的类型来定义整个消息的数据结构。
- **Operation** - 对服务中所支持的操作的抽象描述，一般单个Operation描述了一个访问入口的请求/响应消息对。
- **PortType** - 对于某个访问入口点类型所支持的操作的抽象集合，这些操作可以由一个或多个服务访问点来支持。
- **Binding** - 特定端口类型的具体协议和数据格式规范的绑定。
- **Port** - 定义为协议/数据格式绑定与具体Web访问地址组合的单个服务访问点。
- **Service**- 相关服务访问点的集合。

 可以参考下图来理解一下WSDL的文档结构图：

![WSDL文档元素的结构图](C:\Azrial\technological-base\.image\soap-wsdl-文档结构图.gif)

 

WSDL的xml schema可以参照如下网址：<http://schemas.xmlsoap.org/wsdl/>

**从另一片文章中截取的片段，更加直观易理解**

下面的图中，箭头连接符代表文档不同栏之间的关系。点和箭头代表了引用或使用

关系。双箭头代表"修改"关系。3-D 的箭头代表了包含关系。这样，各Messages 栏使

用Types 栏的定义，PortTypes 栏使用Messages 栏的定义；Bindings 栏引用了

PortTypes 栏，Services 栏引用Bindings 栏，PortTypes 和Bindings 栏包含了operation

元素，而Services 栏包含了port 元素。PortTypes 栏里的operation 元素由Bindings

栏里的operation 元素进一步修改或描述。

![img](C:\Azrial\technological-base\.image\soap-wsdl-abstract-definition.gif)

三：WSDL的基本结构--详述

本节将通过一个例子详细描述WSDL文档每个元素的作用。下面一个例子是一个简单的WSDL文档的内容，该文档的产生可以参见我的另外一篇文章：[xfire开发实例--HelloWorld篇](http://blog.csdn.net/juxtapose/archive/2007/09/10/1779849.aspx) (<http://blog.csdn.net/juxtapose/archive/2007/09/10/1779849.aspx>)。

一个简单的Web Service的WSDL文档，该服务支持名为sayHello的唯一操作，该操作通过在http上运行SOAP协议来实现的。该请求接受一个字符串name，经过处理后返回一个简单的字符串。文档如下：

<?xml version="1.0" encoding="UTF-8" ?>

<wsdl:definitions

​    targetNamespace="http://com.liuxiang.xfireDemo/HelloService"

​    xmlns:tns="http://com.liuxiang.xfireDemo/HelloService"

​    xmlns:wsdlsoap="http://schemas.xmlsoap.org/wsdl/soap/"

​    xmlns:soap12="http://www.w3.org/2003/05/soap-envelope"

​    xmlns:xsd="http://www.w3.org/2001/XMLSchema"

​    xmlns:soapenc11="http://schemas.xmlsoap.org/soap/encoding/"

​    xmlns:soapenc12="http://www.w3.org/2003/05/soap-encoding"

​    xmlns:soap11="http://schemas.xmlsoap.org/soap/envelope/"

​    xmlns:wsdl="http://schemas.xmlsoap.org/wsdl/">

​    <wsdl:types>

​        <xsd:schema xmlns:xsd="http://www.w3.org/2001/XMLSchema"

​            attributeFormDefault="qualified" elementFormDefault="qualified"

​            targetNamespace="http://com.liuxiang.xfireDemo/HelloService">

​            <xsd:element name="sayHello">

​                <xsd:complexType>

​                    <xsd:sequence>

​                        <xsd:element maxOccurs="1" minOccurs="1"

​                            name="name" nillable="true" type="xsd:string" />

​                    </xsd:sequence>

​                </xsd:complexType>

​            </xsd:element>

​            <xsd:element name="sayHelloResponse">

​                <xsd:complexType>

​                    <xsd:sequence>

​                        <xsd:element maxOccurs="1" minOccurs="1"

​                            name="out" nillable="true" type="xsd:string" />

​                    </xsd:sequence>

​                </xsd:complexType>

​            </xsd:element>

​        </xsd:schema>

​    </wsdl:types>

​    <wsdl:message name="sayHelloResponse">

​        <wsdl:part name="parameters" element="tns:sayHelloResponse" />

​    </wsdl:message>

​    <wsdl:message name="sayHelloRequest">

​        <wsdl:part name="parameters" element="tns:sayHello" />

​    </wsdl:message>

​    <wsdl:portType name="HelloServicePortType">

​        <wsdl:operation name="sayHello">

​            <wsdl:input name="sayHelloRequest"

​                message="tns:sayHelloRequest" />

​            <wsdl:output name="sayHelloResponse"

​                message="tns:sayHelloResponse" />

​        </wsdl:operation>

​    </wsdl:portType>

​    <wsdl:binding name="HelloServiceHttpBinding"

​        type="tns:HelloServicePortType">

​        <wsdlsoap:binding style="document"

​            transport="http://schemas.xmlsoap.org/soap/http" />

​        <wsdl:operation name="sayHello">

​            <wsdlsoap:operation soapAction="" />

​            <wsdl:input name="sayHelloRequest">

​                <wsdlsoap:body use="literal" />

​            </wsdl:input>

​            <wsdl:output name="sayHelloResponse">

​                <wsdlsoap:body use="literal" />

​            </wsdl:output>

​        </wsdl:operation>

​    </wsdl:binding>

​    <wsdl:service name="HelloService">

​        <wsdl:port name="HelloServiceHttpPort"

​            binding="tns:HelloServiceHttpBinding">

​            <wsdlsoap:address

​                location="http://localhost:8080/xfire/services/HelloService" />

​        </wsdl:port>

​    </wsdl:service>

</wsdl:definitions>

♦ types元素使用XML模式语言声明在WSDL文档中的其他位置使用的复杂数据类型与元素；

♦ import元素类似于XML模式文档中的import元素，用于从其他WSDL文档中导入WSDL定义；

♦ message元素使用在WSDL文档的type元素中定义或在import元素引用的外部WSDL文档中定义的XML模式的内置类型、复杂类型或元素描述了消息的有效负载；

♦ portType元素和operation元素描述了Web服务的接口并定义了他的方法。portType元素和operation元素类似于java接口和接口中定义的方法声明。operation元素使用一个或者多个message类型来定义他的输入和输出的有效负载；

♦ Binding元素将portType元素和operation元素赋给一个特殊的协议和编码样式；

♦ service元素负责将Internet地址赋给一个具体的绑定；

1、definitions元素

所有的WSDL文档的根元素均是definitions元素。该元素封装了整个文档，同时通过其name提供了一个WSDL文档。除了提供一个命名空间外，该元素没有其他作用，故不作详细描述。

下面的代码是一个definitions元素的结构：

<wsdl:definitions

​    targetNamespace="http://com.liuxiang.xfireDemo/HelloService"

​    xmlns:tns="http://com.liuxiang.xfireDemo/HelloService"

​    xmlns:wsdlsoap="http://schemas.xmlsoap.org/wsdl/soap/"

​    xmlns:soap12="http://www.w3.org/2003/05/soap-envelope"

​    xmlns:xsd="http://www.w3.org/2001/XMLSchema"

​    xmlns:soapenc11="http://schemas.xmlsoap.org/soap/encoding/"

​    xmlns:soapenc12="http://www.w3.org/2003/05/soap-encoding"

​    xmlns:soap11="http://schemas.xmlsoap.org/soap/envelope/"

​    xmlns:wsdl="http://schemas.xmlsoap.org/wsdl/">

</wsdl:definitions>

2、types元素

 WSDL采用了W3C XML模式内置类型作为其基本类型系统。types元素用作一个容器，用于定义XML模式内置类型中没有描述的各种数据类型。当声明消息部分的有效负载时，消息定义使用了在types元素中定义的数据类型和元素。在本文的WSDL文档中的types定义：

 

<wsdl:types>

​        <xsd:schema xmlns:xsd="http://www.w3.org/2001/XMLSchema"

​            attributeFormDefault="qualified" elementFormDefault="qualified"

​            targetNamespace="http://com.liuxiang.xfireDemo/HelloService">

​            <xsd:element name="sayHello">

​                <xsd:complexType>

​                    <xsd:sequence>

​                        <xsd:element maxOccurs="1" minOccurs="1"

​                            name="name" nillable="true" type="xsd:string" />

​                    </xsd:sequence>

​                </xsd:complexType>

​            </xsd:element>

​            <xsd:element name="sayHelloResponse">

​                <xsd:complexType>

​                    <xsd:sequence>

​                        <xsd:element maxOccurs="1" minOccurs="1"

​                            name="out" nillable="true" type="xsd:string" />

​                    </xsd:sequence>

​                </xsd:complexType>

​            </xsd:element>

​        </xsd:schema>

​    </wsdl:types>

上面是数据定义部分，该部分定义了两个元素，一个是sayHello，一个是sayHelloResponse：

sayHello：定义了一个复杂类型，仅仅包含一个简单的字符串，将来用来描述操作的参入传入部分；

sayHelloResponse：定义了一个复杂类型，仅仅包含一个简单的字符串，将来用来描述操作的返回值；

3、import元素

import元素使得可以在当前的WSDL文档中使用其他WSDL文档中指定的命名空间中的定义元素。本例子中没有使用import元素。通常在用户希望模块化WSDL文档的时候，该功能是非常有效果的。

import的格式如下：

![img](file:///C:/Users/xsu.PSI/AppData/Local/Packages/Microsoft.Office.OneNote_8wekyb3d8bbwe/TempState/msohtmlclip/clip_image003.gif)

<wsdl:import namespace="http://xxx.xxx.xxx/xxx/xxx" location="http://xxx.xxx.xxx/xxx/xxx.wsdl"/>

必须有namespace属性和location属性：

namespace属性：值必须与正导入的WSDL文档中声明的targetNamespace相匹配；

location属性：必须指向一个实际的WSDL文档，并且该文档不能为空。

4、message元素

message元素描述了Web服务使用消息的有效负载。message元素可以描述输出或者接受消息的有效负载；还可以描述SOAP文件头和错误detail元素的内容。定义message元素的方式取决于使用RPC样式还是文档样式的消息传递。在本文中的message元素的定义，本文档使用了采用文档样式的消息传递：

<wsdl:message name="sayHelloResponse">

​        <wsdl:part name="parameters" element="tns:sayHelloResponse" />

​    </wsdl:message>

​    <wsdl:message name="sayHelloRequest">

​        <wsdl:part name="parameters" element="tns:sayHello" />

​    </wsdl:message>

该部分是消息格式的抽象定义：定义了两个消息sayHelloResponse和sayHelloRequest：

sayHelloRequest：sayHello操作的请求消息格式，由一个消息片断组成，名字为parameters,元素是我们前面定义的types中的元素；

sayHelloResponse：sayHello操作的响应消息格式，由一个消息片断组成，名字为parameters,元素是我们前面定义的types中的元素；

 如果采用RPC样式的消息传递，只需要将文档中的element元素应以修改为type即可。

5、portType元素

portType元素定义了Web服务的抽象接口。该接口有点类似Java的接口，都是定义了一个抽象类型和方法，没有定义实现。在WSDL中，portType元素是由binding和service元素来实现的，这两个元素用来说明Web服务实现使用的Internet协议、编码方案以及Internet地址。

一个portType中可以定义多个operation，一个operation可以看作是一个方法，本文中WSDL文档的定义：

​    <wsdl:portType name="HelloServicePortType">

​        <wsdl:operation name="sayHello">

​            <wsdl:input name="sayHelloRequest"

​                message="tns:sayHelloRequest" />

​            <wsdl:output name="sayHelloResponse"

​                message="tns:sayHelloResponse" />

​        </wsdl:operation>

​    </wsdl:portType>

portType定义了服务的调用模式的类型，这里包含一个操作sayHello方法，同时包含input和output表明该操作是一个请求／响应模式，请求消息是前面定义的sayHelloRequest，响应消息是前面定义的sayHelloResponse。input表示传递到Web服务的有效负载，output消息表示传递给客户的有效负载。

６、binding

binding元素将一个抽象portType映射到一组具体协议(SOAO和HTTP)、消息传递样式、编码样式。通常binding元素与协议专有的元素和在一起使用，本文中的例子：

​    <wsdl:binding name="HelloServiceHttpBinding"

​        type="tns:HelloServicePortType">

​        <wsdlsoap:binding style="document"

​            transport="http://schemas.xmlsoap.org/soap/http" />

​        <wsdl:operation name="sayHello">

​            <wsdlsoap:operation soapAction="" />

​            <wsdl:input name="sayHelloRequest">

​                <wsdlsoap:body use="literal" />

​            </wsdl:input>

​            <wsdl:output name="sayHelloResponse">

​                <wsdlsoap:body use="literal" />

​            </wsdl:output>

​        </wsdl:operation>

​    </wsdl:binding>

这部分将服务访问点的抽象定义与SOAP HTTP绑定，描述如何通过SOAP/HTTP来访问按照前面描述的访问入口点类型部署的访问入口。其中规定了在具体SOAP调用时，应当使用的soapAction是""。

具体的使用需要参考特定协议定义的元素。

７、service元素和port元素

service元素包含一个或者多个port元素，其中每个port元素表示一个不同的Web服务。port元素将URL赋给一个特定的binding，甚至可以使两个或者多个port元素将不同的URL赋值给相同的binding。文档中的例子：

​    <wsdl:service name="HelloService">

​        <wsdl:port name="HelloServiceHttpPort"

​            binding="tns:HelloServiceHttpBinding">

​            <wsdlsoap:address

​                location="http://localhost:8080/xfire/services/HelloService" />

​        </wsdl:port>

​    </wsdl:service>

这部分是具体的Web服务的定义，在这个名为HelloService的Web服务中，提供了一个服务访问入口，访问地址是<http://localhost:8080/xfire/services/HelloService>，使用的消息模式是由前面的binding所定义的。