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
当请求一个静态资源时，你只用在你的浏览器地址栏中输入：
http://machineName:port/staticResource
上面地址中，machineName就是你向服务器发出请求时，服务器的名称或地址。如果你的服务器程序运行在本机，那machinename就可以使用localhost代替。staticResource是WEB_ROOT下的静态资源。
例如，如果你的HttpServer运行在本机，你通过浏览器向它请求index.html资源时，可以在浏览器中输入如下地址：
http://localhost:8080/index.html
要停止该Web服务器程序是，你只用通过浏览器发送一条包含关闭命令字符串的URL即可。关闭命令字符串预先定义在了`HttpServer`类的`SHUTDOWN`静态变量中：
```java
private static final String SHUTDOWN_COMMAND = "/SHUTDOWN";
```
所以，当你要关闭服务器程序时，只用输入如下URL：
http://localhost:8080/SHUTDOWN 
接下来，让我们来探讨一下“列1.2”中的await方法。
为什么命名为await，而不是wait？因为wait是`java.lang.Object`类的方法啦。
`await`方法，首先创建了一个`ServerSocket`实例，接着就进入while循环。
>```java
>serverSocket = new ServerSocket(port, 1,InetAddress.getByName("127.0.0.1"));
...
// Loop waiting for a request
while (!shutdown) {
...
}
>```

while循环内的代码将会一直阻塞在`ServerSocket`实例的`accept`方法上，直到有HTTP请求到达8080端口。
```java
socket = serverSocket.accept();
```
只要接收到HTTP请求，`await`将继续往下执行，从返回的`socket`对象处，获得`java.io.InputStream`与`java.io.OutputStream`，如下所示：
```java
input = socket.getInputStream();
output = socket.getOutputStream();
```
紧接着，`await`将创建一个`ex01.pyrmont.Request`类的实例，并调用它的`parse`方法来解析收到的HTTP请求，从复杂的HTTP协议格式中，提取出信息。
```java 
// create Request object and parse
Request request = new Request(input);
request.parse ();
```
然后，`await`方法将创建一个`ex01.pyrmont.Response`对象，并把之前创建的Request对象传给它，并调用它的`sendStaticResource`方法来发送资源。
最后，`await`方法调用Socket的close方法来关闭该Socket，即关闭连接。接下来，`await`方法对刚才HTTP请求的URI进行判断，判断URI字符串是否就是预先定义好的关闭命令字符串，如果是的话，就会跳出while，进而结束整个程序。如果不是的话，继续循环，直到有下一条HTTP请求到来。

### Request类 ###
类`ex01.pyrmont.Request`代表了HTTP请求。创建该类的实例时，需要在构造方法中传入一个`InputStream`对象，该对象是由负责跟客户端通信的Socket返回的。我们可以通过`InputStream`对象的任一跟“读”相关的方法中，得到HTTP请求的原始格式数据。
Request类的代码如“例1.3”所示，它的两个方法，`parse`与`getUri`分别在“例1.4”与“例1.5”中给出。

例1.3	Request类
```java
package ex01.pyrmont;
import java.io.InputStream;
import java.io.IOException;
public class Request {
private InputStream input;
private String uri;
public Request(InputStream input) {
this.input = input;
}
public void parse() {
...
}
private String parseUri(String requestString) { 
...
}
public String getUri() {
return uri;
}
}
```

例1.4 parse方法
```java
public void parse() {
// Read a set of characters from the socket
StringBuffer request = new StringBuffer(2048);
int i;
byte[] buffer = new byte[2048];
try {
i = input.read(buffer);
}
catch (IOException e) {
e.printStackTrace();
i = -1;
}
for (int j=0; j<i; j++) {
request.append((char) buffer[j]);
}
System.out.print(request.toString());
uri = parseUri(request.toString());
}
```

例1.5 parseUri方法
```java
private String parseUri(String requestString) {
int index1, index2;
index1 = requestString.indexOf(' ');
if (index1 != -1) {
index2 = requestString.indexOf(' ', index1 + 1);
if (index2 > index1)
return requestString.substring(index1 + 1, index2);
}
return null;
}
```
`parse`方法的作用是解析HTTP请求的原始格式数据，它的工作并不是很多。该方法从`InputStream`里读取字节流，并把它还原为HTTP请求格式，存储进`StringBuffer`变量， 然后，通过调用私有方法`parseUri`来进一步解析HTTP请求，得到请求的URI。公共方法`getUri`则是向外界返回URI变量内容。
*注意	更多关于HTTP 原始请求数据的处理过程，将在第三章及以后的章节中展开。*
要理解`parse`与`parseUri`方法是如何工作的，你需要之前章节中关于“超文本传输协议”的知识储备。本章中，我们暂时只讨论HTTP请求的第一行，姑且称为“请求行”吧。还记得它的格式吗？
>方法(Method)——统一资源标识符(URI)——协议/版本

上面的各个元素之间，使用空格分割开来。下面举个具体的例子：
>GET /index.html HTTP/1.1

`parse`方法从`InputStream`（HttpServer传过来的）中读取所有的字节流，并把它们存储在一个字节数组中`buffer`。然后，使用一个叫request的`StringBuffer`来从这些字节中还原出字符，并把最后组成的字符串传递给`parseUri`方法。
`parseUri`方法首先找到第一个空格，与第二个空格的位置，接着截取出这两个位置之间的内容，就是URI。

### Response类 ###
类`ex01.pyrmont.Response`代表了一个HTTP响应。它的代码如“例1.6”所示。

例1.6 Response类
```java
package ex01.pyrmont;

import java.io.OutputStream;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.File;

/*
  HTTP Response = Status-Line
    *(( general-header | response-header | entity-header ) CRLF)
    CRLF
    [ message-body ]
    Status-Line = HTTP-Version SP Status-Code SP Reason-Phrase CRLF
*/

public class Response {

  private static final int BUFFER_SIZE = 1024;
  Request request;
  OutputStream output;

  public Response(OutputStream output) {
    this.output = output;
  }

  public void setRequest(Request request) {
    this.request = request;
  }

  public void sendStaticResource() throws IOException {
    byte[] bytes = new byte[BUFFER_SIZE];
    FileInputStream fis = null;
    try {
      File file = new File(HttpServer.WEB_ROOT, request.getUri());
      if (file.exists()) {
        fis = new FileInputStream(file);
        int ch = fis.read(bytes, 0, BUFFER_SIZE);
        while (ch!=-1) {
          output.write(bytes, 0, ch);
          ch = fis.read(bytes, 0, BUFFER_SIZE);
        }
      }
      else {
        // file not found
        String errorMessage = "HTTP/1.1 404 File Not Found\r\n" +
          "Content-Type: text/html\r\n" +
          "Content-Length: 23\r\n" +
          "\r\n" +
          "<h1>File Not Found</h1>";
        output.write(errorMessage.getBytes());
      }
    }
    catch (Exception e) {
      // thrown if cannot instantiate a File object
      System.out.println(e.toString() );
    }
    finally {
      if (fis!=null)
        fis.close();
    }
  }
}

```
首先注意一点，Response类的构造方法，接受的是一个`java.io.OutputStream`对象，如下所示。
```java
public Response(OutputStream output) {
this.output = output;
}
```
Response对象是HttpServer里的await方法创建的。await方法从socket处获得了OutputStream对象，并以此构造了Response。
Response类有两个公共方法：`setRequest`与`sendStaticResource`。其中，`setRequest`方法用来为Response设置Request对象的引用。
`sendStaticResource`则是用来发送类似HTML文件等静态资源的。该方法首先通过传入一个父路径与子路径给File类的构造方法，以此来实例化一个`java.io.File`对象。
```java
File file = new File(HttpServer.WEB_ROOT, request.getUri());
```
然后，会检查该file对象是否存在，如果存在的话，就会进一步通过该file对象来构造一个`java.io.FileInputStream`对象，然后，通过FileInputStream对象的read方法，读出指定长度的字节，并把这些字节写入OutputStream对象。注意，在本例中，静态资源的内容是被原模原样地发送给浏览器的。
```java
if (file.exists()) {
        fis = new FileInputStream(file);
        int ch = fis.read(bytes, 0, BUFFER_SIZE);
        while (ch!=-1) {
          output.write(bytes, 0, ch);
          ch = fis.read(bytes, 0, BUFFER_SIZE);
        }
      }
```

如果指定文件不存在，`sendStaticResource`方法将会给浏览器发送一条“资源不存在”的错误信息。
```java
 // file not found
        String errorMessage = "HTTP/1.1 404 File Not Found\r\n" +
          "Content-Type: text/html\r\n" +
          "Content-Length: 23\r\n" +
          "\r\n" +
          "<h1>File Not Found</h1>";
        output.write(errorMessage.getBytes());
```

### 运行程序 ###
在你的工作目录，编译你的java类，然后在命令行中输入：
`java ex01.pyrmont.HttpServer`
就可以启动服务器程序了。
你可以在你的浏览器地址栏中输入
>http://localhost:8080/index.html 

来测试一下你的程序。
一切正常的话，你将在浏览器中看到如下界面，图1.1。

图1.1
![](images/chapter1-1.png)

同时，你会在控制台看到如下类似的信息：
>GET /index.html HTTP/1.1
Host: localhost:8080
Connection: keep-alive
Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8
Upgrade-Insecure-Requests: 1
User-Agent: Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/48.0.2564.109 Safari/537.36
Accept-Encoding: gzip, deflate, sdch
Accept-Language: zh-CN,zh;q=0.8,en-US;q=0.6,en;q=0.4

>GET /images/logo.gif HTTP/1.1
Host: localhost:8080
Connection: keep-alive
Accept: image/webp,image/*,*/*;q=0.8
User-Agent: Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/48.0.2564.109 Safari/537.36
Referer: http://localhost:8080/index.html
Accept-Encoding: gzip, deflate, sdch
Accept-Language: zh-CN,zh;q=0.8,en-US;q=0.6,en;q=0.4

### 本章小结 ###
在本章中，我们知道了一个简单的Web服务器程序是如何工作的。本章的服务器程序只有三个类，功能还并不完善，但是，却是一个很好的学习样例。在下一章中，我们将讨论如何处理动态内容。

