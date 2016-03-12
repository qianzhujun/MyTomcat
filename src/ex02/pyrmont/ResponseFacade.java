package ex02.pyrmont;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Locale;
import javax.servlet.ServletResponse;
import javax.servlet.ServletOutputStream;

/**
 * 这里使用到了门面模式。ResponseFacade同样实现了ServletResponse接口，但是，注意它的构造方法。在构造方法里，它接收一个ServletResponse对象response，然后把这个response对象，直接赋给私有变量。同时，ResponseFacade实现的所有的方法，调用的都是传入的response对象的方法。
 * 如此一来，外界就只能访问到ResponseFacade的公共方法，根本不知道有response对象的存在。这样，就保护了response对象，response对象的sendStaticResource等方法，外面根本访问不到。
 * 
 * 门面模式：
 * classA实现了接口interface，但是，classA还有一些公用的方法，不想让外界知道，只想给外界提供实现了的interface接口。
 * 这时，classA就需要一个替身（门面）ClassB。让ClassB同样实现接口interface，除了interface接口方法之外，ClassB不再拥有任何方法。创建ClassB对象的时候，传入一个ClassA，所有对ClassB的接口方法的访问，都映射到ClassA上。这样，外界就永远不可能操作到ClassA了，进而保护了ClassA。
 * 
 *  
 *
 */
public class ResponseFacade implements ServletResponse {

  private ServletResponse response;
  public ResponseFacade(Response response) {
    this.response = response;
  }

  public void flushBuffer() throws IOException {
    response.flushBuffer();
  }

  public int getBufferSize() {
    return response.getBufferSize();
  }

  public String getCharacterEncoding() {
    return response.getCharacterEncoding();
  }

  public Locale getLocale() {
    return response.getLocale();
  }

  public ServletOutputStream getOutputStream() throws IOException {
    return response.getOutputStream();
  }

  public PrintWriter getWriter() throws IOException {
    return response.getWriter();
  }

  public boolean isCommitted() {
    return response.isCommitted();
  }

  public void reset() {
    response.reset();
  }

  public void resetBuffer() {
    response.resetBuffer();
  }

  public void setBufferSize(int size) {
    response.setBufferSize(size);
  }

  public void setContentLength(int length) {
    response.setContentLength(length);
  }

  public void setContentType(String type) {
    response.setContentType(type);
  }

  public void setLocale(Locale locale) {
    response.setLocale(locale);
  }

}