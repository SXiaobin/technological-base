# How Tos

## How to convert JSON Array from String to List

### Jackson

Json array String

```json
[{"name":"mkyong", "age":37}, {"name":"fong", "age":38}]
```

POJO

```java
public class Person {
    String name;
    int age;
    //getters and setters
}
```

 Convert the JSON array string to a List

```java
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class JacksonArrayExample {

    public static void main(String[] args) {

        ObjectMapper mapper = new ObjectMapper();
        String json = "[{\"name\":\"mkyong\", \"age\":37}, {\"name\":\"fong\", \"age\":38}]";

        try {

            // 1. convert JSON array to Array objects
            Person[] pp1 = mapper.readValue(json, Person[].class);

            System.out.println("JSON array to Array objects...");
            for (Person person : pp1) {
                System.out.println(person);
            }

            // 2. convert JSON array to List of objects
            List<Person> ppl2 = Arrays.asList(mapper.readValue(json, Person[].class));

            System.out.println("\nJSON array to List of objects");
            ppl2.stream().forEach(x -> System.out.println(x));

            // 3. alternative
            List<Person> pp3 = mapper.readValue(json, new TypeReference<List<Person>>() {});

            System.out.println("\nAlternative...");
            pp3.stream().forEach(x -> System.out.println(x));

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
```