## 第二章 一个简单的Servlet容器 ##
### 概要 ###
本章通过两个程序来介绍如何开发Servlet容器。第一个程序设计得很简单，方便大家理解Servlet容器是如何工作的。第二个程序只是稍微复杂了一点点。
*注意 每章的Servlet容器程序都会比它的前一章复杂一点点，直到第17章，我们最终开发出一个完整的Servlet容器为止。*
Servlet容器不仅能处理静态资源，而且能处理Servlet。你可以使用`PrimitiveServlet`来测试我们所开发的Servlet容器。`PrimitiveServlet`的代码如“例2.1”所示，你可以在webroot目录下找到它的class文件。更复杂的Servlet已经超越了本章的范围，不过没关系，你可以在接下来的章节中学习它们。

例2.1 PrimitiveServlet.java
```java
import javax.servlet.*;
import java.io.IOException;
import java.io.PrintWriter;

public class PrimitiveServlet implements Servlet {

  public void init(ServletConfig config) throws ServletException {
    System.out.println("init");
  }

  public void service(ServletRequest request, ServletResponse response)
    throws ServletException, IOException {
    System.out.println("from service");
    PrintWriter out = response.getWriter();
    out.println("Hello. Roses are red.");
    out.print("Violets are blue.");
  }

  public void destroy() {
    System.out.println("destroy");
  }

  public String getServletInfo() {
    return null;
  }
  public ServletConfig getServletConfig() {
    return null;
  }

}
```

我们所要探讨的两个容器程序都在`ex02.pyrmont`包里。要理解这两个容器程序是如何工作的，你需要首先对`javax.servlet.Servlet`接口很熟悉。所以，本章第一节，我们先来讨论一下这个接口，然后，再学习，当HTTP请求的是一个Servlet时，Servlet容器应该如何处理。

### javax.servlet.Servlet 接口 ###
`javax.servlet`与`javax.servlet.http`包里的类和接口，是Servlet编程得以实现的关键所在。在这些类和接口中，`javax.servlet.Servlet`接口是最重要的。所有Servlet必须实现该接口，或者继承实现了该接口的类。
Servlet接口的5个方法签名如下：
>public void init(ServletConfig config) throws ServletException ;
>public void service(ServletRequest request, ServletResponse response) throws ServletException, java.io.IOException ;
>public void destroy() public ServletConfig getServletConfig() ;
>public java.lang.String getServletInfo();

上面的5个方法中，`init`，`service`，`destroy`是Servlet的生命周期方法。`init`方法会在servlet类实例化后，被Servlet容器所调用。Servlet容器只会调用`init`方法一次，表明servlet类正在被启动。只有`init`执行成功后，servlet才能接收处理请求。servlet类的编写者，可以重写该方法。他们可以把只需要运行一次的初始化代码（例如，加载驱动类、变量赋值等）放到该方法里。如果不需要什么初始代码的话，不动该方法即可。
当HTTP请求资源是servlet时，Servlet容器会调用该servlet类的`service`方法，来提供服务。Servlet容器传给`service`方法一个`javax.servlet.ServletRequest`对象与`javax.servlet.ServletResponse`对象。`ServletRequest`对象包含有客户端的HTTP请求信息。`ServletResponse`对象用了封装servlet的响应。在servlet的生命周期中，`service`方法可以被调用无数次。（只要有针对该servlet的请求，就会被调用。译注）
当Servlet容器要移除一个servlet服务时，会首先调用该servlet的`destroy`方法。这种情况，通常发生在关闭Servlet 容器程序时，或者Servlet容器需要更多的内存空间时。Servlet容器会等到所有运行该servlet的`service`方法的线程退出或超时后，才会调用servlet的`destroy`方法。Servlet容器调用一个servlet的`destroy`方法后，就意味着该servlet已经被移除了，此后，不再会调用它的`service`方法。`destroy`方法被调用后，该servlet占用的所有资源（例如，内存、文件、线程等）都将被回收，同时，servlet的持久化状态会被更新。
在本章中，你可以使用“例2.1”的`PrimitiveServlet`类来进行测试。`PrimitiveServlet`类实现了`javax.servlet.Servlet`接口，同时实现了Servlet接口的5个方法。`PrimitiveServlet`类的功能很简单，每次`init`，`service`，或者 `destroy`方法被调用时，它都会在控制台打印出方法的名称。同时，`service`还会利用从`ServletResponse`对象处获得的`PrintWriter`向客户端输出信息。

Raphael 2016-2-29 11:20:59 翻译至27页。
## 第二章 一个简单的Servlet容器 ##
### 概要 ###
本章通过两个程序来介绍如何开发Servlet容器。第一个程序设计得很简单，方便大家理解Servlet容器是如何工作的。第二个程序只是稍微复杂了一点点。
*注意 每章的Servlet容器程序都会比它的前一章复杂一点点，直到第17章，我们最终开发出一个完整的Servlet容器为止。*
Servlet容器不仅能处理静态资源，而且能处理Servlet。你可以使用`PrimitiveServlet`来测试我们所开发的Servlet容器。`PrimitiveServlet`的代码如“例2.1”所示，你可以在webroot目录下找到它的class文件。更复杂的Servlet已经超越了本章的范围，不过没关系，你可以在接下来的章节中学习它们。

例2.1 PrimitiveServlet.java
```java
import javax.servlet.*;
import java.io.IOException;
import java.io.PrintWriter;

public class PrimitiveServlet implements Servlet {

  public void init(ServletConfig config) throws ServletException {
    System.out.println("init");
  }

  public void service(ServletRequest request, ServletResponse response)
    throws ServletException, IOException {
    System.out.println("from service");
    PrintWriter out = response.getWriter();
    out.println("Hello. Roses are red.");
    out.print("Violets are blue.");
  }

  public void destroy() {
    System.out.println("destroy");
  }

  public String getServletInfo() {
    return null;
  }
  public ServletConfig getServletConfig() {
    return null;
  }

}
```

我们所要探讨的两个容器程序都在`ex02.pyrmont`包里。要理解这两个容器程序是如何工作的，你需要首先对`javax.servlet.Servlet`接口很熟悉。所以，本章第一节，我们先来讨论一下这个接口，然后，再学习，当HTTP请求的是一个Servlet时，Servlet容器应该如何处理。

### javax.servlet.Servlet 接口 ###
`javax.servlet`与`javax.servlet.http`包里的类和接口，是Servlet编程得以实现的关键所在。在这些类和接口中，`javax.servlet.Servlet`接口是最重要的。所有Servlet必须实现该接口，或者继承实现了该接口的类。
Servlet接口的5个方法签名如下：
>public void init(ServletConfig config) throws ServletException ;
>public void service(ServletRequest request, ServletResponse response) throws ServletException, java.io.IOException ;
>public void destroy() public ServletConfig getServletConfig() ;
>public java.lang.String getServletInfo();

上面的5个方法中，`init`，`service`，`destroy`是Servlet的生命周期方法。`init`方法会在servlet类实例化后，被Servlet容器所调用。Servlet容器只会调用`init`方法一次，表明servlet类正在被启动。只有`init`执行成功后，servlet才能接收处理请求。servlet类的编写者，可以重写该方法。他们可以把只需要运行一次的初始化代码（例如，加载驱动类、变量赋值等）放到该方法里。如果不需要什么初始代码的话，不动该方法即可。
当HTTP请求资源是servlet时，Servlet容器会调用该servlet类的`service`方法，来提供服务。Servlet容器传给`service`方法一个`javax.servlet.ServletRequest`对象与`javax.servlet.ServletResponse`对象。`ServletRequest`对象包含有客户端的HTTP请求信息。`ServletResponse`对象用了封装servlet的响应。在servlet的生命周期中，`service`方法可以被调用无数次。（只要有针对该servlet的请求，就会被调用。译注）
当Servlet容器要移除一个servlet服务时，会首先调用该servlet的`destroy`方法。这种情况，通常发生在关闭Servlet 容器程序时，或者Servlet容器需要更多的内存空间时。Servlet容器会等到所有运行该servlet的`service`方法的线程退出或超时后，才会调用servlet的`destroy`方法。Servlet容器调用一个servlet的`destroy`方法后，就意味着该servlet已经被移除了，此后，不再会调用它的`service`方法。`destroy`方法被调用后，该servlet占用的所有资源（例如，内存、文件、线程等）都将被回收，同时，servlet的持久化状态会被更新。
在本章中，你可以使用“例2.1”的`PrimitiveServlet`类来进行测试。`PrimitiveServlet`类实现了`javax.servlet.Servlet`接口，同时实现了Servlet接口的5个方法。`PrimitiveServlet`类的功能很简单，每次`init`，`service`，或者 `destroy`方法被调用时，它都会在控制台打印出方法的名称。同时，`service`还会利用从`ServletResponse`对象处获得的`PrintWriter`向客户端输出信息。

### 程序1 ###
现在，让我们以Servlet容器的角度来审视下如何进行Servlet编程。简单的讲，一个完整的Servlet容器，能为HTTP请求（请求的是servlet）提供如下功能：
-	当servlet第一次被请求提供服务时，Servlet容器将该servlet的class文件载入jvm，并调用它的`init`方法（只会调用一次）。
-	为每个请求创建一个`javax.servlet.ServletRequest`实例与`javax.servlet.ServletResponse`实例。
-	调用的servlet的`service`方法，并传入`ServletRequest`与`ServletResponse`对象。
-	当servlet被关闭时，调用servlet的`destroy`方法，之后，把servlet类从jvm里卸载掉。

本章第一个Servlet容器程序功能并不完善。因此，它只能运行很简单的servlet，并且不会调用servlet的`init`方法与`destroy`方法。相反，它提供如下功能：
-	等待HTTP请求的到达。
-	创建`ServletRequest`与`ServletResponse`对象。
-	如果请求的是静态资源，则会调用`StaticResourceProcessor`实例的`process`方法，并传入ServletRequest对象与ServletResponse对象。
-	如果请求的是servlet资源，则把servlet的class文件载入进jvm，并调用`service`方法，同时传入ServletRequest对象与ServletResponse对象。

注意，在第一个程序中，每次请求servlet资源时，Servlet容器都会把该servlet载入一次。（这只是雏形，完善的Servlet容器肯定不会这样干，译注）
第一个程序由6个类组成：
-	HttpServer1
-	Request
-	Response
-	StaticResourceProcessor
-	ServletProcessor1
-	Constants

图2.1 展示了第一个程序的UML。

