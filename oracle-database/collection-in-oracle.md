集合

Oracle中一共有三种集合类型：index－by表、嵌套表和可变数组。其中index－by表只存在于PL/SQL中，而不能直 接存储在数据库表中。嵌套表可以存储在数据库表中。index－by表和嵌套表统称为PL/SQL表。可变数组被声明为具有固定数目的元素，而PL /SQL表没有声明上限。

一、index－by表

  index－by表在句法上类似于C或JAVA中的数组。首先需要定义表的属性，然后在声明使用。

  句法：TYPE   tabletype  IS  TABLE  OF  type  INDEX BY BINARY_INTEGER;

  其中tabletype是所定义的新类型的类型名，type是一个预定义的类型，或通过%TYPE或%ROWTYPE对一个类型的引用。INDEX BY BINARY_INTEGER子句是该表定义的一部分。这

个子句对于嵌套表是不存在的。

  一旦声明了类型和变量，就可以引用其中的单个元素：tablename(index)

  其中tablename是表的名称，index是一个BINARY_INTEGER类型的变量，或者是一个可以转换成BINARY_INTEGER类型的变量或表达式。

注意：

  a、index-by表是无约束的。其行数的唯一限制(除可用内存外)就是它的关键字是BINARY_INTEGER类型的，因此要受到BINARY_INTEGER类型所能表示数值的约束

(-2147483647~2147483647)。

  b、index-by表中的元素不一定要按任何特定顺序排列。因为它们不像数组那样在内存中是连续存储的，所以其元素可以借助于任意关键字而插入(如果你从PL/SQL中把一个index-by

表传递到C或JAVA的主机数组，其元素应该从1开始依次编号)。

  c、用于index-by表的关键字没有必要是有顺序的。任何BINARY_INTEGER值或表达式都可以用作表的索引。

  d、关键字唯一允许的类型是BINARY_INTEGER。

  e、index-by表类似于数据库表，它有key和value两列。key的类型是BINARY_INTEGER，而value的类型是在定义中指定的任何数据类型，可以是简单数值，也可以是记录，或者是对

象，也可以是集合。如果value的值是一条条记录，那么可以是用table(index)．field来引用该记录的字段。

  f、对于index-by表中的元素i赋值时，如果该元素i不存在，实际上会创建一个元素i，这类似于对数据库表进行的INSERT操作。如果对元素i引用，而其不存在，那么会抛出一个异常。可

以使用DELETE方法来删除表元素。

例如：

DECLARE

   TYPE NameTab IS TABLE OF students.first_name%TYPE INDEX BY BINARY_INTEGER;

   TYPE DateTab IS TABLE OF DATE INDEX BY BINARY_INTEGER;

   v_Names NameTab;

   v_Dates DateTab;

BEGIN

   v_Names(1) := 'Scott';

   v_Dates(-4) := SYSDATE - 1;

END;

非标量类型的index-by表

DECLARE

   TYPE StudentTab IS TABLE OF students%ROWTYPE INDEX BY BINARY_INTEGER;

   v_Students StudentTab;

BEGIN

  SELECT * 

   INTO v_Students(10001)

   FROM students

  WHERE id=10001;

 v_Students(1).first_name := 'Larry';

 v_Students(1).last_name := 'Lemon';

END;

对象类型的index-by表

CREATE OR REPLACE TYPE MyObject AS OBJECT(

   field1 NUMBER,

   field2 VARCHAR2(20),

   field3 DATE);

DECLARE

   TYPE ObjectTab IS TABLE OF MyObject INDEX BY BINARY_INTEGER;

   v_Objects ObjectTab;

BEGIN

  v_Objects(1) := MyObject(1,null,null);

  v_Objects(1).field2 := 'Hello World!';

  v_Objects(1).field3 := SYSDATE;

END;

二、嵌套表

  嵌套表的基本功能与index-by表相同，嵌套表可以被看做是具有两个列的数据库表。我们可以从嵌套表中删除元素，这样得到一个具有非有序关键字的稀疏表，这个表就像index-by表

