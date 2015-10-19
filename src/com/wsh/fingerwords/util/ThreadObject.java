package com.wsh.fingerwords.util;

/**
 * 线程执行对象
 * 
 * @作者 komojoemary
 * @version [版本号, 2011-2-24]
 * @see [相关类/方法]
 * @since [产品/模块版本]
 */
public abstract class ThreadObject {

	/**
	 * 是否处理完成
	 */
	private boolean isOk = false;

	/**
	 * 线程处理操作
	 * 
	 * @exception/throws [违例类型] [违例说明]
	 * @see [类、类#方法、类#成员]
	 */

	public abstract Object handleOperation();

	public boolean isOk() {
		return isOk;
	}

	public void setOk(boolean isOk) {
		this.isOk = isOk;
	}

}