图2.1
![](images/chapter2-1.png)

第一个程序的入口点是`HttpServer1`类的`main`方法。它创建了一个`HttpServer1`对象，并调用对象的`await`方法。`await`方法一直等待HTTP请求的到来，并为每个HTTP请求创建一个Request对象与Response对象，然后，根据HTTP请求的资源类型（静态还是servlet），将Request对象与Response对象传给`StaticResourceProcessor`实例或`ServletProcessor`实例。
`Constants`类里存放的是会被其它类引用的静态变量`WEB_ROOT`,该变量是final修饰的最终变量。`WEB_ROOT`变量指出了`PrimitiveServlet`类的位置与静态资源的位置。
`HttpServer1`实例将会一直等待请求的到来并处理它们，直到接收到关闭命令为止。关闭命令字符串跟第一章的是相同的。
接下来的几个小节中，我们将逐一探讨这6个类。

### HttpServer1 类 ###
本章中的`HttpServer1`类，与第一章中的`HttpServer`类区别并不是很大。但是，本章中的`HttpServer1`类不仅能处理静态资源，还能处理servlet资源。要访问静态资源，你只用在浏览器中输入如下类似的URL：
>http://machineName:port/staticResource
 
这就是我们在第一章中请求静态资源时的写法，还记得吗？
要访问一个servlet资源时，你需要输入如下格式的地址：
> http://machineName:port/servlet/servletClass 

因此，当你在浏览器中请求本机服务器上的`PrimitiveServlet`时，你需要输入如下URL
> http://localhost:8080/servlet/PrimitiveServlet 

Servlet容器能提供`PrimitiveServlet`，但是，当你请求其它servlet时，例如，`ModernServlet`，则会得到一个错误，因为Servlet容器暂时还不能提供`ModernServlet`，没关系，我们将在另一章中也实现提供`ModernServlet`功能。

`HttpServer1`类的代码如“例2.2”所示

例2.2	HttpServer1类
```java
package ex02.pyrmont;

import java.net.Socket;
import java.net.ServerSocket;
import java.net.InetAddress;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;

public class HttpServer1 {

  /** WEB_ROOT is the directory where our HTML and other files reside.
   *  For this package, WEB_ROOT is the "webroot" directory under the working
   *  directory.
   *  The working directory is the location in the file system
   *  from where the java command was invoked.
   */
  // shutdown command
  private static final String SHUTDOWN_COMMAND = "/SHUTDOWN";

  // the shutdown command received
  private boolean shutdown = false;

  public static void main(String[] args) {
    HttpServer1 server = new HttpServer1();
    server.await();
  }

  public void await() {
    ServerSocket serverSocket = null;
    int port = 8080;
    try {
      serverSocket =  new ServerSocket(port, 1, InetAddress.getByName("127.0.0.1"));
    }
    catch (IOException e) {
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

        // check if this is a request for a servlet or a static resource
        // a request for a servlet begins with "/servlet/"
        if (request.getUri().startsWith("/servlet/")) {
          ServletProcessor1 processor = new ServletProcessor1();
          processor.process(request, response);
        }
        else {
          StaticResourceProcessor processor = new StaticResourceProcessor();
          processor.process(request, response);
        }

        // Close the socket
        socket.close();
        //check if the previous URI is a shutdown command
        shutdown = request.getUri().equals(SHUTDOWN_COMMAND);
      }
      catch (Exception e) {
        e.printStackTrace();
        System.exit(1);
      }
    }
  }
}

```
上诉代码中，`await`方法只有在收到停止命令时，才会结束，否则，一直等待HTTP请求。例2.2中的`await`方法与第一章中的`await`方法有何不同？不同的地方在于，例2.2中的`await`方法，能根据请求资源的不同，而把Request传给`StaticResourceProcessor`对象或`ServletProcessor`对象。如果Request的URI中包含`/servlet/`字符串，则会把该Request传给`ServletProcessor`对象，否则，传给`StaticResourceProcessor`对象。

### Request 类 ###
servlet的`service`方法从Servlet容器处接收两个对象，一个是`javax.servlet.ServletRequest`对象，一个是`javax.servlet.ServletResponse`对象。即所谓的，Servlet容器为每个HTTP请求构造一个`ServletRequest`对象与`ServletResponse`对象，并把它们传给`service`方法。
我们用`ex02.pyrmont.Request`类来代表传给`service`方法的`ServletRequest`对象。`ServletRequest`是一个接口，所以我们需要实现该接口。别担心，在这里我们只是简单的实现接口的部分方法，在之后的章节中，我们才会完整的来实现其它方法。对于有些方法，我们让它保持为空就行，如果需要返回值，就返回一个null，正如，“例2.3”所示的一样。

例2.3	Request类
```java
package ex02.pyrmont;

import java.io.InputStream;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;


public class Request implements ServletRequest {

  private InputStream input;
  private String uri;

  public Request(InputStream input) {
    this.input = input;
  }

  public String getUri() {
    return uri;
  }

  private String parseUri(String requestString) {
    int index1, index2;
    index1 = requestString.indexOf(' ');
    if (index1 != -1) {
      index2 = requestString.indexOf(' ', index1 + 1);
      if (index2 > index1)
        return requestString.substring(index1 + 1, index2);
    }
    return null;
  }

  public void parse() {
    // Read a set of characters from the socket
    StringBuffer request = new StringBuffer(2048);
    int i;
    byte[] buffer = new byte[2048];
    try {
      i = input.read(buffer);
    }
    catch (IOException e) {
      e.printStackTrace();
      i = -1;
    }
    for (int j=0; j<i; j++) {
      request.append((char) buffer[j]);
    }
    System.out.print(request.toString());
    uri = parseUri(request.toString());
  }

  /* implementation of the ServletRequest*/
  public Object getAttribute(String attribute) {
    return null;
  }

  public Enumeration getAttributeNames() {
    return null;
  }

  public String getRealPath(String path) {
    return null;
  }

  public RequestDispatcher getRequestDispatcher(String path) {
    return null;
  }

  public boolean isSecure() {
    return false;
  }

  public String getCharacterEncoding() {
    return null;
  }

  public int getContentLength() {
    return 0;
  }

  public String getContentType() {
    return null;
  }

  public ServletInputStream getInputStream() throws IOException {
    return null;
  }

  public Locale getLocale() {
    return null;
  }

  public Enumeration getLocales() {
    return null;
  }

  public String getParameter(String name) {
    return null;
  }

  public Map getParameterMap() {
    return null;
  }

  public Enumeration getParameterNames() {
    return null;
  }

  public String[] getParameterValues(String parameter) {
    return null;
  }

  public String getProtocol() {
    return null;
  }

  public BufferedReader getReader() throws IOException {
    return null;
  }

  public String getRemoteAddr() {
    return null;
  }

  public String getRemoteHost() {
    return null;
  }

  public String getScheme() {
   return null;
  }

  public String getServerName() {
    return null;
  }

  public int getServerPort() {
    return 0;
  }

  public void removeAttribute(String attribute) {
  }

  public void setAttribute(String key, Object value) {
  }

  public void setCharacterEncoding(String encoding)
    throws UnsupportedEncodingException {
  }

}
```
可以看到，在本章的`Request`类里，仍然有第一章中介绍的`parse`与`getUri`方法。

### Response 类###
`ex02.pyrmont.Response`类代表了一个响应。同样的，我们只是部分实现`javax.servlet.ServletResponse`接口。如下，例2.4所示。

例2.4
```java
package ex02.pyrmont;

import java.io.OutputStream;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.File;
import java.io.PrintWriter;
import java.util.Locale;
import javax.servlet.ServletResponse;
import javax.servlet.ServletOutputStream;

public class Response implements ServletResponse {

  private static final int BUFFER_SIZE = 1024;
  Request request;
  OutputStream output;
  PrintWriter writer;

  public Response(OutputStream output) {
    this.output = output;
  }

  public void setRequest(Request request) {
    this.request = request;
  }

  /* This method is used to serve a static page */
  public void sendStaticResource() throws IOException {
    byte[] bytes = new byte[BUFFER_SIZE];
    FileInputStream fis = null;
    try {
      /* request.getUri has been replaced by request.getRequestURI */
      File file = new File(Constants.WEB_ROOT, request.getUri());
      fis = new FileInputStream(file);
      /*
         HTTP Response = Status-Line
           *(( general-header | response-header | entity-header ) CRLF)
           CRLF
           [ message-body ]
         Status-Line = HTTP-Version SP Status-Code SP Reason-Phrase CRLF
      */
      int ch = fis.read(bytes, 0, BUFFER_SIZE);
      while (ch!=-1) {
        output.write(bytes, 0, ch);
        ch = fis.read(bytes, 0, BUFFER_SIZE);
      }
    }
    catch (FileNotFoundException e) {
      String errorMessage = "HTTP/1.1 404 File Not Found\r\n" +
        "Content-Type: text/html\r\n" +
        "Content-Length: 23\r\n" +
        "\r\n" +
        "<h1>File Not Found</h1>";
      output.write(errorMessage.getBytes());
    }
    finally {
      if (fis!=null)
        fis.close();
    }
  }


  /** implementation of ServletResponse  */
  public void flushBuffer() throws IOException {
  }

  public int getBufferSize() {
    return 0;
  }

  public String getCharacterEncoding() {
    return null;
  }

  public Locale getLocale() {
    return null;
  }

  public ServletOutputStream getOutputStream() throws IOException {
    return null;
  }

  public PrintWriter getWriter() throws IOException {
    // autoflush is true, println() will flush,
    // but print() will not.
    writer = new PrintWriter(output, true);
    return writer;
  }

  public boolean isCommitted() {
    return false;
  }

  public void reset() {
  }

  public void resetBuffer() {
  }

  public void setBufferSize(int size) {
  }

  public void setContentLength(int length) {
  }

  public void setContentType(String type) {
  }

  public void setLocale(Locale locale) {
  }
}
```
在`getWriter`方法中，传递给`PrintWriter`构造器的第二个参数是一个布尔类型，它代表是否自动把缓存区的内容发送出去。如果为true的话，每次调用`PrintWriter`的`println`方法时，都会立即把缓存区的内容发送出去（但是，`print`方法不会）。
因此，这个`Response`类就有一个小bug。如果刚好在servlet里`service`方法的最后一行调用了`PrintWriter`的`print`方法，**那`print`方法里的内容就不会被发送到客户端**！没关系，我们在后面的章节里又来修复这个问题。
同样的，本章里的`Response`仍然有第一章中讨论的`sendStaticResource`方法。

