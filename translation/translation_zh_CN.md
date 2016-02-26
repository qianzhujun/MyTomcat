# 待定 #

## 第一章 一个简单的Web服务器 ##
本章介绍了java web servers是如何工作的。Web服务器也叫作HTTP（超文本传输协议）服务器，因为服务器使用HTTP协议与客户端（通常是浏览器）进行通信。Java Web服务器使用两个重要的类java.net.Socket 与java.net.ServerSocket来进行HTTP通信。因此，本章首先讨论HTTP，与这两个重要的类，然后，继续介绍简单的Web服务器。

### 超文本传输协议(HTTP) ###
什么是HTTP？HTTP是服务器与浏览器之间，用来通信的协议。它其实就是一个请求与响应的协议。客户端请求一个资源，服务器对此请求作出响应。HTTP协议的传输依靠的是TCP协议（网络知识，译注），默认使用的TCP端口是80。第一版的HTTP协议的版本号是HTTP/0.9，后来被HTTP/1.0所替代。当前使用的版本是HTTP/1.1（HTTP/2.0已经到来，译注），具体协议规范请访问
http://www.w3.org/Protocols/HTTP/1.1/rfc2616.pdf。

*注意：本章只是概要性的介绍下HTTP 1.1协议，目的只是帮助读者更好的理解服务器与客户端之间的通信格式。如果你需要更详细的协议规范，请访问上面的网址*

HTTP协议里，都是客户端通过建立一个TCP连接与发起一个HTTP请求来开始通信的。服务器是没法主动与客户端建立连接的，只能响应（这个好理解，只有陌生人主动联系你的，哪有你主动联系某个陌生人的，因为你压根不知道要联系哪个？译注）。服务器与客户端都是能终止一条已经建立的连接的（其实就是打电话时，谁都可以挂电话。译注）。例如，网站打开太慢，你等得不耐烦，点了“停止加载”按钮，这就中断了与服务器的连接。

### HTTP请求 ###
一个HTTP请求有3部分组成：
- 方法(Method)——统一资源标识符(URI)——协议/版本
- 请求头部(Request headers)
- 实体(Entity body)

下面是一个HTTP请求例子：
>POST /examples/default.jsp HTTP/1.1
>Accept: text/plain; text/html
>Accept-Language: en-gb
>Connection: Keep-Alive 
>Host: localhost 
>User-Agent: Mozilla/4.0 (compatible; MSIE 4.01; Windows 98) 
>Content-Length: 33 
>Content-Type: application/x-www-form-urlencoded Accept-Encoding: gzip, deflate 
>
>lastName=Franks&firstName=Michael

第一行声明了“方法 ——统一资源标识符 ——协议/版本”。
>POST /examples/default.jsp HTTP/1.1

POST是请求方法。“/examples/default.jsp”是统一资源标识符，“HTTP/1.1”是采用的协议与版本。
每个HTTP请求只能使用一个HTTP规范里定义好的方法。HTTP 1.1支持的请求方法有GET, POST, HEAD, OPTIONS, PUT, DELETE,TRACE。GET与POST是使用得最普遍的方法。
统一资源标识符（URI），指出了所请求的资源的位置（并非一定得是真正的物理位置，任何虚拟的都可以。译注）。URI里的位置是相对服务器的根目录而言的。因此，URI总是以“/”开头。统一资源定位符（URL），实际上只是URI的一种（Web中分得没那么清，可以认为二者一样。译注），具体请参考http://www.ietf.org/rfc/rfc2396.txt。
请求的头部，包含了许多客户端有用的信息和请求的实体。例如，它包含了浏览器的设置语言，请求实体的长度，等等。请求的头之间，使用回车换行（CRLF）分割开（其实就是行尾敲个Enter，译注）。（回车换行，即enter，包含两个动作“移到行首，移到下一行”，windows里用/r/n,Unix 使用/n。计算机技术知识。译注）
请求的头部，与请求的实体之间，通过一空行进行分割。在请求格式里面，这一空行是非常重要的，因为，它告诉服务器，请求的实体从这一空行之后开始。在许多Web开发书籍中，这一空行被当作HTTP请求的第四部分。
在之前这个HTTP请求的例子中，实体部分只是简单的：
lastName=Franks&firstName=Michael
当然，这只是一个例子。实际情况中，这个实体部分一般都是相当长的。

### HTTP响应 ###
跟HTTP请求一样，HTTP响应也是由三部分组成：
- 协议(Protocol)——状态码(Status code)——描述(Description)
- 响应头部(Response headers)
- 实体(Entity body)

