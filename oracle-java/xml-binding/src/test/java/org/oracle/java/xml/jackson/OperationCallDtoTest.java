package org.oracle.java.xml.jackson;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class OperationCallDtoTest {

   @Test
   public void whenJavaSerializedToXmlStr_thenCorrect() throws JsonProcessingException {
      // Setup
      String expected = "<OperationSpecifics xmlns=\"http://www.psipenta.de/WebServiceDataSchema\">\n" +
              "  <Arguments>\n" +
              "    <Property name=\"FUNKTIONS_KZ\">BU</Property>\n" +
              "    <Property name=\"VORGANGS_KZ\">F</Property>\n" +
              "    <Property name=\"RETROGRAD_BUCHEN\">M</Property>\n" +
              "    <Property name=\"KENN_LAGER_KOSTENTRAEGER\">L</Property>\n" +
              "    <Property name=\"LAGERORT\">100</Property>\n" +
              "    <Property name=\"RM_GUT_MENGE\">1</Property>\n" +
              "  </Arguments>\n" +
              "</OperationSpecifics>\n";
      List<OperationCallDto.Property> properties = new ArrayList<>();
      properties.add(new OperationCallDto.Property().setName("FUNKTIONS_KZ").setValue("BU"));
      properties.add(new OperationCallDto.Property().setName("VORGANGS_KZ").setValue("F"));
      properties.add(new OperationCallDto.Property().setName("RETROGRAD_BUCHEN").setValue("M"));
      properties.add(new OperationCallDto.Property().setName("KENN_LAGER_KOSTENTRAEGER").setValue("L"));
      properties.add(new OperationCallDto.Property().setName("LAGERORT").setValue("100"));
      properties.add(new OperationCallDto.Property().setName("RM_GUT_MENGE").setValue("1"));

      OperationCallDto operationCallDto = new OperationCallDto();
      operationCallDto.getArgument().setProperties(properties);

      XmlMapper xmlMapper = new XmlMapper();
      xmlMapper.enable(SerializationFeature.INDENT_OUTPUT);

      // Run the test
      String result = xmlMapper.writeValueAsString(operationCallDto).replaceAll("\r", "");
      assertEquals(expected, result);
   }
}
