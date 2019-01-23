# Oracle Hint

Hint 是Oracle 提供的一种SQL语法，它允许用户在SQL语句中插入相关的语法，从而影响SQL的执行方式。

因为Hint的特殊作用，所以对于开发人员不应该在代码中使用它，Hint 更像是Oracle提供给DBA用来分析问题的工具 。在SQL代码中使用Hint，可能导致非常严重的后果，因为数据库的数据是变化的，在某一时刻使用这个执行计划是最优的，在另一个时刻，却可能很差，这也是CBO 取代RBO的原因之一，规则是死的，而数据是时刻变化的，为了获得最正确的执行计划，只有知道表中数据的实际情况，通过计算各种执行计划的成本，则其最优，才是最科学的，这也是CBO的工作机制。 在SQL代码中加入Hint，特别是性能相关的Hint是很危险的做法。

Hints 

Hints are comments in a SQL statement that pass instructions to the Oracle Database optimizer. The optimizer uses these hints to choose an execution plan for the statement, unless some condition exists that prevents the optimizer from doing so.

Hints were introduced in Oracle7, when users had little recourse if the optimizer generated suboptimal plans. Now Oracle provides a number of tools, including the SQL Tuning Advisor, SQL plan management, and SQL Performance Analyzer, to help you address performance problems that are not solved by the optimizer. Oracle strongly recommends that you use those tools rather than hints. The tools are far superior to hints, because when used on an ongoing basis, they provide fresh solutions as your data and database environment change.

Hints should be used sparingly, and only after you have collected statistics on the relevant tables and evaluated the optimizer plan without hints using the EXPLAIN PLAN statement. Changing database conditions as well as query performance enhancements in subsequent releases can have significant impact on how hints in your code affect performance.

The remainder of this section provides information on some commonly used hints. If you decide to use hints rather than the more advanced tuning tools, be aware that any short-term benefit resulting from the use of hints may not continue to result in improved performance over the long term.

Oracle 联机文档对Hint的说明：

<http://download.oracle.com/docs/cd/E11882_01/server.112/e10592/sql_elements006.htm#SQLRF50705>

之前整理的一篇文章：

常见Oracle HINT的用法

<http://blog.csdn.net/tianlesoftware/archive/2009/12/13/4969702.aspx>

在使用Hint时需要注意的一点是，并非任何时刻Hint都起作用。 导致HINT 失效的原因有如下2点：

（1） 如果CBO 认为使用Hint 会导致错误的结果时，Hint将被忽略。

如索引中的记录因为空值而和表的记录不一致时，结果就是错误的，会忽略hint。

（2） 如果表中指定了别名，那么Hint中也必须使用别名，否则Hint也会忽略。

Select /*+full(a)*/ * from t a; -- 使用hint

Select /*+full(t) */ * from t a; --不使用hint

根据hint的功能，可以分成如下几类：

| **Hint**       | **Hint** **语法** |
| -------------- | ----------------- |
| 优化器模式提示 | ALL_ROWS   Hint   |
|                | FIRST_ROWS   Hint |
|                | RULE   Hint       |

| 访问路径提示   | CLUSTER   Hint                 |
| -------------- | ------------------------------ |
|                | FULL   Hint                    |
|                | HASH   Hint                    |
|                | INDEX   Hint                   |
|                | NO_INDEX   Hint                |
|                | INDEX_ASC   Hint               |
|                | INDEX_DESC   Hint              |
|                | INDEX_COMBINE   Hint           |
|                | INDEX_FFS   Hint               |
|                | INDEX_SS   Hint                |
|                | INDEX_SS_ASC   Hint            |
|                | INDEX_SS_DESC   Hint           |
|                | NO_INDEX_FFS   Hint            |
|                | NO_INDEX_SS   Hint             |
|                | ORDERED   Hint                 |
|                | LEADING   Hint                 |
|                | USE_HASH   Hint                |
|                | NO_USE_HASH   Hint             |
| 表连接顺序提示 | USE_MERGE   Hint               |
|                | NO_USE_MERGE   Hint            |
|                | USE_NL   Hint                  |
|                | USE_NL_WITH_INDEX   Hint       |
|                | NO_USE_NL   Hint               |
| 表关联方式提示 | PARALLEL   Hint                |
|                | NO_PARALLEL   Hint             |
|                | PARALLEL_INDEX   Hint          |
|                | NO_PARALLEL_INDEX   Hint       |
|                | PQ_DISTRIBUTE   Hint           |
| 并行执行提示   | FACT   Hint                    |
|                | NO_FACT   Hint                 |
|                | MERGE   Hint                   |
|                | NO_MERGE   Hint                |
|                | NO_EXPAND   Hint               |
|                | USE_CONCAT   Hint              |
| 查询转换提示   | REWRITE   Hint                 |
|                | NO_REWRITE   Hint              |
|                | UNNEST   Hint                  |
|                | NO_UNNEST   Hint               |
|                | STAR_TRANSFORMATION   Hint     |
|                | NO_STAR_TRANSFORMATION   Hint  |
|                | NO_QUERY_TRANSFORMATION   Hint |
|                | APPEND   Hint                  |
|                | NOAPPEND   Hint                |
|                | CACHE   Hint                   |
|                | NOCACHE   Hint                 |
|                | CURSOR_SHARING_EXACT   Hint    |
| 其他Hint       | DRIVING_SITE   Hint            |
|                | DYNAMIC_SAMPLING   Hint        |
|                | PUSH_PRED   Hint               |
|                | NO_PUSH_PRED   Hint            |
|                | PUSH_SUBQ   Hint               |
|                | NO_PUSH_SUBQ   Hint            |
|                | PX_JOIN_FILTER   Hint          |
|                | NO_PX_JOIN_FILTER   Hint       |
|                | NO_XML_QUERY_REWRITE   Hint    |
|                | QB_NAME   Hint                 |
|                | MODEL_MIN_ANALYSIS   Hint      |