。然而，嵌套表必须用有序的关键字创建，而且关键字不能是负数。此外，嵌套表可以存储到数据库中，而index-by表则不能。嵌套表中的最大行数是2G字节，这也是最大的关键字值。

  创建一个嵌套表类型的句法：TYPE table_name IS TABLE OF table_type [NOT NULL];

  其中table_name是新类型的类型名字，table_type是嵌套表中每一个元素的类型，它可以是用户定义的对象类型，也可以是使用%TYPE的表达式，但是它不可以是BOOLEAN、

NCHAR、NCLOB、NVARCHAR2或REF CURSOR。如果存在NOT NULL，那么嵌套表的元素不能是NULL。

  当声明了一个嵌套表时，它还没有任何元素，它会被自动初始化为NULL。此时如果直接使用它的话就会抛出一个COLLECTION_IS_NULL的异常。可以使用构造器来进行初始化，嵌

套 表的构造器与表的类型名本身具有相同的名称。然而，构造器有不定数目的参数，每一个参数都应该与表元素类型可以兼容，每一个参数就是其中的表元素。参数成 为从索引1开始有序的表元素。如果使用的是不带参数的构造器进行初始化，这会创建一个没有元素的空表，但不会被初始化为NULL。虽然表是无约束的，但是 你不能对不存在的元素赋值，这样将会导致表的大小增加，会抛出一个异常。你可以使用EXTEND方法来增加嵌套表的大小。

例如：

DECLARE

  TYPE NumbersTab IS TABLE OF NUMBER;

  v_Tab1 NumbersTab := NumbersTab(-1);

  v_Primes NumbersTab := NumbersTab(1,2,3,5,7);

  v_Tab2 NumbersTab := NumbersTab();

BEGIN

  v_Tab1(1) := 12345;

  FOR v_Count IN 1．．5 LOOP

   DBMS_OUTPUT.PUT(v_Primes(v_Count) || ' ');

  END LOOP;

  DBMS_OUTPUT.NEW_LINE;

END;

三、可变数组

  可变数组是一种非常类似于C或JAVA数组的数据类型。对可变数组的访问与对嵌套表或index-by表的访问类似。但是，可变数组在大小方面有一个固定的上界，这个上界作为类型声明

的一部分被指定。可变数组不是没有上界的稀疏数据结构，元素插入可变数组中时以索引1开始，一直到在可变数组类型中声明的最大长度。可变数组的极限大小也是2G字节。可变数组的元素在内存中连续存储。这不同于嵌套表的存储，嵌套表更像一个数据库表。

  可变数组类型声明的句法：TYPE type_name IS {VARRAY | VARYING ARRAY} (maximum_size) OF element_type [NOT NULL];

  其中type_name是新可变数组类型的类型名，maximum_size是一个指定可变数组中元素最大数目的一个整数，element_type是一个PL/SQL标量、记录或对象类型。element_type

可 以使用%TYPE来指定，但是它不可以是BOOLEAN、NCHAR、NCLOB、NVARCHAR2或REF CURSOR。可变数组也使用一个构造器来进行初始化。传递到构造器的参数数目成为可变数组的初始长度，它必须少于或等于在可变数组类型中指定的最大长 度。传递的参数就是可变数组的元素。如果引用可变数组的元素超出其界限，那么就会抛出SUBSCRIPT_OUTSIDE_LIMIT异常。可变数组的大 小也可以使用EXTEND方法来增加。不同于嵌套表的是，可变数组不能够被扩展超过为可变数组类型声明的极限大小。

例如：

DECLARE

  TYPE Numbers IS VARRAY(20) OF NUMBER(3);

  v_NullList Numbers;

  v_List1 Numbers := Numbers(1,2);

  v_List2 Numbers := Numbers(null);

BEGIN

  IF v_NullList IS NULL THEN

​    DBMS_OUTPUT.PUT_LINE(' v_NullList is NULL');

  END IF;

  IF v_List2(1) IS NULL THEN