下面是一个HTTP响应例子：
>HTTP/1.1 200 OK 
>Server: Microsoft-IIS/4.0 
>Date: Mon, 5 Jan 2004 13:13:33 GMT 
>Content-Type: text/html 
>Last-Modified: Mon, 5 Jan 2004 13:13:12 GMT 
>Content-Length: 112 
>
>```html
><html> 
><head> 
><title>HTTP Response Example</title> 
></head> 
><body> Welcome to Brainy Software </body> 
></html>```

响应的第一行类似于请求的第一行。响应的第一行告诉我们，使用的是HTTP/1.1协议，请求成功（200就是成功），一切OK。
响应头部里包含的有用信息跟请求头部里的类似。响应的实体，是HTML内容。响应头部与实体之间，同样是用一空白行隔开。

### Socket类 ###
socket（中文译为套接字，译注）是一条网络连接（非物理，译注）的终端。应用程序通过它才能向网络上读取、写入数据。不同电脑上的两个应用程序，通过收发字节流来进行通信。当一个应用程序向另一个应用程序发送消息时，它必须得知道另一个应用程序的IP地址和socket的端口（就好比，你向一个集团内部办公室打电话时，你不仅要知道他们集团的电话号码，还要知道办公室所在的分机号码。译注）。在java中，java.net.Socket类代表了我们刚才所谈到的socket。
当你要创建一个socket时，你可以使用Socket类的众多构造方法中的一个。其中一个构造方法通过接收一个ip，一个端口号来创建：
```java
public Socket (java.lang.String host, int port)
```
其中，host远程机器的名字或ip地址，port是需要连接到的远程应用的端口号。例如，要连接到yahoo.com（端口80），你只需要如下进行构造
```java
new Socket ("yahoo.com", 80)
```
一旦成功创建了一个Socket的实例，你就可以使用该实例来发送、接收字节流了。为了发送字节流，首先，你必须执行Socket类的`getOutputStream`方法，这样才能获得一个`java.io.OutputStream`对象。为了给远程应用发送文本信息，通常都是从返回的`java.io.OutputStream`对象中，构造一个`java.io.PrintWriter`对象。为了接收其它连接发过来的字节流，你必须执行Socket类的`getInputStream`方法，该方法返回一个`java.io.InputStream`对象。
下面的代码片段创建了一个能跟本机（127.0.0.1）发送HTTP请求、接收HTTP响应的Socket。它创建了一个`StringBuffer`对象来保存响应内容，并在控制台打印出来。
```java
Socket socket = new Socket("127.0.0.1", "8080"); 
OutputStream os = socket.getOutputStream();
boolean autoflush = true; 
PrintWriter out = new PrintWriter( socket.getOutputStream(), autoflush); 
BufferedReader in = new BufferedReader( new InputStreamReader( socket.getInputstream() ));
 
// send an HTTP request to the web server 
out.println("GET /index.jsp HTTP/1.1"); 
out.println("Host: localhost:8080");
out.println("Connection: Close"); 
out.println(); 

// read the response 
boolean loop = true; 
StringBuffer sb = new StringBuffer(8096); 
while (loop) { 
	if ( in.ready() ) { 
		int i=0;
		while (i!=-1) {
			i = in.read(); 
			sb.append((char) i);
		}
		loop = false;
	}
	Thread.currentThread().sleep(50);
}

// display the response to the out console 
System.out.println(sb.toString()); 
socket.close();
```
请注意，你必须以HTTP协议的格式发送一个HTTP请求，才能从Web服务器处得到一个正确的响应。如果你读了之前的小节，你应该能够理解上面代码中关于HTTP 请求的部分。

