package kakalgy.yusj.demonmq.util;

import java.io.PrintWriter;

/**
 * A helper class for printing indented text
 * <p>
 * 打印缩进的帮助类，其原理如下：
 * <ul>
 * <li>1.String indent为每次缩进的字符串，默认是用" "两个空格</li>
 * <li>2.int indentLevel是缩进的次数，比如indentLevel = 3，则缩进三个indent的字符串</li>
 * <li>3.该类只能打印到PrintWriter，不是打印到日志</li>
 * </ul>
 * 
 * @author gyli
 *
 */
public class IndentPrinter {

    private int indentLevel;// 缩进次数
    private String indent;// 缩进字符串
    private PrintWriter out;

    /**
     * 构造函数
     */
    public IndentPrinter() {
        // TODO Auto-generated constructor stub
        this(new PrintWriter(System.out), "  ");
    }

    /**
     * 构造函数
     */
    public IndentPrinter(PrintWriter out) {
        // TODO Auto-generated constructor stub
        this(out, "  ");
    }

    /**
     * 构造函数
     */
    public IndentPrinter(PrintWriter out, String indent) {
        // TODO Auto-generated constructor stub
        this.out = out;
        this.indent = indent;
    }

    public void println(Object value) {
        out.print(value.toString());
        out.println();
    }

    public void println(String text) {
        out.print(text);
        out.println();
    }

    public void print(String text) {
        out.print(text);
    }

    public void printIndent() {
        for (int i = 0; i < indentLevel; i++) {
            out.print(indent);
        }
    }

    public void println() {
        out.println();
    }

    public void incrementIndent() {
        ++indentLevel;
    }

    public void decrementIndent() {
        --indentLevel;
    }

    public void flush() {
        out.flush();
    }

    public int getIndentLevel() {
        return indentLevel;
    }

    public void setIndentLevel(int indentLevel) {
        this.indentLevel = indentLevel;
    }
}