​    DBMS_OUTPUT.PUT_LINE(' v_List2(1) is NULL');

  END IF;

END;

四、多层集合

  多层集合就是集合的集合，多层集合的类型声明与一维集合的声明相同，只是集合类型本身就是一个集合。因此我们使用两个括号来访问多层集合包含的元素。

例如：

DECLARE

  TYPE t_Numbers IS TABLE OF NUMBER INDEX BY BINARY_INTEGER;

  TYPE t_MultiNumbers IS TABLE OF t_Numbers INDEX BY BINARY_INTEGER;

  TYPE t_MultiVarray IS VARRAY(10) OF t_Numbers;

  TYPE t_MultiNested IS TABLE OF t_Numbers;

  v_MultiNumbers t_MultiNumbers;

BEGIN

  v_MultiNumbers(1)(1) := 12345;

END;

五、三种集合之间的比较

  1、可变数组与嵌套表

  两者的相似之处：

  ●两种类型(加上index-by表)都使用PL/SQL中的下标符号来允许对单个元素的访问。

  ●两种类型都可以存储在数据库表中(当在PL/SQL语句块外声明时)。

  ●集合方法可以应用于这两种类型。

  两者的区别：

  ●可变数组有大小上限，而嵌套表没有一个明确的大小上限。

  ●当存储到数据库中时，可变数组保持了元素的排序和下标的值，而嵌套表却不同。

  2、嵌套表和index-by表

  两者的相似之处：

  ●两种表的数据类型具有相同的结构。

  ●两种表中的单个元素都是使用下标符号进行访问的。

  ●嵌套表可用的方法包括index-by表的所有表属性。

  两者的区别：

  ●嵌套表可以使用SQL进行操作，而且可以存储到数据库中，而index-by表则不能。

  ●嵌套表合法的下标范围为1．．2147483647，而index-by的范围为-2147483647．．2147483647。index-by表可以有负数下标，而嵌套表则不能。

  ●嵌套表可以自动为NULL(用IS NULL操作符检验)。

  ●要添加元素，必须初始化和扩展嵌套表。

  ●嵌套表有可用的其它方法，如EXTEND和TRIM。

  ●PL/SQL会自动在主机数组和index-by表之间进行转换，但不能在主机数组和嵌套表之间转换。

六、数据库中的集合

1、存储集合的隐含式

a、模式层类型

  为了从数据库表中存储和检索一个集合，该集合类型必须为PL/SQL和SQL所知。这意味着它不能是PL/SQL的局部集合，而应该与对象类型类似，使用CREATE TYPE语句来声明。

在 模式层创建的类型对于PL/SQL来说是全局的，它有类似于任何其他数据库对象的范围和可见性规则。模式层的类型也可以用作数据库的列。声明为PL /SQL语句块的局部类型，只在声明它的那个语句块中可见，而且不可用作数据库的列。在包头声明的类型在整个PL/SQL中都是可见的，也仍然不能用作数 据库的列。只有模式层的类型才可以用作数据库的列。

例如：

SQL>CREATE OR REPLACE TYPE NameList AS VARRAY(20) OF VARCHAR2(30);

   /

DECLARE 

  TYPE DateList IS VARRAY(10) OF DATE;

  v_Dates DateList;

  v_Names NameList;

BEGIN

  NULL;

END;

b、已存储可变数组的结构

  可变数组可以用作数据库列的类型。在这种情况下，整个可变数组都与其他列并排着被存储到一个数据库行中。不同的行包含不同的可变数组。

注意：大于4K的可变数组数据实际上将与其余的表列分开存储，它将存储到LOB中。任何可变数组列的类型必须为数据库所知，并被存储在数据字典中，因此需要有CREATE TYPE语句。

例如：

CREATE OR REPLACE TYPE BookList AS VARRAY(10) OF NUMBER(4);

CREATE TABLE class_material(

  department    CHAR(3),

  course      NUMBER(3),

  required_reading BooList

);

c、已存储嵌套表的结构

  嵌套表也可以被存储为数据库的一个列。数据库表的每一行都可以包含一个不同的嵌套表。

