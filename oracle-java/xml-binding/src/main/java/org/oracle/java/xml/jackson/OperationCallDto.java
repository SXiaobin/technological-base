package org.oracle.java.xml.jackson;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

/**
 * The Data Transfer Object which we need send to Penta WS as request Body later.
 */
@Accessors(chain = true)
@Data
@JacksonXmlRootElement(localName = "OperationSpecifics")
public class OperationCallDto {
   @JacksonXmlProperty(isAttribute = true)
   private String xmlns = "http://www.psipenta.de/WebServiceDataSchema";

   @JacksonXmlProperty(localName = "Arguments")
   private Argument argument = new Argument();

   @Accessors(chain = true)
   @Data
   public static class Property {
      @JacksonXmlProperty(isAttribute = true)
      private String name;
      @JacksonXmlText
      private String value;
   }

   @Accessors(chain = true)
   @Data
   public class Argument {
      @JacksonXmlProperty(localName = "Property")
      @JacksonXmlElementWrapper(useWrapping = false)
      private List<Property> properties = new ArrayList<>();
   }
}
