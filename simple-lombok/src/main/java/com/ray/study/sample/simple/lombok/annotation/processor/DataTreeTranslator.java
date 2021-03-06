package com.ray.study.sample.simple.lombok.annotation.processor;

import com.sun.source.tree.Tree;
import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.tree.TreeTranslator;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Names;

import javax.annotation.processing.Messager;
import javax.tools.Diagnostic;

/**
 * AST树访问处理器
 *
 * @author r.shi 2021/5/27 17:06
 */
public class DataTreeTranslator extends TreeTranslator {
    /**
     * 构造JCTree的工具类
     */
    private final TreeMaker treeMaker;

    /**
     * 名字处理工具类
     */
    private final Names names;

    /**
     * 编译期的日志打印工具类
     */
    private final Messager messager;

    public DataTreeTranslator(TreeMaker treeMaker, Names names, Messager messager) {
        this.treeMaker = treeMaker;
        this.names = names;
        this.messager = messager;
    }

    /**
     * 访问到类定义时的处理
     * @param jcClassDecl 类定义的抽象语法树节点
     */
    @Override
    public void visitClassDef(JCTree.JCClassDecl jcClassDecl) {
        super.visitClassDef(jcClassDecl);
        jcClassDecl.defs.forEach(def -> {
            if (def.getKind().equals(Tree.Kind.VARIABLE)) {
                messager.printMessage(Diagnostic.Kind.NOTE,def + "----processed");
                // 插入getter方法
                jcClassDecl.defs = jcClassDecl.defs.prepend(createGetterMethod((JCTree.JCVariableDecl) def));
                // 插入setter方法
                jcClassDecl.defs = jcClassDecl.defs.prepend(createSetterMethod((JCTree.JCVariableDecl) def));
            }
        });
    }

    /**
     * 创建getter方法的语法树节点
     * @param def 变量节点
     * @return getter方法的语法树节点
     */
    private JCTree createGetterMethod(JCTree.JCVariableDecl def) {



        return treeMaker.MethodDef(
                // 访问修饰符
                treeMaker.Modifiers(Flags.PUBLIC),
                // 方法名
                names.fromString("get" + this.toFirstUpperCase(def.getName().toString())),
                // 方法返回类型
                (JCTree.JCExpression) def.getType(),
                // 泛型参数
                List.nil(),
                // 方法参数列表
                List.nil(),
                // throw表达式
                List.nil(),
                // 方法体
                treeMaker.Block(0L,List.of(
                        treeMaker.Return(
                                treeMaker.Select(
                                        treeMaker.Ident(names.fromString("this")),
                                        names.fromString(def.getName().toString())
                                )
                        )
                )),
                null
        );
    }

    /**
     * 创建setter方法的语法树节点
     * @param def 变量节点
     * @return setter方法的语法树节点
     */
    private JCTree createSetterMethod(JCTree.JCVariableDecl def) {
        // 构造方法入参
        JCTree.JCVariableDecl param = treeMaker.VarDef(treeMaker.Modifiers(Flags.PARAMETER), def.getName(), def.vartype, null);
        // 构造 this.def = def
        JCTree.JCFieldAccess thisX = treeMaker.Select(treeMaker.Ident(names.fromString("this")), def.getName());
        JCTree.JCAssign assign = treeMaker.Assign(thisX, treeMaker.Ident(def.getName()));
        // 构造方法体
        JCTree.JCBlock methodBody = treeMaker.Block(0L, List.of(treeMaker.Exec(assign)));
        // 构造方法返回类型
        JCTree.JCExpression returnType = treeMaker.Type(new Type.JCVoidType());
        // 构造setter方法
        return treeMaker.MethodDef(
                // 访问修饰符
                treeMaker.Modifiers(Flags.PUBLIC),
                // 方法名
                names.fromString("set" + this.toFirstUpperCase(def.getName().toString())),
                // 方法返回类型
                returnType,
                // 泛型参数
                List.nil(),
                // 方法参数列表
                List.of(param),
                // throw表达式
                List.nil(),
                // 方法体
                methodBody,
                // 默认值
                null
        );
    }

    /**
     * 工具方法：将字符串首位转为大写
     * @param str 源字符串
     * @return 首位大写的字符串
     */
    private String toFirstUpperCase(String str) {
        char[] charArray = str.toCharArray();
        charArray[0] -= 32;
        return String.valueOf(charArray);
    }
}