### StaticResourceProcessor 类###
`ex02.pyrmont.StaticResourceProcessor`类的作用是，为针对静态资源的HTTP请求提供服务。它只有一个`process`方法。详细代码，如“例2.5”所示。

例2.5	StaticResourceProcessor类
```java
package ex02.pyrmont;

import java.io.IOException;

public class StaticResourceProcessor {

  public void process(Request request, Response response) {
    try {
      response.sendStaticResource();
    }
    catch (IOException e) {
      e.printStackTrace();
    }
  }
}
```
`process`方法接收两个对象，一个`Request`，一个`Response`。在方法的内部，只是简单的调用了`Response`对象的`sendStaticResource`方法。

### ServletProcessor1 类 ###
`ex02.pyrmont.ServletProcessor1`类专门服务于针对servlet资源的请求。代码如“例2.6”所示。

例2.6 ServletProcessor1类
```java
package ex02.pyrmont;

import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandler;
import java.io.File;
import java.io.IOException;
import javax.servlet.Servlet;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public class ServletProcessor1 {

  public void process(Request request, Response response) {

    String uri = request.getUri();
    String servletName = uri.substring(uri.lastIndexOf("/") + 1);
    URLClassLoader loader = null;

    try {
      // create a URLClassLoader
      URL[] urls = new URL[1];
      URLStreamHandler streamHandler = null;
      File classPath = new File(Constants.WEB_ROOT);
      // the forming of repository is taken from the createClassLoader method in
      // org.apache.catalina.startup.ClassLoaderFactory
      String repository = (new URL("file", null, classPath.getCanonicalPath() + File.separator)).toString() ;
      // the code for forming the URL is taken from the addRepository method in
      // org.apache.catalina.loader.StandardClassLoader class.
      urls[0] = new URL(null, repository, streamHandler);
      loader = new URLClassLoader(urls);
    }
    catch (IOException e) {
      System.out.println(e.toString() );
    }
    Class myClass = null;
    try {
      myClass = loader.loadClass(servletName);
    }
    catch (ClassNotFoundException e) {
      System.out.println(e.toString());
    }

    Servlet servlet = null;

    try {
      servlet = (Servlet) myClass.newInstance();
      servlet.service((ServletRequest) request, (ServletResponse) response);
    }
    catch (Exception e) {
      System.out.println(e.toString());
    }
    catch (Throwable e) {
      System.out.println(e.toString());
    }

  }
}
```
Raphael 2016-3-1 11:03:13 翻译到40页下
### Request 类 ###
servlet的`service`方法从Servlet容器处接收两个对象，一个是`javax.servlet.ServletRequest`对象，一个是`javax.servlet.ServletResponse`对象。即所谓的，Servlet容器为每个HTTP请求构造一个`ServletRequest`对象与`ServletResponse`对象，并把它们传给`service`方法。
我们用`ex02.pyrmont.Request`类来代表传给`service`方法的`ServletRequest`对象。`ServletRequest`是一个接口，所以我们需要实现该接口。别担心，在这里我们只是简单的实现接口的部分方法，在之后的章节中，我们才会完整的来实现其它方法。对于有些方法，我们让它保持为空就行，如果需要返回值，就返回一个null，正如，“例2.3”所示的一样。

例2.3	Request类
```java
package ex02.pyrmont;

import java.io.InputStream;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;


public class Request implements ServletRequest {

  private InputStream input;
  private String uri;

  public Request(InputStream input) {
    this.input = input;
  }

  public String getUri() {
    return uri;
  }

  private String parseUri(String requestString) {
    int index1, index2;
    index1 = requestString.indexOf(' ');
    if (index1 != -1) {
      index2 = requestString.indexOf(' ', index1 + 1);
      if (index2 > index1)
        return requestString.substring(index1 + 1, index2);
    }
    return null;
  }

  public void parse() {
    // Read a set of characters from the socket
    StringBuffer request = new StringBuffer(2048);
    int i;
    byte[] buffer = new byte[2048];
    try {
      i = input.read(buffer);
    }
    catch (IOException e) {
      e.printStackTrace();
      i = -1;
    }
    for (int j=0; j<i; j++) {
      request.append((char) buffer[j]);
    }
    System.out.print(request.toString());
    uri = parseUri(request.toString());
  }

  /* implementation of the ServletRequest*/
  public Object getAttribute(String attribute) {
    return null;
  }

  public Enumeration getAttributeNames() {
    return null;
  }

  public String getRealPath(String path) {
    return null;
  }

  public RequestDispatcher getRequestDispatcher(String path) {
    return null;
  }

  public boolean isSecure() {
    return false;
  }

  public String getCharacterEncoding() {
    return null;
  }

  public int getContentLength() {
    return 0;
  }

  public String getContentType() {
    return null;
  }

  public ServletInputStream getInputStream() throws IOException {
    return null;
  }

  public Locale getLocale() {
    return null;
  }

  public Enumeration getLocales() {
    return null;
  }

  public String getParameter(String name) {
    return null;
  }

  public Map getParameterMap() {
    return null;
  }

  public Enumeration getParameterNames() {
    return null;
  }

  public String[] getParameterValues(String parameter) {
    return null;
  }

  public String getProtocol() {
    return null;
  }

  public BufferedReader getReader() throws IOException {
    return null;
  }

  public String getRemoteAddr() {
    return null;
  }

  public String getRemoteHost() {
    return null;
  }

  public String getScheme() {
   return null;
  }

  public String getServerName() {
    return null;
  }

  public int getServerPort() {
    return 0;
  }

  public void removeAttribute(String attribute) {
  }

  public void setAttribute(String key, Object value) {
  }

  public void setCharacterEncoding(String encoding)
    throws UnsupportedEncodingException {
  }

}
```
可以看到，在本章的`Request`类里，仍然有第一章中介绍的`parse`与`getUri`方法。

### Response 类###
`ex02.pyrmont.Response`类代表了一个响应。同样的，我们只是部分实现`javax.servlet.ServletResponse`接口。如下，例2.4所示。

例2.4
```java
package ex02.pyrmont;

import java.io.OutputStream;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.File;
import java.io.PrintWriter;
import java.util.Locale;
import javax.servlet.ServletResponse;
import javax.servlet.ServletOutputStream;

public class Response implements ServletResponse {

  private static final int BUFFER_SIZE = 1024;
  Request request;
  OutputStream output;
  PrintWriter writer;

  public Response(OutputStream output) {
    this.output = output;
  }

  public void setRequest(Request request) {
    this.request = request;
  }

  /* This method is used to serve a static page */
  public void sendStaticResource() throws IOException {
    byte[] bytes = new byte[BUFFER_SIZE];
    FileInputStream fis = null;
    try {
      /* request.getUri has been replaced by request.getRequestURI */
      File file = new File(Constants.WEB_ROOT, request.getUri());
      fis = new FileInputStream(file);
      /*
         HTTP Response = Status-Line
           *(( general-header | response-header | entity-header ) CRLF)
           CRLF
           [ message-body ]
         Status-Line = HTTP-Version SP Status-Code SP Reason-Phrase CRLF
      */
      int ch = fis.read(bytes, 0, BUFFER_SIZE);
      while (ch!=-1) {
        output.write(bytes, 0, ch);
        ch = fis.read(bytes, 0, BUFFER_SIZE);
      }
    }
    catch (FileNotFoundException e) {
      String errorMessage = "HTTP/1.1 404 File Not Found\r\n" +
        "Content-Type: text/html\r\n" +
        "Content-Length: 23\r\n" +
        "\r\n" +
        "<h1>File Not Found</h1>";
      output.write(errorMessage.getBytes());
    }
    finally {
      if (fis!=null)
        fis.close();
    }
  }


  /** implementation of ServletResponse  */
  public void flushBuffer() throws IOException {
  }

  public int getBufferSize() {
    return 0;
  }

  public String getCharacterEncoding() {
    return null;
  }

  public Locale getLocale() {
    return null;
  }

  public ServletOutputStream getOutputStream() throws IOException {
    return null;
  }

  public PrintWriter getWriter() throws IOException {
    // autoflush is true, println() will flush,
    // but print() will not.
    writer = new PrintWriter(output, true);
    return writer;
  }

  public boolean isCommitted() {
    return false;
  }

  public void reset() {
  }

  public void resetBuffer() {
  }

  public void setBufferSize(int size) {
  }

  public void setContentLength(int length) {
  }

  public void setContentType(String type) {
  }

  public void setLocale(Locale locale) {
  }
}
```
在`getWriter`方法中，传递给`PrintWriter`构造器的第二个参数是一个布尔类型，它代表是否自动把缓存区的内容发送出去。如果为true的话，每次调用`PrintWriter`的`println`方法时，都会立即把缓存区的内容发送出去（但是，`print`方法不会）。
因此，这个`Response`类就有一个小bug。如果刚好在servlet里`service`方法的最后一行调用了`PrintWriter`的`print`方法，**那`print`方法里的内容就不会被发送到客户端**！没关系，我们在后面的章节里又来修复这个问题。
同样的，本章里的`Response`仍然有第一章中讨论的`sendStaticResource`方法。

### StaticResourceProcessor 类###
`ex02.pyrmont.StaticResourceProcessor`类的作用是，为针对静态资源的HTTP请求提供服务。它只有一个`process`方法。详细代码，如“例2.5”所示。

例2.5	StaticResourceProcessor类
```java
package ex02.pyrmont;

import java.io.IOException;

public class StaticResourceProcessor {

  public void process(Request request, Response response) {
    try {
      response.sendStaticResource();
    }
    catch (IOException e) {
      e.printStackTrace();
    }
  }
}
```
`process`方法接收两个对象，一个`Request`，一个`Response`。在方法的内部，只是简单的调用了`Response`对象的`sendStaticResource`方法。

### ServletProcessor1 类 ###
`ex02.pyrmont.ServletProcessor1`类专门服务于针对servlet资源的请求。代码如“例2.6”所示。

