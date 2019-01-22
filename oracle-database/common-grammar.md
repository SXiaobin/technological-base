# 获取创建表的语句	

```sql
select dbms_metadata.get_ddl('TABLE', 'PFAK') from dual
```

# 查询表空间使用情况

```sql
SELECT Upper(F.TABLESPACE_NAME)         "表空间名",
       D.TOT_GROOTTE_MB                 "表空间大小(M)",
       D.TOT_GROOTTE_MB - F.TOTAL_BYTES "已使用空间(M)",
       To_char(Round(( D.TOT_GROOTTE_MB - F.TOTAL_BYTES ) / D.TOT_GROOTTE_MB * 100, 2), '990.99')
       || '%'                           "使用比",
       F.TOTAL_BYTES                    "空闲空间(M)",
       F.MAX_BYTES                      "最大块(M)"
FROM   (SELECT TABLESPACE_NAME,
               Round(Sum(BYTES) / ( 1024 * 1024 ), 2) TOTAL_BYTES,
               Round(Max(BYTES) / ( 1024 * 1024 ), 2) MAX_BYTES
        FROM   SYS.DBA_FREE_SPACE
        GROUP  BY TABLESPACE_NAME) F,
       (SELECT DD.TABLESPACE_NAME,
               Round(Sum(DD.BYTES) / ( 1024 * 1024 ), 2) TOT_GROOTTE_MB
        FROM   SYS.DBA_DATA_FILES DD
        GROUP  BY DD.TABLESPACE_NAME) D
WHERE  D.TABLESPACE_NAME = F.TABLESPACE_NAME
ORDER  BY 1
```

## 查询表空间的free space

```sql
select tablespace_name, count(*) AS extends,round(sum(bytes) / 1024 / 1024, 2) AS MB,sum(blocks) AS blocks from dba_free_space group BY tablespace_name;
```

## 查询表空间的总容量

```sql
select tablespace_name, sum(bytes) / 1024 / 1024 as MB from dba_data_files group by tablespace_name;
```

## 查询表空间使用率

```sql
SELECT total.tablespace_name,
       Round(total.MB, 2)           AS Total_MB,
       Round(total.MB - free.MB, 2) AS Used_MB,
       Round(( 1 - free.MB / total.MB ) * 100, 2)
       || '%'                       AS Used_Pct
FROM   (SELECT tablespace_name,
               Sum(bytes) / 1024 / 1024 AS MB
        FROM   dba_free_space
        GROUP  BY tablespace_name) free,
       (SELECT tablespace_name,
               Sum(bytes) / 1024 / 1024 AS MB
        FROM   dba_data_files
        GROUP  BY tablespace_name) total
WHERE  free.tablespace_name = total.tablespace_name;
```

# [LTRIM](https://www.baidu.com/s?wd=LTRIM&tn=44039180_cpr&fenlei=mv6quAkxTZn0IZRqIHckPjm4nH00T1d-PHckujNWn10YnWKBm1TY0ZwV5Hcvrjm3rH6sPfKWUMw85HfYnjn4nH6sgvPsT6KdThsqpZwYTjCEQLGCpyw9Uz4Bmy-bIi4WUvYETgN-TLwGUv3EnWbLP1bzPHRs)

## 语法

[LTRIM](https://www.baidu.com/s?wd=LTRIM&tn=44039180_cpr&fenlei=mv6quAkxTZn0IZRqIHckPjm4nH00T1d-PHckujNWn10YnWKBm1TY0ZwV5Hcvrjm3rH6sPfKWUMw85HfYnjn4nH6sgvPsT6KdThsqpZwYTjCEQLGCpyw9Uz4Bmy-bIi4WUvYETgN-TLwGUv3EnWbLP1bzPHRs)（string1,string2）

## 功能

返回删除从左边算起出现在string2中的字符的string1。String2被缺省设置为单个的空格。数据库将扫描string1，从最左边开始。当遇到不在string2中的第一个字符，结果就被返回了。[LTRIM](https://www.baidu.com/s?wd=LTRIM&tn=44039180_cpr&fenlei=mv6quAkxTZn0IZRqIHckPjm4nH00T1d-PHckujNWn10YnWKBm1TY0ZwV5Hcvrjm3rH6sPfKWUMw85HfYnjn4nH6sgvPsT6KdThsqpZwYTjCEQLGCpyw9Uz4Bmy-bIi4WUvYETgN-TLwGUv3EnWbLP1bzPHRs)的行为方式与RTRIM很相似.

# 查询锁

```sql
SELECT b.owner,
       b.object_name,
       a.session_id,
       a.locked_mode,
       s.sid,
       s.serial#,
       s.username,
       s.schemaname,
       s.osuser,
       s.process,
       s.machine,
       s.terminal,
       s.logon_time
  FROM v$locked_object a, dba_objects b, v$session s
 WHERE b.object_id = a.object_id AND a.SESSION_ID = S.SID

```

# 终止用户连接

```sql
SELECT A.USERNAME,
       A.OSUSER,
       A.PROGRAM,
       A.MACHINE,
       A.SID,
       A.SERIAL#,
       B.SPID,
       'alter system kill session '
       || ''''
       || TRIM (A.SID)
       || ','
       || TRIM (A.SERIAL#)
       || ''';'
  FROM V$SESSION A, V$PROCESS B
 WHERE A.PADDR = B.ADDR AND A.USERNAME = 'PSIIFACE';
 
```

# 查看约束

```sql
SELECT 
  USER_CONS_COLUMNS.CONSTRAINT_NAME AS 约束名, 
  USER_CONS_COLUMNS.TABLE_NAME AS 表名, 
  USER_CONS_COLUMNS.COLUMN_NAME AS 列名, 
  USER_CONS_COLUMNS.POSITION AS 位置 
FROM 
  USER_CONSTRAINTS 
    JOIN USER_CONS_COLUMNS 
    ON (USER_CONSTRAINTS.CONSTRAINT_NAME 
        = USER_CONS_COLUMNS.CONSTRAINT_NAME) 
WHERE 
  CONSTRAINT_TYPE = 'P'; 
```

# 创建删除用户

```sql
SQL> drop user lys cascade;
 
User dropped
 
SQL> create user lys identified by lys;
 
User created
 
SQL> grant dba to lys;
 
Grant succeeded
```

