# SSH Tunneling using PuTTY

Sometimes we need a tunnel to pass our internet traffic trough some external server. We usually do that when we have some limitations from our internet provider and want to enter pages we normally don’t have access to. In this short tutorial I will show you how to do that using **PuTTY** (SSH) and **Firefox**.

## **Requirements**

We will need a shell account obviously. There are plenty of providers, and universities usually give sort of shell accounts to their students. Firefox browser can be downloaded here and PuTTY can be found [here](https://www.putty.org/).

## **Creating SSH Tunnel in PuTTY**

First thing we need to do is creating connection. Open PuTTY and configure your connection by providing host address and port number. For SSH we usually use port `22`.

![](<https://raw.githubusercontent.com/SXiaobin/technological-base/master/.image/1559050324227.png?token=AGDYE4ONN7LHBOTX3C5DTKK45U6AM>)

Go to *Connection → SSH → Tunnels* option in the tree view (left panel).

![1559050634136](<https://raw.githubusercontent.com/SXiaobin/technological-base/master/.image/1559050634136.png?token=AGDYE4OLOLT7S73MDDBRNU245U6B2>)

Now we need to do as follows: choose *Dynamic*, fill in the source port (for example `9999`, but feel free to use any available port) and click *Add* button. Now it should be something in format `D{PORT_NUMBER}` on the list above. Click on **Open**.

Next, log in to your server as usual:

![1559050672875](<https://raw.githubusercontent.com/SXiaobin/technological-base/master/.image/1559050672875.png?token=AGDYE4IZ77KZT6YHMDHP4GS45U6GA>)

Since now we have our own proxy server on *127.0.0.1:8888*.

### **Using a tunnel in Firefox**

Download plugin ***FoxyProxy***. Then add configuration like below:

![1559050817455](<https://raw.githubusercontent.com/SXiaobin/technological-base/master/.image/1559050817455.png?token=AGDYE4NJQ7LI3KD4HZWL6RC45U6HA>)

Then we could enable this configuration. 

![1559051418027](<https://raw.githubusercontent.com/SXiaobin/technological-base/master/.image/1559051418027.png?token=AGDYE4LLNASFFP74TMFOTFK476WD6>)

Now you could access the website by Firefox thought the tunnel.

# [SSH Tunneling with SSH comand](https://manpages.debian.org/stretch/openssh-client/ssh_config.5.en.html)

In [OpenSSH](https://www.ssh.com/ssh/openssh/), local port forwarding is configured using the `-L` option:

```cmd
ssh -L intra.example.com:80:gw.example.com:80 

ssh install@212.117.89.75 -p 52074 -L 192.168.140.8:4840:192.168.140.8:4840
```

This example opens a connection to the `gw.example.com` jump server, and forwards any connection to port 80 on the local machine to port 80 on `intra.example.com`.

By default, anyone (even on different machines) can connect to the specified port on the SSH client machine. However, this can be restricted to programs on the same host by supplying a *bind address*:

```
ssh -L 127.0.0.1:80:intra.example.com:80 gw.example.com
```

The `LocalForward` option in the [OpenSSH client configuration file](https://www.ssh.com/ssh/config/) can be used to configure forwarding without having to specify it on command line.

# How Tos

## How to forward multiple ports

`ssh -L LPort1:RHOST1:RPORT1 -L LPORT2:RHOST2:RPORT2 me@ju.mp.ho.st`

This can be shortened to `ssh jump`, using the "alias" `jump`, if you set up a matching section in your `~/.ssh/config` like this:

```cmd
Host [jump]
    User [myUserName]
    Hostname [ju.mp.ho.st]
    Port [Connecting Port]
    LocalForward [Local Port] [Remote Server]:[Remote Port]
    LocalForward [Local Port] [Remote Server]:[Local Port]
    LocalForward [Local Port] [Remote Server]:[Local Port]
    LocalForward [Local Port] [Remote Server]:[Local Port]
```