注意：●表的类型在表定义中使用，就像列对象或内置类型那样。它必须是以CREATE TYPE语句创建的模式层类型。

​    ●对于每一个给定数据库表中的嵌套表，都需要NESTED TABLE子句。这个子句表明了存储表的名称。

​    ●存储表是系统产生的表，用来存储嵌套表中的实际数据。与已存储可变数组不同，嵌套表的数据并不是存储在表列中而是单独存储的。

​    ●存储表可以以另一种模式存在，而且可以有与主表不同的存储参数。存储表可以在user_tables中描述并存在于其中，但是不能被直接访问。

例如：

CREATE OR REPLACE TYPE StudentList AS TABLE OF NUMBER(5);

CREATE TABLE library_catalog(

 catalog_number  NUMBER(4),

   FOREIGN KEY (catalog_number) REFERENCES books(catalog_number),

 num_copies       NUMBER,

 num_out     NUMBER,

 checked_out    StudentList)

NESTED TABLE checked_out STORE AS co_tab;

2、操作整个集合

  你可以使用SQL DML 语句来操作一个存储集合。这些类型的操作将影响到整个集合，而非单个元素。集合中的元素可以通过使用PL/SQL操作，也可以通过SQL运算符操作。

a、INSERT

  INSERT语句被用来将集合插入到数据库行中，必须首先创建并初始化一个集合。

b、UPDATE

  UPDATE也被用来修改一个存储集合。

c、DELETE

  DELETE可以删除一个包含集合的行。

例如：

DECLARE

  v_StudentList  StudentList := StudentList(10000,10001,10002);

  

BEGIN

  INSERT INTO library_catalog(catalog_number,num_copies,num_out)

  VALUES(1000,20,3);

  INSERT INTO library_catalog(catalog_number,num_copies,num_out)

  VALUES(1001,20,3);

  INSERT INTO library_catalog(catalog_number,num_copies,num_out)

  VALUES(1000,20,3);

  INSERT INTO library_catalog(catalog_number,num_copies,num_out)

  VALUES(1002,20,3);

  UPDATE library_catalog

  SET  checked_out = v_StudentList

  WHERE catalog_number = 1000;

  DELETE FROM library_catalog

  WHERE catalog_number = 1000;

END;

d、SELECT

  使用SELECT语句可把集合从数据库检索到PL/SQL变量中。一旦集合保存到PL/SQL中，就可以使用过程化语句来操作它。

查询可变数组：

例如：

CREATE OR REPLACE PROCEDURE PrintRequired(

  p_Department  IN  class_material.department%TYPE,

  p_Course  IN  class_material.course%TYPE) 

IS

  v_Books class_material.required_reading%TYPE;

  v_Title books.title%TYPE;

BEGIN

  SELECT required_reading

​    INTO v_Books

   FROM class_material

  WHERE department = p_Department

​     AND  course = p_Course;

 

  FOR v_Index IN 1．．v_Books.COUNT LOOP

   SELECT title

​      INTO v_Title

​    FROM books

   WHERE catalog_number = v_Books(v_Index);

  END LOOP;

END;

查询嵌套表：

  当一个嵌套表被检索进某PL/SQL变量时，它就被赋于从1开始一直排到表中元素数的关键字值。后者可以使用COUNT方法来决定。

例如：

CREATE OR REPLACE PACKAGE BODY Library AS

  PROCEDURE PrintCheckedOut(p_CatalogNumber  IN  library_catalog.catalog_number%TYPE) IS 

  v_StudentList  StudentList;

  v_Student students%ROWTYPE;

  v_Book books%ROWTYPE;

  v_FoundOne  BOOLEAN := FALSE;

BEGIN

  SELECT checked_out

​     INTO  v_StudentList

   FROM  library_catalog

  WHERE  catalog_number = p_CatalogNumber;

  IF v_StudentList IS NOT NULL THEN

   FOR v_Index IN 1．．v_StudentList.COUNT LOOP 