例2.6 ServletProcessor1类
```java
package ex02.pyrmont;

import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandler;
import java.io.File;
import java.io.IOException;
import javax.servlet.Servlet;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public class ServletProcessor1 {

  public void process(Request request, Response response) {

    String uri = request.getUri();
    String servletName = uri.substring(uri.lastIndexOf("/") + 1);
    URLClassLoader loader = null;

    try {
      // create a URLClassLoader
      URL[] urls = new URL[1];
      URLStreamHandler streamHandler = null;
      File classPath = new File(Constants.WEB_ROOT);
      // the forming of repository is taken from the createClassLoader method in
      // org.apache.catalina.startup.ClassLoaderFactory
      String repository = (new URL("file", null, classPath.getCanonicalPath() + File.separator)).toString() ;
      // the code for forming the URL is taken from the addRepository method in
      // org.apache.catalina.loader.StandardClassLoader class.
      urls[0] = new URL(null, repository, streamHandler);
      loader = new URLClassLoader(urls);
    }
    catch (IOException e) {
      System.out.println(e.toString() );
    }
    Class myClass = null;
    try {
      myClass = loader.loadClass(servletName);
    }
    catch (ClassNotFoundException e) {
      System.out.println(e.toString());
    }

    Servlet servlet = null;

    try {
      servlet = (Servlet) myClass.newInstance();
      servlet.service((ServletRequest) request, (ServletResponse) response);
    }
    catch (Exception e) {
      System.out.println(e.toString());
    }
    catch (Throwable e) {
      System.out.println(e.toString());
    }

  }
}
```
可以发现`ServletProcessor1`类是相当的简单，只有一个`process`方法。通过传入的`ServletRequest`对象，我们能获取得到当前请求的URI：
```java
String uri = request.getUri();
```
还记得本章中，针对servlet资源的请求格式吗？
> /servlet/servletName

其中，`servletName`就是servlet类的类名。
为了把servlet类载入jvm中，首先，我们需要把servlet的类名从URI中剥离出来。我们可以使用如下代码：
```java
String servletName = uri.substring(uri.lastIndexOf("/") + 1);
```
其次，我们需要创建一个类加载器（class loader），并告诉它，去哪里寻找我们的servlet类文件。在本例中，类加载器会去`Constants.WEB_ROOT`常量代表的位置中寻找，即`webroot`目录下。

*注意 关于类加载器（  Class loaders）,我们将在第八章中详细讨论*

在这里，我们使用的是`java.net.URLClassLoader`类加载器，它是`java.lang.ClassLoader`的间接子类（类加载器分为父类加载器、根类加载器，译注）。实例化`URLClassLoader`后，就可以调用它的`loadClass`方法加载类了。`URLClassLoader`的构造方法很简单，如下：
```java
public URLClassLoader(URL[] urls);
```
其中，`urls`是由URL对象组成的数组。这些URL对象指出了，当URLClassLoader类加载器要加载一个类时，应该去什么地方搜索类文件。任何以反斜杠`/`结尾的URL，代表的都是目录（directory）。否则，URL都被认为代表的是一个jar包（如果url指向的是网络，则会在必要的时候下载下来）。

*注意 在Servlet容器中，类加载器搜索servlet类文件的位置，被称为“仓库”*

在本章的程序中，类加载器只用搜索工作目录下的`webroot`这一个位置。因此，我们首先创建了一个长度为1的URL数组。URL类提供了很多的构造方法来创建实例。在这里，我们使用的构造方法跟Tomcat里的一样：
> `public URL(URL context, java.lang.String spec, URLStreamHandler hander)
throws MalformedURLException`

在这个构造方法中，你只用给第二个参数`spec`传入一个指定路径的字符串即可，第一个与第三个参数都传入null。但是，URL还有另外一个构造方法：
> `public URL(java.lang.String protocol, java.lang.String host,
java.lang.String file) throws MalformedURLException`

所以，当你只给第二个参数传入值的话，编译器就不知道你到底调用的是哪一个构造方法。为了避免这种问题，你可以明确指定第三个参数的类型，即使它的值为null：
```java
URLStreamHandler streamHandler = null;
new URL(null, aString, streamHandler);
```
在第二个构造方法中，通过指定协议，以及协议对应的指定文件（夹），就能构造出一个URL实例了。例如：
`String repository = (new URL("file", null,
classPath.getCanonicalPath() + File.separator)).toString() ;`

好了，现在让我们来看看创建类加载器的完整过程：
```java
// create a URLClassLoader
URL[] urls = new URL[1];
URLStreamHandler streamHandler = null;
File classPath = new File(Constants.WEB_ROOT);
String repository = (new URL("file", null,
classPath.getCanonicalPath() + File.separator)).toString() ;
urls[0] = new URL(null, repository, streamHandler);
loader = new URLClassLoader(urls);
```
*注意 上面的代码中，获取`repository`变量的内容时，借鉴了` org.apache.catalina.startup.ClassLoaderFactory`的`createClassLoader`方法，同样的，`URL`借鉴了`org.apache.catalina.loader.StandardClassLoader`类的`addRepository`方法。别担心，在以后的章节中，我们再来讨论这两个类。*

获得类加载器之后，我们就可以调用它的`loadClass`方法，来加载servlet类了：
```java
Class myClass = null;
try {
myClass = loader.loadClass(servletName);
}
catch (ClassNotFoundException e) {
System.out.println(e.toString());
} 
```
接下来，`process`方法创建了一个servlet的实例，强制转换为`javax.servlet.Servlet`后，调用了它的`service`方法来提供服务：
```java
Servlet servlet = null;
try {
servlet = (Servlet) myClass.newInstance();
servlet.service((ServletRequest) request,
(ServletResponse) response);
}
catch (Exception e) {
System.out.println(e.toString());
}
catch (Throwable e) {
System.out.println(e.toString());
}
```

### 运行程序 ###
在Windows上运行该程序时，请在命令行中输入如下指令（用eclipse开发，不用这么麻烦，译注）：
> java -classpath ./lib/servlet.jar;./ ex02.pyrmont.HttpServer1

在Linux上，则输入：
> java -classpath ./lib/servlet.jar:./ ex02.pyrmont.HttpServer1

好了，接下来让我们在浏览器中来测试一下。
在浏览器中输入
> http://localhost:8080/index.html 
或
> http://localhost:8080/servlet/PrimitiveServlet 

当请求`PrimitiveServlet`时，你会发现浏览器显示出**Hello. Roses are red.**这几个字，但却不会显示**Violets are blue.**这几个字。

图2-2
![](images/chapter2-2.png)
因为在`PrimitiveServlet`中，只有第一行字被发送给了客户端。我们将在第三章修正这个问题。

### 程序2 ###
在程序1中，有一个严重的安全隐患问题。当我们调用`ServletProcessor1`的`process`时，分别把`ex02.pyrmont.Request`与`ex02.pyrmont.Response`的实例当作它们的接口`javax.servlet.ServletRequest`与`javax.servlet.ServletResponse`来传递的：
> ```java
> try {
servlet = (Servlet) myClass.newInstance();
servlet.service((ServletRequest) request,
(ServletResponse) response);
}
>```

这样就会有一个安全问题。如果servlet的开发者知道，你传递给service方法里的request与response的运行时类型就是`ex02.pyrmont.Request`与`ex02.pyrmont.Response`的话，那么，开发者就可以把request与response转换为原本的`ex02.pyrmont.Request`与`ex02.pyrmont.Response`实例。这样就很危险了，开发者拥有`ex02.pyrmont.Request`与`ex02.pyrmont.Response`实例，完全可以对这两个实例的公共方法为所欲为。比如，调用`ex02.pyrmont.Request`实例的`parse`方法，调用`ex02.pyrmont.Response`实例的`sendStaticResource`方法，等等。
这个安全隐患要怎么解决？把`ex02.pyrmont.Request`实例的`parse`方法，与`ex02.pyrmont.Response`实例的`sendStaticResource`方法设为私有？这肯定是不行的。设为私有后，Servlet容器里其它基础类就无法调用这些方法了。那就把他们的访问策略设为“只能在同一个包下访问”。嗯，这的确是一种解决方法。但是，还有一种更优雅的解决方案：使用门面模式。见下图：

图2-3 Façade classes
![](images/chapter2-3.png)

在程序2中，我们添加了两个门面类：`RequestFacade`与`ResponseFacade`。
`RequestFacade`同样实现了`ServletRequest`接口，且它的构造方法里需要传入一个`ServletRequest`对象request。没错，你可能已经猜到了。所有对`ServletRequest`接口方法的操作，在`RequestFacade`里都会映射为对request对象的操作。而在`RequestFacade`里，request对象是私有的，即只有`RequestFacade`能操作，外界根本不知道有request对象的存在，外界只能操作`RequestFacade`实现的接口方法。
如此一来，我们在给servlet的`service`方法，传递参数时，就不用传递`ex02.pyrmont.Request`了，只用构造一个`RequestFacade`对象，并传递给`service`方法就行。就算servlet开发者能把传入的`Request`对象，转换为`RequestFacade`对象，他也只能操作`RequestFacade`对象的公共方法。这样的话，不就保护了`ex02.pyrmont.Request`里的`parseUri`方法了么！
`RequestFacade`的代码如“例2.7”所示：

例2.7 RequestFacade类
```java
package ex02.pyrmont;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;

public class RequestFacade implements ServletRequest {

  private ServletRequest request = null;

  public RequestFacade(Request request) {
    this.request = request;
  }

  /* implementation of the ServletRequest*/
  public Object getAttribute(String attribute) {
    return request.getAttribute(attribute);
  }

  public Enumeration getAttributeNames() {
    return request.getAttributeNames();
  }

  public String getRealPath(String path) {
    return request.getRealPath(path);
  }

  public RequestDispatcher getRequestDispatcher(String path) {
    return request.getRequestDispatcher(path);
  }

  public boolean isSecure() {
    return request.isSecure();
  }

  public String getCharacterEncoding() {
    return request.getCharacterEncoding();
  }

  public int getContentLength() {
    return request.getContentLength();
  }

  public String getContentType() {
    return request.getContentType();
  }

  public ServletInputStream getInputStream() throws IOException {
    return request.getInputStream();
  }

  public Locale getLocale() {
    return request.getLocale();
  }

  public Enumeration getLocales() {
    return request.getLocales();
  }

  public String getParameter(String name) {
    return request.getParameter(name);
  }

  public Map getParameterMap() {
    return request.getParameterMap();
  }

  public Enumeration getParameterNames() {
    return request.getParameterNames();
  }

  public String[] getParameterValues(String parameter) {
    return request.getParameterValues(parameter);
  }

  public String getProtocol() {
    return request.getProtocol();
  }

  public BufferedReader getReader() throws IOException {
    return request.getReader();
  }

  public String getRemoteAddr() {
    return request.getRemoteAddr();
  }

  public String getRemoteHost() {
    return request.getRemoteHost();
  }

  public String getScheme() {
   return request.getScheme();
  }

  public String getServerName() {
    return request.getServerName();
  }

  public int getServerPort() {
    return request.getServerPort();
  }

  public void removeAttribute(String attribute) {
    request.removeAttribute(attribute);
  }

  public void setAttribute(String key, Object value) {
    request.setAttribute(key, value);
  }

  public void setCharacterEncoding(String encoding)
    throws UnsupportedEncodingException {
    request.setCharacterEncoding(encoding);
  }

}
```