一． 和优化器相关的Hint

Oracle 允许在系统级别，会话级别和SQL中（hint）优化器类型：

系统级别：

   1: SQL>alter system set optimizer_mode=all_rows;

会话级别：

SQL>alter system set optimizer_mode=all_rows;

关于优化器，参考：

Oracle Optimizer CBO RBO

<http://blog.csdn.net/tianlesoftware/archive/2010/08/19/5824886.aspx>

1.1 ALL_ROWS 和FIRST_ROWS（n） -- CBO 模式

对于OLAP系统，这种系统中通常都是运行一些大的查询操作，如统计，报表等任务。 这时优化器模式应该选择ALL_ROWS. 对于一些分页显示的业务，就应该用FIRST_ROWS（n）。 如果是一个系统上运行这两种业务，那么就需要在SQL 用hint指定优化器模式。

如：

SQL> select /* + all_rows*/ * from dave;

SQL> select /* + first_rows(20)*/ * from dave;

1.2 RULE Hint -- RBO 模式

尽管Oracle 10g已经弃用了RBO，但是仍然保留了这个hint。 它允许在CBO 模式下使用RBO 对SQL 进行解析。

如：

SQL> show parameter optimizer_mode

NAME TYPE VALUE

------------------------------------ ----------- ------------------------------

optimizer_mode string ALL_ROWS

SQL> set autot trace exp;

SQL> select /*+rule */ * from dave;

执行计划

\----------------------------------------------------------

Plan hash value: 3458767806

\----------------------------------

| Id | Operation | Name |

\----------------------------------

| 0 | SELECT STATEMENT | |

| 1 | TABLE ACCESS FULL| DAVE |

\----------------------------------

Note

\-----

\- rule based optimizer used (consider using cbo) -- 这里提示使用RBO

SQL>

二． 访问路径相关的Hint

这一部分hint 将直接影响SQL 的执行计划，所以在使用时需要特别小心。 该类Hint对DBA分析SQL性能非常有帮助，DBA 可以让SQL使用不同的Hint得到不同的执行计划，通过比较不同的执行计划来分析当前SQL性能。

2.1 FULL Hint

该Hint告诉优化器对指定的表通过全表扫描的方式访问数据。

示例：

SQL> select /*+full(dave) */ * from dave;

要注意，如果表有别名，在hint里也要用别名， 这点在前面已经说明。

2.2 INDEX Hint

Index hint 告诉优化器对指定的表通过索引的方式访问数据，当访问索引会导致结果集不完整时，优化器会忽略这个Hint。

示例：

SQL> select /*+index(dave index_dave) */ * from dave where id>1;

谓词里有索引字段，才会用索引。

2.3 NO_INDEX Hint

No_index hint 告诉优化器对指定的表不允许使用索引。

示例：

SQL> select /*+no_index(dave index_dave) */ * from dave where id>1;

2.4 INDEX_DESC Hint

该Hint 告诉优化器对指定的索引使用降序方式访问数据，当使用这个方式会导致结果集不完整时，优化器将忽略这个索引。

示例：

SQL> select /*+index_desc(dave index_dave) */ * from dave where id>1;

2.5 INDEX_COMBINE Hint

该Hint告诉优化器强制选择位图索引，当使用这个方式会导致结果集不完整时，优化器将忽略这个Hint。

示例：

SQL> select /*+ index_combine(dave index_bm) */ * from dave;

