package kakalgy.yusj.demonmq.util;

import javax.jms.JMSException;
import javax.jms.JMSSecurityException;
import javax.jms.MessageEOFException;
import javax.jms.MessageFormatException;

/**
 * 
 * @author kakalgy
 * @Description: TODO(用一句话描述该文件做什么)
 * @date 2018年3月15日 下午10:56:59
 */
public final class JMSExceptionSupport {

	private JMSExceptionSupport() {
	}

	/**
	 * 
	 * @param msg
	 * @param cause
	 * @return
	 */
	public static JMSException create(String msg, Throwable cause) {
		JMSException exception = new JMSException(msg);
		exception.initCause(cause);
		return exception;
	}

	/**
	 * 
	 * @param msg
	 * @param cause
	 * @return
	 */
	public static JMSException create(String msg, Exception cause) {
		JMSException exception = new JMSException(msg);
		exception.setLinkedException(cause);
		exception.initCause(cause);
		return exception;
	}

	/**
	 * 
	 * @param cause
	 * @return
	 */
	public static JMSException create(Throwable cause) {
		if (cause instanceof JMSException) {
			return (JMSException) cause;
		}
		String msg = cause.getMessage();
		if (msg == null || msg.length() == 0) {
			msg = cause.toString();
		}
		JMSException exception;
		if (cause instanceof SecurityException) {
			exception = new JMSSecurityException(msg);
		} else {
			exception = new JMSException(msg);
		}
		exception.initCause(cause);
		return exception;
	}

	/**
	 * 
	 * @param cause
	 * @return
	 */
	public static JMSException create(Exception cause) {
		if (cause instanceof JMSException) {
			return (JMSException) cause;
		}
		String msg = cause.getMessage();
		if (msg == null || msg.length() == 0) {
			msg = cause.toString();
		}
		JMSException exception;
		if (cause instanceof SecurityException) {
			exception = new JMSSecurityException(msg);
		} else {
			exception = new JMSException(msg);
		}
		exception.setLinkedException(cause);
		exception.initCause(cause);
		return exception;
	}

	/**
	 * 
	 * @param cause
	 * @return
	 */
	public static MessageEOFException createMessageEOFException(Exception cause) {
		String msg = cause.getMessage();
		if (msg == null || msg.length() == 0) {
			msg = cause.toString();
		}
		MessageEOFException exception = new MessageEOFException(msg);
		exception.setLinkedException(cause);
		exception.initCause(cause);
		return exception;
	}

	/**
	 * 
	 * @param cause
	 * @return
	 */
	public static MessageFormatException createMessageFormatException(Exception cause) {
		String msg = cause.getMessage();
		if (msg == null || msg.length() == 0) {
			msg = cause.toString();
		}
		MessageFormatException exception = new MessageFormatException(msg);
		exception.setLinkedException(cause);
		exception.initCause(cause);
		return exception;
	}
}