注意`RequestFacade`类的构造方法。它的构造方法，接收一个`ServletRequest`对象request，然后把这个request对象直接赋给私有变量。`RequestFacade`里的每个方法，跟传入的request对象实现的方法都一样。

`ResponseFacade`类的作用与实现，同上。不再累述。

下面是程序2用到的所有类：
-	HttpServer2 
-	Request 
-	Response 
-	StaticResourceProcessor 
-	ServletProcessor2   
-	Constants 

`HttpServer2`与`HttpServer1`类的唯一区别是，在`await`方法里使用的是`ServletProcessor2`而不是`ServletProcessor1`：
```java
if (request.getUri().startsWith("/servlet/")) {
          ServletProcessor2 processor = new ServletProcessor2();
          processor.process(request, response);
        }
```


`ServletProcessor2`与`ServletProcessor1`唯一的是，在`process`方法里使用了门面模式类：
```java
Servlet servlet = null;
    RequestFacade requestFacade = new RequestFacade(request);
    ResponseFacade responseFacade = new ResponseFacade(response);
    try {
      servlet = (Servlet) myClass.newInstance();
      servlet.service((ServletRequest) requestFacade, (ServletResponse) responseFacade);
    }
    catch (Exception e) {
      System.out.println(e.toString());
    }
```

### 运行程序2 ###
在Windows上运行程序2时，请在命令行中输入：
> java -classpath ./lib/servlet.jar;./ ex02.pyrmont.HttpServer2

在Linux上，则输入：
> java -classpath ./lib/servlet.jar:./ ex02.pyrmont.HttpServer2

然后你就可以在浏览器中输入跟程序1一样的URL，进行测试了。

### 本章小结 ###
本章讨论了两个简单的即能发送静态资源又能发送servlet资源的Servlet容器。同时，我们也介绍了`javax.servlet.Servlet`相关的接口。


## 第三章 连接器 ##

### 概要 ###
在前面的章节中，我们提到过，Catalina（Tomcat的核心，译注）里有两个重要的模块：容器（container）与连接器（connector）。在本章中，我们将加入连接器，以此来增强上一章中程序的功能。连接器能创建更好的`request`与`response`对象。一个符合Servlet2.3与2.4规范的连接器，必须创建能够传递给`service`方法的`javax.servlet.http.HttpServletRequest `与`javax.servlet.http.HttpServletResponse`实例。
第二章中的Servlet容器，只能运行实现了`javax.servlet.Servlet`接口的servlet，同时，还要给它的`service`方法传入`javax.servlet.ServletRequest`与`javax.servlet.ServletResponse`实例。因为连接器不知道servlet的具体类型，是实现了`javax.servlet.Servlet`接口的类，还是`javax.servlet.GenericServlet`的子类，亦或`javax.servlet.http.HttpServlet`的子类，所以，连接器必须要能提供`HttpServletRequest`与`HttpServletResponse`的实例。（`javax.servlet.Servlet`与`javax.servlet.GenericServlet`的`service`方法接收的都是`ServletRequest`与`ServletResponse`，而`javax.servlet.http.HttpServlet`有两个重载的`service`方法，即能接收`ServletXXX`，又能接收`HttpServletXXX`，所以，连接器最把稳的做法就是提供`HttpServletXXX`，因为它是`ServletXXX`的子类接口，译注）。
本章里，我们创建的连接器，是tomcat4（第四章讨论）中默认连接器的一个简单版本。虽然Tomcat4里的默认连接器已经被废弃了，但是它仍然具有很大的学习价值。本章接下来的内容里，“连接器”都代表的是一个模块。

*注意 跟以往的章节不同，本章的连接器跟容器是分开的*

本章里的代码可以在ex03.pyrmont包下找到。连接器部分的代码，可以在ex03.pyrmont.connector与ex03.pyrmont.connector.http下找到。
从本章开始，都会提供一个bootstrap启动类，以此来启动我们的程序。但是，在本阶段，还暂时没必要提供一个stop的功能，所以，将就着通过关闭命令行窗口来stop我们的程序吧。
在开始构建本章的程序之前，先让我们来认识一下来自org.apache.catalina.util包里的`StringManager`类吧。它可是负责处理Catalina里各个模块的国际化错误消息。

### StringManager类 ###
像Tomcat这样的大型程序，需要谨慎地处理各种错误消息。这些错误消息，不管是对系统管理员，还是servlet开发者来说，都是极其有用的。例如，系统管理员能够通过Tomcat打印出的错误信息，轻松地定位到异常发生的地方。对于servlet开发者而言，通过Tomcat抛出的包含特定消息的`javax.servlet.ServletException`异常，就能知道自己的servlet在什么地方出了问题。
对于错误消息，Tomcat的做法是，把它们存储在一个属性文件里（properties文件）。这样，就能很容易的编辑它们了。但是，Tomcat里有成百上千个类，如果把所有类的错误消息都存储进一个文件里的话，将会是维护工作的噩梦。为了避免这个问题，Tomcat为每个包分配一个属性文件。例如，org.apache.catalina.connector包下的属性文件，只会包含该包下的所有类的错误消息。
每个`org.apache.catalina.util.StringManager`实例，处理一个属性文件。因此，当Tomcat启动的时候，会有许多的`StringManager`实例，每个实例读取一个特定包下的属性文件。由于Tomcat流行甚广，因此，有必要将错误消息实现多语言化。目前，支持英语、西班牙语、日语，三种语言。英语环境下的属性文件，命名为**LocalStrings.properties**,另外两种语言下的文件名分别命名为**LocalStrings_es.properties**与**LocalStrings_ja.properties**。
当一个类要在本包下的属性文件里，查找一个错误消息时，首先得获得一个`StringManager`的实例。可是，这样的话，本包下有多少个类需要错误消息的对象，那Tomcat就得一一为它们创建一个对应的`StringManager`实例。这种浪费资源的事情，是无法容忍的。因此，`StringManager`类被设计为，同一个包下是可共享的。如果你熟悉设计模式的话，你可能已经猜到了。没错，单例模式！`StringManager`被设计成了单例。`StringManager`唯一仅有的一个构造方法，被`private`修饰，变成私有的了。如此一来，你就不能再在其它地方通过new关键词来创建它的实例了。要想获得它的实例，只能调用它的静态类方法`getManager`，同时传入包的名称。它的每个实例都存储在一个Hashtable中（一种map结构，译注），包名则作为访问key进行映射。代码如下：
```java
private static Hashtable managers = new Hashtable();
public synchronized static StringManager getManager(String packageName) {
	StringManager mgr = (StringManager)managers.get(packageName); 
	if (mgr == null) {
		mgr = new StringManager(packageName);
		managers.put(packageName, mgr);
	}
	return mgr;
}
```

*注意 关于单例模式的文章，你可以在文件夹Articles里找到*

例如，要在ex03.pyrmont.connector.http包下使用`StringManager`，则可以给`getManager`方法传入该包名，来获得对应包的实例。
> StringManager sm =StringManager.getManager("ex03.pyrmont.connector.http");

在ex03.pyrmont.connector.http包下面，你可以找到以下三个属性文件：LocalStrings.properties, LocalStrings_es.properties 与 LocalStrings_ja.properties。`StringManager`会根据运行时所属语言环境来选择不同的文件。打开LocalStrings.properties文件，第一行（非注释行）内容如下：
> httpConnector.alreadyInitialized=HTTP connector has already been initialized

要取得一个错误消息，调用`StringManager`的`getString`方法，传入对应的错误代码就可以了。`getString`有很多重载的方法，我们使用的这个如下：
> public String getString(String key)

所以，我们要得到‘HTTP connector has already been initialized’消息的话，只需要调用
> getString(" httpConnector.alreadyInitialized")

就可以了。

### 程序代码 ###
从本章开始，所有的程序代码都会以模块的形式进行划分。本章程序包含三个模块：连接器，启动器，核心。
启动器模块仅仅由一个类`Bootstrap`组成，它的作用是启动程序。
连接器模块包含的类就要稍微多一点，可以分成5个部分：
-	连接器模块，与它的支撑类（HttpConnector 与 HttpProcessor）
-	代表HTTP请求的HttpRequest类，及其支撑类
-	代表HTTP响应的HttpResponse类，及其支撑类
-	门面类HttpRequestFacade 与 HttpResponseFacade
-	常量类Constant

核心模块包含两个类：`ServletProcessor`与`StaticResourceProcessor`。
图3.1展示了这些类的UML关系图。为了增强可读性，突出重点，我们略去了跟`HttpRequest`与`HttpResponse`相关的类。不过没关系，等到我们分别的讨论这两个类时，你就可以看到他们的UML关系图了。

图3.1
![](images/chapter3-1.png)