2.6 INDEX_FFS Hint

该hint告诉优化器以INDEX_FFS(INDEX Fast Full Scan)的方式访问数据。当使用这个方式会导致结果集不完整时，优化器将忽略这个Hint。

示例：

SQL> select /*+ index_ffs(dave index_dave) */ id from dave where id>0;

2.7 INDEX_JOIN Hint

索引关联，当谓词中引用的列上都有索引时，可以通过索引关联的方式来访问数据。

示例：

SQL> select /*+ index_join(dave index_dave index_bm) */ * from dave where id>0 and name='安徽安庆';

2.8 INDEX_SS Hint

该Hint强制使用index skip scan 的方式访问索引，从Oracle 9i开始引入这种索引访问方式，当在一个联合索引中，某些谓词条件并不在联合索引的第一列时（或者谓词并不在联合索引的第一列时），可以通过index skip scan 来访问索引获得数据。 当联合索引第一列的唯一值很小时，使用这种方式比全表扫描效率要高。当使用这个方式会导致结果集不完整时，优化器将忽略这个Hint。

示例：

SQL> select /*+ index_ss(dave index_union) */ * from dave where id>0;

三． 表关联顺序的Hint

表之间的连接方式有三种。 具体参考blog：

多表连接的三种方式详解 HASH JOIN MERGE JOIN NESTED LOOP

<http://blog.csdn.net/tianlesoftware/archive/2010/08/20/5826546.aspx>

3.1 LEADING hint

在一个多表关联的查询中，该Hint指定由哪个表作为驱动表，告诉优化器首先要访问哪个表上的数据。

示例：

SQL> select /*+leading(t1,t) */ * from scott.dept t,scott.emp t1 where t.deptno=t1.deptno;

SQL> select /*+leading(t,t1) */ * from scott.dept t,scott.emp t1 where t.deptno=t1.deptno;

\--------------------------------------------------------------------------------

| Id | Operation | Name | Rows | Bytes | Cost (%CPU)| Ti

\--------------------------------------------------------------------------------

| 0 | SELECT STATEMENT | | 14 | 812 | 6 (17)| 00

| 1 | MERGE JOIN | | 14 | 812 | 6 (17)| 00

| 2 | TABLE ACCESS BY INDEX ROWID| DEPT | 4 | 80 | 2 (0)| 00

| 3 | INDEX FULL SCAN | PK_DEPT | 4 | | 1 (0)| 00

|* 4 | SORT JOIN | | 14 | 532 | 4 (25)| 00

| 5 | TABLE ACCESS FULL | EMP | 14 | 532 | 3 (0)| 00

\--------------------------------------------------------------------------------

3.2 ORDERED Hint

该hint 告诉Oracle 按照From后面的表的顺序来选择驱动表，Oracle 建议在选择驱动表上使用Leading，它更灵活一些。

SQL> select /*+ordered */ * from scott.dept t,scott.emp t1 where t.deptno=t1.deptno;

四． 表关联操作的Hint

4.1 USE_HASH,USE_NL,USE_MERGE hint

表之间的连接方式有三种。 具体参考blog：

多表连接的三种方式详解 HASH JOIN MERGE JOIN NESTED LOOP

<http://blog.csdn.net/tianlesoftware/archive/2010/08/20/5826546.aspx>

这三种关联方式是多表关联中主要使用的关联方式。 通常来说，当两个表都比较大时，Hash Join的效率要高于嵌套循环（nested loops）的关联方式。

Hash join的工作方式是将一个表（通常是小一点的那个表）做hash运算，将列数据存储到hash列表中，从另一个表中抽取记录，做hash运算，到hash 列表中找到相应的值，做匹配。

Nested loops 工作方式是从一张表中读取数据，访问另一张表（通常是索引）来做匹配，nested loops适用的场合是当一个关联表比较小的时候，效率会更高。

Merge Join 是先将关联表的关联列各自做排序，然后从各自的排序表中抽取数据，到另一个排序表中做匹配，因为merge join需要做更多的排序，所以消耗的资源更多。 通常来讲，能够使用merge join的地方，hash join都可以发挥更好的性能。

USE_HASH,USE_NL,USE_MERGE 这三种hint 就是告诉优化器使用哪种关联方式。

示例如下：

SQL> select /*+use_hash(t,t1) */ * from scott.dept t,scott.emp t1 where t.deptno=t1.deptno;

SQL> select /*+use_nl(t,t1) */ * from scott.dept t,scott.emp t1 where t.deptno=t1.deptno;

SQL> select /*+use_merge(t,t1) */ * from scott.dept t,scott.emp t1 where t.deptno=t1.deptno;

