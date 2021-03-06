package com.javax0.fluflu.processor;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic.Kind;

import com.javax0.aptools.Environment;
import com.javax0.aptools.FromThe;
import com.javax0.aptools.The;
import com.javax0.aptools.There;

@SupportedAnnotationTypes("com.javax0.fluflu.*")
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class FlufluApt extends AbstractProcessor {
  private String clonerMethodName;

  private void generateFluentizedClassFrom(Element classElement) throws IOException {

    AnnotationMirror fluentize = FromThe.element(classElement).getTheAnnotation("com.javax0.fluflu.Fluentize");
    String startState = FromThe.annotation(fluentize).getStringValue("startState");
    String className = FromThe.annotation(fluentize).getStringValue("className");
    String startMethod = FromThe.annotation(fluentize).getStringValue("startMethod");
    String packageName = FromThe.element(classElement).getPackageName();
    String classToFluentize = FromThe.element(classElement).getClassName();

    StringBuilder body = new StringBuilder();
    if (There.is(className)) {
      FluentClassMaker maker = new FluentClassMaker(packageName, className, classToFluentize, null, clonerMethodName);
      body.append(maker.generateFluentClassHeader(startState, startMethod));

      if (There.is(startMethod)) {
        body.append(maker.generateStartMethod(startState, startMethod));
      }
      if (The.element(classElement).isAbstract()) {
        body.append(maker.generateFluentClassMethods(classElement));
      }
      body.append(maker.generateFluentClassFooter());

      new ClassWriter(processingEnv).writeSource(packageName, className, body.toString());
    }
  }

  @Override
  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
    Environment.set(processingEnv);
    ClassWriter classWriter = new ClassWriter(processingEnv);
    for (Element rootElement : roundEnv.getRootElements()) {
      if (The.element(rootElement).hasAnnotation("com.javax0.fluflu.Fluentize")) {
        try {
          ClassParser parser = new ClassParser(rootElement, classWriter);
          parser.parse();
          clonerMethodName = parser.clonerMethodName;
          generateFluentizedClassFrom(rootElement);
        } catch (IOException e) {
          Environment.get().getMessager().printMessage(Kind.ERROR, "Can not write outputfile.");
          StringWriter sw = new StringWriter();
          PrintWriter pw = new PrintWriter(sw);
          e.printStackTrace(pw);
          Environment.get().getMessager().printMessage(Kind.ERROR, sw.toString());
        }
      }
    }
    return false;
  }
}
