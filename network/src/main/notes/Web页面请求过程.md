# Web页面请求过程

## 1 DHCP(Dynamic Host Configuration Protocol)配置主机信息
1. **发现阶段**
  1. DHCP客户机以广播方式发送DHCP discover发现信息来寻找DHCP服务器，即向地址255.255.255.255发送特定的广播信息。
  1. 网络上每一台安装了TCP/IP协议的主机都会接收到这种广播信息，但只有DHCP服务器才会做出响应。
    源IP地址：0.0.0.0, 目标IP地址：255.255.255.255。
1. **提供阶段**
  1. 网络中接收到DHCP discover发现信息的DHCP服务器都会做出响应，它从尚未出租的IP地址中挑选一个分配给DHCP客户机，向DHCP客户机发送一个包含出租的IP地址和其他设置的DHCP offer提供信息。
1. **选择阶段**，即DHCP客户机选择某台DHCP服务器提供的IP地址的阶段。
  1. 如果有多台DHCP服务器向DHCP客户机发来的DHCP offer提供信息，则DHCP客户机只接受第一个收到的DHCP offer提供信息，然后它就以广播方式回答一个DHCP request请求信息，该信息中包含向它所选定的DHCP服务器请求IP地址的内容。
1. **确认阶段**，即DHCP服务器确认所提供的IP地址的阶段。
1. 当DHCP服务器收到DHCP客户机回答的DHCP request请求信息之后，它便向DHCP客户机发送一个包含它所提供的IP地址和其他设置的DHCP ack确认信息，告诉DHCP客户机可以使用它所提供的IP地址。
1. 然后DHCP客户机便将其TCP/IP协议与网卡绑定，另外，除DHCP客户机选中的服务器外，其他的DHCP服务器都将收回曾提供的IP地址。
1. 之后就配置它的IP地址、子网掩码和DNS服务器的IP地址，并在其IP转发表中安装默认网关。
1. **重新登录**，以后DHCP客户机每次重新登录网络时，就不需要再发送DHCP discover发现信息了，而是直接发送包含前一次所分配的IP地址的DHCP request请求信息。
1. 当DHCP服务器收到这一信息后，它会尝试让DHCP客户机继续使用原来的IP地址，并回答一个DHCP ack确认信息。
2. 如果此IP地址已无法再分配给原来的DHCP客户机使用时（比如此IP地址已分配给其它DHCP客户机使用），则DHCP服务器给DHCP客户机回答一个DHCP nack否认信息。
3. 当原来的DHCP客户机收到此DHCP nack否认信息后，它就必须重新发送DHCP discover发现信息来请求新的IP地址。
1. **更新租约**
  1. DHCP服务器向DHCP客户机出租的IP地址一般都有一个租借期限，期满后DHCP服务器便会收回出租的IP地址。
  1. 如果DHCP客户机要延长其IP租约，则必须更新其IP租约。
    DHCP客户机启动时和IP租约期限过一半时，DHCP客户机都会自动向DHCP服务器发送更新其IP租约的信息。
## 2 ARP解析MAC地址
- IP地址决定了最终的目的地，而MAC地址决定了**下一跳**到哪
- IP地址是虚拟的，MAC地址是物理的
- 两者的映射由ARP协议完成
1. 主机通过浏览器生成一个TCP套接字，套接字向HTTP服务器发送HTTP请求。为了生成该套接字，主机需要知道网站的域名对应的IP地址。
1. 主机生成一个DNS查询报文，该报文具有53号端口，因为DNS服务器的端口号是53。
1. 该DNS查询报文被放入目的地址为DNS服务器IP地址的IP数据报中。
1. 该IP数据报被放入一个以太网帧中，该帧将发送到网关路由器。
1. DHCP过程只知道网关路由器的IP地址，为了获取网关路由器的MAC地址，需要使用ARP协议。
1. 主机生成一个包含目的地址为网关路由器IP地址的ARP查询报文，将该ARP查询报文放入一个具有广播目的地址（FF:FF:FF:FF:FF:FF）的以太网帧中，并向交换机发送该以太网帧，交换机将该帧转发给所有的连接设备，包括网关路由器。
1. 网关路由器接收到该帧后，不断向上分解得到ARP报文，发现其中的IP地址与其接口的IP地址匹配，因此就发送一个ARP回答报文，包含了它的MAC地址，发回给主机。
## 3 DNS解析域名
1. 知道了网关路由器的MAC地址之后，就可以继续DNS的解析过程了。
1. 网关路由器接收到包含DNS查询报文的以太网帧后，抽取出IP数据报，并根据转发表决定该IP数据报应该转发的路由器。
1. 因为路由器具有内部网关协议（RIP、OSPF）和外部网关协议（BGP）这两种路由选择协议，因此路由表中已经配置了网关路由器到达DNS服务器的路由表项。
1. 到达DNS服务器之后，DNS服务器抽取出DNS查询报文，并在DNS数据库中查找待解析的域名。
1. 找到DNS记录之后，发送DNS回答报文，将该回答报文放入UDP报文段中，然后放入IP数据报中，通过路由器反向转发回网关路由器，并经过以太网交换机到达主机。
## 4 HTTP请求页面
- 有了HTTP服务器的IP地址之后，主机就能够生成TCP套接字，该套接字将用于向Web服务器发送HTTP GET报文。
- 在生成TCP套接字之前，必须先与HTTP服务器进行三次握手来建立连接。生成一个具有目的端口80的TCPSYN报文段，并向HTTP服务器发送该报文段。
- HTTP服务器收到该报文段之后，生成TCPSYNACK报文段，发回给主机。
- 连接建立之后，浏览器生成HTTPGET报文，并交付给HTTP服务器。
- HTTP服务器从TCP套接字读取HTTPGET报文，生成一个HTTP响应报文，将Web页面内容放入报文主体中，发回给主机。
- 浏览器收到HTTP响应报文后，抽取出Web页面内容，之后进行渲染，显示Web页面。