4.2 NO_USE_HASH,NO_USE_NL,NO_USE_MERGE HINT

分别禁用对应的关联方式。

示例：

SQL> select /*+no_use_merge(t,t1) */ * from scott.dept t,scott.emp t1 where t.deptno=t1.deptno;

SQL> select /*+no_use_nl(t,t1) */ * from scott.dept t,scott.emp t1 where t.deptno=t1.deptno;

SQL> select /*+no_use_hash(t,t1) */ * from scott.dept t,scott.emp t1 where t.deptno=t1.deptno;

五． 并行执行相关的Hint

5.1 PARALLEL HINT

指定SQL 执行的并行度，这个值会覆盖表自身设定的并行度，如果这个值为default，CBO使用系统参数值。

示例：

SQL> select /*+parallel(t 4) */ * from scott.dept t;

关于表的并行度，我们在创建表的时候可以指定，如：

SQL> CREATE TABLE Anqing

2 (

3 name VARCHAR2 (10)

4 )

5 PARALLEL 2;

表已创建。

SQL> select degree from all_tables where table_name = 'ANQING'; -- 查看表的并行度

DEGREE

\--------------------

2

SQL> alter table anqing parallel(degree 3); -- 修改表的并行度

表已更改。

SQL> select degree from all_tables where table_name = 'ANQING';

DEGREE

\--------------------

3

SQL> alter table anqing noparallel; -- 取消表的并行度

表已更改。

SQL> select degree from all_tables where table_name = 'ANQING';

DEGREE

\--------------------

1

5.2 NO_PARALLEL HINT

在SQL中禁止使用并行。

示例：

SQL> select /*+ no_parallel(t) */ * from scott.dept t;

六． 其他方面的一些Hint

6.1 APPEND HINT

提示数据库以直接加载的方式（direct load）将数据加载入库。

示例：

Insert /*+append */ into t as select * from all_objects;

这个hint 用的比较多。 尤其在插入大量的数据，一般都会用此hint。

Oracle 插入大量数据

<http://blog.csdn.net/tianlesoftware/archive/2009/10/30/4745144.aspx>

6.2 DYNAMIC_SAMPLING HINT

提示SQL 执行时动态采样的级别。 这个级别从0-10，它将覆盖系统默认的动态采样级别。

示例：

SQL> select /*+ dynamic_sampling(t 2) */ * from scott.emp t where t.empno>0;

6.3 DRIVING_SITE HINT

这个提示在分布式数据库操作中比较有用，比如我们需要关联本地的一张表和远程的表：

Select /* + driving_site(departmetns) */ * from employees,departments@dblink where

employees .department_id = departments.department_id;

如果没有这个提示，Oracle 会在远端机器上执行departments 表查询，将结果送回本地，再和employees表关联。 如果使用driving_site(departments), Oracle将查询本地表employees，将结果送到远端，在远端将数据库上的表与departments关联，然后将查询的结果返回本地。

如果departments查询结果很大，或者employees查询结果很小，并且两张表关联之后的结果集很小，那么就可以考虑把本地的结果集发送到远端。 在远端执行完后，在将较小的最终结果返回本地。

6.4 CACHE HINT

在全表扫描操作中，如果使用这个提示，Oracle 会将扫描的到的数据块放到LRU（least recently Used： 最近很少被使用列表，是Oracle 判断内存中数据块活跃程度的一个算法）列表的最被使用端（数据块最活跃端），这样数据块就可以更长时间地驻留在内存当中。 如果有一个经常被访问的小表，这个设置会提高查询的性能；同时CACHE也是表的一个属性，如果设置了表的cache属性，它的作用和hint一样，在一次全表扫描之后，数据块保留在LRU列表的最活跃端。

示例：

SQL> select /*+full(t) cache (t) */ * from scott.emp;

小结：

对于DBA来讲，掌握一些Hint操作，在实际性能优化中有很大的好处，比如我们发现一条SQL的执行效率很低，首先我们应当查看当前SQL的执行计划，然后通过hint的方式来改变SQL的执行计划，比较这两条SQL 的效率，作出哪种执行计划更优，如果当前执行计划不是最优的，那么就需要考虑为什么CBO 选择了错误的执行计划。当CBO 选择错误的执行计划，我们需要考虑表的分析是否是最新的，是否对相关的列做了直方图，是否对分区表做了全局或者分区分析等因素。

关于执行计划参考：

Oracle Explain Plan

<http://blog.csdn.net/tianlesoftware/archive/2010/08/20/5827245.aspx>

总之，在处理问题时，我们要把问题掌握在可控的范围内，不能将问题扩大化，甚至失控。 作为一个DBA，需要的扎实的基本功，还有胆大心细，遇事不慌。