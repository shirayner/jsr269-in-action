package com.ray.study.sample.simple.lombok.annotation.processor;


import com.ray.study.sample.simple.lombok.annotation.Data;
import com.sun.source.util.Trees;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.Names;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.util.Set;

/**
 * description
 *
 * @author r.shi 2021/5/27 16:29
 */
@SupportedAnnotationTypes("com.ray.study.sample.simple.lombok.annotation.Data")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class DataAnnotationProcessor extends AbstractProcessor {

    /**
     * 可将Element转换成JCTree，组成AST语法树。使用Trees.instance(processingEnv);进行初始化。
     */
    private Trees trees;

    /**
     * 构造JCTree的工具类。使用TreeMaker.instance(((JavacProcessingEnvironment) processingEnv).getContext());初始化。
     */
    private TreeMaker treeMaker;

    /**
     * 名字处理工具类。使用Names.instance(context);进行初始化。
     */
    private Names names;

    /**
     * 编译期的日志打印工具类。使用processingEnv.getMessager();进行初始化
     */
    private Messager messager;


    /**
     * init 初始化
     *
     * @param processingEnv 环境：提供一些Javac的执行工具
     */
    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        this.messager = processingEnv.getMessager();
        this.trees = Trees.instance(processingEnv);
        Context context = ((JavacProcessingEnvironment) processingEnv).getContext();
        this.treeMaker = TreeMaker.instance(context);
        this.names = Names.instance(context);
    }

    /**
     * 具体的执行
     *
     * @param annotations 注解集合
     * @param roundEnv    执行round环境
     * @return 执行结果
     */
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        messager.printMessage(Diagnostic.Kind.NOTE, "roundEnv --->" + roundEnv);
        if (!roundEnv.processingOver()) {
            // 所有@Data注解标注的类
            Set<? extends Element> annotatedElements = roundEnv.getElementsAnnotatedWith(Data.class);
            annotatedElements.forEach(element -> {
                // 获得当前遍历类的语法树
                JCTree tree = (JCTree) trees.getTree(element);
                // 使用DataTreeTranslator处理
                tree.accept(new DataTreeTranslator(treeMaker, names, messager));
            });
        }
        // 返回true：Javac编译器会从编译期的第二阶段回到第一阶段
        return true;
    }

    /**
     * 获取当前处理器支持的注解类型集合
     * <p>
     * JDK7后可用注解@SupportedAnnotationTypes代替该方法
     *
     * @return 注解类全限定类名字符串的集合
     */
    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return super.getSupportedAnnotationTypes();
    }

    /**
     * 获取支持的JDK版本
     * <p>
     * JDK7后可用注解@SupportedSourceVersion代替该方法
     *
     * @return JDK版本
     */
    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }


}
