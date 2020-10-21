# Null 是如何影响索引的建立和维护的？

 NULL值是关系数据库系统布尔型(true,false,unknown)中比较特殊类型的一种值，通常称为UNKNOWN或空值，即是未知的，不确定的。由于NULL存在着无数的可能，因此NULL值也不等于NULL值，所以与NULL值相关的操作同样都为NULL值。正是基于这样一个特性，对于NULL值列上的B树索引导致了is null/is not null不走索引的情形，下面描述了NULL值与索引以及索引NULL列上的执行计划，如何使得NULL值走索引的情形。

注：本文仅仅讨论的是B树索引上的NULL值，位图索引不在此范围之内。

一、null值与索引的关系

**[sql]** [view plaincopyprint?](http://blog.csdn.net/leshami/article/details/7437561#)

scott@ORCL> **create** **table** t1(id number,val varchar2(1));  

  

-->为表t1创建唯一索引  

scott@ORCL> **create** **unique** **index** i_t1_id **on** t1(id);  

  

scott@ORCL> **insert** **into** t1 **select** null,'Y' **from** dual;  

  

scott@ORCL> **insert** **into** t1 **select** null,'N' **from** dual;  

  

-->从上面的操作可知，尽管列id上存在唯一索引，但由于null值不等于任一null值，因此能够成功插入  

scott@ORCL> **commit**;  

  

-->再次为表添加唯一复合索引，即基于id列与val列  

scott@ORCL> **create** **unique** **index** i_t1_id_val **on** t1(id,val);  

  

**Index** created.  

  

-->插入null,'N'的记录时失败，提示违反唯一性约束  

scott@ORCL> **insert** **into** t1 **select** null,'N' **from** dual;  

**insert** **into** t1 **select** null,'N' **from** dual  

\*  

ERROR **at** line 1:  

ORA-00001: **unique** **constraint** (SCOTT.I_T1_ID_VAL) violated  

  

-->插入null,'Y'的记录时同样失败，提示违反唯一性约束  

scott@ORCL> **insert** **into** t1 **select** null,'Y' **from** dual;  

**insert** **into** t1 **select** null,'Y' **from** dual  

\*  

ERROR **at** line 1:  

ORA-00001: **unique** **constraint** (SCOTT.I_T1_ID_VAL) violated  

  

-->插入两个null值成功  

scott@ORCL> **insert** **into** t1 **select** null,null **from** dual;  

  

1 row created.  

  

scott@ORCL> **insert** **into** t1 **select** null,null **from** dual;  

  

1 row created.  

  

scott@ORCL> **insert** **into** t1 **select** null,'A' **from** dual;  

  

1 row created.  

  

scott@ORCL> **commit**;  

  

**Commit** complete.  

  

scott@ORCL> **set** null unknown;  

scott@ORCL> **select** * **from** t1;  

  

​        ID VAL  

---------- ------------------------------

unknown    Y  

unknown    N  

unknown    unknown  

unknown    unknown  

unknown    A  

  

scott@ORCL> **exec** dbms_stats.gather_table_stats('SCOTT','T1',**cascade**=>**true**);  

​           

scott@ORCL> **select** index_name,index_type,blevel,leaf_blocks,num_rows,status,distinct_keys  

  2  **from** user_indexes  **where** table_name='T1';  

  

INDEX_NAME      INDEX_TYPE     BLEVEL LEAF_BLOCKS   NUM_ROWS STATUS   DISTINCT_KEYS  

--------------- ---------- ---------- ----------- ---------- -------- -------------

I_T1_ID         NORMAL              0           0          0 VALID                0  

I_T1_ID_VAL     NORMAL              0           1          3 VALID                3  

  

-->从上面的情形可知，  

-->基于单列的唯一索引，可以多次插入null值，但其索引上并不存储null值。  

-->基于多列的复合索引，尽管全为null值的行可以多次插入，但不全为null的重复行则不能被插入(注,非唯一复合索引不存在此限制，此处不演示)。  

-->基于多列的复合索引，对于全为null值的索引值也不会被存储。如上面的情形，尽管插入了5条记录，复合索引中只存储了3条。  

-->注：对于唯一性约束，null值不等于null值，同样(null,null)也不等同于(null,null)，所以上面的两次null能够被插入。  

二、null值与执行计划

**[sql]** [view plaincopyprint?](http://blog.csdn.net/leshami/article/details/7437561#)

scott@ORCL> **set** autot trace exp;  

scott@ORCL> **select** * **from** t1 **where** id **is** null;  

  

Execution Plan  

\----------------------------------------------------------  

Plan hash value: 3617692013  

  

\--------------------------------------------------------------------------  

| Id  | Operation         | **Name** | **Rows**  | Bytes | Cost (%CPU)| **Time**     |  

\--------------------------------------------------------------------------  

|   0 | **SELECT** STATEMENT  |      |     5 |     5 |     3   (0)| 00:00:01 |  

|*  1 |  **TABLE** ACCESS **FULL**| T1   |     5 |     5 |     3   (0)| 00:00:01 |  

\--------------------------------------------------------------------------  

  

Predicate Information (identified **by** operation id):  

\---------------------------------------------------  

  

   1 - filter("ID" **IS** NULL)  

  

-->从上面的测试可知，由于null值是不被存储的，因此当使用id is null作为谓词时，走了全表扫描  

​     

scott@ORCL> **select** * **from** t1 **where** id **is** not null;  

  

Execution Plan  

\----------------------------------------------------------  

Plan hash value: 796913935  

  

\---------------------------------------------------------------------------------------  

| Id  | Operation                   | **Name**    | **Rows**  | Bytes | Cost (%CPU)| **Time**     |  

\---------------------------------------------------------------------------------------  

|   0 | **SELECT** STATEMENT            |         |     1 |     1 |     0   (0)| 00:00:01 |  

|   1 |  **TABLE** ACCESS **BY** **INDEX** ROWID| T1      |     1 |     1 |     0   (0)| 00:00:01 |  

|*  2 |   **INDEX** **FULL** SCAN           | I_T1_ID |     1 |       |     0   (0)| 00:00:01 |  

\---------------------------------------------------------------------------------------  

  

Predicate Information (identified **by** operation id):  

\---------------------------------------------------  

  

   2 - filter("ID" **IS** NOT NULL)  

  

-->从上面的测试可知，尽管当前表上id列上的所有值都为null，但不排除后续记录插入的id不为null的列。  

-->故当使用id is not null作为谓词时，此时执行计划中走了索引全扫描。     

  

-->下面来看看复合索引的情形     

scott@ORCL> **select** * **from** t1 **where** val **is** null;  

  

Execution Plan  

\----------------------------------------------------------  

Plan hash value: 3617692013  

  

\--------------------------------------------------------------------------  

| Id  | Operation         | **Name** | **Rows**  | Bytes | Cost (%CPU)| **Time**     |  

\--------------------------------------------------------------------------  

|   0 | **SELECT** STATEMENT  |      |     2 |     2 |     3   (0)| 00:00:01 |  

|*  1 |  **TABLE** ACCESS **FULL**| T1   |     2 |     2 |     3   (0)| 00:00:01 |  

\--------------------------------------------------------------------------  

  

Predicate Information (identified **by** operation id):  

\---------------------------------------------------  

  

   1 - filter("VAL" **IS** NULL)  

  

scott@ORCL> **select** * **from** t1 **where** val **is** not null;  

  

Execution Plan  

\----------------------------------------------------------  

Plan hash value: 1931510411  

  

\--------------------------------------------------------------------------------  

| Id  | Operation        | **Name**        | **Rows**  | Bytes | Cost (%CPU)| **Time**     |  

\--------------------------------------------------------------------------------  

|   0 | **SELECT** STATEMENT |             |     3 |     3 |     1   (0)| 00:00:01 |  

|*  1 |  **INDEX** **FULL** SCAN | I_T1_ID_VAL |     3 |     3 |     1   (0)| 00:00:01 |  

\--------------------------------------------------------------------------------  

  

Predicate Information (identified **by** operation id):  

\---------------------------------------------------  

  

   1 - filter("VAL" **IS** NOT NULL)  

  

-->对于复合唯一索引的情形，当使用单列且非前导列谓词时，使用is null与 is not null等同于单列唯一索引的情形。  

-->即原理也是一样的，val is null走全表扫描而val is not null走索引。因为null值不会被存储。  

  

-->下面看看两个列都作为谓词的情形     

scott@ORCL> **select** * **from** t1 **where** id **is** null and val **is** not null;  

  

Execution Plan  

\----------------------------------------------------------  

Plan hash value: 1040510552  

  

\--------------------------------------------------------------------------------  

| Id  | Operation        | **Name**        | **Rows**  | Bytes | Cost (%CPU)| **Time**     |  

\--------------------------------------------------------------------------------  

|   0 | **SELECT** STATEMENT |             |     3 |     3 |     1   (0)| 00:00:01 |  

|*  1 |  **INDEX** RANGE SCAN| I_T1_ID_VAL |     3 |     3 |     1   (0)| 00:00:01 |  

\--------------------------------------------------------------------------------  

  

Predicate Information (identified **by** operation id):  

\---------------------------------------------------  

  

   1 - access("ID" **IS** NULL)  

​       filter("VAL" **IS** NOT NULL)  

  

-->从上面的测试可知，尽管两个谓词列上都存在索引，一个为单列唯一索引，一个为复合唯一索引。Oracle 选择了复合索引I_T1_ID_VAL。      

  

scott@ORCL> **select** * **from** t1 **where** id **is** not null and val **is** null;  

  

Execution Plan  

\----------------------------------------------------------  

Plan hash value: 796913935  

  

\---------------------------------------------------------------------------------------  

| Id  | Operation                   | **Name**    | **Rows**  | Bytes | Cost (%CPU)| **Time**     |  

\---------------------------------------------------------------------------------------  

|   0 | **SELECT** STATEMENT            |         |     1 |     1 |     0   (0)| 00:00:01 |  

|*  1 |  **TABLE** ACCESS **BY** **INDEX** ROWID| T1      |     1 |     1 |     0   (0)| 00:00:01 |  

|*  2 |   **INDEX** **FULL** SCAN           | I_T1_ID |     1 |       |     0   (0)| 00:00:01 |  

\---------------------------------------------------------------------------------------  

  

Predicate Information (identified **by** operation id):  

\---------------------------------------------------  

  

   1 - filter("VAL" **IS** NULL)  

   2 - filter("ID" **IS** NOT NULL)      

  

-->同样的情形，谓词的顺序与复合索引定义的顺序一样，只不过第一个谓词为id is not null，而第二个谓词为val is null。  

-->此时Oracle 选择了单列唯一索引I_T1_ID  

-->看到此，不知道大家是否已明白，即哪个列为is not null，则会使用该列上的索引，原因还是那句话，索引不存储null值。  

-->对于颠倒id列与val列以及id,val列为null或not null的其他不同组合情形不再演示，其执行计划类似。  

三、使用is null走索引的情形

**[sql]** [view plaincopyprint?](http://blog.csdn.net/leshami/article/details/7437561#)

scott@ORCL> **set** autot **off**;  

--删除原有表上的null值记录  

scott@ORCL> **delete** **from** t1 **where** val not in('Y','N') or val **is** null;  

  

3 **rows** deleted.  

  

scott@ORCL> **update** t1 **set** id=1 **where** val='Y';  

  

1 row updated.  

  

scott@ORCL> **update** t1 **set** id=2 **where** val='N';  

  

1 row updated.  

  

scott@ORCL> **commit**;  

  

**Commit** complete.  

  

-->对原有记录更新后的情形  

scott@ORCL> **select** * **from** t1;  

  

​        ID VAL  

---------- ------------------------------

​         1 Y  

​         2 N  

  

scott@ORCL> **exec** dbms_stats.gather_table_stats('SCOTT','T1',**cascade**=>**true**);  

  

PL/SQL **procedure** successfully completed.  

  

-->修改表列id使之具有not null约束的特性  

scott@ORCL> **alter** **table** t1 **modify**(id not null);  

  

**Table** altered.  

  

scott@ORCL> **set** autot trace exp;  

scott@ORCL> **select** * **from** t1 **where** id **is** null;  

  

Execution Plan  

\----------------------------------------------------------  

Plan hash value: 3160894736  

  

\--------------------------------------------------------------------------------  

| Id  | Operation        | **Name**        | **Rows**  | Bytes | Cost (%CPU)| **Time**     |  

\--------------------------------------------------------------------------------  

|   0 | **SELECT** STATEMENT |             |     1 |     5 |     0   (0)|          |  

|*  1 |  FILTER          |             |       |       |            |          |  

|   2 |   **INDEX** **FULL** SCAN| I_T1_ID_VAL |     2 |    10 |     1   (0)| 00:00:01 |  

\--------------------------------------------------------------------------------  

  

Predicate Information (identified **by** operation id):  

\---------------------------------------------------  

  

   1 - filter(NULL **IS** NOT NULL)  

  

-->从上面的执行计划中可知，当表t1列id上具有not null 约束时，此时使用id is null选择了索引范围扫描  

  

-->下面来看看列val is null 的情形     

scott@ORCL> **select** * **from** t1 **where** val **is** null;  

  

Execution Plan  

\----------------------------------------------------------  

Plan hash value: 48744011  

  

\------------------------------------------------------------------------------------  

| Id  | Operation            | **Name**        | **Rows**  | Bytes | Cost (%CPU)| **Time**     |  

\------------------------------------------------------------------------------------  

|   0 | **SELECT** STATEMENT     |             |     1 |     5 |     2   (0)| 00:00:01 |  

|*  1 |  **INDEX** FAST **FULL** SCAN| I_T1_ID_VAL |     1 |     5 |     2   (0)| 00:00:01 |  

\------------------------------------------------------------------------------------  

  

Predicate Information (identified **by** operation id):  

\---------------------------------------------------  

  

   1 - filter("VAL" **IS** NULL)  

  

-->尽管val列上允许null值存在，但由于列id上具有not null 约束，且id列与val列存在复合唯一索引，因此此时选择了索引快速全扫描  

-->其余不同组合情形大致相同，不再演示  

  

-->为表t1新增一条val为null的记录  

scott@ORCL> **insert** **into** t1 **select** 3,null **from** dual;  

  

1 row created.  

  

scott@ORCL> **commit**;  

  

**Commit** complete.  

  

scott@ORCL> **exec** dbms_stats.gather_table_stats('SCOTT','T1',**cascade**=>**true**);  

  

PL/SQL **procedure** successfully completed.  

  

-->下面的查询中可以看出尽管只有列id有not null约束，当所有的索引值都被存储  

scott@ORCL> **select** index_name,index_type,blevel,leaf_blocks,num_rows,status,distinct_keys  

  2  **from** user_indexes  **where** table_name='T1';  

  

INDEX_NAME      INDEX_TYPE     BLEVEL LEAF_BLOCKS   NUM_ROWS STATUS   DISTINCT_KEYS  

--------------- ---------- ---------- ----------- ---------- -------- -------------

I_T1_ID         NORMAL              0           1          3 VALID                3  

I_T1_ID_VAL     NORMAL              0           1          3 VALID                3  

  

-->Author : Robinson Cheng  

-->Blog :   <http://blog.csdn.net/robinson_0612>  

四、总结

​    无论是单列唯一索引或复合唯一索引，对于可以为null的列或复合null值，Oracle不会为其存储索引值。

​    故在基于单列创建B树唯一索引或多列创建B树复合唯一索引的情形下，

​    当列上允许为null值时

​        where子句使用了基于is null的情形，其执行计划走全表扫描。

​        where子句使用了基于is not null的情形，其执行计划走索引扫描(索引范围扫描或索引全扫描)。

​    当列上不允许为null值时，存在非null约束

​        where子句使用了基于is null的情行，其执行计划走索引扫描。

​        where子句使用了基于is not null的情形，其执行计划也是走索引扫描。

​    注：此在Oracle 10g R2(linux)下的情形，不同的优化器版本可能会有偏差。

 

 

 在[NULL值与索引(一)](http://blog.csdn.net/robinson_0612/article/details/7437561)中讲述了null值与索引的一些基本情况。其主要的内容为，基于允许存在null值的索引列，其索引值不会被存储；其次

是由于这个特性导致了我们在使用is null时索引失效的情形；最后则是描述的通过为null值列添加not null约束来使得is null走索引。尽管我

们可以通过添加not null来解决is null走索引，当现实中的情况是仍然很多列根本是无法确定的，而必须保持其null特性。对于此种情形该如

何解决呢？

 

一、通过基于函数的索引来使得is null使用索引

**[sql]** [view plaincopyprint?](http://blog.csdn.net/leshami/article/details/7438397#)

-->演示环境  

scott@ORCL> **select** * **from** v$version **where** rownum<2;  

  

BANNER  

\----------------------------------------------------------------  

Oracle **Database** 10g Enterprise Edition Release 10.2.0.1.0 - Prod  

  

-->创建测试表t2  

scott@ORCL> **create** **table** t2(obj_id,obj_name) **as** **select** object_id,object_name **from** dba_objects;  

  

**Table** created.  

  

-->演示表t2上不存在not null约束  

scott@ORCL> **desc** t2  

 **Name**                          Null?    Type  

----------------------------- -------- --------------------

 OBJ_ID                                 NUMBER  

 OBJ_NAME                               VARCHAR2(128)  

  

-->为表t2创建一个普通的B树索引  

scott@ORCL> **create** **index** i_t2_obj_id **on** t2(obj_id);  

  

**Index** created.  

  

-->将表t2列obj_id<=100的obj_id置空  

-->注：在Oracle 10g中空字符串等同于null值  

scott@ORCL> **update** t2 **set** obj_id='' **where** obj_id<=100;  

  

99 **rows** updated.  

  

-->下面的查询亦表明在此时空字符串等同于null值  

scott@ORCL> **set** null unknown  

scott@ORCL> **select** * **from** t2 **where** obj_id **is** null and rownum<3;  

  

​    OBJ_ID OBJ_NAME  

---------- ------------------------------

unknown    ICOL$  

unknown    I_USER1  

  

-->收集统计信息  

scott@ORCL> **exec** dbms_stats.gather_table_stats('SCOTT','T2',**cascade**=>**true**);  

  

PL/SQL **procedure** successfully completed.  

  

-->基于null值上使用not null会使用索引扫描，等同于前面 null值与索引(一) 中的描述  

scott@ORCL> **select** count(*) **from** t2 **where** obj_id **is** not null;  

  

Execution Plan  

\----------------------------------------------------------  

Plan hash value: 3840858596  

  

\-------------------------------------------------------------------------------------  

| Id  | Operation             | **Name**        | **Rows**  | Bytes | Cost (%CPU)| **Time**     |  

\-------------------------------------------------------------------------------------  

|   0 | **SELECT** STATEMENT      |             |     1 |     5 |     7   (0)| 00:00:01 |  

|   1 |  SORT AGGREGATE       |             |     1 |     5 |            |          |  

|*  2 |   **INDEX** FAST **FULL** SCAN| I_T2_OBJ_ID | 11719 | 58595 |     7   (0)| 00:00:01 |  

\-------------------------------------------------------------------------------------  

  

Predicate Information (identified **by** operation id):  

\---------------------------------------------------  

  

   2 - filter("OBJ_ID" **IS** NOT NULL)  

  

-->列obj_id is null走全表扫描  

scott@ORCL> **select** count(*) **from** t2 **where** obj_id **is** null;  

  

Execution Plan  

\----------------------------------------------------------  

Plan hash value: 3321871023  

  

\---------------------------------------------------------------------------  

| Id  | Operation          | **Name** | **Rows**  | Bytes | Cost (%CPU)| **Time**     |  

\---------------------------------------------------------------------------  

|   0 | **SELECT** STATEMENT   |      |     1 |     5 |    13   (0)| 00:00:01 |  

|   1 |  SORT AGGREGATE    |      |     1 |     5 |            |          |  

|*  2 |   **TABLE** ACCESS **FULL**| T2   |     1 |     5 |    13   (0)| 00:00:01 |  

\---------------------------------------------------------------------------  

  

Predicate Information (identified **by** operation id):  

\---------------------------------------------------  

  

   2 - filter("OBJ_ID" **IS** NULL)  

  

-->创建基于函数的索引来使得is null走索引  

-->下面使用了nvl函数来创建函数索引，即当obj_id为null值时，存储-1     

scott@ORCL> **create** **index** i_fn_t2_obj_id **on** t2(nvl(obj_id,-1));  

  

**Index** created.  

  

-->收集索引信息  

scott@ORCL> **exec** dbms_stats.gather_index_stats('SCOTT','I_FN_T2_OBJ_ID');  

  

PL/SQL **procedure** successfully completed.  

  

-->可以看到下面的执行计划中刚刚创建的函数索引已经生效I_FN_T2_OBJ_ID  

scott@ORCL> **select** count(*) **from** t2 **where** nvl(obj_id,-1) = -1;  

  

Execution Plan  

\----------------------------------------------------------  

Plan hash value: 3983750858  

  

\------------------------------------------------------------------------------------  

| Id  | Operation         | **Name**           | **Rows**  | Bytes | Cost (%CPU)| **Time**     |  

\------------------------------------------------------------------------------------  

|   0 | **SELECT** STATEMENT  |                |     1 |     5 |     1   (0)| 00:00:01 |  

|   1 |  SORT AGGREGATE   |                |     1 |     5 |            |          |  

|*  2 |   **INDEX** RANGE SCAN| I_FN_T2_OBJ_ID |   100 |   500 |     1   (0)| 00:00:01 |  

\------------------------------------------------------------------------------------  

  

Predicate Information (identified **by** operation id):  

\---------------------------------------------------  

  

   2 - access(NVL("OBJ_ID",(-1))=(-1))  

二、使用伪列创建基于函数的索引来使得is null使用索引

**[sql]** [view plaincopyprint?](http://blog.csdn.net/leshami/article/details/7438397#)

-->下面通过添加一个值为-1(可取任意值)的伪列来创建索引  

scott@ORCL> **create** **index** i_new_t2_obj_id **on** t2(obj_id,-1);  

  

**Index** created.  

  

-->收集索引信息  

scott@ORCL> **exec** dbms_stats.gather_index_stats('SCOTT','I_NEW_T2_OBJ_ID');  

  

PL/SQL **procedure** successfully completed.     

  

-->从下面的查询可以看出obj_id is null使用了刚刚创建的索引  

scott@ORCL> **select** count(*) **from** t2 **where** obj_id **is** null;  

  

Execution Plan  

\----------------------------------------------------------  

Plan hash value: 801885198  

  

\-------------------------------------------------------------------------------------  

| Id  | Operation         | **Name**            | **Rows**  | Bytes | Cost (%CPU)| **Time**     |  

\-------------------------------------------------------------------------------------  

|   0 | **SELECT** STATEMENT  |                 |     1 |     5 |     2   (0)| 00:00:01 |  

|   1 |  SORT AGGREGATE   |                 |     1 |     5 |            |          |  

|*  2 |   **INDEX** RANGE SCAN| I_NEW_T2_OBJ_ID |    99 |   495 |     2   (0)| 00:00:01 |  

\-------------------------------------------------------------------------------------  

  

Predicate Information (identified **by** operation id):  

\---------------------------------------------------  

  

   2 - access("OBJ_ID" **IS** NULL)  

  

-->查看刚刚创建的所有索引的相关统计信息     

scott@ORCL> **select** index_name,index_type,blevel,leaf_blocks,num_rows,status,distinct_keys  

  2  **from** user_indexes **where** table_name='T2';  

  

INDEX_NAME      INDEX_TYPE                         BLEVEL LEAF_BLOCKS   NUM_ROWS STATUS   DISTINCT_KEYS  

--------------- ------------------------------ ---------- ----------- ---------- -------- -------------

I_FN_T2_OBJ_ID  **FUNCTION**-BASED NORMAL                   1          26      11719 VALID            11621  

I_NEW_T2_OBJ_ID **FUNCTION**-BASED NORMAL                   1          32      11719 VALID            11621  

I_T2_OBJ_ID     NORMAL                                  1          25      11620 VALID            11620  

  

-->从上面的结果可知：  

-->普通的B索引(I_T2_OBJ_ID)使用的索引块最小，因为null值没有被存储，NUM_ROWS与DISTINCT_KEYS即是佐证  

-->使用NVL函数创建的索引I_FN_T2_OBJ_ID中如实的反应了null值，即11620 + null值 = 11621  

-->使用伪列创建的索引依然属于函数索引，其耗用的叶节点块数最多，因为多出了一个值(-1)来存储  

-->尽管使用NVL创建的函数占用的磁盘空间小于使用伪列创建的索引，当在书写谓词时需要带上NVL函数，而伪列索引中谓词直接使用is null。  

三、NULL值与索引衍生特性

**[sql]** [view plaincopyprint?](http://blog.csdn.net/leshami/article/details/7438397#)

-->由前面的种种事例再次说明NULL值不会被存储到索引中，因此基于这个特性可以使用decode函数来压缩索引列。  

-->在实际应用的多数情形中，如表上有打印状态列is_printed通常为两种情形，已打印或未打印，假定1表示已打印，而0表示未打印。  

-->通常情况下90%以上的单据都处于已打印状态，而仅有10%左右的处于未打印。而经常要使用的情形是查询未打印的单据并重新打印。  

-->基于上述情况，可以使用位图索引来解决，但此处我们讨论的是B树索引，故不考虑该情形(或者说你使用了非企业版Oracle，不支持位图索引)  

-->此处对于这类情形我们可以使用decode函数来解决这个问题  

  

-->更新表上的列，使之obj_id为1的行占绝大多数  

scott@ORCL> **update** t2 **set** obj_id=1 **where** obj_id **is** not null;  

  

11620 **rows** updated.  

  

-->更新表，使之obj_id为0的行占少部分  

scott@ORCL> **update** t2 **set** obj_id = 0 **where** obj_id **is** null;  

  

99 **rows** updated.  

  

scott@ORCL> **commit**;  

  

-->收集统计信息  

scott@ORCL> **exec** dbms_stats.gather_table_stats('SCOTT','T2',**cascade**=>**true**);  

  

PL/SQL **procedure** successfully completed.  

  

-->表t2上obj_id列的最终分布  

scott@ORCL> **select** obj_id,count(*) **from** t2 **group** **by** obj_id;  

  

​    OBJ_ID   COUNT(*)  

---------- ----------

​         1      11620  

​         0         99     

  

-->使用decode函数创建索引  

-->注意此处decode的使用，当obj_id非0值时，其值被赋予为null值，由于该null值不会存储到索引，因此大部分obj_id列值为1的不会被索引  

scott@ORCL> **create** **index** i_fn2_t2_obj_id **on** t2(decode(obj_id,0,0,null));  

  

**Index** created.  

  

-->收集索引上的统计信息  

scott@ORCL> **exec** dbms_stats.gather_index_stats('SCOTT','I_FN2_T2_OBJ_ID');  

  

PL/SQL **procedure** successfully completed.  

  

-->查看新索引的执行计划  

scott@ORCL> **set** autot trace exp;  

scott@ORCL> **select** count(*) **from** t2 **where** decode(obj_id,0,0,null) = 0;  

  

Execution Plan  

\----------------------------------------------------------  

Plan hash value: 1461308992  

  

\-------------------------------------------------------------------------------------  

| Id  | Operation         | **Name**            | **Rows**  | Bytes | Cost (%CPU)| **Time**     |  

\-------------------------------------------------------------------------------------  

|   0 | **SELECT** STATEMENT  |                 |     1 |     3 |     1   (0)| 00:00:01 |  

|   1 |  SORT AGGREGATE   |                 |     1 |     3 |            |          |  

|*  2 |   **INDEX** RANGE SCAN| I_FN2_T2_OBJ_ID |    98 |   294 |     1   (0)| 00:00:01 |  

\-------------------------------------------------------------------------------------  

  

Predicate Information (identified **by** operation id):  

\---------------------------------------------------  

  

   2 - access(DECODE("OBJ_ID",0,0,NULL)=0)  

  

-->当直接使用obj_id = 0来查询时使用的是普通的B树索引  

scott@ORCL> **select** count(*) **from** t2 **where** obj_id = 0;  

  

Execution Plan  

\----------------------------------------------------------  

Plan hash value: 1804118247  

  

\---------------------------------------------------------------------------------  

| Id  | Operation         | **Name**        | **Rows**  | Bytes | Cost (%CPU)| **Time**     |  

\---------------------------------------------------------------------------------  

|   0 | **SELECT** STATEMENT  |             |     1 |     3 |     1   (0)| 00:00:01 |  

|   1 |  SORT AGGREGATE   |             |     1 |     3 |            |          |  

|*  2 |   **INDEX** RANGE SCAN| I_T2_OBJ_ID |    99 |   297 |     1   (0)| 00:00:01 |  

\---------------------------------------------------------------------------------  

  

Predicate Information (identified **by** operation id):  

\---------------------------------------------------  

  

   2 - access("OBJ_ID"=0)     

  

-->当使用obj_id = 1来查询时走全表扫描，因为obj_id = 1占据表90%以上，由CBO特性决定了走全表扫描     

scott@ORCL> **select** * **from** t2 **where** obj_id = 1;  

  

Execution Plan  

\----------------------------------------------------------  

Plan hash value: 1513984157  

  

\--------------------------------------------------------------------------  

| Id  | Operation         | **Name** | **Rows**  | Bytes | Cost (%CPU)| **Time**     |  

\--------------------------------------------------------------------------  

|   0 | **SELECT** STATEMENT  |      | 11620 |   249K|    14   (8)| 00:00:01 |  

|*  1 |  **TABLE** ACCESS **FULL**| T2   | 11620 |   249K|    14   (8)| 00:00:01 |  

\--------------------------------------------------------------------------  

  

Predicate Information (identified **by** operation id):  

\---------------------------------------------------  

  

   1 - filter("OBJ_ID"=1)  

​     

-->表t2上所有索引的统计信息  

scott@ORCL> **select** index_name,index_type,blevel,leaf_blocks,num_rows,status,distinct_keys  

  2  **from** user_indexes **where** table_name='T2';  

​    

INDEX_NAME      INDEX_TYPE                         BLEVEL LEAF_BLOCKS   NUM_ROWS STATUS   DISTINCT_KEYS  

--------------- ------------------------------ ---------- ----------- ---------- -------- -------------

I_FN_T2_OBJ_ID  **FUNCTION**-BASED NORMAL                   1          40      11719 VALID                2  

I_NEW_T2_OBJ_ID **FUNCTION**-BASED NORMAL                   1          52      11719 VALID                2  

I_FN2_T2_OBJ_ID **FUNCTION**-BASED NORMAL                   0           1         99 VALID                1  

I_T2_OBJ_ID     NORMAL                                  1          40      11719 VALID                2  

  

-->从上面的结果可知，索引I_FN2_T2_OBJ_ID仅仅存储了99跳记录，且DISTINCT_KEYS值为1个，因为所有非0值的全部被置NULL。  

-->以上方法实现了索引压缩，避免了较大索引维护所需的开销，同时也提高了查询性能。  

-->Author : Robinson Cheng  

-->Blog :   <http://blog.csdn.net/robinson_0612>  

四、总结

无论是单列唯一索引或复合唯一索引，对于可以为null的列或复合null值，Oracle不会为其存储索引值。故在基于单列创建B树唯一索引或多列创建B树复合唯一索引的情形下，
**当列上允许为null值时：**
  where子句使用了基于is null的情形，其执行计划走全表扫描。
  where子句使用了基于is not null的情形，其执行计划走索引扫描。

**当列上不允许为null值时，存在非null约束：**
  where子句使用了基于is null的情行，其执行计划走索引扫描。
  where子句使用了基于is not null的情形，其执行计划走索引扫描。

​    1、对于用于连接或经常被谓词使用到的列应尽可能避免NULL值属性，因为它容易导致索引失效。

​    2、为需要使用NULL值的列添加缺省值(alter table tb modify(col default 'Y'))。

​    3、如果NULL值不可避免也不能使用缺省值，应考虑为该常用列使用nvl函数创建索引，或使用伪列来创建索引以提高查询性能。

​    4、对于复合索引应保证索引中至少有一列不为NULL值，还是因为全部列为NULL时不被索引存储，以保证使用is null是可以使用索引。

​    5、对于复合索引应保证索引列应使用数据类型长度最小的列来添加not null约束应节省磁盘空间。