​     v_FoundOne := TRUE;

​     SELECT * 

​        INTO v_Student

​       FROM Students

​      WHERE ID = v_StudentList(v_Index); 

   END LOOP;

  END IF;

END;

带有无序关键字的已存储表：

  存储在数据库中的嵌套表不能用PL/SQL直接操作，而只能用SQL操作。因此，关键字值并不被记录下来。当使用SELECT命令从数据库中选择一个嵌套表时，关键字从1开始依次重

新编号。因而，如果你把一个带有无序关键字的嵌套表插入到数据库时，关键字值将会改变，关键字也会从1开始依次重新编号。

例如：

CREATE OR REPLACE TYPE DateTab AS TABLE OF DATE;

CREATE TABLE famous_dates(

 key    VARCHAR2(100) PRIMARY KEY;

 date_list  DateTab)

NESTED TABLE date_list STORE AS dates_tab;

/

DECLARE

  v_Dates DateTab := DateTab(TO_DATE('04-JUL-1776','DD-MON-YYYY'),

​                 TO_DATE('12-APR-1861','DD-MON-YYYY'),

​                 TO_DATE('05-JUN-1968','DD-MON-YYYY'),

​                 TO_DATE('26-JAN-1986','DD-MON-YYYY'),

​                 TO_DATE('01-JAN-2001','DD-MON-YYYY'));

  PROCEDURE Print(p_Dates IN DateTab) IS 

​     v_Index BINARY_INTEGER := p_Dates.FIRST;

  BEGIN

​    WHILE v_Index <= p_Dates.LAST LOOP

​      DBMS_OUTPUT.PUT(' ' || v_Index || ': ' );

​      DBMS_OUTPUT.PUT_LINE(TO_CHAR(p_Dates(v_Index), 'DD-MON-YYYY'));

​      v_Index := p_Dates.NEXT(v_Index);

​    END LOOP;

  END Print;

BEGIN

  v_Dates.DELETE(2);

  DBMS_OUTPUT.PUT_LINE(' Initial value of the table: ');

  Print(v_Dates);

  INSERT INTO famous_dates(key, date_list)

  VALUES('Dates in American History', v_Dates);

  SELECT date_list

​        INTO v_Dates

​      FROM famous_dates

  WHERE key = 'Dates in American History';

  DBMS_OUTPUT.PUT_LINE('Table after INSERT and SELECT : ');

  Print(v_Dates);

END;

3、操作单个集合元素

a、PL/SQL操作

  把一个集合元素选入到PL/SQL变量中，然后通过操作这个变量来操作单个集合元素。

b、SQL表操作符

  可以直接使用带TABLE操作符的SQL来操作已存储嵌套表的元素，操作它并把它更新回数据库。然而，已存储可变数组的元素不能直接用DML操作，它们必须在PL/SQL中操作。

  语法：TABLE(subquery)

  其中subquery是一个返回嵌套表列的查询。

例如：

PROCEDURE PrintCheckedOut(p_CatalogNumber IN library_catalog.catalog_number%TYPE) IS

  v_StudentList  StudentList;

  v_Student students%ROWTYPE;

  v_Book   books%ROWTYPE;

  v_FoundOne  BOOLEAN := FALSE;

  CURSOR c_CheckedOut  IS

​    SELECT  column_value ID

​        FROM  TABLE(SELECT checked_Out

​                  FROM  library_catalog

​            WHERE catalog_number = p_CatalogNumber);

BEGIN

  SELECT *

​     INTO v_Book

   FROM books

  WHERE catalog_number = p_catalogNumber;

  FOR v_Rec IN c_CheckedOut  LOOP

​    v_FoundOne := TRUE;

​    SELECT *

​       INTO v_Student

​     FROM students

​    WHERE ID = v_Rec.ID;

  END LOOP;

END PrintCheckedOut;

查询已存储可变数组

  虽然已存储可变数组的元素不能用DML语句操作(不同于嵌套表)，但是可变数组可以使用TABLE操作符进行查询。在这种情况下，TABLE接受一个可变数组列，并返回其中的元素，

