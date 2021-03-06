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
      URLStreamHandler streamHandler = null;//之所以使用这个空对象，是因为直接使用new URL(null, repository, null)的话，编译器根本不知道你调用的到底是哪个构造方法。
      File classPath = new File(Constants.WEB_ROOT);
      // the forming of repository is taken from the createClassLoader method in
      // org.apache.catalina.startup.ClassLoaderFactory
      //repository的这种取得形式，来自org.apache.catalina.startup.ClassLoaderFactory的createClassLoader方法。
      String repository = (new URL("file", null, classPath.getCanonicalPath() + File.separator)).toString() ;
      // the code for forming the URL is taken from the addRepository method in
      // org.apache.catalina.loader.StandardClassLoader class.
      //URL的取得形式，同样来自Tomcat。
      urls[0] = new URL(null, repository, streamHandler);
      loader = new URLClassLoader(urls);//通过URL获取类加载器
    }
    catch (IOException e) {
      System.out.println(e.toString() );
    }
    Class myClass = null;
    try {
      myClass = loader.loadClass(servletName);//类加载器载入具体的servlet
    }
    catch (ClassNotFoundException e) {
      System.out.println(e.toString());
    }

    Servlet servlet = null;

    try {
      servlet = (Servlet) myClass.newInstance();//强制转换为所有servlet的父类
      
      /**
       * 下面这句代码有个安全隐患。如果servlet开发者知道，传入的ServletRequest的运行时类型是ex02.pyrmont。Request的话，就能将ServletRequest对象转换为真正的 ex02.pyrmont。Request对象，如此一来，开发者就能操作容器里的parseURI方法了。这是一个非常严重的后果。
       * 这个漏洞将在ServletProcessor2里修复。
       */
      servlet.service((ServletRequest) request, (ServletResponse) response);//调用servlet的service方法提供服务。
    }
    catch (Exception e) {
      System.out.println(e.toString());
    }
    catch (Throwable e) {
      System.out.println(e.toString());
    }

  }
}