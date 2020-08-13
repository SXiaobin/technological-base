# Oracle-导入导出

## 导出脚本
可以将以下脚本复制另存为 .cmd 文件，即可执行。
```basic
echo on

call exp DATABASE4300/DATABASE4300@34 file=e:34dbfile\DATABASE4300.dmp owner=(DATABASE4300) log=e:34dbfile\DATABASE4300.log

call exp DATABASE4310/DATABASE4310@34 file=e:34dbfile\DATABASE4310.dmp owner=(DATABASE4310) log=e:34dbfile\DATABASE4310.log

call exp DATABASE4320/DATABASE4320@34 file=e:34dbfile\DATABASE4320.dmp owner=(DATABASE4320) log=e:34dbfile\DATABASE4320.log

call exp DATABASE4321/DATABASE4321@34 file=e:34dbfile\DATABASE4321.dmp owner=(DATABASE4321) log=e:34dbfile\DATABASE4321.log

call exp DATABASE4322/DATABASE4322@34 file=e:34dbfile\DATABASE4322.dmp owner=(DATABASE4322) log=e:34dbfile\DATABASE4322.log


call exp PM4322/PM4322@34 file=e:34dbfile\PM4322.dmp owner=(PM4322) log=e:34dbfile\PM4322.log

call exp WMS4301/WMS4301@34 file=e:34dbfile\WMS4301.dmp owner=(WMS4301) log=e:34dbfile\WMS4301.log

call exp WMS4320/WMS4320@34 file=e:34dbfile\WMS4320.dmp owner=(WMS4320) log=e:34dbfile\WMS4320.log

call exp WMS4330/WMS4330@34 file=e:34dbfile\WMS4330.dmp owner=(WMS4330) log=e:34dbfile\WMS4330.log

call exp WMS4330TEST/WMS4330TEST@34 file=e:34dbfile\WMS4330TEST.dmp owner=(WMS4330TEST) log=e:34dbfile\WMS4330TEST.log

pause


exp pm4database/pm4database@246 file=e:34dbfile\PM4322.dmp owner=(PM4322) log=e:34dbfile\PM4322.log

```

### Trouble Shooting

#### Problem: 导出过程中，出现汉字缺失或者编码长度不够

##### **Cause**: 

字符集编码问题导致的

##### **Solution**:

可以在导出脚本中添加如下命令，设置导出编码集。

```
@echo on

C:

set NLS_LANG=AMERICAN_AMERICA.AL32UTF8
set NLS_NCHAR_CHARACTERSET=UTF8
exp vamames/vamames@172.18.99.100/VAMAMES owner=vamames file=E:\exp.dmp log=E:\log.log
```

#### Problem: ORA-02304 - IMP同库Type对象导入报错

##### **Cause**: 

