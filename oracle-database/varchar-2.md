# Varchar2(char) & Varchar2( byte)

Oracle中varchar2(20)和varchar2(20 byte)区别

 

 开发环境中：有的表中字段类型定义为varchar2(20)有的表中字段类型定义为varchar2(20 byte)

varchar2(20)和varchar2(20 byte)是否相同呢？

相不相同是由[数据库](http://www.2cto.com/database/)的参数NLS_LENGTH_SEMANTICS决定的，有两个单位，char（字符）或者字节（byte），该参数默认值为BYTE。

所以说，在默认情况下 varchar2(20) = varchar2(20 byte)。如果参数值为CHAR 就不相等。

建议：使用统一的格式如：varchar2(20)

演示：

SQL> show parameter nls_length_semantics;

 

NAME                                        TYPE       VALUE

------------------------------------ ----------- ------------------------------

nls_length_semantics                    string     BYTE

SQL> create table tab1 (

  2  id   number(10),

  3  description varchar2(20)

  4  );

 

Table created.

 

SQL> create table tab2 (

  2  id   number(10),

  3  description varchar2(20 char)

  4  );

 

Table created.

 

SQL> desc tab1;

 Name                                             Null?    Type

 ----------------------------------------- -------- ----------------------------

 ID                                                     NUMBER(10)

 DESCRIPTION                                                  VARCHAR2(20)

 

SQL> desc tab2;

 Name                                             Null?    Type

 ----------------------------------------- -------- ----------------------------

 ID                                                     NUMBER(10)

 DESCRIPTION                                                  VARCHAR2(20 CHAR)

SQL> alter session set nls_length_semantics=char;

 

Session altered.

 

SQL> create table tab3 (

  2  id number(10),

  3  description varchar2(20)

  4  );

 

Table created.

 

SQL> desc tab1;

 Name                                             Null?    Type

 ----------------------------------------- -------- ----------------------------

 ID                                                     NUMBER(10)

 DESCRIPTION                                                  VARCHAR2(20 BYTE)

 

SQL> desc tab2;

 Name                                             Null?    Type

 ----------------------------------------- -------- ----------------------------

 ID                                                     NUMBER(10)

 DESCRIPTION                                                  VARCHAR2(20)

 

SQL> desc tab3;

 Name                                             Null?    Type

 ----------------------------------------- -------- ----------------------------

 ID                                                     NUMBER(10)

 DESCRIPTION                                                  VARCHAR2(20)

注意：对于SYS和SYSTEM而言不受该NLS_LENGTH_SEMANTICS影响，始终为BYTE

请不要随意修改SYSTEM级别的NLS_LENGTH_SEMANTICS，否则一些成品套件例如EBS将可能无法正常运行。

 

Pasted from <<http://www.2cto.com/database/201304/203337.html>> 