### ServerSocket类 ###
上一小节的Socket类代表了一个客户端socket，例如，你无论什么时候想连接到远程服务时，你都会构造一个Socket。那么，如果你想实现的不是一个客户端程序，而是一个服务端程序，例如HTTP服务器、FTP服务器之类的。该怎么做呢？你需要一条不同的途径。因为，服务器根本不知道客户端会在什么时候连接进来，因此它需要永不停歇的7X24运行着。为了使你的程序能永不停歇的运行，你需要使用`java.net.ServerSocket`类。该类是服务端Socket的一个实现（socket是不分什么客户端、服务端的，你编写的程序，如果是提供服务的，那就是服务端。java里分Socket，Server Socket只是方便开发者而已，译注）。
Server Socket跟Socket是不相同的。Server Socket担任的角色是等候者，等候来自客户端发起的连接请求。一旦有连接请求到来，Server Socket就会创建一个Socket实例来跟客户端进行通信。
为了创建一个Server Socket，你需要使用`ServerSocket`类的四个构造方法中的任意一个。你必须指定Server Socket需要监听的IP与端口。通常而言，都是监听在本机，即127.0.0.1。Server Socket监听的IP，称作“绑定IP”。Server Socket的另一个重要属性是它的“积压量”，即它能容纳的最大连接数（其实，就是网络术语中吞吐量的吞，译注）。
下面是一个具体的`ServerSocket`构造方法：
```java
public ServerSocket(int port, int backLog, InetAddress bindingAddress);
```
请注意，在该构造方法中，bindingAddress必须是`java.net.InetAddress`的一个实例。创建InetAddress对象的一种简单方法是，调用它的`getByName`静态方法，传入一个包含地址的字符串。如下所示：
```java
InetAddress.getByName("127.0.0.1");
```
下面的代码创建了一个监听在本机8080端口的ServerSocket，它的最大连接量设为了1。
```java
new ServerSocket(8080, 1, InetAddress.getByName("127.0.0.1"));
```
一旦创建了一个`ServerSocket`实例，你就可以调用它的接收方法，让它一直处于等待状态，直到有它所监听地址端口的连接请求到达。接收方法，只会在有连接请求到达时，才会返回，且返回的是一个Socket实例。接下来，该Socket实例就可以用来跟客户端程序发送、接收字节流了，正如前一小节所介绍的那样。

### 应用程序 ###
本章的web服务器程序，在ex01.pyrmont包中，由三个类组成：
- HttpServer
- Request
- Response

程序的入口点（main方法）在HttpServer类中。该main方法创建了一个HttpServer的实例，然后，调用它的等待方法。该方法的意义，如它的名字一样，“等待”。等待客户端的连接到达，处理连接，然后发送响应给客户端。它会一直等待，直到它接收到“关闭”命令。
本章的应用程序只能发送文件系统上的静态资源，例如HTML文件，图片。同时，它也会把到达的连接信息在控制台打印出来。然而，它还不会给客户端发送任何响应头部，例如时间、缓存等。
接下来的小节中，让我们来详细看一下这三个类。

### HttpServer类 ###
HttpServer类代表了一个Web服务器程序，它的代码如“例1.1”所示。注意，为了节省空间，“等待”方法的代码在“例1.2”中给出，此处不再给出。

例1.1	HttpServer类
```java
 package ex01.pyrmont;
 import java.net.Socket;
 import java.net.ServerSocket;
 import java.net.InetAddress;
 import java.io.InputStream;
 import java.io.OutputStream;
 import java.io.IOException;
 import java.io.File;
 public class HttpServer {

 /** WEB_ROOT is the directory where our HTML and other files reside. 
 * For this package, WEB_ROOT is the "webroot" directory under the 
 * working directory. 
 * The working directory is the location in the file system 
 * from where the java command was invoked. */ 
 
	public static final String WEB_ROOT = System.getProperty("user.dir") + File.separator + "webroot";
 	// shutdown command 
	private static final String SHUTDOWN_COMMAND = "/SHUTDOWN";
 	// the shutdown command received 
	private boolean shutdown = false;
    public static void main(String[] args) { 
		HttpServer server = new HttpServer();
		server.await();
	}

	public void await() { 
		...
	}
```

例1.2	HttpServer类的await方法
```java
	public void await() {
		ServerSocket serverSocket = null;
		int port = 8080;
		try {
			serverSocket = new ServerSocket(port, 1,
					InetAddress.getByName("127.0.0.1"));
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		// Loop waiting for a request
		while (!shutdown) {
			Socket socket = null;
			InputStream input = null;
			OutputStream output = null;
			try {
				socket = serverSocket.accept();
				input = socket.getInputStream();
				output = socket.getOutputStream();
				// create Request object and parse
				Request request = new Request(input);
				request.parse();
				// create Response object
				Response response = new Response(output);
				response.setRequest(request);
				response.sendStaticResource();
				// Close the socket
				socket.close();
				// check if the previous URI is a shutdown command
				shutdown = request.getUri().equals(SHUTDOWN_COMMAND);
			} catch (Exception e) {
				e.printStackTrace();
				continue;
			}
		}
	}	
```
该Web服务器程序能提供服务器上的静态资源，这些资源位于WEB_ROOT变量所声明的文件夹（及其子文件夹）中。WEB_ROOT变量的内容初始化如下：
```java
public static final String WEB_ROOT =System.getProperty("user.dir") + File.separator + "webroot";
```
上面的代码中，有一个叫webroot文件夹，该文件夹不仅包含了所有的静态资源，还包含了一些Servlet，你可以用它们来进行测试。
Raphael 2016-2-26 17:07:45 翻译到第15也中下。
