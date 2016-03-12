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


/**
 * 这里使用到了门面模式。RequestFacade同样实现了ServletRequest接口，但是，注意它的构造方法。在构造方法里，它接收一个ServletRequest对象request，然后把这个request对象，直接赋给私有变量。同时，RequestFacade实现的所有的方法，调用的都是传入的request对象的方法。
 * 如此一来，外界就只能访问到RequestFacade的公共方法，根本不知道有request对象的存在。这样，就保护了request对象，request对象的parseUri等方法，外面根本访问不到。
 * 
 * 门面模式：
 * classA实现了接口interface，但是，classA还有一些公用的方法，不想让外界知道，只想给外界提供实现了的interface接口。
 * 这时，classA就需要一个替身（门面）ClassB。让ClassB同样实现接口interface，除了interface接口方法之外，ClassB不再拥有任何方法。创建ClassB对象的时候，传入一个ClassA，所有对ClassB的接口方法的访问，都映射到ClassA上。这样，外界就永远不可能操作到ClassA了，进而保护了ClassA。
 * 
 *  
 *
 */
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