> Type是我们经常使用的数据库对象结构。我们在实际中，可以单独定义type类型，之后在PL/SQL代码或者数据表中使用。
>
>  
>
> 在一个偶然的机会让笔者发现使用Type类型在数据exp/imp中的麻烦。当我们使用exp/imp工具进行同数据库实例（Instance）不同Schema之间数据拷贝时，如果Schema中有type类型，就会出现问题错误。
>
>  
>
> 具体我们还是通过一系列的实验进行证明。
>
> 1、实验环境准备
>
>  我们使用10gR2作为实验数据库。
>
>  
>
> SQL> conn scott/tiger@ots;
>
> Connected to [Oracle](http://www.linuxidc.com/topicnews.aspx?tid=12) Database 10g Enterprise Edition Release 10.2.0.1.0
>
> Connected as scott
>
>  
>
> SQL> select * from v$version;
>
> BANNER
>
> \----------------------------------------------------------------
>
> Oracle Database 10g Enterprise Edition Release 10.2.0.1.0 - Prod
>
> PL/SQL Release 10.2.0.1.0 - Production
>
> CORE       10.2.0.1.0        Production
>
>  
>
>  我们首先将scott用户schema的所有对象导出。注意，当前scott下存在一些数据type对象。
>
>  
>
> SQL> select type_name, type_oid, typecode from user_types;
>
>  
>
> TYPE_NAME                     TYPE_OID                        TYPECODE
>
> ------------------------------ -------------------------------- ------------------------------
>
> CUST_ADDRESS_TYPE_NEW         0239FC5ABD78464D8D6C4D7085E2F549 OBJECT
>
> T_REC_TEST                    428A1B3C7E1E4A3CB2063B93623693EA OBJECT
>
> T_REC_TABLE                   D9AFD3FAE0A54964B1684CA28C69CEED COLLECTION
>
> T_TYP                         8E294AB7CC28493A94FF82791A376379 OBJECT
>
> N_TYP                         338172B836854BAB8C26D4C27B5908F1 OBJECT
>
>  
>
>  在Oracle中，每一个type都会分配出唯一的oid编号，作为一种内部标志。下面，我们使用exp工具将scott用户对象导出。
>
>  
>
> D:\>exp scott/tiger@ots file=scott_20120606.dmp indexes=y rows=y compress=y cons
>
> traints=y wner=scott
>
>  
>
> Export: Release 10.2.0.1.0 - Production on星期三6月6 17:22:16 2012
>
> Copyright (c) 1982, 2005, Oracle. All rights reserved.
>
>  
>
> 连接到: Oracle Database 10g Enterprise Edition Release 10.2.0.1.0 - Production
>
> With the Partitioning, OLAP and Data Mining options
>
> 已导出ZHS16GBK字符集和AL16UTF16 NCHAR字符集
>
>  
>
> 即将导出指定的用户...
>
> .正在导出pre-schema过程对象和操作
>
> .正在导出用户SCOTT的外部函数库名
>
> .导出PUBLIC类型同义词
>
> .正在导出专用类型同义词
>
> .正在导出用户SCOTT的对象类型定义
>
> (篇幅原因，部分省略……)
>
> 成功终止导出,没有出现警告。
>
>  
>
> D:\>
>
> 
>
> 之后，我们创建同数据库用户scottback。
>
>  
>
> SQL> create user scottback identified by scottback;
>
> User created
>
>  
>
> SQL> grant resource to scottback;
>
> Grant succeeded
>
>  
>
> SQL> grant connect to scottback;
>
> Grant succeeded
>
>  
>
> SQL> grant exp_full_database to scottback;
>
> Grant succeeded
>
>  
>
> SQL> grant imp_full_database to scottback;
>
> Grant succeeded
>
>  
>
>  
>
> 2、数据导入
>
>  
>
> 当我们试图将数据导入到相同数据库时，出现报错。
>
>   
>
> D:\>imp scottback/scottback@ots file=scott_20120606.dmp indexes=y rows=y constra
>
> ints=y ignore=y fromuser=scott touser=scottback
>
>  
>
> Import: Release 10.2.0.1.0 - Production on星期三6月6 17:34:21 2012
>
>  
>
> Copyright (c) 1982, 2005, Oracle. All rights reserved.
>
> 连接到: Oracle Database 10g Enterprise Edition Release 10.2.0.1.0 - Production
>
> With the Partitioning, OLAP and Data Mining options
>
>  
>
> 经由常规路径由EXPORT:V10.02.01创建的导出文件
>
>  
>
> 警告:这些对象由SCOTT导出,而不是当前用户
>
>  
>
> 已经完成ZHS16GBK字符集和AL16UTF16 NCHAR字符集中的导入
>
> IMP-00017:由于ORACLE错误2304,以下语句失败:
>
>  "CREATE TYPE "T_REC_TEST" TIMESTAMP '2010-12-21:18:17:30' OID '428A1B3C7E1E4"
>
>  "A3CB2063B93623693EA'  as object("
>
>  "id number);"
>
>  ""
>
>  ""
>
> IMP-00003:遇到ORACLE错误2304
>
> ORA-02304:无效的对象标识符文字
>
> IMP-00017:由于ORACLE错误2304,以下语句失败:
>
>  "CREATE TYPE "T_REC_TABLE" TIMESTAMP '2010-12-21:18:17:33' OID 'D9AFD3FAE0A5"
>
>  "4964B1684CA28C69CEED'  as table of t_rec_test;"
>
>  ""
>
>  ""
>
> IMP-00003:遇到ORACLE错误2304
>
> ORA-02304:无效的对象标识符文字
>
> IMP-00017:由于ORACLE错误2304,以下语句失败:
>
>  "CREATE TYPE "T_TYP" TIMESTAMP '2012-03-07:10:47:03' OID '8E294AB7CC28493A94"
>
>  "FF82791A376379'  as object (id number);"
>
>  ""
>
>  ""
>
> IMP-00003:遇到ORACLE错误2304
>
> ORA-02304:无效的对象标识符文字
>
> IMP-00017:由于ORACLE错误2304,以下语句失败:
>
>  "CREATE TYPE "N_TYP" TIMESTAMP '2012-03-07:11:03:01' OID '338172B836854BAB8C"
>
>  "26D4C27B5908F1'  as object (t_id number,t_name varchar2(10),t_addr varchar"
>
>  "2(20));"
>
>  ""
>
>  ""
>
> IMP-00003:遇到ORACLE错误2304
>
> ORA-02304:无效的对象标识符文字
>
> IMP-00017:由于ORACLE错误2304,以下语句失败:
>
>  "CREATE TYPE "CUST_ADDRESS_TYPE_NEW" TIMESTAMP '2012-05-23:16:15:03' OID '02"
>
>  "39FC5ABD78464D8D6C4D7085E2F549'  as object"
>
>  "(street_address varchar2"
>
>  "(40),"
>
>  "postal_code varchar2(10)"
>
>  ",city varchar2(30)"
>
>  ",state_province varchar2(10)"
>
>  ",country_id char(2)"
>
>  ");"
>
>  ""
>
>  ""
>
> IMP-00003:遇到ORACLE错误2304
>
> ORA-02304:无效的对象标识符文字
>
> (篇幅原因，部分省略…..)
>
>  
>
> ORA-02270:此列列表的唯一或主键不匹配
>
> 即将启用约束条件...
>
> 成功终止导入,但出现警告。
>
>  
>
>  从日志信息上，我们看到在创建type类型变量的时候，Oracle报错2304。利用oerr工具，我们可以检查错误信息。
>
>  
>
>  [oracle@bspdev ~]$ oerr ora 2304
>
> 02304, 00000, "invalid object identifier literal"
>
> // *Cause: An attempt was made to enter an object identifier literal for
>
> //         CREATE TYPE that is either:
>
> //         - not a string of 32 hexadecimal characters
>
> //         - an object identifier that already identifies an existing
>
> //               object
>
> //         - an object identifier different from the original object
>
> //                identifier already assigned to the type
>
> // *Action: Do not specify the object identifier clause or specify a 32
>
> //         hexadecimal-character object identifier literal that is unique
>
> //         or identical to the originally assigned object identifier. Then
>
> //         retry the operation.
>
>  
>
>   从字面的情况看，是创建type的命令语句出现错误。从脚本的信息上，的确显示的script中创建type的语句是很特殊，中间有timestamp和oid信息。而且与原来schema中的相对应。
>
> 那么，这个特殊的语法结构是否是文件中特有的呢？我们使用show参数，将dmp脚本输出。
>
>  
>
>  D:\>imp scottback/scottback@ots file=scott_20120606.dmp indexes=y rows=y constra
>
> ints=y ignore=y show=y fromuser=scott touser=scottback log=imp.log
>
>  
>
> 连接到: [Oracle](http://www.linuxidc.com/topicnews.aspx?tid=12) Database 10g Enterprise Edition Release 10.2.0.1.0 - Production
>
> With the Partitioning, OLAP and Data Mining options
>
>  
>
> 经由常规路径由EXPORT:V10.02.01创建的导出文件
>
> 警告:这些对象由SCOTT导出,而不是当前用户
>
>  
>
> 已经完成ZHS16GBK字符集和AL16UTF16 NCHAR字符集中的导入
>
> "CREATE TYPE "T_REC_TEST"TIMESTAMP '2010-12-21:18:17:30' OID '428A1B3C7E1E4"
>
>  "A3CB2063B93623693EA'  as object("
>
>  "id number);"
>
>  ""
>
>   "CREATE TYPE "T_REC_TABLE"TIMESTAMP '2010-12-21:18:17:33' OID 'D9AFD3FAE0A5"
>
>  "4964B1684CA28C69CEED'  as table of t_rec_test;"
>
>  ""
>
> "CREATE TYPE "T_TYP"TIMESTAMP '2012-03-07:10:47:03' OID '8E294AB7CC28493A94"
>
>  "FF82791A376379'  as object (id number);"
>
>  ""
>
> "CREATE TYPE "N_TYP"TIMESTAMP '2012-03-07:11:03:01' OID '338172B836854BAB8C"
>
>  "26D4C27B5908F1'  as object (t_id number,t_name varchar2(10),t_addr varchar"
>
>  "2(20));"
>
>  
>
>  
>
> 看来，timestamp和oid的确是DUMP文件的一部分。也就是说，Oracle在imp type类型的时候，要将原有的timestamp和oid连带的转移到新的数据环境中。
>
>  
>
> 那么，如果我们是转移到其他数据环境下，是否有问题呢？答案是否定的，经过实验，只要不是相同数据库，imp操作都是正常的。
>
>  
>
> 问题的关键在于oid，从格式上看，OID是一个类似于GUID的字符串。按照GUID生成规则，GUID是不可能重复的。笔者猜测在Oracle内部，要求type类型不管schema归属，每一个type都必须有一个唯一的OID编号。当我们在一个数据库中强制插入两个相同oid的type时，系统自然报错。
>
>  
>
> 在MOS中，笔者也找到了相应的依据。
>
>  
>
> [ID 1066139.6]
>
> In brief, if the FROMUSER's object types already exist on the target instance, errors occur because the object identifiers (OIDs) of the TOUSER's object types already exist. Within a single database instance, object identifiers (OIDs) must be unique. As a result, the error causes Import will skip the creation of relational tables with columns of the pre-existing user defined type.

##### **Solution 1**: 

> 综合各方面的意见，关键问题在于导出的type携带有唯一的oid信息，并且需要导入到同库schema中。在使用exp/imp的情况下，我们是没有什么很好的方法。最直接的做法就是将数据库中冲突的type和相关联的对象删除，这样做不是一般场景可以支持的。
>
>  
>
> 在MOS中提供了一些折中方法，其中一个是在imp之前，就手工的将type对象创建好。这样最多在imp设置ignore=y的时候报错对象重复。
>
>  
>
> --手工创建type
>
> SQL> conn scottback/scottback@ots;
>
> Connected to Oracle Database 10g Enterprise Edition Release 10.2.0.1.0
>
> Connected as scottback
>
>  
>
> SQL> create or replace type cust_address_type_new as object
>
>  2 (
>
>  3   street_address varchar2(40),
>
>  4   postal_code   varchar2(10),
>
>  5   city          varchar2(30),
>
>  6   state_province varchar2(10),
>
>  7   country_id    char(2)
>
>  8 )
>
>  9 ;
>
>  10 /
>
> Type created
>
>  
>
> SQL> create or replace type n_typ as object(t_id number, t_name varchar2(10), t_addr varchar2(20));
>
>  2 /
>
>  
>
> Type created
>
>  
>
> SQL>
>
> SQL> create or replace type t_rec_test as object(id number);
>
>  2 /
>
>  
>
> Type created
>
>  
>
> SQL> create or replace type t_typ as object(id number);
>
>  2 /
>
>  
>
> Type created
>
>  
>
> SQL>
>
> SQL> create or replace type t_rec_table as table of t_rec_test;
>
>  2 /
>
>  
>
> Type created
>
>  
>
> --导入操作；
>
> D:\>imp scottback/scottback@ots file=scott_20120606.dmp indexes=y rows=y constraints=y ignore=y fromuser=scott touser=scottback log=res.log
>
>  
>
>  
>
> 警告:这些对象由SCOTT导出,而不是当前用户
>
>  
>
> 已经完成ZHS16GBK字符集和AL16UTF16 NCHAR字符集中的导入
>
> IMP-00061:警告:对象类型"SCOTTBACK"."T_REC_TEST"已经以不同标识符存在
>
>  "CREATE TYPE "T_REC_TEST" TIMESTAMP '2010-12-21:18:17:30' OID '428A1B3C7E1E4"
>
>  "A3CB2063B93623693EA'  as object("
>
>  "id number);"
>
>  ""
>
>  ""
>
> IMP-00061:警告:对象类型"SCOTTBACK"."T_REC_TABLE"已经以不同标识符存在
>
>  "CREATE TYPE "T_REC_TABLE" TIMESTAMP '2010-12-21:18:17:33' OID 'D9AFD3FAE0A5"
>
>  "4964B1684CA28C69CEED'  as table of t_rec_test;"
>
>  ""
>
>  ""
>
> IMP-00061:警告:对象类型"SCOTTBACK"."T_TYP"已经以不同标识符存在
>
>  "CREATE TYPE "T_TYP" TIMESTAMP '2012-03-07:10:47:03' OID '8E294AB7CC28493A94"
>
>  "FF82791A376379'  as object (id number);"
>
>  ""
>
>  ""
>
> IMP-00061:警告:对象类型"SCOTTBACK"."N_TYP"已经以不同标识符存在
>
>  "CREATE TYPE "N_TYP" TIMESTAMP '2012-03-07:11:03:01' OID '338172B836854BAB8C"
>
>  "26D4C27B5908F1'  as object (t_id number,t_name varchar2(10),t_addr varchar"
>
>  "2(20));"
>
>  ""
>
>  ""
>
> IMP-00061:警告:对象类型"SCOTTBACK"."CUST_ADDRESS_TYPE_NEW"已经以不同标识符存在
>
>  "CREATE TYPE "CUST_ADDRESS_TYPE_NEW" TIMESTAMP '2012-05-23:16:15:03' OID '02"
>
>  "39FC5ABD78464D8D6C4D7085E2F549'  as object"
>
>  "(street_address varchar2"
>
>  "(40),"
>
>  "postal_code varchar2(10)"
>
>  ",city varchar2(30)"
>
>  ",state_province varchar2(10)"
>
>  ",country_id char(2)"
>
>  ");"
>
>  ""
>
>  ""
>
> . .正在导入表                            "A"导入了          1行
>
> IMP-00063:警告:跳过表"SCOTTBACK"."ADDRESS_TABLE",因为无法创建对象类型"SCOTTBACK"."CUST_ADDRESS_TYPE_NEW"或它具有不同的标识符
>
> . .正在导入表                            "B"导入了          2行
>
> . .正在导入表                        "BONUS"导入了          0行
>
> . .正在导入表                      "BO_TEST"导入了          0行
>
> . .正在导入表                 "CHAINED_ROWS"
>
> 注:表包含ROWID列,其值可能已废弃导入了          0行
>
> . .正在导入表                        "CHILD"导入了          0行
>
> . .正在导入表                       "CURSOR"导入了          3行
>
> IMP-00063:警告:跳过表"SCOTTBACK"."CUSTOMER_ADDRESSES",因为无法创建对象类型"SCOTTBACK"."CUST_ADDRESS_TYPE_NEW"或它具有不同的标识符
>
>  
>
>  
>
>  
>
> 这样做的确可以避免报错。但是后果也是存在的，如果这些导入的type存在依赖对象。如数据表列、存储过程代码依赖于type。虽然手工创建了原有type，但是这些对象也不会使用创建好的type对象。笔者猜测这就是oid的作用。
>
>  
>
>  
>
> SQL> select name, type, DEPENDENCY_TYPE from user_dependencies where REFERENCED_NAME in (select type_name from user_types);
>
>  
>
> NAME                          TYPE             DEPENDENCY_TYPE
>
> ------------------------------ ----------------- ---------------
>
> CUSTOMER_ADDRESSES            TABLE            REF
>
> ADDRESS_TABLE                 TABLE            HARD
>
> F_SPILE                       FUNCTION         HARD
>
> T_REC_TABLE                   TYPE             HARD
>
> F_SPILE                       FUNCTION         HARD
>
> N_T                           TABLE            HARD
>
>  
>
> 6 rows selected

##### Solution 2:

> 我们借助Oracle 10g提出的数据泵（Data Dump）工具，是可以避免这个问题的。
>
>  
>
> 1、环境准备
>
>  
>
> 我们同样适用Oracle 11gR2进行试验。
>
>  
>
>  
>
> SQL> select * from v$version;
>
> BANNER
>
> \--------------------------------------------------------------------------------
>
> Oracle Database 11g Enterprise Edition Release 11.2.0.1.0 - Production
>
> PL/SQL Release 11.2.0.1.0 - Production
>
> CORE       11.2.0.1.0        Production
>
>  
>
>  
>
> 在scott用户下，我们创建一些type类型对象。
>
>  
>
>  
>
> SQL> grant imp_full_database to scott;
>
> Grant succeeded
>
>  
>
> SQL> grant exp_full_database to scott;
>
> Grant succeeded
>
>  
>
> SQL> conn scott/tiger@wilson;
>
> Connected to Oracle Database 11g Enterprise Edition Release 11.2.0.1.0
>
> Connected as scott
>
>  
>
> SQL> create type mt_type as object (xm number, tchar varchar2(10));
>
>  2 /
>
>  
>
> Type created
>
>  
>
> SQL> select type_name, type_oid from user_types;
>
> TYPE_NAME                     TYPE_OID
>
> ------------------------------ --------------------------------
>
> MT_TYPE                       C230A55B1FC34E1DE040A8C0580017C6
>
>  
>
> SQL> create table my_tabletype of mt_type;
>
> Table created
>
>  
>
> SQL> insert into my_tabletype values (1,'df');
>
> 1 row inserted
>
>  
>
> SQL> commit;
>
> Commit complete
>
>  
>
>  
>
> 之后，我们创建用户scottback。使用数据泵expdp从scott中将数据导出。
>
>  
>
>  
>
> SQL> create user scottback identified by scottback;
>
> User created
>
>  
>
> SQL> grant resource to scottback;
>
> Grant succeeded
>
>  
>
> SQL> grant connect to scottback;
>
> Grant succeeded
>
>  
>
> SQL> grant exp_full_database to scottback;
>
> Grant succeeded
>
>  
>
> SQL> grant imp_full_database to scottback;
>
> Grant succeeded
>
>  
>
>  
>
> 2、expdp数据导出
>
>  
>
> 数据泵DataDump作为10g中推出的新一代数据备份还原工具，具有很多好的特点。DataDump是服务器端使用工具，需要在服务器上执行。
>
>  
>
> 首先，我们需要创建directory对象，对应服务器上的一个目录位置。
>
>  
>
>  
>
> [root@oracle11g /]# pwd
>
> /
>
> [root@oracle11g /]# mkdir export
>
> [root@oracle11g /]# ls -l | grep export
>
> drwxr-xr-x   2 root  root     4096 Jun 11 19:29 export
>
> [root@oracle11g /]#chown oracle:oinstall export
>
> [root@oracle11g /]# ls -l | grep export
>
> drwxr-xr-x  2 oracle oinstall 4096 Jun 11 19:39 export
>
>  
>
>  
>
> 创建directory对象，并且将read write权限授予给scott和scottback。
>
>  
>
>  
>
> SQL> create or replace directory MY_DIR
>
>  2   as '/export';
>
>  
>
> Directory created
>
>  
>
> SQL> grant write, read on directory my_dir to scott;
>
> Grant succeeded
>
>  
>
> SQL> grant write, read on directory my_dir to scottback;
>
> Grant succeeded’
>
>  
>
>  
>
> 再使用expdp命令行进行导出。
>
>  
>
>  
>
> [oracle@oracle11g ~]$ cd /export/
>
> [oracle@oracle11g export]$ pwd
>
> /export
>
> [oracle@oracle11g export]$ expdp scott/tiger@wilson directory=my_dir dumpfile=scott.dmp logfile=resexp.log schemas=scott
>
>  
>
> Export: Release 11.2.0.1.0 - Production on Mon Jun 11 19:35:08 2012
>
>  
>
> [oracle@oracle11g export]$ expdp scott/tiger@wilson directory=my_dir dumpfile=scott.dmp logfile=resexp.log schemas=scott
>
> Export: Release 11.2.0.1.0 - Production on Mon Jun 11 19:35:08 2012
>
>  
>
> Copyright (c) 1982, 2009, Oracle and/or its affiliates. All rights reserved.
>
>  
>
> Connected to: Oracle Database 11g Enterprise Edition Release 11.2.0.1.0 - Production
>
> With the Partitioning, OLAP, Data Mining and Real Application Testing options
>
> Starting "SCOTT"."SYS_EXPORT_SCHEMA_01": scott/********@wilson directory=my_dir dumpfile=scott.dmp logfile=resexp.log schemas=scott
>
> Estimate in progress using BLOCKS method...
>
> Processing object type SCHEMA_EXPORT/TABLE/TABLE_DATA
>
> （篇幅原因，部分省略…..）
>
> . . exported "SCOTT"."T"                                    0 KB      0 rows
>
> . . exported "SCOTT"."T1"                                   0 KB      0 rows
>
> . . exported "SCOTT"."T2"                                   0 KB      0 rows
>
> Master table "SCOTT"."SYS_EXPORT_SCHEMA_01" successfully loaded/unloaded
>
> ******************************************************************************
>
> Dump file set for SCOTT.SYS_EXPORT_SCHEMA_01 is:
>
>  /export/scott.dmp
>
> Job "SCOTT"."SYS_EXPORT_SCHEMA_01" successfully completed at 19:36:00
>
>  
>
> [oracle@oracle11g export]$ ls -l
>
> total 420
>
> -rw-r--r-- 1 oracle oinstall  2467 Jun 11 19:36 resexp.log
>
> -rw-r----- 1 oracle oinstall 421888 Jun 11 19:36 scott.dmp
>
>  
>
>  
>
> 3、impdp导入数据
>
>  
>
> 在默认的impdp方式下，type也是不能导入到相同的数据库中去的。
>
>  
>
>  
>
> [oracle@oracle11g export]$impdp scottback/scottback@wilson directory=my_dir dumpfile=scott.dmp logfile=resimp.log remap_schema=scott:scottback
>
>  
>
> Import: Release 11.2.0.1.0 - Production on Mon Jun 11 19:37:37 2012
>
>  
>
> Copyright (c) 1982, 2009, Oracle and/or its affiliates. All rights reserved.
>
>  
>
> Connected to: Oracle Database 11g Enterprise Edition Release 11.2.0.1.0 - Production
>
> With the Partitioning, OLAP, Data Mining and Real Application Testing options
>
> Master table "SCOTTBACK"."SYS_IMPORT_FULL_01" successfully loaded/unloaded
>
> Starting "SCOTTBACK"."SYS_IMPORT_FULL_01": scottback/********@wilson directory=my_dir dumpfile=scott.dmp logfile=resimp.log remap_schema=scott:scottback
>
> Processing object type SCHEMA_EXPORT/USER
>
> ORA-31684: Object type USER:"SCOTTBACK" already exists
>
> Processing object type SCHEMA_EXPORT/SYSTEM_GRANT
>
> Processing object type SCHEMA_EXPORT/ROLE_GRANT
>
> Processing object type SCHEMA_EXPORT/DEFAULT_ROLE
>
> Processing object type SCHEMA_EXPORT/PRE_SCHEMA/PROCACT_SCHEMA
>
> Processing object type SCHEMA_EXPORT/TYPE/TYPE_SPEC
>
> ORA-39083: Object type TYPE failed to create with error:
>
> ORA-02304: invalid object identifier literal
>
> Failing sql is:
>
> CREATE TYPE "SCOTTBACK"."MT_TYPE"  OID 'C230A55B1FC34E1DE040A8C0580017C6' as object (xm number, tchar varchar2(10));
>
>  
>
>  
>
> Processing object type SCHEMA_EXPORT/TABLE/TABLE
>
> ORA-39117: Type needed to create table is not included in this operation. Failing sql is:
>
> CREATE TABLE "SCOTTBACK"."MY_TABLETYPE" OF "SCOTTBACK"."MT_TYPE" OID 'C230B8AA21E527C9E040A8C058001816' OIDINDEX ( PCTFREE 10 INITRANS 2 MAXTRANS 255 STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645 PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT) TABLESPACE "SYSTEM" ) PCTFREE 10 PCTUSED 40 INITRANS 1 MAXTRANS 255 NOCOMPRESS LOGGING
>
> Processing object type SCHEMA_EXPORT/TABLE/TABLE_DATA
>
> . . imported "SCOTTBACK"."BASELINE_TEST"                22.90 KB      1 rows
>
> （篇幅原因，省略部分…..）
>
> Processing object type SCHEMA_EXPORT/TABLE/STATISTICS/TABLE_STATISTICS
>
> Job "SCOTTBACK"."SYS_IMPORT_FULL_01" completed with 4 error(s) at 19:37:47
>
>  
>
>  
>
> 注意，在默认情况下，我们导入数据依然会遇到oid的问题。显示的依然是type创建SQL中包括有oid信息，引起oid冲突。进而是连带的数据表my_tabletype不能创建。
>
>  
>
> 有一个片段可以关注：
>
>  
>
>  
>
> CREATE TYPE "SCOTTBACK"."MT_TYPE"  OID'C230A55B1FC34E1DE040A8C0580017C6'as object (xm number, tchar varchar2(10));
>
>  
>
>  
>
> Processing object type SCHEMA_EXPORT/TABLE/TABLE
>
> ORA-39117: Type needed to create table is not included in this operation. Failing sql is:
>
> CREATE TABLE "SCOTTBACK"."MY_TABLETYPE" OF "SCOTTBACK"."MT_TYPE" OID'C230B8AA21E527C9E040A8C058001816'OIDINDEX ( PCTFREE 10 INITRANS 2 MAXTRANS 255 STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS
>
>  
>
>  
>
> 关联oid相同，说明type的oid在Oracle内部是作为关联的重要信息使用的。
>
>  
>
> 在impdp中，我们可以使用transform参数设置，要求将原有dmp文件中oid映射重新生成。
>
>  
>
>  
>
> [oracle@oracle11g export]$ impdp scottback/scottback@wilson directory=my_dir dumpfile=scott.dmp logfile=resimp2.log remap_schema=scott:scottbacktransform=oid:n
>
>  
>
> Import: Release 11.2.0.1.0 - Production on Mon Jun 11 19:39:07 2012
>
>  
>
> Copyright (c) 1982, 2009, Oracle and/or its affiliates. All rights reserved.
>
>  
>
> Connected to: Oracle Database 11g Enterprise Edition Release 11.2.0.1.0 - Production
>
> With the Partitioning, OLAP, Data Mining and Real Application Testing options
>
> Master table "SCOTTBACK"."SYS_IMPORT_FULL_01" successfully loaded/unloaded
>
> Starting "SCOTTBACK"."SYS_IMPORT_FULL_01": scottback/********@wilson directory=my_dir dumpfile=scott.dmp logfile=resimp2.log remap_schema=scott:scottback transform=oid:n
>
> Processing object type SCHEMA_EXPORT/USER
>
> ORA-31684: Object type USER:"SCOTTBACK" already exists
>
> Processing object type SCHEMA_EXPORT/SYSTEM_GRANT
>
> Processing object type SCHEMA_EXPORT/ROLE_GRANT
>
> Processing object type SCHEMA_EXPORT/DEFAULT_ROLE
>
> Processing object type SCHEMA_EXPORT/PRE_SCHEMA/PROCACT_SCHEMA
>
> Processing object type SCHEMA_EXPORT/TYPE/TYPE_SPEC
>
> Processing object type SCHEMA_EXPORT/TABLE/TABLE
>
> Processing object type SCHEMA_EXPORT/TABLE/TABLE_DATA
>
> . . imported "SCOTTBACK"."BASELINE_TEST"                22.90 KB      1 rows
>
> . . imported "SCOTTBACK"."DEPT"                         5.937 KB      4 rows
>
> . . imported "SCOTTBACK"."EMP"                          8.992 KB     14 rows
>
> . . imported "SCOTTBACK"."MY_TABLETYPE"                 6.507 KB      1 rows
>
> . . imported "SCOTTBACK"."SALES_QUAL"                   6.007 KB      6 rows
>
> . . imported "SCOTTBACK"."SALGRADE"                     5.867 KB      5 rows
>
> . . imported "SCOTTBACK"."BONUS"                            0 KB      0 rows
>
> . . imported "SCOTTBACK"."T"                                0 KB      0 rows
>
> . . imported "SCOTTBACK"."T1"                               0 KB      0 rows
>
> . . imported "SCOTTBACK"."T2"                               0 KB      0 rows
>
> Processing object type SCHEMA_EXPORT/TABLE/INDEX/INDEX
>
> Processing object type SCHEMA_EXPORT/TABLE/CONSTRAINT/CONSTRAINT
>
> Processing object type SCHEMA_EXPORT/TABLE/INDEX/STATISTICS/INDEX_STATISTICS
>
> Processing object type SCHEMA_EXPORT/TABLE/COMMENT
>
> Processing object type SCHEMA_EXPORT/VIEW/VIEW
>
> ORA-31684: Object type VIEW:"SCOTTBACK"."V_T1" already exists
>
> Processing object type SCHEMA_EXPORT/TABLE/CONSTRAINT/REF_CONSTRAINT
>
> Processing object type SCHEMA_EXPORT/TABLE/STATISTICS/TABLE_STATISTICS
>
> Job "SCOTTBACK"."SYS_IMPORT_FULL_01" completed with 2 error(s) at 19:39:20
>
>  
>
>  
>
> 其中，transform取值oid:n的含义就是对oid信息不进行加载，重新进行生成。数据表取值正确。
>
>  
>
>  
>
>  
>
> SQL> conn scottback/scottback@wilson;
>
> Connected to Oracle Database 11g Enterprise Edition Release 11.2.0.1.0
>
> Connected as scottback
>
>  
>
> SQL> select * from my_tabletype;
>
>  
>
> ​       XM TCHAR
>
> ---------- ----------
>
> ​        1 df
>
>  

##### Remark:

> 在使用type中，一定要注意可能引起的imp/exp导出导入问题.
>
>  
>
> 我们讨论了由于type使用特性的原因，如果我们使用exp/imp工具导入到相同数据库中，是会发生报错现象。
>
>  
>
> 当我们使用exp/imp的时候，报错ORA-02304实际上是没有什么特别好的解决方法的。Type导入相同库报错的本质在于在导出的时候，[Oracle](http://www.linuxidc.com/topicnews.aspx?tid=12)会将type的oid连带导出。而导入的时候，又希望将其还原为相同的oid从而引发冲突。
>
> 随着Oracle功能不断完善，很多新特性在exp/imp工具上已经不能支持。Oracle 10g下推出的Data Dump有很多功能，是我们可以进行借鉴使用的

#### Problem: 空表无法导出

##### Solution:

1. 执行如下脚本，创建存储过程

```sql
create or replace procedure zx_null_table is
  cursor c_1 is
    Select 'alter table ' || table_name || ' allocate extent'
      from user_tables a
     where a.segment_created = 'NO' ;
  s_sql varchar2( 200);
begin
  open c_1;
  loop
    fetch c_1
      into s_sql;
    exit when c_1%notfound ;
    execute immediate s_sql;
  end loop;
  close c_1;
end zx_null_table;

```

2. 在数据库中调用该存储过程

## 导入脚本

可以将以下脚本复制另存为 .cmd 文件，即可执行。

```basic
@echo off

SET DATABASE_NAME=DATABASE4200

call imp %DATABASE_NAME%/%DATABASE_NAME%@246 file=e:34dbfile/%DATABASE_NAME%.dmp log=e:34dbfile/%DATABASE_NAME%.log full=y ignore=y

SET DATABASE_NAME=DATABASE4201

call imp %DATABASE_NAME%/%DATABASE_NAME%@246 file=e:34dbfile/%DATABASE_NAME%.dmp log=e:34dbfile/%DATABASE_NAME%.log full=y ignore=y

SET DATABASE_NAME=DATABASE4300

call imp %DATABASE_NAME%/%DATABASE_NAME%@246 file=e:34dbfile/%DATABASE_NAME%.dmp log=e:34dbfile/%DATABASE_NAME%.log full=y ignore=y

SET DATABASE_NAME=DATABASE4310

call imp %DATABASE_NAME%/%DATABASE_NAME%@246 file=e:34dbfile/%DATABASE_NAME%.dmp log=e:34dbfile/%DATABASE_NAME%.log full=y ignore=y

SET DATABASE_NAME=DATABASE4320

call imp %DATABASE_NAME%/%DATABASE_NAME%@246 file=e:34dbfile/%DATABASE_NAME%.dmp log=e:34dbfile/%DATABASE_NAME%.log full=y ignore=y

SET DATABASE_NAME=DATABASE4321

call imp %DATABASE_NAME%/%DATABASE_NAME%@246 file=e:34dbfile/%DATABASE_NAME%.dmp log=e:34dbfile/%DATABASE_NAME%.log full=y ignore=y

SET DATABASE_NAME=DATABASE4322

call imp %DATABASE_NAME%/%DATABASE_NAME%@246 file=e:34dbfile/%DATABASE_NAME%.dmp log=e:34dbfile/%DATABASE_NAME%.log full=y ignore=y

SET DATABASE_NAME=PM4322

call imp %DATABASE_NAME%/%DATABASE_NAME%@246 file=e:34dbfile/%DATABASE_NAME%.dmp log=e:34dbfile/%DATABASE_NAME%.log full=y ignore=y

SET DATABASE_NAME=WMS4301

call imp %DATABASE_NAME%/%DATABASE_NAME%@246 file=e:34dbfile/%DATABASE_NAME%.dmp log=e:34dbfile/%DATABASE_NAME%.log full=y ignore=y


SET DATABASE_NAME=WMS4320

call imp %DATABASE_NAME%/%DATABASE_NAME%@246 file=e:34dbfile/%DATABASE_NAME%.dmp log=e:34dbfile/%DATABASE_NAME%.log full=y ignore=y

SET DATABASE_NAME=WMS4330

call imp %DATABASE_NAME%/%DATABASE_NAME%@246 file=e:34dbfile/%DATABASE_NAME%.dmp log=e:34dbfile/%DATABASE_NAME%.log full=y ignore=y

SET DATABASE_NAME=WMS4330TEST

call imp %DATABASE_NAME%/%DATABASE_NAME%@246 file=e:34dbfile/%DATABASE_NAME%.dmp log=e:34dbfile/%DATABASE_NAME%.log full=y ignore=y

pause
```

```cmd
imp psiiface/data@mes82 file=C:\Users\xsu\Desktop\tcq_engeneer_change.dmp log=C:\Users\xsu\Desktop\import.log full=y ignore=y
```