好像可变数组本身是一个单独的单列表。该列的列名是column_value。

例如：

SELECT department, course, column_value

 FROM class_material, TABLE(required_reading);

七、集合方法

  嵌套表和可变数组是对象类型，它们本身就有定义方法。index-by表有属性。

  属性或集合方法都使用下列语法调用：collection_instance.method_or_attribute

  其中，collection_instance是一个集合变量(不是类型名)，method_or_attribute是一个方法或属性。这些方法只能从过程化语句调用，而不能从SQL语句调用。

1、EXISTS

  EXISTS被用来确定所引用的元素是否在集合中存在。

  语法：EXISTS(n)

  其中，n是一个整数表达式。如果由n指定的元素存在，即便该元素是NULL(无效)的，它也会返回TRUE。如果n超出了范围，EXISTS就返回FALSE，而不是抛出异常。EXISTS和

DELETE可以被用来维护稀疏嵌套表。EXISTS也可以应用于自动的NULL嵌套表或可变数组，在这些情况下，它总是返回FALSE。

例如：

CREATE OR REPLACE TYPE NumTab AS TABLE OF NUMBER;

CREATE OR REPLACE TYPE NumVar AS VARRAY(25) OF NUMBER;

CREATE OR REPLACE PACKAGE IndexBy AS 

  TYPE NumTab IS TABLE OF NUMBER INDEX BY BINARY_INTEGER;

END IndexBy;

DECLARE

  v_NestedTable NumTab := NumTab(-7, 14.3, 3.14159, NULL, 0);

  v_Count  BINARY_INTEGER := 1;

  v_IndexByTable IndexBy.NumTab;

BEGIN

  LOOP

   IF v_NestedTable.EXISTS(v_Count) THEN

​    DBMS_OUTPUT.PUT_LINE(' v_NestedTable(' || v_Count || '): ' || v_NestedTable(v_Count));

​    v_Count := v_Count + 1;

   ELSE

​    EXIT;

   END IF;

  END LOOP;

  v_IndexByTable(1) := -7;

  v_IndexByTable(2) := 14.3;

  v_IndexByTable(3) := 3.14159;

  v_IndexByTable(4) := NULL;

  v_IndexByTable(5) := 0;

  v_Count := 1;

  LOOP

   IF v_IndexByTable.EXISTS(v_Count) THEN

​    DBMS_OUTPUT.PUT_LINE(' v_IndexByTable(' || v_Count || '): ' || v_IndexByTable(v_Count));

​    v_Count := v_Count +1;

   ELSE

​    EXIT;

   END IF;

  END LOOP;

END;

2、COUNT

  COUNT返回目前在集合中的元素数，它是一个整数。COUNT不带参数，而且在整数表达式有效处，它也有效。对于可变数组，COUNT总是与LAST相等，因为从可变数组中不能删除

元素。然而，从嵌套表中可以删除元素，因此，对于一个表来说，COUNT可能与LAST不同。当从数据库中选择一个嵌套表时，COUNT非常有用，因为那时元素的数目是未知的。在计算总

数时，COUNT将忽略已删除的元素。

3、LIMIT

  LIMIT返回一个集合目前的最大可允许的元素数(上限)。因为嵌套表没有大小上限，所以当应用于嵌套表时，LIMIT总是返回NULL。LIMIT对于index-by表是无效的。

例如：

DECLARE

  v_Table NumTab := NumTab(1,2,3);

  v_Varray NumVar := NumVar(1234,4321);

BEGIN

  DBMS_OUTPUT.PUT_LINE(' Varray limit : ' || v_Varray.LIMIT);

  DBMS_OUTPUT.PUT_LINE(' Varray count : ' || v_Varray.COUNT);

  IF v_Table.LIMIT IS NULL THEN

​    DBMS_OUTPUT.PUT_LINE(' Table limit is NULL' );

  ELSE

​    DBMS_OUTPUT.PUT_LINE('Table limit : ' || v_Table.LIMIT);

  END IF;

  DBMS_OUTPUT.PUT_LINE(' Table count : ' || v_Table.COUNT);

