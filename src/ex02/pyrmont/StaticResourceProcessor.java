package ex02.pyrmont;

import java.io.IOException;

public class StaticResourceProcessor {
	
	/**
	 * 静态资源处理类，直接调用response的返回静态资源方法
	 */
	
	

  public void process(Request request, Response response) {
    try {
      response.sendStaticResource();
    }
    catch (IOException e) {
      e.printStackTrace();
    }
  }
}