跟第二章的关系图比起来，你会发现，`HttpServer`被划分成了两个类，`HttpConnector`与`HttpProcessor`，同时，`Request`被`HttpRequest`代替了，`Response`也被`HttpResponse`所取代，而且，本章里使用了更多的类。
在第二章中，`HttpServer`的职责是“等待http请求的到来，同时为之创建request对象，与response对象”。但是，在本章中，“等待http请求的到来”这一任务，被分配给了`HttpConnector`，而“创建request对象，与response对象”，则被分配给了`HttpProcessor`负责。
在本章中，我们使用实现了`javax.servlet.http.HttpServletRequest`接口的`HttpRequest`类来代表http请求对象。`HttpRequest`对象会被转换为`HttpServletRequest`对象，这样才能传给servlet的`service`方法。因此，`HttpRequest`实例，就必须拥有servlet能使用的一些属性，例如URI,querystring,parameters,cookies 等等。因为，连接器并不知道servlet会使用到哪些属性，所以，连接器必须尽可能多地从http请求里解析到这些属性。但是呢，解析http请求的字符串，是一件很劳民伤财的事情，如果每个servlet想要什么属性，连接器就只解析什么属性的话，将会极大的节省CPU资源的。例如，如果一个servlet并不需要任何请求的参数（即，该servlet不会调用HttpServletRequest 的getParameter, getParameterMap, getParameterNames, getParameterValues等方法），那么，连接器就完全没必要再去从请求的字符串里解析出这些参数。本章的连接器，跟Tomcat的默认连接器一样，除非servlet真的需要（调用了某个获取参数的方法），否则，绝不会去主动解析这些参数，这样就极大的提高了效率。

Tomcat默认的连接器跟我们的连接器一样，都是使用`SocketInputStream`类，从`socket`对象的`InputStream`里读取字节流数据。`socket`对象的`getInputStream`方法，返回一个`java.io.InputStream`实例，`SocketInputStream`对返回的这个`java.io.InputStream`进行包装。`SocketInputStream`类提供了两个重要的方法：**readRequestLine**与**readHeader**，其中readRequestLine方法返回的是HTTP请求的第一行数据，即URI，方法，http版本那些信息。因为，`InputStream`处理字节流，是一次性处理，从第一个字节开始到最后一个字节结束，而且不会回退，所以，`readRequestLine`方法只能调用一次，且必须在`readHeader`方法之前被调用。每次调用`readHeader`方法，都会返回一组http请求头组成的键值对，而http请求头会有很多，所以，`readHeader`方法会被重复调用，只到所有的请求头都被读取出来。`readRequestLine`方法，返回的是一个`HttpRequestLine`实例，而`readHeader`方法返回的是一个`HttpHeader`实例。我们会在接下来的内容里讨论它们。

我们会在`HttpProcessor`实例里，创建一个`HttpRequest`实例，因此，需要提供必要的值。`HttpProcessor`会调用它的`parse`方法解析出HTTP请求里的“请求行”与“请求头”，解析剩下的其它值，则会赋给`HttpProcessor`的相应字段。但是，`parse`方法并不会去解析“请求实体(post里的那些值，译注)”或“请求字符串（get里的那些值，译注）”里的参数，这个任务呢，就交给`HttpRequest`自己了。只有在servlet需要这些参数的时候，`HttpRequest`才会去解析，正如我们之前谈过的。

另一个比前面章节增强的地方是，增加了一个用来启动程序的启动类`ex03.pyrmont.startup.Bootstrap`。

在下面的这些章节中，我们将详细地对程序进行探讨。
-	开始构建
-	连接器
-	创建`HttpRequest`实例
-	创建`HttpResponse`实例
-	静态资源与servlet资源的处理
-	启动程序

### 开始构建 ###
我们从启动类`ex03.pyrmont.startup.Bootstrap`开始，构建程序。类的代码如“例3.1”所示

例3.1 启动类Bootstrap
```java
package ex03.pyrmont.startup;

import ex03.pyrmont.connector.http.HttpConnector;

public final class Bootstrap {
  public static void main(String[] args) {
    HttpConnector connector = new HttpConnector();
    connector.start();
  }
}
```

`Bootstrap`类里的`main`方法，实例化了一个`HttpConnector`对象，并调用它的`start`方法。`HttpConnector`类的代码如“例3.2”所示

例3.2 HttpConnector
```java
package ex03.pyrmont.connector.http;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class HttpConnector implements Runnable {

  boolean stopped;
  private String scheme = "http";

  public String getScheme() {
    return scheme;
  }

  public void run() {
    ServerSocket serverSocket = null;
    int port = 8080;
    try {
      serverSocket =  new ServerSocket(port, 1, InetAddress.getByName("127.0.0.1"));
    }
    catch (IOException e) {
      e.printStackTrace();
      System.exit(1);
    }
    while (!stopped) {
      // Accept the next incoming connection from the server socket
      Socket socket = null;
      try {
        socket = serverSocket.accept();
      }
      catch (Exception e) {
        continue;
      }
      // Hand this socket off to an HttpProcessor
      HttpProcessor processor = new HttpProcessor(this);
      processor.process(socket);
    }
  }

  public void start() {
    Thread thread = new Thread(this);
    thread.start();
  }
}
```
### 连接器Connector ###
例3.2里的`ex03.pyrmont.connector.http.HttpConnector`类代表了一个连接器。它负责创建一个等待http请求到来的server端`socket`。类`HttpConnector`，实现了`java.lang.Runnable`接口，这样做的好处在于，它能独自在一个线程。当你启动程序的时候，会创建一个`HttpConnector`实例，并执行它的`run`方法。

*注意 你可以通过阅读本应用目录下的“Working_with_Threads”一文，来回忆下怎么创建java线程*

`run`方法里包含一个while循环：
-	等待http请求到来
-	为每个请求创建一个`HttpProcessor`实例
-	调用`HttpProcessor`的`process`方法

*注意	`HttpConnector`里的run方法，跟第二章里的`HttpServer1`类的`await`方法，功能类似。*

你会发现，`HttpConnector`类与`ex02.pyrmont.HttpServer1`类很相似。除了一点，从`java.net.ServerSocket`处获得`socket`后的处理步奏： `HttpConnector`里获得socket之后，并不直接操纵它，而是创建一个`HttpProcessor`实例，并调用实例的`process`方法，同时，把socket作为参数，传递进去。

*注意 `HttpConnector`类里还有另外一个方法`getScheme`，该方法返回http*

`HttpProcessor`类的`process`方法，接收一个从http请求处获得的`socket`对象。它负责为每一个达到的http请求，进行如下处理：
1.	创建一个`HttpRequest`对象
2.	创建一个`HttpResponse`对象
3.	解析http请求里的第一行，与请求头信息，并为`HttpRequest`对象赋值。
4.	根据需要，把`HttpRequest`和`HttpResponse`对象传递给`ServletProcessor`或者`StaticResourceProcessor`。就跟第二章里的一样，`ServletProcessor`会调用所请求的servlet的`service`方法提供服务，而`StaticResourceProcessor`则会提供发送静态资源服务。

`HttpProcessor`类process方法的代码如“例3.3”所示

例3.3 HttpProcessor类的process方法
```java
public void process(Socket socket) {
    SocketInputStream input = null;
    OutputStream output = null;
    try {
      input = new SocketInputStream(socket.getInputStream(), 2048);//构造一个InputStream的包装类SocketInputStream
      output = socket.getOutputStream();

      // create HttpRequest object and parse
      request = new HttpRequest(input);

      // create HttpResponse object
      response = new HttpResponse(output);
      response.setRequest(request);

      response.setHeader("Server", "Pyrmont Servlet Container");

      parseRequest(input, output);
      parseHeaders(input);

      //check if this is a request for a servlet or a static resource
      //a request for a servlet begins with "/servlet/"
      if (request.getRequestURI().startsWith("/servlet/")) {
        ServletProcessor processor = new ServletProcessor();
        processor.process(request, response);
      }
      else {
        StaticResourceProcessor processor = new StaticResourceProcessor();
        processor.process(request, response);
      }

      // Close the socket
      socket.close();
      // no shutdown for this application
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }
```

`process`方法，首先通过`socket`获得`input stream`与`output stream`。但是，请注意，我们使用的是继承了`java.io.InputStream`的包装类`SocketInputStream`。
> ```java
SocketInputStream input = null;
    OutputStream output = null;
    try {
      input = new SocketInputStream(socket.getInputStream(), 2048);
      output = socket.getOutputStream();
> ```

接着，`process`方法创建了一个`HttpRequest`实例与`HttpResponse`实例，并设置了`HttpResponse`对`HttpRequest`的引用。
> ```java
      request = new HttpRequest(input);
      // create HttpResponse object
      response = new HttpResponse(output);
      response.setRequest(request);
> ```

本章的`HttpResponse`类，比第二章的`Response`类更健全。例如，你可以调用`setHeader`方法，向客户端反馈一些头信息。
> `response.setHeader("Server", "Pyrmont Servlet Container");`

接下来，`process`方法调用了对象`HttpProcessor`自己的两个私有方法：
> ```java
 parseRequest(input, output);
 parseHeaders(input);
> ```

然后，根据URI请求资源的不同，它把`HttpRequest`与`HttpResponse`传给`ServletProcessor`或`StaticResourceProcessor`处理。
最后，`process`方法关闭连接。
> `socket.close()`

有一点同样需要注意一下，`HttpProcessor`类使用`org.apache.catalina.util.StringManager`类来发送错误消息。
> ```java
  protected StringManager sm =
    StringManager.getManager("ex03.pyrmont.connector.http");
>```

`HttpProcessor`里的私有方法`parseRequest`，`parseHeaders`，`normalize`都是用于帮助填充`HttpRequest`里的属性的。我们在接下来的内容中，会讨论它们。

### 创建HttpRequest对象 ###
`HttpRequest`类实现了`javax.servlet.http.HttpServletRequest`接口，它有一个门面类`HttpRequestFacade`。图3.2显示了跟它相关的类的UML关系图。

图3.2
![](images/chapter3-2.png)

本章里，`HttpRequest`的很多方法，只是保留了空白，并没有具体实现（我们将在第四章里完全实现所有方法）。不过，没关系，本章里的`HttpRequest`已经能提供servlet所需要的头信息、cookie、参数等信息。这三类信息存储在如下的变量里：
```java
protected HashMap headers = new HashMap();
protected ArrayList cookies = new ArrayList();
protected ParameterMap parameters = null;
```
servlet能从`javax.servlet.http.HttpServletRequest`的以下方法中，取得所需要的信息：getCookies,getDateHeader, getHeader, getHeaderNames, getHeaders, getParameter, getPrameterMap, getParameterNames, and getParameterValues。只要活得头信息、cookie，参数等信息后，实现`HttpRequest`里的其它方法就比较容易了。
显而易见，此处的难点在于如何解析HTTP请求，并把解析出来的信息赋值给`HttpRequest`。对于头信息与cookie，`HttpProcessor`会在它的`parseHeaders`方法里，调用`HttpRequest`提供的`addHeader`与`addCookie`方法来处理。对于请求参数而言，只会在需要它们的时候，才会调用`HttpRequestp`的`parseParameters`方法进行解析。本节我们会详细的讨论。
由于，解析HTTP请求是一项非常复杂的任务，所以，本节我们分成如下几个小节：
-	读取socket的input stream
-	解析请求行
-	解析请求头信息
-	解析cookie
-	获取请求参数