END;

4、FIRST和LAST

  FIRST返回集合中第一个元素的索引，而LAST返回最后一个元素的索引。对于可变数组，FIRST总是返回1，而LAST总是返回COUNT的值，这时因为可变数组是密集的，而且它的元

素不能被删除。FIRST和LAST可以与NEXT和PRIOR一起使用，来循环处理集合。

5、NEXT和PRIOR

  NEXT和PRIOR用来增加和减少集合的关键字值。

  它们的语法是：NEXT(n)和PRIOR(n)

  其中的n是一个整数表达式。NEXT(n)返回紧接在位置n处元素后面的那个元素的关键字，PRIOR(n)返回位置n处元素前面的那个元素的关键字。如果其前或其后没有元素，那么

NEXT和PRIOR将返回NULL。

例如：

DECLARE

  TYPE CharTab IS TABLE OF CHAR(1);

  v_Characters CharTab := CharTab('M' , 'a', 'd', 'a', 'm', ',', ' ', 'I', '''', 'm', ' ', 'A', 'd', 'a', 'm');

  v_Index INTEGER;

BEGIN

  v_Index := v_Characters.FIRST;

  WHILE v_Index <= v_Characters.LAST LOOP

   DBMS_OUTPUT.PUT(v_Characters(v_Index));

   v_Index := v_Characters.NEXT(v_Index);

  END LOOP;

  DBMS_OUTPUT.NEW_LINE;

 

  v_Index := v_Characters.LAST;

  WHILE v_Index >= v_Characters.FIRST LOOP

   DBMS_OUTPUT.PUT(v_Characters(v_Index));

   v_Index := v_Characters.PRIOR(v_Index);

  END LOOP;

  DBMS_OUTPUT.NEW_LINE;

END;

6、EXTEND

  EXTEND被用来把元素添加到嵌套表或可变数组的末端。它对于index-by表是无效的。

  EXTEND有三种形式：

  EXTEND

  EXTEND(n)

  EXTEND(n, i)

  没有参数的EXTEND仅仅用索引LAST+1把一个NULL元素添加到集合的末端。EXTEND(n)把n个NULL元素添加到表的末端，而EXTEND(n, i)把元素i的n个副本添加到表的末端。如

果该集合是用NOT NULL约束创建的，那么只有最后的这种可以使用，因为它不添加NULL元素。

  因为嵌套表没有一个明确的大小上限，所以你可以调用带n的EXTEND，n根据需要可取任意大的值(其大小上限是2G，同时受内存限制的影响)。然而，可变数组只能被扩展到其大小

上限，因此，n最大可以是(LIMIT - COUNT)。

  EXTEND对集合的内部大小进行操作，这包括嵌套表的任何已删除的元素。当一个元素已删除时(DELETE方法)，该元素的数据也被消除，但是关键字却保留了下来。

例如：

DECLARE

  v_Numbers NumTab := NumTab(-2, -1, 0, 1, 2);

  PROCEDURE Print(p_Table IN NumTab) IS 

​    v_Index INTEGER;

  BEGIN

​    v_Index := p_Table.FIRST;

​    WHILE v_Index <= p_Table.LAST LOOP

​      DBMS_OUTPUT.PUT(' Element ' || v_Index || ' : ‘);

​      DBMS_OUTPUT.PUT_LINE(p_Table(v_Index));

​      v_Index := p_Table.NEXT(v_Index);

​    END LOOP

  END Print;

BEGIN

  DBMS_OUTPUT.PUT_LINE(' At initialization , v_Numbers contains');

  Print(v_Numbers);

  v_Numbers.DELETE(3);

  DBMS_OUTPUT.PUT_LINE('After delete, v_Numbers contains');

  Print(v_Numbers);

  v_Numbers.EXTEND(2, 1);

  DBMS_OUTPUT.PUT_LINE(' After extend, v_Numbers contains');

  Print(v_Numbers);

  DBMS_OUTPUT.PUT_LINE(' v_Numbers.COUNT = ' || v_Numbers.COUNT);

  DBMS_OUTPUT.PUT_LINE(' v_Numbers.LAST = ' || v_Numbers.LAST);

END;

7、TRIM

  TRIM被用来从嵌套表或可变数组的末端删除元素。

  它有两种形式：TRIM和TRIM(n)

  没有参数时，TRIM从集合的末端删除一个元素。否则，则删除n个元素。如果n大于COUNT，则抛出异常。因为删除了一些元素，在TRIM操作后COUNT将变小。TRIM也对集合内部的

大小进行操作，包括以DELETE删除的任何元素。

例如：

DECLARE

  v_Numbers NumTab := NumTab(-3, -2, -1, 0, 1, 2, 3);

  PROCEDURE Print(p_Table  IN  NumTab) IS

   v_Index  INTEGER;

  BEGIN

   v_Index := p_Table.FIRST;

   WHILE v_Index <= p_Table.LAST  LOOP

​     DBMS_OUTPUT.PUT('Element ' || v_Index || ': ');

​     DBMS_OUTPUT.PUT_LINE(p_Table(v_Index));

​     v_Index := p_Table.NEXT(v_Index);

   END LOOP;

   DBMS_OUTPUT.PUT_LINE(' COUNT = ' || p_Table.COUNT);

   DBMS_OUTPUT.PUT_LINE(' LAST = ' || p_Table.LAST);

  END Print;

BEGIN

  DBMS_OUTPUT.PUT_LINE(' At initialization, v_Numbers contains');

  Print(v_Numbers);

  v_Numbers.DELETE(6);

  DBMS_OUTPUT.PUT_LINE(' After delete, v_Numbers contains');

  Print(v_Numbers);

  v_Numbers.TRIM(3);

  DBMS_OUTPUT.PUT_LINE(' After trim, v_Numbers contains');

  Print(v_Numbers);

END;

8、DELETE

  DELETE将从index-by表和嵌套表中删除一个或多个元素。DELETE对可变数组没有影响，因为它的大小固定(事实上，在可变数组上调用DELETE是不合法的)。

  DELETE有三种形式：

  DELETE

  DELETE(n)

  DELETE(m, n)

  没有参数时，DELETE将删除整个表。DELETE(n)将在索引n处删除一个元素，而DELETE(m, n)将删除索引m和n之间的所有元素。在DELETE操作之后，COUNT将变小，它反映了

嵌套表的新的大小。如果要删除的表元素不存在，DELETE不会引起错误，而是仅仅跳过那个元素。

例如：

DECLARE

  v_Numbers NumTab := NumTab(10, 20, 30, 40, 50, 60, 70, 80, 90, 100);

  PROCEDURE Print(p_Table IN NumTab) IS

   v_Index INTEGER;

  BEGIN

   v_Index := p_Table.FIRST;

   WHILE v_Index <= p_Table.LAST LOOP

​    DBMS_OUTPUT.PUT(' Element ' || v_Index || ': ');

​    DBMS_OUTPUT.PUT_LINE(p_Table(v_Index));

​    v_Index := p_Table.NEXT(v_Index);

   END LOOP;

   DBMS_OUTPUT.PUT_LINE(' COUNT = ' || p_Table.COUNT);

   DBMS_OUTPUT.PUT_LINE(' LAST = ' || p_Table.LAST);

  END;

BEGIN

  DBMS_OUTPUT.PUT_LINE(' At initialization, v_Numbers contains' );

  Print(v_Numbers);

  

  DBMS_OUTPUT.PUT_LINE(' After delete(6), v_Numbers contains' );

  v_Numbers.DELETE(6);

  Print(v_Numbers);

 

  DBMS_OUTPUT.PUT_LINE(' After delete(7,9), v_Numbers contains‘);

  v_Numbers.DELETE(7,9);

  Print(v_Numbers);

END;

 

Pasted from <<http://scenical.blog.163.com/blog/static/1061040201341392810944/>> 