# How-Tos

## 按照后缀删除文件名

``` cmd
find /psi/mes/mounts/workflow -name "*.deployed"|xargs rm -rf
```

```cmd
find . -name "*.deployed"|xargs rm -rf
```

find：使用find命令搜索文件，使用它的-name参数指明文件后缀名。
. :是[当前目录](https://www.baidu.com/s?wd=%E5%BD%93%E5%89%8D%E7%9B%AE%E5%BD%95&tn=SE_PcZhidaonwhc_ngpagmjz&rsv_dl=gh_pc_zhidao)，因为Linux是树形目录，所以总有一个交集目录，这里根据需要设置
'*.exe': 指明后缀名，*是通配符
" -type f : "查找的类型为文件
"-print" :输出查找的[文件目录](https://www.baidu.com/s?wd=%E6%96%87%E4%BB%B6%E7%9B%AE%E5%BD%95&tn=SE_PcZhidaonwhc_ngpagmjz&rsv_dl=gh_pc_zhidao)名
-exec: -exec选项后边跟着一个所要执行的命令，表示将find出来的文件或目录执行该命令。

## 修改文件、文件夹权限

```cmd
chmod 554 a.txt
```

## 如何创建软连接

```cmd
#用ln -s来建立软链接
[roc@roclinux ~]$ ln -s source.txt softsource.txt
 
#查看文件i节点信息
[roc@roclinux ~]$ ls -il source.txt softsource.txt
2235009 lrwxrwxrwx 1 roc roc 10 3月   1 00:24 softsource.txt -> source.txt
2235010 -rw-rw-r-- 2 roc roc 14 3月   1 00:19 source.txt
```

