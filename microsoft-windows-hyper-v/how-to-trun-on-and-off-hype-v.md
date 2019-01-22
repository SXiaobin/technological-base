# Windows hyper-v 关启方法

前提：需要dgreadiness_v3.6工具

1. 以管理员身份运行powershell

2. 进入dgreadiness_v3.6主目录

3. 运行

   ```cmd
   Set-ExecutionPolicy AllSigned
   ```

4. 运行

	```cmd
	.\DG_Readiness_Tool_v3.6.ps1 -Disable -AutoReboot
	```

5. 如果需要启动hyper-v 则使用enable

5. 接着Window会执行重启等操作，再启动过程中根据提示选择确定。