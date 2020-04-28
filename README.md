# **technological-base** 

公用技术信息

# How Tos

## Stackoverflow无法正常使用

Stack Overflow requires external JavaScript from another domain, which is blocked or failed to load.

### Case

某些JS文件加载的问题。打开浏览器控制台发现第一个错是获取[https://ajax.googleapis.com/ajax/libs/jquery/1.12.4/jquery.min.js ](https://ajax.googleapis.com/ajax/libs/jquery/1.12.4/jquery.min.js)失败了，因为Google被墙了，导致其域名下的js文件也访问不了，而这个jquery获取失败导致了后面一连串的js报错。所以我们解决问题的关键变成了能让stackoverflow加载这个jquery文件。

### Solution

除了翻墙或者Google被解封，不然我们不可能从原有地址获取该jquery文件，但是我不想用vpn（因为买不起），所以我想能不能从其他CDN获取相同版本的jquery文件再让stackoverflow加载呢？这是完全可以的，只要我们在stackoverflow网页加载的过程中将google jquery的script标签替换成其他CDN的应该就可以了。但是怎么实现访问所有stackoverflow域名下的页面都能实现自动替换呢？

Chrom 可以使用CDN插件 - https://github.com/justjavac/ReplaceGoogleCDN