#### 读取socket的input stream ####
在第一章与第二章，我们已经做过部分解析HTTP请求的工作了，还记得吗？就在`ex01.pyrmont.HttpRequest`与`ex02.pyrmont.HttpRequest`里。通过调用`java.io.InputStream`的`read`方法，我们获得了包含请求方法、请求URI，HTTP版本的请求行：
> ```java
> byte[] buffer = new byte [2048];
try {
// input is the InputStream from the socket.
i = input.read(buffer);
}
> ```

在之前的两个程序里，我们并没有试图更进一步的去解析请求。但是，在本章的程序中，我们将使用`ex03.pyrmont.connector.http.SocketInputStream`（org.apache.catalina.connector.http.SocketInputStream的复制）类去进一步解析请求，不仅能获得请求行，而且能获得请求头。
通过传入一个`InputStream`与一个指定缓存大小的数值，就能构造一个`SocketInputStream`实例。本章中，我们在`ex03.pyrmont.connector.http.HttpProcessor`类的`process`方法里来构造`SocketInputStream`对象，如下片段所示：
```java
SocketInputStream input = null;
OutputStream output = null;
try {
input = new SocketInputStream(socket.getInputStream(), 2048);
...
```
正如之前提过的，我们之所以要使用`SocketInputStream`，是因为它有两个很重要的方法：`readRequestLine`与`readHeader`。

#### 解析请求行 ####
`HttpProcessor`的`process`方法，会调用私有方法`parseRequest`来解析请求行，即HTTP请求的第一行。下面是一个请求行的例子：
> GET /myApp/ModernServlet?userName=tarzan&password=pwd HTTP/1.1

请求行的第二部分是URI加上一些可选的参数。上面的例子中，URI是：
> /myApp/ModernServlet

**？**号后面的是请求的参数。所以上诉例子中的请求参数就是：
> userName=tarzan&password=pwd

请求参数不是必须的，根据需要，可有可无。上面的请求参数包含两个键值对：userName/tarzan 与 password/pwd。在servlet（jsp）的开发过程中，参数`jsessionid`用来传递session标识符。session标识符通常放在cookie中，但是也有例外，例如，在浏览器被禁止使用cookie的情况下，就可以把session标识符放在请求参数里。
在`parseRequest`方法运行时，变量`request`指向一个`HttpRequest`实例。`parseRequest`方法通过解析请求行而获得许多信息，并把这些信息赋给`HttpRequest`实例。现在，就让我们近距离的观察下`parseRequest`方法：

例3.4  parseRequest方法
```java
  private void parseRequest(SocketInputStream input, OutputStream output)
    throws IOException, ServletException {

    // Parse the incoming request line
	//解析出到达的http请求行信息
    input.readRequestLine(requestLine);
    String method =
      new String(requestLine.method, 0, requestLine.methodEnd);
    String uri = null;
    String protocol = new String(requestLine.protocol, 0, requestLine.protocolEnd);

    // Validate the incoming request line
    //校验到达的http请求行信息是否符合规范，即是否有请求方法、请求URL
    if (method.length() < 1) {
      throw new ServletException("Missing HTTP request method");
    }
    else if (requestLine.uriEnd < 1) {
      throw new ServletException("Missing HTTP request URI");
    }
    // Parse any query parameters out of the request URI
    //从请求的URL里解析出参数的键值对，URL里的参数都是跟在?后面的
    int question = requestLine.indexOf("?");
    if (question >= 0) {
      request.setQueryString(new String(requestLine.uri, question + 1,
        requestLine.uriEnd - question - 1));
      uri = new String(requestLine.uri, 0, question);
    }
    else {
      request.setQueryString(null);
      uri = new String(requestLine.uri, 0, requestLine.uriEnd);
    }


    // Checking for an absolute URI (with the HTTP protocol)
    if (!uri.startsWith("/")) {
      int pos = uri.indexOf("://");
      // Parsing out protocol and host name
      if (pos != -1) {
        pos = uri.indexOf('/', pos + 3);
        if (pos == -1) {
          uri = "";
        }
        else {
          uri = uri.substring(pos);
        }
      }
    }

    // Parse any requested session ID out of the request URI
    String match = ";jsessionid=";
    int semicolon = uri.indexOf(match);
    if (semicolon >= 0) {
      String rest = uri.substring(semicolon + match.length());
      int semicolon2 = rest.indexOf(';');
      if (semicolon2 >= 0) {
        request.setRequestedSessionId(rest.substring(0, semicolon2));
        rest = rest.substring(semicolon2);
      }
      else {
        request.setRequestedSessionId(rest);
        rest = "";
      }
      request.setRequestedSessionURL(true);
      uri = uri.substring(0, semicolon) + rest;
    }
    else {
      request.setRequestedSessionId(null);
      request.setRequestedSessionURL(false);
    }

    // Normalize URI (using String operations at the moment)
    String normalizedUri = normalize(uri);

    // Set the corresponding request properties
    ((HttpRequest) request).setMethod(method);
    request.setProtocol(protocol);
    if (normalizedUri != null) {
      ((HttpRequest) request).setRequestURI(normalizedUri);
    }
    else {
      ((HttpRequest) request).setRequestURI(uri);
    }

    if (normalizedUri == null) {
      throw new ServletException("Invalid URI: " + uri + "'");
    }
  }
```
`parseRequest`方法首先调用了`SocketInputStream`类的`readRequestLine`方法：
> input.readRequestLine(requestLine);

其中，`requestLine`是`HttpProcessor`内部里`HttpRequestLine`的一个实例：
> private HttpRequestLine requestLine = new HttpRequestLine();

调用`SocketInputStream`的`readRequestLine`方法，目的在于，通过它来对`HttpRequestLine`实例进行各种赋值。因此，紧接着，`parseRequest`就能获得请求的方法、URI、以及协议：
> ```java
> String method =
new String(requestLine.method, 0, requestLine.methodEnd);
String uri = null;
String protocol = new String(requestLine.protocol, 0,
requestLine.protocolEnd);
> ```

然而，URI的解析有点麻烦，URI后面可能跟着请求参数（？号的字符串）。因此，`parseRequest`首先检查是否有请求参数，如果有，就分离出它们，并通过调用`setQueryString`方法，将请求参数赋给`HttpRequest`对象：
> ```java
// Parse any query parameters out of the request URI
int question = requestLine.indexOf("?");
if (question >= 0) { // there is a query string.
request.setQueryString(new String(requestLine.uri, question + 1,
requestLine.uriEnd - question - 1));
uri = new String(requestLine.uri, 0, question);
}
else {
request.setQueryString (null);
uri = new String(requestLine.uri, 0, requestLine.uriEnd);
}
> ```

大多数URI都指向一个相对路径，但是，也有URI指向绝对路径，例如：
> http://www.brainysoftware.com/index.html?name=Tarzan

*译注 这里的绝对路径并非文件系统上的，这里指不以/开头的请求。我们平时在一个应用下开发，发出的请求都是以/开头的，因为是在同一个域名下。但是，在该域名下请求其它域名的URI，就得是以http://开头了，例如，外连其它网站的图片。大家可以捕获下http请求看看*

`parseRequest`同样需要判断是相对路径还是绝对路径：
> ```java
 // Checking for an absolute URI (with the HTTP protocol)
    if (!uri.startsWith("/")) {
      int pos = uri.indexOf("://");
      // Parsing out protocol and host name
      if (pos != -1) {
        pos = uri.indexOf('/', pos + 3);
        if (pos == -1) {
          uri = "";
        }
        else {
          uri = uri.substring(pos);
        }
      }
    }
>```

由于请求参数里也可能有包含session标识符的jsessionid参数，因此，`parseRequest`方法同样需要检查session标识符。如果`jsessionid`参数存成的话，`parseRequest`方法就会获取它的值（session标识符），并通过调用`setRequestedSessionId`方法，把值赋给`HttpRequest`：
> ```java
> // Parse any requested session ID out of the request URI
    String match = ";jsessionid=";
    int semicolon = uri.indexOf(match);
    if (semicolon >= 0) {
      String rest = uri.substring(semicolon + match.length());
      int semicolon2 = rest.indexOf(';');
      if (semicolon2 >= 0) {
        request.setRequestedSessionId(rest.substring(0, semicolon2));
        rest = rest.substring(semicolon2);
      }
      else {
        request.setRequestedSessionId(rest);
        rest = "";
      }
      request.setRequestedSessionURL(true);
      uri = uri.substring(0, semicolon) + rest;
    }
    else {
      request.setRequestedSessionId(null);
      request.setRequestedSessionURL(false);
    }
>```

如果请求参数中包含`jsessionid`参数，那也就意味着，session标识符不在cookie里，因此，就需要给`setRequestSessionURL`方法传递true，否则，传递false，同时，给`setRequestedSessionURL`传递null。

到此为止，我们已经把`jsessionid`从URI里剥离出去了。

接下来，`parseRequest`方法会把解析出的URI传递给`normalize`方法。`normalize`方法的作用在于，修正URI里不合规范的地方，例如，不能有保留字符‘%’等。只有URI符合规范，`normalize`才会返回原始URI或修正后的URI，否则只会返回null。如果返回null的话，`parseRequest`会在接下来的处理中抛出一个`Invalid URI`的异常。

最后，`parseRequest`方法会继续为`HttpRequest`对象赋值：
> ```
// Set the corresponding request properties
    ((HttpRequest) request).setMethod(method);
    request.setProtocol(protocol);
    if (normalizedUri != null) {
      ((HttpRequest) request).setRequestURI(normalizedUri);
    }
    else {
      ((HttpRequest) request).setRequestURI(uri);
    }
>```

同时，检查URI是否符合规范，不符合就抛出异常：
> ```
>   if (normalizedUri == null) {
      throw new ServletException("Invalid URI: " + uri + "'");
    }
> ```

#### 解析请求头信息 ####
2016-4-9 11:18:08 翻译到64页
